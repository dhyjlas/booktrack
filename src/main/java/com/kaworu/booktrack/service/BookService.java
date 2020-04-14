package com.kaworu.booktrack.service;

import com.kaworu.booktrack.entity.Book;
import com.kaworu.booktrack.entity.Chapter;
import com.kaworu.booktrack.entity.Website;
import com.kaworu.booktrack.exception.BusinessException;
import com.kaworu.booktrack.repository.BookRepository;
import com.kaworu.booktrack.repository.ChapterRepository;
import com.kaworu.booktrack.repository.WebsiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ChapterRepository chapterRepository;

    @Autowired
    private WebsiteRepository websiteRepository;

    @Autowired
    private XPathCrawlService xPathCrawlService;

    /**
     * 获取图书列表
     * @param page
     * @param size
     * @param sort
     * @param direction
     * @param query
     * @return
     */
    public Page<Book> list(int page, int size, String sort, String direction, String query){
        Sort sortDate = new Sort("desc".equals(direction) ? Sort.Direction.DESC : Sort.Direction.ASC, sort);
        Pageable pageable = PageRequest.of(page, size, sortDate);

        Specification<Book> example = (Specification<Book>) (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            //书名
            if(!StringUtils.isEmpty(query)){
                Predicate predicate = criteriaBuilder.like(root.get("bookName").as(String.class), "%" + query + "%");
                predicates.add(predicate);
            }
            //作者
            if(!StringUtils.isEmpty(query)){
                Predicate predicate = criteriaBuilder.like(root.get("author").as(String.class), "%" + query + "%");
                predicates.add(predicate);
            }

            if (predicates.size() == 0) {
                return null;
            }

            Predicate[] predicateArr = new Predicate[predicates.size()];
            predicateArr = predicates.toArray(predicateArr);

            return criteriaBuilder.or(predicateArr);
        };

        return bookRepository.findAll(example, pageable);
    }

    /**
     * 刷新图书信息
     * @param id
     * @throws IOException
     */
    @Transactional
    public int refresh(long id) throws IOException {
        Book book = bookRepository.findById(id).orElse(null);
        if(book == null)
            throw new BusinessException("找不到这本书了");
        Website website = websiteRepository.findById(book.getSource()).orElse(null);
        if(website == null)
            throw new BusinessException("找不到爬取规则了");

        String bookName = book.getBookName();
        String author = book.getAuthor();
        String url = book.getUrl();

        book = xPathCrawlService.getBook(book.getUrl(), website);

        List<Chapter> chapters = xPathCrawlService.getChapter(book, website);
        chapterRepository.saveAll(chapters);

        int addNum = chapters.size() - book.getChapters();
        book.setChapters(chapters.size());
        book.setBookName(bookName);
        book.setAuthor(author);
        book.setUrl(url);
        bookRepository.save(book);

        return addNum;
    }

    /**
     * 新增图书
     * @param url
     * @param website
     * @return
     */
    @Transactional
    public Book add(String url, Website website){
        try {
            Book book = xPathCrawlService.getBook(url, website);
            bookRepository.saveAndFlush(book);

            List<Chapter> chapters = xPathCrawlService.getChapter(book, website);
            chapterRepository.saveAll(chapters);

            book.setChapters(chapters.size());
            bookRepository.saveAndFlush(book);

            return book;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 获取图书信息
     * @param id
     * @return
     */
    public Book findById(long id){
        return bookRepository.findById(id).orElse(null);
    }

    /**
     * 保存图书信息
     * @param book
     */
    @Transactional
    public void save(Book book){
        bookRepository.save(book);
    }

    /**
     * 删除整本图书
     * @param id
     */
    @Transactional
    public void delete(long id){
        bookRepository.deleteById(id);
    }
}
