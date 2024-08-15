package top.sharehome.springbootinittemplate.exception.customize;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.exception.CustomizeException;

import java.util.Objects;

/**
 * 自定义文档异常
 *
 * @author AntonyCheng
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Slf4j
public class CustomizeDocumentException extends CustomizeException {

    public CustomizeDocumentException() {
        this.returnCode = ReturnCode.FAIL;
        this.msg = ReturnCode.FAIL.getMsg();
    }

    public CustomizeDocumentException(ReturnCode returnCode) {
        this.returnCode = returnCode;
        this.msg = returnCode.getMsg();
    }

    public CustomizeDocumentException(ReturnCode returnCode, String msg) {
        if (Objects.equals(ReturnCode.EXCEL_FILE_ERROR.getCode(), returnCode.getCode())) {
            log.error("{}，{}", ReturnCode.EXCEL_FILE_ERROR.getMsg(), msg);
        } else if (Objects.equals(ReturnCode.WORD_FILE_ERROR.getCode(), returnCode.getCode())) {
            log.error("{}，{}", ReturnCode.WORD_FILE_ERROR.getMsg(), msg);
        } else if (Objects.equals(ReturnCode.PDF_FILE_ERROR.getCode(), returnCode.getCode())) {
            log.error("{}，{}", ReturnCode.PDF_FILE_ERROR.getMsg(), msg);
        }
        this.returnCode = returnCode;
        this.msg = StringUtils.isBlank(msg) ? returnCode.getMsg() : returnCode.getMsg() + " ==> [" + msg + "]";
    }

    @Override
    public String getMessage() {
        return this.msg;
    }

}
