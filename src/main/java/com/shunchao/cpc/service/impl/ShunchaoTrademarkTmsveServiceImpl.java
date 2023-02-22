package com.shunchao.cpc.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSONObject;
import com.shunchao.config.CpcPathInComputer;
import com.shunchao.cpc.model.ShunchaoTrademarkTmsve;
import com.shunchao.cpc.service.IShunchaoTrademarkTmsveService;
import com.shunchao.cpc.util.CustomAnnotation;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.util.StringUtil;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description: 商标局官文
 * @Author: jeecg-boot
 * @Date:   2022-04-12
 * @Version: V1.0
 */
@Service
@Slf4j
public class ShunchaoTrademarkTmsveServiceImpl implements IShunchaoTrademarkTmsveService {

    @Value(value = "${jeecg.path.cases.basecpc}")
    private String basecpc;
    @Value(value = "${jeecg.path.cases.notices}")
    private String notices;
    @Value(value = "${connecturl}")
    private String connecturl;


    /**
     * 功能描述:获取商标网Cookie
     * 场景:
     * @Param: [enterpriceAgencyId]
     * @Return: void
     * @Author: Ironz
     * @Date: 2022/4/13 14:17
     */
    public Map<String,String> getCookie() throws IOException {
        Connection.Response response = Jsoup.connect("https://wssq.sbj.cnipa.gov.cn:9443/tmsve/main/login.jsp")
                .method(Connection.Method.POST).ignoreContentType(true).ignoreHttpErrors(true).execute();
        Map<String,String> cookie = response.cookies();
        return  cookie;
    }

    /**
     * 功能描述:商标网登录
     * 场景:
     * @Param: [enterpriceAgencyId]
     * @Return: void
     * @Author: Ironz
     * @Date: 2022/4/13 14:17
     */
    public void tmsveLogin(JSONObject enterpriseInfo, Map<String,String> cookie) throws IOException {
        if (Objects.nonNull(enterpriseInfo)) {
            if (Objects.nonNull(enterpriseInfo.getString("tmsvePin")) && Objects.nonNull(enterpriseInfo.getString("tmsveSignCert")) && Objects.nonNull(enterpriseInfo.getString("tmsveSignData"))) {

                Map<String,String> tmsMap = new HashMap<>();
                //tmsMap.put("pin","456123");
                tmsMap.put("pin",enterpriseInfo.getString("tmsvePin"));
                //tmsMap.put("signCert","{\"name\":\"北京和鼎泰知识产权代理有限公司\",\"loginName\":\"Beijinghedingtai\",\"cacertSn\":\"020001\",\"appName\":\"wellhope\",\"containerName\":\"sGXSKlDuIRmnHiV\",\"devId\":\"5496_ehYlWouXhkjCAiYRIzwtH5Q4qBi\",\"validDate\":\"2026-11-09+23:59:59\",\"cn\":\"CNIPASM2Class2CA\"}");
                tmsMap.put("signCert",enterpriseInfo.getString("tmsveSignCert"));
//                tmsMap.put("signCert","{\"name\":\"安徽顺超知识产权代理事务所（特殊普通合伙）\",\"loginName\":\"hfscip\",\"cacertSn\":\"020001\",\"appName\":\"wellhope\",\"containerName\":\"197AC095-84D1-40B2-8956-D6D8C9B36E91\",\"devId\":\"5612_J5zVrmxJGx8otyZw211sQ9XXXIU\",\"validDate\":\"2026-12-12 23:59:59\",\"cn\":\"CNIPASM2Class2CA\"}");
                tmsMap.put("signData",enterpriseInfo.getString("tmsveSignData"));
//                tmsMap.put("signData","MIIDrQYJKoZIhvcNAQcCoIIDnjCCA5oCAQExDjAMBggqgRzPVQGDdQUAMAsGCSqGSIb3DQEHAaCCAkMwggI/MIIB46ADAgECAgx8hgAAABOYBbYrZw8wDAYIKoEcz1UBg3UFADBIMQswCQYDVQQGEwJDTjEeMBwGA1UECgwV5Zu95a6255+l6K+G5Lqn5p2D5bGAMRkwFwYDVQQDDBBDTklQQVNNMkNsYXNzMkNBMB4XDTIxMTEwOTE2MDAwMFoXDTI2MTEwOTE1NTk1OVowZTEaMBgGA1UECgwRYmVpamluZ2hlbGlhbnNodW4xDzANBgNVBAsMBjAyMDAwMTE2MDQGA1UEAwwt5YyX5Lqs5ZKM6IGU6aG655+l6K+G5Lqn5p2D5Luj55CG5pyJ6ZmQ5YWs5Y+4MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEtvNg659j4UGhVD0038N9ZjfhJG57ARlONadu3jw8NYJzUGkRivK86fFS1RgHAWz+YSnAp4m45IZLvd71Ku606aOBkzCBkDARBglghkgBhvhCAQEEBAMCAIAwCwYDVR0PBAQDAgDAMCAGA1UdJQEB/wQWMBQGCCsGAQUFBwMCBggrBgEFBQcDBDAMBgNVHRMEBTADAQEAMB8GA1UdIwQYMBaAFMN2LiPzYKmimyTmf5KyQAPznjetMB0GA1UdDgQWBBTBUd1zmdGlMqFanf/wPMzJ/YCNQDAMBggqgRzPVQGDdQUAA0gAMEUCIHF4pVjVTIQkVU87bczb8LGnOW4Yb3M4U22E8ne+XZWhAiEApZ6lfJMYPMQLXoCrVYL7V89htWEGrSARLfp/7RyTA4wxggEvMIIBKwIBATBYMEgxCzAJBgNVBAYTAkNOMR4wHAYDVQQKDBXlm73lrrbnn6Xor4bkuqfmnYPlsYAxGTAXBgNVBAMMEENOSVBBU00yQ2xhc3MyQ0ECDHyGAAAAE5gFtitnDzAMBggqgRzPVQGDdQUAoGkwGAYJKoZIhvcNAQkDMQsGCSqGSIb3DQEHATAcBgkqhkiG9w0BCQUxDxcNMjIwOTE5MDcyMDQ2WjAvBgkqhkiG9w0BCQQxIgQgl41mG3P15KdJcicfpPXkVsSTkAZSATX0uotYzWuJv98wCgYIKoEcz1UBg3UERzBFAiEAyIUzl2LFFiHrqSkWmvoG1vdaRYu0pARbD0Ij85faNR0CIFpOCXTgeo8feEfTx3Agcvdv8B5QfxgveW8vxCFAwt4Z");

                tmsMap.put("clearData",enterpriseInfo.getString("tmsveClearData"));//1663572183955465365
                tmsMap.put("hashData",enterpriseInfo.getString("tmsveHashData"));//149776826
                tmsMap.put("validDate","");
                tmsMap.put("startValidDate","");
                tmsMap.put("certInfo","");
                tmsMap.put("username","");
                tmsMap.put("name","");
                tmsMap.put("typeCert","");
                tmsMap.put("hardInfo","");
                tmsMap.put("certCode","");
                tmsMap.put("caCertSN","");
                tmsMap.put("containerName","");
                tmsMap.put("tmurl","https://wssq.sbj.cnipa.gov.cn:9443/tmsve/");
                tmsMap.put("agreeProt","on");
                tmsMap.put("qrid","");
                tmsMap.put("uniscid","");
                tmsMap.put("id","");
                tmsMap.put("str","");
                tmsMap.put("agreeProt2","on");
                Connection.Response response = Jsoup.connect("https://wssq.sbj.cnipa.gov.cn:9443/tmsve/wssqsy_getCayzDl.xhtml").cookies( cookie).data(tmsMap)
                        .method(Connection.Method.POST).ignoreContentType(true).execute();
                String body = response.body();
                System.out.println("商标网登录");
            }
    }
    }

    /**
     * 功能描述:查询商标网国内申请管理
     * 场景:
     * @Param: [domesticApplyDateBegin, domesticApplyDateEnd, enterpriceAgencyId, cookie]
     * @Return: void
     * @Author: Ironz
     * @Date: 2022/4/14 9:22
     */
    public List<ShunchaoTrademarkTmsve> tmsveQueryDomesticApplication(String domesticApplyDateBegin, String domesticApplyDateEnd, Map<String,String> cookie) throws IOException{
        List<ShunchaoTrademarkTmsve> trademarkTmsveList = new ArrayList<>();
            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

            //查询商标网国内申请管理
            Map<String, String> tmsMap3 = new HashMap<>();
            //tmsMap3.put("wd.date1","2020-04-12");
            tmsMap3.put("wd.date1", domesticApplyDateBegin);
            //tmsMap3.put("wd.date2","2022-04-12");
            tmsMap3.put("wd.date2", domesticApplyDateEnd);
            tmsMap3.put("wd.appNum", "");
            //tmsMap3.put("wd.appName", "");
            tmsMap3.put("wd.appName", "");
            tmsMap3.put("wd.wsType", "-1");
            tmsMap3.put("wd.orderNum", "");
            tmsMap3.put("pagenum", "1");
            tmsMap3.put("pagesize", "30");
            tmsMap3.put("sum", "25");
            tmsMap3.put("countpage", "1");
            tmsMap3.put("gopage","1");
            tmsMap3.put("wd.appState", "");

            Connection.Response response = Jsoup.connect("https://wssq.sbj.cnipa.gov.cn:9443/tmsve/wdsqgl_getWdsqCondition.xhtml")
                    .cookies(cookie)
                    .data(tmsMap3).method(Connection.Method.POST).ignoreContentType(true).execute();
            Document parse =Jsoup.parse(response.body());
            Elements elementTable = parse.getElementsByClass("import_tab");
            if (Objects.nonNull(elementTable)) {
                Elements trs = elementTable.select("tr");
                for (int i = 1; i < trs.size(); i++) {
                    Element tr = trs.get(i);
                    Elements tds = tr.select("td");
                    ShunchaoTrademarkTmsve trademarkTmsve = new ShunchaoTrademarkTmsve();
                    for (int j = 0; j < tds.size(); j++) {
                        if (j == 3) {
                            Element td3 = tds.get(j);
                            trademarkTmsve.setTmsveAgencynumber(td3.text());
                        }
                        if (j == 4) {
                           Element td4 = tds.get(j);
                            try {
                                Date applyDay = sf.parse(td4.text());
                                trademarkTmsve.setTmsveApplyday(applyDay);
                            } catch (ParseException e) {
                                log.error("申请日转换错误",e);
                            }
                        }
                        if (j == 5) {
                            Element td5 = tds.get(j);
                            trademarkTmsve.setTmsveApplynumber(td5.text());
                        }
                    }
                    trademarkTmsveList.add(trademarkTmsve);
                }
            }
        return trademarkTmsveList;

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
    public <T> List<Map<String, Object>> analyzing(Class<T> tClass, String appNum, Map<String,String> cookie) {
        List<Map<String, Object>> tmsveList = new ArrayList<>();
        SimpleDateFormat simpleFormatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
        SimpleDateFormat simpleFormatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            //忽略https证书
            String url= "https://wssq.sbj.cnipa.gov.cn:9443/tmsve/fwcx_getFwCondition.xhtml";
            Map<String, String> mapParams = new HashMap<>();
            mapParams.put("fw.appNum", appNum);
            mapParams.put("fw.date1", "");
            mapParams.put("fw.date2", "");
            mapParams.put("fw.regNum", "");
            mapParams.put("fw.flowType", "-1");
            mapParams.put("fw.fileType", "");
            mapParams.put("pagenum", "1");
            mapParams.put("pagesize", "30");
            mapParams.put("sum", "2");
            mapParams.put("countpage", "1");
            mapParams.put("gopage", "1");
            Connection.Response response3 = Jsoup.connect(url).cookies(cookie).data(mapParams).method(Connection.Method.POST).ignoreContentType(true).execute();
            //Document document = Jsoup.parse(new File("C:\\Users\\admin\\Desktop\\我的发文demo.html"),"UTF-8");
            Document document = response3.parse();
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
    public Map<String, String> downloadpdf(Map<String,String> cookie, String docId, String applyNumber, String token) throws IOException {
        Map<String, String> tmsDocMap = new HashMap<>();
        tmsDocMap.put("_", System.currentTimeMillis()+"");
        //tmsDocMap.put("docId","B1021TMZC00000051884945JFTZ0100012");
        tmsDocMap.put("docId", docId);
        tmsDocMap.put("docNo", "1");
        Connection.Response docResponse = Jsoup.connect("https://wssq.sbj.cnipa.gov.cn:9443/tmsve/fwcx_getPdf.xhtml")
                .cookies(cookie).data(tmsDocMap).method(Connection.Method.POST).ignoreContentType(true).execute();
        //响应转换成输入流
        BufferedInputStream bufferedInputStream = docResponse.bodyStream();
        //保存 相对路径 trademark/offcialText/商标id/申请号/官文.pdf （dociD）
        String path = CpcPathInComputer.getCpcBinPathWindowsComputer() + File.separator + basecpc + File.separator + notices + File.separator + applyNumber + File.separator;
        //String path = "d:\\gwssitemp" + File.separator + basecpc + File.separator + notices + File.separator + applyNumber + File.separator;
        String fileName = docId + ".pdf";
        File file = new File( path);
        if (!file.exists()) {
            file.mkdirs();
        }

        FileCopyUtils.copy(bufferedInputStream, new BufferedOutputStream(new FileOutputStream(new File( path + fileName))));
        File file2 = new File(path + fileName);
        if (file2.exists()) {
            HashMap<String, Object> paramMap = new HashMap<>();
            paramMap.put("docId", docId);
            paramMap.put("applyNumber", applyNumber);
            paramMap.put("file", file2);
            HttpResponse execute = HttpRequest.post(connecturl + "/trademark/shunchaoTrademarkTmsve/upload").
                    header("X-Access-Token", token).form(paramMap).execute();
            String body = execute.body();
            JSONObject jsonObject = JSONObject.parseObject(body);
            Boolean success = (Boolean) jsonObject.get("success");
            tmsDocMap.clear();
            tmsDocMap.put("path", jsonObject.getString("result"));
        }
        return tmsDocMap;
    }
}
