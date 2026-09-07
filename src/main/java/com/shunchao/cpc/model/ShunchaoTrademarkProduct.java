package com.shunchao.cpc.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ShunchaoTrademarkProduct {

    /**商品主类*/
    private String productMainCategory;

    /**序号*/
    private java.lang.String indexNumber;

    //编码
    private String scode;

    //名称
    private String sname;
}
