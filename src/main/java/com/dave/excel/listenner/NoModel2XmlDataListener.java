package com.dave.excel.listenner;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 直接用map接收数据
 *
 * @author Jiaju Zhuang
 */
public class NoModel2XmlDataListener extends AnalysisEventListener<Map<Integer, String>> {

    /**
     * 每隔5条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
     */
    private Map<Integer, String> headMap = new HashMap<>();
    List<Map<Integer, String>> list = new ArrayList<Map<Integer, String>>();
    private String xmlPath;

    public NoModel2XmlDataListener(String fileName) {
        xmlPath = new File(fileName).getParentFile().getPath() + File.separator + "strings" + File.separator;
    }

    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        System.out.println("解析到一条数据:" + JSON.toJSONString(data));
        list.add(data);
    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        super.invokeHeadMap(headMap, context);
        this.headMap = new HashMap<>(headMap);
        System.out.println("invokeHeadMap：解析到一条数据:" + JSON.toJSONString(headMap));

    }

    public void doAfterAllAnalysed(AnalysisContext context) {
        save2Xml();
        System.out.println("所有数据解析完成！");
    }

    /**
     * 加上存储数据库
     */
    private void save2Xml() {
        System.out.println(list.size() + "条数据，开始存储数据库！");
        int size = headMap.size();
        for (int index = 1; index < size; index++) {
            System.out.println("开始写入head：" + headMap.get(index));

            try {


                int temIndex = index;
                Document document = DocumentHelper.createDocument();
                Element root = document.addElement("resources");

                list.forEach(action -> {
                    //创建root
                    //生成root的一个接点
                    Element param = root.addElement("string");
                    // 为节点添加属性
                    param.addAttribute("name", action.get(0));
                    // 为节点添加文本, 也可以用addText()
                    param.addText(action.get(temIndex));
                });
                //设置文件编码
                OutputFormat xmlFormat = new OutputFormat();
                xmlFormat.setEncoding("UTF-8");
                // 设置换行
                xmlFormat.setNewlines(true);
                // 生成缩进
                xmlFormat.setIndent(true);
                // 使用4个空格进行缩进, 可以兼容文本编辑器
                xmlFormat.setIndent("    ");
                String filePath = xmlPath + "value-" + headMap.get(index) + "/" + "strings.xml";
                File file = new File(filePath).getParentFile();
                if (!file.exists()) {
                    file.mkdirs();
                }
                //创建写文件方法
                XMLWriter xmlWriter = new XMLWriter(new FileWriter(filePath), xmlFormat);
                //写入文件
                xmlWriter.write(document);
                //关闭
                xmlWriter.close();
                System.out.println("成功写入head：" + headMap.get(index));
            } catch (Exception e) {
                System.out.println(headMap.get(index) + "写入失败！");
                e.printStackTrace();
            }
        }


        System.out.println("存储数据成功！:" + xmlPath);
    }


}