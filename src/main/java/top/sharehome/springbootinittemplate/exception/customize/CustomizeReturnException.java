package top.sharehome.springbootinittemplate.exception.customize;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;

/**
 * 自定义返回错误异常类
 *
 * @author AntonyCheng
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CustomizeReturnException extends RuntimeException {

    private ReturnCode returnCode;

    private String msg;

    public <T> CustomizeReturnException() {
        this.returnCode = ReturnCode.FAIL;
        this.msg = null;
    }

    public <T> CustomizeReturnException(ReturnCode returnCode) {
        this.returnCode = returnCode;
        this.msg = null;
    }

    public <T> CustomizeReturnException(ReturnCode returnCode, String msg) {
        this.returnCode = returnCode;
        this.msg = msg;
    }

    @Override
    public String getMessage() {
        return msg == null ? returnCode.getMsg() : msg;
    }

}