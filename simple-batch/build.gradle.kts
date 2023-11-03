import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
	base
	java
	id("org.sonarqube")
	id("com.google.cloud.tools.jib") version "3.4.0"
}

val springCloudTaskVersion: String by project

tasks.getByName<BootJar>("bootJar") {
	archiveVersion.set("")
}

dependencies {
	implementation(project(path = ":simple-core", configuration = "runtimeElements"))
	implementation("org.springframework.boot:spring-boot-starter-batch") {
		exclude(group = "org.codehaus.jettison", module = "jettison")
	}
	implementation("org.codehaus.jettison:jettison:1.5.4")
	implementation("org.springframework.boot:spring-boot-starter") {
		exclude(group = "org.yaml", module = "snakeyaml")
	}
	implementation("org.yaml:snakeyaml:2.2")
	implementation("org.hibernate.orm:hibernate-core")
	implementation("org.hibernate.validator:hibernate-validator")

	implementation("org.springframework.cloud:spring-cloud-starter-task")
	implementation("org.hsqldb:hsqldb")

	implementation("org.postgresql:postgresql")
	implementation("io.micrometer.prometheus:prometheus-rsocket-spring:1.5.2")
	implementation("io.micrometer.prometheus:prometheus-rsocket-client:1.5.2")
	implementation("org.springframework:spring-webflux")

	implementation("org.glassfish:jakarta.el:4.0.2")
	implementation("org.xerial.snappy:snappy-java:1.1.10.5")

	implementation("com.fasterxml.jackson.module:jackson-module-parameter-names")

	implementation("io.micrometer:micrometer-registry-prometheus")
	implementation("io.prometheus:simpleclient_pushgateway")

	implementation("io.micrometer:micrometer-tracing-bridge-otel") {
		exclude(group = "io.opentelemetry", module = "opentelemetry-semconv")
	}
	implementation("io.opentelemetry.instrumentation:" +
			"opentelemetry-instrumentation-api-semconv:${rootProject.properties["opentelemetry.version"]}-alpha")
	implementation("io.opentelemetry.instrumentation:" +
			"opentelemetry-grpc-1.6:${rootProject.properties["opentelemetry.version"]}-alpha")
	implementation("io.opentelemetry:opentelemetry-exporter-otlp")
	implementation("com.squareup.okio:okio:3.5.0")

	implementation("io.netty:netty-resolver-dns-native-macos:${rootProject.properties["netty.version"]}:osx-aarch_64")

	testImplementation("org.springframework.batch:spring-batch-test")
}

sonarqube {
	properties {
		property("sonar.projectKey", project.group.toString() + ":" + project.name)
	}
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-task-dependencies:${springCloudTaskVersion}")
	}
}

base {
	distsDirectory.dir(".")
}

jib {
	from {
		image = "gcr.io/distroless/java:17"
	}
	container {
		ports = listOf("8080")
	}
}
