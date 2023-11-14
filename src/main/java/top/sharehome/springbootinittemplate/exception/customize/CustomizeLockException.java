package top.sharehome.springbootinittemplate.exception.customize;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;

/**
 * 自定义锁异常
 *
 * @author AntonyCheng
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CustomizeLockException extends RuntimeException {

    private ReturnCode returnCode = ReturnCode.LOCK_SERVICE_ERROR;

    @Override
    public String getMessage() {
        return returnCode.getMsg();
    }

}