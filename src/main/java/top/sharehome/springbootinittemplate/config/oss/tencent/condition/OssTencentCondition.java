package top.sharehome.springbootinittemplate.config.oss.tencent.condition;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * 腾讯云COS自定义配置条件
 *
 * @author AntonyCheng
 */
public class OssTencentCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String property = context.getEnvironment().getProperty("oss.tencent.enable");
        return StringUtils.equals("true", property);
    }

}