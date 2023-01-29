package com.javabom.otmp.test

import com.javabom.otmp.main.OtmpApplication
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ActiveProfiles("test")
@SpringBootTest(classes = [OtmpApplication::class])
@Import(BatchTestConfig::class)
annotation class BatchSpringTest
