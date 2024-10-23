package top.sharehome.springbootinittemplate.config.sensitive.desensitize.serialize;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import lombok.extern.slf4j.Slf4j;
import top.sharehome.springbootinittemplate.config.sensitive.desensitize.annotation.Desensitize;
import top.sharehome.springbootinittemplate.config.sensitive.desensitize.enums.DesensitizedType;

import java.io.IOException;
import java.util.Objects;

/**
 * 数据脱敏序列化器
 *
 * @author AntonyCheng
 */
@Slf4j
public class DesensitizedSerialize extends JsonSerializer<Object> implements ContextualSerializer {

    private DesensitizedType desensitizedType;

    @Override
    public void serialize(Object value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(desensitizedType.getFunction().apply(value));
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider serializerProvider, BeanProperty beanProperty) throws JsonMappingException {
        Desensitize annotation = beanProperty.getAnnotation(Desensitize.class);
        if (Objects.nonNull(annotation)) {
            desensitizedType = annotation.desensitizeType();
            return this;
        }
        return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
    }

}
