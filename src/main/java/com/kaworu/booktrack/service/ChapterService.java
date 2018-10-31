package com.kaworu.booktrack.service;

import com.kaworu.booktrack.entity.Chapter;
import com.kaworu.booktrack.exception.BusinessException;
import com.kaworu.booktrack.repository.ChapterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class ChapterService {
    @Autowired
    private ChapterRepository chapterRepository;

    @Autowired
    private FactoryService factoryService;

    /**
     * 获取图书章节
     * @param page
     * @param size
     * @param sort
     * @param direction
     * @param bookId
     * @return
     */
    public Page<Chapter> list(int page, int size, String sort, String direction, long bookId){
        Sort sortDate = new Sort("desc".equals(direction) ? Sort.Direction.DESC : Sort.Direction.ASC, sort);
        Pageable pageable = PageRequest.of(page, size, sortDate);

        return chapterRepository.findAllByBookId(bookId, pageable);
    }

    /**
     * 通过章节ID获取对应内容
     * @param id
     * @return
     * @throws IOException
     */
    public Chapter content(long id) throws IOException {
        Chapter chapter = chapterRepository.findById(id).orElse(null);
        if(chapter == null){
            throw new BusinessException("找不到对应章节");
        }

        return content(chapter);
    }

    /**
     * 通过页面序号和图书ID获取对应内容
     * @param chapter
     * @return
     * @throws IOException
     */
    @Transactional
    public Chapter content(Chapter chapter) throws IOException {
        if(!chapter.isStatus()){
            BaseCrawlService service = factoryService.getBean(chapter.getSource());
            if(service == null)
                throw new BusinessException("数据来源错误");
            String content = service.getContent(chapter.getUrl());
            chapter.setContent(content);
            chapter.setStatus(true);
            chapter.setUpdateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            chapterRepository.saveAndFlush(chapter);
            return chapter;
        }

        return chapter;
    }
}
