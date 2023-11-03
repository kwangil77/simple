package com.example.simple.client.service

import com.example.simple.client.model.UserResponse
import com.example.simple.grpc.Empty
import com.example.simple.grpc.ReactorUserGrpc
import com.example.simple.grpc.UserRequest
import net.devh.boot.grpc.client.inject.GrpcClient
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class UserService(
	@GrpcClient("simple-api") private val userStub: ReactorUserGrpc.ReactorUserStub,
	@GrpcClient("simple-server") private val reactorUserStub: ReactorUserGrpc.ReactorUserStub
) {
	fun getUsers() =
		userStub.getUsers(Empty.newBuilder().build()).flatMap {
			user -> Flux.just(UserResponse(user))
		}

	fun getUser(id: Long) =
		userStub.getUser(UserRequest.newBuilder().setId(id).build()).flatMap {
			user -> Mono.just(UserResponse(user))
		}

	fun getReactorUsers() =
		reactorUserStub.getUsers(Empty.newBuilder().build()).flatMap {
			user -> Mono.just(UserResponse(user))
		}

	fun getReactorUser(id: Long) =
		reactorUserStub.getUser(UserRequest.newBuilder().setId(id).build()).flatMap {
			user -> Mono.just(UserResponse(user))
		}
}