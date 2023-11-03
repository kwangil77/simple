import org.springframework.boot.gradle.tasks.bundling.BootJar

buildscript {
	repositories {
		mavenCentral()
	}
	dependencies {
		classpath("org.flywaydb:flyway-mysql:9.22.3")
	}
}

plugins {
	`java-library`
	jacoco
	id("org.sonarqube")
	id("org.flywaydb.flyway") version "9.22.3"
	kotlin("plugin.jpa") version "1.8.22"
	kotlin("plugin.allopen")
	kotlin("kapt")
}

tasks.getByName<BootJar>("bootJar") {
	enabled = false
}

flyway {
	url = "jdbc:mariadb://127.0.0.1:3306/simple?allowPublicKeyRetrieval=true&useSSL=false"
	user = "root"
	password = ""
}

dependencies {
	implementation("org.apache.commons:commons-lang3")
	implementation("org.springframework.boot:spring-boot-starter-cache")
	api("org.springframework.boot:spring-boot-starter-data-jpa") {
		exclude(group = "org.yaml", module = "snakeyaml")
	}
	implementation("org.yaml:snakeyaml:2.2")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.hibernate.orm:hibernate-core")

	implementation("net.ttddyy:datasource-proxy:1.8.1")
	implementation("net.ttddyy.observation:datasource-micrometer-spring-boot:1.0.2")
	implementation("org.mariadb.jdbc:mariadb-java-client")
	implementation("com.zaxxer:HikariCP")
	api(group = "com.querydsl", name = "querydsl-jpa", classifier = "jakarta")

	kapt(group = "com.querydsl", name = "querydsl-apt", classifier = "jakarta") {
		exclude(group = "com.google.guava", module = "guava")
		exclude(group = "io.github.classgraph", module = "classgraph")
	}
	kapt("jakarta.annotation:jakarta.annotation-api")
	kapt("jakarta.persistence:jakarta.persistence-api")

	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
}

sonarqube {
	properties {
		property("sonar.projectKey", project.group.toString() + ":" + project.name)
	}
}
