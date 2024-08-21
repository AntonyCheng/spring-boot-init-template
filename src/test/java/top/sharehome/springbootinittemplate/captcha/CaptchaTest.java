package top.sharehome.springbootinittemplate.captcha;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.sharehome.springbootinittemplate.config.captcha.model.CaptchaCreate;
import top.sharehome.springbootinittemplate.config.captcha.service.CaptchaService;
import top.sharehome.springbootinittemplate.utils.redisson.KeyPrefixConstants;
import top.sharehome.springbootinittemplate.utils.redisson.cache.CacheUtils;

/**
 * 验证码测试类
 *
 * @author AntonyCheng
 */
@SpringBootTest
public class CaptchaTest {

    @Resource
    private CaptchaService captchaService;

    /**
     * 测试验证码生成和校验
     */
    @Test
    public void testCaptcha() {
        CaptchaCreate captcha = captchaService.createCaptcha();
        String uuid = captcha.getUuid();
        String key = KeyPrefixConstants.CAPTCHA_PREFIX + uuid;
        String code = CacheUtils.getNoPrefix(key, String.class);
        System.out.println("生成验证码成功: " + code);
        captchaService.checkCaptcha(code, uuid);
        System.out.println("验证码校验成功");
    }

}