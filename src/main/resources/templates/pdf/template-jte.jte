<%--不能留空行--%>
@import java.util.List
@param List<String> list
@param String str
<?xml version="1.0" encoding="utf-8"?>
<!--根标签-->
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format"
         xmlns:fox="http://xmlgraphics.apache.org/fop/extensions"
         xmlns:xe="http://www.x-easypdf.cn/ns"
         xmlns:svg="http://www.w3.org/2000/svg">
    <!--页面模板-->
    <fo:layout-master-set>
        <!--单页面模板-->
        <fo:simple-page-master master-name="A4">
            <!--页面区域主体-->
            <fo:region-body/>
        </fo:simple-page-master>
    </fo:layout-master-set>
    <!--页面序列-->
    <fo:page-sequence master-reference="A4">
        <!--页面流-->
        <fo:flow flow-name="xsl-region-body">
            @for(String data : list)
            <fo:block>${data}</fo:block>
            @endfor
            @if(str!=null)
            <fo:block>${str}</fo:block>
            @else
            <fo:block>为空</fo:block>
            @endif
        </fo:flow>
    </fo:page-sequence>
</fo:root>
