package top.sharehome.springbootinittemplate.exception.customize;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;

/**
 * 自定义Excel异常
 *
 * @author AntonyCheng
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CustomizeExcelException extends RuntimeException {

    private ReturnCode returnCode;

    private String msg;

    public CustomizeExcelException() {
        this.returnCode = ReturnCode.FAIL;
        this.msg = ReturnCode.FAIL.getMsg();
    }

    public CustomizeExcelException(ReturnCode returnCode) {
        this.returnCode = returnCode;
        this.msg = returnCode.getMsg();
    }

    public CustomizeExcelException(ReturnCode returnCode, String msg) {
        this.returnCode = returnCode;
        this.msg = returnCode.getMsg() + " ==> [" + msg + "]";
    }

    @Override
    public String getMessage() {
        return this.msg;
    }

}
