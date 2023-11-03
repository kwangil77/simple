package com.example.simple.server.repository

import com.example.simple.server.model.User
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.data.repository.reactive.ReactiveSortingRepository

interface UserRepository : ReactiveSortingRepository<User, Long>, ReactiveCrudRepository<User, Long>
