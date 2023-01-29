package com.javabom.otmp.main

import com.javabom.otmp.config.OtmpConfig
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Import
import kotlin.system.exitProcess


@Import(OtmpConfig::class)
@SpringBootApplication
class OtmpApplication

fun main(vararg args: String) {
    val exit = SpringApplication.exit(runApplication<OtmpApplication>(*args))
    exitProcess(exit)
}
