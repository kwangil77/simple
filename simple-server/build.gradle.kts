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
	base
	java
	`java-library`
	id("org.sonarqube")
	id("com.google.cloud.tools.jib") version "3.4.0"
	id("com.epages.restdocs-api-spec") version "0.19.0"
	id("org.flywaydb.flyway") version "9.22.3"
	id("com.google.protobuf") version "0.9.4"
}

val springDocVersion: String by project
val protobufVersion: String by project
val grpcVersion: String by project
val reactiveGrpcVersion: String by project

tasks.getByName<BootJar>("bootJar") {
	archiveVersion.set("")
}

openapi3 {
	setServer("http://localhost:8080")
	title = "OpenAPI definition"
	description = "OpenAPI definition"
	version = "v0"
	format = "yaml"
}

flyway {
	url = "jdbc:mariadb://127.0.0.1:3306/simple?allowPublicKeyRetrieval=true&useSSL=false"
	user = "root"
	password = ""
}

dependencies {
	api("org.springframework.boot:spring-boot-starter-data-r2dbc") {
		exclude(group = "org.yaml", module = "snakeyaml")
	}
	implementation("org.yaml:snakeyaml:2.2")
	implementation("org.springframework.boot:spring-boot-starter-actuator")

	implementation("io.r2dbc:r2dbc-proxy")
	implementation("io.asyncer:r2dbc-mysql:1.0.5")

	implementation("org.springframework.boot:spring-boot-starter-webflux") {
		exclude(group = "org.yaml", module = "snakeyaml")
	}
	implementation("net.devh:grpc-server-spring-boot-starter:2.14.1.RELEASE") {
		exclude(group = "io.grpc", module = "grpc-netty-shaded")
	}
	implementation("org.hibernate.validator:hibernate-validator")
	implementation("org.glassfish:jakarta.el:4.0.2")

	implementation("org.springframework.boot:spring-boot-starter-graphql")
	implementation("com.graphql-java:graphql-java-extended-scalars:20.2")

	developmentOnly("org.springframework.boot:spring-boot-devtools")
	developmentOnly("org.mariadb.jdbc:mariadb-java-client")

	implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:${springDocVersion}") {
		exclude(group = "io.github.classgraph", module = "classgraph")
	}
	implementation("io.micrometer:micrometer-registry-prometheus")

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

	testImplementation("org.springframework.restdocs:spring-restdocs-webtestclient")
	testImplementation("com.epages:restdocs-api-spec:0.19.0")
	testImplementation("com.epages:restdocs-api-spec-webtestclient:0.19.0")

	implementation("io.grpc:grpc-stub:${grpcVersion}")
	implementation("io.grpc:grpc-protobuf:${grpcVersion}")
	implementation("io.grpc:grpc-netty:${grpcVersion}")
	implementation("io.grpc:grpc-services:${grpcVersion}")
	implementation("com.google.protobuf:protobuf-kotlin:${protobufVersion}")
	implementation("com.google.protobuf:protobuf-java-util:${protobufVersion}")
	implementation("com.salesforce.servicelibs:reactor-grpc-stub:${reactiveGrpcVersion}")
	implementation("javax.annotation:javax.annotation-api:1.3.2")
	implementation("com.google.guava:guava:32.1.2-android")

	implementation("com.google.errorprone:error_prone_annotations:2.21.1")
	implementation("org.checkerframework:checker-qual:3.37.0")
}

sonarqube {
	properties {
		property("sonar.projectKey", project.group.toString() + ":" + project.name)
	}
}

base {
	distsDirectory.dir(".")
}

protobuf {
	protoc {
		artifact = "com.google.protobuf:protoc:${protobufVersion}"
	}
	plugins {
		create("grpc") {
			artifact = "io.grpc:protoc-gen-grpc-java:${grpcVersion}"
		}
		create("reactor") {
			artifact = "com.salesforce.servicelibs:reactor-grpc:${reactiveGrpcVersion}"
		}
	}
	generateProtoTasks {
		all().forEach {
			it.plugins {
				create("grpc")
				create("reactor")
			}
		}
	}
}

jib {
	from {
		image = "gcr.io/distroless/java:17"
	}
	container {
		ports = listOf("8080")
	}
}
