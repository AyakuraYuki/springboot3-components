package cc.ayakurayuki.spring.components.starter.http.server;

import cc.ayakurayuki.spring.components.errors.ServerCode;
import cc.ayakurayuki.spring.components.http.server.HttpServerIndicator;
import cc.ayakurayuki.spring.components.http.server.interceptor.HttpContextInterceptor;
import com.alibaba.fastjson2.JSONWriter.Feature;
import com.alibaba.fastjson2.support.spring6.http.converter.FastJsonHttpMessageConverter;
import io.opentelemetry.api.trace.StatusCode;
import io.prometheus.client.hotspot.DefaultExports;
import io.prometheus.client.servlet.jakarta.exporter.MetricsServlet;
import io.undertow.Undertow;
import jakarta.servlet.Servlet;
import java.util.Collections;
import org.apache.catalina.startup.Tomcat;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.handler.MappedInterceptor;

@Configuration
@ConditionalOnClass(HttpServerIndicator.class)
@EnableConfigurationProperties({
    TomcatHttpServerProperties.class,
    UndertowHttpServerProperties.class
})
@EnableAspectJAutoProxy
public class HttpServerAutoConfiguration {

  /**
   * Tomcat customizer
   */
  @Configuration
  @ConditionalOnClass({Tomcat.class, TomcatWebServerCustomizer.class})
  @ConditionalOnMissingBean(TomcatWebServerCustomizer.class)
  static class TomcatWebServerCustomizerConfiguration {

    @Bean
    TomcatWebServerCustomizer tomcatWebServerCustomizer(TomcatHttpServerProperties tomcatHttpServerProperties) {
      return new TomcatWebServerCustomizer(tomcatHttpServerProperties);
    }

  }

  /**
   * Undertow customizer
   */
  @Configuration
  @ConditionalOnClass({Undertow.class, UndertowWebServerCustomizer.class})
  @ConditionalOnMissingBean(UndertowWebServerCustomizer.class)
  static class UndertowWebServerCustomizerConfiguration {

    @Bean
    UndertowWebServerCustomizer undertowWebServerCustomizer(UndertowHttpServerProperties undertowHttpServerProperties) {
      return new UndertowWebServerCustomizer(undertowHttpServerProperties);
    }

  }

  /**
   * Prometheus exports
   */
  @Configuration
  @ConditionalOnClass(DefaultExports.class)
  static class PrometheusHotspotExportConfiguration implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
      DefaultExports.initialize();
    }

  }

  /**
   * Metrics servlet
   */
  @Configuration
  @ConditionalOnClass({Servlet.class, MetricsServlet.class})
  static class PrometheusHttpMetricsServletConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "metricsServletRegistration")
    ServletRegistrationBean<MetricsServlet> metricsServletRegistration() {
      ServletRegistrationBean<MetricsServlet> registration = new ServletRegistrationBean<>();
      registration.setLoadOnStartup(1);
      registration.setAsyncSupported(true);
      registration.setOrder(1);
      return registration;
    }

  }

  @Configuration
  @ConditionalOnClass(Servlet.class)
  static class MonitorPingServletConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "monitorPingServletRegistration")
    ServletRegistrationBean<MonitorPingServlet> monitorPingServletRegistration() {
      ServletRegistrationBean<MonitorPingServlet> registration = new ServletRegistrationBean<>();
      registration.setLoadOnStartup(1);
      registration.setAsyncSupported(true);
      registration.setOrder(2);
      return registration;
    }

  }

  @Configuration
  @ConditionalOnClass({Aspect.class, ProceedingJoinPoint.class})
  static class RateLimitAspectConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "rateLimitAspect")
    RateLimitAspect rateLimitAspect() {
      return new RateLimitAspect();
    }

  }

  @Configuration
  @ConditionalOnClass(HttpContextInterceptor.class)
  static class HttpContextInterceptorConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "httpContextInterceptor")
    MappedInterceptor httpContextInterceptor() {
      HttpContextInterceptor interceptor = new HttpContextInterceptor();
      return new MappedInterceptor(new String[]{"/**"}, interceptor);
    }

  }

  @Configuration
  @ConditionalOnClass(ResponseBody.class)
  @ConditionalOnProperty(prefix = "application.http.server.advice", name = "enabled", havingValue = "true", matchIfMissing = true)
  static class HttpServerResponseBodyAdviceConfiguration {

    @Bean
    @ConditionalOnMissingBean(HttpServerResponseBodyAdvice.class)
    HttpServerResponseBodyAdvice httpServerResponseBodyAdvice() {
      return new HttpServerResponseBodyAdvice();
    }

  }

  @Configuration
  @ConditionalOnClass({RestController.class, StatusCode.class})
  @ConditionalOnProperty(prefix = "application.http.server.advice", name = "enabled", havingValue = "true", matchIfMissing = true)
  static class HttpServerRestRpcAdviceConfiguration {

    @Bean
    @ConditionalOnMissingBean(HttpServerRestRpcAdvice.class)
    HttpServerRestRpcAdvice httpServerRestRpcAdvice() {
      return new HttpServerRestRpcAdvice();
    }

  }

  @Configuration
  @ConditionalOnClass({RestController.class, ServerCode.class})
  @ConditionalOnProperty(prefix = "application.http.server.advice", name = "enabled", havingValue = "true", matchIfMissing = true)
  static class HttpServerWebDataBinderAdviceConfiguration {

    @Bean
    @ConditionalOnMissingBean(HttpServerWebDataBinderAdvice.class)
    HttpServerWebDataBinderAdvice httpServerWebDataBinderAdvice() {
      return new HttpServerWebDataBinderAdvice();
    }

  }

  /**
   * FastJson2 HTTP message converter
   */
  @Configuration
  @ConditionalOnClass(FastJsonHttpMessageConverter.class)
  @ConditionalOnMissingBean(FastJsonHttpMessageConverter.class)
  @ConditionalOnProperty(prefix = "application.http.server.fastjson", name = "enabled", havingValue = "true")
  static class FastJsonHttpMessageConverterConfiguration {

    @Bean
    FastJsonHttpMessageConverter fastJsonHttpMessageConverter() {
      FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
      converter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));
      // non-string key serializes to string, for example, number 123 converts "123"
      converter.getFastJsonConfig().setWriterFeatures(Feature.WriteNonStringKeyAsString);
      return converter;
    }

  }

}
