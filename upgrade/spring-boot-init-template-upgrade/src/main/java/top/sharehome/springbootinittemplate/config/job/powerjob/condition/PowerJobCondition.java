package top.sharehome.springbootinittemplate.config.job.powerjob.condition;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * PowerJob自定义配置条件
 *
 * @author AntonyCheng
 */
public class PowerJobCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String property = context.getEnvironment().getProperty("powerjob.worker.enabled");
        return StringUtils.equals(Boolean.TRUE.toString(), property);
    }

}