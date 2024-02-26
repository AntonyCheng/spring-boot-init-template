package top.sharehome.springbootinittemplate.config.oss.ali.condition;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * 阿里云OSS自定义配置条件
 *
 * @author AntonyCheng
 */
public class OssAliCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String property = context.getEnvironment().getProperty("oss.ali.enable");
        return StringUtils.equals(Boolean.TRUE.toString(), property);
    }

}