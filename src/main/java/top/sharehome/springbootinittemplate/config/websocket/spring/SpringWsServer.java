package top.sharehome.springbootinittemplate.config.websocket.spring;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import top.sharehome.springbootinittemplate.config.websocket.spring.condition.SpringWsCondition;
import top.sharehome.springbootinittemplate.config.websocket.spring.handler.SpringWsHandler;
import top.sharehome.springbootinittemplate.config.websocket.spring.properties.SpringWsProperties;

/**
 * Websocket服务器
 *
 * @author AntonyCheng
 */
@Configuration
@EnableConfigurationProperties(SpringWsProperties.class)
@EnableWebSocket
@Conditional(SpringWsCondition.class)
@Slf4j
public class SpringWsServer {

    @Bean
    public WebSocketConfigurer webSocketConfigurer(SpringWsHandler springWsHandler, SpringWsProperties springWsProperties) {
        if (StringUtils.isBlank(springWsProperties.getPath())) {
            springWsProperties.setPath("/websocket");
        }
        if (StringUtils.isBlank(springWsProperties.getAllowedOrigins())) {
            springWsProperties.setAllowedOrigins("*");
        }
        return config -> config.addHandler(springWsHandler, springWsProperties.getPath()).setAllowedOrigins(springWsProperties.getAllowedOrigins());
    }

    /**
     * 依赖注入日志输出
     */
    @PostConstruct
    private void initDi() {
        log.info("############ {} Configuration DI.", this.getClass().getSimpleName().split("\\$\\$")[0]);
    }

}
