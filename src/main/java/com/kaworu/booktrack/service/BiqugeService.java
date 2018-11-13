package com.kaworu.booktrack.service;

import com.kaworu.booktrack.entity.Book;
import com.kaworu.booktrack.entity.Chapter;
import com.kaworu.booktrack.repository.BookRepository;
import com.kaworu.booktrack.repository.ChapterRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 笔趣阁爬取
 */
@Service
public class BiqugeService implements BaseCrawlService{
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ChapterRepository chapterRepository;

    /**
     * 获取图书基本信息
     * @param url
     * @return
     * @throws IOException
     */
    @Override
    public Book getBook(String url) throws IOException {
        Document document = Jsoup.connect(url)
                .header("X-DevTools-Emulate-Network-Conditions-Client-Id", "8C56E9AF561964EA7FAE697309B115CE")
                .header("upgrade-insecure-requests", "1")
                .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36")
                .get();

        Book book = bookRepository.findByUrl(url).orElse(new Book());

        //URL地址
        book.setUrl(url);
        //创建时间
        if(StringUtils.isEmpty(book.getCreateTime()))
            book.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        book.setUpdateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        //图书名
        book.setBookName(document.body().select("[id=info]").select("h1").text());
        //作者名
        String author = document.body().select("[id=info]").select("p").first().text().replaceAll(" ","").replaceAll("作者：","");
        book.setSource("2");
        book.setAuthor(author);

        return book;
    }

    /**
     * 获取图书章节信息
     * @param book
     * @return
     * @throws IOException
     */
    @Override
    public List<Chapter> getChapter(Book book) throws IOException {
        Document document = Jsoup.connect(book.getUrl())
                .header("X-DevTools-Emulate-Network-Conditions-Client-Id", "8C56E9AF561964EA7FAE697309B115CE")
                .header("upgrade-insecure-requests", "1")
                .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36")
                .get();

        List<Chapter> chapters = chapterRepository.findAllByBookId(book.getId());

        Elements elements = document.body().select("div[class=box_con]").select("dd");
        for (Element e : elements) {
            String url = "http://www.biquge.com.tw" + e.select("a").attr("href");
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
            chapter.setChapterName(e.text());
//            chapter.setChapterId(ChineseChangeToNumber.number(e.text()));
            chapter.setChapterId(Integer.valueOf(url.substring(url.lastIndexOf("/") + 1, url.lastIndexOf(".html"))));
            chapter.setStatus(false);
            chapter.setSource(book.getSource());
        }

        return chapters;
    }

    /**
     * 获取章节内容
     * @param url
     * @return
     * @throws IOException
     */
    @Override
    public String getContent(String url) throws IOException {
        Document document = Jsoup.connect(url)
                .header("X-DevTools-Emulate-Network-Conditions-Client-Id","8C56E9AF561964EA7FAE697309B115CE")
                .header("upgrade-insecure-requests","1")
                .header("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36")
                .get();
        String content = document.body().select("div[id=content]").toString();
        return content;
    }

    public static void main(String[] args) throws IOException {
        System.out.println(URLEncoder.encode("无限恐怖","gbk"));
        System.out.println(URLDecoder.decode("%CE%DE%CF%DE%BF%D6%B2%C0","gbk"));
    }
}
