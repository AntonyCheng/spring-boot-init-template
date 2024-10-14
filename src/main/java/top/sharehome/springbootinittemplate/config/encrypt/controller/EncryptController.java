package top.sharehome.springbootinittemplate.config.encrypt.controller;

import org.springframework.context.annotation.Conditional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.sharehome.springbootinittemplate.common.base.R;
import top.sharehome.springbootinittemplate.common.validate.PostGroup;
import top.sharehome.springbootinittemplate.config.encrypt.EncryptConfiguration;
import top.sharehome.springbootinittemplate.config.encrypt.annotation.RSAEncrypt;
import top.sharehome.springbootinittemplate.config.encrypt.condition.EncryptCondition;
import top.sharehome.springbootinittemplate.config.encrypt.controller.model.TestEncryptDto;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

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

    /**
     * 加密测试控制器方法（仅测试使用，使用时取消注释GetMapping注解和RSADecrypt注解）
     * 注意：想要运行测试方法，需要获取RSA公钥后自行前往支持在线RSA算法加密的网站进行内容加密，再传入该方法中
     */
//    @PostMapping("/test")
//    @RSADecrypt
    public R<Map<String, Object>> test(@RequestBody @Validated({PostGroup.class}) TestEncryptDto testEncryptDto, @RequestParam @RSAEncrypt String test) {
        return R.ok(new HashMap<>() {
            {
                put("testEncryptDto", testEncryptDto);
                put("test", test);
            }
        });
    }

}
