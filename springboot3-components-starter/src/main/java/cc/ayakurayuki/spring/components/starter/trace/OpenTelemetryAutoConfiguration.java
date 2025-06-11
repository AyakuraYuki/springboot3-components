package cc.ayakurayuki.spring.components.starter.trace;

import cc.ayakurayuki.spring.components.opentelemetry.OpenTelemetrySimpleBuilder;
import cc.ayakurayuki.spring.components.trace.CompositeOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.context.propagation.TextMapPropagator;
import io.opentelemetry.extension.trace.propagation.JaegerPropagator;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenTelemetry auto configuration
 * <p>
 * Enabled as default, depended by {@link OpenTelemetry}
 * <p>
 * <ul>
 *   <li>properties: {@code application.opentelemetry.enabled}</li>
 *   <li>value: a single boolean value in {@code true / false}</li>
 * </ul>
 *
 * @author Lynx
 * @author Ayakura Yuki
 */
@Configuration
@ConditionalOnProperty(prefix = "application.opentelemetry", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnClass(OpenTelemetry.class)
public class OpenTelemetryAutoConfiguration {

  @Configuration
  @ConditionalOnClass({
      OpenTelemetrySdk.class,
      OpenTelemetrySimpleBuilder.class
  })
  public static class OpenTelemetrySdkAutoConfiguration {

    /**
     * Enabled as default, depended on missing {@link SdkTracerProvider}
     * <p>
     * <ul>
     *   <li>properties: {@code application.opentelemetry.trace.enabled}</li>
     *   <li>value: a single boolean value in {@code true / false}</li>
     * </ul>
     */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "application.opentelemetry.trace", name = "enabled", havingValue = "true", matchIfMissing = true)
    public SdkTracerProvider defaultSdkTracerProvider() {
      Sampler sampler = Sampler.alwaysOn();
      return SdkTracerProvider.builder().setSampler(sampler).build();
    }

    @Bean
    @ConditionalOnMissingBean
    public OpenTelemetry defaultOpenTelemetry(SdkTracerProvider tracerProvider) {
      TextMapPropagator textMapPropagator = TextMapPropagator.composite(
          // JaegerPropagator as default, provides `uber-trace-id`, `jaeger-baggage` and `uberctx-*` in gRPC Metadata,
          // from `opentelemetry-trace-span-key` and `opentelemetry-baggage-key`
          JaegerPropagator.getInstance()

          // W3CBaggagePropagator.getInstance()      // alternatively, W3CBaggagePropagator provides `baggage` in gRPC Metadata, from `opentelemetry-baggage-key`
          // W3CTraceContextPropagator.getInstance() // W3CTraceContextPropagator provides `traceparent` and `tracestate` in gRPC Metadata, from `opentelemetry-trace-span-key`
      );
      ContextPropagators contextPropagators = ContextPropagators.create(textMapPropagator);
      OpenTelemetrySdk openTelemetrySdk = OpenTelemetrySdk.builder()
          .setTracerProvider(tracerProvider)
          .setPropagators(contextPropagators)
          .build();
      CompositeOpenTelemetry.get().registerIfAbsent(openTelemetrySdk);
      return openTelemetrySdk;
    }

  }

}
