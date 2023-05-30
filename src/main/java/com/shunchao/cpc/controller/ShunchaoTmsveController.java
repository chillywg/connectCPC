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
										 @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
										 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
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
				TrademarkUtils.tmsveLogin(enterpriceAgencyInfo,cookie);

				paramMap.put("cookie",cookie.toString());
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
