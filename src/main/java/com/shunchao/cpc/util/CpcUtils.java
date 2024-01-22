package com.shunchao.cpc.util;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shunchao.cpc.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


@Slf4j
public class CpcUtils {
    /**
     * http://localhost:9999/docs/v1/list
     * 获取cpc系统电子专利证书
     */
    public static List<Map<String,Object>> getPatentCertificate(String fawenrStart,String fawenrEnd,String xiazaizt,String shenqingh) throws IOException {
        //切换到电子票据交付系统的登录对话框
        Map<String, Object> map=getConfirmed(fawenrStart,fawenrEnd,xiazaizt,shenqingh,10);
        int total= Integer.parseInt(map.get("total").toString());
        Map<String, Object> map1=getConfirmed(fawenrStart,fawenrEnd,xiazaizt,shenqingh,total);
        List<Map<String,Object>> records2 = (List) map1.get("records");
        List<Map<String,Object>> shunchaoDzsqKhdTzsList=new ArrayList<>();
        for(Map<String,Object> map2:records2){
            Map<String,Object> shunchaoDzsqKhdTzs = new HashMap<>();
            shunchaoDzsqKhdTzs.put("fid",map2.get("fid").toString());
            shunchaoDzsqKhdTzs.put("shenqingh",map2.get("zhuanlisqh").toString());
            shunchaoDzsqKhdTzs.put("tongzhismc",map2.get("certName").toString());
            shunchaoDzsqKhdTzs.put("famingmc",map2.get("zhuanlimc").toString());
            shunchaoDzsqKhdTzs.put("tongzhisbh",map2.get("fawenxlh").toString());
            shunchaoDzsqKhdTzs.put("fawenxlh",map2.get("fawenxlh").toString());
            shunchaoDzsqKhdTzs.put("fawenrq",map2.get("fawenDate").toString().substring(0,10));
            shunchaoDzsqKhdTzsList.add(shunchaoDzsqKhdTzs);
        }
        return shunchaoDzsqKhdTzsList;
    }
    /**
     * http://localhost:9999/certs/v1/list
     * 获取cpc系统已确认列表官文
     */
    public static Map<String, Object> getConfirmed(String fawenrStart,String fawenrEnd,String xiazaizt,String shenqingh,int size) throws IOException {
        //切换到电子票据交付系统的登录对话框
        //2022222057897，2022226093197,2022225939714
        String body="{\"famingczmc\":\"\",\"fawenrStart\":\""+fawenrStart+"\",\"fawenrEnd\":\""+fawenrEnd+"\",\"fawenxlh\":\"\",\"shenqingh\":\""+shenqingh+"\",\"xiazaizt\":\""+xiazaizt+"\",\"zhanghu\":\"\",\"size\":"+size+",\"current\":1}";
        Connection.Response response3 = Jsoup.connect("http://localhost:9999/certs/v1/list")
                .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) cnipa-cpc/0.1.3 Chrome/87.0.4280.141 Electron/11.3.0 Safari/537.36")
                .header("Accept","application/json, text/plain, */*")
                .header("Accept-Encoding","gzip, deflate, br")
                .header("Connection", "keep-alive")
                .header("Content-Type","application/json;charset=UTF-8")
                .requestBody(body)
                .method(Connection.Method.POST).ignoreContentType(true).execute();//1649815366888
        org.jsoup.nodes.Document document2 = response3.parse();
        String sss=document2.body().text();
        Result result = JSONObject.parseObject(sss, Result.class);
        Map<String,Object> map = (Map) result.getResult();
        return map;
    }
    public static String inportFile(String ids,String TONGZHISBH) throws Exception {
        String body="[\""+ids+"\"]";
        String dbPath ="";
        BufferedInputStream bufferedInputStream =null;
        Jsoup.connect("http://localhost:9999/certs/download/file").timeout(100000)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) cnipa-cpc/0.1.3 Chrome/87.0.4280.141 Electron/11.3.0 Safari/537.36")
                .header("Accept","application/json, text/plain, */*")
                .header("Accept-Encoding","gzip, deflate, br")
                .header("Connection", "keep-alive")
                .header("Content-Type","application/json;charset=UTF-8")
                .requestBody(body)
                .method(Connection.Method.POST).ignoreContentType(true).execute();
        String base64 = Base64.getEncoder().encodeToString(TONGZHISBH.getBytes("utf-8"));
        Connection.Response response = Jsoup.connect("http://localhost:9999/certs/export/file/" + base64)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) cnipa-cpc/0.1.3 Chrome/87.0.4280.141 Electron/11.3.0 Safari/537.36")
                .header("Accept","application/json, text/plain, */*")
                .header("Accept-Encoding","gzip, deflate, br")
                .header("Connection", "keep-alive")
                .header("Content-Type","application/json;charset=UTF-8")
                .maxBodySize(0)
                .method(Connection.Method.GET).ignoreContentType(true).execute();
        bufferedInputStream=response.bodyStream();
        log.info("下载流："+bufferedInputStream.toString());
        String ctxPath = "D://upFiles//";
        String fileName = null;
        String bizPath = "cases" + File.separator + "ceshi" +File.separator + new SimpleDateFormat("yyyyMMdd").format(new Date())+File.separator + TONGZHISBH;
        File file = new File(ctxPath + bizPath);
        if(!file.exists()){
            file.mkdirs();//创建文件目录
        }
        fileName = TONGZHISBH +".zip";//根据时间重命名文件文件名
        FileOutputStream fileOutputStream = new FileOutputStream(new File(file.getPath() + File.separator + fileName));
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
        FileCopyUtils.copy(bufferedInputStream, bufferedOutputStream);

        dbPath = ctxPath+bizPath + File.separator +fileName;//数据库存储路径
        if (dbPath.contains("\\")) {
            dbPath = dbPath.replace("\\", "/");
        }
        bufferedInputStream.close();
        fileOutputStream.close();
        bufferedOutputStream.close();
        return dbPath;
    }

    /**
     * http://localhost:9999/docs/v1/list
     * 获取cpc系统电子专利证书
     */
    public static Result<?> getPatentCertificateList(String famingczmc,String fawenrStart,String fawenrEnd,String fawenxlh,String shenqingh,String xiazaizt,int size,int current) throws IOException{
        String body="{\"famingczmc\":\""+famingczmc+"\",\"fawenrStart\":\""+fawenrStart+"\",\"fawenrEnd\":\""+fawenrEnd+"\",\"fawenxlh\":\""+fawenxlh+"\",\"shenqingh\":\""+shenqingh+"\",\"xiazaizt\":\""+xiazaizt+"\",\"zhanghu\":\"\",\"size\":"+size+",\"current\":"+current+"}";
//        String body="{\"famingczmc\":\"\",\"fawenrStart\":\"\",\"fawenrEnd\":\"\",\"fawenxlh\":\"\",\"shenqingh\":\"\",\"xiazaizt\":\"\",\"zhanghu\":\"\",\"size\":10,\"current\":1}";
        Connection.Response response3 = Jsoup.connect("http://localhost:9999/certs/v1/list")
                .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) cnipa-cpc/0.1.3 Chrome/87.0.4280.141 Electron/11.3.0 Safari/537.36")
                .header("Accept","application/json, text/plain, */*")
                .header("Accept-Encoding","gzip, deflate, br")
                .header("Connection", "keep-alive")
                .header("Content-Type","application/json;charset=UTF-8")
                .requestBody(body)
                .method(Connection.Method.POST).ignoreContentType(true).execute();//1649815366888
        org.jsoup.nodes.Document document2 = response3.parse();
        String sss=document2.body().text();
        Result result = JSONObject.parseObject(sss, Result.class);
        return result;
    }

    //官文文件导入
    public static String inportTzsFile(String ids,String TONGZHISBH) throws Exception {
        String body="[\""+ids+"\"]";
        String dbPath ="";
        BufferedInputStream bufferedInputStream =null;
        Connection.Response responseTzs = Jsoup.connect("http://localhost:9999/docs/download/file").timeout(100000)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) cnipa-cpc/0.1.3 Chrome/87.0.4280.141 Electron/11.3.0 Safari/537.36")
                .header("Accept","application/json, text/plain, */*")
                .header("Accept-Encoding","gzip, deflate, br")
                .header("Connection", "keep-alive")
                .header("Content-Type","application/json;charset=UTF-8")
                .requestBody(body)
                .method(Connection.Method.POST).ignoreContentType(true).execute();
        org.jsoup.nodes.Document documentTzs = responseTzs.parse();
        String sss=documentTzs.body().text();
//        String sss="{\"code\":200,\"message\":\"请求成功\",\"result\":[\"106259971\"],\"timestamp\":1705829777965}";
        Result result = JSONObject.parseObject(sss, Result.class);
        ObjectMapper objectapper = new ObjectMapper();
        String [] str=objectapper.convertValue(result.getResult(),String[].class);
        String resultString = str[0];
        if(StringUtils.isEmpty(TONGZHISBH)){
            TONGZHISBH = resultString;
        }
        log.info(TONGZHISBH);
        String base64 = Base64.getEncoder().encodeToString(resultString.getBytes("utf-8"));
        Connection.Response response = Jsoup.connect("http://localhost:9999/docs/export/file/" + base64)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) cnipa-cpc/0.1.3 Chrome/87.0.4280.141 Electron/11.3.0 Safari/537.36")
                .header("Accept","application/json, text/plain, */*")
                .header("Accept-Encoding","gzip, deflate, br")
                .header("Connection", "keep-alive")
                .header("Content-Type","application/json;charset=UTF-8")
                .maxBodySize(0)
                .method(Connection.Method.GET).ignoreContentType(true).execute();
        bufferedInputStream=response.bodyStream();
        log.info("下载流："+bufferedInputStream.toString());
        String ctxPath = "D://upFiles//";
        String fileName = null;
        String bizPath = "cases" + File.separator + "notices" +File.separator + new SimpleDateFormat("yyyyMMdd").format(new Date())+File.separator + TONGZHISBH;
        File file = new File(ctxPath + bizPath);
        if(!file.exists()){
            file.mkdirs();//创建文件目录
        }
        fileName = TONGZHISBH +".zip";//根据时间重命名文件文件名
        FileOutputStream fileOutputStream = new FileOutputStream(new File(file.getPath() + File.separator + fileName));
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
        FileCopyUtils.copy(bufferedInputStream, bufferedOutputStream);

        dbPath = ctxPath+bizPath + File.separator +fileName;//数据库存储路径
        if (dbPath.contains("\\")) {
            dbPath = dbPath.replace("\\", "/");
        }
        bufferedInputStream.close();
        fileOutputStream.close();
        bufferedOutputStream.close();
        return dbPath;
    }

}
