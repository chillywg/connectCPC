package com.shunchao.cpc.model;

import lombok.Data;

import java.util.List;

@Data
public class ShunchaoTrademarkApplicantProduct {


    /*申请人类型 0法人或其他组织1自然人*/
    private String applicantType;

    /*书式类型 0中国大陆1中国台湾2中国香港3中国澳门4国外*/
    private String bookOwnerType;

    /*代理文号*/
    private String agentNumber;

    /*代理人姓名*/
    private String agentName;

    /*代理委托书 路径*/
    private List<ShunchaoTrademarkPow> sba0023;

    /*申请人名称(中文)/共有人*/
    private String applicantName;

    /*统一社会信用代码*/
    private String unifiedSocialCreditcode;


    /*证件名称 0身份证1护照2其他*/
    private String idName;

    /*证件号码*/
    private String idNumber;

    /*身份证明文件(中文)*/
    private String sba0025;

    /*主体资格证明文件(中文)*/
    private String sba0027;

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

    /*联系人*/
    private String contactPerson;

    /*联系电话*/
    private String contactNumber;

    /*传真（含地区号）*/
    private String faxInAreacode;

    /*邮政编码*/
    private String postalCode;

    /*申请人名称(英文)*/
    private String applicantOwnerEnglishname;

    /*主体资格证明原文件(外文)*/
    private String sba0028;

    /*国家或地区*/
    private String countryArea;

    /*证明文件原件是否为中文 0否1是*/
    private String whetherOriginaldocChinese;

    /*身份证明原文件(外文)*/
    private String sba0026;


    /*商标类型 1一般2集体3证明*/
    private String trademarkType;

    /*是否地理标志 0否1是*/
    private String whetherGeographicalIndication;

    /*是否三维标志*/
    private String whetherThreedimensionLogo;

    /*商标使用管理规则*/
    private String certificationManagementRulesTxt;

    /*商标使用管理规则（附件）*/
    private String sba0001;

    /*集体成员名单*/
    private String collectiveMembers;

    /*集体成员名单（附件）*/
    private String sba0030;

    /*附件*/
    private String sba0007;

    /*地理标志材料一*/
    private String sba0002;

    /*地理标志材料二*/
    private String sba0003;

    /*地理标志材料三*/
    private String sba0004;

    /*地理标志材料四*/
    private String sba0005;

    /*地理标志材料五*/
    private String sba0006;

    /*申请人是否具备检测能力*/
    private String whetherApplicantAbilityTest;

    /*申请人与具有检测资格的机构签署的委托检测合同（附件）*/
    private String sba0012;

    /*受委托机构的单位法人证书（附件）*/
    private String sba0013;

    /*受委托机构的资质证书（附件）*/
    private String sba0014;

    /*专业检测设备清单（附件）*/
    private String sba0015;

    /*专业技术人员名单（附件）*/
    private String sba0016;

    /*申请人检测资质证书（附件）*/
    private String sba0008;

    /*申请人专业检测设备清单*/
    private String sba0009;

    /*申请人专业技术人员名单*/
    private String sba0010;

    /*申请人技术人员证书*/
    private String sba0011;

    /*是否颜色组合*/
    private String whetherColorCombination;

    /*声音商标*/
    private String soundMark;

    /*声音文件*/
    private String sba0017;

    /*商标名称*/
    private String trademarkName;

    /*商标说明*/
    private String trademarkDescription;


    /*是否共同申请*/
    private String whetherApplyJointly;

    /*共有人申请信息*/
    List<ShunchaoTrademarkCoApplicant> trademarkCoApplicants;

    /*优先权声明 0在先优先权1展会优先权*/
    private String priorityStatement;

    /*是否上传优先权证明文件 0否1是*/
    private String whetherUploadPriorityDoc;

    /*优先权证明文件*/
    private String sba0018;

    /*申请/展出国家/地区*/
    private String applicationArea;

    /*申请/展出日期*/
    private String applicationData;

    /*申请号*/
    private String applicationNumber;

    /*商品*/
    List<ShunchaoTrademarkProduct> trademarkProducts;

    /*着色图样*/
    private String sba0019;

    /*黑白图样*/
    private String sba0020;

    /*以肖像作为商标申请注册 0否1是*/
    private String applicationRegistrationApplication;

    /*证明文件（公正文件）*/
    private String sba0021;

    /*有关说明文件*/
    private String sba0022;


    //==================新增=============
    /*国内申请人联系地址*/
    private String applicantContactAddress;

    /*国内申请人电子邮箱*/
    private String applicantEmail;

    /*商标代理机构PIN码*/
    private String tmsvePin;

}
