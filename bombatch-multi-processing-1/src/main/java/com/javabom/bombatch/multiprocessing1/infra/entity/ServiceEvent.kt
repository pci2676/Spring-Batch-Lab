package com.javabom.bombatch.multiprocessing1.infra.entity

import java.util.UUID
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table
import javax.persistence.UniqueConstraint

@Entity
@Table(
    name = "service_event",
    uniqueConstraints = [
        UniqueConstraint(name = "UK_SERVICE_EVENT_1", columnNames = ["uuid"])
    ]
)
class ServiceEvent(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long? = null,

    @Column(name = "uuid", columnDefinition = "VARCHAR(32) COMMENT '이벤트 고유번호'")
    val uuid: String = UUID.randomUUID().toString(),

    _name: String,
) {

    @Column(name = "name", columnDefinition = "VARCHAR(256) COMMENT '이름'")
    var name: String = _name
        protected set
}
