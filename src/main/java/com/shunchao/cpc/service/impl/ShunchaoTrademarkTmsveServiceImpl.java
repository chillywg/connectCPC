package com.shunchao.cpc.service.impl;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONObject;
import com.shunchao.cpc.model.ShunchaoTrademarkAnnex;
import com.shunchao.cpc.model.ShunchaoTrademarkApplicant;
import com.shunchao.cpc.model.ShunchaoTrademarkTmsve;
import com.shunchao.cpc.service.IShuncaoConnectService;
import com.shunchao.cpc.service.IShunchaoTrademarkTmsveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * @Description: 商标局官文
 * @Author: jeecg-boot
 * @Date:   2022-04-12
 * @Version: V1.0
 */
@Service
@Slf4j
public class ShunchaoTrademarkTmsveServiceImpl implements IShunchaoTrademarkTmsveService {
    @Value(value = "${connecturl}")
    private String connecturl;
    @Autowired
    private IShuncaoConnectService shuncaoConnectService;

    @Override
    public void getTrademarkApplicantInfo(String workbenchId, String token) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("workbenchId", workbenchId);
        //获取代理机构信息
        String trademarkApplicantData = HttpRequest.get(connecturl + "/trademark/shunchaoTrademarkAnnex/getTrademarkApplicantData").
                header("X-Access-Token", token).form(paramMap).execute().body();
        JSONObject tmsveJson= JSONObject.parseObject(trademarkApplicantData);
        String resultJson =tmsveJson.get("result").toString();
        List<ShunchaoTrademarkAnnex> shunchaoTrademarkAnnexList = JSONObject.parseArray(resultJson, ShunchaoTrademarkAnnex.class);
        String mark = shuncaoConnectService.getTrademarkAnnex(shunchaoTrademarkAnnexList,token);
    }

    @Override
    public ShunchaoTrademarkApplicant getDfSendTsvmeData(String workbenchId, String token) {

        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("workbenchId", workbenchId);
        //获取代理机构信息
        String dfSendTsvmeData = HttpRequest.get(connecturl + "/trademark/shunchaoTrademarkSendcase/getDfSendTsvmeData").
                header("X-Access-Token", token).form(paramMap).execute().body();
        JSONObject tmsveJson= JSONObject.parseObject(dfSendTsvmeData);
        JSONObject resultJson =(JSONObject) tmsveJson.get("result");
        ShunchaoTrademarkApplicant shunchaoTrademarkApplicant= JSONObject.toJavaObject(resultJson, ShunchaoTrademarkApplicant.class);
        return shunchaoTrademarkApplicant;
    }


    @Override
    public void connectTmsveDownloadData(String trademarkId, String token) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("trademarkId", trademarkId);
        //获取代理机构信息
        String trademarkApplicantData = HttpRequest.get(connecturl + "/trademark/shunchaoTrademarkAnnex/connectTmsveDownloadData").
                header("X-Access-Token", token).form(paramMap).execute().body();
        JSONObject tmsveJson= JSONObject.parseObject(trademarkApplicantData);
        String resultJson =tmsveJson.get("result").toString();
        List<ShunchaoTrademarkAnnex> shunchaoTrademarkAnnexList = JSONObject.parseArray(resultJson, ShunchaoTrademarkAnnex.class);
        String mark = shuncaoConnectService.getTrademarkAnnex(shunchaoTrademarkAnnexList,token);
    }

    @Override
    public ShunchaoTrademarkTmsve getSendTsvmeData(String trademarkId, String token) {

        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("trademarkId", trademarkId);
        //获取代理机构信息
        String dfSendTsvmeData = HttpRequest.get(connecturl + "/trademark/shunchaoTrademarkSendcase/getSendTsvmeData").
                header("X-Access-Token", token).form(paramMap).execute().body();
        JSONObject tmsveJson= JSONObject.parseObject(dfSendTsvmeData);
        JSONObject resultJson =(JSONObject) tmsveJson.get("result");
        ShunchaoTrademarkTmsve shunchaoTrademarkTmsve= JSONObject.toJavaObject(resultJson, ShunchaoTrademarkTmsve.class);
        return shunchaoTrademarkTmsve;
    }
}
