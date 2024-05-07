package top.sharehome.springbootinittemplate.exception.customize;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;

/**
 * 自定义邮件异常
 *
 * @author AntonyCheng
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CustomizeMailException extends RuntimeException {

    private ReturnCode returnCode;

    private String msg;

    public CustomizeMailException() {
        this.returnCode = ReturnCode.FAIL;
        this.msg = ReturnCode.FAIL.getMsg();
    }

    public CustomizeMailException(ReturnCode returnCode) {
        this.returnCode = returnCode;
        this.msg = returnCode.getMsg();
    }

    public CustomizeMailException(ReturnCode returnCode, String msg) {
        this.returnCode = returnCode;
        this.msg = returnCode.getMsg() + " ==> [" + msg + "]";
    }

    @Override
    public String getMessage() {
        return this.msg;
    }

}