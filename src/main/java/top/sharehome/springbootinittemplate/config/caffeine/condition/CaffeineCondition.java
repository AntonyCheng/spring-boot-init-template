package top.sharehome.springbootinittemplate.config.caffeine.condition;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Caffeine本地缓存自定义配置条件
 *
 * @author AntonyCheng
 */
public class CaffeineCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String property = context.getEnvironment().getProperty("caffeine.enabled");
        return StringUtils.equals("true", property);
    }

}