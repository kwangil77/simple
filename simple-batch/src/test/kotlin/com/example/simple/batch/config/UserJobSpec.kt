package com.example.simple.batch.config

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.extensions.spring.SpringExtension
import org.springframework.batch.core.ExitStatus
import org.springframework.batch.core.JobExecution
import org.springframework.batch.core.JobInstance
import org.springframework.batch.core.JobParameters
import org.springframework.batch.test.JobLauncherTestUtils
import org.springframework.batch.test.JobRepositoryTestUtils
import org.springframework.batch.test.context.SpringBatchTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import strikt.api.expectThat
import strikt.assertions.isEqualTo

@SpringBatchTest
@ActiveProfiles("local")
@SpringBootTest
class UserJobSpec @Autowired constructor(
	private val jobLauncherTestUtils: JobLauncherTestUtils,
	private val jobRepositoryTestUtils: JobRepositoryTestUtils
) : DescribeSpec() {
	override fun extensions() = listOf(SpringExtension)

	init {
		this.describe("user") {
			context("run user job") {
				it("returns completed") {
					val jobParameters: JobParameters = jobLauncherTestUtils.uniqueJobParameters
					val jobExecution: JobExecution = jobLauncherTestUtils.launchJob(jobParameters)
					val actualJobInstance: JobInstance = jobExecution.jobInstance
					val actualJobExitStatus: ExitStatus = jobExecution.exitStatus

					expectThat(actualJobInstance.jobName).isEqualTo("user-job")
					expectThat(actualJobExitStatus.exitCode).isEqualTo(ExitStatus.COMPLETED.exitCode)
				}
			}
			afterSpec {
				jobRepositoryTestUtils.removeJobExecutions()
			}
		}
	}
}