package top.sharehome.springbootinittemplate.config.captcha.properties.enums;

import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.captcha.generator.RandomGenerator;
import lombok.AllArgsConstructor;
import lombok.Getter;
import top.sharehome.springbootinittemplate.config.captcha.generator.UnsignedMathGenerator;

/**
 * 验证码类型
 *
 * @author AntonyCheng
 */
@Getter
@AllArgsConstructor
public enum CaptchaType {

    /**
     * 数字
     */
    MATH(UnsignedMathGenerator.class),

    /**
     * 字符
     */
    CHAR(RandomGenerator.class);

    private final Class<? extends CodeGenerator> clazz;

}