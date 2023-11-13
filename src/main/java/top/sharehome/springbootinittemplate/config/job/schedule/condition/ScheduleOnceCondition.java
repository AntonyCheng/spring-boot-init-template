package top.sharehome.springbootinittemplate.config.job.schedule.condition;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * SpringBoot Schedule注解全量任务自定义配置条件
 *
 * @author AntonyCheng
 */
public class ScheduleOnceCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String property = context.getEnvironment().getProperty("schedule.once.enable");
        return StringUtils.equals("true", property);
    }

}