package com.shunchao.cpc.service;
import com.alibaba.fastjson.JSONObject;
import com.shunchao.cpc.model.ShunchaoTrademarkTmsve;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Description: 商标局官文
 * @Author: jeecg-boot
 * @Date: 2022-04-12
 * @Version: V1.0
 */
public interface IShunchaoTrademarkTmsveService{

    String tmsveLogin(JSONObject enterpriceAgencyInfo) throws IOException;

    List<ShunchaoTrademarkTmsve> tmsveQueryDomesticApplication(String domesticApplyDateBegin, String domesticApplyDateEnd, JSONObject enterpriceAgencyInfo, String cookie) throws IOException;

    Map<String,String>  downloadpdf(String cookieId, String docId,String applyNumber, String token) throws IOException;

    String getCookie() throws IOException;
}
