package top.sharehome.springbootinittemplate.exception.customize;

import com.google.common.base.Objects;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.exception.CustomizeException;

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
        if (Objects.equal(ReturnCode.EXCEL_FILE_ERROR.getCode(), returnCode.getCode())) {
            log.error("{}，{}", ReturnCode.EXCEL_FILE_ERROR.getMsg(), msg);
        } else if (Objects.equal(ReturnCode.WORD_FILE_ERROR.getCode(), returnCode.getCode())) {
            log.error("{}，{}", ReturnCode.WORD_FILE_ERROR.getMsg(), msg);
        } else if (Objects.equal(ReturnCode.PDF_FILE_ERROR.getCode(), returnCode.getCode())) {
            log.error("{}，{}", ReturnCode.PDF_FILE_ERROR.getMsg(), msg);
        }
        this.returnCode = returnCode;
        this.msg = returnCode.getMsg() + " ==> [" + msg + "]";
    }

    @Override
    public String getMessage() {
        return this.msg;
    }

}
