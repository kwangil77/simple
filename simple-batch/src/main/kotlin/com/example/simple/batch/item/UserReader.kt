package com.example.simple.batch.item

import com.example.simple.core.model.User
import com.example.simple.core.repository.UserRepository
import org.springframework.batch.item.data.RepositoryItemReader
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

class UserReader(
	private val name: String,
	private val repository: UserRepository,
	private val sorts: Map<String, Sort.Direction>
): RepositoryItemReader<User>() {
	@Volatile private var page = 0
	private var pageSize = 10
	@Volatile private var current = 0
	@Volatile private var results: List<User>? = null
	private val lock = Any()

	init {
		setName(name)
		setRepository(repository)
		setSort(sorts)
		setMethodName("findAll")
	}

	@Throws(Exception::class)
	override fun doRead(): User? {
		synchronized(lock) {
			val nextPageNeeded = results != null && current >= results!!.size
			if (results == null || nextPageNeeded) {
				results = doPageRead()
				page++
				if (results!!.isEmpty()) {
					return null
				}
				if (nextPageNeeded) {
					current = 0
				}
			}
			return if (current < results!!.size) {
				val curLine: User = results!![current]
				current++
				curLine
			} else {
				null
			}
		}
	}

	@Throws(Exception::class)
	override fun jumpToItem(itemLastIndex: Int) {
		synchronized(lock) {
			page = itemLastIndex / pageSize
			current = itemLastIndex % pageSize
		}
	}

	@Throws(Exception::class)
	override fun doPageRead(): List<User> {
		val sort: Sort = Sort.by(sorts.entries.map { entry -> Sort.Order(entry.value, entry.key) })
		val pageRequest: Pageable = PageRequest.of(page, pageSize, sort)
		val curPage = repository.findAll(pageRequest)
		return curPage.content
	}
}