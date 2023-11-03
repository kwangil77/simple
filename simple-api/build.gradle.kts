import org.springframework.boot.gradle.tasks.bundling.BootBuildImage
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
	base
	java
	id("org.sonarqube")
	id("com.google.cloud.tools.jib") version "3.4.0"
	id("com.epages.restdocs-api-spec") version "0.19.0"
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

dependencies {
	implementation(project(path = ":simple-core", configuration = "runtimeElements"))
	implementation("org.springframework.boot:spring-boot-starter-web") {
		exclude(group = "org.yaml", module = "snakeyaml")
	}
	implementation("org.yaml:snakeyaml:2.2")
	implementation("net.devh:grpc-server-spring-boot-starter:2.14.1.RELEASE") {
		exclude(group = "io.grpc", module = "grpc-netty-shaded")
	}
	implementation("org.springframework.boot:spring-boot-starter-graphql")
	implementation("com.graphql-java:graphql-java-extended-scalars:20.2")

	developmentOnly("org.springframework.boot:spring-boot-devtools")
	implementation("org.hibernate.orm:hibernate-core")
	implementation("org.hibernate.validator:hibernate-validator")

	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:${springDocVersion}") {
		exclude(group = "io.github.classgraph", module = "classgraph")
	}
	implementation("org.springframework:spring-webflux")

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

	testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
	testImplementation("com.epages:restdocs-api-spec-mockmvc:0.19.0")

	implementation("io.grpc:grpc-stub:${grpcVersion}")
	implementation("io.grpc:grpc-protobuf:${grpcVersion}")
	implementation("io.grpc:grpc-netty:${grpcVersion}")
	implementation("io.grpc:grpc-services:${grpcVersion}")
	implementation("com.google.protobuf:protobuf-kotlin:${protobufVersion}")
	implementation("com.google.protobuf:protobuf-java-util:${protobufVersion}")
	implementation("com.salesforce.servicelibs:reactor-grpc-stub:${reactiveGrpcVersion}")
	implementation("javax.annotation:javax.annotation-api:1.3.2")
	implementation("com.google.guava:guava:32.1.2-android")
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

graalvmNative {
	metadataRepository {
		enabled.set(true)
	}
	binaries {
		all {
			resources.autodetect()
		}
		named("main") {
			javaLauncher.set(javaToolchains.launcherFor {
				languageVersion.set(JavaLanguageVersion.of(JavaVersion.VERSION_17.toString()))
				vendor.set(JvmVendorSpec.matching("GraalVM Community"))
			})
			mainClass.set("com.example.simple.api.SimpleApiApplicationKt")
			sharedLibrary.set(false)
			testSupport.set(false)
			buildArgs.add("--initialize-at-build-time=ch.qos.logback,org.apache.commons.logging," +
				"org.hibernate.internal.util.ReflectHelper,io.github.classgraph,nonapi.io.github.classgraph")
			buildArgs.add("--initialize-at-run-time=io.netty")
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

tasks.getByName<BootBuildImage>("bootBuildImage") {
	builder.set("harbor.example.io/docker.io/paketobuildpacks/builder:tiny")
	environment.set(mapOf("BP_NATIVE_IMAGE" to "true"))
	docker {
		if (project.hasProperty("deployUsername")) {
			publishRegistry {
				username.set(project.property("deployUsername").toString())
				password.set(project.property("deployPassword").toString())
			}
		}
		bindHostToBuilder.set(true)
	}
}
