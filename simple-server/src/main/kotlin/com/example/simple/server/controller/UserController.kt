package com.example.simple.server.controller

import com.example.simple.server.model.UserResponse
import com.example.simple.server.repository.UserRepository
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
	fun getUsers() = userRepository.findAll().map { user -> UserResponse(user) }

	@GetMapping("/{id}")
	@QueryMapping("userById")
	@Transactional(readOnly = true)
	fun getUser(@PathVariable @Argument id: Long) =
		userRepository.findById(id).map { user -> UserResponse(user) }

	@PostMapping
	@Transactional
	fun createUser(@RequestBody user: UserResponse) =
		userRepository.save(user.convertUser()).map { user -> UserResponse(user) }
}
