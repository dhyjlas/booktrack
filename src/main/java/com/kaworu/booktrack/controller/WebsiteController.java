package com.kaworu.booktrack.controller;

import com.kaworu.booktrack.entity.TestDTO;
import com.kaworu.booktrack.entity.Website;
import com.kaworu.booktrack.exception.BusinessException;
import com.kaworu.booktrack.service.WebsiteService;
import com.kaworu.booktrack.utils.ResponseResult;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/website")
public class WebsiteController {
    @Autowired
    private WebsiteService websiteService;

    @Value("${website.manager.authorization}")
    private String websiteManagerAuthorization;

    @ApiOperation("获取站点列表")
    @GetMapping("/list")
    public ResponseResult list(@ApiParam("排序字段") @RequestParam(value = "sort", defaultValue = "id") String sort,
                               @ApiParam("排序类型") @RequestParam(value = "direction", defaultValue = "asc") String direction,
                               @ApiParam("查询") @RequestParam(value = "query", defaultValue = "") String query){
        Page<Website> bookPage;
        try {
            bookPage = websiteService.list(0, 1048575, sort, direction, query);
        }catch (Exception e){
            return ResponseResult.getFailure("查询失败");
        }

        return ResponseResult.getSuccess("查询成功").setData(bookPage);
    }

    @ApiOperation("获取单个站点")
    @GetMapping("/{id}")
    public ResponseResult list(@ApiParam("站点ID") @PathVariable("id") long id){
        Website website;
        try {
            website = websiteService.findById(id);
        }catch (Exception e){
            return ResponseResult.getFailure("查询失败");
        }

        return ResponseResult.getSuccess("查询成功").setData(website);
    }

    @ApiOperation("新增站点")
    @PostMapping("/save")
    public ResponseResult add(@RequestHeader(name = "Authorization") String authorization, @ApiParam("站点信息") @RequestBody Website website){
        try {
            if(!websiteManagerAuthorization.equals(authorization)){
                return ResponseResult.getFailure("抱歉，您没有管理权限，无法添加或修改站点信息，您可将需要添加的站点数据发送至邮箱：dhyjlas@163.com");
            }
            websiteService.save(website);
        }catch (BusinessException e){
            e.printStackTrace();
            return ResponseResult.getFailure(e.getMessage());
        }
        return ResponseResult.getSuccess("提交成功");
    }

    @ApiOperation("规则测试")
    @PostMapping("/test")
    public ResponseResult test(@ApiParam("站点信息") @RequestBody Website website){
        try {
            TestDTO testDTO = websiteService.test(website);
            return ResponseResult.getSuccess("测试完成").setData(testDTO);
        }catch (BusinessException e){
            e.printStackTrace();
            return ResponseResult.getFailure(e.getMessage());
        }
    }

    @ApiOperation("删除某个站点")
    @DeleteMapping("/{id}")
    public ResponseResult delete(@RequestHeader(name = "Authorization") String authorization, @ApiParam("站点ID") @PathVariable("id") long id){
        try {
            if(!websiteManagerAuthorization.equals(authorization)){
                return ResponseResult.getFailure("抱歉，您没有管理权限，无法添加或修改站点信息，您可将需要添加的站点数据发送至邮箱：dhyjlas@163.com");
            }
            websiteService.deleteById(id);
        }catch (Exception e){
            return ResponseResult.getFailure("删除失败");
        }

        return ResponseResult.getSuccess("删除成功");
    }
}
