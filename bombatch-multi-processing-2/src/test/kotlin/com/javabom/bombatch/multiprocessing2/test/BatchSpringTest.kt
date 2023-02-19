package com.javabom.bombatch.multiprocessing2.test

import com.javabom.bombatch.multiprocessing2.main.MultiProcessing2Application
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ActiveProfiles("test")
@SpringBootTest(classes = [MultiProcessing2Application::class])
@Import(BatchTestConfig::class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
annotation class BatchSpringTest
