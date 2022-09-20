package com.shunchao.cpc.controller;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shunchao.cpc.model.Result;
import com.shunchao.cpc.model.ShunchaoTmsveAnnotation;
import com.shunchao.cpc.model.ShunchaoTrademarkTmsve;
import com.shunchao.cpc.service.IShunchaoTrademarkTmsveService;
import com.shunchao.cpc.util.CustomAnnotation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.util.StringUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
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
	public Result<?> excuteQueryDomestic(String domesticApplyDateBegin,String domesticApplyDateEnd,String enterpriceAgencyId,
										 @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
										 @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
										 String registrationNumber,String applyNumber,String caseNumber, HttpServletRequest req){
		String token = req.getParameter("token");
		if (StringUtils.isNotBlank(domesticApplyDateBegin)
				&& StringUtils.isNotBlank(domesticApplyDateEnd)
				&& StringUtils.isNotBlank(enterpriceAgencyId)
				&& pageNo < 2) {
			HashMap<String, Object> paramMap = new HashMap<>();
			paramMap.put("id", enterpriceAgencyId);
			//获取代理机构信息及cookie
			String enterInfoAndCookie = HttpRequest.get(connecturl + "/trademark/shunchaoTrademarkTmsve/getEnterInfoAndCookie").
					header("X-Access-Token", token).form(paramMap).execute().body();
			JSONObject json1= JSONObject.parseObject(enterInfoAndCookie);
			JSONObject resultObject = (JSONObject) json1.get("result");
			JSONObject enterpriceAgencyInfo = (JSONObject)resultObject.get("enterpriceAgencyInfo");
			String cookie = resultObject.getString("cookie");
			if (StringUtils.isBlank(cookie)) {
				try {
					cookie =shunchaoTrademarkTmsveService.tmsveLogin(enterpriceAgencyInfo);
					paramMap.put("cookie",cookie);
					//保存cookie
					HttpRequest.get(connecturl + "/trademark/shunchaoTrademarkTmsve/saveCookie").
							header("X-Access-Token", token).form(paramMap).execute().body();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				List<ShunchaoTrademarkTmsve> trademarkTmsveList=shunchaoTrademarkTmsveService.tmsveQueryDomesticApplication(domesticApplyDateBegin, domesticApplyDateEnd, enterpriceAgencyInfo, cookie);
				HashMap<String, Object> map = new HashMap<>();
				map.put("trademarkTmsveList",trademarkTmsveList);
				//存入商标信息
				HttpRequest.post(connecturl + "/trademark/shunchaoTrademarkTmsve/updateTrademarkinfo").
						header("X-Access-Token", token).form(map).execute().body();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				String tmsveList = HttpRequest.get(connecturl + "/trademark/shunchaoTrademarkTmsve/getTmsveList").
						header("X-Access-Token", token).execute().body();
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
						Map<String, String> map = new HashMap<>();
						map.put("fw.appNum", (String) objectMap.get("tmsveApplyNumber"));
						List<Map<String, Object>> mapList = analyzing(ShunchaoTmsveAnnotation.class, map, "http://wssq.sbj.cnipa.gov.cn:9080/tmsve/fwcx_getFwCondition.xhtml", cookie);
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
								shunchaoTrademarkTmsve.setTmsveRelativepath(mapPath.get("path"));
								shunchaoTrademarkTmsveList.add(shunchaoTrademarkTmsve);
							} catch (Exception e) {
								log.error("商标官文-下载文件失败或入库失败：{},申请号：{},docId:{}", e, map.get("fw.appNum"), s);
							}
						}
					} catch (Exception e) {
						log.error("商标官文-获取商标数据出现失败：{}", e);
					}
				}
				HashMap<String, Object> map = new HashMap<>();
				map.put("shunchaoTrademarkTmsveList",shunchaoTrademarkTmsveList);
				//存入商标信息
				HttpRequest.post(connecturl + "/trademark/shunchaoTrademarkTmsve/saveShunchaoTrademark").
						header("X-Access-Token", token).form(map).execute().body();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
//		String bodyInternal = HttpRequest.get(connecturl + "/trademark/shunchaoTrademarkTmsve/gettmsveList").
//				header("X-Access-Token", token).execute().body();
//
//		JSONObject jsonExcuteQuery = JSONObject.parseObject(bodyInternal);

		return Result.ok();
	}

	/**
	 * @throws
	 * @title analyzing
	 * @description
	 * @author djlcc
	 * @param: tClass
	 * @param: mapParams 需要自定义传入参数 申请号 appNum，开始时间 date1，结束时间 date2
	 * @param: url
	 * @param: cookie
	 * @updateTime 2022/4/13 16:58
	 * @return: java.util.List<java.util.Map   <   java.lang.String   ,   java.lang.Object>>
	 */
	public static <T> List<Map<String, Object>> analyzing(Class<T> tClass, Map<String, String> mapParams, String url, String cookie) {
		List<Map<String, Object>> tmsveList = new ArrayList<>();
		SimpleDateFormat simpleFormatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
		SimpleDateFormat simpleFormatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			mapParams.put("fw.regNum", "");
			mapParams.put("fw.flowType", "-1");
			mapParams.put("fw.fileType", "");
			mapParams.put("pagenum", "1");
			mapParams.put("pagesize", "30");
			mapParams.put("sum", "4");
			mapParams.put("countpage", "1");
			mapParams.put("gopage", "1");
			Document document = Jsoup.connect(url).cookie("Cookie", cookie).data(mapParams).get();
			//Document document = Jsoup.parse(new File("C:\\Users\\admin\\Desktop\\我的发文demo.html"),"UTF-8");
			Elements elements = document.getElementsByClass("import_tab");
			Elements elements1 = elements.select("tr");
			Field[] fields = tClass.getDeclaredFields();
			Map<String, Object> map = new HashMap<>();
			for (Field field : fields) {
				CustomAnnotation tmsveAnnotation = field.getAnnotation(CustomAnnotation.class);
				map.put(tmsveAnnotation.value(), field.getName());
			}
			for (int a = 1; a < elements1.size(); a++) {
				Map<String, Object> map1 = new HashMap<>();
				Elements elements2 = elements1.get(a).select("td");
				for (int b = 0; b < elements2.size(); b++) {
					String fieldName = (String) map.get(elements1.get(0).select("td").get(b).text());
					if (StringUtil.isEmpty(fieldName)) {
						continue;
					}
					Object fieldValue = elements2.get(b).text();
					if (b == elements2.size() - 1) {
						String[] element = elements2.get(b).select("a").get(1).attr("href").replaceAll("'\\)", "").split("=");
						fieldValue = element[element.length - 1];
					}
					if ("发文日期".equals(elements1.get(0).select("td").get(b).text())) {
						fieldValue = simpleFormatter1.format(simpleFormatter.parse(elements2.get(b).text()));
					}
					map1.put(fieldName, fieldValue);
				}
				tmsveList.add(map1);
			}
		} catch (Exception e) {
			log.error("商标解析html文件失败:{}", e);
		}
		return tmsveList;
	}
}
