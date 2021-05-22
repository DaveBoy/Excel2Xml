package com.dave.excel;

import com.alibaba.excel.EasyExcel;
import com.dave.excel.listenner.NoModel2XmlDataListener;

public class Main {
    public static void main(String[] args){
        String fileName = "C:\\Users\\Administrator\\Desktop\\translation_languages_20210518_修订.xlsx";
        EasyExcel.read(fileName, new NoModel2XmlDataListener(fileName)).sheet().doRead();
    }
}
