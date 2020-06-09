package com.shunchao.cpc.model;

import lombok.Data;

import java.util.Date;

/**
 * @Description: 案件信息
 * @Author: jeecg-boot
 * @Date:   2019-12-16
 * @Version: V1.0
 */
@Data
public class ShunchaoCaseInfo {
    
	/**ID*/
	private String id;
	/**流程实例ID*/
	private String procInstId;
	/**合同ID*/
	private String contractId;
	/**市场用户ID*/
	private String userId;
	/**客户ID(甲方)*/
	private String customerId;
	/**关联案件ID*/
	private String associatedCaseId;
	/**关联案件名称*/
	private String associatedCaseName;
	/**案件名称*/
	private String caseName;
	/**业务编号*/
	private String businessNumber;
	/**内部编号*/
	private java.lang.String internalNumber;
	/**专利号*/
	private String patentNumber;
	/**0:普通申请--发明 1:普通申请--新型 2:普通申请--外观 3:PCT申请--发明 4:PCT申请--新型 5:复审 6:无效*/
	private String applicationType;
	/**0：专利类 1：软著类 2：商标类*/
	private String caseType;
	/**业务类型*/
	private String businessType;
	/**0：不限次数 1：1次 2：2次 3:3次*/
	private String numberOfReplies;
	/**代理费用*/
	private String agencyFee;
	/**官费费用*/
	private String officialFee;
	/**0：不监控 1：监控*/
	private String monitoringAnnualFee;
	/**0：85% 1：75%  2：无费减*/
	private String feeReductionRatio;
	/**0：机械 1：电学 2：化学*/
	private String technicalField;
	/**0：技术交底 1：技术描述文本*/
	private String technicalDisclosure;
	/**技术描述*/
	private String technicalDescription;
	/**案件状态*/
	private String caseStatus;
	/**案件进度*/
	private String caseProgress;
	/**0：临时 1：有效*/
	private String validMark;
	/**创建人*/
	private String createBy;
	/**创建时间*/
	private Date createTime;
	/**修改人*/
	private String updateBy;
	/**修改时间*/
	private Date updateTime;
	/**0：否 1：是*/
	private String whetherToTrade;
	/**案件费用总额*/
	private java.math.BigDecimal totalCost;
	/**0：未缴费 1：未答复*/
	private String lossPatentRights;
	/**0：无 1：有*/
	private String microbialPreservation;
	/**1.发明创造依赖于遗传资源 2.拟向外国申请专利3.涉及核苷酸或氨基酸序列表4.请求提前公布
             5.同日申请实用新型专利*/
	private String microbialPreservationOption;
	/**0：中国微生物菌种保藏管理委员会普通微生物中心1.中国典型培养物保藏中心*/
	private String preservationUnit;
	/**保藏地址*/
	private String preservationAddress;
	/**保藏单位编号*/
	private String preservationUnitNumber;
	/**保藏日期*/
	private Date preservationDate;
	/**分类命名*/
	private String classificationNaming;
	/**申请日*/
	private Date applicationDate;
	/**变更业务*/
	private String changeBusiness;
	/**申请文件副本*/
	private String applyCopyDocuments;
	/**委内编号*/
	private java.lang.String weineibh;
}
