package com.example.simple.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.EnableMBeanExport
import org.springframework.jmx.support.RegistrationPolicy
import reactor.core.publisher.Hooks

@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
@SpringBootApplication(scanBasePackages = ["com.example.simple"])
class SimpleServerApplication

fun main(args: Array<String>) {
	Hooks.enableAutomaticContextPropagation()
	runApplication<SimpleServerApplication>(*args)
}
