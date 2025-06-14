package com.example.domain.manager.repository

import com.example.domain.manager.entity.Log
import org.springframework.data.jpa.repository.JpaRepository

interface LogRepository : JpaRepository<Log, Long>
