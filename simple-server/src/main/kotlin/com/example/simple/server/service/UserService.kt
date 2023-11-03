package com.example.simple.server.service

import com.example.simple.grpc.Empty
import com.example.simple.grpc.ReactorUserGrpc
import com.example.simple.grpc.UserRequest
import com.example.simple.grpc.UserResponse
import com.example.simple.server.model.User
import com.example.simple.server.repository.UserRepository
import net.devh.boot.grpc.server.service.GrpcService
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@GrpcService
class UserService(
	private val userRepository: UserRepository
) : ReactorUserGrpc.UserImplBase() {
	@Transactional(readOnly = true)
	override fun getUser(request: UserRequest) =
		userRepository.findById(request.id).flatMap {
			user -> Mono.just(convertUserResponse(user))
		}

	@Transactional(readOnly = true)
	override fun getUsers(request: Empty) =
		userRepository.findAll().flatMap {
			user -> Flux.just(convertUserResponse(user))
		}

	fun convertUserResponse(user: User) =
		UserResponse.newBuilder()
			.setId(user.id)
			.setEmail(user.email)
			.setCreatedBy(user.createdBy)
			.setCreatedDate(
				com.google.protobuf.Timestamp.newBuilder()
					.setSeconds(user.createdDate!!.epochSecond)
					.setNanos(user.createdDate!!.nano)
					.build()
			)
			.setLastModifiedBy(user.lastModifiedBy)
			.setLastModifiedDate(
				com.google.protobuf.Timestamp.newBuilder()
					.setSeconds(user.lastModifiedDate!!.epochSecond)
					.setNanos(user.lastModifiedDate!!.nano)
					.build()
			)
			.build()!!
}