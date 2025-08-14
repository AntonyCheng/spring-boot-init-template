package top.sharehome.springbootinittemplate.ai;

import com.alibaba.fastjson.JSON;
import org.elasticsearch.core.Map;
import org.springframework.ai.document.Document;
import top.sharehome.springbootinittemplate.config.ai.spring.etl.reader.DocumentReader;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class DocumentReaderTest {

    private final static String PROJECT_PATH = System.getProperty("user.dir");

    public static void main(String[] args) throws FileNotFoundException {
        ByteArrayInputStream inputStream1 = new ByteArrayInputStream(JSON.toJSONString(Map.of("name", "AntonyCheng", "age", "18")).getBytes());
        List<Document> documents1 = DocumentReader.readJson(inputStream1, "name", "age");
        documents1.forEach(document -> System.out.println(document.getText()));

        ByteArrayInputStream inputStream2 = new ByteArrayInputStream("""
                晨光初露时，山谷中还弥漫着一层薄雾，仿佛天地尚未完全苏醒。远处的山峰在淡青色的天幕下若隐若现，像是被水墨晕染过的画卷。林间的鸟鸣声此起彼伏，清脆悦耳，打破了夜的沉寂。阳光从东边缓缓升起，金色的光线穿透树梢，洒落在湿润的苔藓上，泛起微弱的光斑。溪水潺潺流淌，带着昨夜雨水的清凉，在岩石间跳跃，发出叮咚作响的声音。空气中夹杂着泥土与草叶混合的气息，清新而纯净。偶尔有松鼠跃过枝头，惊落几滴露珠，坠入溪流，激起一圈圈细小的涟漪。
                城市的夜从来不眠。霓虹灯在高楼大厦之间交织成一片光海，五彩斑斓的广告牌映照着街道，像一场永不落幕的幻梦。车流如织，红色尾灯连成一条蜿蜒的长龙，在玻璃幕墙的倒影中流动。地铁站口人来人往，西装革履的上班族拖着疲惫的步伐，匆匆赶向末班车；街头艺人抱着吉他轻声吟唱，旋律飘散在夜风中，混入人群的喧嚣。咖啡馆内灯光柔和，几位年轻人围坐在角落低声交谈，笑声偶尔溢出。夜越深，城市的节奏却似乎愈发繁忙，仿佛每一盏亮着的窗户背后，都藏着一个未完的故事。
                他坐在藤椅上，背靠着老旧的砖墙，手里握着一只已经掉漆的搪瓷杯。岁月在他的脸上留下深深的痕迹，皱纹如同干涸河流的沟壑，静静地诉说着一生的风雨。花白的头发稀疏而柔软，随风轻轻摆动。他的眼神温和而深远，仿佛能看透时光，却又带着一丝不易察觉的孤独。每当他说话，声音低沉缓慢，像是从遥远的地方传来，带着某种让人心安的力量。他时常沉默地看着门前的小路，仿佛在等待某个永远不会回来的人，又仿佛只是享受这份宁静与回忆交织的片刻。
                """.getBytes());
        List<Document> documents2 = DocumentReader.readText(inputStream2);
        documents2.forEach(document -> System.out.println(document.getText()));

        ByteArrayInputStream inputStream3 = new ByteArrayInputStream("""
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <title>My Web Page</title>
                    <meta name="description" content="A sample web page for Spring AI">
                    <meta name="keywords" content="spring, ai, html, example">
                    <meta name="author" content="John Doe">
                    <meta name="date" content="2024-01-15">
                    <link rel="stylesheet" href="style.css">
                </head>
                <body>
                    <header>
                        <h1>Welcome to My Page</h1>
                    </header>
                    <nav>
                        <ul>
                            <li><a href="/">Home</a></li>
                            <li><a href="/about">About</a></li>
                        </ul>
                    </nav>
                    <article>
                        <h2>Main Content</h2>
                        <p>This is the main content of my web page.</p>
                        <p>It contains multiple paragraphs.</p>
                        <a href="https://www.example.com">External Link</a>
                    </article>
                    <footer>
                        <p>&copy; 2024 John Doe</p>
                    </footer>
                </body>
                </html>
                """.getBytes());
        List<Document> documents3 = DocumentReader.readHtml(inputStream3, "body", StandardCharsets.ISO_8859_1.name());
        documents3.forEach(document -> System.out.println(document.getText()));

        ByteArrayInputStream inputStream4 = new ByteArrayInputStream("""
                This is a Java sample application:
                                
                ```java
                package com.example.demo;
                                
                import org.springframework.boot.SpringApplication;
                import org.springframework.boot.autoconfigure.SpringBootApplication;
                                
                @SpringBootApplication
                public class DemoApplication {
                    public static void main(String[] args) {
                        SpringApplication.run(DemoApplication.class, args);
                    }
                }
                ```
                                
                Markdown also provides the possibility to `use inline code formatting throughout` the entire sentence.
                                
                ---
                                
                Another possibility is to set block code without specific highlighting:
                                
                ```
                ./mvnw spring-javaformat:apply
                ```
                """.getBytes());
        List<Document> documents4 = DocumentReader.readMarkdown(inputStream4, false, false, false);
        documents4.forEach(document -> System.out.println(document.getText()));

        FileInputStream inputStream5 = new FileInputStream(PROJECT_PATH + "\\src\\test\\java\\top\\sharehome\\springbootinittemplate\\ai\\file\\test.pdf");
        List<Document> documents5 = DocumentReader.readPdf(inputStream5);
        documents5.forEach(document -> System.out.println(document.getText()));

        FileInputStream inputStream6 = new FileInputStream(PROJECT_PATH + "\\src\\test\\java\\top\\sharehome\\springbootinittemplate\\ai\\file\\test.pdf");
        List<Document> documents6 = DocumentReader.readTika(inputStream6);
        documents6.forEach(document -> System.out.println(document.getText()));
        FileInputStream inputStream7 = new FileInputStream(PROJECT_PATH + "\\src\\test\\java\\top\\sharehome\\springbootinittemplate\\ai\\file\\test.docx");
        List<Document> documents7 = DocumentReader.readTika(inputStream7);
        documents7.forEach(document -> System.out.println(document.getText()));
    }

}
