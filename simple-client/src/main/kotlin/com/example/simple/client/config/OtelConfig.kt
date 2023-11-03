package com.example.simple.client.config

import io.grpc.ClientInterceptor
import io.opentelemetry.api.OpenTelemetry
import io.opentelemetry.context.propagation.TextMapPropagator
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter
import io.opentelemetry.extension.trace.propagation.JaegerPropagator
import io.opentelemetry.instrumentation.grpc.v1_6.GrpcTelemetry
import net.devh.boot.grpc.client.interceptor.GrpcGlobalClientInterceptor
import net.devh.boot.grpc.common.util.InterceptorOrder
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

	@GrpcGlobalClientInterceptor
	@Order(InterceptorOrder.ORDER_TRACING_METRICS + 1)
	fun globalOtelTraceClientInterceptorConfigurer(
		openTelemetry: OpenTelemetry
	): ClientInterceptor {
		return GrpcTelemetry.create(openTelemetry).newClientInterceptor()
	}
}