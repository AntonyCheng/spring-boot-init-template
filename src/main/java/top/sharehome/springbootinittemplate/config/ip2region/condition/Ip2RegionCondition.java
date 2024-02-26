package top.sharehome.springbootinittemplate.config.ip2region.condition;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * 离线IP库自定义配置条件
 *
 * @author AntonyCheng
 */
public class Ip2RegionCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String property = context.getEnvironment().getProperty("ip2region.enable");
        return StringUtils.equals(Boolean.TRUE.toString(), property);
    }
}
