package com.example.simple.batch.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cloud.task.configuration.DefaultTaskConfigurer
import org.springframework.cloud.task.configuration.TaskConfigurer
import org.springframework.cloud.task.configuration.TaskProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

@Configuration
class TaskConfig {
	@Bean
	fun taskConfigurer(
			@Qualifier("batchDataSource") batchDataSource: DataSource, taskProperties: TaskProperties): TaskConfigurer {
		return DefaultTaskConfigurer(batchDataSource, taskProperties.tablePrefix, null)
	}
}