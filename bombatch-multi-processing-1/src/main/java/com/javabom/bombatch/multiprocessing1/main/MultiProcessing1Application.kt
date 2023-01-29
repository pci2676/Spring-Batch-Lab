package com.javabom.bombatch.multiprocessing1.main

import com.javabom.bombatch.multiprocessing1.config.ApplicationConfig
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import
import kotlin.system.exitProcess

@SpringBootApplication
@Import(ApplicationConfig::class)
class MultiProcessing1Application

fun main(vararg args: String) {
    val exit = SpringApplication.exit(runApplication<MultiProcessing1Application>(*args))
    exitProcess(exit)
}

