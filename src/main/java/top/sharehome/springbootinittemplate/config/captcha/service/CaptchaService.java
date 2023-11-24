package top.sharehome.springbootinittemplate.config.captcha.service;

import top.sharehome.springbootinittemplate.config.captcha.model.Captcha;

/**
 * 验证码服务接口
 *
 * @author xg
 */
public interface CaptchaService {

    /**
     * 生成验证码
     */
    Captcha createCaptcha();

    /**
     * 校验验证码
     *
     * @param code 验证码
     * @param uuid 验证码的UUID
     */
    void checkCaptcha(String code, String uuid);

}