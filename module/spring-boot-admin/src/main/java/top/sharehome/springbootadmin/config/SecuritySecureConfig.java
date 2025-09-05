package top.sharehome.springbootadmin.config;

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

/**
 * Spring Security配置文件
 *
 * @author AntonyCheng
 */
@Configuration(proxyBeanMethods = false)
public class SecuritySecureConfig {

    private final String adminContextPath;

    public SecuritySecureConfig(AdminServerProperties adminServerProperties) {
        this.adminContextPath = adminServerProperties.getContextPath();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setTargetUrlParameter("redirectTo");

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(adminContextPath + "/assets/**").permitAll()
                        .requestMatchers(adminContextPath + "/login").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage(adminContextPath + "/login")
                        .successHandler(successHandler)
                )
                .logout(logout -> logout.logoutUrl(adminContextPath + "/logout"))
                .httpBasic(httpBasic -> {
                })
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
