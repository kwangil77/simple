package com.example.simple.server.config

import net.devh.boot.grpc.common.autoconfigure.GrpcCommonCodecAutoConfiguration
import net.devh.boot.grpc.common.autoconfigure.GrpcCommonTraceAutoConfiguration
import net.devh.boot.grpc.server.autoconfigure.GrpcAdviceAutoConfiguration
import net.devh.boot.grpc.server.autoconfigure.GrpcHealthServiceAutoConfiguration
import net.devh.boot.grpc.server.autoconfigure.GrpcMetadataConsulConfiguration
import net.devh.boot.grpc.server.autoconfigure.GrpcMetadataEurekaConfiguration
import net.devh.boot.grpc.server.autoconfigure.GrpcMetadataNacosConfiguration
import net.devh.boot.grpc.server.autoconfigure.GrpcMetadataZookeeperConfiguration
import net.devh.boot.grpc.server.autoconfigure.GrpcReflectionServiceAutoConfiguration
import net.devh.boot.grpc.server.autoconfigure.GrpcServerAutoConfiguration
import net.devh.boot.grpc.server.autoconfigure.GrpcServerFactoryAutoConfiguration
import net.devh.boot.grpc.server.autoconfigure.GrpcServerMetricAutoConfiguration
import net.devh.boot.grpc.server.autoconfigure.GrpcServerSecurityAutoConfiguration
import net.devh.boot.grpc.server.autoconfigure.GrpcServerTraceAutoConfiguration
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.context.annotation.Configuration

@Configuration
@ImportAutoConfiguration(
	GrpcCommonCodecAutoConfiguration::class,
	GrpcCommonTraceAutoConfiguration::class,
	GrpcAdviceAutoConfiguration::class,
	GrpcHealthServiceAutoConfiguration::class,
	GrpcMetadataConsulConfiguration::class,
	GrpcMetadataEurekaConfiguration::class,
	GrpcMetadataNacosConfiguration::class,
	GrpcMetadataZookeeperConfiguration::class,
	GrpcReflectionServiceAutoConfiguration::class,
	GrpcServerAutoConfiguration::class,
	GrpcServerFactoryAutoConfiguration::class,
	GrpcServerMetricAutoConfiguration::class,
	GrpcServerSecurityAutoConfiguration::class,
	GrpcServerTraceAutoConfiguration::class
)
internal class GrpcConfig