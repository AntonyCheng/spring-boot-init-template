package top.sharehome.springbootinittemplate.config.security.condition;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * SaToken鉴权自定义配置条件
 *
 * @author AntonyCheng
 */
public class AuthorizationCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String property = context.getEnvironment().getProperty("sa-token.enable-authorization");
        return StringUtils.equals("true", property);
    }

}