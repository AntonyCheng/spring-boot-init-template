package top.sharehome.springbootinittemplate.config.encrypt.annotation;

import java.lang.annotation.*;

/**
 * 需要RSA算法加密的参数或字段注解
 *
 * @author AntonyCheng
 */
@Inherited
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RSAEncrypt {
}
