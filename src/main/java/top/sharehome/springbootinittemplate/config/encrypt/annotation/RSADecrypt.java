package top.sharehome.springbootinittemplate.config.encrypt.annotation;

import java.lang.annotation.*;

/**
 * 需要RSA算法解密的方法注解
 *
 * @author AntonyCheng
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RSADecrypt {

    String[] params() default {};

}