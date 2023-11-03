package com.example.simple.core.repository

import com.example.simple.core.model.User
import org.springframework.data.querydsl.QuerydslPredicateExecutor
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.PagingAndSortingRepository

interface UserRepository : CrudRepository<User, Long>, PagingAndSortingRepository<User, Long>,
		QuerydslPredicateExecutor<User>
