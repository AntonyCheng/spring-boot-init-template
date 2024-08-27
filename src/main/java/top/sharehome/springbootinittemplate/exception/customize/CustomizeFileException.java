package top.sharehome.springbootinittemplate.exception.customize;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.exception.CustomizeException;

/**
 * 自定义文件异常
 *
 * @author AntonyCheng
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class CustomizeFileException extends CustomizeException {

    public CustomizeFileException() {
        this.returnCode = ReturnCode.FAIL;
        this.msg = ReturnCode.FAIL.getMsg();
    }

    public CustomizeFileException(ReturnCode returnCode) {
        this.returnCode = returnCode;
        this.msg = returnCode.getMsg();
    }

    public CustomizeFileException(ReturnCode returnCode, String msg) {
        log.error("{} ==> {}", returnCode.getMsg(), "[" + msg + "]");
        this.returnCode = returnCode;
        this.msg = StringUtils.isBlank(msg) ? returnCode.getMsg() : msg;
    }

    @Override
    public String getMessage() {
        return this.msg;
    }

}