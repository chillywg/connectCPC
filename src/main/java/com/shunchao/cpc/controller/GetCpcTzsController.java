package com.shunchao.cpc.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.XmlUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSONObject;
import com.shunchao.cpc.model.Result;
import com.shunchao.cpc.util.SqliteDBUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;

import javax.servlet.http.HttpServletRequest;
import javax.xml.xpath.XPathConstants;
import java.io.File;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/cpc/connectCpc2")
public class GetCpcTzsController {
    @Value(value = "${connecturl}")
    private String connecturl;

    @GetMapping(value = "/getNotices", produces = "application/jsonp; charset=utf-8")
    public String getNoticesByPatentNo(String callback, @RequestParam(name = "token") String token, HttpServletRequest req) throws Exception {
        String shunchaoDzsqKhdTzs = HttpRequest.get(connecturl + "/notice/shunchaoDzsqKhdTzs/getFawenrStart").
                header("X-Access-Token", token).execute().body();
        JSONObject json = JSONObject.parseObject(shunchaoDzsqKhdTzs);
        Boolean succ = (Boolean) json.get("success");
        String fawenrStart ="";
        if (succ) {
            if(Objects.isNull(json.get("result"))){
                if (StringUtils.isNotBlank(callback)) {
                    String string = JSONObject.toJSONString(Result.error(500, "上一次官文日期获取失败"));
                    return callback + "(" + string + ")";
                }else{
                    return JSONObject.toJSONString(Result.error(500, "上一次官文日期获取失败"));
                }
            }else{
                fawenrStart = json.get("result").toString();
            }
        }else{
            if (StringUtils.isNotBlank(callback)) {
                String string = JSONObject.toJSONString(Result.error(500, "上一次官文日期获取失败"));
                return callback + "(" + string + ")";
            } else {
                return JSONObject.toJSONString(Result.error(500, "上一次官文日期获取失败"));
            }
        }
//        String rId = CpcUtils.getRId(fawenrStart);
        String sql ="SELECT zxsq_dzfwbxx_t_rid,yewuztbh,fawenbcflj,fawenxlh,zhuanlimc,fawenbmc,tongzhismc,tongzhislx,dianzifwrq FROM zxsq_dzfwbxx_t where del_flag = 0";
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
            File xmlFile =new File(CUNCHULUJING+"\\list.xml");
            Document docResult = XmlUtil.readXML(xmlFile);
            //获取官文中内部编号
            String neibubh = XmlUtil.getByXPath("//data-bus/TONGZHISXJ/SHUXINGXX/NEIBUBH", docResult, XPathConstants.STRING).toString();
            paramMap.put("neibubh",neibubh);
            paramMap.put("qianzhangbj","0");

            File file = new File(CUNCHULUJING+zxsqDzfwbxxTRid+".zip");
            Integer tongzhishubh = (Integer) queryMap.get("zxsqDzfwbxxTRid");

            for (String col : column2) {
                paramMap.put(col.toLowerCase(), queryMap.get(col));
            }
            if (file.exists()) {
                String updatesql ="update zxsq_dzfwbxx_t set del_flag = 1 where zxsq_dzfwbxx_t_rid = "+tongzhishubh;
                SqliteDBUtils.update(updatesql);
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
                        log.info("通知书编号：" + tongzhishubh + " 对应的通知书系统已经获取，无需重复获取，发明名称为：" + (String) queryMap.get("FAMINGMC") + "，内部编号为：" + (String) queryMap.get("NEIBUBH"));
                    }else if(40003 == code){
                        log.info("通知书编号：" + tongzhishubh + " 未匹配到系统中案件，发明名称为：" + (String) queryMap.get("FAMINGMC") + "，内部编号为：" + (String) queryMap.get("NEIBUBH"));

                    }else {
                        log.info("通知书编号：" + tongzhishubh + " 对应的通知书获取失败，通知书名称：" + queryMap.get("TONGZHISMC") + "，发明名称为：" + (String) queryMap.get("FAMINGMC") + "，内部编号为：" + (String) queryMap.get("NEIBUBH"));
                        fail++;
//							return JSONObject.toJSONString(Result.error(500, "从CPC获取官文失败"));
                    }
                } else {
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

}
