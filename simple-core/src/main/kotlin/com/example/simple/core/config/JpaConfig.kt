package com.example.simple.core.config

import com.example.simple.core.model.Auditable
//import com.example.simple.core.model.ModelsMarker
import com.example.simple.core.model.User
import com.example.simple.core.repository.RepositoriesMarker
import com.example.simple.core.security.SimpleAuditorAware
import io.micrometer.observation.ObservationRegistry
import jakarta.persistence.EntityManagerFactory
import net.ttddyy.dsproxy.listener.logging.SLF4JQueryLoggingListener
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder
import net.ttddyy.dsproxy.transform.TransformInfo
import net.ttddyy.observation.tracing.DataSourceObservationListener
import org.hibernate.cfg.AvailableSettings
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
//import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.Database
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.transaction.support.TransactionSynchronizationManager
import org.springframework.orm.jpa.persistenceunit.PersistenceManagedTypes
import javax.sql.DataSource

@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
@EnableJpaRepositories(
	basePackageClasses = [RepositoriesMarker::class],
	transactionManagerRef = "simpleTransactionManager",
	entityManagerFactoryRef = "simpleEntityManagerFactory"
)
@EnableJpaAuditing(auditorAwareRef = "simpleAuditor")
class JpaConfig(
	private val observationRegistry: ObservationRegistry,
	@Value("\${meta.release:unknown}") private val release: String
) {
	@Bean
	fun simpleEntityManagerFactory(
		@Qualifier("simpleDataSource") simpleDataSource: DataSource): LocalContainerEntityManagerFactoryBean {
		val jpaVendorAdapter = HibernateJpaVendorAdapter()
		jpaVendorAdapter.setGenerateDdl(false)
		jpaVendorAdapter.setDatabase(Database.MYSQL)
		jpaVendorAdapter.setDatabasePlatform(org.hibernate.dialect.MySQLDialect::class.qualifiedName)
		val entityManagerFactoryBean = LocalContainerEntityManagerFactoryBean()
		entityManagerFactoryBean.dataSource = simpleDataSource
		entityManagerFactoryBean.jpaVendorAdapter = jpaVendorAdapter
//    entityManagerFactoryBean.setPackagesToScan(
//        ModelsMarker::class.java.getPackage().name,
//        Jsr310JpaConverters::class.java.getPackage().name
//    )
		entityManagerFactoryBean.setManagedTypes(PersistenceManagedTypes.of(
			Auditable::class.java.name,
			User::class.java.name
		))
		entityManagerFactoryBean.setJpaPropertyMap(
			mapOf(
				AvailableSettings.GLOBALLY_QUOTED_IDENTIFIERS to true
			)
		)
		entityManagerFactoryBean.persistenceUnitName = "simplePU"
		return entityManagerFactoryBean
	}

	@Bean
	fun simpleTransactionManager(simpleEntityManagerFactory: EntityManagerFactory): PlatformTransactionManager {
		val jpaTransactionManager = JpaTransactionManager(simpleEntityManagerFactory)
		jpaTransactionManager.entityManagerFactory = simpleEntityManagerFactory
		return jpaTransactionManager
	}

	@Bean
	fun simpleDataSource(
			@Qualifier("simpleMasterDataSource") simpleMasterDataSource: DataSource,
			@Qualifier("simpleSlaveDataSource") simpleSlaveDataSource: DataSource): DataSource {
		return LazyConnectionDataSourceProxy(
			ProxyDataSourceBuilder.create(
				ReplicationRoutingDataSource(simpleMasterDataSource, simpleSlaveDataSource)
			)
				.name("simpleDS")
				.listener(SLF4JQueryLoggingListener())
				.listener(DataSourceObservationListener(observationRegistry))
				.queryTransformer { transformInfo: TransformInfo ->
					String.format(
						null,
						"/* %s */ %s",
						release,
						transformInfo.query
					)
				}
				.build())
	}

	@Bean
	@ConfigurationProperties("simple.datasource.master")
	fun simpleMasterDataSource(): com.zaxxer.hikari.HikariDataSource {
		return DataSourceBuilder.create().type(com.zaxxer.hikari.HikariDataSource::class.java).build()
	}

	@Bean
	@ConfigurationProperties("simple.datasource.slave")
	fun simpleSlaveDataSource(): com.zaxxer.hikari.HikariDataSource {
		return DataSourceBuilder.create().type(com.zaxxer.hikari.HikariDataSource::class.java).build()
	}

	@Bean
	fun simpleAuditor() = SimpleAuditorAware()

	class ReplicationRoutingDataSource(
		private val writeDataSource: DataSource,
		private val readDataSource: DataSource
	) :
		AbstractRoutingDataSource() {
		init {
			val dataSources: Map<Any, Any> = mapOf(
				DataSourceContextType.MASTER to writeDataSource,
				DataSourceContextType.SLAVE to readDataSource
			)
			setTargetDataSources(dataSources)
			setDefaultTargetDataSource(writeDataSource)
			super.afterPropertiesSet()
		}

		override fun determineCurrentLookupKey(): Any {
			if (TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
				return DataSourceContextType.SLAVE
			}
			return DataSourceContextType.MASTER
		}

		private enum class DataSourceContextType {
			MASTER, SLAVE
		}
	}
}
