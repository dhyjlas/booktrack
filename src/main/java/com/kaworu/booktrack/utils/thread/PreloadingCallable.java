package com.kaworu.booktrack.utils.thread;

import com.kaworu.booktrack.entity.Chapter;
import com.kaworu.booktrack.service.ChapterService;
import com.kaworu.booktrack.utils.SpringUtil;
import org.springframework.data.domain.Page;

import java.util.concurrent.Callable;

/**
 * 预加载
 */
public class PreloadingCallable implements Callable {
    private int serial;
    private long bookId;

    public PreloadingCallable(int serial, long bookId){
        this.serial = serial;
        this.bookId = bookId;
    }

    @Override
    public Object call() throws Exception {
        ChapterService chapterService = (ChapterService) SpringUtil.getBean("chapterService");
        Page<Chapter> chapterPage = chapterService.list(serial, 1, "id", "asc", bookId);
        if(chapterPage.getContent() != null && chapterPage.getContent().size() > 0)
            chapterService.content(chapterPage.getContent().get(0));
        return null;
    }
}
