package com.example.simple.api

import org.springframework.aot.hint.MemberCategory
import org.springframework.aot.hint.RuntimeHints
import org.springframework.aot.hint.RuntimeHintsRegistrar
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.EnableMBeanExport
import org.springframework.context.annotation.ImportRuntimeHints
import org.springframework.jmx.support.RegistrationPolicy

@ImportRuntimeHints(SimpleApiApplication.Hints::class)
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
@SpringBootApplication(scanBasePackages = ["com.example.simple"])
class SimpleApiApplication {
	class Hints : RuntimeHintsRegistrar {
		override fun registerHints(hints: RuntimeHints, classLoader: ClassLoader?) {
			hints.reflection().registerType(java.lang.Module::class.java, MemberCategory.INVOKE_DECLARED_METHODS)
			hints.reflection().registerType(java.lang.ModuleLayer::class.java, MemberCategory.INVOKE_DECLARED_METHODS)
			hints.reflection().registerType(java.lang.module.Configuration::class.java,
				MemberCategory.INVOKE_DECLARED_METHODS)
			hints.reflection().registerType(java.lang.module.ResolvedModule::class.java,
				MemberCategory.INVOKE_DECLARED_METHODS)
		}
	}
}

fun main(args: Array<String>) {
	runApplication<SimpleApiApplication>(*args)
}
