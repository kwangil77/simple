package com.example.simple.client.config

import com.example.simple.rest.ApiClient
import com.example.simple.rest.api.UserControllerApi
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class RestConfig(
	@Value("\${rest.simple-api.url}") private val simpleApiUrl: String,
	@Value("\${rest.simple-server.url}") private val simpleServerUrl: String,
) {
	@Bean
	fun userApi(webClient: WebClient): UserControllerApi {
		val apiClient = ApiClient(webClient)
		apiClient.basePath = simpleApiUrl
		val userApi = UserControllerApi()
		userApi.apiClient = apiClient
		return userApi
	}

	@Bean
	fun userServer(webClient: WebClient): UserControllerApi {
		val apiClient = ApiClient(webClient)
		apiClient.basePath = simpleServerUrl
		val userApi = UserControllerApi()
		userApi.apiClient = apiClient
		return userApi
	}
}