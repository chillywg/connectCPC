package com.shunchao.cpc.util;


import com.alibaba.fastjson.JSONObject;
import com.shunchao.cpc.model.Result;
import com.shunchao.cpc.model.ShunchaoTrademarkCoApplicant;
import com.shunchao.cpc.model.ShunchaoTrademarkPow;
import com.shunchao.cpc.model.ShunchaoTrademarkProduct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

/**
 * @author djlcc
 * @title: 商标工具类
 * @projectName jeecg-boot-parent
 * @description: TODO 解析html数据
 * @date 2022/4/13 9:24
 */
@Component
@Slf4j
public class TrademarkUtils {
    private static WebDriver driver;
    private static String url ="https://wssq.sbj.cnipa.gov.cn:9443/tmsve/";
    /**
     * 功能描述:获取商标网Cookie
     * 场景:
     * @Param: [enterpriceAgencyId]
     * @Return: void
     * @Author: Ironz
     * @Date: 2022/4/13 14:17
     */
    public static Map<String,String> getCookie() throws IOException {
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
    public static Map<String,String> tmsveLogin(JSONObject enterpriseInfo, Map<String,String> cookie) throws IOException {
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
                tmsMap.put("cipher",enterpriseInfo.getString("tmsveCipher"));
                tmsMap.put("agreeProt","on");
                tmsMap.put("qrid","");
                tmsMap.put("uniscid","");
                tmsMap.put("id","");
                tmsMap.put("str","");
                tmsMap.put("agreeProt2","on");
                Connection.Response response = Jsoup.connect("https://wssq.sbj.cnipa.gov.cn:9443/tmsve/wssqsy_getCayzDl.xhtml").cookies( cookie).data(tmsMap)
                        .method(Connection.Method.POST).ignoreContentType(true).execute();
                String body = response.body();
                Map<String,String> cookies = response.cookies();
                cookies.put("FECW",cookie.get("FECW"));
                System.out.println("商标网登录cookies:"+cookies.toString());
                return  cookies;
            }
        }
        return  null;
    }

    /**
     * 功能描述:商标网登录
     * 场景:
     * @Param: [enterpriceAgencyId]
     * @Return: void
     * @Author: Ironz
     * @Date: 2022/4/13 14:17
     */
    public static Map<String,String> tmsveLogin2(JSONObject enterpriseInfo) throws Exception {

        String mark = null;
        String alertScrollTopJs = "document.querySelector('.pop-content').scrollTop=";
        String htmlScrolltoJs = "parent.scrollTo(0,600)";
        Map<String,String> cookieMap = new HashMap<>();
        try {
//        String rootPath = System.getProperty("exe.path");
            String rootPath = "D:\\driver\\foxDriver\\geckodriver-v0.34.0-win64\\";
            //System.out.println("开始提交程序：=====根目录====="+rootPath);
            driver = FoxDriverUtils.foxDriver2(driver, rootPath);
            //driver.navigate().to(url);
            driver.get(url);
            //driver.findElement(By.id("pin")).sendKeys(pinword);
            Thread.sleep(5000);
            driver.findElement(By.id("pin")).sendKeys(enterpriseInfo.getString("tmsvePin"));
            driver.findElement(By.id("cipher")).sendKeys(enterpriseInfo.getString("tmsveCipher"));
            //driver.findElement(By.xpath("//*[@id=\"pinWord\"]")).click();
            driver.findElement(By.cssSelector("#pinWord")).click();
            Thread.sleep(3000);
            Set<Cookie> cookies = driver.manage().getCookies();
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                String value = cookie.getValue();
                cookieMap.put(name, value);
            }
            System.out.println(cookieMap);
        }catch (Exception e) {
            log.info("登录失败",e);
            throw new Exception ("登录失败");
        }
        return cookieMap;
    }

}
