package top.sharehome.springbootinittemplate.config.mongo.condition;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * MongoDB自定义配置条件
 * todo: 借鉴Redis设计自定义配置条件，以便替换MgRepository和MgService中@Condition的参数
 *
 * @author AntonyCheng
 */

public class MongoCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return false;
    }
}
