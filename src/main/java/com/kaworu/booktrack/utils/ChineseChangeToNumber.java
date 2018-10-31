package com.kaworu.booktrack.utils;

public class ChineseChangeToNumber {
    public static int chineseToNumber(String str){
        String str1 = new String();
        String str2 = new String();
        String str3 = new String();
        int k = 0;
        boolean dealflag = true;

        for(int i=0;i<str.length();i++){
            if(!isNumber(str.charAt(i))){
                str = str.substring(0, i) + str.substring(i+1);
            }else
                break;
        }

        for(int i=0;i<str.length();i++){
            if(!isNumber(str.charAt(i))){
                str = str.substring(0, i);
                break;
            }
        }

        for(int i=0;i<str.length();i++){//先把字符串中的“零”除去
            if('零' == (str.charAt(i))){
                str = str.substring(0, i) + str.substring(i+1);
            }
        }
        String chineseNum = str;
        for(int i=0;i<chineseNum.length();i++){
            if(chineseNum.charAt(i) == '亿'){
                str1 = chineseNum.substring(0,i);//截取亿前面的数字，逐个对比表格。然后转换
                k = i+1;
                dealflag = false;//已经处理
            }
            if(chineseNum.charAt(i) == '万'){
                str2 = chineseNum.substring(k,i);
                str3 = str.substring(i+1);
                dealflag = false;//已经处理
            }
        }
        if(dealflag){//假设没有处理
            str3 =  chineseNum;
        }
        int result = sectionChinese(str1) * 100000000 +
                sectionChinese(str2) * 10000 + sectionChinese(str3);
        return result;
    }

    public static boolean isNumber(char ch){
        for(char c : Tool.chnNumChinese){
            if(ch == c)
                return true;
        }
        for(char c : Tool.chnUnitChar){
            if(ch == c)
                return true;
        }
        return false;
    }

    public static int sectionChinese(String str){
        int value = 0;
        int sectionNum = 1;
        for(int i=0;i<str.length();i++){
            int v = (int) Tool.intList.get(str.charAt(i));
            if( v == 10 || v == 100 || v == 1000 ){//假设数值是权位则相乘
                sectionNum = v * sectionNum;
                value = value + sectionNum;
                sectionNum = 1;
            }else if(i == str.length()-1){
                value = value + v;
            }else{
                sectionNum = v;
            }
        }
        return value;
    }

    public static int number(String chapterName){

        chapterName = chapterName.replaceAll("两", "二");
        chapterName = chapterName.replaceAll("干", "千");
        chapterName = chapterName.replaceAll("张", "章");

        int chapterId;
        String chapterNameTmp;
        int begin;
        int end;

        try {
            end = chapterName.indexOf("章");
            if(end > -1) {
                chapterNameTmp = chapterName.substring(0, end);
                begin = chapterNameTmp.lastIndexOf("第");
                if(begin > -1){
                    chapterNameTmp = chapterNameTmp.substring(begin + 1, chapterNameTmp.length());
                }
            }else{
                chapterNameTmp = chapterName;
            }
        }catch (Exception e) {
            chapterNameTmp = chapterName;
        }

        try {
            chapterId = Integer.valueOf(chapterNameTmp);
            return chapterId;
        } catch (Exception e) {

        }

        try{
            chapterId = ChineseChangeToNumber.chineseToNumber(chapterNameTmp);

        }catch (Exception e){
            try{
                chapterId = ChineseChangeToNumber.chineseToNumber(chapterNameTmp);
            }catch (Exception ee){
                ee.printStackTrace();
                return 0;
            }
        }

        return chapterId;
    }

    public static void main(String[] args){
        System.out.println(ChineseChangeToNumber.number("第九章 象甲功"));
        System.out.println(ChineseChangeToNumber.number("第一干一百一十八章空间神通"));
    }
}