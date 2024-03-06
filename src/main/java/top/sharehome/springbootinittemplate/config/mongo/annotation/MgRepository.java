package top.sharehome.springbootinittemplate.config.mongo.annotation;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Repository;

import java.lang.annotation.*;

/**
 * MongoBD Repository注解
 *
 * @author AntonyCheng
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repository
@ConditionalOnBean(name = {"mongoTemplate"})
public @interface MgRepository {
}
