package top.sharehome.springbootinittemplate.config.encrypt.filter;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.annotation.Resource;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import top.sharehome.springbootinittemplate.common.base.R;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.bean.SpringContextHolder;
import top.sharehome.springbootinittemplate.config.encrypt.EncryptConfiguration;
import top.sharehome.springbootinittemplate.config.encrypt.annotation.RSADecrypt;
import top.sharehome.springbootinittemplate.config.encrypt.annotation.RSAEncrypt;
import top.sharehome.springbootinittemplate.config.encrypt.condition.EncryptCondition;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeEncryptException;
import top.sharehome.springbootinittemplate.utils.encrypt.RSAUtils;
import top.sharehome.springbootinittemplate.utils.request.ParamsAndBodyRequestWrapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * RSA加密过滤器
 * 想要使用该过滤器，需要知道该过滤器对加密参数的识别方法：
 * 即先过滤所有RSADecrypt注解的Controller的URI，然后再区分RequestBody和RequestParam注解，
 * 如果是RequestBody，则默认是请求体，遍历其对象中所有字段，将带有RSAEncrypt注解且类型为String的字段值进行解密和更新，
 * 如果是RequestParam，则默认是请求参数，将带有RSAEncrypt注解且类型为String的参数值进行解密和更新。
 *
 * @author AntonyCheng
 */
@Conditional(EncryptCondition.class)
@WebFilter(urlPatterns = "/*", filterName = "RSAEncryptFilter")
public class RSAEncryptFilter implements Filter {

    @Value("${server.servlet.context-path}")
    private String contextPath;

    private final Map<String, HandlerMethod> uriMethodMap = new HashMap<>();

    @Resource
    private EncryptConfiguration encryptConfiguration;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 从 Spring Context 中获取 RequestMappingHandlerMapping
        RequestMappingHandlerMapping requestMappingHandlerMapping = SpringContextHolder.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);

        // 获取所有的 HandlerMethod 映射
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingHandlerMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            HandlerMethod handlerMethod = entry.getValue();

            // 检查方法或类是否带有 @RSADecrypt 注解
            if (Objects.nonNull(AnnotationUtils.findAnnotation(handlerMethod.getMethod(), RSADecrypt.class)) ||
                    Objects.nonNull(AnnotationUtils.findAnnotation(handlerMethod.getBeanType(), RSADecrypt.class))) {

                // 获取 URI pattern 并将其与 HandlerMethod 关联
                Set<String> patterns = entry.getKey().getPatternsCondition().getPatterns();
                for (String pattern : patterns) {
                    uriMethodMap.put(pattern, handlerMethod);
                }
            }
        }
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 转换参数
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // 判断本次请求是否包含在RSADecrypt注解方法URI列表中，如果在，那么根据请求体注解（RequestBody）和请求参数注解（RequestParam）进行区分操作
        HandlerMethod handlerMethod = uriMethodMap.get(request.getRequestURI().replace(contextPath, ""));
        if (Objects.nonNull(handlerMethod)) {
            ParamsAndBodyRequestWrapper paramsAndBodyRequestWrapper = new ParamsAndBodyRequestWrapper(request);
            Parameter[] parameters = handlerMethod.getMethod().getParameters();
            for (Parameter parameter : parameters) {
                Annotation[] annotations = parameter.getAnnotations();
                if (Arrays.stream(annotations).anyMatch(annotation -> annotation instanceof RequestBody)) {
                    // 如果是 RequestBody 注解，则遍历该对象的所有 String 属性
                    Class<?> parameterType = parameter.getType();
                    Field[] declaredFields = parameterType.getDeclaredFields();
                    ObjectMapper objectMapper = new ObjectMapper();
                    Object requestBodyObject = objectMapper.readValue(paramsAndBodyRequestWrapper.getInputStream(), parameterType);
                    for (Field declaredField : declaredFields) {
                        RSAEncrypt annotation = declaredField.getDeclaredAnnotation(RSAEncrypt.class);
                        if (!Objects.equals(declaredField.getType(), String.class) || Objects.isNull(annotation)) {
                            continue;
                        }
                        try {
                            declaredField.setAccessible(true);
                            String value = (String) declaredField.get(requestBodyObject);
                            String decrypt = RSAUtils.decrypt(value, encryptConfiguration.getRsaPrivateKey());
                            declaredField.set(requestBodyObject, decrypt);
                        } catch (CustomizeEncryptException e) {
                            response.setContentType("application/json;charset=utf-8");
                            response.setHeader("Access-Control-Allow-Origin", "*");
                            response.setHeader("Access-Control-Allow-Methods", "*");
                            PrintWriter writer = response.getWriter();
                            writer.write(JSON.toJSONString(R.fail(e.getReturnCode().getCode(), e.getMessage())));
                            writer.flush();
                            return;
                        } catch (IllegalAccessException e) {
                            response.setContentType("application/json;charset=utf-8");
                            response.setHeader("Access-Control-Allow-Origin", "*");
                            response.setHeader("Access-Control-Allow-Methods", "*");
                            PrintWriter writer = response.getWriter();
                            writer.write(JSON.toJSONString(R.fail(ReturnCode.FAIL.getCode(), e.getMessage())));
                            writer.flush();
                            return;
                        } catch (Exception ignore) {
                        }
                    }
                    paramsAndBodyRequestWrapper.updateOrAddRequestBody(requestBodyObject);
                } else if (Arrays.stream(annotations).anyMatch(annotation -> annotation instanceof RequestParam)) {
                    RSAEncrypt annotation = parameter.getDeclaredAnnotation(RSAEncrypt.class);
                    if (!Objects.equals(parameter.getType(), String.class) || Objects.isNull(annotation)) {
                        continue;
                    }
                    String key = parameter.getName();
                    String value = paramsAndBodyRequestWrapper.getParameter(key);
                    try {
                        String decrypt = RSAUtils.decrypt(value, encryptConfiguration.getRsaPrivateKey());
                        paramsAndBodyRequestWrapper.updateOrAddParams(key, decrypt);
                    } catch (CustomizeEncryptException e) {
                        response.setContentType("application/json;charset=utf-8");
                        response.setHeader("Access-Control-Allow-Origin", "*");
                        response.setHeader("Access-Control-Allow-Methods", "*");
                        PrintWriter writer = response.getWriter();
                        writer.write(JSON.toJSONString(R.fail(e.getReturnCode().getCode(), e.getMessage())));
                        writer.flush();
                        return;
                    }
                }
            }
            filterChain.doFilter(paramsAndBodyRequestWrapper, response);
        } else {
            filterChain.doFilter(request, response);
        }
    }

}
