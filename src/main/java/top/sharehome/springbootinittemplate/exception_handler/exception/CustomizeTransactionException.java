package top.sharehome.springbootinittemplate.exception_handler.exception;

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

    private ReturnCode returnCode = ReturnCode.ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE;

    @Override
    public String getMessage() {
        return returnCode.getMsg();
    }
}