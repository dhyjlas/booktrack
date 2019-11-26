package com.kaworu.booktrack.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;

import java.io.IOException;
import java.util.List;

public class TestService{

    public static void main(String[] args) throws IOException {
        String url = "https://www.lingyu.org/wjsw/4/4394/";
        Document document = Jsoup.connect(url).timeout(10 * 1000)
                .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36")
                .get();

        JXDocument jxd = JXDocument.create(document);
        List<JXNode> jxNodes = jxd.selN("//div[@class='ml_list']/ul/li/a/text()");
        for(JXNode node : jxNodes){
            System.out.println(node.toString());
        }
        List<JXNode> jxNodes2 = jxd.selN("//div[@class='ml_list']/ul/li/a/@href");
        for(JXNode node : jxNodes2){
            System.out.println(node.toString());
        }
        List<JXNode> jxNodes3 = jxd.selN("//div[@class='introduce']/h1/text()");
        for(JXNode node : jxNodes3){
            System.out.println(node.toString());
        }
        List<JXNode> jxNodes4 = jxd.selN("//div[@class='introduce']/p[@class='bq']/span[2]/a/text()");
        for(JXNode node : jxNodes4){
            System.out.println(node.toString());
        }

        content();
    }

    public static void content() throws IOException {
        String url = "https://www.lingyu.org/wjsw/4/4394/24334655.html";
        Document document = Jsoup.connect(url).timeout(10 * 1000)
                .header("X-DevTools-Emulate-Network-Conditions-Client-Id", "8C56E9AF561964EA7FAE697309B115CE")
                .header("upgrade-insecure-requests", "1")
                .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36")
                .get();

        JXDocument jxd = JXDocument.create(document);
        List<JXNode> jxNodes = jxd.selN("//p[@id='articlecontent']");
        for(JXNode node : jxNodes){
            System.out.println(node.toString());
        }
    }
}
