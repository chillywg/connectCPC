package com.shunchao.cpc.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Description: 商标局官文
 * @Author: jeecg-boot
 * @Date:   2022-04-12
 * @Version: V1.0
 */
@Data
public class ShunchaoTrademarkTmsve {
    /**id*/
    private java.lang.String id;
    /**逻辑删除标记 0存在1删除*/
    private java.lang.Integer delFlag;
    /**创建人*/
    private java.lang.String createBy;
    /**创建时间*/
    private java.util.Date createTime;
    /**更新人*/
    private java.lang.String updateBy;
    /**更新时间*/
    private java.util.Date updateTime;
    /**租户id*/
    private java.lang.Integer providerId;
    /**商标id*/
    private java.lang.String trademarkId;
    /**注册号*/
    private java.lang.String tmsveRegistrationnumber;
    /**申请号*/
    private java.lang.String tmsveApplynumber;
    /**业务类型*/
    private java.lang.String tmsveBusinesstype;
    /**书式类型*/
    private java.lang.String tmsveBooktype;
    /**发文日期*/
    private java.util.Date tmsvePublicationdate;
    /**官方绝限日*/
    private java.util.Date offcialdeadlineDay;
    /**官文状态*/
    private java.lang.String offcialStatus;
    /**客户id*/
    private java.lang.String customerId;
    /**通知书相对路径*/
    private java.lang.String tmsveRelativepath;
    /**查看状态*/
    private java.lang.String tmsveCheckstatus;
    /**是否有效*/
    private java.lang.String tmsveIsiteffective;
    /**代理公司*/
    private java.lang.String tmsveEnterpriseagency;
    /**代理文号*/
    private java.lang.String tmsveAgencynumber;
    /**申请日期*/
    private java.util.Date tmsveApplyday;
    /**申请业务类型*/
    private java.lang.String tmsveApplybusinesstype;
    /**业务状态*/
    private java.lang.String tmsveBusinessstatus;

    //===========================新增========================
    private String tmsveDocid;


    //===========================非表========================
    private String tmsveIndex;
}
