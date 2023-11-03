package com.example.simple.api.service

import com.google.common.collect.Streams
import com.example.simple.core.model.User
import com.example.simple.core.repository.UserRepository
import com.example.simple.grpc.ReactorUserGrpc
import com.example.simple.grpc.UserRequest
import com.example.simple.grpc.UserResponse
import com.example.simple.grpc.Empty
import net.devh.boot.grpc.server.service.GrpcService
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.Optional

@GrpcService
class UserService(
	private val userRepository: UserRepository
) : ReactorUserGrpc.UserImplBase() {
	@Transactional(readOnly = true)
	override fun getUsers(request: Empty) =
		Flux.fromIterable(
			Streams.stream(userRepository.findAll()).map { user ->
				convertUserResponse(user)
			}.toList()
		)

	@Transactional(readOnly = true)
	override fun getUser(request: UserRequest) =
		Mono.just(
			userRepository.findById(request.id).flatMap {
				user -> Optional.of(convertUserResponse(user))
			}.get()
		)

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