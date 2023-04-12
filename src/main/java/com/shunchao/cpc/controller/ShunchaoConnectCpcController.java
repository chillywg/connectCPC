package com.shunchao.cpc.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.XmlUtil;
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
import com.shunchao.cpc.model.ShunchaoTrademarkAnnex;
import com.shunchao.cpc.service.IShuncaoConnectService;
import com.shunchao.cpc.util.CpcUtils;
import com.shunchao.cpc.util.DBHelper;
import com.shunchao.cpc.util.SqliteDBUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.w3c.dom.Document;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.xpath.XPathConstants;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;

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
	 * 功能描述:加载商标局官网所需附件
	 * 场景:
	 * @Param: [trademarkAnnexList, request]
	 * @Return: java.lang.String
	 * @Author: Ironz
	 * @Date: 2022/1/12 14:05
	 */
	@PostMapping(value = "/getTrademarkAnnex")
	public Result<?> getTrademarkAnnex(@RequestBody List<ShunchaoTrademarkAnnex> trademarkAnnexList,HttpServletRequest request,HttpServletResponse response){
		//response.setHeader("Access-Control-Allow-Origin", "http://user.duou.com");
		String token = request.getParameter("token");
		String mark = shuncaoConnectService.getTrademarkAnnex(trademarkAnnexList,token);
		if ("0".equals(mark)) {
			return Result.ok(mark);
		}else {
			return Result.error(mark);
		}
	}


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
				/*paramMap.put("id", sid);
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
						header("X-Access-Token", token).form(param).execute();*/

				long l = System.currentTimeMillis();

				HashMap<String, Object> mapPara = new HashMap<>();
				mapPara.put("id", sid);
				HttpResponse execute = HttpRequest.get(connecturl + "/sendcpc/shunchaoSendCpcCase/downloadNewCPCFileBag").
						header("X-Access-Token", token).form(mapPara).execute();
				String body1 = execute.body();
				JSONObject parseObject = JSONObject.parseObject(body1);
				JSONObject result = (JSONObject) parseObject.get("result");
//				String message = parseObject.getString("message");
				String content = result.getString("content");
				String filename = result.getString("filename");
				byte[] bytes1 = Base64Utils.decodeFromString(content);

				System.out.println(System.currentTimeMillis() - l);

				long a = System.currentTimeMillis();
//				HttpRequest.post("http://localhost:9999/common/anjian/import").header("X-Access-Token", token).form("file", bytes1, "123.zip");
				InputStream in = new ByteArrayInputStream(bytes1);
				org.jsoup.Connection.Response response = Jsoup.connect("http://localhost:9999/common/anjian/import")
						.data("file",filename,in).method(org.jsoup.Connection.Method.POST).ignoreContentType(true).execute();
				System.out.println(System.currentTimeMillis() - a);
				HashMap<String, Object> param = new HashMap<>();
				param.put("id", sid);
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

	@GetMapping(value = "/getNoticesc", produces = "application/jsonp; charset=utf-8")
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
	
	@GetMapping(value = "/getNotices", produces = "application/jsonp; charset=utf-8")
	public String getNoticesByPatentNo2(String callback,@RequestParam(name = "token") String token, HttpServletRequest req) {
		//获取系统的所有案件的内部编号
		int count = 0;//常规官文总数
		int signature = 0;//签章官文总数
		int receipt = 0;//电子申请回执总数

		int fail = 0;//失败总数

        Connection conn = null;
		try {
			//获取官文数量
			String[] column;
			String[] dateFormatColumn = {"FAWENRQ", "DAFURQ", "XIAZAIRQ", "GongBuR", "JinRuSSR", "ShouQuanGGR"};

            conn = DBHelper.getConnection();
			StringBuilder sql = new StringBuilder("");
			sql.append("select SQXX.SHENQINGH, TZS.TONGZHISBH, TZS.TONGZHISDM, TZS.FAMINGMC, TZS.FAWENXLH, TZS.TONGZHISMC, TZS.SHENQINGBH, TZS.FAWENRQ" +
					", TZS.DAFURQ,TZS.QIANMINGXX, TZS.ZHUCEDM, TZS.XIAZAIRQ, TZS.XIAZAICS, TZS.ZHUANGTAI, TZS.SHIFOUSC, TZS.NEIBUBH, TZS.GongBuH" +
					", TZS.GongBuR,TZS.JinRuSSR,TZS.ShouCiNFND, TZS.WaiGuanFLH,TZS.ShouQuanGGH,TZS.ShouQuanGGR,TZS.DaoChuZT,TZS.QIANZHANGBJ " +
					"from DZSQ_KHD_TZS TZS LEFT JOIN DZSQ_KHD_SHENQINGXX SQXX ON SQXX.SHENQINGBH = TZS.SHENQINGBH WHERE SHIFOUSC = '0'");
			column = new String[]{"TONGZHISBH", "TONGZHISDM", "FAMINGMC", "FAWENXLH", "TONGZHISMC", "SHENQINGBH","SHENQINGH",
					"FAWENRQ", "DAFURQ", "QIANMINGXX", "ZHUCEDM", "XIAZAIRQ", "XIAZAICS", "ZHUANGTAI", "SHIFOUSC",
					"NEIBUBH", "GongBuH", "GongBuR", "JinRuSSR", "ShouCiNFND", "WaiGuanFLH", "ShouQuanGGH", "ShouQuanGGR",
					"DaoChuZT", "QIANZHANGBJ"};
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
						}else if(40003 == code){
							log.info("通知书编号：" + tongzhishubh + " 未匹配到系统中案件，发明名称为：" + (String) queryMap.get("FAMINGMC") + "，内部编号为：" + (String) queryMap.get("NEIBUBH"));

						}else {
							log.info("通知书编号：" + tongzhishubh + " 对应的通知书获取失败，通知书名称：" + queryMap.get("TONGZHISMC") + "，发明名称为：" + (String) queryMap.get("FAMINGMC") + "，内部编号为：" + (String) queryMap.get("NEIBUBH"));
							fail++;
//							return JSONObject.toJSONString(Result.error(500, "从CPC获取官文失败"));
						}
					} else {
						String qianzhangbj = paramMap.get("qianzhangbj").toString();

						if ("1".equals(qianzhangbj)) {
							signature++;
						} else {
							String tongzhisdm = paramMap.get("tongzhisdm").toString();
							if ("200105".equals(tongzhisdm)) {
								receipt++;
							} else {
								count++;
							}
						}
					}
				}
			}
			/*	String bodyInternal = HttpRequest.get(connecturl + "/caseinfo/shunchaoCaseInfo/selectCaseInfoUsedCpc").
				header("X-Access-Token", token).execute().body();
				JSONObject jsonObjectCaseInfo = JSONObject.parseObject(bodyInternal);
				JSONArray jsonArray = (JSONArray) jsonObjectCaseInfo.get("result");
				List<HashMap> mapList = JSONObject.parseArray(jsonArray.toJSONString(), HashMap.class);

				for (int i = 0; i < mapList.size(); i++) {
					StringBuilder sb = new StringBuilder();
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
					String patentNumber = mapList.get(i).get("patentNumber").toString();
					String internalNumber = mapList.get(i).get("internalNumber").toString();
					StringBuilder sql = new StringBuilder("");
					if(StringUtils.isNotBlank(patentNumber)){
					sql.append("select SQXX.SHENQINGH, TZS.TONGZHISBH, TZS.TONGZHISDM, TZS.FAMINGMC, TZS.FAWENXLH, TZS.TONGZHISMC, TZS.SHENQINGBH, TZS.FAWENRQ" +
							", TZS.DAFURQ,TZS.QIANMINGXX, TZS.ZHUCEDM, TZS.XIAZAIRQ, TZS.XIAZAICS, TZS.ZHUANGTAI, TZS.SHIFOUSC, TZS.NEIBUBH, TZS.GongBuH" +
							", TZS.GongBuR,TZS.JinRuSSR,TZS.ShouCiNFND, TZS.WaiGuanFLH,TZS.ShouQuanGGH,TZS.ShouQuanGGR,TZS.DaoChuZT,TZS.QIANZHANGBJ " +
							"from DZSQ_KHD_TZS TZS LEFT JOIN DZSQ_KHD_SHENQINGXX SQXX ON SQXX.SHENQINGBH = TZS.SHENQINGBH WHERE SHIFOUSC = '0' " +
							"AND SQXX.SHENQINGH = '" + patentNumber +"'");
					column = new String[]{"TONGZHISBH", "TONGZHISDM", "FAMINGMC", "FAWENXLH", "TONGZHISMC", "SHENQINGBH","SHENQINGH",
							"FAWENRQ", "DAFURQ", "QIANMINGXX", "ZHUCEDM", "XIAZAIRQ", "XIAZAICS", "ZHUANGTAI", "SHIFOUSC",
							"NEIBUBH", "GongBuH", "GongBuR", "JinRuSSR", "ShouCiNFND", "WaiGuanFLH", "ShouQuanGGH", "ShouQuanGGR",
							"DaoChuZT", "QIANZHANGBJ"};
				}else {
					sql.append("select TZS.TONGZHISBH, TZS.TONGZHISDM, TZS.FAMINGMC, TZS.FAWENXLH, TZS.TONGZHISMC, TZS.SHENQINGBH, TZS.FAWENRQ" +
							", TZS.DAFURQ,TZS.QIANMINGXX, TZS.ZHUCEDM, TZS.XIAZAIRQ, TZS.XIAZAICS, TZS.ZHUANGTAI, TZS.SHIFOUSC, TZS.NEIBUBH, TZS.GongBuH" +
							", TZS.GongBuR,TZS.JinRuSSR,TZS.ShouCiNFND, TZS.WaiGuanFLH,TZS.ShouQuanGGH,TZS.ShouQuanGGR,TZS.DaoChuZT,TZS.QIANZHANGBJ" +
							" from DZSQ_KHD_TZS TZS WHERE SHIFOUSC = '0' AND NEIBUBH = '" + internalNumber + "'");
					column = new String[]{"TONGZHISBH", "TONGZHISDM", "FAMINGMC", "FAWENXLH", "TONGZHISMC", "SHENQINGBH",
							"FAWENRQ", "DAFURQ", "QIANMINGXX", "ZHUCEDM", "XIAZAIRQ", "XIAZAICS", "ZHUANGTAI", "SHIFOUSC",
							"NEIBUBH", "GongBuH", "GongBuR", "JinRuSSR", "ShouCiNFND", "WaiGuanFLH", "ShouQuanGGH", "ShouQuanGGR",
							"DaoChuZT", "QIANZHANGBJ"};
				}
				if(StringUtils.isNotBlank(sb.toString())){
					sql.append(" AND TONGZHISBH NOT IN " + sb.toString());

				}*/
		} catch (Exception e) {
			log.error("从CPC获取官文失败", e);
			if (StringUtils.isNotBlank(callback)) {
				String string = JSONObject.toJSONString(Result.error(500, "从CPC获取官文失败"));
				return callback + "(" + string + ")";
			} else {
				return JSONObject.toJSONString(Result.error(500, "从CPC获取官文失败"));
			}
		}
		String result = "成功获取常规官文：" + count + "，<br>签章官文：" + signature + "，<br>电子申请回执：" + receipt + "，<br>获取失败总数：" + fail;//<br>标签由前端处理换行
		if (StringUtils.isNotBlank(callback)) {
			String string = JSONObject.toJSONString(Result.ok(result));
			return callback + "(" + string + ")";
		} else {
			return JSONObject.toJSONString(Result.ok(result));
		}
//		return Result.ok("获取官文成功");
	}
	@GetMapping(value = "/getNotices2", produces = "application/jsonp; charset=utf-8")
	public String getNoticesByPatentNo(String callback, @RequestParam(name = "token") String token, HttpServletRequest req) throws Exception {
		String sql ="SELECT zxsq_dzfwbxx_t_rid,yewuztbh,fawenbcflj,fawenxlh,zhuanlimc,fawenbmc,tongzhismc,tongzhislx,dianzifwrq,create_user FROM zxsq_dzfwbxx_t where del_flag = 0 and fawenbcflj is not NULL and fawenbcflj !=''";
		String[] column = new String[]{"zxsq_dzfwbxx_t_rid","yewuztbh", "fawenbcflj", "fawenxlh", "zhuanlimc","fawenbmc", "tongzhismc", "tongzhislx","dianzifwrq","create_user"};
		String[] column2 = new String[]{"zxsqDzfwbxxTRid","SHENQINGH", "CUNCHULUJING", "FAWENXLH", "FAMINGMC","TONGZHISBH", "TONGZHISMC", "TONGZHISDM","FAWENRQ","phone"};
		List<Map<String, Object>> maps = SqliteDBUtils.queryMapListBySql(sql, column,column2);
		Map map = System.getenv();
		String cnipa_client_home = map.get("CNIPA_CLIENT_HOME").toString();
		cnipa_client_home = cnipa_client_home +"\\plugins\\as\\cpc-main-as\\data";
		Map<String, Object> paramMap = null;
		int fail = 0;//失败总数
		int count = 0;//常规官文总数
		int receipt = 0;//电子申请回执总数
		for(Map<String, Object> queryMap : maps){
			paramMap = new HashMap<>();
			String CUNCHULUJING = (String) queryMap.get("CUNCHULUJING");
			Integer zxsqDzfwbxxTRid = (Integer) queryMap.get("zxsqDzfwbxxTRid");
			CUNCHULUJING=cnipa_client_home+CUNCHULUJING.substring(11,CUNCHULUJING.length())+"\\";
			File xmlFile =new File(CUNCHULUJING+"\\list.xml");
			Document docResult = XmlUtil.readXML(xmlFile);
			//获取官文中内部编号
			String neibubh = XmlUtil.getByXPath("//data-bus/TONGZHISXJ/SHUXINGXX/NEIBUBH", docResult, XPathConstants.STRING).toString();
			queryMap.put("neibubh",neibubh);
			paramMap.put("neibubh",neibubh);
			paramMap.put("qianzhangbj","0");

			File file = new File(CUNCHULUJING+zxsqDzfwbxxTRid+".zip");
			String tongzhishubh = (String) queryMap.get("TONGZHISBH");

			for (String col : column2) {
				paramMap.put(col.toLowerCase(), queryMap.get(col));
			}
			if (file.exists()) {
//                paramMap.put("file", ZipUtil.zip(file));
				paramMap.put("file", file);
				HttpResponse execute = HttpRequest.post(connecturl + "/notice/shunchaoDzsqKhdTzs/upload").
						header("X-Access-Token", token).form(paramMap).execute();
				String body = execute.body();
				JSONObject jsonObject = JSONObject.parseObject(body);
				Boolean success = (Boolean) jsonObject.get("success");
				Integer code = (Integer) jsonObject.get("code");
				if (!success) {
					if (40002 == code) {
						String updatesql ="update zxsq_dzfwbxx_t set del_flag = 1 where zxsq_dzfwbxx_t_rid = "+zxsqDzfwbxxTRid;
						SqliteDBUtils.update(updatesql);
						log.info("通知书编号：" + tongzhishubh + " 对应的通知书获取失败，通知书名称：" + queryMap.get("TONGZHISMC") +" 对应的通知书系统已经获取，无需重复获取，发明名称为：" + (String) queryMap.get("FAMINGMC") + "，内部编号为：" + (String) queryMap.get("neibubh"));
					}else if(40003 == code){
						log.info("通知书编号：" + tongzhishubh + " 对应的通知书获取失败，通知书名称：" + queryMap.get("TONGZHISMC") +" 未匹配到系统中案件，发明名称为：" + (String) queryMap.get("FAMINGMC") + "，内部编号为：" + (String) queryMap.get("neibubh"));

					}else if(40006 == code){
						log.info("通知书编号：" + tongzhishubh + " 对应的通知书获取失败，通知书名称：" + queryMap.get("TONGZHISMC") +" 该通知书与压缩包文件内容不一致，发明名称为：" + (String) queryMap.get("FAMINGMC") + "，内部编号为：" + (String) queryMap.get("neibubh"));

					}else {
						log.info("通知书编号：" + tongzhishubh + " 对应的通知书获取失败，通知书名称：" + queryMap.get("TONGZHISMC") + "，发明名称为：" + (String) queryMap.get("FAMINGMC") + "，内部编号为：" + (String) queryMap.get("neibubh"));
						fail++;
//							return JSONObject.toJSONString(Result.error(500, "从CPC获取官文失败"));
					}
				} else {
					String updatesql ="update zxsq_dzfwbxx_t set del_flag = 1 where zxsq_dzfwbxx_t_rid = "+zxsqDzfwbxxTRid;
					SqliteDBUtils.update(updatesql);
//                    String qianzhangbj = paramMap.get("qianzhangbj").toString();
					String tongzhisdm = paramMap.get("tongzhisdm").toString();
					if ("200105".equals(tongzhisdm)) {
						receipt++;
					} else {
						count++;
					}
				}
			}
		}
		String result = "成功获取常规官文：" + count +  "，<br>电子申请回执：" + receipt + "，<br>获取失败总数：" + fail;//<br>标签由前端处理换行
		if (StringUtils.isNotBlank(callback)) {
			String string = JSONObject.toJSONString(Result.ok(result));
			return callback + "(" + string + ")";
		} else {
			return JSONObject.toJSONString(Result.ok(result));
		}
	}
	/**
	* @Description : 获取CPC官文（走Fork join 框架）
	* @Param [callback, token, req]
	* @return:java.lang.String
	* @Author:FuQiangCalendar
	* @Date: 2021/5/8 12:09
	*/
	@GetMapping(value = "/getNoticesNew", produces = "application/jsonp; charset=utf-8")
	public String getNoticesByPatentNoNew(String callback,@RequestParam(name = "token") String token, HttpServletRequest req) {
		return shuncaoConnectService.getNoticesByPatentNo(callback, token, req);
	}

     @PostMapping(value = "/upload")
     public String upload(HttpServletRequest request, HttpServletResponse response) {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile mf = multipartRequest.getFile("file");// 获取上传文件对象
		String orgName = mf.getOriginalFilename();// 获取文件名
		System.out.println("文件名" + orgName);
	    return "12345";
     }

     //处理新cpc系统路径问题官文
	@GetMapping(value = "/getNotices3", produces = "application/jsonp; charset=utf-8")
	public String getNoticesByPatentNo3(String callback, @RequestParam(name = "token") String token, HttpServletRequest req) throws Exception {
		String sql ="SELECT zxsq_dzfwbxx_t_rid,yewuztbh,fawenbcflj,fawenxlh,zhuanlimc,fawenbmc,tongzhismc,tongzhislx,dianzifwrq FROM zxsq_dzfwbxx_t where fawenbcflj is not NULL and fawenbcflj !=''";
		String[] column = new String[]{"zxsq_dzfwbxx_t_rid","yewuztbh", "fawenbcflj", "fawenxlh", "zhuanlimc","fawenbmc", "tongzhismc", "tongzhislx","dianzifwrq"};
		String[] column2 = new String[]{"zxsqDzfwbxxTRid","SHENQINGH", "CUNCHULUJING", "FAWENXLH", "FAMINGMC","TONGZHISBH", "TONGZHISMC", "TONGZHISDM","FAWENRQ"};
		List<Map<String, Object>> maps = SqliteDBUtils.queryMapListBySql(sql, column,column2);
		Map map = System.getenv();
		String cnipa_client_home = map.get("CNIPA_CLIENT_HOME").toString();
		cnipa_client_home = cnipa_client_home +"\\plugins\\as\\cpc-main-as\\data";
		Map<String, Object> paramMap = null;
		int fail = 0;//失败总数
		int count = 0;//常规官文总数
		int receipt = 0;//电子申请回执总数
		for(Map<String, Object> queryMap : maps){
			paramMap = new HashMap<>();
			String CUNCHULUJING = (String) queryMap.get("CUNCHULUJING");
			Integer zxsqDzfwbxxTRid = (Integer) queryMap.get("zxsqDzfwbxxTRid");
			CUNCHULUJING=cnipa_client_home+CUNCHULUJING.substring(11,CUNCHULUJING.length())+"\\";

			File file = new File(CUNCHULUJING+zxsqDzfwbxxTRid+".zip");
			String tongzhishubh = (String) queryMap.get("TONGZHISBH");

			for (String col : column2) {
				paramMap.put(col.toLowerCase(), queryMap.get(col));
			}
			if (file.exists()) {
//                paramMap.put("file", ZipUtil.zip(file));
				paramMap.put("file", file);
				HttpResponse execute = HttpRequest.post(connecturl + "/notice/shunchaoDzsqKhdTzs/upload2").
						header("X-Access-Token", token).form(paramMap).execute();
				String body = execute.body();
				JSONObject jsonObject = JSONObject.parseObject(body);
				Boolean success = (Boolean) jsonObject.get("success");
				Integer code = (Integer) jsonObject.get("code");
				if (!success) {
					if (40001 == code) {
						fail++;
						log.info("失败通知书编号：" + tongzhishubh + " 官文为空，通知书名称：" + queryMap.get("TONGZHISMC") +"发明名称为：" + (String) queryMap.get("FAMINGMC"));
					}else if(40002 == code){
						fail++;
						log.info("失败通知书编号：" + tongzhishubh + " 编号为空，通知书名称：" + queryMap.get("TONGZHISMC") +"发明名称为：" + (String) queryMap.get("FAMINGMC"));
					}else if(40003 == code){
						count++;
						log.info("成功通知书编号：" + tongzhishubh + " 不需要替换，通知书名称：" + queryMap.get("TONGZHISMC") +"发明名称为：" + (String) queryMap.get("FAMINGMC"));
					}else{
						fail++;
						log.info("失败通知书编号：" + tongzhishubh + " 异常报错，通知书名称：" + queryMap.get("TONGZHISMC") +"发明名称为：" + (String) queryMap.get("FAMINGMC"));
					}
				} else {
					count++;
					log.info("成功通知书编号：" + tongzhishubh + "通知书名称：" + queryMap.get("TONGZHISMC") +"发明名称为：" + (String) queryMap.get("FAMINGMC"));
				}
			}
		}
		String result = "成功处理官文：" + count+ "，<br>失败处理总数：" + fail;//<br>标签由前端处理换行
		if (StringUtils.isNotBlank(callback)) {
			String string = JSONObject.toJSONString(Result.ok(result));
			return callback + "(" + string + ")";
		} else {
			return JSONObject.toJSONString(Result.ok(result));
		}
	}
	@GetMapping(value = "/getNotices4", produces = "application/jsonp; charset=utf-8")
	public String getPatentCertificate(String callback, @RequestParam(name = "token") String token, HttpServletRequest req) throws Exception {
//		String fawenrStart= "2023-04-11";//开始时间
		String fawenrStart= "";
//		String fawenrEnd = new SimpleDateFormat("yyyy-MM-dd").format(new Date());//结束时间
		String fawenrEnd ="";
		String xiazaizt = "1";//("":全部，1：待下载，2：已下载)
		List<Map<String,Object>> maps = CpcUtils.getPatentCertificate(fawenrStart,fawenrEnd,xiazaizt,"");//注:开始时间和结束时间必须同时传值
		int fail = 0;//失败总数
		int count = 0;//常规官文总数
		for(Map<String, Object> paramMap : maps){
			String dbPath = CpcUtils.inportFile(paramMap.get("fid").toString(), paramMap.get("tongzhisbh").toString());
			paramMap.put("qianzhangbj","0");
			//解压压缩包
			File unzip = ZipUtil.unzip(new File(dbPath));
			File[] files = unzip.listFiles();
			File file = null;
			for (File f : files) {
				if (f.isFile() && f.getPath().contains(paramMap.get("fawenxlh").toString())) {
					file = f;
					break;
				}
			}
			String tongzhishubh = (String) paramMap.get("tongzhisbh");
			if (file.exists()) {
//                paramMap.put("file", ZipUtil.zip(file));
				paramMap.put("file", file);
				HttpResponse execute = HttpRequest.post(connecturl + "/notice/shunchaoDzsqKhdTzs/upload").
						header("X-Access-Token", token).form(paramMap).execute();
				String body = execute.body();
				JSONObject jsonObject = JSONObject.parseObject(body);
				Boolean success = (Boolean) jsonObject.get("success");
				Integer code = (Integer) jsonObject.get("code");
				if (!success) {
					if (40002 == code) {
						log.info("通知书编号：" + tongzhishubh + " 对应的证书获取失败，通知书名称：" + paramMap.get("tongzhismc") +" 对应的通知书系统已经获取，无需重复获取，发明名称为：" + (String) paramMap.get("famingmc") );
					}else if(40003 == code){
						fail++;
						log.info("通知书编号：" + tongzhishubh + " 对应的证书获取失败，通知书名称：" + paramMap.get("tongzhismc") +" 未匹配到系统中案件，发明名称为：" + (String) paramMap.get("famingmc") );
					}else if(40006 == code){
						fail++;
						log.info("通知书编号：" + tongzhishubh + " 对应的证书获取失败，通知书名称：" + paramMap.get("tongzhismc") +" 该通知书与压缩包文件内容不一致，发明名称为：" + (String) paramMap.get("famingmc") );
					}else {
						fail++;
						log.info("通知书编号：" + tongzhishubh + " 对应的证书获取失败，通知书名称：" + paramMap.get("tongzhismc") + "，发明名称为：" + (String) paramMap.get("famingmc"));
					}
				} else {
					count++;
				}
			}
		}
		String result = "成功获取证书：" + count +  "，<br>获取失败总数：" + fail;//<br>标签由前端处理换行
		if (StringUtils.isNotBlank(callback)) {
			String string = JSONObject.toJSONString(Result.ok(result));
			return callback + "(" + string + ")";
		} else {
			return JSONObject.toJSONString(Result.ok(result));
		}
	}
}
