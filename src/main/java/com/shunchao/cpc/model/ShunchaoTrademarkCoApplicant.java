package com.shunchao.cpc.model;

import lombok.Data;

@Data
public class ShunchaoTrademarkCoApplicant {

    /*共有人类型 0法人或其他组织1自然人*/
    private String applicantType;

    /*共有人国籍 0中国大陆1中国台湾2中国香港3中国澳门4国外*/
    private String bookOwnerType;

    /*共有人名称中文*/
    private String applicantName;

    /*主体资格证明文件*/
    private String sba0027;

    /*证明文件原件是否为中文*/
    private String whetherOriginaldocChinese;

    /*主体资格证明文件(外文)*/
    private String sba0028;

    /*共有人名称英文*/
    private String applicantOwnerEnglishname;

    /*证件名称*/
    private String idName;

    /*证件号*/
    private String idNumber;

    /*身份证明文件*/
    private String sba0025;

    /*身份证明文件（外文）*/
    private String sba0026;

}
