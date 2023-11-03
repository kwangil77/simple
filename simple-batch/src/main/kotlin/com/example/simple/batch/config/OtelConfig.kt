package com.example.simple.batch.config

import io.opentelemetry.context.propagation.TextMapPropagator
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter
import io.opentelemetry.extension.trace.propagation.JaegerPropagator
import org.springframework.boot.actuate.autoconfigure.tracing.ConditionalOnEnabledTracing
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnEnabledTracing
class OtelConfig {
	@Bean
	fun jaegerPropagator(): TextMapPropagator = JaegerPropagator.getInstance()

	@Bean
	fun otlpExporter(): OtlpGrpcSpanExporter = OtlpGrpcSpanExporter.getDefault()
}