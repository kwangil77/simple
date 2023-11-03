package com.example.simple.batch.item

import com.example.simple.core.model.User
import org.springframework.batch.item.ItemProcessor

class UserProcessor : ItemProcessor<User, User> {
	override fun process(user: User): User {
		return User(user.id, user.email)
	}
}