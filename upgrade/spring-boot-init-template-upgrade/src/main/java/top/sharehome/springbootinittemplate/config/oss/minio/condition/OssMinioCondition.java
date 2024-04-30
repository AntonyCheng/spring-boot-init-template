package top.sharehome.springbootinittemplate.config.oss.minio.condition;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * MinIO自定义配置条件
 *
 * @author AntonyCheng
 */
public class OssMinioCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String property = context.getEnvironment().getProperty("oss.minio.enable");
        return StringUtils.equals(Boolean.TRUE.toString(), property);
    }

}