package com.example.simple.api.model

import com.example.simple.core.model.User
import java.time.ZoneId

class UserResponse(user: User) {
	val id = user.id
	val email = user.email
	val createdBy = user.createdBy
	val createdDate = user.createdDate?.atZone(ZoneId.systemDefault())
	val lastModifiedBy = user.lastModifiedBy
	val lastModifiedDate = user.lastModifiedDate?.atZone(ZoneId.systemDefault())

	fun convertUser() : User {
		val user = User(id, email)
		user.createdBy = createdBy
		user.createdDate = createdDate?.toInstant()
		user.lastModifiedBy = lastModifiedBy
		user.lastModifiedDate = lastModifiedDate?.toInstant()
		return user
	}
}