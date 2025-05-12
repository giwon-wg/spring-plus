package com.example

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    val dotenv = Dotenv.configure()
        .directory("./")
        .filename(".env")
        .ignoreIfMalformed()
        .ignoreIfMissing()
        .load()

    // 필수 환경 변수들을 시스템 프로퍼티로 등록
    dotenv.entries().forEach { entry ->
        System.setProperty(entry.key, entry.value)
    }

    runApplication<Application>(*args)
}