package com.example.simple.api.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class SecurityConfig {
	@Bean
	fun corsConfigurationSource() =
		UrlBasedCorsConfigurationSource().apply {
			registerCorsConfiguration(
				"/**",
				CorsConfiguration().apply {
					addAllowedOrigin("*")
					addAllowedHeader("*")
					addAllowedMethod("*")
				}
			)
		}
}