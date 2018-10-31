package com.kaworu.booktrack.service;

import com.kaworu.booktrack.entity.Book;
import com.kaworu.booktrack.entity.Chapter;

import java.io.IOException;
import java.util.List;

public interface BaseCrawlService {
    Book getBook(String url) throws IOException;
    List<Chapter> getChapter(Book book) throws IOException;
    String getContent(String url) throws IOException;
}
