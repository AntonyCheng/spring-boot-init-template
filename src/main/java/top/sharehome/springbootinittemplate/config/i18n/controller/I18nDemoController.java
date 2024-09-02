package top.sharehome.springbootinittemplate.config.i18n.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.sharehome.springbootinittemplate.common.base.R;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.i18n.I18nManager;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeReturnException;

import java.util.Random;

/**
 * 国际化示例控制器（仅测试使用，使用时取消注释RestController注解）
 *
 * @author AntonyCheng
 */
//@RestController
public class I18nDemoController {

    @GetMapping("/i18n")
    public R<String> welcome(@RequestParam String name) {
        int seed = new Random().nextInt();
        if (seed % 2 == 0) {
            throw new CustomizeReturnException(ReturnCode.ACCOUNT_AND_EMAIL_DO_NOT_MATCH.toI18n());
        }else {
            return R.ok(I18nManager.getMessage("welcome", name));
        }
    }

}