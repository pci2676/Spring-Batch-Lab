package com.javabom.otmp.module

import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import org.hibernate.ScrollMode
import org.hibernate.ScrollableResults
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.query.Query
import org.springframework.batch.item.ExecutionContext
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader
import org.springframework.beans.factory.InitializingBean
import org.springframework.util.Assert
import org.springframework.util.ClassUtils
import org.springframework.util.ReflectionUtils
import java.lang.reflect.Method
import java.util.Objects.nonNull
import java.util.function.Function
import javax.persistence.EntityManagerFactory
import kotlin.math.max

open class QuerydslCursorItemReader<T> constructor(
    entityManagerFactory: EntityManagerFactory,
    private val fetchSize: Int,
    private val queryFunction: Function<JPAQueryFactory, JPAQuery<T>>,
) : AbstractItemCountingItemStreamItemReader<T>(), InitializingBean {
    private val sessionFactory: SessionFactory = entityManagerFactory.unwrap(SessionFactory::class.java)
    private lateinit var statefulSession: Session
    private lateinit var cursor: ScrollableResults
    private var initialized: Boolean = false

    init {
        setName(ClassUtils.getShortName(QuerydslCursorItemReader::class.java))
    }

    override fun afterPropertiesSet() {
        Assert.state(this.fetchSize >= 0, "fetchSize 는 양수여야 한다.")
    }

    override fun doOpen() {
        Assert.state(!this.initialized, "ItemReader 가 열려있습니다. close 먼저 호출해주세요.")
        this.cursor = getForwardOnlyCursor(this.fetchSize)
        this.initialized = true
    }

    private fun getForwardOnlyCursor(fetchSize: Int): ScrollableResults {
        return createQuery().setFetchSize(fetchSize).scroll(ScrollMode.FORWARD_ONLY)
    }

    fun createQuery(): Query<T> {
        if (!this::statefulSession.isInitialized) {
            this.statefulSession = this.sessionFactory.openSession()
        }

        val queryFactory = JPAQueryFactory(statefulSession)
        val jpaQuery = this.queryFunction.apply(queryFactory)
        return jpaQuery.createQuery().unwrap(Query::class.java) as Query<T>
    }

    override fun doRead(): T? {
        if (cursor.next()) {
            val data = cursor.get()

            return if (data.size > 1) {
                data as T
            } else {
                data[0] as T
            }
        }

        return null
    }

    override fun update(executionContext: ExecutionContext) {
        super.update(executionContext)
        clear()
    }

    private fun clear() {
        if (nonNull(this.statefulSession)) {
            statefulSession.clear()
        }
    }

    override fun jumpToItem(itemIndex: Int) {
        val flushSize = max(fetchSize, 100)
        jumpToItem(cursor, itemIndex, flushSize)
    }

    private fun jumpToItem(cursor: ScrollableResults, itemIndex: Int, flushInterval: Int) {
        for (i in 0 until itemIndex) {
            cursor.next()
            if (i % flushInterval == 0) {
                statefulSession.clear() // Clears in-memory cache
            }
        }
    }

    override fun doClose() {
        this.initialized = false

        this.cursor.close()

        if (nonNull(statefulSession)) {
            val close = ReflectionUtils.findMethod(Session::class.java, "close") as Method
            ReflectionUtils.invokeMethod(close, statefulSession)
        }
    }

}
