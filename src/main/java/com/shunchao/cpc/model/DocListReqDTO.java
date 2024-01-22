package com.shunchao.cpc.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class DocListReqDTO {

    private static final long serialVersionUID = 1L;

    private String fid;
    @ApiModelProperty("申请类型(1:国家申请,2:PTC国际阶段,3:外观海牙)")
    private String system;
    @ApiModelProperty("申请服务类型：cn:国家;pct:PCT国际;hague:海牙;fswx:复审无效")
    private String reqType;
    @ApiModelProperty("搜索框申请号")
    private String applicationnumber;
    @ApiModelProperty("列表申请号")
    private String cnApplicationnumber;
    @ApiModelProperty("官文类型:0：通知书和回执；1：回执；2：通知书")
    private String type;
    @ApiModelProperty("文件代码")
    private String code;
    @ApiModelProperty("账户")
    private String account;
    @ApiModelProperty("是否已下载")
    private String isDownload;
    @ApiModelProperty("发文起始日期")
    private String fawenrStart;
    @ApiModelProperty("发文截止日期")
    private String fawenrEnd;
    @ApiModelProperty("发文序号")
    private String fawenxlh;
    @ApiModelProperty("通知书名称")
    private String tongzhismc;
    @ApiModelProperty("发明创造名称")
    private String famingczmc;
    @ApiModelProperty("国际申请号")
    private String guojisqh;
    private String dljgnbbh;
    @ApiModelProperty("页数")
    private String current;
    @ApiModelProperty("每页数量")
    private String size;
    @ApiModelProperty("下载次数")
    private String xiazaizt;
    @ApiModelProperty("发文日期")
    private String dianzifwrq;
    @ApiModelProperty("编号")
    private String name;

}
