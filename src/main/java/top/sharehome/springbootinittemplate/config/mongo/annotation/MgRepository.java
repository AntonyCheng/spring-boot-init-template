package top.sharehome.springbootinittemplate.config.mongo.annotation;

import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Repository;
import top.sharehome.springbootinittemplate.config.mongo.condition.MongoCondition;

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
@Conditional(MongoCondition.class)
public @interface MgRepository {
}
