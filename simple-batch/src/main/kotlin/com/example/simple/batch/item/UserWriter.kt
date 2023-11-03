package com.example.simple.batch.item

import com.example.simple.core.model.User
import com.example.simple.core.repository.UserRepository
import org.springframework.batch.item.Chunk
import org.springframework.batch.item.data.RepositoryItemWriter

class UserWriter(
	private val repository: UserRepository
): RepositoryItemWriter<User>() {
	init {
		setRepository(repository)
	}

	@Throws(Exception::class)
	override fun doWrite(items: Chunk<out User>) {
		repository.saveAll(items)
	}
}