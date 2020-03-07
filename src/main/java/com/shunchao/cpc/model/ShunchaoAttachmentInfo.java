package com.shunchao.cpc.model;

import lombok.Data;

import java.util.Date;

/**
 * @Description: 附件信息
 * @Author: jeecg-boot
 * @Date:   2019-12-23
 * @Version: V1.0
 */
@Data
public class ShunchaoAttachmentInfo {
    
	/**ID*/
	private String id;
	/**业务ID*/
	private String businessId;
	/**案件编号*/
	private String caseNumber;
	/**附件名称*/
	private String attachmentName;
	/**存储相对路径*/
	private String relativePath;
	/**附加大小*/
	private String attachmentSize;
	/**附件后缀*/
	private String attachmentSuffix;
	/**0：普通的公共附件 1：技术交底书2：五书 3：申请材料 4：答复材料*/
	private String attachmentType;
	/**发文日*/
	private String tableCode;
	/**收到日期*/
	private String fileType;
	/**权利要求书：项数 其他：页数，下边有页数字段，此处是否是只保存项数*/
	private String counts;
	/**官文名称*/
	private String originalName;
	/**页数*/
	private String pages;
	/**备注*/
	private String remark;
	/**创建人*/
	private String createBy;
	/**创建人*/
	private String createOrg;
	/**创建时间*/
	private Date createTime;
	/**修改人*/
	private String updateBy;
	/**修改时间*/
	private Date updateTime;
}
