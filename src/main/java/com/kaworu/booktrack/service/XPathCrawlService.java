package com.kaworu.booktrack.service;

import com.alibaba.fastjson.JSON;
import com.kaworu.booktrack.entity.Book;
import com.kaworu.booktrack.entity.Chapter;
import com.kaworu.booktrack.entity.Website;
import com.kaworu.booktrack.repository.BookRepository;
import com.kaworu.booktrack.repository.ChapterRepository;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.seimicrawler.xpath.JXDocument;
import org.seimicrawler.xpath.JXNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class XPathCrawlService{
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ChapterRepository chapterRepository;

    public final Pattern HTTP_PATTERN = Pattern.compile("(http|https):\\/\\/([\\w.]+\\/?)\\S*");

    /**
     * 获取图书基本信息
     * @param url
     * @param website
     * @return
     * @throws IOException
     */
    public Book getBook(String url, Website website) throws IOException {
        Document document = connAndGetDocument(url, website);
        Book book = bookRepository.findByUrl(url).orElse(new Book());

        //URL地址
        book.setUrl(url);
        //创建时间
        if(StringUtils.isEmpty(book.getCreateTime()))
            book.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        book.setUpdateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        //图书名
        book.setBookName(parseToString(document, website.getBookNameXpath()));
        //来源
        book.setSource(website.getId());
        //作者名
        book.setAuthor(parseToString(document, website.getAuthorXpath()));

        return book;
    }

    /**
     * 获取图书章节信息
     * @param book
     * @param website
     * @return
     * @throws IOException
     */
    public List<Chapter> getChapter(Book book, Website website) throws IOException {
        Document document = connAndGetDocument(book.getUrl(), website);

        List<Chapter> chapters = chapterRepository.findAllByBookId(book.getId());
        List<String> chapterNameList = parseToList(document, website.getChapterNameXpath());
        List<String> chapterUrlList = parseToList(document, website.getChapterUrlXpath());
        int count = Math.min(chapterNameList.size(), chapterUrlList.size());

        for(int i = 0 ; i < count ; i ++){
            String chapterName = chapterNameList.get(i);
            String chapterUrl = chapterUrlList.get(i);
            String url;
            //解析url
            if(HTTP_PATTERN.matcher(chapterUrl).matches()){
                url = chapterUrl;
            }else if("/".equals(chapterUrl.substring(0, 1))){
                String url0 = website.getHost().endsWith("/") ? website.getHost().substring(0, website.getHost().length() - 1) : website.getHost();
                url = url0 + chapterUrl;
            }else{
                String url0 = book.getUrl().endsWith("/") ? book.getUrl() : book.getUrl() + "/";
                url = url0 + chapterUrl;
            }

            Chapter chapter = null;
            for(Chapter chapterTmp : chapters){
                if(url.equals(chapterTmp.getUrl()))
                    chapter = chapterTmp;
            }

            if(chapter == null) {
                chapter = new Chapter();
                chapter.setUpdateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                chapters.add(chapter);
            }

            chapter.setBookId(book.getId());
            chapter.setBookName(book.getBookName());
            chapter.setUrl(url);
            chapter.setChapterName(chapterName);
            chapter.setStatus(false);
            chapter.setSource(book.getSource());
        }

        return chapters;
    }

    /**
     * 获取章节内容
     * @param url
     * @param website
     * @return
     * @throws IOException
     */
    public String getContent(String url, Website website) throws IOException {
        Document document = connAndGetDocument(url, website);

        String content = parseToString(document, website.getChapterContentXpath());

        String contentReplace = website.getContentReplace();
        if(!StringUtils.isEmpty(contentReplace)) {
            Map<String, String> map = (Map<String, String>) JSON.parse(contentReplace);
            for (String key : map.keySet()) {
                content = content.replaceAll(key, map.get(key));
            }
        }
        return content;
    }

    /**
     * 连接并获取数据
     * @param url
     * @param website
     * @return
     * @throws IOException
     */
    private Document connAndGetDocument(String url, Website website) throws IOException {
        Connection connection = Jsoup.connect(url).timeout(website.getMaxOutTime());
        String headerString = website.getHeader();
        if(!StringUtils.isEmpty(headerString)){
            Map<String, String> map = (Map<String, String>) JSON.parse(headerString);
            for(String key : map.keySet()){
                connection.header(key, map.get(key));
            }
        }
        return connection.get();
    }

    /**
     * 解析返回字符串
     * @param document
     * @param xpath
     * @return
     */
    private String parseToString(Document document, String xpath){
        if(StringUtils.isEmpty(xpath)){
            return "";
        }
        JXDocument jxd = JXDocument.create(document);
        List<JXNode> jxNodes = jxd.selN(xpath);
        for(JXNode node : jxNodes){
            return node.toString();
        }
        return "";
    }

    /**
     * 解析返回数组
     * @param document
     * @param xpath
     * @return
     */
    private List<String> parseToList(Document document, String xpath){
        JXDocument jxd = JXDocument.create(document);
        List<JXNode> jxNodes = jxd.selN(xpath);
        List<String> list = new ArrayList<>();
        for(JXNode node : jxNodes){
            list.add(node.toString());
        }
        return list;
    }

    /**
     * 规则测试
     * @param url
     * @param website
     * @param xpath
     * @return
     * @throws IOException
     */
    public String parseToListForString(String url, Website website, String xpath) throws IOException {
        if(StringUtils.isEmpty(xpath)){
            return "";
        }
        Document document = connAndGetDocument(url, website);
        List<String> list = parseToList(document, xpath);
        StringBuilder builder = new StringBuilder();
        String flag = "";
        for(String data : list){
            builder.append(flag).append(data);
            flag = "\n";
        }

        return builder.toString();
    }
}
