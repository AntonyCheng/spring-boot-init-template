package top.sharehome.springbootinittemplate.config.redisson.condition;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Redisson单机模式自定义配置条件
 *
 * @author AntonyCheng
 */
public class RedissonSingleCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String property = context.getEnvironment().getProperty("redisson.singleServerConfig.enableSingle");
        return StringUtils.equals("true", property);
    }

}