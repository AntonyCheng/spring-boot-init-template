package top.sharehome.springbootinittemplate.config.redisson.condition;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Redisson集群模式自定义配置条件
 *
 * @author AntonyCheng
 */
public class RedissonClusterCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String property = context.getEnvironment().getProperty("redisson.cluster-servers-config.enable-cluster");
        return StringUtils.equals(Boolean.TRUE.toString(), property);
    }

}