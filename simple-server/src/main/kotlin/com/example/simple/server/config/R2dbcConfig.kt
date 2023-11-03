package com.example.simple.server.config

import com.example.simple.server.repository.RepositoriesMarker
import com.example.simple.server.security.SimpleAuditorAware
import io.micrometer.observation.ObservationRegistry
import io.r2dbc.pool.ConnectionPool
import io.r2dbc.pool.ConnectionPoolConfiguration
import io.r2dbc.proxy.ProxyConnectionFactory
import io.r2dbc.proxy.observation.ObservationProxyExecutionListener
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import io.r2dbc.spi.Option
import io.r2dbc.spi.ValidationDepth
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.PropertyMapper
import org.springframework.boot.r2dbc.ConnectionFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.r2dbc.connection.R2dbcTransactionManager
import org.springframework.r2dbc.connection.lookup.AbstractRoutingConnectionFactory
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.transaction.reactive.TransactionSynchronizationManager
import reactor.core.publisher.Mono
import java.time.Duration

@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
@EnableR2dbcRepositories(
	basePackageClasses = [RepositoriesMarker::class]
)
@EnableR2dbcAuditing(auditorAwareRef = "simpleAuditor")
class R2dbcConfig(
	private val observationRegistry: ObservationRegistry
) : AbstractR2dbcConfiguration() {
	override fun connectionFactory(): ConnectionFactory {
		val connectionFactory = ReplicationRoutingConnectionFactory(
			simpleMasterConnectionFactory(), simpleSlaveConnectionFactory())
		return ProxyConnectionFactory.builder(
			connectionFactory
		)
			.listener(ObservationProxyExecutionListener(observationRegistry, connectionFactory, null))
			.build()
	}

	@Bean
	fun simpleTransactionManager() = R2dbcTransactionManager(connectionFactory())

	@Bean
	fun simpleMasterConnectionFactory() = createConnectionFactory(simpleMasterR2dbcProperties())

	@Bean
	fun simpleSlaveConnectionFactory() = createConnectionFactory(simpleSlaveR2dbcProperties())

	@Bean
	@ConfigurationProperties("simple.r2dbc.master")
	fun simpleMasterR2dbcProperties() = R2dbcProperties()

	@Bean
	@ConfigurationProperties("simple.r2dbc.slave")
	fun simpleSlaveR2dbcProperties() = R2dbcProperties()

	@Bean
	fun simpleAuditor() = SimpleAuditorAware()

	fun initOptions(properties: R2dbcProperties): ConnectionFactoryOptions.Builder {
		val builder = ConnectionFactoryOptions.parse(properties.url).mutate()
		builder.option(ConnectionFactoryOptions.USER, properties.username)
		builder.option(ConnectionFactoryOptions.PASSWORD, properties.password)
		if (properties.properties != null) {
			properties.properties.forEach { (key: String, value: String) ->
				builder.option(Option.valueOf(key), value)
			}
		}
		return builder
	}

	fun createConnectionFactory(properties: R2dbcProperties) : ConnectionFactory {
		val builder = ConnectionPoolConfiguration.builder(
			ConnectionFactoryBuilder
				.withOptions(initOptions(properties))
				.build())
		val pool: R2dbcProperties.Pool = properties.pool
		val map = PropertyMapper.get().alwaysApplyingWhenNonNull()
		map.from(pool.maxIdleTime).to { maxIdleTime: Duration? ->
			builder.maxIdleTime(maxIdleTime)
		}
		map.from(pool.maxIdleTime).to { maxIdleTime: Duration? ->
			builder.maxIdleTime(maxIdleTime)
		}
		map.from(pool.maxLifeTime).to { maxLifeTime: Duration? ->
			builder.maxLifeTime(maxLifeTime!!)
		}
		map.from(pool.maxAcquireTime).to { maxAcquireTime: Duration? ->
			builder.maxAcquireTime(maxAcquireTime)
		}
		map.from(pool.maxCreateConnectionTime).to { maxCreateConnectionTime: Duration? ->
			builder.maxCreateConnectionTime(maxCreateConnectionTime)
		}
		map.from(pool.initialSize).to { initialSize: Int? ->
			builder.initialSize(initialSize!!)
		}
		map.from(pool.maxSize).to { maxSize: Int? ->
			builder.maxSize(maxSize!!)
		}
		map.from(pool.validationQuery).whenHasText().to { validationQuery: String? ->
			builder.validationQuery(validationQuery!!)
		}
		map.from(pool.validationDepth).to { validationDepth: ValidationDepth? ->
			builder.validationDepth(validationDepth!!)
		}
		return ConnectionPool(builder.build())
	}

	class ReplicationRoutingConnectionFactory(
		private val writeConnectionFactory: ConnectionFactory,
		private val readConnectionFactory: ConnectionFactory
	) : AbstractRoutingConnectionFactory() {
		init {
			val factories = mapOf(
				ConnectionFactoryContextType.MASTER to writeConnectionFactory,
				ConnectionFactoryContextType.SLAVE to readConnectionFactory
			)
			setTargetConnectionFactories(factories)
			setDefaultTargetConnectionFactory(writeConnectionFactory)
			super.afterPropertiesSet()
		}

		override fun determineCurrentLookupKey(): Mono<Any> {
			return TransactionSynchronizationManager.forCurrentTransaction()
				.flatMap {
					val dataSourceType = if (it.isCurrentTransactionReadOnly) {
						ConnectionFactoryContextType.SLAVE
					} else {
						ConnectionFactoryContextType.MASTER
					}
					Mono.just(dataSourceType)
				}
		}

		private enum class ConnectionFactoryContextType {
			MASTER, SLAVE
		}
	}
}
