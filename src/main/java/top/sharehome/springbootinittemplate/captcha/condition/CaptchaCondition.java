package top.sharehome.springbootinittemplate.captcha.condition;

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
        String property = context.getEnvironment().getProperty("captcha.enable");
        return StringUtils.equals("true", property);
    }

}