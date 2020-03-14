package com.shunchao.cpc.controller;

import java.io.*;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.ZipUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
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
import lombok.extern.slf4j.Slf4j;

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

	@GetMapping(value = "/sendCase")
	public Result<?> sendCase(HttpServletRequest req) {
		HashMap<String, Object> paramMap = new HashMap<>();
		String id = req.getParameter("id");
		String token = req.getParameter("token");
		paramMap.put("id", id);

//		String result = HttpUtil.get(connecturl + "/sendcpc/shunchaoSendCpcCase/queryCaseInfoById", paramMap);
		String body = HttpRequest.get(connecturl + "/sendcpc/shunchaoSendCpcCase/queryCaseInfoById").
				header("X-Access-Token", token).form(paramMap).execute().body();

		try {
			JSONObject jsonObject = JSONObject.parseObject(body);
			JSONObject resultObject = (JSONObject) jsonObject.get("result");
			String caseInfoObject = resultObject.getString("shunchaoCaseInfo");
			String attachmentListObject =  resultObject.getString("attachmentList");
			ShunchaoCaseInfo shunchaoCaseInfo = JSONObject.parseObject(caseInfoObject, ShunchaoCaseInfo.class);
			List<ShunchaoAttachmentInfo> shunchaoAttachmentInfoList = JSONObject.parseArray(attachmentListObject, ShunchaoAttachmentInfo.class);
			shuncaoConnectService.sendCase(shunchaoCaseInfo, shunchaoAttachmentInfoList, token);
//			FileCopyUtils.copy();
		} catch (Exception e) {
			log.error("获取向CPC送案失败", e);
			return Result.error(500, "获取向CPC送案失败");
		}

		return Result.ok("向CPC送案成功");
	}

	@GetMapping(value = "/getNotices")
	public Result<?> getNotices(@RequestParam(name = "internalNumbers") String internalNumbers,@RequestParam(name = "token") String token, HttpServletRequest req) {

		try {
			String dataPath = CpcPathInComputer.getCpcDataPathWindowsComputer();
			Database db = DatabaseBuilder.open(new File(dataPath));

			Table table = db.getTable("DZSQ_KHD_TZS");
			Table fjTable = db.getTable("DZSQ_KHD_WJFJ");
			String[] internalNumberArray = internalNumbers.split(",");
			for (String in : internalNumberArray) {
				for (Row row : table) {
					if (in.equals(row.getString("NEIBUBH"))) {
						HashMap<String, Object> paramMap = new HashMap<>();
						ShunchaoAttachmentInfo t = new ShunchaoAttachmentInfo();
                        paramMap.put("attachmentName",row.getString("NEIBUBH"));
						//通知书编号
						String tongzhishubh = row.getString("TONGZHISBH");
						/*for (Row r : fjTable) {
							if (tongzhishubh.equals(r.getString("TONGZHISBH"))) {
                                paramMap.put("attachmentSize",r.getString("FUJIANDX"));
							}
						}*/

						paramMap.put("file", ZipUtil.zip(new File("D:\\notices\\" + tongzhishubh)));
						HttpResponse execute = HttpRequest.post("http://localhost:8080/jeecg-boot" + "/notice/shunchaoDzsqKhdTzs/upload").
								header("X-Access-Token", token).form(paramMap).execute();
					}
				}
			}

		} catch (Exception e) {
			log.error("获取官文失败", e);
			return Result.error(500, "获取官文失败");
		}

		return Result.ok("获取官文成功");
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
