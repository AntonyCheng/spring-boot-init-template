package top.sharehome.springbootinittemplate.config.redis.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Redis自定义配置条件
 *
 * @author AntonyCheng
 */
public class RedisCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        boolean mark = true;
        int index = 0;
        while (mark) {
            String property = context.getEnvironment().getProperty(String.format("spring.autoconfigure.exclude[%d]", index++));
            if ("org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration".equals(property)) {
                return false;
            }
            if (property == null) {
                mark = false;
            }
        }
        return true;
    }
}
