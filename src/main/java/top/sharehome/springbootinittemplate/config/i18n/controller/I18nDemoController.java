package top.sharehome.springbootinittemplate.config.i18n.controller;

import org.springframework.context.annotation.Conditional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.sharehome.springbootinittemplate.common.base.R;
import top.sharehome.springbootinittemplate.config.i18n.I18nManager;
import top.sharehome.springbootinittemplate.config.i18n.condition.I18nCondition;

/**
 * 国际化示例控制器
 *
 * @author AntonyCheng
 */
@RestController
@Conditional(I18nCondition.class)
public class I18nDemoController {

    @GetMapping("/i18n")
    public R<String> welcome(@RequestParam String name) {
        return R.ok(I18nManager.getMessage("msg_welcome", new String[]{name}));
    }

}