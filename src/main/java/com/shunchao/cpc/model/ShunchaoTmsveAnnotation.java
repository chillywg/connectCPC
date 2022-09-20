package com.shunchao.cpc.model;


import com.shunchao.cpc.util.CustomAnnotation;
import lombok.Data;

/**
 * @author djlcc
 * @title: ShunchaoTmsveAnnotation
 * @projectName jeecg-boot-parent
 * @description: TODO 解析html实体类
 * @date 2022/4/13 13:45
 */
@Data
public class ShunchaoTmsveAnnotation {

    @CustomAnnotation("操作")
    private String tmsveDocId;
    @CustomAnnotation("注册号")
    private String tmsveRegistrationNumber;
    @CustomAnnotation("申请号")
    private String tmsveApplyNumber;
    @CustomAnnotation("业务类型")
    private String tmsveBusinessType;
    @CustomAnnotation("书式类型")
    private String tmsveBookType;
    @CustomAnnotation("发文日期")
    private String tmsvePublicationdate;
    @CustomAnnotation("查看状态")
    private String tmsveCheckStatus;
    @CustomAnnotation("是否有效")
    private String tmsveIsitEffective;

}
