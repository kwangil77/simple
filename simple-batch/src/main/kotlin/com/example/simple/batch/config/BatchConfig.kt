package com.example.simple.batch.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.jdbc.DataSourceBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.jdbc.datasource.DataSourceTransactionManager
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.util.StringUtils
import javax.sql.DataSource

@Configuration
class BatchConfig(
	@Value("\${spring.datasource.url:}") private val url: String
) {
	@Primary
	@Bean
	fun batchTransactionManager(@Qualifier("batchDataSource") batchDataSource: DataSource): PlatformTransactionManager {
		return DataSourceTransactionManager(batchDataSource)
	}

	@Primary
	@Bean
	@ConfigurationProperties("spring.datasource")
	fun batchDataSource(): com.zaxxer.hikari.HikariDataSource {
		val dataSourceBuilder = DataSourceBuilder.create()

		if (StringUtils.hasText(url)) {
			dataSourceBuilder.url(url)
		}
		return dataSourceBuilder.type(com.zaxxer.hikari.HikariDataSource::class.java).build()
	}
}