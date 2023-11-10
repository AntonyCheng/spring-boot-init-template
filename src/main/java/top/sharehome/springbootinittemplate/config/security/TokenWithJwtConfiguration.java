package top.sharehome.springbootinittemplate.config.security;

import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpLogic;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * SaToken是否使用Jwt
 *
 * @author AntonyCheng
 */
@Configuration
@ConditionalOnProperty(prefix = "sa-token", name = "isJwt", havingValue = "true")
@Slf4j
public class TokenWithJwtConfiguration {

    /**
     * Sa-Token 整合 jwt (该模板使用 Simple 简单模式，一共有三种模式，详情见：https://sa-token.cc/doc.html#/plugin/jwt-extend)
     */
    @Bean
    public StpLogic getStpLogicJwt() {
        return new StpLogicJwtForSimple();
    }

    /**
     * 依赖注入日志输出
     */
    @PostConstruct
    private void initDi() {
        log.info("############ jwt config DI.");
    }

}