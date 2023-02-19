package com.javabom.bombatch.multiprocessing2.main


import com.javabom.bombatch.multiprocessing2.config.ApplicationConfig
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import
import kotlin.system.exitProcess

@SpringBootApplication
@Import(ApplicationConfig::class)
class MultiProcessing2Application

fun main(vararg args: String) {
    val exit = SpringApplication.exit(runApplication<MultiProcessing2Application>(*args))
    exitProcess(exit)
}

