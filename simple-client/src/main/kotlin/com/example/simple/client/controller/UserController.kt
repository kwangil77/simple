package com.example.simple.client.controller

import com.example.simple.client.model.UserResponse
import com.example.simple.client.service.UserGqlService
import com.example.simple.client.service.UserService
import com.example.simple.rest.api.UserControllerApi
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/users")
class UserController (
	@Qualifier("userApi") private val userApi: UserControllerApi,
	@Qualifier("userServer") private val userServer: UserControllerApi,
	private val userService: UserService,
	private val userGqlService: UserGqlService
) {
	@GetMapping
	fun getUsers(@RequestParam(required = false, defaultValue = "") protocol: String,
      @RequestParam(required = false, defaultValue = "") type: String): Flux<UserResponse> {
		return if (protocol == "grpc") {
			if (StringUtils.hasLength(type)) {
				userService.getReactorUsers()
			} else {
				userService.getUsers()
			}
		} else if (protocol == "graphql") {
			if (StringUtils.hasLength(type)) {
				userGqlService.getReactorUsers()
			} else {
				userGqlService.getUsers()
			}
		} else {
			if (StringUtils.hasLength(type)) {
				userServer.getUsers().map { user -> UserResponse(user) }
			} else {
				userApi.getUsers().map { user -> UserResponse(user) }
			}
		}
	}

	@GetMapping("/{id}")
	fun getUser(@PathVariable id: Long, @RequestParam(required = false, defaultValue = "") protocol: String,
      @RequestParam(required = false, defaultValue = "") type: String): Mono<UserResponse> {
		return if (protocol == "grpc") {
			if (StringUtils.hasLength(type)) {
				userService.getReactorUser(id)
			} else {
				userService.getUser(id)
			}
		} else if (protocol == "graphql") {
			if (StringUtils.hasLength(type)) {
				userGqlService.getReactorUser(id)
			} else {
				userGqlService.getUser(id)
			}
		} else {
			if (StringUtils.hasLength(type)) {
				userServer.getUser(id).map { user -> UserResponse(user) }
			} else {
				userApi.getUser(id).map { user -> UserResponse(user) }
			}
		}
	}
}