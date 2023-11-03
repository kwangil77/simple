package com.example.simple.client.model

import com.example.simple.rest.model.UserResponse
import java.time.Instant
import java.time.ZoneOffset

class UserResponse(user: UserResponse) {
	val id = user.id
	val email = user.email
	val createdBy = user.createdBy
	val createdDate = user.createdDate
	val lastModifiedBy = user.lastModifiedBy
	val lastModifiedDate = user.lastModifiedDate

	constructor(user: com.example.simple.grpc.UserResponse) : this(
		UserResponse()
			.id(user.id)
			.email(user.email)
			.createdBy(user.createdBy)
			.createdDate(Instant.ofEpochSecond(user.createdDate.seconds, user.createdDate.nanos.toLong())
				.atOffset(ZoneOffset.UTC))
			.lastModifiedBy(user.lastModifiedBy)
			.lastModifiedDate(Instant.ofEpochSecond(user.lastModifiedDate.seconds, user.lastModifiedDate.nanos.toLong())
				.atOffset(ZoneOffset.UTC))
	)
	constructor(user: com.example.simple.graphql.User) : this(
		UserResponse()
			.id(user.id.toLong())
			.email(user.email)
			.createdBy(user.createdBy)
			.createdDate(user.createdDate)
			.lastModifiedBy(user.lastModifiedBy)
			.lastModifiedDate(user.lastModifiedDate)
	)
}