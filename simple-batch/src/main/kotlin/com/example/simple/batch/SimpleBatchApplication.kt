package com.example.simple.batch

import org.springframework.aop.SpringProxy
import org.springframework.aop.framework.Advised
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.aot.hint.TypeReference
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.EnableMBeanExport
import org.springframework.context.annotation.ImportRuntimeHints
import org.springframework.core.DecoratingProxy
import org.springframework.jmx.support.RegistrationPolicy

@ImportRuntimeHints(SimpleBatchApplication.Hints::class)
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
@SpringBootApplication(scanBasePackages = ["com.example.simple"])
class SimpleBatchApplication {
	class Hints : RuntimeHintsRegistrar {
		override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
			hints.proxies().registerJdkProxy { builder ->
				builder
					.proxiedInterfaces(
						TypeReference.of(org.springframework.batch.core.launch.JobOperator::class.qualifiedName!!))
					.proxiedInterfaces(SpringProxy::class.java, Advised::class.java, DecoratingProxy::class.java)
			}
		}
	}
}

fun main(args: Array<String>) {
	runApplication<SimpleBatchApplication>(*args)
}
