package com.kaworu.booktrack.utils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "请求结果响应体")
public class ResponseResult implements Serializable {

    @ApiModelProperty(value = "响应状态回执码")
    private Integer status;

    @ApiModelProperty(value = "响应回执消息")
    private String msg;

    @ApiModelProperty(value = "数据体")
    private Object data;

    @ApiModelProperty(value = "响应时间戳")
    private final long timestamps = System.currentTimeMillis();

    public ResponseResult(Integer status){
        this.status = status;
    }

    public ResponseResult(Integer status, String msg){
        this.status = status;
        this.msg = msg;
    }

    public static ResponseResult getSuccess(){
        return new ResponseResult(200);
    }

    public static ResponseResult getFailure(){
        return new ResponseResult(5000);
    }

    public static ResponseResult getSuccess(String msg){
        return new ResponseResult(200, msg);
    }

    public static ResponseResult getFailure(String msg){
        return new ResponseResult(500, msg);
    }

    public ResponseResult setData(Object data){
        this.data = data;
        return this;
    }
}
