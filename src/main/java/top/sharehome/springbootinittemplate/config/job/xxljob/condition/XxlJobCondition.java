package top.sharehome.springbootinittemplate.config.job.xxljob.condition;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * XxlJob自定义配置条件
 *
 * @author AntonyCheng
 */
public class XxlJobCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String property = context.getEnvironment().getProperty("xxl.job.enable");
        return StringUtils.equals("true", property);
    }

}