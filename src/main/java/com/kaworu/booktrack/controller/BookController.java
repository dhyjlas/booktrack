package com.kaworu.booktrack.controller;

import com.kaworu.booktrack.entity.Book;
import com.kaworu.booktrack.entity.Option;
import com.kaworu.booktrack.entity.Website;
import com.kaworu.booktrack.exception.BusinessException;
import com.kaworu.booktrack.service.BookService;
import com.kaworu.booktrack.service.ChapterService;
import com.kaworu.booktrack.service.WebsiteService;
import com.kaworu.booktrack.utils.ResponseResult;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/book")
public class BookController {
    @Autowired
    private BookService bookService;

    @Autowired
    private ChapterService chapterService;

    @Autowired
    private WebsiteService websiteService;

    @Value("${website.manager.authorization}")
    private String websiteManagerAuthorization;

    @ApiOperation("获取图书列表")
    @GetMapping("/list")
    public ResponseResult list(@ApiParam("排序字段") @RequestParam(value = "sort", defaultValue = "id") String sort,
                               @ApiParam("排序类型") @RequestParam(value = "direction", defaultValue = "asc") String direction,
                               @ApiParam("查询") @RequestParam(value = "query", defaultValue = "") String query){
        try {
            Page<Book> bookPage = bookService.list(0, 1048575, sort, direction, query);

            return ResponseResult.getSuccess("查询成功").setData(bookPage);
        }catch (Exception e){
            return ResponseResult.getFailure("查询失败");
        }
    }

    @ApiOperation("刷新图书")
    @PostMapping("/refresh")
    public ResponseResult refresh(@ApiParam("ID") @RequestParam("id") long id){
        int adds;
        try {
            adds = bookService.refresh(id);
        } catch (BusinessException e) {
            e.printStackTrace();
            return ResponseResult.getFailure(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseResult.getFailure("刷新失败");
        }

        if(adds > 0)
            return ResponseResult.getSuccess("刷新成功，新增" + adds + "章");
        else
            return ResponseResult.getSuccess("刷新成功，没有新的章节");
    }

    @ApiOperation("新增图书")
    @PostMapping("/add")
    public ResponseResult add(@ApiParam("URL") @RequestBody Book book){
        try {
            Website website = null;
            if (book.getSource() == 0) {
                website = websiteService.analysisUrl(book.getUrl());
            }else{
                website = websiteService.findById(book.getSource());
            }
            bookService.add(book.getUrl(), website);
        }catch (MalformedURLException e){
            e.printStackTrace();
            return ResponseResult.getFailure("网址解析失败");
        }catch (BusinessException e){
            e.printStackTrace();
            return ResponseResult.getFailure(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResponseResult.getFailure("新增失败");
        }
        return ResponseResult.getSuccess("图书已更新成功");
    }

    @ApiOperation("获取所有支持的网站")
    @GetMapping("/website")
    public ResponseResult website(){
        List<Option> websites = new ArrayList<>();
        List<Website> websiteList = websiteService.getWebsites();
        for(Website website : websiteList){
            websites.add(new Option(website.getId(), website.getName()));
        }

        return ResponseResult.getSuccess("获取成功").setData(websites);
    }

    @ApiOperation("获取图书信息")
    @GetMapping("/info/{id}")
    public ResponseResult again(@ApiParam("图书ID") @PathVariable("id") long id){
        try {
            Book book = bookService.findById(id);

            return ResponseResult.getSuccess("获取成功").setData(book);
        } catch (BusinessException e){
            e.printStackTrace();
            return ResponseResult.getFailure(e.getMessage());
        }
    }

    @ApiOperation("保存图书信息")
    @PostMapping("/save")
    public ResponseResult save(@RequestHeader(name = "Authorization") String authorization, @ApiParam("图书信息") @RequestBody Book book){
        try {
            if(!websiteManagerAuthorization.equals(authorization)){
                return ResponseResult.getFailure("抱歉，您没有管理权限，无法修改图书信息");
            }

            Book book1 = bookService.findById(book.getId());
            if(book1 == null){
                return ResponseResult.getFailure("保存失败，找不到图书ID");
            }
            book1.setBookName(book.getBookName());
            book1.setAuthor(book.getAuthor());
            book1.setUrl(book.getUrl());
            book1.setSource(book.getSource());
            bookService.save(book1);

            return ResponseResult.getSuccess("保存成功");
        }catch (BusinessException e){
            e.printStackTrace();
            return ResponseResult.getFailure(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResponseResult.getFailure("保存失败");
        }
    }

    @ApiOperation("删除整本图书")
    @DeleteMapping("/all/{id}")
    public ResponseResult save(@RequestHeader(name = "Authorization") String authorization, @ApiParam("图书ID") @PathVariable("id") long id){
        try {
            if(!websiteManagerAuthorization.equals(authorization)){
                return ResponseResult.getFailure("抱歉，您没有管理权限，无法删除该本图书");
            }

            Book book = bookService.findById(id);
            if(book == null){
                return ResponseResult.getFailure("删除失败，找不到图书ID");
            }

            chapterService.deleteByBookId(id);
            bookService.delete(id);

            return ResponseResult.getSuccess("删除成功");
        }catch (BusinessException e){
            e.printStackTrace();
            return ResponseResult.getFailure(e.getMessage());
        }catch (Exception e){
            e.printStackTrace();
            return ResponseResult.getFailure("删除失败");
        }
    }
}
