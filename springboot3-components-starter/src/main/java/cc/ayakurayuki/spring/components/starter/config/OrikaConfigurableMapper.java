package cc.ayakurayuki.spring.components.starter.config;

import java.util.Map;
import ma.glasnost.orika.Converter;
import ma.glasnost.orika.Mapper;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import ma.glasnost.orika.impl.DefaultMapperFactory.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/// Orika Mapper configuration.
///
/// Automatically scan and register `ma.glasnost.orika.Converter` and `ma.glasnost.orika.Mapper`.
@Configuration
public class OrikaConfigurableMapper extends ConfigurableMapper {

    private ApplicationContext applicationContext;
    private MapperFactory mapperFactory;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        if (this.mapperFactory != null) {
            // add global converter and mapper after MapperFactory been initialized
            addConverters();
            addMappers();
        }
    }

    /// configure MapperFactory
    @Override
    protected void configure(MapperFactory factory) {
        // register customized converter
        factory.getConverterFactory().registerConverter(new CustomCloneableConverter<>());
        super.configure(factory);
        this.mapperFactory = factory;
    }

    /// configure FactoryBuilder
    @Override
    protected void configureFactoryBuilder(Builder factoryBuilder) {
        super.configureFactoryBuilder(factoryBuilder);
        // can disable builtin converters
        factoryBuilder.useBuiltinConverters(true);
    }

    /// expose MapperFactory as Spring Bean
    @Bean
    public MapperFactory getMapperFactory() {
        return mapperFactory;
    }

    /// load and register all `Converter` from Spring ApplicationContext
    @SuppressWarnings("rawtypes")
    private void addConverters() {
        final Map<String, Converter> converters = this.applicationContext.getBeansOfType(Converter.class);
        for (final Converter<?, ?> converter : converters.values()) {
            addConverter(converter);
        }
    }

    /// register a `Converter` to MapperFactory manually
    private void addConverter(final Converter<?, ?> converter) {
        this.mapperFactory.getConverterFactory().registerConverter(converter);
    }

    /// load and register all `Mapper` from Spring ApplicationContext
    @SuppressWarnings("rawtypes")
    private void addMappers() {
        final Map<String, Mapper> mappers = this.applicationContext.getBeansOfType(Mapper.class);
        for (final Mapper<?, ?> mapper : mappers.values()) {
            addMapper(mapper);
        }
    }

    /// register a `Mapper` to MapperFactory manually
    private void addMapper(final Mapper<?, ?> mapper) {
        this.mapperFactory.registerMapper(mapper);
    }

}
