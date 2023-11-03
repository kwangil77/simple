package com.example.simple.client.service

import com.google.common.collect.Streams
import com.kobylynskyi.graphql.codegen.model.graphql.GraphQLRequest
import com.example.simple.client.model.UserResponse
import com.example.simple.graphql.UserByIdQueryRequest
import com.example.simple.graphql.UserByIdQueryResponse
import com.example.simple.graphql.UserResponseProjection
import com.example.simple.graphql.UsersQueryRequest
import com.example.simple.graphql.UsersQueryResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class UserGqlService(
	@Qualifier("apiClient") private val apiClient: WebClient.RequestBodySpec,
	@Qualifier("serverClient") private val serverClient: WebClient.RequestBodySpec
) {
	fun getUsers() =
		apiClient.bodyValue(GraphQLRequest(
				UsersQueryRequest.builder().build(), UserResponseProjection().`all$`()).toHttpJsonBody())
			.retrieve().toEntity(UsersQueryResponse::class.java).flatMapMany { response ->
				Flux.fromIterable(
					Streams.stream(response.body!!.users().iterator()).map { user ->
						UserResponse(user)
					}.toList()
				)
			}

	fun getUser(id: Long) =
		apiClient.bodyValue(GraphQLRequest(
				UserByIdQueryRequest.builder().setId(id.toString()).build(), UserResponseProjection().`all$`()).toHttpJsonBody())
			.retrieve().toEntity(UserByIdQueryResponse::class.java).flatMap { response ->
				Mono.just(UserResponse(response.body!!.userById()))
			}

	fun getReactorUsers() =
		serverClient.bodyValue(GraphQLRequest(
				UsersQueryRequest.builder().build(), UserResponseProjection().`all$`()).toHttpJsonBody())
			.retrieve().toEntity(UsersQueryResponse::class.java).flatMapMany { response ->
				Flux.fromIterable(
					Streams.stream(response.body!!.users().iterator()).map { user ->
						UserResponse(user)
					}.toList()
				)
			}

	fun getReactorUser(id: Long) =
		serverClient.bodyValue(GraphQLRequest(
				UserByIdQueryRequest.builder().setId(id.toString()).build(), UserResponseProjection().`all$`()).toHttpJsonBody())
			.retrieve().toEntity(UserByIdQueryResponse::class.java).flatMap { response ->
				Mono.just(UserResponse(response.body!!.userById()))
			}
}