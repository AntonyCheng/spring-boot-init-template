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
@SpringBootApplication(scanBasePackages = {"top.sharehome.springbootinittemplate.**"})
@MapperScan(basePackages = {"top.sharehome.springbootinittemplate.mapper"})
@EsMapperScan(value = "top.sharehome.springbootinittemplate.elasticsearch.mapper")
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EnableConfigurationProperties
@ConfigurationPropertiesScan(basePackages = {"top.sharehome.springbootinittemplate.config.**"})
@EnableTransactionManagement
public class MainUpgradeApplication {

	public static void main(String[] args) {
		SpringApplication.run(MainUpgradeApplication.class, args);
	}

}
