package top.sharehome.springbootinittemplate.config.sensitive.desensitize.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import top.sharehome.springbootinittemplate.config.sensitive.desensitize.enums.DesensitizedType;
import top.sharehome.springbootinittemplate.config.sensitive.desensitize.serialize.DesensitizedSerialize;

import java.lang.annotation.*;

/**
 * 需要脱敏的字段注解
 *
 * @author AntonyCheng
 */
@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonSerialize(using = DesensitizedSerialize.class)
@Documented
public @interface Desensitize {

    DesensitizedType desensitizeType() default DesensitizedType.ALL;

}
