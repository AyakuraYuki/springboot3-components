package cc.ayakurayuki.spring.components.opentelemetry;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.samplers.Sampler;

public class OpenTelemetrySimpleBuilder {

  public SdkTracerProvider defaultSdkTracerProvider() {
    Sampler sampler = Sampler.alwaysOff();
    return SdkTracerProvider.builder()
        .setSampler(sampler)
        .build();
  }

  public OpenTelemetry defaultOpenTelemetry(SdkTracerProvider tracerProvider) {
    return OpenTelemetrySdk.builder()
        .setTracerProvider(tracerProvider)
        .build();
  }

}
