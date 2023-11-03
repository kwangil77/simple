package com.example.simple.api.controller

import com.google.common.collect.Streams
import com.querydsl.core.types.Predicate
import com.example.simple.api.model.UserResponse
import com.example.simple.core.model.QUser
import com.example.simple.core.repository.UserRepository
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping

@RestController
@RequestMapping("/users")
class UserController(
	private val userRepository: UserRepository
) {
	@GetMapping
	@QueryMapping("users")
	@Transactional(readOnly = true)
	fun getUsers() =
		Streams.stream(userRepository.findAll()).map { user -> UserResponse(user) }.toList()

	@GetMapping("/{id}")
	@QueryMapping("userById")
	@Transactional(readOnly = true)
	fun getUser(@PathVariable @Argument id: Long): UserResponse {
		val predicate: Predicate = QUser.user.id.eq(id)
		return UserResponse(userRepository.findOne(predicate).orElse(null))
	}

	@PostMapping
	@Transactional
	fun createUser(@RequestBody user: UserResponse) =
		UserResponse(userRepository.save(user.convertUser()))
}
