package com.example.simple.client

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.EnableMBeanExport
import org.springframework.jmx.support.RegistrationPolicy
import reactor.core.publisher.Hooks

@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
@SpringBootApplication(scanBasePackages = ["com.example.simple"])
class SimpleClientApplication

fun main(args: Array<String>) {
  Hooks.enableAutomaticContextPropagation()
  runApplication<SimpleClientApplication>(*args)
}
