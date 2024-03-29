package com.shunchao.cpc.service;
import com.alibaba.fastjson.JSONObject;
import com.shunchao.cpc.model.ShunchaoTmsveAnnotation;
import com.shunchao.cpc.model.ShunchaoTrademarkApplicant;
import com.shunchao.cpc.model.ShunchaoTrademarkTmsve;
import com.shunchao.cpc.model.ShunchaoTrademarkZrZy;

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
    void getTrademarkApplicantInfo(String workbenchId, String token);

    ShunchaoTrademarkApplicant getDfSendTsvmeData(String workbenchId, String token);

    void connectTmsveDownloadData(String trademarkId, String token);

    ShunchaoTrademarkZrZy getZYSendTsvmeData(String trademarkId, String token);

}
