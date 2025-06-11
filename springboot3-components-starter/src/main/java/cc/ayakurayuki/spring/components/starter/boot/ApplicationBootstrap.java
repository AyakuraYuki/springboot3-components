package cc.ayakurayuki.spring.components.starter.boot;

import cc.ayakurayuki.spring.components.boot.BootstrapSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.ClassUtils;

@Slf4j
class ApplicationBootstrap {

  private static final String   DEFAULT_RESOURCE_PATTERN = "**/*.class";
  private static final String[] DEFAULT_PACKAGES         = new String[]{"cc.ayakurayuki.spring.components"};

  private Environment             environment;
  private ResourcePatternResolver resourcePatternResolver;
  private MetadataReaderFactory   metadataReaderFactory;

  void boot() {
    final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    final AtomicInteger counter = new AtomicInteger();
    final boolean debugEnabled = log.isDebugEnabled();
    List<BootstrapSource> loaders = loaders();
    // packages to scan
    Set<String> packages = loaders.stream().flatMap(bs -> Stream.of(bs.packages())).collect(Collectors.toSet());
    // exclude packages
    List<TypeFilter> excludeFilters = loaders.stream().map(bs -> new PackageExcludeFilter(bs.exclude())).collect(Collectors.toList());
    // do scanning
    packages.stream()
        .flatMap(p -> scan(p, excludeFilters).stream())
        .forEach(className -> {
          if (debugEnabled) {
            log.debug("  - bootstrap load class: %s".formatted(className));
          }
          try {
            Class.forName(className, true, classLoader);
            counter.incrementAndGet();
          } catch (Throwable ignored) {
          }
        });
    // display
    int count = counter.get();
    String suffix = count <= 1 ? "class" : "classes";
    log.info("Boot Application load %d %s".formatted(count, suffix));
  }

  private List<BootstrapSource> loaders() {
    ServiceLoader<BootstrapSource> serviceLoader = ServiceLoader.load(BootstrapSource.class);
    List<BootstrapSource> sources = new ArrayList<>();
    serviceLoader.forEach(sources::add);
    return sources;
  }

  private Set<String> scan(String basePackage, List<TypeFilter> excludeFilter) {
    final boolean debugEnabled = log.isDebugEnabled();
    String searchPath = "%s%s/%s".formatted(
        ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX,
        resolveBasePackage(basePackage),
        DEFAULT_RESOURCE_PATTERN
    );

    Set<String> set = new HashSet<>();
    try {

      Resource[] resources = getResourcePatternResolver().getResources(searchPath);

      for (Resource resource : resources) {
        if (resource.isReadable()) {

          try {
            MetadataReader metadataReader = getMetadataReaderFactory().getMetadataReader(resource);
            if (isCandidateComponent(metadataReader, excludeFilter)) {
              set.add(metadataReader.getAnnotationMetadata().getClassName());
            }
          } catch (Throwable ex) {
            log.warn("failed to read candidate component class: %s".formatted(resource), ex);
          }

        } else {

          if (debugEnabled) {
            log.debug("ignore resource because of unreadable: %s".formatted(resource));
          }

        }
      }

    } catch (IOException e) {

      if (debugEnabled) {
        log.debug("IOException occurred", e);
      }

    }

    return set;
  }

  private String resolveBasePackage(String basePackage) {
    return ClassUtils.convertClassNameToResourcePath(getEnvironment().resolveRequiredPlaceholders(basePackage));
  }

  private Environment getEnvironment() {
    if (this.environment == null) {
      this.environment = new StandardEnvironment();
    }
    return this.environment;
  }

  private ResourcePatternResolver getResourcePatternResolver() {
    if (this.resourcePatternResolver == null) {
      this.resourcePatternResolver = new PathMatchingResourcePatternResolver();
    }
    return this.resourcePatternResolver;
  }

  protected boolean isCandidateComponent(MetadataReader metadataReader, List<TypeFilter> excludeFilters) throws IOException {
    for (TypeFilter filter : excludeFilters) {
      if (filter.match(metadataReader, getMetadataReaderFactory())) {
        return false;
      }
    }
    return true;
  }

  private MetadataReaderFactory getMetadataReaderFactory() {
    if (this.metadataReaderFactory == null) {
      this.metadataReaderFactory = new CachingMetadataReaderFactory();
    }
    return this.metadataReaderFactory;
  }

}
