package top.sharehome.springbootinittemplate.exception.customize;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;

/**
 * 自定义事务异常
 * 数据库事物处理出现错误，回滚到该异常
 *
 * @author AntonyCheng
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CustomizeTransactionException extends RuntimeException {

    private ReturnCode returnCode;

    private String msg;

    public CustomizeTransactionException() {
        this.returnCode = ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE;
        this.msg = ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE.getMsg();
    }

    public CustomizeTransactionException(ReturnCode returnCode) {
        this.returnCode = returnCode;
        this.msg = returnCode.getMsg();
    }

    public CustomizeTransactionException(ReturnCode returnCode, String msg) {
        this.returnCode = returnCode;
        this.msg = returnCode.getMsg() + " ==> [" + msg + "]";
    }

    @Override
    public String getMessage() {
        return this.msg;
    }

}