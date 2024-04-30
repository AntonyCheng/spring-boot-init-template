package top.sharehome.springbootinittemplate.config.captcha.generator;

import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.core.math.Calculator;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.RandomUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * 无符号计算生成器
 *
 * @author AntonyCheng
 */
public class UnsignedMathGenerator implements CodeGenerator {

    private static final long serialVersionUID = 694053667924803668L;

    private static final String OPERATORS = "+-*";

    /**
     * 参与计算数字最大长度
     */
    private final int numberLength;

    /**
     * 构造
     */
    public UnsignedMathGenerator() {
        this(1);
    }

    /**
     * 构造
     *
     * @param numberLength 参与计算最大数字位数
     */
    public UnsignedMathGenerator(int numberLength) {
        this.numberLength = numberLength;
    }

    @Override
    public String generate() {
        final int limit = getLimit();
        int a = RandomUtil.randomInt(limit);
        int b = RandomUtil.randomInt(limit);
        String max = Integer.toString(Math.max(a, b));
        String min = Integer.toString(Math.min(a, b));
        max = StringUtils.rightPad(max, this.numberLength, CharUtil.SPACE);
        min = StringUtils.rightPad(min, this.numberLength, CharUtil.SPACE);

        return max + RandomUtil.randomChar(OPERATORS) + min + '=';
    }

    @Override
    public boolean verify(String code, String userInputCode) {
        int result;
        try {
            result = Integer.parseInt(userInputCode);
        } catch (NumberFormatException e) {
            // 用户输入非数字
            return false;
        }

        final int calculateResult = (int) Calculator.conversion(code);
        return result == calculateResult;
    }

    /**
     * 获取验证码长度
     *
     * @return 验证码长度
     */
    public int getLength() {
        return this.numberLength * 2 + 2;
    }

    /**
     * 根据长度获取参与计算数字最大值
     *
     * @return 最大值
     */
    private int getLimit() {
        if (this.numberLength < 1 || this.numberLength > 9) {
            return 10;
        }
        return Integer.parseInt("1" + StringUtils.repeat('0', this.numberLength));
    }

}