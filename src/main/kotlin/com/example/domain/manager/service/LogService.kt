package com.example.domain.manager.service

import com.example.domain.manager.entity.Log
import com.example.domain.manager.repository.LogRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class LogService(

    private val logRepository: LogRepository
) {

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun saveLog(action: String, details: String) {
        val log = Log(action = action, details = details)
        logRepository.save(log)
    }
}

