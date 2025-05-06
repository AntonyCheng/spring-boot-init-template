package top.sharehome.springbootinittemplate.config.milvus.condition;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Milvus向量数据库自定义配置条件
 *
 * @author AntonyCheng
 */
public class MilvusCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String encryptProperty = context.getEnvironment().getProperty("milvus.enable");
        return StringUtils.equals(Boolean.TRUE.toString(), encryptProperty);
    }

}