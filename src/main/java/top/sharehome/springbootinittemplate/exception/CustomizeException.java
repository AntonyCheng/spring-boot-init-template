package top.sharehome.springbootinittemplate.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;

/**
 * 自定义异常接口
 *
 * @author AntonyCheng
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CustomizeException extends RuntimeException {

    public ReturnCode returnCode = null;

    public String msg = null;

}
