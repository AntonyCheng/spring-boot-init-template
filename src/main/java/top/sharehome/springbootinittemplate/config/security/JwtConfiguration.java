package top.sharehome.springbootinittemplate.config.security;

import cn.dev33.satoken.jwt.StpLogicJwtForSimple;
import cn.dev33.satoken.stp.StpLogic;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import top.sharehome.springbootinittemplate.config.security.condition.JwtCondition;

/**
 * SaToken是否使用Jwt
 *
 * @author AntonyCheng
 */
@Configuration
@Conditional(JwtCondition.class)
@Slf4j
public class JwtConfiguration {

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
        log.info("############ {} Configuration DI.", this.getClass().getSimpleName().split("\\$\\$")[0]);
    }

}