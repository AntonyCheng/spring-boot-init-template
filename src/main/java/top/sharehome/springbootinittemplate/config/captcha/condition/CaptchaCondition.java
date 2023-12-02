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
        String redissonSingleProperty = context.getEnvironment().getProperty("redisson.singleServerConfig.enableSingle");
        String redissonClusterProperty = context.getEnvironment().getProperty("redisson.clusterServersConfig.enableCluster");
        boolean redissonResult = StringUtils.equals("true", redissonSingleProperty) || StringUtils.equals("true", redissonClusterProperty);
        String captchaProperty = context.getEnvironment().getProperty("captcha.enable");
        return StringUtils.equals("true", captchaProperty) && redissonResult;
    }

}