package com.shunchao.cpc.controller;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONObject;
import com.shunchao.cpc.model.Result;
import com.shunchao.cpc.service.IShunchaoTrademarkTmsveService;
import com.shunchao.cpc.util.TrademarkUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


/**
 * @Description: 商标获取官文
 * @Author: zcs
 * @Date:   2023-05-26
 * @Version: V1.0
 */
@Slf4j
@RestController
@RequestMapping("/trademark/shunchaoTrademarkTmsve")
public class ShunchaoTmsveController {
	@Value(value = "${connecturl}")
	private String connecturl;

	@GetMapping(value = "/excuteQueryDomestic")
	public String excuteQueryDomestic(String callback, String enterpriceAgencyId,
									  HttpServletRequest req){
		String token = req.getParameter("token");
		int size = 0 ;
		if (StringUtils.isNotBlank(enterpriceAgencyId)) {
			try{
				HashMap<String, Object> paramMap = new HashMap<>();
				paramMap.put("agencyId", enterpriceAgencyId);
				//获取代理机构信息
				String enterInfoAndTmsveDate = HttpRequest.get(connecturl + "/trademark/shunchaoTrademarkTmsve/getEnterInfoAndTmsveDate").
						header("X-Access-Token", token).form(paramMap).execute().body();
				HttpRequest.closeCookie();
				JSONObject json1= JSONObject.parseObject(enterInfoAndTmsveDate);
				JSONObject resultObject = (JSONObject) json1.get("result");
				size = TrademarkUtils.tmsveLogin(resultObject, connecturl, token);
			}catch (Exception e){
				log.info("获取失败",e);
				return JSONObject.toJSONString(Result.error("获取失败"));
			}
		}
		log.info("获取结束");
		if (StringUtils.isNotBlank(callback)) {
			String string = JSONObject.toJSONString(Result.ok(size));
			return callback + "(" + string + ")";
		} else {
			return JSONObject.toJSONString(Result.error("获取失败"));
		}
	}

	@GetMapping(value = "/excuteQueryDomestic2")
	public String excuteQueryDomestic2(String callback, String enterpriceAgencyId,String size,String domesticApplyDateBegin,
										 HttpServletRequest req){
		String token = req.getParameter("token");
		if (StringUtils.isNotBlank(enterpriceAgencyId)) {
			try{
				HashMap<String, Object> paramMap = new HashMap<>();
				paramMap.put("id", enterpriceAgencyId);
				//获取代理机构信息
				String enterInfoAndCookie = HttpRequest.get(connecturl + "/trademark/shunchaoTrademarkTmsve/getEnterInfo").
						header("X-Access-Token", token).form(paramMap).execute().body();
				HttpRequest.closeCookie();
				Map<String,String> cookie = TrademarkUtils.getCookie();
				JSONObject json1= JSONObject.parseObject(enterInfoAndCookie);
				JSONObject resultObject = (JSONObject) json1.get("result");
				JSONObject enterpriceAgencyInfo = (JSONObject)resultObject.get("enterpriceAgencyInfo");
				Map<String, String> cookies = TrademarkUtils.tmsveLogin2(enterpriceAgencyInfo, cookie);
//				Map<String, String> cookies = TrademarkUtils.tmsveLogin2(enterpriceAgencyInfo);

				paramMap.put("cookie",cookies.toString());
				paramMap.put("size",size);
				paramMap.put("tmsveDate",domesticApplyDateBegin);
				HttpRequest.get(connecturl + "/trademark/shunchaoTrademarkTmsve/getTrademarkTmsve").
						header("X-Access-Token", token).form(paramMap).execute().body();
			}catch (Exception e){
				log.info("获取失败",e);
				return JSONObject.toJSONString(Result.error("获取失败"));
			}
		}
		log.info("获取结束");
		if (StringUtils.isNotBlank(callback)) {
			String string = JSONObject.toJSONString(Result.ok("获取成功"));
			return callback + "(" + string + ")";
		} else {
			return JSONObject.toJSONString(Result.error("获取失败"));
		}
	}
}
