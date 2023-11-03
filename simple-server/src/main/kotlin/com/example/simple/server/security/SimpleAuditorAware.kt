package com.example.simple.server.security

import org.springframework.data.domain.ReactiveAuditorAware
import reactor.core.publisher.Mono

class SimpleAuditorAware : ReactiveAuditorAware<String> {
	override fun getCurrentAuditor(): Mono<String> {
		return Mono.just(UserContext.getUserName(DEFAULT_SYSTEM_USER))
	}

	companion object {
		const val DEFAULT_SYSTEM_USER = "System"
	}
}
