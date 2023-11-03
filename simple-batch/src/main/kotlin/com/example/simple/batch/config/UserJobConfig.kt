package com.example.simple.batch.config

import com.example.simple.batch.item.UserProcessor
import com.example.simple.batch.item.UserReader
import com.example.simple.batch.item.UserWriter
import com.example.simple.core.model.User
import com.example.simple.core.repository.UserRepository
import org.springframework.batch.core.job.builder.JobBuilder
import org.springframework.batch.core.launch.support.RunIdIncrementer
import org.springframework.batch.core.repository.JobRepository
import org.springframework.batch.core.step.builder.StepBuilder
import org.springframework.cloud.task.configuration.EnableTask
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.Sort
import org.springframework.transaction.PlatformTransactionManager

@EnableTask
@Configuration
class UserJobConfig(
	private val batchTransactionManager: PlatformTransactionManager,
	private val jobRepository: JobRepository,
	private val userRepository: UserRepository
) {
	@Bean
	fun job() = JobBuilder("user-job", jobRepository)
		.incrementer(RunIdIncrementer())
		.start(step())
		.build()

	@Bean
	fun step() = StepBuilder("user-step", jobRepository)
		.chunk<User, User>(1, batchTransactionManager)
		.reader(reader())
		.processor(processor())
		.writer(writer())
		.build()

	@Bean
	fun reader() = UserReader("user-reader", userRepository, mapOf("id" to Sort.Direction.ASC))

	@Bean
	fun writer() = UserWriter(userRepository)

	@Bean
	fun processor() = UserProcessor()
}