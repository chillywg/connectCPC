package com.shunchao.cpc.controller;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shunchao.cpc.model.Result;
import com.shunchao.cpc.model.ShunchaoTmsveAnnotation;
import com.shunchao.cpc.model.ShunchaoTrademarkTmsve;
import com.shunchao.cpc.service.IShunchaoTrademarkTmsveService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @Description: 测试
 * @Author: chilly
 * @Date:   2019-11-7
 * @Version: V1.0
 */
@Slf4j
@RestController
@RequestMapping("/trademark/shunchaoTrademarkTmsve")
public class ShunchaoTmsveController {
	@Value(value = "${connecturl}")
	private String connecturl;
	@Autowired
	private IShunchaoTrademarkTmsveService shunchaoTrademarkTmsveService;


	@GetMapping(value = "/excuteQueryDomestic")
	public String excuteQueryDomestic(String callback,String domesticApplyDateBegin,String domesticApplyDateEnd,String enterpriceAgencyId,
										 @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
										 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
										 String registrationNumber,String applyNumber,String caseNumber, HttpServletRequest req) throws IOException {
		String token = req.getParameter("token");
		if (StringUtils.isNotBlank(domesticApplyDateBegin)
				&& StringUtils.isNotBlank(domesticApplyDateEnd)
				&& StringUtils.isNotBlank(enterpriceAgencyId)
				&& pageNo < 2)
		{
			HashMap<String, Object> paramMap = new HashMap<>();
			paramMap.put("id", enterpriceAgencyId);
			//获取代理机构信息
			String enterInfoAndCookie = HttpRequest.get(connecturl + "/trademark/shunchaoTrademarkTmsve/getEnterInfoAndCookie").
					header("X-Access-Token", token).form(paramMap).execute().body();
			HttpRequest.closeCookie();
			Map<String,String> cookie =shunchaoTrademarkTmsveService.getCookie();
			JSONObject json1= JSONObject.parseObject(enterInfoAndCookie);
			JSONObject resultObject = (JSONObject) json1.get("result");
			JSONObject enterpriceAgencyInfo = (JSONObject)resultObject.get("enterpriceAgencyInfo");
			shunchaoTrademarkTmsveService.tmsveLogin(enterpriceAgencyInfo,cookie);
			try {
				List<ShunchaoTrademarkTmsve> trademarkTmsveList=shunchaoTrademarkTmsveService.tmsveQueryDomesticApplication(domesticApplyDateBegin, domesticApplyDateEnd, cookie);
				HashMap<String, Object> map = new HashMap<>();
				map.put("trademarkString",JSON.toJSONString(trademarkTmsveList));
				map.put("enterpriceAgencyId",enterpriceAgencyId);
				//存入商标信息
				String tmsveList = HttpRequest.get(connecturl + "/trademark/shunchaoTrademarkTmsve/updateAndGetTrademarkinfo").
						header("X-Access-Token", token).form(map).execute().body();
				HttpRequest.closeCookie();
				JSONObject tmsveJson= JSONObject.parseObject(tmsveList);
				String resultJson =tmsveJson.get("result").toString();
				List<HashMap> stringList = JSONObject.parseArray(resultJson, HashMap.class);
				List<ShunchaoTrademarkTmsve> shunchaoTrademarkTmsveList =new ArrayList<>();
				for (Map<String, Object> objectMap : stringList) {
					try {
						List<String> arrayList = new ArrayList<String>();
						if(!Objects.isNull(objectMap.get("tmsveDocId"))){
							String[] str = objectMap.get("tmsveDocId").toString().split(",");
							arrayList = Arrays.asList(str);
						}
						List<Map<String, Object>> mapList = shunchaoTrademarkTmsveService.analyzing(ShunchaoTmsveAnnotation.class, (String) objectMap.get("tmsveApplyNumber"), cookie);
						log.info("商标官文-数据库数据：{}条，html抓取数据：{}条", arrayList.size(), mapList.size());
						List<String> mapListString = mapList.stream().map(i -> (String) i.get("tmsveDocId")).collect(Collectors.toList());
						mapListString.removeAll(arrayList);
						log.info("商标官文-过滤后，需要下载文件的个数：{}", mapListString.size());
						for (String s : mapListString) {
							try {
								Map<String, String> mapPath = shunchaoTrademarkTmsveService.downloadpdf(cookie, s, (String) objectMap.get("tmsveApplyNumber"),token);
								List<Map<String, Object>> objectMap1 = mapList.stream().filter(i -> s.equals((String) i.get("tmsveDocId"))).collect(Collectors.toList());
								String jsonString = JSON.toJSONString(objectMap1.get(0));
								ShunchaoTrademarkTmsve shunchaoTrademarkTmsve = JSON.parseObject(jsonString, ShunchaoTrademarkTmsve.class);
								shunchaoTrademarkTmsve.setAgencyId(enterpriceAgencyId);
								shunchaoTrademarkTmsve.setTmsveRelativepath(mapPath.get("path"));
								shunchaoTrademarkTmsveList.add(shunchaoTrademarkTmsve);
							} catch (Exception e) {
								e.printStackTrace();
								log.error("商标官文-下载文件失败或入库失败：{},申请号：{},docId:{}", e, map.get("fw.appNum"), s);
							}
						}
					} catch (Exception e) {
						log.error("商标官文-获取商标数据出现失败：{}", e);
					}
				}
				HashMap<String, Object> map2 = new HashMap<>();
				map2.put("shunchaoTrademarkString",JSON.toJSONString(shunchaoTrademarkTmsveList));
				//存入商标信息
				HttpRequest.get(connecturl + "/trademark/shunchaoTrademarkTmsve/saveShunchaoTrademark").
						header("X-Access-Token", token).form(map2).execute().body();
				HttpRequest.closeCookie();
			} catch (IOException e) {
				log.error("商标官文下载失败", e);
				if (StringUtils.isNotBlank(callback)) {
					String string = JSONObject.toJSONString(Result.error(500, "商标官文下载失败"));
					return callback + "(" + string + ")";
				} else {
					return JSONObject.toJSONString(Result.error(500, "商标官文下载失败"));
				}
			}
		}
		HashMap<String, Object> map3 = new HashMap<>();
		map3.put("domesticApplyDateBegin", domesticApplyDateBegin);
		map3.put("domesticApplyDateEnd", domesticApplyDateEnd);
		map3.put("enterpriceAgencyId", enterpriceAgencyId);
		map3.put("pageNo", pageNo);
		map3.put("pageSize", pageSize);
		map3.put("registrationNumber", registrationNumber);
		map3.put("applyNumber", applyNumber);
		map3.put("caseNumber", caseNumber);
		String tmsveList = HttpRequest.get(connecturl + "/trademark/shunchaoTrademarkTmsve/findList").
				header("X-Access-Token", token).form(map3).execute().body();

		if (StringUtils.isNotBlank(callback)) {
			return callback + "(" + tmsveList + ")";
		} else {
			return tmsveList;
		}
	}
}
