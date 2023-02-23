package com.shunchao.cpc.service;
import com.alibaba.fastjson.JSONObject;
import com.shunchao.cpc.model.ShunchaoTmsveAnnotation;
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

    void tmsveLogin(JSONObject enterpriceAgencyInfo, Map<String,String> cookie) throws IOException;

    List<ShunchaoTrademarkTmsve> tmsveQueryDomesticApplication(String domesticApplyDateBegin, String domesticApplyDateEnd, Map<String,String> cookie) throws IOException;
    <T> List<Map<String, Object>> analyzing(Class<T> tClass, String appNum, Map<String,String> cookie);
    Map<String,String>  downloadpdf(Map<String,String> cookie, String docId,String applyNumber, String token) throws IOException;
    Map<String,String> getCookie() throws IOException;
}
