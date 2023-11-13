package top.sharehome.springbootinittemplate.config.rabbitmq.condition;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * RabbitMQ自定义配置条件
 *
 * @author AntonyCheng
 */
public class RabbitMqCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String property = context.getEnvironment().getProperty("spring.rabbitmq.enable");
        return StringUtils.equals("true", property);
    }

}