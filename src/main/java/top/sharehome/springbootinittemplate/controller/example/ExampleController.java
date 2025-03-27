package top.sharehome.springbootinittemplate.controller.example;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.FilenameUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;
import top.sharehome.springbootinittemplate.common.base.Constants;
import top.sharehome.springbootinittemplate.common.base.R;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.common.validate.PostGroup;
import top.sharehome.springbootinittemplate.config.captcha.annotation.EnableCaptcha;
import top.sharehome.springbootinittemplate.config.encrypt.annotation.RSADecrypt;
import top.sharehome.springbootinittemplate.config.encrypt.annotation.RSAEncrypt;
import top.sharehome.springbootinittemplate.config.log.annotation.ControllerLog;
import top.sharehome.springbootinittemplate.config.log.enums.Operator;
import top.sharehome.springbootinittemplate.controller.example.entity.ExampleCaptcha;
import top.sharehome.springbootinittemplate.controller.example.entity.ExampleEncryptBody;
import top.sharehome.springbootinittemplate.controller.example.entity.ExampleSingleFile;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeReturnException;
import top.sharehome.springbootinittemplate.utils.document.pdf.PdfUtils;
import top.sharehome.springbootinittemplate.utils.document.pdf.enums.ExportDataSource;
import top.sharehome.springbootinittemplate.utils.document.word.WordUtils;
import top.sharehome.springbootinittemplate.utils.net.NetUtils;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 示例接口控制器
 *
 * @author AntonyCheng
 */
@RestController
@RequestMapping("/example")
@SaCheckLogin
@SaCheckRole(value = {Constants.ROLE_ADMIN})
public class ExampleController {

    /**
     * 验证码注解示例
     * 要点：
     * 1、修改application-XXX.yaml文件中captcha配置，开启验证码功能
     * 2、对Controller接口方法标记@EnableCaptcha注解
     * 3、请求方式一定是Post
     * 4、在请求体中加入Captcha类型字段，例如ExampleCaptcha类
     *
     * @return 验证结果
     */
    @PostMapping("/check/captcha")
    @EnableCaptcha
    @ControllerLog(description = "用户调用验证码示例接口", operator = Operator.OTHER)
    public R<String> checkCaptcha(@RequestBody ExampleCaptcha exampleCaptcha) {
        return R.ok("验证成功");
    }

    /**
     * 请求参数解密注解示例
     * 要点：
     * 1、修改application-XXX.yaml文件中encrypt配置，开启请求参数解密功能
     * 2、对Controller接口方法标记@RSADecrypt注解
     * 3、在请求参数前标记@RequestParam和@RSAEncrypt注解，如exampleEncryptParam参数
     * 4、在请求体前标记@RequestBody注解，在其需要解密的字段上标记@RSADecrypt注解，如ExampleEncryptBody类
     * 5、如果想要单独运行接口，需要获取RSA公钥后自行前往支持在线RSA算法加密的网站进行内容加密，再传入该方法中
     *
     * @return 加密结果
     */
    @PostMapping("/encrypt")
    @RSADecrypt
    @ControllerLog(description = "用户调用请求参数解密示例接口", operator = Operator.OTHER)
    public R<Map<String, Object>> decryptionRequestParameters(@RequestBody @Validated({PostGroup.class}) ExampleEncryptBody exampleEncryptBody, @RequestParam @RSAEncrypt String exampleEncryptParam) {
        return R.ok(new HashMap<>() {
            {
                put("exampleEncryptBody", exampleEncryptBody);
                put("exampleEncryptParam", exampleEncryptParam);
            }
        });
    }

    /**
     * 通过模板导出Word
     *
     * @return 返回Word
     */
    @GetMapping("/word/template")
    @ControllerLog(description = "用户调用通过模板导出Word接口", operator = Operator.OTHER)
    public R<Void> exportWordByTemplate(@RequestParam String title, @RequestParam String name, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, HttpServletResponse response) {
        HashMap<String, Object> hashMap = new HashMap<>() {
            {
                put("title", title);
                put("name", name);
                put("date", date);
            }
        };
        new WordUtils.Template().export("template.docx", hashMap, "输出模板Word", response);
        return R.empty();
    }

    /**
     * 获取Word中所有段落TXT文件
     *
     * @return 返回TXT文件
     */
    @PostMapping("/word/txt/paragraphs")
    @ControllerLog(description = "用户调用获取Word中所有段落TXT文件接口", operator = Operator.OTHER)
    public R<Void> getParagraphsTxtInWord(@Validated({PostGroup.class}) ExampleSingleFile exampleSingleFile, HttpServletResponse response) {
        MultipartFile file = exampleSingleFile.getFile();
        try (InputStream inputStream = file.getInputStream()) {
            String filename = StringUtils.isEmpty(file.getOriginalFilename()) ? file.getName() : file.getOriginalFilename();
            String extension = FilenameUtils.getExtension(filename).toLowerCase();
            if ("docx".equals(extension)) {
                new WordUtils.XWPFReader(inputStream).getParagraphsResponse(response);
            } else {
                new WordUtils.HWPFReader(inputStream).getParagraphsResponse(response);
            }
        } catch (IOException ignored) {
        }
        return R.empty();
    }

    /**
     * 获取Word中所有表格压缩包
     *
     * @return 返回压缩包
     */
    @PostMapping("/word/zip/tables")
    @ControllerLog(description = "用户调用获取Word中所有表格压缩包接口", operator = Operator.OTHER)
    public R<Void> getTablesZipInWord(@Validated({PostGroup.class}) ExampleSingleFile exampleSingleFile, HttpServletResponse response) {
        MultipartFile file = exampleSingleFile.getFile();
        try (InputStream inputStream = file.getInputStream()) {
            String filename = StringUtils.isEmpty(file.getOriginalFilename()) ? file.getName() : file.getOriginalFilename();
            String extension = FilenameUtils.getExtension(filename).toLowerCase();
            if ("docx".equals(extension)) {
                new WordUtils.XWPFReader(inputStream).getTablesResponse(response);
            } else {
                new WordUtils.HWPFReader(inputStream).getTablesResponse(response);
            }
        } catch (IOException ignored) {
        }
        return R.empty();
    }

    /**
     * 获取Word中所有图片压缩包
     *
     * @return 返回压缩包
     */
    @PostMapping("/word/zip/images")
    @ControllerLog(description = "用户调用获取Word中所有图片压缩包接口", operator = Operator.OTHER)
    public R<Void> getImagesZipInWord(@Validated({PostGroup.class}) ExampleSingleFile exampleSingleFile, HttpServletResponse response) {
        MultipartFile file = exampleSingleFile.getFile();
        try (InputStream inputStream = file.getInputStream()) {
            String filename = StringUtils.isEmpty(file.getOriginalFilename()) ? file.getName() : file.getOriginalFilename();
            String extension = FilenameUtils.getExtension(filename).toLowerCase();
            if ("docx".equals(extension)) {
                new WordUtils.XWPFReader(inputStream).getImagesResponse(response);
            } else {
                new WordUtils.HWPFReader(inputStream).getImagesResponse(response);
            }
        } catch (IOException ignored) {
        }
        return R.empty();
    }

    /**
     * 通过Freemarker模板导出PDF
     *
     * @return 返回PDF
     */
    @GetMapping("/pdf/template/freemarker")
    @ControllerLog(description = "用户调用通过Freemarker模板导出PDF接口", operator = Operator.OTHER)
    public R<Void> exportPdfByFreemarkerTemplate(HttpServletResponse response) {
        Map<String, Object> freemarkerData = new HashMap<>();
        List<String> freemarkerList = new ArrayList<>(2);
        freemarkerList.add("hello");
        freemarkerList.add("world");
        freemarkerData.put("list", freemarkerList);
        freemarkerData.put("str", "hello world");
        new PdfUtils.Template().export("template-freemarker.fo", ExportDataSource.FREEMARKER, freemarkerData, "输出Freemarker模板PDF", response);
        return R.empty();
    }

    /**
     * 通过Thymeleaf模板导出PDF
     *
     * @return 返回PDF
     */
    @GetMapping("/pdf/template/thymeleaf")
    @ControllerLog(description = "用户调用通过Thymeleaf模板导出PDF接口", operator = Operator.OTHER)
    public R<Void> exportPdfByThymeleafTemplate(HttpServletResponse response) {
        Map<String, Object> thymeleafData = new HashMap<>();
        thymeleafData.put("data", "hello world");
        new PdfUtils.Template().export("template-thymeleaf.fo", ExportDataSource.THYMELEAF, thymeleafData, "输出Thymeleaf模板PDF", response);
        return R.empty();
    }

    /**
     * 通过Jte模板导出PDF
     *
     * @return 返回PDF
     */
    @GetMapping("/pdf/template/jte")
    @ControllerLog(description = "用户调用通过Jte模板导出PDF接口", operator = Operator.OTHER)
    public R<Void> exportPdfByJteTemplate(HttpServletResponse response) {
        Map<String, Object> jteData = new HashMap<>();
        List<String> jteList = new ArrayList<>(2);
        jteList.add("hello");
        jteList.add("world");
        jteData.put("list", jteList);
        jteData.put("str", "hello world");
        new PdfUtils.Template().export("template-jte.jte", ExportDataSource.JTE, jteData, "输出Jte模板PDF", response);
        return R.empty();
    }

    /**
     * 获取Pdf中所有段落TXT文件
     *
     * @return 返回TXT文件
     */
    @PostMapping("/pdf/txt/paragraphs")
    @ControllerLog(description = "用户调用获取PDF中所有段落TXT文件接口", operator = Operator.OTHER)
    public R<Void> getParagraphsTxtInPdf(@Validated({PostGroup.class}) ExampleSingleFile exampleSingleFile, HttpServletResponse response) {
        try (InputStream inputStream = exampleSingleFile.getFile().getInputStream()) {
            new PdfUtils.Reader(inputStream).getParagraphsResponse(response);
        } catch (IOException ignored) {
        }
        return R.empty();
    }

    /**
     * 获取PDF中所有图片压缩包
     *
     * @return 返回压缩包
     */
    @PostMapping("/pdf/zip/images")
    @ControllerLog(description = "用户调用获取PDF中所有图片压缩包接口", operator = Operator.OTHER)
    public R<Void> getTablesZipInPdf(@Validated({PostGroup.class}) ExampleSingleFile exampleSingleFile, HttpServletResponse response) {
        try (InputStream inputStream = exampleSingleFile.getFile().getInputStream()) {
            new PdfUtils.Reader(inputStream).getImagesResponse(response);
        } catch (IOException ignored) {
        }
        return R.empty();
    }

    /**
     * 通过IP获取地址
     *
     * @return IP
     */
    @GetMapping("/ip2region/region/by/ip")
    @ControllerLog(description = "用户调用通过IP获取地址接口", operator = Operator.OTHER)
    public R<String> getRegionByIp(@RequestParam String ip) {
        try {
            return R.ok(NetUtils.getRegionByIp(ip));
        } catch (Exception e) {
            throw new CustomizeReturnException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "IP参数错误");
        }
    }

    /**
     * 通过请求获取IP和地址
     *
     * @return IP和地址
     */
    @GetMapping("/ip2region/ip/region/by/request")
    @ControllerLog(description = "用户调用通过请求获取IP和地址", operator = Operator.OTHER)
    public R<String> getIpAndRegionByRequest(HttpServletRequest request) {
        String ip = NetUtils.getIpByRequest(request);
        return R.ok("ip：" + ip + " 地址：" + NetUtils.getRegionByIp(ip));
    }

}
