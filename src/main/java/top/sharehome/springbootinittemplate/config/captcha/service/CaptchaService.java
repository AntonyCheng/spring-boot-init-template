package top.sharehome.springbootinittemplate.config.captcha.service;

import java.util.Map;

/**
 * 验证码服务接口
 *
 * @author xg
 */
public interface CaptchaService {

    /**
     * 生成验证码
     */
    Map<String, Object> createCaptcha();

    /**
     * 校验验证码
     *
     * @param code 验证码
     * @param uuid 验证码的UUID
     */
    void checkCaptcha(String code, String uuid);

}