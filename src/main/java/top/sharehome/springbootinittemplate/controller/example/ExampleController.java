package top.sharehome.springbootinittemplate.controller.example;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.idev.excel.EasyExcel;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import top.sharehome.springbootinittemplate.common.base.Constants;
import top.sharehome.springbootinittemplate.common.base.R;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.common.validate.PostGroup;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.impl.AiChatServiceImpl;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.ChatResult;
import top.sharehome.springbootinittemplate.config.ai.spring.service.chat.model.entity.OpenAiChatEntity;
import top.sharehome.springbootinittemplate.config.captcha.annotation.EnableCaptcha;
import top.sharehome.springbootinittemplate.config.easyexcel.core.impl.DefaultExcelListener;
import top.sharehome.springbootinittemplate.config.encrypt.annotation.RSADecrypt;
import top.sharehome.springbootinittemplate.config.encrypt.annotation.RSAEncrypt;
import top.sharehome.springbootinittemplate.config.log.annotation.ControllerLog;
import top.sharehome.springbootinittemplate.config.log.enums.Operator;
import top.sharehome.springbootinittemplate.controller.example.model.*;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeReturnException;
import top.sharehome.springbootinittemplate.utils.document.excel.ExcelUtils;
import top.sharehome.springbootinittemplate.utils.document.pdf.PdfUtils;
import top.sharehome.springbootinittemplate.utils.document.pdf.enums.ExportDataSource;
import top.sharehome.springbootinittemplate.utils.document.word.WordUtils;
import top.sharehome.springbootinittemplate.utils.net.NetUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

/**
 * 示例接口控制器
 *
 * @author AntonyCheng
 */
@RestController
@RequestMapping("/example")
@SaCheckLogin
@SaCheckRole(value = {Constants.ROLE_ADMIN})
@Slf4j
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

    @Resource
    private AiChatServiceImpl aiChatService;

    @PostMapping("/chatExcel")
    @SaIgnore
    public R<JSONObject> chatExcel(MultipartFile file) {
        StopWatch sw = new StopWatch();
        sw.start();
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new CustomizeReturnException(ReturnCode.FAIL, "文件处理异常");
        }
        DefaultExcelListener<Map<Integer, Object>> listener = new DefaultExcelListener<>();
        EasyExcel
                .read(inputStream, listener)
                .sheet()
                .doRead();
        List<String> headers = new ArrayList<>(listener.getHeadMap().values());
        List<Map<Integer, Object>> dataList = listener.getExcelResult().getList();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<Integer, Object> data : dataList) {
            Map<String, Object> item = new LinkedHashMap<>();
            data.forEach((key, value) -> {
                String fieldName = headers.get(key);
                item.put(fieldName, value);
            });
            result.add(item);
        }
        result.forEach(data -> {
            try {
                int people1 = Integer.parseInt((String) data.get("对方人数"));
                int people2 = Integer.parseInt((String) data.get("我方人数"));
                double money = Double.parseDouble((String) data.get("报账金额"));
                double average = money / (people1 + people2);
                data.put("人均消费", String.format("%.2f", average));
            } catch (NumberFormatException e) {
                data.put("人均消费", "N/A");
            }
            String type = String.valueOf(data.getOrDefault("招待类型", ""));
            if (org.apache.commons.lang3.StringUtils.equals(type, "其他公务招待活动") || org.apache.commons.lang3.StringUtils.equals(type, "系统内业务招待")) {
                try {
                    int people1 = Integer.parseInt((String) data.get("对方人数"));
                    int people2 = Integer.parseInt((String) data.get("我方人数"));
                    if (people1 <= 10) {
                        if (people2 <= 3) {
                            data.put("人数上限情况", "我方上限人数3人，实际人数" + people2 + "人，未超规格");
                        } else {
                            data.put("人数上限情况", "我方上限人数3人，实际人数" + people2 + "人，已超规格");
                        }
                    } else {
                        int realPeople = people1 / 3;
                        if (people2 <= realPeople) {
                            data.put("人数上限情况", "我方上限人数" + realPeople + "人，实际人数" + people2 + "人，未超规格");
                        } else {
                            data.put("人数上限情况", "我方上限人数" + realPeople + "人，实际人数" + people2 + "人，已超规格");
                        }
                    }
                } catch (NumberFormatException e) {
                    data.put("人数上限情况", "N/A");
                }
            }
        });
        int num;
        if (result.size() % 30 == 0) {
            num = result.size() / 30;
        } else {
            num = (result.size() / 30) + 1;
        }
        String basePrompt = """
                ############################################
                # 你的角色
                ############################################
                                
                你是 **“中国联通黑龙江省分公司业务招待费智能稽核助手”**。
                请严格按照本提示词中给出的 **稽核规则** 与 **违规类型 1‑6** 对我随后提供的 `records` JSON 数组执行逐条和跨条审查，并输出规定的 JSON 结果对象。
                                
                ############################################
                # 输入数据格式
                ############################################
                                
                我会输入形如：
                [
                  {
                    "报账单编号": "23231223011210300084",
                    "发票代码": "023002000104",
                    "发票号码": "22593722",
                    "招待等级": "省部门、市（地）、区县其他人员",
                    "招待类型": "其他公务招待活动",
                    "对方人数": 7,
                    "我方人数": 2,
                    "酒水安排": "自购",
                    "酒水预算": 100.00,
                    "预估费用合计": 1500.00,
                    "用餐地点": "内部",
                    "用餐次数": 1,
                    "招待开始日期": "2023-01-06 10:35:15.0",
                    "招待结束日期": "2023-01-09",
                    "开票日期": "2023-01-11",
                    "报账申请日期": "2023-01-12",
                    "报账金额": 2700.00,
                    "人均消费": 300.00,
                    "人数上限情况": "我方上限人数3人，实际人数2人，未超规格"
                  },
                  ...
                ]
                                
                ############################################
                # 稽核规则
                ############################################
                                
                ### 1. 销方名称（违规类型1）
                - 允许包含类似关键词：`饭``餐``食堂``酒楼``酒店``宴会厅`
                - 若 `销方名称` 不包含任一允许关键词（如出现 “高尔夫”“超市”“便利店”等），判 **违规类型1**。
                                
                ### 2. 陪同人数上限（违规类型2）
                判断依据 `招待类型` 和 `人数上限情况` ：
                | 招待类型关键字                          | 规则说明                                                     |
                | --------------------------------------- | ------------------------------------------------------------ |
                | `商务招待` 或 `外事招待`                | - 若 `对方人数 ≤ 5`：`我方人数 ≤ 5`；<br>- 若 `对方人数 > 5`：`我方人数 < 5 + (对方人数‑5)/2` (向下取整)。 |
                | 其他 (`其他公务招待`或`对内业务招待`) | - 若 `对方人数 ≤ 10`：`我方人数 ≤ 3`；<br>- 若 `对方人数 > 10`：`我方人数 < 对方人数 × 1/3` (向下取整)。 |
                                
                超过上限即判 **违规类型2**。
                                
                ### 3. 人均标准（违规类型3）
                1. 参考 `人均消费` 值即可。 \s
                2. 根据下表的 **招待等级 × 招待类型** 找到对应 **人均限额**，若人均费用超标，则判 **违规类型3**，并在 `违规详情` 写明 \s
                   「人均X元>Y元标准（XX 场景）」。
                                
                | 招待等级字段中关键词                               | 招待类型关键字           | 人均限额 (元) |
                | -------------------------------------------------- | ------------------------ | ------------- |
                | `省公司领导人员`                                   | 商务招待 / 外事招待      | **500**       |
                | `省公司领导人员`                                   | 其他公务招待             | **300**       |
                | `省公司各单位`、`地市分公司`、`区县分公司领导人员` | 对外业务全部类型         | **240**       |
                | `其他人员`、`省部门`、`市（地）`、`区县其他人员`   | 对外业务全部类型         | **200**       |
                | 任意招待等级                                       | 对内业务招待：`外省人员` | **150**       |
                | 任意招待等级                                       | 对内业务招待：`省内人员` | **50**        |
                                
                > **匹配规则**： \s
                > - 先用 `招待等级` 字段定位行；如匹配不到，视为 “其他人员”。 \s
                > - `招待类型` 字段若含 `商务` 则视为商务招待，含 `外事` 视为外事招待，含 `其他公务` 视为其他公务招待，含 `对内` 视为对内业务招待。 \s
                                
                ### 4. 跨记录比对（违规类型4‑6）
                在同一次输入的 `records` 内进行：
                                
                | 违规类型 | 判定条件                                                     |
                | -------- | ------------------------------------------------------------ |
                | **4**    | ≥2条记录的 `发票号码` **连续或相邻**（如 22593721 → 22593722）。 |
                | **5**    | `招待开始日期` 不同 **且** `开票日期` 相同 **且** `发票号码` 连号或相邻。 |
                | **6**    | `招待开始日期` 不同 **且** `开票日期` 不同 **且** `发票号码` 连号或相近 **且** `报账金额` 相同。 |
                                
                - 对符合条件的记录集合，输出：
                  ```json
                  {
                    "违规类型": 5,
                    "涉及单据": ["单号A", "单号B", ...],
                    "违规证据": "简洁中文说明"
                  }
                  ```
                - 若不存在跨记录问题，跨记录违规组 给空数组。
                                
                ############################################
                # 输出格式（必须完全遵守）
                ############################################
                                
                {
                  "详细结果": [
                    {
                      "报账单编号": "……",
                      "是否违规": true,
                      "违规类型": [3, 5],
                      "违规详情": [
                        "类型3（超标）：人均300.00元 > 200.00元标准（其他人员/对外业务）",
                        "类型2（陪同人数超标）：我方人数6 > 上限3"
                      ]
                    },
                    {
                      "报账单编号": "……",
                      "是否违规": false
                    }
                  ],
                  "跨记录违规组": [
                    {
                      "违规类型": 5,
                      "涉及单据": ["23231223011210300084", "23231223011210300082"],
                      "违规证据": "不同招待日期 + 相同开票日期 + 连号发票(22593721→22593722)"
                    }
                  ]
                }
                                
                - 所有金额保留 2 位小数；日期格式保持输入原样。
                - 字段次序、字段名、大小写、布尔类型 **不得修改**。
                - `违规类型` 只能为 `[1,2,3,4,5,6]` 的子集并升序排列；若 `是否违规` 为 `false`，`违规类型` 与 `违规详情` 两字段完全省略。
                - 说明文字全部用简体中文。
                - 不得输出除 JSON 结果以外的任何额外文本。
                                
                ############################################
                # 开始稽核
                ############################################
                                
                收到我发给你的 `records = [...]` 后，请立即按上述规则返回审查结果。
                                
                records =
                """;
        List<JSONObject> detailRes = new ArrayList<>();
        List<JSONObject> straddleRes = new ArrayList<>();
        List<List<Map<String, Object>>> list = new ArrayList<>();

        for (int i = 0; i < num; i++) {
            int start = i * 30;
            int end = Math.min((i + 1) * 30, result.size());
            List<Map<String, Object>> temp = new ArrayList<>();
            for (int j = start; j < end; j++) {
                temp.add(result.get(j));
            }
            list.add(temp);
        }
        Flux.fromIterable(list)
                .parallel()
                .runOn(Schedulers.newParallel("parallel", 12))
                .doOnNext(temp -> {
                    log.info("开始分析[{}]", System.currentTimeMillis());
                    String prompt = basePrompt + JSONObject.toJSONString(temp);
//                    ZhiPuAiChatEntity entity = new ZhiPuAiChatEntity("glm-4.5", "771cebbad74644af8c849ca899cd9566.O2Nenvq8CyxyJOPO");
                    OpenAiChatEntity entity = new OpenAiChatEntity("./deepseek-v3-int4/", "sk-I28olFy9SFMofgsf865aAaF0D9Bb47D48eC7Cb9a26010f83", "http://10.9.0.146:3001", 1000 * 60 * 10L);
//                    OpenAiChatEntity entity = new OpenAiChatEntity("deepseek-r1:1.5b", "sk-6el9vpsB0404bbFnynPr4EQLcx4fDVj04wb4u8OIpq2yIYf0", "http://192.168.230.60:3000");
//                    OllamaChatEntity entity = new OllamaChatEntity("deepseek-r1:1.5b", "http://localhost:11434");
//                    OllamaChatEntity entity = new OllamaChatEntity("gpt-oss:20b", "http://10.9.0.18:9528");
//                    OpenAiChatEntity entity = new OpenAiChatEntity("o4-mini", "sk-1L8xfj2oMskTOOu3iUMeRw5u3h511UKE99keF7EHKSzRUSBQ", "https://api.chatanywhere.tech");
                    ChatResult chatResult = aiChatService.chatString(entity, prompt);
                    String chatRes = chatResult.getContent();
                    System.out.println(chatRes);
                    if (chatRes.startsWith("```json") && chatRes.endsWith("```")) {
                        chatRes = chatRes.substring(7, chatRes.length() - 3);
                    }
                    if (JSON.isValid(chatRes)) {
                        JSONObject jsonObject = JSONObject.parseObject(chatRes);
                        JSONArray arr1 = jsonObject.getJSONArray("详细结果");
                        arr1.forEach(obj -> {
                            JSONObject o = (JSONObject) obj;
                            if (o.getBoolean("是否违规")) {
                                detailRes.add(o);
                            }
                        });
                        JSONArray arr2 = jsonObject.getJSONArray("跨记录违规组");
                        arr2.forEach(obj -> {
                            straddleRes.add((JSONObject) obj);
                        });
                    }
                    log.info("分析完成[{}]", System.currentTimeMillis());
                })
                .sequential()
                .blockLast();
        JSONObject returnResult = JSONObject.of("detailRes", detailRes, "straddleRes", straddleRes);
        System.out.println(returnResult);
        sw.stop();
        long millis = sw.getDuration().toMillis();
        System.out.println("耗时：" + millis);
        return R.ok(returnResult);
    }

    private static final Map<Integer, String> MAP = Map.of(
            1, "地点非饭店",
            2, "陪同人数超标",
            3, "超过用餐标准接待",
            4, "发票连号",
            5, "招待时间不同，开票时间相同且发票连号",
            6, "招待时间不同，开票时间不同，发票连号或相近，且金额相同"
    );

    public static void main(String[] args) throws IOException {
        String res1 = FileUtils.readFileToString(new File("D:/Desktop/spring-boot-init-template/src/main/java/top/sharehome/springbootinittemplate/controller/example/res1.json"), StandardCharsets.UTF_8.name());
        String res2 = FileUtils.readFileToString(new File("D:/Desktop/spring-boot-init-template/src/main/java/top/sharehome/springbootinittemplate/controller/example/res2.json"), StandardCharsets.UTF_8.name());
        String res3 = FileUtils.readFileToString(new File("D:/Desktop/spring-boot-init-template/src/main/java/top/sharehome/springbootinittemplate/controller/example/res3.json"), StandardCharsets.UTF_8.name());
        List<DetailRes> detailRes1 = getDetailRes(res1);
        List<StraddleRes> straddleRes1 = getStraddleRes(res1);
        ExcelUtils.exportLocalFile(detailRes1, "违规条例表", DetailRes.class, "D:/Desktop/spring-boot-init-template/src/main/java/top/sharehome/springbootinittemplate/controller/example/detailRes1.xlsx");
        ExcelUtils.exportLocalFile(straddleRes1, "跨组违规表", StraddleRes.class, "D:/Desktop/spring-boot-init-template/src/main/java/top/sharehome/springbootinittemplate/controller/example/straddleRes1.xlsx");

        List<DetailRes> detailRes2 = getDetailRes(res2);
        List<StraddleRes> straddleRes2 = getStraddleRes(res2);
        ExcelUtils.exportLocalFile(detailRes2, "违规条例表", DetailRes.class, "D:/Desktop/spring-boot-init-template/src/main/java/top/sharehome/springbootinittemplate/controller/example/detailRes2.xlsx");
        ExcelUtils.exportLocalFile(straddleRes2, "跨组违规表", StraddleRes.class, "D:/Desktop/spring-boot-init-template/src/main/java/top/sharehome/springbootinittemplate/controller/example/straddleRes2.xlsx");

        List<DetailRes> detailRes3 = getDetailRes(res3);
        List<StraddleRes> straddleRes3 = getStraddleRes(res3);
        ExcelUtils.exportLocalFile(detailRes3, "违规条例表", DetailRes.class, "D:/Desktop/spring-boot-init-template/src/main/java/top/sharehome/springbootinittemplate/controller/example/detailRes3.xlsx");
        ExcelUtils.exportLocalFile(straddleRes3, "跨组违规表", StraddleRes.class, "D:/Desktop/spring-boot-init-template/src/main/java/top/sharehome/springbootinittemplate/controller/example/straddleRes3.xlsx");
    }

    public static List<DetailRes> getDetailRes(String res) {
        return new ArrayList<>(JSONObject.parseObject(res).getList("detailRes", JSONObject.class)
                .stream()
                .map(obj -> {
                    DetailRes detailRes = new DetailRes();
                    detailRes.setId(obj.getString("报账单编号"));
                    detailRes.setIsError(obj.getBoolean("是否违规") ? "发现违规" : "为发现违规");
                    detailRes.setErrorType(String.join(" / ", obj.getList("违规类型", Integer.class).stream().map(MAP::get).toList()));
                    detailRes.setDetail(String.join(" / ", obj.getList("违规详情", String.class).stream().toList()));
                    return detailRes;
                })
                .toList());
    }

    public static List<StraddleRes> getStraddleRes(String res) {
        return new ArrayList<>(JSONObject.parseObject(res).getList("straddleRes", JSONObject.class)
                .stream()
                .map(obj -> {
                    StraddleRes straddleRes = new StraddleRes();
                    straddleRes.setErrorType(MAP.get(obj.getInteger("违规类型")));
                    straddleRes.setIds(String.join(" / ", obj.getList("涉及单据", String.class).stream().toList()));
                    straddleRes.setDetail(obj.getString("违规证据"));
                    return straddleRes;
                })
                .toList());
    }

}
