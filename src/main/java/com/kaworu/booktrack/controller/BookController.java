package com.kaworu.booktrack.controller;

import com.kaworu.booktrack.entity.Book;
import com.kaworu.booktrack.entity.Option;
import com.kaworu.booktrack.entity.Website;
import com.kaworu.booktrack.exception.BusinessException;
import com.kaworu.booktrack.service.BookService;
import com.kaworu.booktrack.utils.ResponseResult;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/book")
public class BookController {
    @Autowired
    private BookService bookService;

    @ApiOperation("获取图书列表")
    @GetMapping("/list")
    public ResponseResult list(@ApiParam("排序字段") @RequestParam(value = "sort", defaultValue = "id") String sort,
                               @ApiParam("排序类型") @RequestParam(value = "direction", defaultValue = "asc") String direction,
                               @ApiParam("查询") @RequestParam(value = "query", defaultValue = "") String query){
        Page<Book> bookPage;
        try {
            bookPage = bookService.list(0, 1048575, sort, direction, query);
        }catch (Exception e){
            return ResponseResult.getFailure("查询失败");
        }

        return ResponseResult.getSuccess("查询成功").setData(bookPage);
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
                website = bookService.analysisUrl(book.getUrl());
            }else{
                website = bookService.findById(book.getSource());
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
        List<Website> websiteList = bookService.getWebsites();
        for(Website website : websiteList){
            websites.add(new Option(website.getId(), website.getName()));
        }

        return ResponseResult.getSuccess("获取成功").setData(websites);
    }
}
