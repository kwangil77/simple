package com.example.simple.batch

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.EnableMBeanExport
import org.springframework.jmx.support.RegistrationPolicy

@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
@SpringBootApplication(scanBasePackages = ["com.example.simple"])
class SimpleBatchApplication

fun main(args: Array<String>) {
	runApplication<SimpleBatchApplication>(*args)
}
