package com.shunchao.cpc.util;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shunchao.cpc.model.*;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.openqa.selenium.*;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
    public static Map<String,String> tmsveLogin2(JSONObject enterpriseInfo, Map<String,String> cookie) throws IOException {
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
    public static int tmsveLogin(JSONObject resultObject,String connecturl,String token) throws Exception {
        JSONObject enterpriseInfo = (JSONObject)resultObject.get("enterpriceAgencyInfo");
        String minSendTime ="";
        if(ObjectUtil.isNotNull(resultObject.get("minSendTime"))){
            minSendTime =resultObject.get("minSendTime").toString();
        }
        String tmsveDate ="";
        if(ObjectUtil.isNotNull(resultObject.get("tmsveDate"))){
            tmsveDate=resultObject.get("tmsveDate").toString();
        }
        log.info("---------------minSendTime-----------------:"+minSendTime);
        log.info("---------------tmsveDate-----------------:"+tmsveDate);
        String mark = null;
        String alertScrollTopJs = "document.querySelector('.pop-content').scrollTop=";
        String htmlScrolltoJs = "parent.scrollTo(0,600)";
        Map<String,String> cookieMap = new HashMap<>();
        try {
        String rootPath = System.getProperty("exe.path");
//            String rootPath ="D:\\DUOUEXE\\duou\\";
//            String rootPath = "D:\\driver\\foxDriver\\geckodriver-v0.34.0-win64\\";
            //System.out.println("开始提交程序：=====根目录====="+rootPath);
            driver = ChromeDriverUtils.beforeM(driver, rootPath);
            //driver.navigate().to(url);
            driver.get(url);
            //driver.findElement(By.id("pin")).sendKeys(pinword);
            Thread.sleep(5000);
            driver.findElement(By.id("pin")).sendKeys(enterpriseInfo.getString("tmsvePin"));
            driver.findElement(By.id("cipher")).sendKeys(enterpriseInfo.getString("tmsveCipher"));
            //driver.findElement(By.xpath("//*[@id=\"pinWord\"]")).click();
            driver.findElement(By.cssSelector("#pinWord")).click();
            Thread.sleep(3000);

            //移动弹框内滚动条
            ((JavascriptExecutor) driver).executeScript(alertScrollTopJs+1000);

            //driver.findElement(By.xpath("/html/body/div[2]/div[2]/div[1]/div/input")).click();//开发本地电脑可以
            //driver.findElement(By.xpath("//INPUT[@class=\"pop-ok pop-next\"]")).click();//ie8可用
            driver.findElement(By.xpath("//INPUT[@class=\"pop-ok pop-next\"]")).sendKeys(Keys.ENTER);
            //driver.findElement(By.cssSelector(".pop-next")).click();//开发本地电脑可以
            Thread.sleep(3000);

            ((JavascriptExecutor) driver).executeScript(alertScrollTopJs+3000);
            Thread.sleep(3000);

            driver.findElement(By.xpath("//INPUT[@class=\"pop-ok pop-close\"]")).click();//ie8可用

            if(!StringUtils.isEmpty(minSendTime)){
                //菜单
                driver.findElement(By.xpath("//*[@id=\"menu\"]/ul/li[9]/a")).click();

                driver.findElement(By.xpath("//*[@id=\"menu\"]/ul/li[9]/ul/li[1]/a")).click();

                //进入iframe
                driver.switchTo().frame("myframe");
                Thread.sleep(2000);
                driver.findElement(By.xpath("//*[@id=\"date1\"]")).sendKeys(minSendTime);
                driver.findElement(By.id("but1")).click();
                Thread.sleep(6000);
//            WebElement elementTable = driver.findElement(By.tagName("import_tab"));
                WebElement elementTable = driver.findElement(By.xpath("//*[@id=\"form1\"]/table[3]"));
                System.out.println(elementTable.getTagName());
                List<ShunchaoTrademarkTmsve> trademarkTmsveList = new ArrayList<>();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年M月d日");
                SimpleDateFormat simpleFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if (Objects.nonNull(elementTable)) {
                    List<WebElement> elements = elementTable.findElements(By.tagName("tr"));
                    for (int i = 1; i < elements.size(); i++) {
                        WebElement tr = elements.get(i);
                        List<WebElement> tds = tr.findElements(By.tagName("td"));
                        ShunchaoTrademarkTmsve trademarkTmsve = new ShunchaoTrademarkTmsve();
                        if(StringUtils.isEmpty(tds.get(3).getText())){
                            continue;
                        }
                        trademarkTmsve.setTmsveAgencynumber(tds.get(3).getText());

                        String replace = tds.get(4).getText();
                        LocalDate localDate = LocalDate.parse(replace, formatter);
                        Date applyDay = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                        trademarkTmsve.setTmsveApplyday(applyDay);
                        trademarkTmsve.setTmsveApplynumber(tds.get(5).getText());
                        trademarkTmsveList.add(trademarkTmsve);
                    }
                }
                if (trademarkTmsveList.size()>0){
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("trademarkString", JSON.toJSONString(trademarkTmsveList));
                    //维护商标申请号和申请日期
                    String body = HttpRequest.get(connecturl + "/trademark/shunchaoTrademarkTmsve/updateTrademarkinfo").
                            header("X-Access-Token", token).form(map).execute().body();
                    JSONObject jsonObject = JSONObject.parseObject(body);
                    Boolean success = (Boolean) jsonObject.get("success");
                    if (!success) {
                        throw new Exception ("维护商标申请号和申请日期失败");
                    }
                }
                driver.switchTo().parentFrame();
            }
            //菜单
            driver.findElement(By.xpath("//*[@id=\"menu\"]/ul/li[8]/a")).click();

            driver.findElement(By.xpath("//*[@id=\"menu\"]/ul/li[8]/ul/li[1]/a")).click();

            //进入iframe
            driver.switchTo().frame("myframe");
            Thread.sleep(2000);
            driver.findElement(By.xpath("//*[@id=\"date1\"]")).sendKeys(tmsveDate);
            driver.findElement(By.id("but1")).click();
            Thread.sleep(6000);
            WebElement webElement= driver.findElement(By.xpath("//*[@id=\"form1\"]/table[2]"));
            List<WebElement> elements1 = webElement.findElements(By.tagName("font"));
            int size = Integer.valueOf(elements1.get(1).getText());
            System.out.println(size);
            if (size>0){
                Set<Cookie> cookies = driver.manage().getCookies();
                for (Cookie cookie : cookies) {
                    String name = cookie.getName();
                    String value = cookie.getValue();
                    cookieMap.put(name, value);
                }

                System.out.println(cookieMap);
                HashMap<String, Object> paramMap= new HashMap<>();
                paramMap.put("id",enterpriseInfo.get("id"));
                //拉取官文
                paramMap.put("cookie",cookieMap.toString());
				paramMap.put("size",size);
				paramMap.put("tmsveDate",tmsveDate);
                String body = HttpRequest.get(connecturl + "/trademark/shunchaoTrademarkTmsve/getTrademarkTmsve").
                        header("X-Access-Token", token).form(paramMap).execute().body();
                JSONObject jsonObject = JSONObject.parseObject(body);
                Boolean success = (Boolean) jsonObject.get("success");
                if (!success) {
                    throw new Exception ("获取失败");
                }
            }
            driver.close();
            driver.quit();
            return size;
        }catch (Exception e) {
            log.info("获取失败",e);
            driver.close();
            driver.quit();
            throw new Exception ("获取失败");
        }
    }
}
