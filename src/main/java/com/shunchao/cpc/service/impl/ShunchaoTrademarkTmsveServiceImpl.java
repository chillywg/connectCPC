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
}
