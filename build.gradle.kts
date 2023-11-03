import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	java
	`java-library`
	jacoco
	id("io.spring.dependency-management") version "1.1.3"
	id("com.gorylenko.gradle-git-properties") version "2.4.1" apply false
	id("org.sonarqube") version "4.4.1.3373" apply false
	id("org.springframework.boot") version "3.1.5"
	id("org.graalvm.buildtools.native") version "0.9.28"
	kotlin("jvm") version "1.8.22" apply false
	kotlin("plugin.spring") version "1.8.22" apply false
}

val mockkVersion: String by project
val striktVersion: String by project
val kotestVersion: String by project

allprojects {
	repositories {
		maven {
			url = uri("https://maven.pkg.github.com/kwangil77/grpc-spring")
			credentials {
				username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
				password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
			}
		}
		mavenCentral()
	}
}

subprojects {
	apply {
		plugin("java")
		plugin("java-library")
		plugin("idea")
		plugin("jacoco")
		plugin("com.gorylenko.gradle-git-properties")
		plugin("org.sonarqube")
		plugin("org.springframework.boot")
		plugin("org.graalvm.buildtools.native")
		plugin("io.spring.dependency-management")
		plugin("kotlin")
		plugin("kotlin-spring")
	}
	project.group = "com.example"
	project.version = "0.0.1-SNAPSHOT"

	java {
		sourceCompatibility = JavaVersion.VERSION_17
	}

	dependencies {
		implementation("org.slf4j:jul-to-slf4j")
		implementation("org.slf4j:slf4j-api")
		implementation("org.slf4j:slf4j-ext")

		implementation("ch.qos.logback:logback-core")
		implementation("ch.qos.logback:logback-access")
		implementation("ch.qos.logback:logback-classic")

		implementation("net.logstash.logback:logstash-logback-encoder:7.4")

		implementation("com.fasterxml.jackson.core:jackson-databind")

		implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.22")
		implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.22")

		testImplementation("org.springframework.boot:spring-boot-starter-test") {
			exclude("org.ow2.asm")
		}
		testImplementation("io.mockk:mockk:${mockkVersion}")
		testImplementation("io.strikt:strikt-core:${striktVersion}")
		testImplementation("org.opentest4j:opentest4j:1.3.0")

		testImplementation("io.kotest:kotest-runner-junit5-jvm:${kotestVersion}") {
			exclude("net.java.dev.jna")
		}
		testImplementation("io.kotest:kotest-assertions-core:${kotestVersion}")
		testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.3")
	}

	tasks.withType<KotlinCompile> {
		kotlinOptions {
			freeCompilerArgs = listOf("-Xjsr305=strict")
			jvmTarget = JavaVersion.VERSION_17.toString()
		}
	}

	tasks.withType<Test> {
		useJUnitPlatform()
	}

	tasks.jacocoTestReport {
		reports {
			xml.required.set(true)
		}
	}
}
