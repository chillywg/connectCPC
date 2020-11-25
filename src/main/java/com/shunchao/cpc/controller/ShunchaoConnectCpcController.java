package com.shunchao.cpc.controller;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;
import com.shunchao.config.CpcPathInComputer;
import com.shunchao.cpc.model.Result;
import com.shunchao.cpc.model.ShunchaoAttachmentInfo;
import com.shunchao.cpc.model.ShunchaoCaseInfo;
import com.shunchao.cpc.service.IShuncaoConnectService;
import com.shunchao.cpc.util.AccessDBUtils;

import com.shunchao.cpc.util.DBHelper;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * @Description: 测试
 * @Author: chilly
 * @Date:   2019-11-7
 * @Version: V1.0
 */
@Slf4j
@RestController
@RequestMapping("/cpc/connectCpc")
public class ShunchaoConnectCpcController {
	@Value(value = "${connecturl}")
	private String connecturl;
	@Value(value = "${jeecg.path.cases.basecpc}")
	private String basecpc;
	@Value(value = "${jeecg.path.cases.notices}")
	private String notices;
	@Autowired
	private IShuncaoConnectService shuncaoConnectService;
	/**
	 * 分页列表查询
	 *
	 * @param req
	 * @return
	 */
	@GetMapping(value = "/list")
	public String queryPageList(HttpServletRequest req) {
		String commond = "reg query HKEY_LOCAL_MACHINE\\SOFTWARE\\WOW6432Node\\gwssi\\CPC客户端";
		try {
			String dataPath = CpcPathInComputer.getCpcDataPathWindowsComputer();
			Database db = DatabaseBuilder.open(new File(dataPath));
			Table table = db.getTable("DZSQ_KHD_GJXZQH");
			for(Row row : table) {
				//顺序取出表中的字段和值
				//字段名区分大小写，如果不一致会导致取值为null
				System.out.println("--城市名字--" + row.get("GJXZQHMC"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "12345";
	}

	@GetMapping(value = "/sendCase", produces = "application/jsonp; charset=utf-8")
	public String sendCase(String callback, HttpServletRequest req) {
		HashMap<String, Object> paramMap = new HashMap<>();
		String id = req.getParameter("id");
		String token = req.getParameter("token");
		String[] split = id.split(",");







		try {
			for (String sid : split) {
				paramMap.put("id", sid);
				//		String result = HttpUtil.get(connecturl + "/sendcpc/shunchaoSendCpcCase/queryCaseInfoById", paramMap);
				String body = HttpRequest.get(connecturl + "/sendcpc/shunchaoSendCpcCase/queryCaseInfoById").
						header("X-Access-Token", token).form(paramMap).execute().body();
				JSONObject jsonObject = JSONObject.parseObject(body);
				JSONObject resultObject = (JSONObject) jsonObject.get("result");
				String caseInfoObject = resultObject.getString("shunchaoCaseInfo");
				String category = resultObject.getString("category");
				String fillMode = resultObject.getString("fillMode");
				String sendId = resultObject.getString("sendId");
				String attachmentListObject = resultObject.getString("attachmentList");
				ShunchaoCaseInfo shunchaoCaseInfo = JSONObject.parseObject(caseInfoObject, ShunchaoCaseInfo.class);
				List<ShunchaoAttachmentInfo> shunchaoAttachmentInfoList = JSONObject.parseArray(attachmentListObject, ShunchaoAttachmentInfo.class);
				shuncaoConnectService.sendCase(shunchaoCaseInfo, shunchaoAttachmentInfoList, token, category, fillMode);

				HashMap<String, Object> param = new HashMap<>();
				param.put("id", sendId);
				HttpRequest.get(connecturl + "/sendcpc/shunchaoSendCpcCase/updateStatus").
						header("X-Access-Token", token).form(param).execute();
			}


//			FileCopyUtils.copy();
		} catch (Exception e) {
			log.error("获取向CPC送案失败", e);
            if (StringUtils.isNotBlank(callback)) {
                String string = JSONObject.toJSONString(Result.error(500, "获取向CPC送案失败"));
                return callback + "(" + string + ")";
            } else {
                return JSONObject.toJSONString(Result.error(500, "获取向CPC送案失败"));
            }

		}

		if (StringUtils.isNotBlank(callback)) {
			String string = JSONObject.toJSONString(Result.ok("向CPC送案成功"));
			return callback + "(" + string + ")";
		} else {
			return JSONObject.toJSONString(Result.ok("向CPC送案成功"));
		}

	}

	@GetMapping(value = "/getNotices", produces = "application/jsonp; charset=utf-8")
	public String getNotices(String callback,@RequestParam(name = "token") String token, HttpServletRequest req) {

		//todo 获取系统的所有案件的内部编号
		String bodyInternal = HttpRequest.get(connecturl + "/caseinfo/shunchaoCaseInfo/queryAllInternalNumber").
				header("X-Access-Token", token).execute().body();

		JSONObject jsonObjectInternal = JSONObject.parseObject(bodyInternal);
		JSONArray jsonArray = (JSONArray) jsonObjectInternal.get("result");
		List<HashMap> hashMaps = JSONObject.parseArray(jsonArray.toJSONString(), HashMap.class);

//		JSONObject.parseArray(resultObject);
//		JSONObject.parseArray()
		int count = 0;
		try {
			String dataPath = CpcPathInComputer.getCpcDataPathWindowsComputer();
			Database db = DatabaseBuilder.open(new File(dataPath));

			Table table = db.getTable("DZSQ_KHD_TZS");
			Table fjTable = db.getTable("DZSQ_KHD_WJFJ");
//			String decode = URLDecoder.decode(internalNumbers,"UTF-8");
//			String[] internalNumberArray = decode.split(",");
			//获取官文数量

			for (int i = 0; i < hashMaps.size(); i++) {
				String internalNumber = hashMaps.get(i).get("internalNumber").toString();
				for (Row row : table) {
					if (StringUtils.isNotBlank(internalNumber) && internalNumber.equals(row.getString("NEIBUBH")) && "0".equals(row.getString("SHIFOUSC"))) {
						HashMap<String, Object> paramMap = new HashMap<>();
						ShunchaoAttachmentInfo t = new ShunchaoAttachmentInfo();
						//通知书编号
						String tongzhishubh = row.getString("TONGZHISBH");
						paramMap.put("tongzhisbh",tongzhishubh);
						paramMap.put("tongzhisdm",row.getString("TONGZHISDM"));
						paramMap.put("famingmc",row.getString("FAMINGMC"));
						paramMap.put("fawenxlh",row.getString("FAWENXLH"));
						paramMap.put("tongzhismc",row.getString("TONGZHISMC"));
						paramMap.put("shenqingbh",row.getString("SHENQINGBH"));
						paramMap.put("fawenrq", DateUtil.format((Date) row.get("FAWENRQ"), "yyyy-MM-dd"));
						paramMap.put("dafurq", DateUtil.format((Date) row.get("DAFURQ"), "yyyy-MM-dd"));
						paramMap.put("qianmingxx",row.getString("QIANMINGXX"));
						paramMap.put("zhucedm",row.getString("ZHUCEDM"));
						paramMap.put("xiazairq", DateUtil.format((Date) row.get("XIAZAIRQ"), "yyyy-MM-dd"));
						paramMap.put("xiazaics", row.get("XIAZAICS"));
						paramMap.put("zhuangtai",row.getString("ZHUANGTAI"));
//						paramMap.put("shifousc",row.getString("SHIFOUSC"));
						paramMap.put("shifousc",row.getString("SHIFOUSC"));
						paramMap.put("neibubh",row.getString("NEIBUBH"));
						paramMap.put("gongbuh",row.getString("GongBuH"));
						paramMap.put("gongbur", DateUtil.format((Date) row.get("GongBuR"), "yyyy-MM-dd"));
						paramMap.put("jinrussr", DateUtil.format((Date) row.get("JinRuSSR"), "yyyy-MM-dd"));
						paramMap.put("shoucinfnd",row.getString("ShouCiNFND"));
						paramMap.put("waiguanflh",row.getString("WaiGuanFLH"));
						paramMap.put("shouquanggh",row.getString("ShouQuanGGH"));
						paramMap.put("shouquanggr",DateUtil.format((Date) row.get("ShouQuanGGR"), "yyyy-MM-dd"));
						paramMap.put("daochuzt",row.getString("DaoChuZT"));
						paramMap.put("qianzhangbj",row.getString("QIANZHANGBJ"));
						/*for (Row r : fjTable) {
							if (tongzhishubh.equals(r.getString("TONGZHISBH"))) {
                                paramMap.put("attachmentSize",r.getString("FUJIANDX"));
							}
						}*/
						File file = new File(CpcPathInComputer.getCpcBinPathWindowsComputer() + File.separator + basecpc + File.separator + notices + File.separator + tongzhishubh);

						if (file.exists()) {
							paramMap.put("file", ZipUtil.zip(file));
							HttpResponse execute = HttpRequest.post(connecturl + "/notice/shunchaoDzsqKhdTzs/upload").
									header("X-Access-Token", token).form(paramMap).execute();
							String body = execute.body();
							JSONObject jsonObject = JSONObject.parseObject(body);
							Boolean success = (Boolean) jsonObject.get("success");
							Integer code = (Integer) jsonObject.get("code");
							if (!success) {
								if (40002 == code) {
									log.info("通知书编号：" + tongzhishubh + " 对应的通知书系统已经获取，无需重复获取，发明名称为：" + row.getString("FAMINGMC") + "，内部编号为：" + row.getString("NEIBUBH"));
								} else {
									log.info("通知书编号：" + tongzhishubh + " 对应的通知书获取失败，发明名称为：" + row.getString("FAMINGMC") + "，内部编号为：" + row.getString("NEIBUBH"));
									return JSONObject.toJSONString(Result.error(500, "从CPC获取官文失败"));
								}
							} else {
								count++;
							}
						}
					}
				}
			}
//			for (String in : internalNumberArray) {
//
//			}

		} catch (Exception e) {
			log.error("从CPC获取官文失败", e);
//			return Result.error(500, "从CPC获取官文失败");
			if (StringUtils.isNotBlank(callback)) {
				String string = JSONObject.toJSONString(Result.error(500, "从CPC获取官文失败"));
				return callback + "(" + string + ")";
			} else {
				return JSONObject.toJSONString(Result.error(500, "从CPC获取官文失败"));
			}
		}
		if (StringUtils.isNotBlank(callback)) {
			String string = JSONObject.toJSONString(Result.ok("成功获取官文：" + count));
			return callback + "(" + string + ")";
		} else {
			return JSONObject.toJSONString(Result.ok("成功获取官文：" + count));
		}
//		return Result.ok("获取官文成功");
	}
	
	@GetMapping(value = "/getNoticesByPatentNo", produces = "application/jsonp; charset=utf-8")
	public String getNoticesByPatentNo(String callback,@RequestParam(name = "token") String token, HttpServletRequest req) {

		//todo 获取系统的所有案件的内部编号
		String bodyInternal = HttpRequest.get(connecturl + "/caseinfo/shunchaoCaseInfo/selectCaseInfoUsedCpc").
				header("X-Access-Token", token).execute().body();

		JSONObject jsonObjectCaseInfo = JSONObject.parseObject(bodyInternal);
		JSONArray jsonArray = (JSONArray) jsonObjectCaseInfo.get("result");
		List<HashMap> mapList = JSONObject.parseArray(jsonArray.toJSONString(), HashMap.class);
		int count = 0;

        Connection conn = null;
		try {
			//获取官文数量
			String[] column = {"TONGZHISBH", "TONGZHISDM", "FAMINGMC", "FAWENXLH", "TONGZHISMC", "SHENQINGBH",
					"FAWENRQ", "DAFURQ", "QIANMINGXX", "ZHUCEDM", "XIAZAIRQ", "XIAZAICS", "ZHUANGTAI", "SHIFOUSC",
					"NEIBUBH", "GongBuH", "GongBuR", "JinRuSSR", "ShouCiNFND", "WaiGuanFLH", "ShouQuanGGH", "ShouQuanGGR",
					"DaoChuZT", "QIANZHANGBJ"};
			String[] dateFormatColumn = {"FAWENRQ", "DAFURQ", "XIAZAIRQ", "GongBuR", "JinRuSSR", "ShouQuanGGR"};

            conn = DBHelper.getConnection();
			for (int i = 0; i < mapList.size(); i++) {
				StringBuilder sb = new StringBuilder();
				//以获取通知书编号
				String patent = mapList.get(i).get("patentNumber").toString();
				String source= mapList.get(i).get("source").toString();
				if(StringUtils.isNotBlank(mapList.get(i).get("tongZhiSBH").toString())){
					sb.append("(");
					String[] tongZhiSBH = mapList.get(i).get("tongZhiSBH").toString().split(",");
					for (int j = 0; j < tongZhiSBH.length; j++){
						if(j == tongZhiSBH.length-1){
							sb.append("'" + tongZhiSBH[j] + "'");
						}else {
							sb.append("'" + tongZhiSBH[j] + "',");
						}
					}
					sb.append(")");
				}
				StringBuilder sql = new StringBuilder("");
				if("2".equals(mapList.get(i).get("source").toString())){
					String patentNumber = mapList.get(i).get("patentNumber").toString();
					sql.append("select TZS.TONGZHISBH, TZS.TONGZHISDM, TZS.FAMINGMC, TZS.FAWENXLH, TZS.TONGZHISMC, TZS.SHENQINGBH, TZS.FAWENRQ" +
							", TZS.DAFURQ,TZS.QIANMINGXX, TZS.ZHUCEDM, TZS.XIAZAIRQ, TZS.XIAZAICS, TZS.ZHUANGTAI, TZS.SHIFOUSC, TZS.NEIBUBH, TZS.GongBuH" +
							", TZS.GongBuR,TZS.JinRuSSR,TZS.ShouCiNFND, TZS.WaiGuanFLH,TZS.ShouQuanGGH,TZS.ShouQuanGGR,TZS.DaoChuZT,TZS.QIANZHANGBJ " +
							"from DZSQ_KHD_SHENQINGXX SQXX LEFT JOIN DZSQ_KHD_TZS TZS ON SQXX.SHENQINGBH = TZS.SHENQINGBH WHERE SHIFOUSC = '0' " +
							"AND SQXX.SHENQINGH = '" + patentNumber +"'");
                }else {
					String internalNumber = mapList.get(i).get("internalNumber").toString();
					sql.append("select TZS.TONGZHISBH, TZS.TONGZHISDM, TZS.FAMINGMC, TZS.FAWENXLH, TZS.TONGZHISMC, TZS.SHENQINGBH, TZS.FAWENRQ" +
							", TZS.DAFURQ,TZS.QIANMINGXX, TZS.ZHUCEDM, TZS.XIAZAIRQ, TZS.XIAZAICS, TZS.ZHUANGTAI, TZS.SHIFOUSC, TZS.NEIBUBH, TZS.GongBuH" +
							", TZS.GongBuR,TZS.JinRuSSR,TZS.ShouCiNFND, TZS.WaiGuanFLH,TZS.ShouQuanGGH,TZS.ShouQuanGGR,TZS.DaoChuZT,TZS.QIANZHANGBJ" +
							" from DZSQ_KHD_TZS TZS WHERE SHIFOUSC = '0' AND NEIBUBH = '" + internalNumber + "'");
                }

				if(StringUtils.isNotBlank(sb.toString())){
					sql.append(" AND TONGZHISBH NOT IN " + sb.toString());
				}
				Map<String, Object> paramMap = null;
				List<Map<String, Object>> queryMapListBySql = DBHelper.queryMapListBySql(conn, sql.toString(), column);
	            for (Map<String, Object> queryMap : queryMapListBySql) {
	            	paramMap = new HashMap<>();
					ShunchaoAttachmentInfo t = new ShunchaoAttachmentInfo();
	                for (String col : column) {
	                	if (Arrays.asList(dateFormatColumn).contains(col)) {
	                		paramMap.put(col.toLowerCase(), DateUtil.format((Date) queryMap.get(col), "yyyy-MM-dd"));
	                	}else {
	                		paramMap.put(col.toLowerCase(), queryMap.get(col));
	                	}
	                }
	                String tongzhishubh = (String) queryMap.get("TONGZHISBH");
	                File file = new File(CpcPathInComputer.getCpcBinPathWindowsComputer() + File.separator + basecpc + File.separator + notices + File.separator + tongzhishubh);
	                
					if (file.exists()) {
						paramMap.put("file", ZipUtil.zip(file));
						HttpResponse execute = HttpRequest.post(connecturl + "/notice/shunchaoDzsqKhdTzs/upload").
								header("X-Access-Token", token).form(paramMap).execute();
						String body = execute.body();
						JSONObject jsonObject = JSONObject.parseObject(body);
						Boolean success = (Boolean) jsonObject.get("success");
						Integer code = (Integer) jsonObject.get("code");
						if (!success) {
							if (40002 == code) {
								log.info("通知书编号：" + tongzhishubh + " 对应的通知书系统已经获取，无需重复获取，发明名称为：" + (String) queryMap.get("FAMINGMC") + "，内部编号为：" + (String) queryMap.get("NEIBUBH"));
							} else {
								log.info("通知书编号：" + tongzhishubh + " 对应的通知书获取失败，发明名称为：" + (String) queryMap.get("FAMINGMC") + "，内部编号为：" + (String) queryMap.get("NEIBUBH"));
								return JSONObject.toJSONString(Result.error(500, "从CPC获取官文失败"));
							}
						} else {
							count++;
						}
					}
	            }    
				//String internalNumber = mapList.get(i).get("internalNumber").toString();
			}
		} catch (Exception e) {
			log.error("从CPC获取官文失败", e);
			if (StringUtils.isNotBlank(callback)) {
				String string = JSONObject.toJSONString(Result.error(500, "从CPC获取官文失败"));
				return callback + "(" + string + ")";
			} else {
				return JSONObject.toJSONString(Result.error(500, "从CPC获取官文失败"));
			}
		}
		if (StringUtils.isNotBlank(callback)) {
			String string = JSONObject.toJSONString(Result.ok("成功获取官文：" + count));
			return callback + "(" + string + ")";
		} else {
			return JSONObject.toJSONString(Result.ok("成功获取官文：" + count));
		}
//		return Result.ok("获取官文成功");
	}

     @PostMapping(value = "/upload")
     public String upload(HttpServletRequest request, HttpServletResponse response) {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile mf = multipartRequest.getFile("file");// 获取上传文件对象
		String orgName = mf.getOriginalFilename();// 获取文件名
		System.out.println("文件名" + orgName);
	    return "12345";
     }
}
