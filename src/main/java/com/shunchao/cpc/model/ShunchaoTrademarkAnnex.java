package com.shunchao.cpc.model;

import lombok.Data;

@Data
public class ShunchaoTrademarkAnnex {
    /**主键*/
    private String id;
    /**租户id*/
    private java.lang.Integer providerId;
    /**相对路径*/
    private String relativePath;
    /**附件编码*/
    private String annexCode;
    /**上传文件名称*/
    private String uploadName;
}
