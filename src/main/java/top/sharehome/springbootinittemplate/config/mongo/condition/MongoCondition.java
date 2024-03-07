package top.sharehome.springbootinittemplate.config.mongo.condition;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * MongoDB自定义配置条件
 *
 * @author AntonyCheng
 */

public class MongoCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        boolean mark = true;
        int index = 0;
        while (mark) {
            String property = context.getEnvironment().getProperty(String.format("spring.autoconfigure.exclude[%d]", index++));
            if ("org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration".equals(property)) {
                return false;
            }
            if (StringUtils.isEmpty(property)) {
                mark = false;
            }
        }
        return true;
    }
}
