package top.sharehome.springbootinittemplate.config.captcha.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 创建验证码实体类
 *
 * @author AntonyCheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class CaptchaCreate implements Serializable {

    /**
     * 是否开启验证码
     */
    private boolean enableCode;

    /**
     * 验证码UUID，用于组成Redis中的键
     */
    private String uuid;

    /**
     * 验证码图片Base64字符串
     */
    private String imgBase64;

    @Serial
    private static final long serialVersionUID = -2384156304879302998L;

}