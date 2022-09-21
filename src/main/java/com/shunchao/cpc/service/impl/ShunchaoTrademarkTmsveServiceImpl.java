package com.shunchao.cpc.service.impl;

import cn.hutool.core.util.ZipUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSONObject;
import com.shunchao.config.CpcPathInComputer;
import com.shunchao.cpc.model.Result;
import com.shunchao.cpc.model.ShunchaoTrademarkTmsve;
import com.shunchao.cpc.service.IShunchaoTrademarkTmsveService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.*;
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


    public String getCookie() throws IOException {
        Connection.Response response = Jsoup.connect("http://wssq.sbj.cnipa.gov.cn:9080/tmsve/main/login.jsp")
                .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                .header("Accept-Encoding", "gzip, deflate")
                .header("Accept-Language", "zh-CN,zh;q=0.9")
                .header("Host","wssq.sbj.cnipa.gov.cn:9080")
                .header("Cache-Control","max-age=0")
                .header("Connection","keep-alive")
                .header("Upgrade-Insecure-Requests","1")
                .header("Cookie","_trs_uv=l82fo8cf_4693_25n4")
                .referrer("http://wssq.sbj.cnipa.gov.cn:9080/tmsve/wssqsy_quitLogin.xhtml")
                .method(Connection.Method.GET).ignoreContentType(true).execute();
        Document parse = response.parse();
        Map<String,String> cookie = response.cookies();
        List<String> list = new ArrayList<>();

        Set<Map.Entry<String,String>> entries = cookie.entrySet();
        for (Map.Entry<String,String> entry:entries){
            list.add(entry.getKey()+"="+entry.getValue()+";");
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i == list.size() - 1) {
                sb.append(list.get(i));
            } else {
                sb.append(list.get(i) + " ");
            }
        }

        String cookieId = sb.toString();
        return  cookieId;
    }

    /**
     * 功能描述:商标网登录获取cookie
     * 场景:
     * @Param: [enterpriceAgencyId]
     * @Return: void
     * @Author: Ironz
     * @Date: 2022/4/13 14:17
     */
    public String tmsveLogin(JSONObject enterpriseInfo) throws IOException {
        if (Objects.nonNull(enterpriseInfo)) {
            if (Objects.nonNull(enterpriseInfo.getString("tmsvePin")) && Objects.nonNull(enterpriseInfo.getString("tmsveSignCert")) && Objects.nonNull(enterpriseInfo.getString("tmsveSignData"))) {

                Map<String,String> tmsMap = new HashMap<>();
                Map<String,String> tmsMap2 = new HashMap<>();
                //tmsMap.put("pin","456123");
                tmsMap.put("pin",enterpriseInfo.getString("tmsvePin"));
                //tmsMap.put("signCert","{\"name\":\"北京和鼎泰知识产权代理有限公司\",\"loginName\":\"Beijinghedingtai\",\"cacertSn\":\"020001\",\"appName\":\"wellhope\",\"containerName\":\"sGXSKlDuIRmnHiV\",\"devId\":\"5496_ehYlWouXhkjCAiYRIzwtH5Q4qBi\",\"validDate\":\"2026-11-09+23:59:59\",\"cn\":\"CNIPASM2Class2CA\"}");
//                tmsMap.put("signCert",enterpriseInfo.getString("tmsveSignCert"));
                tmsMap.put("signCert","{\"name\":\"安徽顺超知识产权代理事务所（特殊普通合伙）\",\"loginName\":\"hfscip\",\"cacertSn\":\"020001\",\"appName\":\"wellhope\",\"containerName\":\"197AC095-84D1-40B2-8956-D6D8C9B36E91\",\"devId\":\"5612_J5zVrmxJGx8otyZw211sQ9XXXIU\",\"validDate\":\"2026-12-12 23:59:59\",\"cn\":\"CNIPASM2Class2CA\"}");
//                tmsMap.put("signCert","{\"name\":\"北京和联顺知识产权代理有限公司\",\"loginName\":\"beijinghelianshun\",\"cacertSn\":\"020001\",\"appName\":\"wellhope\",\"containerName\":\"DVhRXCSgVPAlQxX\",\"devId\":\"5611_6WL9oT4KGtV3ve+D6wsPR4sIAhP\",\"validDate\":\"2026-11-09 23:59:59\",\"cn\":\"CNIPASM2Class2CA\"}");
                //tmsMap.put("signData","MIIDqwYJKoZIhvcNAQcCoIIDnDCCA5gCAQExDjAMBggqgRzPVQGDdQUAMAsGCSqGSIb3DQEHAaCCAkEwggI9MIIB4qADAgECAgx8hgAAABOY/pY+CqEwDAYIKoEcz1UBg3UFADBIMQswCQYDVQQGEwJDTjEeMBwGA1UECgwV5Zu95a6255+l6K+G5Lqn5p2D5bGAMRkwFwYDVQQDDBBDTklQQVNNMkNsYXNzMkNBMB4XDTIxMTEwOTE2MDAwMFoXDTI2MTEwOTE1NTk1OVowZDEZMBcGA1UECgwQQmVpamluZ2hlZGluZ3RhaTEPMA0GA1UECwwGMDIwMDAxMTYwNAYDVQQDDC3ljJfkuqzlkozpvI7ms7Dnn6Xor4bkuqfmnYPku6PnkIbmnInpmZDlhazlj7gwWTATBgcqhkjOPQIBBggqgRzPVQGCLQNCAARZJzYJLuIFbHqMvu7FWXixjqXOiZ6Knkgr6DSaAQvIxhDpnl/nuH6CU//xoaIgAF+6zoFaRWmpNrt04nIihdbso4GTMIGQMBEGCWCGSAGG+EIBAQQEAwIAgDALBgNVHQ8EBAMCAMAwIAYDVR0lAQH/BBYwFAYIKwYBBQUHAwIGCCsGAQUFBwMEMAwGA1UdEwQFMAMBAQAwHwYDVR0jBBgwFoAUw3YuI/NgqaKbJOZ/krJAA/OeN60wHQYDVR0OBBYEFKx/7ooYAKklaqIfwQyNqdGbH0NUMAwGCCqBHM9VAYN1BQADRwAwRAIgM4Jlo3ZRzggCXSWhWcW0VOghRVME9uGKc3dLdlynHS8CIDqQL09RLjYbv10+LZ7g5myk/3cHb92M3hUSb/9XY+9zMYIBLzCCASsCAQEwWDBIMQswCQYDVQQGEwJDTjEeMBwGA1UECgwV5Zu95a6255+l6K+G5Lqn5p2D5bGAMRkwFwYDVQQDDBBDTklQQVNNMkNsYXNzMkNBAgx8hgAAABOY/pY+CqEwDAYIKoEcz1UBg3UFAKBpMBgGCSqGSIb3DQEJAzELBgkqhkiG9w0BBwEwHAYJKoZIhvcNAQkFMQ8XDTIyMDQxMTAzMDYxM1owLwYJKoZIhvcNAQkEMSIEIJLnXLhEXlT6rIWxp3ncKBY9LQcY6ICiKMrtjMT1oEDgMAoGCCqBHM9VAYN1BEcwRQIhAI/rVNLUB8qRGGEUeXHsJ2n3F3z8mwLMGamdyZxmwYKlAiB0pzUwMwCLMHjI3Fe7rzlH8EEqgJJ7vHoflDx3L7gooA==");
//                tmsMap.put("signData",enterpriseInfo.getString("tmsveSignData"));
                tmsMap.put("signData","MIIDtgYJKoZIhvcNAQcCoIIDpzCCA6MCAQExDjAMBggqgRzPVQGDdQUAMAsGCSqGSIb3DQEHAaCCAkswggJHMIIB6qADAgECAgx8hgAAABcketUHHVUwDAYIKoEcz1UBg3UFADBIMQswCQYDVQQGEwJDTjEeMBwGA1UECgwV5Zu95a6255+l6K+G5Lqn5p2D5bGAMRkwFwYDVQQDDBBDTklQQVNNMkNsYXNzMkNBMB4XDTIxMTIxMjE2MDAwMFoXDTI2MTIxMjE1NTk1OVowbDEPMA0GA1UECgwGaGZzY2lwMQ8wDQYDVQQLDAYwMjAwMDExSDBGBgNVBAMMP+WuieW+vemhuui2heefpeivhuS6p+adg+S7o+eQhuS6i+WKoeaJgO+8iOeJueauiuaZrumAmuWQiOS8me+8iTBZMBMGByqGSM49AgEGCCqBHM9VAYItA0IABL1Q6kxBG8gFwrXVMhU+D8IyEW0id2EvLKck7FAS8sTiihBmyRtdwbaaV3hCAIGJmz8hlXUOx3HPJzEEJPKmhIWjgZMwgZAwEQYJYIZIAYb4QgEBBAQDAgCAMAsGA1UdDwQEAwIAwDAgBgNVHSUBAf8EFjAUBggrBgEFBQcDAgYIKwYBBQUHAwQwDAYDVR0TBAUwAwEBADAfBgNVHSMEGDAWgBTDdi4j82Cpopsk5n+SskAD8543rTAdBgNVHQ4EFgQUHPQoVb6wGkvJbGcJXautzjQqRrYwDAYIKoEcz1UBg3UFAANJADBGAiEAx/N/XMWpDX1JQ2mt5yKNjX/r0p1jUilk3OfJh6GpX5sCIQDMKvazARFL4oKREAQHIArulIPZIxcXKefBiflWB1AK1TGCATAwggEsAgEBMFgwSDELMAkGA1UEBhMCQ04xHjAcBgNVBAoMFeWbveWutuefpeivhuS6p+adg+WxgDEZMBcGA1UEAwwQQ05JUEFTTTJDbGFzczJDQQIMfIYAAAAXJHrVBx1VMAwGCCqBHM9VAYN1BQCgaTAYBgkqhkiG9w0BCQMxCwYJKoZIhvcNAQcBMBwGCSqGSIb3DQEJBTEPFw0yMjA5MjAwMDQ0MzlaMC8GCSqGSIb3DQEJBDEiBCDXGhjodkRorMRHAP88ee6x4Yv9txRPytlpjExU2YTiDTAKBggqgRzPVQGDdQRIMEYCIQDEF7wKujOETCDiv1oCpkMg6Ud6hNHtnrd2ri7RpTRnowIhAP/9cxvv9ZL8kEcnuHZDDV62SAGzJyEMEbFb8BeeVQUa");
//                tmsMap.put("signData","MIIDrQYJKoZIhvcNAQcCoIIDnjCCA5oCAQExDjAMBggqgRzPVQGDdQUAMAsGCSqGSIb3DQEHAaCCAkMwggI/MIIB46ADAgECAgx8hgAAABOYBbYrZw8wDAYIKoEcz1UBg3UFADBIMQswCQYDVQQGEwJDTjEeMBwGA1UECgwV5Zu95a6255+l6K+G5Lqn5p2D5bGAMRkwFwYDVQQDDBBDTklQQVNNMkNsYXNzMkNBMB4XDTIxMTEwOTE2MDAwMFoXDTI2MTEwOTE1NTk1OVowZTEaMBgGA1UECgwRYmVpamluZ2hlbGlhbnNodW4xDzANBgNVBAsMBjAyMDAwMTE2MDQGA1UEAwwt5YyX5Lqs5ZKM6IGU6aG655+l6K+G5Lqn5p2D5Luj55CG5pyJ6ZmQ5YWs5Y+4MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEtvNg659j4UGhVD0038N9ZjfhJG57ARlONadu3jw8NYJzUGkRivK86fFS1RgHAWz+YSnAp4m45IZLvd71Ku606aOBkzCBkDARBglghkgBhvhCAQEEBAMCAIAwCwYDVR0PBAQDAgDAMCAGA1UdJQEB/wQWMBQGCCsGAQUFBwMCBggrBgEFBQcDBDAMBgNVHRMEBTADAQEAMB8GA1UdIwQYMBaAFMN2LiPzYKmimyTmf5KyQAPznjetMB0GA1UdDgQWBBTBUd1zmdGlMqFanf/wPMzJ/YCNQDAMBggqgRzPVQGDdQUAA0gAMEUCIHF4pVjVTIQkVU87bczb8LGnOW4Yb3M4U22E8ne+XZWhAiEApZ6lfJMYPMQLXoCrVYL7V89htWEGrSARLfp/7RyTA4wxggEvMIIBKwIBATBYMEgxCzAJBgNVBAYTAkNOMR4wHAYDVQQKDBXlm73lrrbnn6Xor4bkuqfmnYPlsYAxGTAXBgNVBAMMEENOSVBBU00yQ2xhc3MyQ0ECDHyGAAAAE5gFtitnDzAMBggqgRzPVQGDdQUAoGkwGAYJKoZIhvcNAQkDMQsGCSqGSIb3DQEHATAcBgkqhkiG9w0BCQUxDxcNMjIwOTE5MDcyMDQ2WjAvBgkqhkiG9w0BCQQxIgQgl41mG3P15KdJcicfpPXkVsSTkAZSATX0uotYzWuJv98wCgYIKoEcz1UBg3UERzBFAiEAyIUzl2LFFiHrqSkWmvoG1vdaRYu0pARbD0Ij85faNR0CIFpOCXTgeo8feEfTx3Agcvdv8B5QfxgveW8vxCFAwt4Z");

                tmsMap.put("clearData","1663634849939800817");//1663572183955465365
                tmsMap.put("hashData","1588351197");//149776826
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
                tmsMap.put("tmurl","http://wssq.sbj.cnipa.gov.cn:9080/tmsve/");
                tmsMap.put("agreeProt","on");
                Connection.Response response = Jsoup.connect("http://wssq.sbj.cnipa.gov.cn:9080/tmsve/wssqsy_getCayzDl.xhtml").data(tmsMap)
                        .method(Connection.Method.POST).ignoreContentType(true).execute();
                Document parse = response.parse();
                Map<String,String> cookie = response.cookies();
                List<String> list = new ArrayList<>();

                Set<Map.Entry<String,String>> entries = cookie.entrySet();
                for (Map.Entry<String,String> entry:entries){
                    list.add(entry.getKey()+"="+entry.getValue()+";");
                }

                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < list.size(); i++) {
                    if (i == list.size() - 1) {
                        sb.append(list.get(i));
                    } else {
                        sb.append(list.get(i) + " ");
                    }
                }

                String cookieId = sb.toString();
                return  cookieId;
            }
        }else {
            return null;
        }
        return null;
    }


    /**
     * 功能描述:查询商标网国内申请管理
     * 场景:
     * @Param: [domesticApplyDateBegin, domesticApplyDateEnd, enterpriceAgencyId, cookie]
     * @Return: void
     * @Author: Ironz
     * @Date: 2022/4/14 9:22
     */
    public List<ShunchaoTrademarkTmsve> tmsveQueryDomesticApplication(String domesticApplyDateBegin, String domesticApplyDateEnd, JSONObject enterpriseInfo, String cookie) throws IOException{
        cookie ="_trs_uv=l82fo8cf_4693_25n4; tmsve10.21=2272.7689.15402.0000; 018f9ebc91337e3e72=a91dd1b5d77cb4fb8bc58c55500c4c6c; JSESSIONID=00007vzoP8b7x06cTJeyYn0diRE:1bm10lcno;";
        List<ShunchaoTrademarkTmsve> trademarkTmsveList = new ArrayList<>();
        if (Objects.nonNull(enterpriseInfo)) {

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
            tmsMap3.put("pagesize", "");
            tmsMap3.put("sum", "");
            tmsMap3.put("countpage", "");
            tmsMap3.put("wd.appState", "");

            Connection.Response response3 = Jsoup.connect("http://wssq.sbj.cnipa.gov.cn:9080/tmsve/wdsqgl_getWdsqCondition.xhtml")
                    .cookie("Cookie", cookie).data(tmsMap3).method(Connection.Method.POST).ignoreContentType(true).execute();

            Document parse3 = response3.parse();

            Elements elementTable = parse3.getElementsByClass("import_tab");
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
                        if (j == 5) {
                            Element td5 = tds.get(j);
                            trademarkTmsve.setTmsveApplynumber(td5.text());
                        }
                    }
                    trademarkTmsveList.add(trademarkTmsve);
                }
            }
        }
        return trademarkTmsveList;

    }
    public Map<String, String> downloadpdf(String cookieId, String docId, String applyNumber, String token) throws IOException {
        Map<String, String> tmsDocMap = new HashMap<>();
        tmsDocMap.put("_", System.currentTimeMillis()+"");
        //tmsDocMap.put("docId","B1021TMZC00000051884945JFTZ0100012");
        tmsDocMap.put("docId", docId);
        tmsDocMap.put("docNo", "1");
        Connection.Response docResponse = Jsoup.connect("http://wssq.sbj.cnipa.gov.cn:9080/tmsve/fwcx_getPdf.xhtml")
                .cookie("Cookie", cookieId).data(tmsDocMap).method(Connection.Method.POST).ignoreContentType(true).execute();
        //响应转换成输入流
        BufferedInputStream bufferedInputStream = docResponse.bodyStream();
        //保存 相对路径 trademark/offcialText/商标id/申请号/官文.pdf （dociD）
        String path = CpcPathInComputer.getCpcBinPathWindowsComputer() + File.separator + basecpc + File.separator + notices + File.separator + applyNumber + File.separator;
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
            Integer code = (Integer) jsonObject.get("code");
            if (!success) {
            } else {
            }
        }
        return tmsDocMap;
    }
}
