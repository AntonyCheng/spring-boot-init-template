package top.sharehome.springbootinittemplate.config.mongo.annotation;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * MongoBD Service注解
 *
 * @author AntonyCheng
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Service
@ConditionalOnBean(name = {"mongoTemplate"})
public @interface MgService {
}
