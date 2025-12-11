package cc.ayakurayuki.spring.components.starter.config;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

/**
 * Custom Cloneable Converter for Orika Mapper
 *
 * @author Ayakura Yuki
 * @date 2025/12/11-17:46
 */
public class CustomCloneableConverter<T extends Cloneable> extends BidirectionalConverter<T, T> {

    @Override
    public T convertFrom(T source, Type<T> destinationType, MappingContext mappingContext) {
        return convertTo(source, destinationType, mappingContext);
    }

    @Override
    @SuppressWarnings("unchecked")
    public T convertTo(T source, Type<T> type, MappingContext mappingContext) {
        try {
            // create a copy using the clone() method
            return (T) source.getClass().getMethod("clone").invoke(source);
        } catch (Exception e) {
            throw new RuntimeException("failed to clone object of type " + source.getClass().getName(), e);
        }
    }

}
