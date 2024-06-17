package top.sharehome.springbootinittemplate.config.captcha.condition;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * 验证码自定义配置条件
 *
 * @author AntonyCheng
 */
public class CaptchaCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String redissonSingleProperty = context.getEnvironment().getProperty("redisson.single-server-config.enable-single");
        String redissonClusterProperty = context.getEnvironment().getProperty("redisson.cluster-servers-config.enable-cluster");
        boolean redissonResult = StringUtils.equals(Boolean.TRUE.toString(), redissonSingleProperty)
                || StringUtils.equals(Boolean.TRUE.toString(), redissonClusterProperty);
        String captchaProperty = context.getEnvironment().getProperty("captcha.enable");
        return StringUtils.equals(Boolean.TRUE.toString(), captchaProperty) && redissonResult;
    }

}