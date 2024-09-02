package top.sharehome.springbootinittemplate.utils.request;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeReturnException;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 请求参数和请求体修改工具类
 *
 * @author AntonyCheng
 */
@Slf4j
public class ParamsAndBodyRequestWrapper extends HttpServletRequestWrapper {

    /**
     * 请求参数Map
     */
    private final Map<String, String[]> requestParams;

    /**
     * 请求体字节数组
     */
    private byte[] requestBody;

    public ParamsAndBodyRequestWrapper(HttpServletRequest request) {
        super(request);
        this.requestParams = new HashMap<>(request.getParameterMap());
        try (ServletInputStream inputStream = request.getInputStream()) {
            this.requestBody = inputStream.readAllBytes();
        } catch (IOException e) {
            throw new CustomizeReturnException(ReturnCode.FAIL, "请求数据流获取失败");
        }
    }

    /**
     * 修改或添加请求参数
     *
     * @param key   参数键
     * @param value 参数值
     */
    public void updateOrAddParams(String key, String value) {
        String[] values = {value};
        this.requestParams.put(key, values);
    }

    /**
     * 删除请求参数
     *
     * @param key 参数键
     */
    public void deleteParams(String key) {
        this.requestParams.remove(key);
    }

    /**
     * 获取单个请求参数数组
     *
     * @param key 参数键
     */
    @Override
    public String[] getParameterValues(String key) {
        return requestParams.get(key);
    }

    /**
     * 获取单个请求参数
     *
     * @param key 参数键
     */
    @Override
    public String getParameter(String key) {
        String[] values = this.requestParams.get(key);
        return values != null && values.length > 0 ? values[0] : null;
    }

    /**
     * 获取所有请求参数
     */
    @Override
    public Map<String, String[]> getParameterMap() {
        return this.requestParams;
    }

    /**
     * 修改或添加请求体
     *
     * @param requestBody 请求体对象
     */
    public void updateOrAddRequestBody(Object requestBody) {
        String jsonString = JSON.toJSONString(requestBody);
        updateOrAddRequestBodyStr(jsonString);
    }

    /**
     * 修改或添加请求体
     *
     * @param requestBodyStr 请求体字符串
     */
    public void updateOrAddRequestBodyStr(String requestBodyStr) {
        if (Objects.isNull(requestBodyStr)) {
            requestBodyStr = "";
        }
        this.requestBody = requestBodyStr.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 获取请求体数据流
     */
    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.requestBody);
        return new ServletInputStream() {

            @Override
            public int available() {
                return requestBody.length;
            }

            @Override
            public boolean isFinished() {
                return Objects.equals(byteArrayInputStream.available(), 0);
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

        };
    }

    /**
     * 获取请求体数据缓冲区读取器
     */
    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }
}
