package top.sharehome.springbootinittemplate.config.i18n.condition;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * 国际化自定义配置条件
 *
 * @author AntonyCheng
 */
public class I18nCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String property = context.getEnvironment().getProperty("spring.messages.enable");
        return StringUtils.equals("true", property);
    }

}