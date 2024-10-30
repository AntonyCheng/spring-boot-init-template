package top.sharehome.springbootinittemplate.config.websocket.netty.condition;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * WebSocket自定义配置条件
 *
 * @author AntonyCheng
 */
public class NettyWsCondition implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String webSocketProperty = context.getEnvironment().getProperty("websocket.netty.enable");
        return StringUtils.equals(Boolean.TRUE.toString(), webSocketProperty);
    }

}