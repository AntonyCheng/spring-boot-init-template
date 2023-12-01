package top.sharehome.springbootinittemplate.i18n.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.sharehome.springbootinittemplate.common.base.R;
import top.sharehome.springbootinittemplate.utils.i18n.I18nUtils;

/**
 * 国际化示例控制器
 *
 * @author AntonyCheng
 */
@RestController
public class I18nDemoController {
    @GetMapping("/i18n")
    public R<String> welcome(@RequestParam String name) {
        return R.ok(I18nUtils.getMessage("msg_welcome", new String[]{name}));
    }
}
