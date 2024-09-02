package top.sharehome.springbootinittemplate.exception.customize;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.exception.CustomizeException;

/**
 * 自定义邮件异常
 *
 * @author AntonyCheng
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class CustomizeMailException extends CustomizeException{

    public CustomizeMailException() {
        this.returnCode = ReturnCode.FAIL;
        this.msg = ReturnCode.FAIL.getMsg();
    }

    public CustomizeMailException(ReturnCode returnCode) {
        this.returnCode = returnCode;
        this.msg = returnCode.getMsg();
    }

    public CustomizeMailException(ReturnCode returnCode, String msg) {
        log.error("{} ==> {}", returnCode.getMsg(), "[" + msg + "]");
        this.returnCode = returnCode;
        this.msg = StringUtils.isBlank(msg) ? returnCode.getMsg() : msg;
    }

    public CustomizeMailException(ReturnCode returnCode, String msg, Throwable cause) {
        log.error("{} ==> {} ==> {}", returnCode.getMsg(), "[" + msg + "]", cause.getMessage());
        this.returnCode = returnCode;
        this.msg = StringUtils.isBlank(msg) ? returnCode.getMsg() : msg;
    }

    @Override
    public String getMessage() {
        return this.msg;
    }

}