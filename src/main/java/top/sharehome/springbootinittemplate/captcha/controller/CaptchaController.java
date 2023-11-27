package top.sharehome.springbootinittemplate.captcha.controller;

import org.springframework.context.annotation.Conditional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import top.sharehome.springbootinittemplate.captcha.condition.CaptchaCondition;
import top.sharehome.springbootinittemplate.common.base.R;
import top.sharehome.springbootinittemplate.captcha.model.CaptchaCreate;
import top.sharehome.springbootinittemplate.captcha.service.CaptchaService;

import javax.annotation.Resource;

/**
 * 验证码控制器
 *
 * @author AntonyCheng
 */
@RestController
public class CaptchaController {

    @Resource
    private CaptchaService captchaService;

    /**
     * 获取验证码
     *
     * @return 返回注册结果
     */
    @PostMapping("/captcha")
    public R<CaptchaCreate> captcha() {
        return R.ok(captchaService.createCaptcha());
    }

}