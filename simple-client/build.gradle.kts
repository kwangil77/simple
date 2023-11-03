import io.github.kobylynskyi.graphql.codegen.gradle.GraphQLCodegenGradleTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
	base
	java
	`java-library`
	id("org.sonarqube")
	id("com.google.cloud.tools.jib") version "3.4.0"
	id("com.epages.restdocs-api-spec") version "0.19.0"
	id("net.croz.apicurio-registry-gradle-plugin") version "1.1.0"
	id("com.google.protobuf") version "0.9.4"
	id("org.openapi.generator") version "7.0.1"
	id("io.github.kobylynskyi.graphql.codegen") version "5.8.0"
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
	implementation("org.yaml:snakeyaml:2.2")
	implementation("org.springframework.boot:spring-boot-starter-actuator")

	implementation("org.springframework.boot:spring-boot-starter-webflux") {
		exclude(group = "org.yaml", module = "snakeyaml")
	}
	implementation("net.devh:grpc-client-spring-boot-starter:2.14.1.RELEASE") {
		exclude(group = "io.grpc", module = "grpc-netty-shaded")
	}
	implementation("org.hibernate.validator:hibernate-validator")
	implementation("org.glassfish:jakarta.el:4.0.2")

	implementation("io.github.kobylynskyi:graphql-java-codegen:5.8.0")

	developmentOnly("org.springframework.boot:spring-boot-devtools")

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
	implementation("com.google.protobuf:protobuf-kotlin:${protobufVersion}")
	implementation("com.salesforce.servicelibs:reactor-grpc-stub:${reactiveGrpcVersion}")
	implementation("javax.annotation:javax.annotation-api:1.3.2")
	implementation("com.google.guava:guava:32.1.2-android")

	implementation("org.openapitools:jackson-databind-nullable:0.2.6")
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

springBoot {
	buildInfo()
}

sourceSets.main {
	java.srcDir(listOf(
		"${layout.buildDirectory}/generated/source/openapi/src/main/java",
		"${layout.buildDirectory}/generated/source/graphql"
	))
	proto.srcDir("${layout.buildDirectory}/resources/schemas")
}

schemaRegistry {
	config {
		url("https://apicurio-registry.example.io")
	}
	download {
		artifact {
			groupId = "simple"
			id = "simple-grpc"
			outputPath = "${layout.buildDirectory}/resources/schemas"
			outputFileName = "simple-grpc"
		}
		artifact {
			groupId = "simple"
			id = "simple-rest"
			outputPath = "${layout.buildDirectory}/resources/schemas"
			outputFileName = "simple-rest"
		}
		artifact {
			groupId = "simple"
			id = "simple-gql"
			outputPath = "${layout.buildDirectory}/resources/schemas"
			outputFileName = "simple-gql"
		}
	}
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

openApiGenerate {
	generatorName.set("java")
	library.set("webclient")
	inputSpec.set("${layout.buildDirectory}/resources/schemas/simple-rest.json")
	outputDir.set("${layout.buildDirectory}/generated/source/openapi")
	apiPackage.set("com.example.simple.rest.api")
	modelPackage.set("com.example.simple.rest.model")
	globalProperties.set(mapOf(
		"models" to "",
		"modelTests" to "false",
		"modelDocs" to "false",
		"apis" to "",
		"apiTests" to "false",
		"apiDocs" to "false",
		"supportingFiles" to listOf(
	    "ApiKeyAuth.java",
			"ApiClient.java",
			"Authentication.java",
			"HttpBasicAuth.java",
			"HttpBearerAuth.java",
			"JavaTimeFormatter.java",
			"RFC3339DateFormat.java"
		).joinToString(",")
	))
	configOptions.set(mapOf(
		"dateLibrary" to "java8",
		"useTags" to "true"
	))
}

tasks.named<GraphQLCodegenGradleTask>("graphqlCodegen") {
	graphqlSchemaPaths = listOf("${layout.buildDirectory}/resources/schemas/simple-gql.graphql")
	outputDir = file("${layout.buildDirectory}/generated/source/graphql")
	packageName = "com.example.simple.graphql"
	customTypesMapping = mutableMapOf(Pair("DateTime", "java.time.OffsetDateTime"))
	generateClient = true
}

tasks.withType<KotlinCompile> {
	dependsOn("openApiGenerate", "graphqlCodegen")
}

jib {
	from {
		image = "gcr.io/distroless/java:17"
	}
	container {
		ports = listOf("8080")
	}
}
