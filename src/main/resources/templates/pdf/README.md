### 存放PDF模板的目录

##### 注意1：不要删除fop.xconf，这个是PdfUtils所依赖的FOP配置文件

##### 注意2：SourceHanSansCN-Regular.ttf是思源黑体字体文件，也是该模板项目默认的PDF文件输出字体，如需更换，请连同PdfUtils.java文件静态代码块以及fop.xconf相关内容一起更改

##### 注意3：template.fo是PdfUtils.Template所需要的模板，支持Freemarker、Thymeleaf以及Jte三类数据源，XSL-FO以及三类数据源的具体用法请自行前往官网学习