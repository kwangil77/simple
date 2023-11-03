package com.example.simple.core.security

import org.springframework.data.domain.AuditorAware
import java.util.Optional

class SimpleAuditorAware : AuditorAware<String> {
	override fun getCurrentAuditor(): Optional<String> {
		return Optional.of(UserContext.getUserName(DEFAULT_SYSTEM_USER))
	}

	companion object {
		const val DEFAULT_SYSTEM_USER = "System"
	}
}
