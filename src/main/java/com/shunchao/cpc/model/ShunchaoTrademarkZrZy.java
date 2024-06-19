package com.shunchao.cpc.model;

import lombok.Data;

import java.util.List;

@Data
public class ShunchaoTrademarkZrZy {
    /**商标类型 1一般2集体3证明*/
    private String trademarkType;

    /**注册号*/
    private java.lang.String registrationNumber;

    /**代理文号*/
    private String agentNumber;

    /**是否地理标志 0否1是*/
    private String whetherGeographicalIndication;

    /**是否三维标志*/
    private String whetherThreedimensionLogo;

    /**商标使用管理规则*/
    private String certificationManagementRulesTxt;

    /**商标使用管理规则（附件）*/
    private String sba0001;

    /**集体成员名单*/
    private String collectiveMembers;

    /**集体成员名单（附件）*/
    private String sba0030;

    /**附件*/
    private String sba0007;

    /**地理标志材料一*/
    private String sba0002;

    /**地理标志材料二*/
    private String sba0003;

    /**地理标志材料三*/
    private String sba0004;

    /**地理标志材料四*/
    private String sba0005;

    /**地理标志材料五*/
    private String sba0006;

    /**申请人是否具备检测能力*/
    private String whetherApplicantAbilityTest;

    /**申请人与具有检测资格的机构签署的委托检测合同（附件）*/
    private String sba0012;

    /**受委托机构的单位法人证书（附件）*/
    private String sba0013;

    /**受委托机构的资质证书（附件）*/
    private String sba0014;

    /**专业检测设备清单（附件）*/
    private String sba0015;

    /**专业技术人员名单（附件）*/
    private String sba0016;

    /**申请人检测资质证书（附件）*/
    private String sba0008;

    /**申请人专业检测设备清单*/
    private String sba0009;

    /**申请人专业技术人员名单*/
    private String sba0010;

    /**申请人技术人员证书*/
    private String sba0011;

    /**是否颜色组合*/
    private String whetherColorCombination;

    /**声音商标*/
    private String soundMark;

    /**声音文件*/
    private String sba0017;

    /**商标名称*/
    private String trademarkName;

    /**商标说明*/
    private String trademarkDescription;


    /**是否共同申请 0否1是*/
    private String whetherApplyJointly;

    /**共有人申请信息*/
    List<ShunchaoTrademarkApplicant> trademarkCoApplicants;

    /**优先权声明 0在先优先权1展会优先权*/
    private String priorityStatement;

    /**是否上传优先权证明文件 0否1是*/
    private String whetherUploadPriorityDoc;

    /**优先权证明文件*/
    private String sba0018;

    /**申请/展出国家/地区*/
    private String applicationArea;

    /**申请/展出日期*/
    private String applicationData;

    /**申请号*/
    private String applicationNumber;

    /**着色图样*/
    private String sba0019;

    /**黑白图样*/
    private String sba0020;

    /**以肖像作为商标申请注册 0否1是*/
    private String applicationRegistrationApplication;

    /**证明文件（公正文件）*/
    private String sba0021;

    /**有关说明文件*/
    private String sba0022;

    /**共有人知情转让转移证明*/
    private String sba0031;

    /**转让人委托书*/
    private String sba0034;


    /**受让人委托书*/
    private String sba0035;


    //==================新增=============
    /**国内申请人联系地址*/
    private String applicantContactAddress;

    /**国内申请人电子邮箱*/
    private String applicantEmail;

    /**商标代理机构PIN码*/
    private String tmsvePin;

    /*商标代理机构密码*/
    private String tmsveCipher;

    /**代理人姓名*/
    private String agentName;

    /**邮政编码（代理人）*/
    private String agentPostalCode;

    /**是否着色图样*/
    private String whetherColorPattern;

    /**转让人*/
    ShunchaoTrademarkApplicant applicant1;

    /**受让人*/
    ShunchaoTrademarkApplicant applicant2;

    /**办理业务(1：商标转让，2：商标转移)*/
    private String transferType;
}
