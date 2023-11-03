package com.example.simple.client.config

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.springframework.boot.info.BuildProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.client.reactive.ReactorResourceFactory
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ExchangeFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.Connection
import reactor.netty.http.client.HttpClient
import reactor.netty.resources.ConnectionProvider
import java.time.Duration
import java.util.concurrent.TimeUnit

@Configuration
class WebClientConfig(
	private val webClientBuilder: WebClient.Builder
) {
	@Bean
	fun webClient(buildProperties: BuildProperties): WebClient {
		val clientHttpConnector = ReactorClientHttpConnector(resourceFactory()) { httpClient: HttpClient ->
			httpClient
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECT_TIMEOUT_MILLIS)
				.doOnConnected { connection: Connection ->
					connection
						.addHandlerLast(ReadTimeoutHandler(READ_TIMEOUT_SEC, TimeUnit.SECONDS))
						.addHandlerLast(WriteTimeoutHandler(WRITE_TIMEOUT_SEC, TimeUnit.SECONDS))
				}
		}
		return webClientBuilder
			.clientConnector(clientHttpConnector)
			.filter { request: ClientRequest, next: ExchangeFunction ->
				next.exchange(ClientRequest.from(request).header(
					HttpHeaders.USER_AGENT, "${buildProperties.name} - ${buildProperties.version}").build())
			}.build()
	}

	fun resourceFactory(): ReactorResourceFactory {
		val factory = ReactorResourceFactory()
		factory.isUseGlobalResources = false
		factory.connectionProvider = ConnectionProvider
			.builder("exampleConnectionPool")
			.maxConnections(CONNECTION_POOL_SIZE)
			.pendingAcquireMaxCount(-1)
			.pendingAcquireTimeout(Duration.ofSeconds(READ_TIMEOUT_SEC))
			.maxLifeTime(Duration.ofSeconds(MAX_LIFE_TIME_SEC))
			.maxIdleTime(Duration.ofSeconds(MAX_IDLE_TIME_SEC)).build()
		factory.afterPropertiesSet()
		return factory
	}

	companion object {
		private const val CONNECT_TIMEOUT_MILLIS = 5 * 1000
		private const val READ_TIMEOUT_SEC = 60L
		private const val WRITE_TIMEOUT_SEC = 60L
		private const val MAX_LIFE_TIME_SEC = 60 * 10L
		private const val MAX_IDLE_TIME_SEC = 50L
		private const val CONNECTION_POOL_SIZE = 50
	}
}
