package com.shunchao.cpc.service;


import com.shunchao.cpc.model.ShunchaoAttachmentInfo;
import com.shunchao.cpc.model.ShunchaoCaseInfo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface IShuncaoConnectService {
    public void sendCase(ShunchaoCaseInfo shunchaoCaseInfo, List<ShunchaoAttachmentInfo> shunchaoAttachmentInfoList, String token, String category, String fillMode) throws Exception;

    /**
    * @Description : 从CPC获取官文
    * @Param [callback, token, req]
    * @return:java.lang.String
    * @Author:FuQiangCalendar
    * @Date: 2021/5/8 10:42
    */
    public String getNoticesByPatentNo(String callback, String token, HttpServletRequest req);
}
