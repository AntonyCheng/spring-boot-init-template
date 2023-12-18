package top.sharehome.springbootinittemplate;

import org.dromara.easyes.starter.register.EsMapperScan;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 启动类
 *
 * @author AntonyCheng
 */
@SpringBootApplication
@MapperScan("top.sharehome.springbootinittemplate.mapper")
@EsMapperScan("top.sharehome.springbootinittemplate.elasticsearch.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EnableConfigurationProperties
@ConfigurationPropertiesScan("top.sharehome.springbootinittemplate.config.**")
@EnableTransactionManagement
public class MainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

}