package top.sharehome.springbootinittemplate.utils.servlet;

import io.undertow.servlet.spec.ServletOutputStreamImpl;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 响应修改工具类
 *
 * @author AntonyCheng
 */
@Slf4j
public class ResponseWrapper extends HttpServletResponseWrapper {

    private ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private PrintWriter writer = new PrintWriter(outputStream);

    public ResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public PrintWriter getWriter() {
        return writer;
    }

    public byte[] getResponseData() {
        writer.flush();
        return outputStream.toByteArray();
    }

    public void setResponseData(String data) {
        outputStream.reset();
        writer.write(data);
        writer.flush();
    }

}
