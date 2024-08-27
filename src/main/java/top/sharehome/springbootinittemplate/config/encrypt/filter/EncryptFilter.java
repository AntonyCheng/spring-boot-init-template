package top.sharehome.springbootinittemplate.config.encrypt.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import top.sharehome.springbootinittemplate.config.bean.SpringContextHolder;
import top.sharehome.springbootinittemplate.config.encrypt.EncryptConfiguration;
import top.sharehome.springbootinittemplate.config.encrypt.annotation.RSADecrypt;
import top.sharehome.springbootinittemplate.config.encrypt.condition.EncryptCondition;
import top.sharehome.springbootinittemplate.model.dto.auth.AuthLoginDto;
import top.sharehome.springbootinittemplate.utils.encrypt.RSAUtils;
import top.sharehome.springbootinittemplate.utils.request.ParamsAndBodyRequestWrapper;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;

/**
 * 加密过滤器
 *
 * @author AntonyCheng
 */
@Conditional(EncryptCondition.class)
@WebFilter(urlPatterns = "/*", filterName = "EncryptFilter")
public class EncryptFilter implements Filter {

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 转换参数
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // 初始化URI和方法对象映射
        Map<String, HandlerMethod> uriMethodMap = new HashMap<>();
        List<String> uriPatterns = new ArrayList<>();
        // 从请求映射对象中获取到带有RSADecrypt注解的方法URI
        RequestMappingHandlerMapping requestMappingHandlerMapping = SpringContextHolder.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();
            if (AnnotationUtils.findAnnotation(handlerMethod.getMethod(), RSADecrypt.class) != null ||
                    AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), RSADecrypt.class) != null) {
                Set<String> patterns = entry.getKey().getPatternsCondition().getPatterns();
                uriPatterns.addAll(patterns);
                if (patterns.iterator().hasNext()) {
                    uriMethodMap.put(patterns.iterator().next(), handlerMethod);
                }
            }
        }
        // 判断本次请求是否包含在RSADecrypt注解方法URI列表中，如果在，那么根据请求体注解（RequestBody）和请求参数注解（RequestParam）进行区分操作
        String requestURI = request.getRequestURI().replace(contextPath, "");
        if (uriPatterns.contains(requestURI)) {
            ParamsAndBodyRequestWrapper paramsAndBodyRequestWrapper = new ParamsAndBodyRequestWrapper(request);
            HandlerMethod handlerMethod = uriMethodMap.get(requestURI);
            MethodParameter[] methodParameters = handlerMethod.getMethodParameters();
            for (MethodParameter methodParameter : methodParameters) {
                Annotation[] annotations = methodParameter.getParameterAnnotations();
                if (Arrays.stream(annotations).anyMatch(annotation -> annotation instanceof RequestBody)) {
                    System.out.println(annotations);
                } else if (Arrays.stream(annotations).anyMatch(annotation -> annotation instanceof RequestParam)) {
                    String paramName = methodParameter.getParameterName();
                    String encryptValue = paramsAndBodyRequestWrapper.getParameter(paramName);
                    String decryptValue = RSAUtils.decrypt(encryptValue, EncryptConfiguration.RSAPrivateKey);
                    System.out.println(annotations);
                } else {
                    continue;
                }
            }
            paramsAndBodyRequestWrapper.updateOrAddParams("test", "12345678");
            AuthLoginDto user = new AuthLoginDto()
                    .setAccount("user")
                    .setPassword("123456");
            paramsAndBodyRequestWrapper.updateOrAddRequestBody(user);
            filterChain.doFilter(paramsAndBodyRequestWrapper, response);
        } else {
            filterChain.doFilter(request, response);
        }
    }

}
