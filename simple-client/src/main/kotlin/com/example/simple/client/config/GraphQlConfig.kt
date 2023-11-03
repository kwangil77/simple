package com.example.simple.client.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class GraphQlConfig(
	@Value("\${graphql.simple-api.url}") private val simpleApiUrl: String,
	@Value("\${graphql.simple-server.url}") private val simpleServerUrl: String,
	private val webClient: WebClient
) {
	@Bean
	fun apiClient() = webClient.post().uri(simpleApiUrl)
		.accept(MediaType.APPLICATION_JSON)
		.contentType(MediaType.APPLICATION_JSON)

	@Bean
	fun serverClient() = webClient.post().uri(simpleServerUrl)
		.accept(MediaType.APPLICATION_JSON)
		.contentType(MediaType.APPLICATION_JSON)
}