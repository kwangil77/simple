package com.example.simple.server.config

import io.grpc.ServerInterceptor
import io.opentelemetry.api.OpenTelemetry
import io.opentelemetry.context.propagation.TextMapPropagator
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter
import io.opentelemetry.extension.trace.propagation.JaegerPropagator
import io.opentelemetry.instrumentation.grpc.v1_6.GrpcTelemetry
import net.devh.boot.grpc.common.util.InterceptorOrder
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor
import org.springframework.boot.actuate.autoconfigure.tracing.ConditionalOnEnabledTracing
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order

@Configuration
@ConditionalOnEnabledTracing
class OtelConfig {
	@Bean
	fun jaegerPropagator(): TextMapPropagator = JaegerPropagator.getInstance()

	@Bean
	fun otlpExporter(): OtlpGrpcSpanExporter = OtlpGrpcSpanExporter.getDefault()

	@GrpcGlobalServerInterceptor
	@Order(InterceptorOrder.ORDER_TRACING_METRICS + 1)
	fun globalOtelTraceServerInterceptorConfigurer(
		openTelemetry: OpenTelemetry
	): ServerInterceptor {
		return GrpcTelemetry.create(openTelemetry).newServerInterceptor()
	}
}