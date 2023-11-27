package top.sharehome.springbootinittemplate.captcha.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 校验验证码实体类
 *
 * @author AntonyCheng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class CaptchaCheck implements Serializable {

    private static final long serialVersionUID = -2384156304879302998L;

    /**
     * 验证码
     */
    private String code;

    /**
     * 验证码UUID，用于组成Redis中的键
     */
    private String uuid;

}