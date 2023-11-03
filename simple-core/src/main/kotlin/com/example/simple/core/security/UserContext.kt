package com.example.simple.core.security

import org.apache.commons.lang3.StringUtils

object UserContext {
	private val userThreadLocal: ThreadLocal<String> = InheritableThreadLocal()

	var userName: String
		get() = getUserName(StringUtils.EMPTY)
		set(userName) {
			userThreadLocal.set(userName)
		}

	fun getUserName(defaultUserName: String): String {
		return userThreadLocal.get() ?: return defaultUserName
	}

	fun removeUserName() {
		userThreadLocal.remove()
	}
}
