package com.example.simple.client.config

import net.devh.boot.grpc.client.autoconfigure.GrpcClientAutoConfiguration
import net.devh.boot.grpc.client.autoconfigure.GrpcClientMetricAutoConfiguration
import net.devh.boot.grpc.client.autoconfigure.GrpcClientHealthAutoConfiguration
import net.devh.boot.grpc.client.autoconfigure.GrpcClientSecurityAutoConfiguration
import net.devh.boot.grpc.client.autoconfigure.GrpcClientTraceAutoConfiguration
import net.devh.boot.grpc.client.autoconfigure.GrpcDiscoveryClientAutoConfiguration
import net.devh.boot.grpc.common.autoconfigure.GrpcCommonCodecAutoConfiguration
import net.devh.boot.grpc.common.autoconfigure.GrpcCommonTraceAutoConfiguration
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.context.annotation.Configuration

@Configuration
@ImportAutoConfiguration(
	GrpcClientAutoConfiguration::class,
	GrpcClientHealthAutoConfiguration::class,
	GrpcClientMetricAutoConfiguration::class,
	GrpcClientSecurityAutoConfiguration::class,
	GrpcClientTraceAutoConfiguration::class,
	GrpcDiscoveryClientAutoConfiguration::class,
	GrpcCommonCodecAutoConfiguration::class,
	GrpcCommonTraceAutoConfiguration::class
)
internal class GrpcConfig