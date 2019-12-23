package com.kaworu.booktrack.controller;

import com.kaworu.booktrack.entity.Chapter;
import com.kaworu.booktrack.exception.BusinessException;
import com.kaworu.booktrack.service.ChapterService;
import com.kaworu.booktrack.utils.ResponseResult;
import com.kaworu.booktrack.utils.thread.PreloadingCallable;
import com.kaworu.booktrack.utils.thread.ThreadPoolUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/chapter")
public class ChapterController {
    @Autowired
    private ChapterService chapterService;

    @ApiOperation("获取图书章节")
    @GetMapping("/list")
    public ResponseResult list(@ApiParam("页码") @RequestParam(value = "page", defaultValue = "0") int page,
                              @ApiParam("每页数量") @RequestParam(value = "size", defaultValue = "50") int size,
                              @ApiParam("排序字段") @RequestParam(value = "sort", defaultValue = "id") String sort,
                              @ApiParam("排序类型") @RequestParam(value = "direction", defaultValue = "asc") String direction,
                              @ApiParam("图书名") @RequestParam(value = "bookId", defaultValue = "") long bookId){
        Page<Chapter> chapterPage;
        try {
            chapterPage = chapterService.list(page, size, sort, direction, bookId);
            long serial = 1;
            for (Chapter chapter : chapterPage.getContent()) {
                chapter.setContent("");
                chapter.setSerial((serial++) + chapterPage.getNumber() * chapterPage.getSize());
            }
        }catch (Exception e){
            e.printStackTrace();
            return ResponseResult.getFailure("查询失败");
        }

        return ResponseResult.getSuccess("查询成功").setData(chapterPage);
    }

    @ApiOperation("获取正文")
    @GetMapping("/content/{id}")
    public ResponseResult content(@ApiParam("章节ID") @PathVariable("id") long id){
        Chapter chapter;
        try {
            chapter = chapterService.content(id);
        } catch (BusinessException e){
            e.printStackTrace();
            return ResponseResult.getFailure(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseResult.getFailure("获取内容失败");
        }

        return ResponseResult.getSuccess("获取成功").setData(chapter);
    }

    @ApiOperation("重新正文")
    @GetMapping("/again/{id}")
    public ResponseResult again(@ApiParam("章节ID") @PathVariable("id") long id){
        Chapter chapter;
        try {
            chapter = chapterService.contentAgain(id);
        } catch (BusinessException e){
            e.printStackTrace();
            return ResponseResult.getFailure(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseResult.getFailure("获取内容失败");
        }

        return ResponseResult.getSuccess("已重新获取该章节内容").setData(chapter);
    }

    @ApiOperation("获取正文")
    @GetMapping("/content")
    public ResponseResult content(@ApiParam("章节序号") @RequestParam("serial") int serial,
                                  @ApiParam("图书ID") @RequestParam("bookId") long bookId){
        Chapter chapter;
        try {
            Page<Chapter> chapterPage = chapterService.list(serial, 1, "id", "asc", bookId);
            if(chapterPage.getContent() != null && chapterPage.getContent().size() > 0) {
                chapter = chapterPage.getContent().get(0);
                chapter = chapterService.content(chapter);

                //预加载
                ThreadPoolUtils.submit(new PreloadingCallable(serial + 1, bookId));
            }else{
                return ResponseResult.getFailure("获取内容失败");
            }
        } catch (BusinessException e){
            e.printStackTrace();
            return ResponseResult.getFailure(e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseResult.getFailure("获取内容失败");
        }

        return ResponseResult.getSuccess("获取成功").setData(chapter);
    }
}
