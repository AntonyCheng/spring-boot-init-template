package top.sharehome.springbootinittemplate.config.encrypt.controller;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Conditional;
import org.springframework.web.bind.annotation.*;
import top.sharehome.springbootinittemplate.common.base.R;
import top.sharehome.springbootinittemplate.config.encrypt.EncryptConfiguration;
import top.sharehome.springbootinittemplate.config.encrypt.condition.EncryptCondition;

/**
 * 加密控制器
 *
 * @author AntonyCheng
 */
@RestController
@RequestMapping("/encrypt")
@Conditional(EncryptCondition.class)
public class EncryptController {

    @Resource
    private EncryptConfiguration encryptConfiguration;

    /**
     * 获取RSA公钥
     *
     * @return RSA公钥
     */
    @GetMapping("/rsa/public/key")
    public R<String> getRsaPublicKey() {
        return R.ok(encryptConfiguration.getRsaPublicKey());
    }

}
