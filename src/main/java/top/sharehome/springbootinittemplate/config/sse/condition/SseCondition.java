package top.sharehome.springbootinittemplate.config.sse.condition;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * SSE自定义配置条件
 *
 * @author AntonyCheng
 */
public class SseCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String property = context.getEnvironment().getProperty("sse.enable");
        return StringUtils.equals(Boolean.TRUE.toString(), property);
    }

}
