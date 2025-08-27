package top.sharehome.springbootinittemplate.controller.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import top.sharehome.springbootinittemplate.config.captcha.model.Captcha;

import java.io.Serial;
import java.io.Serializable;

/**
 * 示例-验证码注解
 *
 * @author AntonyCheng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ExampleCaptcha implements Serializable {

    private Captcha captcha;

    @Serial
    private static final long serialVersionUID = 281914241259281279L;

}
