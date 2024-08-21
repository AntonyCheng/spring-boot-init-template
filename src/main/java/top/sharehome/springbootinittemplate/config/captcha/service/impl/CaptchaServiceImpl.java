package top.sharehome.springbootinittemplate.config.captcha.service.impl;

import cn.hutool.captcha.AbstractCaptcha;
import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.core.util.ReflectUtil;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Service;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.bean.SpringContextHolder;
import top.sharehome.springbootinittemplate.config.captcha.condition.CaptchaCondition;
import top.sharehome.springbootinittemplate.config.captcha.model.CaptchaCreate;
import top.sharehome.springbootinittemplate.config.captcha.properties.CaptchaProperties;
import top.sharehome.springbootinittemplate.config.captcha.properties.enums.CaptchaType;
import top.sharehome.springbootinittemplate.config.captcha.service.CaptchaService;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeReturnException;
import top.sharehome.springbootinittemplate.utils.redisson.KeyPrefixConstants;
import top.sharehome.springbootinittemplate.utils.redisson.cache.CacheUtils;

import java.time.Duration;
import java.util.UUID;

/**
 * 验证码服务实现类
 *
 * @author AntonyCheng
 */
@EnableConfigurationProperties(CaptchaProperties.class)
@Service
@Conditional(CaptchaCondition.class)
public class CaptchaServiceImpl implements CaptchaService {

    @Resource
    private CaptchaProperties captchaProperties;

    @Override
    public CaptchaCreate createCaptcha() {
        CaptchaCreate captchaCreateResponse = new CaptchaCreate();
        boolean enable = captchaProperties.getEnable();
        captchaCreateResponse.setEnableCode(enable);
        if (!enable) {
            return captchaCreateResponse;
        }
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String codeKeyInRedis = KeyPrefixConstants.CAPTCHA_PREFIX + uuid;
        CaptchaType captchaType = captchaProperties.getType();
        boolean isMath = CaptchaType.MATH == captchaType;
        int length = isMath ? ((captchaProperties.getNumberLength() < 1 || captchaProperties.getNumberLength() > 9) ? 1 : captchaProperties.getNumberLength()) :
                ((captchaProperties.getCharLength() < 1 || captchaProperties.getCharLength() > 100) ? 4 : captchaProperties.getCharLength());
        CodeGenerator codeGenerator = ReflectUtil.newInstance(captchaType.getClazz(), length);
        AbstractCaptcha captcha = SpringContextHolder.getBean(captchaProperties.getCategory().getClazz());
        captcha.setGenerator(codeGenerator);
        captcha.createCode();
        String code = captcha.getCode();
        if (isMath) {
            ExpressionParser parser = new SpelExpressionParser();
            Expression exp = parser.parseExpression(StringUtils.remove(code, "="));
            code = exp.getValue(String.class);
        }
        CacheUtils.putNoPrefix(codeKeyInRedis, code, Duration.ofSeconds(captchaProperties.getExpired()));
        captchaCreateResponse
                .setUuid(uuid)
                .setImgBase64(captcha.getImageBase64());
        return captchaCreateResponse;
    }

    @Override
    public void checkCaptcha(String code, String uuid) {
        if (StringUtils.isBlank(code)) {
            throw new CustomizeReturnException(ReturnCode.CAPTCHA_IS_EMPTY);
        }
        if (StringUtils.isBlank(uuid)) {
            throw new CustomizeReturnException(ReturnCode.CAPTCHA_IS_INVALID);
        }
        String codeKeyInRedis = KeyPrefixConstants.CAPTCHA_PREFIX + uuid;
        String codeValue = CacheUtils.getNoPrefix(codeKeyInRedis, String.class);
        if (StringUtils.isBlank(codeValue)) {
            throw new CustomizeReturnException(ReturnCode.CAPTCHA_HAS_EXPIRED);
        }
        CacheUtils.deleteNoPrefix(codeKeyInRedis);
        if (!StringUtils.equals(code, codeValue)) {
            throw new CustomizeReturnException(ReturnCode.CAPTCHA_IS_INCORRECT);
        }
    }

}