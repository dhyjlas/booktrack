package com.kaworu.booktrack.controller;

import com.kaworu.booktrack.entity.Book;
import com.kaworu.booktrack.entity.Chapter;
import com.kaworu.booktrack.repository.BookRepository;
import com.kaworu.booktrack.repository.ChapterRepository;
import com.kaworu.booktrack.service.BaseCrawlService;
import com.kaworu.booktrack.service.BiqukanService;
import com.kaworu.booktrack.utils.ResponseResult;
import com.kaworu.booktrack.utils.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

//@RestController
public class FrController {
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private ChapterRepository chapterRepository;

    @GetMapping("fr")
    public ResponseResult fr() {
        BaseCrawlService service = (BiqukanService) SpringUtil.getBean("biqukanService");
        try {
            Book book = service.getBook("https://www.biqukan.com/0_319/");
            bookRepository.save(book);

            List<Chapter> chapters = service.getChapter(book);
            chapterRepository.saveAll(chapters);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ResponseResult.getSuccess();
    }

    @GetMapping("zj/{id}")
    public ResponseResult zj(@PathVariable long id) {
        BaseCrawlService service = (BiqukanService) SpringUtil.getBean("biqukanService");

        try {
            Chapter chapter = chapterRepository.findById(id).orElse(null);
            if(chapter != null) {
                String content = service.getContent(chapter.getUrl());
                chapter.setContent(content);
                chapter.setStatus(true);
                chapter.setUpdateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

                chapterRepository.save(chapter);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ResponseResult.getSuccess();
    }
}
