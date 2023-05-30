package com.shunchao.cpc.model;

import lombok.Data;

@Data
public class ShunchaoTrademarkApplicant {


    /*申请人类型 0法人或其他组织1自然人*/
    private String applicantType;

    /*书式类型 0中国大陆1中国台湾2中国香港3中国澳门4国外*/
    private String bookOwnerType;

    /*申请人名称(中文)/共有人*/
    private String applicantName;

    /*统一社会信用代码*/
    private String unifiedSocialCreditcode;

    /*证件名称 0身份证1护照2其他*/
    private String idName;

    /*证件号码*/
    private String idNumber;

    /*申请人地址 国家省市县区*/
    private String applicationAddres;

    /*申请人地址(英文)*/
    private String applicationAddressEnglish;

    /*申请人国内接收人名称*/
    private String applicationMainlandRecipientname;

    /*接收人地址*/
    private String recipientAddress;

    /*接收人邮编*/
    private String recipientPostcode;

    /*传真（含地区号）*/
    private String faxInAreacode;

    /*邮政编码（申请人）*/
    private String postalCode;

    /*申请人名称(英文)*/
    private String applicantOwnerEnglishname;

    /*国家或地区*/
    private String countryArea;

    /*证明文件原件是否为中文 0否1是*/
    private String whetherOriginaldocChinese;

    /*身份证明文件(中文)*/
    private String sba0025;
    /*身份证明原文件(外文)*/
    private String sba0026;
    /*主体资格证明文件(中文)*/
    private String sba0027;
    /*主体资格证明原文件(外文)*/
    private String sba0028;
    /*商标代理机构PIN码*/
    private String tmsvePin;
    /*商标申请号*/
    private String applyNumber;
}
