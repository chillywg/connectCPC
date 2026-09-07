package com.shunchao.cpc.controller;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.shunchao.cpc.model.*;
import com.shunchao.cpc.service.IShunchaoTrademarkTmsveService;
import com.shunchao.cpc.util.ChromeDriverUtils;
import com.shunchao.cpc.util.FoxDriverUtils;
import com.shunchao.cpc.util.HttpsUrlValidator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * @Description: 商标送案提交
 * @Author: zcs
 * @Date:   2023-05-26
 * @Version: V1.0
 */
@Slf4j
@RestController
@RequestMapping("/trademark/connectTmsve")
public class ShunchaoTrademarkSendController {
    @Autowired
    private IShunchaoTrademarkTmsveService shunchaoTrademarkTmsveService;
    private WebDriver driver;
    //String url = "http://www.baidu.com";
    //private String url = "http://wssq.sbj.cnipa.gov.cn:9080/tmsve/";

    @Value("${trademark.URL}")
    private String url;
    /*@Value("${trademark.PINWORD}")
    private String pinword;*/

    /**
     * 功能描述:商标注册
     * 场景:
     * @Param: [trademarkApplicantProduct, request]
     * @Return: void
     * @Author: Ironz
     * @Date: 2021/12/30 13:37
     */
    @GetMapping(value = "/startUpTmsve")
    public Result<?> startUpTmsve(String trademarkId, HttpServletRequest request, HttpServletResponse response){
        String appformId = "";
        try{
            String token = request.getParameter("token");
            String rootPath = System.getProperty("exe.path");
            String referer ="https://toas.sbj.cnipa.gov.cn/toas-extra-prod/toasui-extra-prod/toas-ui/onlineApplication/applyRegister/registerApply?code=toas_00501";
//            rootPath ="D:\\DUOUEXE\\duou\\";
            //加载附件数据到本地
            shunchaoTrademarkTmsveService.connectTmsveDownloadData(trademarkId, token);
            //加载基本信息
            ShunchaoTrademarkTmsve shunchaoTrademarkinfo = shunchaoTrademarkTmsveService.downloadSendTsvmeData(trademarkId, token);


            String tmToasToken = shunchaoTrademarkinfo.getTmToasToken();

            Map<String, Object> next1Map = new HashMap<>();
            next1Map.put("agentName",shunchaoTrademarkinfo.getAgentName());
            next1Map.put("agentInstName",shunchaoTrademarkinfo.getEnterpriseName());

            next1Map.put("agentPoaAttJson","");
            String url = "";
            if(StringUtils.isNotBlank(shunchaoTrademarkinfo.getAppFormId())){
                next1Map.put("appformId",shunchaoTrademarkinfo.getAppFormId());
                url = "https://toas.sbj.cnipa.gov.cn/toas-extra-prod/extra-prod-api/tempdomestic/web/tmzc/appform/edit";
            }else{
                url = "https://toas.sbj.cnipa.gov.cn/toas-extra-prod/extra-prod-api/tempdomestic/web/tmzc/appform/add";
            }
            next1Map.put("viewCode","toas_0050109");
            next1Map.put("voiceFlag","0");
            next1Map.put("threeDimFlag","0");
            next1Map.put("colrCombFlag","0");
            String next1Body = JSON.toJSONString(next1Map);

            //代理信息暂存
            Connection.Response next1Response = nextJsoup(url,next1Body,tmToasToken,referer);

            String next1Text = next1Response.parse().body().text();
            log.info("---------------next1Text:"+next1Text);
            Map<String, String> next1TextMap = JSON.parseObject(next1Text, new TypeReference<Map<String, String>>() {});
            String next1Data = next1TextMap.get("data");
            Map<String, String> next1DataMap = JSON.parseObject(next1Data, new TypeReference<Map<String, String>>() {});
            appformId = next1DataMap.get("appformId");//流水号

            File sba0023 = new File(rootPath + File.separator + shunchaoTrademarkinfo.getSba0023());
            String agentPoaAttJson = fileUpload(sba0023, tmToasToken,false,null,referer);
            next1Map.put("agentPoaAttJson",agentPoaAttJson);
            next1Map.put("appformId",appformId);
            next1Body = JSON.toJSONString(next1Map);
            //代理信息下一步
            url = "https://toas.sbj.cnipa.gov.cn/toas-extra-prod/extra-prod-api/tempdomestic/web/tmzc/appform/edit";
            nextJsoup(url,next1Body,tmToasToken,referer);

            //申请人信息+申请人附件
            ShunchaoTrademarkApplicant application = shunchaoTrademarkinfo.getApplicant1();//申请人
            Map<String, Object> next2Map = new HashMap<>();
            next2Map.put("appformId",appformId);
            next2Map.put("viewCode","toas_0050108");
            next2Map.put("voiceFlag","0");
            next2Map.put("threeDimFlag","0");
            next2Map.put("colrCombFlag","0");
            if ("0".equals(application.getApplicantType())) {//法人或其他组织
                next2Map.put("applicantType","2");
                if ("0".equals(application.getBookOwnerType())) {//中国大陆
                    String [] stringArray = {"1","1000"};
                    next2Map.put("regionCodeList",Arrays.asList(stringArray));
                    next2Map.put("nationType","1");
                    next2Map.put("regionCode","1000");
                    next2Map.put("regionName","（内地）大陆申请人");
                    next2Map.put("applicantCnName",application.getApplicantName());
                    next2Map.put("idetCertType",application.getIdName());
                    next2Map.put("idetCertName",application.getIdetCertName());
                    next2Map.put("idetCertNo",application.getIdNumber());

                    //上传身份证证明文件
                    File sba0025 = new File(rootPath + File.separator + application.getSba0025());
                    String idetCertCnAttJson = fileUpload(sba0025, tmToasToken,true,application,referer);
                    next2Map.put("idetCertCnAttJson",idetCertCnAttJson);
                    next2Map.put("idetCertEnAttJson","");
                    next2Map.put("objCertCnAttJson","");

                    //申请人地址
                    if(StringUtils.isNotBlank(application.getApplicationAddressArea())){
                        next2Map.put("applicantRegionCode",application.getApplicationAddressArea());
                    }else if(StringUtils.isNotBlank(application.getApplicationAddressCity())){
                        next2Map.put("applicantRegionCode",application.getApplicationAddressCity());
                    }else if(StringUtils.isNotBlank(application.getApplicationAddressProvince())){
                        next2Map.put("applicantRegionCode",application.getApplicationAddressProvince());
                    }
                    next2Map.put("applicantCnAddr",application.getApplicationAddres());
                    next2Map.put("applicantPostalCode",application.getPostalCode());//申请人邮政编码

                    //国内申请人联系地址
                    if(StringUtils.isNotBlank(application.getDmstcApplicationAddressArea())){
                        next2Map.put("applicantRegionCode",application.getDmstcApplicationAddressArea());
                    }else if(StringUtils.isNotBlank(application.getDmstcApplicationAddressCity())){
                        next2Map.put("applicantRegionCode",application.getDmstcApplicationAddressCity());
                    }else if(StringUtils.isNotBlank(application.getDmstcApplicationAddressProvince())){
                        next2Map.put("applicantRegionCode",application.getDmstcApplicationAddressProvince());
                    }else{
                        next2Map.put("applicantRegionCode",null);
                    }
                    if(StringUtils.isNotBlank(application.getDmstcApplicantAddr())){
                        next2Map.put("dmstcApplicantAddr",application.getDmstcApplicantAddr());
                    }else{
                        next2Map.put("dmstcApplicantAddr","");
                    }
                    //国内申请人邮政编码
                    if(StringUtils.isNotBlank(application.getRecipientPostcode())){
                        next2Map.put("dmstcApplicantPostalCode",application.getRecipientPostcode());
                    }else{
                        next2Map.put("dmstcApplicantPostalCode","");
                    }
                    //联系人名称
                    if(StringUtils.isNotBlank(application.getContactName())){
                        next2Map.put("contactName",application.getContactName());
                    }
                    //联系人电话
                    if(StringUtils.isNotBlank(application.getContactPhone())){
                        next2Map.put("contactPhone",application.getContactPhone());
                    }
                } else {
                    if ("1".equals(application.getBookOwnerType())) {
                        String [] stringArray = {"1","990001"};
                        next2Map.put("regionCodeList",Arrays.asList(stringArray));
                        next2Map.put("nationType","1");
                        next2Map.put("regionCode","990001");
                        next2Map.put("regionName","中国台湾");
                    }else if ("2".equals(application.getBookOwnerType())) {
                        String [] stringArray = {"1","2029"};
                        next2Map.put("regionCodeList",Arrays.asList(stringArray));
                        next2Map.put("nationType","1");
                        next2Map.put("regionCode","2029");
                        next2Map.put("regionName","中国香港");
                    }else if ("3".equals(application.getBookOwnerType())) {
                        String [] stringArray = {"1","2039"};
                        next2Map.put("regionCodeList",Arrays.asList(stringArray));
                        next2Map.put("nationType","1");
                        next2Map.put("regionCode","2039");
                        next2Map.put("regionName","中国澳门");
                    }else if ("4".equals(application.getBookOwnerType())) {
                        return Result.error("暂不支持做国外的商标");
                    }
                    next2Map.put("applicantCnName",application.getApplicantName());
                    if(StringUtils.isNotBlank(application.getApplicantOwnerEnglishname())){
                        next2Map.put("applicantEnName",application.getApplicantOwnerEnglishname());
                    }else{
                        next2Map.put("applicantEnName","");
                    }
                    next2Map.put("idetCertType",application.getIdName());
                    next2Map.put("idetCertName",application.getIdetCertName());
                    next2Map.put("idetCertNo",application.getIdNumber());

                    //上传身份证证明文件（中文）
                    File sba0025 = new File(rootPath + File.separator + application.getSba0025());
                    String idetCertCnAttJson = fileUpload(sba0025, tmToasToken,false,application,referer);
                    next2Map.put("idetCertCnAttJson",idetCertCnAttJson);

                    next2Map.put("certFileCnFlag",application.getWhetherOriginaldocChinese());
                    if(application.getWhetherOriginaldocChinese().equals("1")){
                        next2Map.put("idetCertEnAttJson","");
                    }else{
                        //上传身份证证明文件（外文）
                        File sba0026 = new File(rootPath + File.separator + application.getSba0026());
                        String idetCertEnAttJson = fileUpload(sba0026, tmToasToken,false,application,referer);
                        next2Map.put("idetCertEnAttJson",idetCertEnAttJson);
                    }
                    next2Map.put("objCertCnAttJson","");
                    next2Map.put("applicantCnAddr",application.getApplicationAddres());
                    if(StringUtils.isNotBlank(application.getApplicationAddressEnglish())){
                        next2Map.put("applicantEnAddr",application.getApplicationAddressEnglish());
                    }else{
                        next2Map.put("applicantEnAddr","");
                    }

                    //内地（大陆）文件接收人名称
                    next2Map.put("mainlandReceiverName",application.getApplicationMainlandRecipientname());
                    //内地（大陆）文件接收人名称
                    if(StringUtils.isNotBlank(application.getDmstcApplicationAddressArea())){
                        next2Map.put("mainlandReceiverRegionCode",application.getDmstcApplicationAddressArea());
                    }else if(StringUtils.isNotBlank(application.getDmstcApplicationAddressCity())){
                        next2Map.put("mainlandReceiverRegionCode",application.getDmstcApplicationAddressCity());
                    }else if(StringUtils.isNotBlank(application.getDmstcApplicationAddressProvince())){
                        next2Map.put("mainlandReceiverRegionCode",application.getDmstcApplicationAddressProvince());
                    }
                    next2Map.put("mainlandReceiverAddr",application.getMainlandReceiverAddr());
                    //接收人邮政编码
                    next2Map.put("mainlandReceiverPostalCode",application.getRecipientPostcode());
                }
            } else if ("1".equals(application.getApplicantType())) {//自然人
                next2Map.put("applicantType","1");
                if ("0".equals(application.getBookOwnerType())) {//中国大陆
                    String [] stringArray = {"1","1000"};
                    next2Map.put("regionCodeList",Arrays.asList(stringArray));
                    next2Map.put("nationType","1");
                    next2Map.put("regionCode","1000");
                    next2Map.put("regionName","（内地）大陆申请人");
                    next2Map.put("applicantCnName",application.getApplicantName());
                    next2Map.put("idetCertType",application.getIdName());
                    next2Map.put("idetCertName",application.getIdetCertName());
                    next2Map.put("idetCertNo",application.getIdNumber());

                    //上传身份证证明文件
                    File sba0025 = new File(rootPath + File.separator + application.getSba0025());
                    String idetCertCnAttJson = fileUpload(sba0025, tmToasToken,true,application,referer);
                    next2Map.put("idetCertCnAttJson",idetCertCnAttJson);
                    next2Map.put("idetCertEnAttJson","");

                    //上传主体资格证明文件
                    next2Map.put("objCertType",application.getObjCertType());
                    if(application.getObjCertType().equals("1")){
                        next2Map.put("objCertName","个体工商户营业执照");
                    }else if(application.getObjCertType().equals("2")){
                        next2Map.put("objCertName","农村土地承包经营合同复印件");
                    }else if(application.getObjCertType().equals("3")){
                        next2Map.put("objCertName","其他");
                    }
                    File sba0027 = new File(rootPath + File.separator + application.getSba0027());
                    String objCertCnAttJson = fileUpload(sba0027, tmToasToken,false,application,referer);
                    next2Map.put("objCertCnAttJson",objCertCnAttJson);
                    if(application.getObjCertType().equals("1")){
                        next2Map.put("uscc",application.getUnifiedSocialCreditcode());
                    }
                    //申请人地址
                    if(StringUtils.isNotBlank(application.getApplicationAddressArea())){
                        next2Map.put("applicantRegionCode",application.getApplicationAddressArea());
                    }else if(StringUtils.isNotBlank(application.getApplicationAddressCity())){
                        next2Map.put("applicantRegionCode",application.getApplicationAddressCity());
                    }else if(StringUtils.isNotBlank(application.getApplicationAddressProvince())){
                        next2Map.put("applicantRegionCode",application.getApplicationAddressProvince());
                    }
                    next2Map.put("applicantCnAddr",application.getApplicationAddres());
                    next2Map.put("applicantPostalCode",application.getPostalCode());//申请人邮政编码

                    //国内申请人联系地址
                    if(StringUtils.isNotBlank(application.getDmstcApplicationAddressArea())){
                        next2Map.put("dmstcApplicantRegionCode",application.getDmstcApplicationAddressArea());
                    }else if(StringUtils.isNotBlank(application.getDmstcApplicationAddressCity())){
                        next2Map.put("dmstcApplicantRegionCode",application.getDmstcApplicationAddressCity());
                    }else if(StringUtils.isNotBlank(application.getDmstcApplicationAddressProvince())){
                        next2Map.put("dmstcApplicantRegionCode",application.getDmstcApplicationAddressProvince());
                    }else{
                        next2Map.put("dmstcApplicantRegionCode",null);
                    }
                    if(StringUtils.isNotBlank(application.getDmstcApplicantAddr())){
                        next2Map.put("dmstcApplicantAddr",application.getDmstcApplicantAddr());
                    }else{
                        next2Map.put("dmstcApplicantAddr","");
                    }
                    //国内申请人邮政编码
                    if(StringUtils.isNotBlank(application.getRecipientPostcode())){
                        next2Map.put("dmstcApplicantPostalCode",application.getRecipientPostcode());
                    }else{
                        next2Map.put("dmstcApplicantPostalCode","");
                    }
                    //联系人名称
                    if(StringUtils.isNotBlank(application.getContactName())){
                        next2Map.put("contactName",application.getContactName());
                    }
                    //联系人电话
                    if(StringUtils.isNotBlank(application.getContactPhone())){
                        next2Map.put("contactPhone",application.getContactPhone());
                    }
                } else {
                    if ("1".equals(application.getBookOwnerType())) {
                        String [] stringArray = {"1","990001"};
                        next2Map.put("regionCodeList",Arrays.asList(stringArray));
                        next2Map.put("nationType","1");
                        next2Map.put("regionCode","990001");
                        next2Map.put("regionName","中国台湾");
                    }else if ("2".equals(application.getBookOwnerType())) {
                        String [] stringArray = {"1","2029"};
                        next2Map.put("regionCodeList",Arrays.asList(stringArray));
                        next2Map.put("nationType","1");
                        next2Map.put("regionCode","2029");
                        next2Map.put("regionName","中国香港");
                    }else if ("3".equals(application.getBookOwnerType())) {
                        String [] stringArray = {"1","2039"};
                        next2Map.put("regionCodeList",Arrays.asList(stringArray));
                        next2Map.put("nationType","1");
                        next2Map.put("regionCode","2039");
                        next2Map.put("regionName","中国澳门");
                    }else if ("4".equals(application.getBookOwnerType())) {
                        return Result.error("暂不支持做国外的商标");
                    }
                    next2Map.put("applicantCnName",application.getApplicantName());
                    if(StringUtils.isNotBlank(application.getApplicantOwnerEnglishname())){
                        next2Map.put("applicantEnName",application.getApplicantOwnerEnglishname());
                    }else{
                        next2Map.put("applicantEnName","");
                    }
                    next2Map.put("idetCertType",application.getIdName());
                    next2Map.put("idetCertName",application.getIdetCertName());
                    next2Map.put("idetCertNo",application.getIdNumber());

                    //上传身份证证明文件（中文）
                    File sba0025 = new File(rootPath + File.separator + application.getSba0025());
                    String idetCertCnAttJson = fileUpload(sba0025, tmToasToken,true,application,referer);
                    next2Map.put("idetCertCnAttJson",idetCertCnAttJson);

                    next2Map.put("certFileCnFlag",application.getWhetherOriginaldocChinese());
                    if(application.getWhetherOriginaldocChinese().equals("1")){
                        next2Map.put("idetCertEnAttJson","");
                    }else{
                        //上传身份证证明文件（外文）
                        File sba0026 = new File(rootPath + File.separator + application.getSba0026());
                        String idetCertEnAttJson = fileUpload(sba0026, tmToasToken,false,application,referer);
                        next2Map.put("idetCertEnAttJson",idetCertEnAttJson);
                    }
                    next2Map.put("objCertCnAttJson","");
                    next2Map.put("applicantCnAddr",application.getApplicationAddres());
                    if(StringUtils.isNotBlank(application.getApplicationAddressEnglish())){
                        next2Map.put("applicantEnAddr",application.getApplicationAddressEnglish());
                    }else{
                        next2Map.put("applicantEnAddr","");
                    }

                    //内地（大陆）文件接收人名称
                    next2Map.put("mainlandReceiverName",application.getApplicationMainlandRecipientname());
                    //内地（大陆）文件接收人名称
                    if(StringUtils.isNotBlank(application.getDmstcApplicationAddressArea())){
                        next2Map.put("mainlandReceiverRegionCode",application.getDmstcApplicationAddressArea());
                    }else if(StringUtils.isNotBlank(application.getDmstcApplicationAddressCity())){
                        next2Map.put("mainlandReceiverRegionCode",application.getDmstcApplicationAddressCity());
                    }else if(StringUtils.isNotBlank(application.getDmstcApplicationAddressProvince())){
                        next2Map.put("mainlandReceiverRegionCode",application.getDmstcApplicationAddressProvince());
                    }
                    next2Map.put("mainlandReceiverAddr",application.getMainlandReceiverAddr());
                    //接收人邮政编码
                    next2Map.put("mainlandReceiverPostalCode",application.getRecipientPostcode());
                }
            }

            String next2Body = JSON.toJSONString(next2Map);
            //申请人信息下一步
            nextJsoup("https://toas.sbj.cnipa.gov.cn/toas-extra-prod/extra-prod-api/tempdomestic/web/tmzc/appform/edit",next2Body,tmToasToken,referer);

            Map<String, Object> next3Map = new HashMap<>();
            next3Map.put("voiceFlag","0");//声音商标，默认否，目前没做过声音商标
            next3Map.put("threeDimFlag",shunchaoTrademarkinfo.getWhetherThreedimensionLogo());//是否三维标志
            next3Map.put("colrCombFlag",shunchaoTrademarkinfo.getWhetherColorCombination());//是否颜色组合
            next3Map.put("tmType","P");
            next3Map.put("voiceAttJson","");
            next3Map.put("gtmRuleAttJson","");
            next3Map.put("gtmMemberAttJson","");
            next3Map.put("ztmRuleAttJson","");
            next3Map.put("othViceAttJson","");
            next3Map.put("commChkCntrCertAttJson","");
            next3Map.put("commQuaCertAttJson","");
            next3Map.put("commChkInvyAttJson","");
            next3Map.put("commTechListAttJson","");
            next3Map.put("chkQuaCertAttJson","");
            next3Map.put("chkInvyAttJson","");
            next3Map.put("techListAttJson","");
            next3Map.put("langmarkAtt1Json","");
            next3Map.put("langmarkAtt2Json","");
            next3Map.put("langmarkAtt3Json","");
            next3Map.put("langmarkAtt4Json","");
            next3Map.put("appformId",appformId);
            next3Map.put("viewCode","toas_0050105");

            String next3Body = JSON.toJSONString(next3Map);
            //商标声明下一步
            nextJsoup("https://toas.sbj.cnipa.gov.cn/toas-extra-prod/extra-prod-api/tempdomestic/web/tmzc/declar/add",next3Body,tmToasToken,referer);

            Map<String, Object> next4Map = new HashMap<>();
            next4Map.put("jointApplicationFlag",0);
            next4Map.put("appformId",appformId);
            next4Map.put("viewCode","");
            String next4Body = JSON.toJSONString(next4Map);
            //共同申请信息下一步
            nextJsoup("https://toas.sbj.cnipa.gov.cn/toas-extra-prod/extra-prod-api/tempdomestic/web/tmzc/coowner/add",next4Body,tmToasToken,referer);

            Map<String, Object> next5Map = new HashMap<>();
            next5Map.put("prrtType",0);
            next5Map.put("prrtCertAttJson","");
            next5Map.put("appformId",appformId);
            next5Map.put("viewCode","toas_0050106");
            String next5Body = JSON.toJSONString(next5Map);
            //优先权信息下一步
            nextJsoup("https://toas.sbj.cnipa.gov.cn/toas-extra-prod/extra-prod-api/tempdomestic/web/tmzc/prrt/add",next5Body,tmToasToken,referer);

            Map<String, Object> next6Map = new HashMap<>();
            next6Map.put("appformId",appformId);

            //商标商品
            List<ShunchaoTrademarkProduct> products = shunchaoTrademarkinfo.getTrademarkProducts();//产品
            List<Map<String, Object>> goodslist = new ArrayList<>();
            for(ShunchaoTrademarkProduct product:products){
                //商品查询
                Map<String, Object> goodsMap = new HashMap<>();
                goodsMap.put("intlCls",product.getProductMainCategory());
                goodsMap.put("goodsCnName",product.getSname());
                goodsMap.put("goodsCode",product.getScode());
                goodsMap.put("pageIndex",1);
                String goodsBody = JSON.toJSONString(goodsMap);
                Connection.Response goodsResponse = Jsoup.connect("https://toas.sbj.cnipa.gov.cn/toas-extra-prod/extra-prod-api/system/dict/goods/queryGoods")
                        .method(Connection.Method.POST)
                        .requestBody(goodsBody)
                        .header("Accept", "application/json, text/plain, */*")
                        .header("Accept-Encoding", "gzip, deflate, br, zstd")
                        .header("Accept-Language", "zh-CN,zh;q=0.9")
                        .header("Referer", "https://toas.sbj.cnipa.gov.cn/toas-extra-prod/toasui-extra-prod/toas-ui/onlineApplication/applyRegister/registerApply?code=toas_00501")
                        .header("Content-Type", "application/json;charset=UTF-8")
                        .header("Authorization", tmToasToken)
                        .header("Host", "https://toas.sbj.cnipa.gov.cn")
                        .ignoreContentType(true).execute();
                String goodsText = goodsResponse.parse().body().text();
                Map<String, String> goodsTextMap = JSON.parseObject(goodsText, new TypeReference<Map<String, String>>() {});
                String goodsData = goodsTextMap.get("data");
                Map<String, String> goodsDataMap = JSON.parseObject(goodsData, new TypeReference<Map<String, String>>() {});
                String listString = goodsDataMap.get("list");
                List<Map<String, String>> list = JSON.parseObject(listString, new TypeReference<List<Map<String, String>>>() {});
                if(list.size()<1){
                    return Result.error("查询不到该商品");
                }else if(list.size()==1){
                    Map<String, String> stringMap = list.get(0);
                    Map<String, Object> goods =new HashMap<>();
                    goods.put("intlCls",stringMap.get("intlCls"));
                    goods.put("intlClsName",stringMap.get("intlClsName"));
                    goods.put("goodsCode",stringMap.get("goodsCode"));
                    goods.put("goodsCnName",stringMap.get("goodsCnName"));
                    goods.put("goodsType",stringMap.get("goodsType"));
                    goods.put("similarCode",stringMap.get("similarCode"));
                    goods.put("similarName",stringMap.get("similarName"));
                    goods.put("goodsValidFlag",stringMap.get("goodsValidFlag"));
                    goods.put("goodsId",stringMap.get("goodsId"));
                    goods.put("stdGoodsFlag",1);
                    goods.put("goodsSeq",product.getIndexNumber());
                    goodslist.add(goods);
                }else{
                    return Result.error("查询到多个商品");
                }
            }
            next6Map.put("goodsList",goodslist);
            next6Map.put("viewCode","toas_0050104");
            String next6Body = JSON.toJSONString(next6Map);
            //商品下一步
            nextJsoup("https://toas.sbj.cnipa.gov.cn/toas-extra-prod/extra-prod-api/tempdomestic/web/tmzc/goods/add",next6Body,tmToasToken,referer);

            Map<String, Object> next7Map = new HashMap<>();
            //上传图样
            File sba0020 = new File(rootPath + File.separator + shunchaoTrademarkinfo.getSba0020());
            String imageAttJson = fileUploadImg(sba0020, tmToasToken,referer);
            next7Map.put("imageAttJson",imageAttJson);
            next7Map.put("tmName",shunchaoTrademarkinfo.getTrademarkName());
            //有关说明文件
            if(StringUtils.isNotBlank(shunchaoTrademarkinfo.getSba0022())){
                File sba0022 = new File(rootPath + File.separator + shunchaoTrademarkinfo.getSba0022());
                String othAttJson = fileUpload(sba0022, tmToasToken,false,null,referer);
                next7Map.put("othAttJson",othAttJson);
            }else{
                next7Map.put("othAttJson","");
            }
            //是否使用肖像作为商标
            if(shunchaoTrademarkinfo.getApplicationRegistrationApplication().equals("1")){
                next7Map.put("avatarFlag","1");
                File sba0021 = new File(rootPath + File.separator + shunchaoTrademarkinfo.getSba0021());
                String avatarAttJson = fileUpload(sba0021, tmToasToken,false,null,referer);
                next7Map.put("avatarAttJson",avatarAttJson);
            }else{
                next7Map.put("avatarFlag","0");
                next7Map.put("avatarAttJson","");
            }
            next7Map.put("tmDesc",shunchaoTrademarkinfo.getTrademarkDescription());
            if(Objects.nonNull(shunchaoTrademarkinfo.getDesignDesc())){
                next7Map.put("designDesc",shunchaoTrademarkinfo.getDesignDesc());
            }else {
                next7Map.put("designDesc","");
            }
            next7Map.put("appformId",appformId);
            next7Map.put("viewCode","toas_0050107");
            next7Map.put("voiceFlag","0");
            next7Map.put("threeDimFlag","0");
            next7Map.put("colrCombFlag","0");

            String next7Body = JSON.toJSONString(next7Map);
            //商标图样，最后一步
            nextJsoup("https://toas.sbj.cnipa.gov.cn/toas-extra-prod/extra-prod-api/tempdomestic/web/tmzc/image/add",next7Body,tmToasToken,referer);

        }catch (Exception e){
            log.info("送案失败",e);
            return Result.error("送案失败");
        }
        return Result.ok(appformId);
    }
    //商标点击下一步
    public Connection.Response nextJsoup(String url, String nextBody, String tmToasToken,String referer) throws IOException {
        log.info("------------nextBody:"+nextBody);
        Connection.Response nextResponse = Jsoup.connect(url)
                .method(Connection.Method.POST)
                .requestBody(nextBody)
                .header("Accept", "application/json, text/plain, */*")
                .header("Accept-Encoding", "gzip, deflate, br, zstd")
                .header("Accept-Language", "zh-CN,zh;q=0.9")
                .header("Referer", referer)
                .header("Content-Type", "application/json;charset=UTF-8")
                .header("Authorization", tmToasToken)
                .ignoreContentType(true).ignoreHttpErrors(true).execute();
        String body = nextResponse.body();
        log.info("----nextResponse:---"+body);
        return nextResponse;
    }

    public String fileUpload(File file,String tmToasToken,Boolean isOcr,ShunchaoTrademarkApplicant application,String referer) throws IOException {
//        Map<String, Object> paramMap = new HashMap<>();
//        paramMap.put("file", file);
//        HttpResponse response = HttpRequest.post("https://toas.sbj.cnipa.gov.cn/toas-extra-prod/extra-prod-api/oss/oss/upload")
//                .header("Accept", "application/json, text/plain, */*")
//                .header("Accept-Encoding", "gzip, deflate, br, zstd")
//                .header("Accept-Language", "zh-CN,zh;q=0.9")
//                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/109.0.0.0 Safari/537.36")
//                .header("Referer", referer)
//                .header("Content-Type", "multipart/form-data; boundary=----WebKitFormBoundaryTA4Aug48H2HkOucM")
//                .header("Authorization", tmToasToken)
//                .form(paramMap).execute();
        Connection.Response response = Jsoup.connect("https://toas.sbj.cnipa.gov.cn/toas-extra-prod/extra-prod-api/oss/oss/upload")
                .method(Connection.Method.POST)
                .header("Connection", "keep-alive")
                .header("sec-ch-ua-platform", "\"Windows\"")
                .header("Authorization", tmToasToken)
                .header("sec-ch-ua", "\"Google Chrome\";v=\"148\", \"Chromium\";v=\"148\", \"Not)A;Brand\";v=\"99\"")
                .header("sec-ch-ua-mobile", "?0")
                .header("Accept", "application/json, text/plain, */*")
                .header("X-Client-IP", "")
                .header("Origin", "https://toas.sbj.cnipa.gov.cn")
                .header("Sec-Fetch-Site", "same-origin")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Dest", "empty")
                .header("Referer", referer)
                .header("Accept-Encoding", "gzip, deflate, br, zstd")
                .header("Accept-Language", "zh-CN,zh;q=0.9")
                .data("file", file.getName(), new FileInputStream(file))
                .ignoreContentType(true)
                .ignoreHttpErrors(true)
                .execute();
        HttpRequest.closeCookie();//清除Cookie
        String body = response.body();
        log.info("----fileUploadbody:---"+body);
        Map<String, String> map = JSON.parseObject(body, new TypeReference<Map<String, String>>() {});
        String data = map.get("data");
        Map<String, String> dataMap = JSON.parseObject(data, new TypeReference<Map<String, String>>() {});
        Map<String, Object>  attJsonMap = new HashMap();
        attJsonMap.put("fileName",dataMap.get("fileName"));
        attJsonMap.put("attId",dataMap.get("ossId"));
        attJsonMap.put("attName",dataMap.get("originalName"));
        attJsonMap.put("ossId",dataMap.get("ossId"));
        attJsonMap.put("url",dataMap.get("url"));
        attJsonMap.put("size",Integer.valueOf(dataMap.get("fileSize")));
        attJsonMap.put("fileSize",Integer.valueOf(dataMap.get("fileSize")));
        if(isOcr){
            //ocr识别
            Map<String, Object> orcMap = new HashMap<>();
            orcMap.put("ossId",dataMap.get("ossId"));
            orcMap.put("ocrType",application.getIdName());
            String orcBody = JSON.toJSONString(orcMap);
            Connection.Response orcResponse = Jsoup.connect("https://toas.sbj.cnipa.gov.cn/toas-extra-prod/extra-prod-api/oss/ocr/identify")
                    .method(Connection.Method.POST)
                    .requestBody(orcBody)
                    .header("Accept", "application/json, text/plain, */*")
                    .header("Accept-Encoding", "gzip, deflate, br, zstd")
                    .header("Accept-Language", "zh-CN,zh;q=0.9")
                    .header("Referer", referer)
                    .header("Content-Type", "application/json;charset=UTF-8")
                    .header("Authorization", tmToasToken)
                    .ignoreContentType(true).execute();
            HttpRequest.closeCookie();//清除Cookie
            String orcText = orcResponse.parse().body().text();
            log.info("----fileUploadorcText:---"+orcText);
            Map<String, String> orcTextMap = JSON.parseObject(orcText, new TypeReference<Map<String, String>>() {});
            String orcData = orcTextMap.get("data");
            attJsonMap.put("orcResult",orcData);
        }
        List<Map<String, Object>> attJsonList = new ArrayList<>();
        attJsonList.add(attJsonMap);
        String attJson = JSON.toJSONString(attJsonList);
        log.info(attJson);
        return attJson;
    }

    public String fileUploadImg(File file,String tmToasToken,String referer) throws IOException {
        Connection.Response response = Jsoup.connect("https://toas.sbj.cnipa.gov.cn/toas-extra-prod/extra-prod-api/oss/oss/uploadImg")
                .method(Connection.Method.POST)
                .header("Connection", "keep-alive")
                .header("sec-ch-ua-platform", "\"Windows\"")
                .header("Authorization", tmToasToken)
                .header("sec-ch-ua", "\"Google Chrome\";v=\"149\", \"Chromium\";v=\"149\", \"Not)A;Brand\";v=\"24\"")
                .header("sec-ch-ua-mobile", "?0")
                .header("Accept", "application/json, text/plain, */*")
                .header("X-Client-IP", "")
                .header("Origin", "https://toas.sbj.cnipa.gov.cn")
                .header("Sec-Fetch-Site", "same-origin")
                .header("Sec-Fetch-Mode", "cors")
                .header("Sec-Fetch-Dest", "empty")
                .header("Referer", referer)
                .header("Accept-Encoding", "gzip, deflate, br, zstd")
                .header("Accept-Language", "zh-CN,zh;q=0.9")
//                .cookie("Hm_lvt_9b6808db613ab359882c1b06397459f7", cookie)
                .data("file", file.getName(), new FileInputStream(file))
                .ignoreContentType(true)
                .ignoreHttpErrors(true)
                .execute();
        HttpRequest.closeCookie();//清除Cookie
        String body = response.body();
        log.info("----fileUploadImg:---"+body);
        Map<String, String> map = JSON.parseObject(body, new TypeReference<Map<String, String>>() {});
        String data = map.get("data");
        Map<String, String> dataMap = JSON.parseObject(data, new TypeReference<Map<String, String>>() {});
        Map<String, Object>  attJsonMap = new HashMap();
        attJsonMap.put("fileName",dataMap.get("fileName"));
        attJsonMap.put("attId",dataMap.get("ossId"));
        attJsonMap.put("attName",dataMap.get("originalName"));
        attJsonMap.put("ossId",dataMap.get("ossId"));
        attJsonMap.put("url",dataMap.get("url"));
        attJsonMap.put("size",Integer.valueOf(dataMap.get("fileSize")));
        attJsonMap.put("fileSize",Integer.valueOf(dataMap.get("fileSize")));
        List<Map<String, Object>> attJsonList = new ArrayList<>();
        attJsonList.add(attJsonMap);
        String attJson = JSON.toJSONString(attJsonList);
        log.info(attJson);
        return attJson;
    }

    /**
     * 功能描述:商标变更提交商标局
     * 场景:
     * @Param: [trademarkAnnexList, request]
     * @Return: java.lang.String
     * @Author: zcs
     * @Date: 2024/6/03
     */
    @GetMapping(value = "/startBGTmsve")
    public Result<?> startBGTmsve(String trademarkId, HttpServletRequest request, HttpServletResponse response) throws InterruptedException {
        String appformId = "";
        try{
            String referer ="https://toas.sbj.cnipa.gov.cn/toas-extra-prod/toasui-extra-prod/toas-ui/onlineApplication/followUp/change?code=toas_0050202";
            String token = request.getParameter("token");
            String rootPath = System.getProperty("exe.path");
//            rootPath ="D:\\DUOUEXE\\duou\\";

            //加载申请人附件数据到本地
            shunchaoTrademarkTmsveService.connectTmsveDownloadData(trademarkId, token);

            //加载申请人基本信息
            ShunchaoTrademarkTmsve shunchaoTrademarkinfo = shunchaoTrademarkTmsveService.getSendTsvmeData(trademarkId, token);

            String tmToasToken = shunchaoTrademarkinfo.getTmToasToken();

            Map<String, Object> next1Map = new HashMap<>();
            next1Map.put("agentName",shunchaoTrademarkinfo.getAgentName());
            next1Map.put("agentInstName",shunchaoTrademarkinfo.getEnterpriseName());

            next1Map.put("agentPoaAttJson","");
            String url = "";
            if(StringUtils.isNotBlank(shunchaoTrademarkinfo.getAppFormId())){
                next1Map.put("appformId",shunchaoTrademarkinfo.getAppFormId());
            }else{
                next1Map.put("appformId","");
            }
            url = "https://toas.sbj.cnipa.gov.cn/toas-extra-prod/extra-prod-api/tempsubsq/web/tmbg/appform/save";
            next1Map.put("viewCode","toas_005020201");
            String next1Body = JSON.toJSONString(next1Map);

            //代理信息暂存
            Connection.Response next1Response = nextJsoup(url,next1Body,tmToasToken,referer);

            String next1Text = next1Response.parse().body().text();
            log.info("---------------next1Text:"+next1Text);
            Map<String, String> next1TextMap = JSON.parseObject(next1Text, new TypeReference<Map<String, String>>() {});
            String next1Data = next1TextMap.get("data");
            Map<String, String> next1DataMap = JSON.parseObject(next1Data, new TypeReference<Map<String, String>>() {});
            appformId = next1DataMap.get("appformId");//流水号

            File sba0023 = new File(rootPath + File.separator + shunchaoTrademarkinfo.getSba0023());
            String agentPoaAttJson = fileUpload(sba0023, tmToasToken,false,null,referer);
            next1Map.put("agentPoaAttJson",agentPoaAttJson);
            next1Map.put("appformId",appformId);
            next1Body = JSON.toJSONString(next1Map);
            //代理信息下一步
            nextJsoup(url,next1Body,tmToasToken,referer);

            ShunchaoTrademarkApplicant application1 = shunchaoTrademarkinfo.getApplicant1();//变更前申请人
            //申请人信息+申请人附件
            ShunchaoTrademarkApplicant application2 = shunchaoTrademarkinfo.getApplicant2();//变更后申请人
            Map<String, Object> next2Map = new HashMap<>();
            next2Map.put("appformId",appformId);
            next2Map.put("viewCode","toas_005020203");
            if ("0".equals(application2.getApplicantType())) {//法人或其他组织
                next2Map.put("applicantType","2");
                if ("0".equals(application2.getBookOwnerType())) {//中国大陆
                    String [] stringArray = {"1","1000"};
                    next2Map.put("regionCodeList",Arrays.asList(stringArray));
                    next2Map.put("applicantCnName",application2.getApplicantName());
                    next2Map.put("idetCertType",application2.getIdName());
                    next2Map.put("idetCertName",application2.getIdetCertName());
                    next2Map.put("idetCertNo",application2.getIdNumber());

                    //上传身份证证明文件
                    File sba0025 = new File(rootPath + File.separator + application2.getSba0025());
                    String idetCertCnAttJson = fileUpload(sba0025, tmToasToken,false,application2,referer);
                    next2Map.put("idetCertCnAttJson",idetCertCnAttJson);
                    next2Map.put("idetCertEnAttJson","");
                    next2Map.put("objCertCnAttJson","");

                    //申请人地址
                    if(StringUtils.isNotBlank(application2.getApplicationAddressArea())){
                        next2Map.put("applicantRegionCode",application2.getApplicationAddressArea());
                    }else if(StringUtils.isNotBlank(application2.getApplicationAddressCity())){
                        next2Map.put("applicantRegionCode",application2.getApplicationAddressCity());
                    }else if(StringUtils.isNotBlank(application2.getApplicationAddressProvince())){
                        next2Map.put("applicantRegionCode",application2.getApplicationAddressProvince());
                    }
                    next2Map.put("applicantCnAddr",application2.getApplicationAddres());
                    if(StringUtils.isNotBlank(application2.getPostalCode())){
                        next2Map.put("applicantPostalCode",application2.getPostalCode());//申请人邮政编码
                    }else {
                        next2Map.put("applicantPostalCode","");//申请人邮政编码
                    }

                    //联系人名称
                    if(StringUtils.isNotBlank(application2.getContactName())){
                        next2Map.put("contactName",application2.getContactName());
                    }
                    //联系人电话
                    if(StringUtils.isNotBlank(application2.getContactPhone())){
                        next2Map.put("contactPhone",application2.getContactPhone());
                    }
                } else {
                    if ("1".equals(application2.getBookOwnerType())) {
                        String [] stringArray = {"1","990001"};
                        next2Map.put("regionCodeList",Arrays.asList(stringArray));
                        next2Map.put("nationType","1");
                        next2Map.put("regionCode","990001");
                        next2Map.put("regionName","中国台湾");
                    }else if ("2".equals(application2.getBookOwnerType())) {
                        String [] stringArray = {"1","2029"};
                        next2Map.put("regionCodeList",Arrays.asList(stringArray));
                        next2Map.put("nationType","1");
                        next2Map.put("regionCode","2029");
                        next2Map.put("regionName","中国香港");
                    }else if ("3".equals(application2.getBookOwnerType())) {
                        String [] stringArray = {"1","2039"};
                        next2Map.put("regionCodeList",Arrays.asList(stringArray));
                        next2Map.put("nationType","1");
                        next2Map.put("regionCode","2039");
                        next2Map.put("regionName","中国澳门");
                    }else if ("4".equals(application2.getBookOwnerType())) {
                        return Result.error("暂不支持做国外的商标");
                    }
                    next2Map.put("applicantCnName",application2.getApplicantName());
                    if(StringUtils.isNotBlank(application2.getApplicantOwnerEnglishname())){
                        next2Map.put("applicantEnName",application2.getApplicantOwnerEnglishname());
                    }else{
                        next2Map.put("applicantEnName","");
                    }
                    next2Map.put("idetCertType",application2.getIdName());
                    next2Map.put("idetCertName",application2.getIdetCertName());
                    next2Map.put("idetCertNo",application2.getIdNumber());

                    //上传身份证证明文件（中文）
                    File sba0025 = new File(rootPath + File.separator + application2.getSba0025());
                    String idetCertCnAttJson = fileUpload(sba0025, tmToasToken,false,application2,referer);
                    next2Map.put("idetCertCnAttJson",idetCertCnAttJson);

                    next2Map.put("certFileCnFlag",application2.getWhetherOriginaldocChinese());
                    if(application2.getWhetherOriginaldocChinese().equals("1")){
                        next2Map.put("idetCertEnAttJson","");
                    }else{
                        //上传身份证证明文件（外文）
                        File sba0026 = new File(rootPath + File.separator + application2.getSba0026());
                        String idetCertEnAttJson = fileUpload(sba0026, tmToasToken,false,application2,referer);
                        next2Map.put("idetCertEnAttJson",idetCertEnAttJson);
                    }
                    next2Map.put("objCertCnAttJson","");
                    next2Map.put("applicantCnAddr",application2.getApplicationAddres());
                    if(StringUtils.isNotBlank(application2.getApplicationAddressEnglish())){
                        next2Map.put("applicantEnAddr",application2.getApplicationAddressEnglish());
                    }else{
                        next2Map.put("applicantEnAddr","");
                    }

                    if(StringUtils.isNotBlank(application2.getPostalCode())){
                        next2Map.put("applicantPostalCode",application2.getPostalCode());//申请人邮政编码
                    }else {
                        next2Map.put("applicantPostalCode","");//申请人邮政编码
                    }
                    //联系人名称
                    if(StringUtils.isNotBlank(application2.getContactName())){
                        next2Map.put("contactName",application2.getContactName());
                    }
                    //联系人电话
                    if(StringUtils.isNotBlank(application2.getContactPhone())){
                        next2Map.put("contactPhone",application2.getContactPhone());
                    }
                }
            } else if ("1".equals(application2.getApplicantType())) {//自然人
                next2Map.put("applicantType","1");
                if ("0".equals(application2.getBookOwnerType())) {//中国大陆
                    String [] stringArray = {"1","1000"};
                    next2Map.put("regionCodeList",Arrays.asList(stringArray));
                    next2Map.put("nationType","1");
                    next2Map.put("regionCode","1000");
                    next2Map.put("regionName","（内地）大陆申请人");
                    next2Map.put("applicantCnName",application2.getApplicantName());
                    next2Map.put("idetCertType",application2.getIdName());
                    next2Map.put("idetCertName",application2.getIdetCertName());
                    next2Map.put("idetCertNo",application2.getIdNumber());

                    //上传身份证证明文件
                    File sba0025 = new File(rootPath + File.separator + application2.getSba0025());
                    String idetCertCnAttJson = fileUpload(sba0025, tmToasToken,false,application2,referer);
                    next2Map.put("idetCertCnAttJson",idetCertCnAttJson);
                    next2Map.put("idetCertEnAttJson","");

                    //上传主体资格证明文件
                    if(StringUtils.isNotBlank(application2.getObjCertType())){
                        next2Map.put("objCertType",application2.getObjCertType());
                        if(application2.getObjCertType().equals("1")){
                            next2Map.put("objCertName","个体工商户营业执照");
                        }else if(application2.getObjCertType().equals("2")){
                            next2Map.put("objCertName","农村土地承包经营合同复印件");
                        }else if(application2.getObjCertType().equals("3")){
                            next2Map.put("objCertName","其他");
                        }
                    }
                    if(StringUtils.isNotBlank(application2.getSba0027())){
                        File sba0027 = new File(rootPath + File.separator + application2.getSba0027());
                        String objCertCnAttJson = fileUpload(sba0027, tmToasToken,false,application2,referer);
                        next2Map.put("objCertCnAttJson",objCertCnAttJson);
                    }
                    if(application2.getObjCertType().equals("1")&&StringUtils.isNotBlank(application2.getUnifiedSocialCreditcode())){
                        next2Map.put("uscc",application2.getUnifiedSocialCreditcode());
                    }
                    //申请人地址
                    if(StringUtils.isNotBlank(application2.getApplicationAddressArea())){
                        next2Map.put("applicantRegionCode",application2.getApplicationAddressArea());
                    }else if(StringUtils.isNotBlank(application2.getApplicationAddressCity())){
                        next2Map.put("applicantRegionCode",application2.getApplicationAddressCity());
                    }else if(StringUtils.isNotBlank(application2.getApplicationAddressProvince())){
                        next2Map.put("applicantRegionCode",application2.getApplicationAddressProvince());
                    }
                    next2Map.put("applicantCnAddr",application2.getApplicationAddres());
                    if(StringUtils.isNotBlank(application2.getPostalCode())){
                        next2Map.put("applicantPostalCode",application2.getPostalCode());//申请人邮政编码
                    }else {
                        next2Map.put("applicantPostalCode","");//申请人邮政编码
                    }

                    //联系人名称
                    if(StringUtils.isNotBlank(application2.getContactName())){
                        next2Map.put("contactName",application2.getContactName());
                    }
                    //联系人电话
                    if(StringUtils.isNotBlank(application2.getContactPhone())){
                        next2Map.put("contactPhone",application2.getContactPhone());
                    }
                } else {
                    if ("1".equals(application2.getBookOwnerType())) {
                        String [] stringArray = {"1","990001"};
                        next2Map.put("regionCodeList",Arrays.asList(stringArray));
                        next2Map.put("nationType","1");
                        next2Map.put("regionCode","990001");
                        next2Map.put("regionName","中国台湾");
                    }else if ("2".equals(application2.getBookOwnerType())) {
                        String [] stringArray = {"1","2029"};
                        next2Map.put("regionCodeList",Arrays.asList(stringArray));
                        next2Map.put("nationType","1");
                        next2Map.put("regionCode","2029");
                        next2Map.put("regionName","中国香港");
                    }else if ("3".equals(application2.getBookOwnerType())) {
                        String [] stringArray = {"1","2039"};
                        next2Map.put("regionCodeList",Arrays.asList(stringArray));
                        next2Map.put("nationType","1");
                        next2Map.put("regionCode","2039");
                        next2Map.put("regionName","中国澳门");
                    }else if ("4".equals(application2.getBookOwnerType())) {
                        return Result.error("暂不支持做国外的商标");
                    }
                    next2Map.put("applicantCnName",application2.getApplicantName());
                    if(StringUtils.isNotBlank(application2.getApplicantOwnerEnglishname())){
                        next2Map.put("applicantEnName",application2.getApplicantOwnerEnglishname());
                    }else{
                        next2Map.put("applicantEnName","");
                    }
                    next2Map.put("idetCertType",application2.getIdName());
                    next2Map.put("idetCertName",application2.getIdetCertName());
                    next2Map.put("idetCertNo",application2.getIdNumber());

                    //上传身份证证明文件（中文）
                    File sba0025 = new File(rootPath + File.separator + application2.getSba0025());
                    String idetCertCnAttJson = fileUpload(sba0025, tmToasToken,false,application2,referer);
                    next2Map.put("idetCertCnAttJson",idetCertCnAttJson);

                    next2Map.put("certFileCnFlag",application2.getWhetherOriginaldocChinese());
                    if(application2.getWhetherOriginaldocChinese().equals("1")){
                        next2Map.put("idetCertEnAttJson","");
                    }else{
                        //上传身份证证明文件（外文）
                        File sba0026 = new File(rootPath + File.separator + application2.getSba0026());
                        String idetCertEnAttJson = fileUpload(sba0026, tmToasToken,false,application2,referer);
                        next2Map.put("idetCertEnAttJson",idetCertEnAttJson);
                    }
                    next2Map.put("objCertCnAttJson","");
                    next2Map.put("applicantCnAddr",application2.getApplicationAddres());
                    if(StringUtils.isNotBlank(application2.getApplicationAddressEnglish())){
                        next2Map.put("applicantEnAddr",application2.getApplicationAddressEnglish());
                    }else{
                        next2Map.put("applicantEnAddr","");
                    }
                    if(StringUtils.isNotBlank(application2.getPostalCode())){
                        next2Map.put("applicantPostalCode",application2.getPostalCode());//申请人邮政编码
                    }else {
                        next2Map.put("applicantPostalCode","");//申请人邮政编码
                    }
                    //联系人名称
                    if(StringUtils.isNotBlank(application2.getContactName())){
                        next2Map.put("contactName",application2.getContactName());
                    }
                    //联系人电话
                    if(StringUtils.isNotBlank(application2.getContactPhone())){
                        next2Map.put("contactPhone",application2.getContactPhone());
                    }
                }
            }
            if(StringUtils.isNotBlank(shunchaoTrademarkinfo.getSba0022())){
                File sba0022 = new File(rootPath + File.separator + shunchaoTrademarkinfo.getSba0022());
                String othAttJson = fileUpload(sba0022, tmToasToken,false,application2,referer);
                next2Map.put("othAttJson",othAttJson);
            }else{
                next2Map.put("othAttJson","");
            }

            String next2Body = JSON.toJSONString(next2Map);
            //申请人信息下一步
            nextJsoup("https://toas.sbj.cnipa.gov.cn/toas-extra-prod/extra-prod-api/tempsubsq/web/tmbg/appform/save",next2Body,tmToasToken,referer);

            Map<String, Object> next3Map = new HashMap<>();
            next3Map.put("appformId",appformId);
            next3Map.put("viewCode","toas_005020202");
            next3Map.put("btnGroup",null);
            next3Map.put("regNo",shunchaoTrademarkinfo.getRegistrationNumber());
            next3Map.put("tmType","P");
            next3Map.put("changeTypeList",Arrays.asList(shunchaoTrademarkinfo.getChangeType().split(",")));
            if ("0".equals(application2.getBookOwnerType())) {//中国大陆,变更联系地址
                if(shunchaoTrademarkinfo.getChangeType().contains("1")){
                    next3Map.put("bfchgApplicantCnName",application1.getApplicantName());
                }
                if(shunchaoTrademarkinfo.getChangeType().contains("2")){
                    next3Map.put("afchgApplicantAddr",application2.getApplicantName());
                    if(StringUtils.isNotBlank(application2.getPostalCode())){
                        next3Map.put("applicantPostalCode",application2.getPostalCode());//申请人邮政编码
                    }else {
                        next3Map.put("applicantPostalCode","");//申请人邮政编码
                    }
                }
                if(shunchaoTrademarkinfo.getChangeType().contains("3")){
                    //国内申请人联系地址
                    if(StringUtils.isNotBlank(application2.getDmstcApplicationAddressArea())){
                        next3Map.put("afchgApplicantContactRegionCode",application2.getDmstcApplicationAddressArea());
                    }else if(StringUtils.isNotBlank(application2.getDmstcApplicationAddressCity())){
                        next3Map.put("afchgApplicantContactRegionCode",application2.getDmstcApplicationAddressCity());
                    }else if(StringUtils.isNotBlank(application2.getDmstcApplicationAddressProvince())){
                        next3Map.put("afchgApplicantContactRegionCode",application2.getDmstcApplicationAddressProvince());
                    }else{
                        next3Map.put("afchgApplicantContactRegionCode","");
                    }
                    if(StringUtils.isNotBlank(application2.getDmstcApplicantAddr())){
                        next3Map.put("afchgApplicantContactAddr",application2.getDmstcApplicantAddr());
                    }else {
                        next3Map.put("afchgApplicantContactAddr","");
                    }
                    //国内申请人邮政编码
                    if(StringUtils.isNotBlank(application2.getRecipientPostcode())){
                        next3Map.put("afchgApplicantContactPostalCode",application2.getRecipientPostcode());
                    }else{
                        next3Map.put("afchgApplicantContactPostalCode","");
                    }
                }
            }else{//非中国大陆
                if(shunchaoTrademarkinfo.getChangeType().contains("1")){
                    next3Map.put("bfchgApplicantCnName",application1.getApplicantName());
                    if(StringUtils.isNotBlank(application2.getApplicantOwnerEnglishname())){
                        next3Map.put("bfchgApplicantEnName",application2.getApplicantOwnerEnglishname());
                    }else{
                        next3Map.put("bfchgApplicantEnName","");
                    }
                }
                if(shunchaoTrademarkinfo.getChangeType().contains("2")){
                    next3Map.put("afchgApplicantAddr",application2.getApplicationAddres());
                    if(StringUtils.isNotBlank(application2.getApplicationAddressEnglish())){
                        next3Map.put("afchgApplicantEnAddr",application2.getApplicationAddressEnglish());
                    }else{
                        next3Map.put("afchgApplicantEnAddr",null);
                    }
                    if(StringUtils.isNotBlank(application2.getPostalCode())){
                        next3Map.put("applicantPostalCode",application2.getPostalCode());//申请人邮政编码
                    }else {
                        next3Map.put("applicantPostalCode","");//申请人邮政编码
                    }
                }
                if(shunchaoTrademarkinfo.getChangeType().contains("4")){//变更文件接收人
                    //内地（大陆）文件接收人名称
                    next3Map.put("afchgReceiverName",application2.getApplicationMainlandRecipientname());
                    //内地（大陆）文件接收人名称
                    if(StringUtils.isNotBlank(application2.getDmstcApplicationAddressArea())){
                        next3Map.put("afchgReceiverRegionCode",application2.getDmstcApplicationAddressArea());
                    }else if(StringUtils.isNotBlank(application2.getDmstcApplicationAddressCity())){
                        next3Map.put("afchgReceiverRegionCode",application2.getDmstcApplicationAddressCity());
                    }else if(StringUtils.isNotBlank(application2.getDmstcApplicationAddressProvince())){
                        next3Map.put("afchgReceiverRegionCode",application2.getDmstcApplicationAddressProvince());
                    }
                    next3Map.put("afchgReceiverAddr",application2.getMainlandReceiverAddr());
                    //接收人邮政编码
                    if(StringUtils.isNotBlank(application2.getRecipientPostcode())){
                        next3Map.put("afchgReceiverPostalCode",application2.getRecipientPostcode());
                    }
                }
            }
            if(application1.getChangeFileType() == 1){
                next3Map.put("bgCertFileCnFlag","1");
            }else{
                next3Map.put("bgCertFileCnFlag","0");
            }
            File sba0036 = new File(rootPath + File.separator + application1.getSba0036());
            String changeProofCnAttJson = fileUpload(sba0036, tmToasToken,false,null,referer);
            next3Map.put("changeProofCnAttJson",changeProofCnAttJson);

            if(application1.getChangeFileType() == 2){
                File sba0037 = new File(rootPath + File.separator + application1.getSba0037());
                String changeProofEnAttJson = fileUpload(sba0037, tmToasToken,false,null,referer);
                next3Map.put("changeProofEnAttJson",changeProofEnAttJson);
            }
            next3Map.put("changeMembersAttJson","");
            next3Map.put("gtmRuleAttJson","");
            next3Map.put("ztmRuleAttJson","");
            next3Map.put("shareTmFlag","0");

            String next3Body = JSON.toJSONString(next3Map);
            //商标图样，最后一步
            nextJsoup("https://toas.sbj.cnipa.gov.cn/toas-extra-prod/extra-prod-api/tempsubsq/web/tmbg/appform/save",next3Body,tmToasToken,referer);


        } catch (Exception e){
            log.info("获取失败",e);
            return Result.error("获取失败");
        }
        return Result.ok(appformId);
    }

    /**
     * 功能描述:商标转让转移提交商标局
     * 场景:
     * @Param: [trademarkAnnexList, request]
     * @Return: java.lang.String
     * @Author: zcs
     * @Date: 2023/7/04
     */
    @GetMapping(value = "/startZYTmsve")
    public Result<?> startZYTmsve(String trademarkId, HttpServletRequest request, HttpServletResponse response) throws InterruptedException, IOException {
        String appformId = "";
        try{
            String token = request.getParameter("token");
            String rootPath = System.getProperty("exe.path");
            String referer ="hhttps://toas.sbj.cnipa.gov.cn/toas-extra-prod/toasui-extra-prod/toas-ui/onlineApplication/followUp/trademarkTransfer?code=toas_0050222";
//            rootPath ="D:\\DUOUEXE\\duou\\";
            //加载附件数据到本地
            shunchaoTrademarkTmsveService.connectTmsveDownloadData(trademarkId, token);
            //加载基本信息
            ShunchaoTrademarkTmsve shunchaoTrademarkinfo = shunchaoTrademarkTmsveService.getSendTsvmeData(trademarkId, token);

            String tmToasToken = shunchaoTrademarkinfo.getTmToasToken();
//            String tmToasToken = "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJsb2dpblR5cGUiOiJsb2dpbiIsImxvZ2luSWQiOiI2N2FkNmEyOWU0YjA0YzIxYjJjMmRmZDUiLCJyblN0ciI6IjkwNmVEdENQYkcyN2hCSExjNG5EUXNZMkdnOGlpbnlrIiwidXNlcklkIjoiNjdhZDZhMjllNGIwNGMyMWIyYzJkZmQ1Iiwicm9sZVR5cGUiOjcwfQ.YOefeAa4AIuZua8JQw4JSyjtMTeaPy7yTnpMqDeZlJk";

            Map<String, Object> next1Map = new HashMap<>();
            next1Map.put("agentName",shunchaoTrademarkinfo.getAgentName());
            next1Map.put("agentInstName",shunchaoTrademarkinfo.getEnterpriseName());

            File sba0034 = new File(rootPath + File.separator + shunchaoTrademarkinfo.getSba0034());
            String assignorAgentAttJson = fileUpload(sba0034, tmToasToken,false,null,referer);
            next1Map.put("assignorAgentAttJson",assignorAgentAttJson);

            File sba0035 = new File(rootPath + File.separator + shunchaoTrademarkinfo.getSba0035());
            String assigneeAgentAttJson = fileUpload(sba0035, tmToasToken,false,null,referer);
            next1Map.put("assigneeAgentAttJson",assigneeAgentAttJson);

            next1Map.put("assignorCertType",null);
            if(StringUtils.isNotBlank(shunchaoTrademarkinfo.getAppFormId())){
                next1Map.put("appformId",shunchaoTrademarkinfo.getAppFormId());
            }else{
                next1Map.put("appformId","");
            }
            next1Map.put("viewCode","toas_005022203");
            String next1Body = JSON.toJSONString(next1Map);

            String url = "https://toas.sbj.cnipa.gov.cn/toas-extra-prod/extra-prod-api/tempsubsq/web/tmzr/appform/save";
            //代理信息暂存
            Connection.Response next1Response = nextJsoup(url,next1Body,tmToasToken,referer);

            String next1Text = next1Response.parse().body().text();
            log.info("---------------next1Text:"+next1Text);
            Map<String, String> next1TextMap = JSON.parseObject(next1Text, new TypeReference<Map<String, String>>() {});
            String next1Data = next1TextMap.get("data");
            Map<String, String> next1DataMap = JSON.parseObject(next1Data, new TypeReference<Map<String, String>>() {});
            appformId = next1DataMap.get("appformId");//流水号

            //转让人信息+附件
            ShunchaoTrademarkApplicant application1 = shunchaoTrademarkinfo.getApplicant1();//申请人
            Map<String, Object> next2Map = new HashMap<>();
            next2Map.put("1",null);
            next2Map.put("appformId",appformId);
            next2Map.put("viewCode","toas_005022202");
            next2Map.put("regNo",shunchaoTrademarkinfo.getRegistrationNumber());
            next2Map.put("tmType","P");//普通商标
            next2Map.put("processBizType",shunchaoTrademarkinfo.getTransferType());//普通商标
            next2Map.put("colrCombFlag","0");
            if ("0".equals(application1.getApplicantType())) {//法人或其他组织
                next2Map.put("agentAssignorType","2");
                if ("0".equals(application1.getBookOwnerType())) {//中国大陆
                    String [] stringArray = {"1","1000"};
                    next2Map.put("assignorRegionCodeList",Arrays.asList(stringArray));
                    next2Map.put("assignorNationType","1");
                    next2Map.put("assignorRegionCode","1000");
                    next2Map.put("assignorRegionName","内地（大陆）申请人");

                    next2Map.put("assignorCnName",application1.getApplicantName());
                    if(StringUtils.isNotBlank(application1.getIdName())){
                        next2Map.put("assignorCertType",application1.getIdName());
                        next2Map.put("assignorCertName",application1.getIdetCertName());
                    }

                    if(StringUtils.isNotBlank(application1.getIdNumber())){
                        next2Map.put("assignorCertNo",application1.getIdNumber());
                    }

                    //上传身份证证明文件（中文）
                    if(StringUtils.isNotBlank(application1.getSba0025())){
                        File sba0025 = new File(rootPath + File.separator + application1.getSba0025());
                        String assignorCertCnAttJson = fileUpload(sba0025, tmToasToken,true,application1,referer);
                        next2Map.put("assignorCertCnAttJson",assignorCertCnAttJson);
                    }
                    next2Map.put("assignorCertEnAttJson","");
                    next2Map.put("assignorObjCertEnAttJson","");

                    //转让人地址
                    if(StringUtils.isNotBlank(application1.getApplicationAddressArea())){
                        next2Map.put("assignorAddrRegionCode",application1.getApplicationAddressArea());
                    }else if(StringUtils.isNotBlank(application1.getApplicationAddressCity())){
                        next2Map.put("assignorAddrRegionCode",application1.getApplicationAddressCity());
                    }else if(StringUtils.isNotBlank(application1.getApplicationAddressProvince())){
                        next2Map.put("assignorAddrRegionCode",application1.getApplicationAddressProvince());
                    }
                    next2Map.put("assignorCnAddr",application1.getApplicationAddres());
                    next2Map.put("assignorPostalCode",application1.getPostalCode());//申请人邮政编码

                    next2Map.put("assignorJointApplyFlag","0");//是否共有商标
                } else {
                    if ("1".equals(application1.getBookOwnerType())) {
                        String [] stringArray = {"1","990001"};
                        next2Map.put("assignorRegionCodeList",Arrays.asList(stringArray));
                        next2Map.put("assignorNationType","1");
                        next2Map.put("assignorRegionCode","990001");
                        next2Map.put("assignorRegionName","中国台湾");
                    }else if ("2".equals(application1.getBookOwnerType())) {
                        String [] stringArray = {"1","2029"};
                        next2Map.put("assignorRegionCodeList",Arrays.asList(stringArray));
                        next2Map.put("assignorNationType","1");
                        next2Map.put("assignorRegionCode","2029");
                        next2Map.put("assignorRegionName","中国香港");
                    }else if ("3".equals(application1.getBookOwnerType())) {
                        String [] stringArray = {"1","2039"};
                        next2Map.put("assignorRegionCodeList",Arrays.asList(stringArray));
                        next2Map.put("assignorNationType","1");
                        next2Map.put("assignorRegionCode","2039");
                        next2Map.put("assignorRegionName","中国澳门");
                    }else if ("4".equals(application1.getBookOwnerType())) {
                        return Result.error("暂不支持做国外的商标");
                    }
                    next2Map.put("assignorCnName",application1.getApplicantName());
                    if(StringUtils.isNotBlank(application1.getApplicantOwnerEnglishname())){
                        next2Map.put("assignorEnName",application1.getApplicantOwnerEnglishname());
                    }else{
                        next2Map.put("assignorEnName",null);
                    }

                    if(StringUtils.isNotBlank(application1.getIdName())){
                        next2Map.put("assignorCertType",application1.getIdName());
                        next2Map.put("assignorCertName",application1.getIdetCertName());
                    }

                    if(StringUtils.isNotBlank(application1.getIdNumber())){
                        next2Map.put("assignorCertNo",application1.getIdNumber());
                    }

                    //上传身份证证明文件（中文）
                    if(StringUtils.isNotBlank(application1.getSba0025())){
                        File sba0025 = new File(rootPath + File.separator + application1.getSba0025());
                        String assignorCertCnAttJson = fileUpload(sba0025, tmToasToken,true,application1,referer);
                        next2Map.put("assignorCertCnAttJson",assignorCertCnAttJson);
                    }

                    next2Map.put("assignorCertFileCnFlag",application1.getWhetherOriginaldocChinese());
                    if(application1.getWhetherOriginaldocChinese().equals("1")){
                        next2Map.put("assignorCertEnAttJson","");
                    }else{
                        //上传身份证证明文件（外文）
                        File sba0026 = new File(rootPath + File.separator + application1.getSba0026());
                        String assignorCertEnAttJson = fileUpload(sba0026, tmToasToken,false,application1,referer);
                        next2Map.put("assignorCertEnAttJson",assignorCertEnAttJson);
                    }
                    next2Map.put("assignorObjCertEnAttJson","");
                    next2Map.put("assignorCnAddr",application1.getApplicationAddres());
                    if(StringUtils.isNotBlank(application1.getApplicationAddressEnglish())){
                        next2Map.put("assignorEnAddr",application1.getApplicationAddressEnglish());
                    }else{
                        next2Map.put("assignorEnAddr","");
                    }
                    next2Map.put("assignorPostalCode",application1.getPostalCode());//申请人邮政编码

                    next2Map.put("assignorJointApplyFlag","0");//是否共有商标
                }
            } else if ("1".equals(application1.getApplicantType())) {//自然人
                next2Map.put("agentAssignorType","1");
                if ("0".equals(application1.getBookOwnerType())) {//中国大陆
                    String [] stringArray = {"1","1000"};
                    next2Map.put("assignorRegionCodeList",Arrays.asList(stringArray));
                    next2Map.put("assignorNationType","1");
                    next2Map.put("assignorRegionCode","1000");
                    next2Map.put("assignorRegionName","内地（大陆）申请人");

                    next2Map.put("assignorCnName",application1.getApplicantName());
                    if(StringUtils.isNotBlank(application1.getIdName())){
                        next2Map.put("assignorCertType",application1.getIdName());
                        next2Map.put("assignorCertName",application1.getIdetCertName());
                    }

                    if(StringUtils.isNotBlank(application1.getIdNumber())){
                        next2Map.put("assignorCertNo",application1.getIdNumber());
                    }

                    //上传身份证证明文件（中文）
                    if(StringUtils.isNotBlank(application1.getSba0025())){
                        File sba0025 = new File(rootPath + File.separator + application1.getSba0025());
                        String assignorCertCnAttJson = fileUpload(sba0025, tmToasToken,true,application1,referer);
                        next2Map.put("assignorCertCnAttJson",assignorCertCnAttJson);
                    }
                    next2Map.put("assignorCertEnAttJson","");

                    //上传主体资格证明文件
                    if(StringUtils.isNotBlank(application1.getObjCertType())){
                        next2Map.put("assignorObjCertType",application1.getObjCertType());
                    }else{
                        next2Map.put("assignorObjCertType","");
                    }
                    if(StringUtils.isNotBlank(application1.getSba0027())){
                        File sba0027 = new File(rootPath + File.separator + application1.getSba0027());
                        String assignorObjCertEnAttJson = fileUpload(sba0027, tmToasToken,false,application1,referer);
                        next2Map.put("assignorObjCertEnAttJson",assignorObjCertEnAttJson);
                    }
                    if("1".equals(application1.getObjCertType())){
                        next2Map.put("assignorUscc",application1.getUnifiedSocialCreditcode());
                    }
                    //转让人地址
                    if(StringUtils.isNotBlank(application1.getApplicationAddressArea())){
                        next2Map.put("assignorAddrRegionCode",application1.getApplicationAddressArea());
                    }else if(StringUtils.isNotBlank(application1.getApplicationAddressCity())){
                        next2Map.put("assignorAddrRegionCode",application1.getApplicationAddressCity());
                    }else if(StringUtils.isNotBlank(application1.getApplicationAddressProvince())){
                        next2Map.put("assignorAddrRegionCode",application1.getApplicationAddressProvince());
                    }
                    next2Map.put("assignorCnAddr",application1.getApplicationAddres());
                    next2Map.put("assignorPostalCode",application1.getPostalCode());//申请人邮政编码

                } else {
                    if ("1".equals(application1.getBookOwnerType())) {
                        String [] stringArray = {"1","990001"};
                        next2Map.put("assignorRegionCodeList",Arrays.asList(stringArray));
                        next2Map.put("assignorNationType","1");
                        next2Map.put("assignorRegionCode","990001");
                        next2Map.put("assignorRegionName","中国台湾");
                    }else if ("2".equals(application1.getBookOwnerType())) {
                        String [] stringArray = {"1","2029"};
                        next2Map.put("assignorRegionCodeList",Arrays.asList(stringArray));
                        next2Map.put("assignorNationType","1");
                        next2Map.put("assignorRegionCode","2029");
                        next2Map.put("assignorRegionName","中国香港");
                    }else if ("3".equals(application1.getBookOwnerType())) {
                        String [] stringArray = {"1","2039"};
                        next2Map.put("assignorRegionCodeList",Arrays.asList(stringArray));
                        next2Map.put("assignorNationType","1");
                        next2Map.put("assignorRegionCode","2039");
                        next2Map.put("assignorRegionName","中国澳门");
                    }else if ("4".equals(application1.getBookOwnerType())) {
                        return Result.error("暂不支持做国外的商标");
                    }
                    next2Map.put("assignorCnName",application1.getApplicantName());
                    if(StringUtils.isNotBlank(application1.getApplicantOwnerEnglishname())){
                        next2Map.put("assignorEnName",application1.getApplicantOwnerEnglishname());
                    }else{
                        next2Map.put("assignorEnName",null);
                    }
                    if(StringUtils.isNotBlank(application1.getIdName())){
                        next2Map.put("assignorCertType",application1.getIdName());
                        next2Map.put("assignorCertName",application1.getIdetCertName());
                    }

                    if(StringUtils.isNotBlank(application1.getIdNumber())){
                        next2Map.put("assignorCertNo",application1.getIdNumber());
                    }

                    //上传身份证证明文件（中文）
                    if(StringUtils.isNotBlank(application1.getSba0025())){
                        File sba0025 = new File(rootPath + File.separator + application1.getSba0025());
                        String assignorCertCnAttJson = fileUpload(sba0025, tmToasToken,true,application1,referer);
                        next2Map.put("assignorCertCnAttJson",assignorCertCnAttJson);
                    }

                    next2Map.put("assignorCertFileCnFlag",application1.getWhetherOriginaldocChinese());
                    if(application1.getWhetherOriginaldocChinese().equals("1")){
                        next2Map.put("assignorCertEnAttJson","");
                    }else{
                        //上传身份证证明文件（外文）
                        File sba0026 = new File(rootPath + File.separator + application1.getSba0026());
                        String assignorCertEnAttJson = fileUpload(sba0026, tmToasToken,false,application1,referer);
                        next2Map.put("assignorCertEnAttJson",assignorCertEnAttJson);
                    }
                    next2Map.put("assignorObjCertEnAttJson","");
                    next2Map.put("assignorCnAddr",application1.getApplicationAddres());
                    if(StringUtils.isNotBlank(application1.getApplicationAddressEnglish())){
                        next2Map.put("assignorEnAddr",application1.getApplicationAddressEnglish());
                    }else{
                        next2Map.put("assignorEnAddr","");
                    }
                    next2Map.put("assignorPostalCode",application1.getPostalCode());//申请人邮政编码

                    next2Map.put("assignorJointApplyFlag","0");//是否共有商标
                }
            }

            if(shunchaoTrademarkinfo.getTransferType().equals("1")){
                //同意转让证明
                File sba0032 = new File(rootPath + File.separator + application1.getSba0032());
                String tmTraCertAttJson = fileUpload(sba0032, tmToasToken,true,application1,referer);
                next2Map.put("tmTraCertAttJson",tmTraCertAttJson);
                next2Map.put("cancelCertAttJson","");

                //联系人名称
                if(StringUtils.isNotBlank(application1.getContactName())){
                    next2Map.put("assignorName",application1.getContactName());
                }
                //联系人电话
                if(StringUtils.isNotBlank(application1.getContactPhone())){
                    next2Map.put("assignorPhone",application1.getContactPhone());
                }
            }else{
                //商标移转的有关法律文件
                File sba0038 = new File(rootPath + File.separator + application1.getSba0038());
                String cancelCertAttJson = fileUpload(sba0038, tmToasToken,true,application1,referer);
                next2Map.put("cancelCertAttJson",cancelCertAttJson);
                next2Map.put("tmTraCertAttJson","");
            }
            next2Map.put("assignorPoaAttJson","");
            next2Map.put("assigneePoaAttJson","");

            //有关说明文件
            if(StringUtils.isNotBlank(shunchaoTrademarkinfo.getSba0022())){
                File sba0022 = new File(rootPath + File.separator + shunchaoTrademarkinfo.getSba0022());
                String othAttJson = fileUpload(sba0022, tmToasToken,false,application1,referer);
                next2Map.put("othAttJson",othAttJson);
            }else{
                next2Map.put("othAttJson","");
            }

            String next2Body = JSON.toJSONString(next2Map);
            //转让人信息下一步
            nextJsoup("https://toas.sbj.cnipa.gov.cn/toas-extra-prod/extra-prod-api/tempsubsq/web/tmzr/appform/save",next2Body,tmToasToken,referer);

//            受让人信息+附件
            ShunchaoTrademarkApplicant application2 = shunchaoTrademarkinfo.getApplicant2();//申请人
            Map<String, Object> next3Map = new HashMap<>();
            next3Map.put("appformId",appformId);
            next3Map.put("viewCode","toas_0050108");
            next3Map.put("voiceFlag","0");
            next3Map.put("threeDimFlag","0");
            next3Map.put("colrCombFlag","0");
            if ("0".equals(application2.getApplicantType())) {//法人或其他组织
                next3Map.put("agentAssigneeType","2");
                if ("0".equals(application2.getBookOwnerType())) {//中国大陆
                    String [] stringArray = {"1","1000"};
                    next3Map.put("regionCodeList",Arrays.asList(stringArray));
                    next3Map.put("assigneeNationType","1");
                    next3Map.put("assigneeRegionCode","1000");
                    next3Map.put("assigneeRegionName","内地（大陆）申请人");

                    next3Map.put("assigneeCnName",application2.getApplicantName());
                    next3Map.put("assigneeCertType",application2.getIdName());
                    next3Map.put("assigneeCertName",application2.getIdetCertName());
                    next3Map.put("assigneeCertNo",application2.getIdNumber());

                    //上传身份证证明文件
                    File sba0025 = new File(rootPath + File.separator + application2.getSba0025());
                    String assigneeCertCnAttJson = fileUpload(sba0025, tmToasToken,true,application2,referer);
                    next3Map.put("assigneeCertCnAttJson",assigneeCertCnAttJson);
                    next3Map.put("assigneeCertEnAttJson","");
                    next3Map.put("assigneeObjCertCnAttJson","");

                    //受让人地址
                    if(StringUtils.isNotBlank(application2.getApplicationAddressArea())){
                        next3Map.put("assigneeAddrRegionCode",application2.getApplicationAddressArea());
                    }else if(StringUtils.isNotBlank(application2.getApplicationAddressCity())){
                        next3Map.put("assigneeAddrRegionCode",application2.getApplicationAddressCity());
                    }else if(StringUtils.isNotBlank(application2.getApplicationAddressProvince())){
                        next3Map.put("assigneeAddrRegionCode",application2.getApplicationAddressProvince());
                    }
                    next3Map.put("assigneeCnAddr",application2.getApplicationAddres());
                    next3Map.put("assigneePostalCode",application2.getPostalCode());//受让人邮政编码

                    //国内受让人联系地址
                    if(StringUtils.isNotBlank(application2.getDmstcApplicationAddressArea())){
                        next3Map.put("dmstcAssigneeRegionCode",application2.getDmstcApplicationAddressArea());
                    }else if(StringUtils.isNotBlank(application2.getDmstcApplicationAddressCity())){
                        next3Map.put("dmstcAssigneeRegionCode",application2.getDmstcApplicationAddressCity());
                    }else if(StringUtils.isNotBlank(application2.getDmstcApplicationAddressProvince())){
                        next3Map.put("dmstcAssigneeRegionCode",application2.getDmstcApplicationAddressProvince());
                    }else{
                        next3Map.put("dmstcAssigneeRegionCode",null);
                    }
                    if(StringUtils.isNotBlank(application2.getDmstcApplicantAddr())){
                        next3Map.put("dmstcAssigneeAddr",application2.getDmstcApplicantAddr());
                    }else{
                        next3Map.put("dmstcAssigneeAddr",null);
                    }
                    //国内受让人邮政编码
                    if(StringUtils.isNotBlank(application2.getRecipientPostcode())){
                        next3Map.put("dmstcAssigneePostalCode",application2.getRecipientPostcode());
                    }else{
                        next3Map.put("dmstcAssigneePostalCode",null);
                    }

                    next3Map.put("teamTmUseruleAttJson","");
                    next3Map.put("teamMemberAttJson","");
                    next3Map.put("certTmUseruleAttJson","");
                    next3Map.put("applyCheckAgentAttJson","");
                    next3Map.put("commQuaCertAttJson","");
                    next3Map.put("commChkInvyAttJson","");
                    next3Map.put("commTechListAttJson","");

                    //联系人名称
                    if(StringUtils.isNotBlank(application2.getContactName())){
                        next3Map.put("assigneeContactName",application2.getContactName());
                    }else{
                        next3Map.put("assigneeContactName","");
                    }
                    //联系人电话
                    if(StringUtils.isNotBlank(application2.getContactPhone())){
                        next3Map.put("assigneeContactPhone",application2.getContactPhone());
                    }else{
                        next3Map.put("assigneeContactPhone","");
                    }
                    next3Map.put("assignorCertType",null);
                    next3Map.put("assigneeJointApplyFlag",0);
                } else {
                    if ("1".equals(application2.getBookOwnerType())) {
                        String [] stringArray = {"1","990001"};
                        next3Map.put("regionCodeList",Arrays.asList(stringArray));
                        next3Map.put("assigneeNationType","1");
                        next3Map.put("assigneeRegionCode","990001");
                        next3Map.put("assigneeRegionName","中国台湾");
                    }else if ("2".equals(application2.getBookOwnerType())) {
                        String [] stringArray = {"1","2029"};
                        next3Map.put("regionCodeList",Arrays.asList(stringArray));
                        next3Map.put("assigneeNationType","1");
                        next3Map.put("assigneeRegionCode","2029");
                        next3Map.put("assigneeRegionName","中国香港");
                    }else if ("3".equals(application2.getBookOwnerType())) {
                        String [] stringArray = {"1","2039"};
                        next3Map.put("regionCodeList",Arrays.asList(stringArray));
                        next3Map.put("assigneeNationType","1");
                        next3Map.put("assigneeRegionCode","2039");
                        next3Map.put("assigneeRegionName","中国澳门");
                    }else if ("4".equals(application2.getBookOwnerType())) {
                        return Result.error("暂不支持做国外的商标");
                    }
                    next3Map.put("applicantCnName",application2.getApplicantName());
                    if(StringUtils.isNotBlank(application2.getApplicantOwnerEnglishname())){
                        next3Map.put("applicantEnName",application2.getApplicantOwnerEnglishname());
                    }else{
                        next3Map.put("applicantEnName","");
                    }
                    next3Map.put("idetCertType",application2.getIdName());
                    next3Map.put("idetCertName",application2.getIdetCertName());
                    next3Map.put("idetCertNo",application2.getIdNumber());

                    //上传身份证证明文件（中文）
                    File sba0025 = new File(rootPath + File.separator + application2.getSba0025());
                    String idetCertCnAttJson = fileUpload(sba0025, tmToasToken,false,application2,referer);
                    next3Map.put("idetCertCnAttJson",idetCertCnAttJson);

                    next3Map.put("certFileCnFlag",application2.getWhetherOriginaldocChinese());
                    if(application2.getWhetherOriginaldocChinese().equals("1")){
                        next3Map.put("idetCertEnAttJson","");
                    }else{
                        //上传身份证证明文件（外文）
                        File sba0026 = new File(rootPath + File.separator + application2.getSba0026());
                        String idetCertEnAttJson = fileUpload(sba0026, tmToasToken,false,application2,referer);
                        next3Map.put("idetCertEnAttJson",idetCertEnAttJson);
                    }
                    next3Map.put("objCertCnAttJson","");
                    next3Map.put("applicantCnAddr",application2.getApplicationAddres());
                    if(StringUtils.isNotBlank(application2.getApplicationAddressEnglish())){
                        next3Map.put("applicantEnAddr",application2.getApplicationAddressEnglish());
                    }else{
                        next3Map.put("applicantEnAddr","");
                    }

                    //内地（大陆）文件接收人名称
                    next3Map.put("mainlandReceiverName",application2.getApplicationMainlandRecipientname());
                    //内地（大陆）文件接收人名称
                    if(StringUtils.isNotBlank(application2.getDmstcApplicationAddressArea())){
                        next3Map.put("mainlandReceiverRegionCode",application2.getDmstcApplicationAddressArea());
                    }else if(StringUtils.isNotBlank(application2.getDmstcApplicationAddressCity())){
                        next3Map.put("mainlandReceiverRegionCode",application2.getDmstcApplicationAddressCity());
                    }else if(StringUtils.isNotBlank(application2.getDmstcApplicationAddressProvince())){
                        next3Map.put("mainlandReceiverRegionCode",application2.getDmstcApplicationAddressProvince());
                    }
                    next3Map.put("mainlandReceiverAddr",application2.getMainlandReceiverAddr());
                    //接收人邮政编码
                    if(StringUtils.isNotBlank(application2.getRecipientPostcode())){
                        next3Map.put("mainlandReceiverPostalCode",application2.getRecipientPostcode());
                    }
                    next3Map.put("assignorCertType",null);
                    next3Map.put("assigneeJointApplyFlag",0);
                }
            } else if ("1".equals(application2.getApplicantType())) {//自然人
                next3Map.put("agentAssigneeType","1");
                if ("0".equals(application2.getBookOwnerType())) {//中国大陆
                    String [] stringArray = {"1","1000"};
                    next3Map.put("regionCodeList",Arrays.asList(stringArray));
                    next3Map.put("assigneeNationType","1");
                    next3Map.put("assigneeRegionCode","1000");
                    next3Map.put("assigneeRegionName","内地（大陆）申请人");

                    next3Map.put("assigneeCnName",application2.getApplicantName());
                    next3Map.put("assigneeCertType",application2.getIdName());
                    next3Map.put("assigneeCertName",application2.getIdetCertName());
                    next3Map.put("assigneeCertNo",application2.getIdNumber());

                    //上传身份证证明文件
                    File sba0025 = new File(rootPath + File.separator + application2.getSba0025());
                    String assigneeCertCnAttJson = fileUpload(sba0025, tmToasToken,true,application2,referer);
                    next3Map.put("assigneeCertCnAttJson",assigneeCertCnAttJson);
                    next3Map.put("idetCertEnAttJson","");

                    //上传主体资格证明文件
                    next3Map.put("assigneeObjCertType",application2.getObjCertType());
                    File sba0027 = new File(rootPath + File.separator + application2.getSba0027());
                    String assigneeObjCertCnAttJson = fileUpload(sba0027, tmToasToken,false,application2,referer);
                    next3Map.put("assigneeObjCertCnAttJson",assigneeObjCertCnAttJson);
                    if(application2.getObjCertType().equals("1")){
                        next3Map.put("assigneeUscc",application2.getUnifiedSocialCreditcode());
                    }
                    //受让人地址
                    if(StringUtils.isNotBlank(application2.getApplicationAddressArea())){
                        next3Map.put("assigneeAddrRegionCode",application2.getApplicationAddressArea());
                    }else if(StringUtils.isNotBlank(application2.getApplicationAddressCity())){
                        next3Map.put("assigneeAddrRegionCode",application2.getApplicationAddressCity());
                    }else if(StringUtils.isNotBlank(application2.getApplicationAddressProvince())){
                        next3Map.put("assigneeAddrRegionCode",application2.getApplicationAddressProvince());
                    }
                    next3Map.put("assigneeCnAddr",application2.getApplicationAddres());
                    next3Map.put("assigneePostalCode",application2.getPostalCode());//受让人邮政编码

                    //国内受让人联系地址
                    if(StringUtils.isNotBlank(application2.getDmstcApplicationAddressArea())){
                        next3Map.put("dmstcAssigneeRegionCode",application2.getDmstcApplicationAddressArea());
                    }else if(StringUtils.isNotBlank(application2.getDmstcApplicationAddressCity())){
                        next3Map.put("dmstcAssigneeRegionCode",application2.getDmstcApplicationAddressCity());
                    }else if(StringUtils.isNotBlank(application2.getDmstcApplicationAddressProvince())){
                        next3Map.put("dmstcAssigneeRegionCode",application2.getDmstcApplicationAddressProvince());
                    }else{
                        next3Map.put("dmstcAssigneeRegionCode",null);
                    }
                    if(StringUtils.isNotBlank(application2.getDmstcApplicantAddr())){
                        next3Map.put("dmstcAssigneeAddr",application2.getDmstcApplicantAddr());
                    }else{
                        next3Map.put("dmstcAssigneeAddr",null);
                    }
                    //国内申请人邮政编码
                    if(StringUtils.isNotBlank(application2.getRecipientPostcode())){
                        next3Map.put("dmstcAssigneePostalCode",application2.getRecipientPostcode());
                    }else{
                        next3Map.put("dmstcAssigneePostalCode",null);
                    }

                    next3Map.put("teamTmUseruleAttJson","");
                    next3Map.put("teamMemberAttJson","");
                    next3Map.put("certTmUseruleAttJson","");
                    next3Map.put("applyCheckAgentAttJson","");
                    next3Map.put("commQuaCertAttJson","");
                    next3Map.put("commChkInvyAttJson","");
                    next3Map.put("commTechListAttJson","");

                    //联系人名称
                    if(StringUtils.isNotBlank(application2.getContactName())){
                        next3Map.put("assigneeContactName",application2.getContactName());
                    }else{
                        next3Map.put("assigneeContactName","");
                    }
                    //联系人电话
                    if(StringUtils.isNotBlank(application2.getContactPhone())){
                        next3Map.put("assigneeContactPhone",application2.getContactPhone());
                    }else{
                        next3Map.put("assigneeContactPhone","");
                    }
                    next3Map.put("assignorCertType",null);
                    next3Map.put("assigneeJointApplyFlag",0);
                } else {
                    if ("1".equals(application2.getBookOwnerType())) {
                        String [] stringArray = {"1","990001"};
                        next3Map.put("regionCodeList",Arrays.asList(stringArray));
                        next3Map.put("assigneeNationType","1");
                        next3Map.put("assigneeRegionCode","990001");
                        next3Map.put("assigneeRegionName","中国台湾");
                    }else if ("2".equals(application2.getBookOwnerType())) {
                        String [] stringArray = {"1","2029"};
                        next3Map.put("regionCodeList",Arrays.asList(stringArray));
                        next3Map.put("assigneeNationType","1");
                        next3Map.put("assigneeRegionCode","2029");
                        next3Map.put("assigneeRegionName","中国香港");
                    }else if ("3".equals(application2.getBookOwnerType())) {
                        String [] stringArray = {"1","2039"};
                        next3Map.put("regionCodeList",Arrays.asList(stringArray));
                        next3Map.put("assigneeNationType","1");
                        next3Map.put("assigneeRegionCode","2039");
                        next3Map.put("assigneeRegionName","中国澳门");
                    }else if ("4".equals(application2.getBookOwnerType())) {
                        return Result.error("暂不支持做国外的商标");
                    }
                    next3Map.put("assigneeCnName",application2.getApplicantName());
                    if(StringUtils.isNotBlank(application2.getApplicantOwnerEnglishname())){
                        next3Map.put("assigneeEnName",application2.getApplicantOwnerEnglishname());
                    }else{
                        next3Map.put("assigneeEnName","");
                    }
                    next3Map.put("assigneeCertType",application2.getIdName());
                    next3Map.put("assigneeCertName",application2.getIdetCertName());
                    next3Map.put("assigneeCertNo",application2.getIdNumber());

                    //上传身份证证明文件（中文）
                    File sba0025 = new File(rootPath + File.separator + application2.getSba0025());
                    String assigneeCertCnAttJson = fileUpload(sba0025, tmToasToken,true,application2,referer);
                    next3Map.put("assigneeCertCnAttJson",assigneeCertCnAttJson);

                    next3Map.put("assigneeCertFileCnFlag",application2.getWhetherOriginaldocChinese());
                    if(application2.getWhetherOriginaldocChinese().equals("1")){
                        next3Map.put("assigneeCertEnAttJson","");
                    }else{
                        //上传身份证证明文件（外文）
                        File sba0026 = new File(rootPath + File.separator + application2.getSba0026());
                        String assigneeCertEnAttJson = fileUpload(sba0026, tmToasToken,false,application2,referer);
                        next3Map.put("assigneeCertEnAttJson",assigneeCertEnAttJson);
                    }
                    next3Map.put("assigneeObjCertCnAttJson","");
                    next3Map.put("assigneeCnAddr",application2.getApplicationAddres());
                    if(StringUtils.isNotBlank(application2.getApplicationAddressEnglish())){
                        next3Map.put("assigneeEnAddr",application2.getApplicationAddressEnglish());
                    }else{
                        next3Map.put("assigneeEnAddr","");
                    }

                    //内地（大陆）文件接收人名称
                    next3Map.put("mainlandReceiverName",application2.getApplicationMainlandRecipientname());
                    //内地（大陆）文件接收人名称
                    if(StringUtils.isNotBlank(application2.getDmstcApplicationAddressArea())){
                        next3Map.put("mainlandReceiverRegionCode",application2.getDmstcApplicationAddressArea());
                    }else if(StringUtils.isNotBlank(application2.getDmstcApplicationAddressCity())){
                        next3Map.put("mainlandReceiverRegionCode",application2.getDmstcApplicationAddressCity());
                    }else if(StringUtils.isNotBlank(application2.getDmstcApplicationAddressProvince())){
                        next3Map.put("mainlandReceiverRegionCode",application2.getDmstcApplicationAddressProvince());
                    }
                    next3Map.put("mainlandReceiverAddr",application2.getMainlandReceiverAddr());
                    //接收人邮政编码
                    if(StringUtils.isNotBlank(application2.getRecipientPostcode())){
                        next3Map.put("mainlandReceiverPostalCode",application2.getRecipientPostcode());
                    }
                    next3Map.put("teamTmUseruleAttJson","");
                    next3Map.put("teamMemberAttJson","");
                    next3Map.put("certTmUseruleAttJson","");
                    next3Map.put("applyCheckAgentAttJson","");
                    next3Map.put("commQuaCertAttJson","");
                    next3Map.put("commChkInvyAttJson","");
                    next3Map.put("commTechListAttJson","");

                    //联系人名称
                    if(StringUtils.isNotBlank(application2.getContactName())){
                        next3Map.put("assigneeContactName",application2.getContactName());
                    }else{
                        next3Map.put("assigneeContactName","");
                    }
                    //联系人电话
                    if(StringUtils.isNotBlank(application2.getContactPhone())){
                        next3Map.put("assigneeContactPhone",application2.getContactPhone());
                    }else{
                        next3Map.put("assigneeContactPhone","");
                    }

                    next3Map.put("assignorCertType",null);
                    next3Map.put("assigneeJointApplyFlag",0);
                }
            }

            String next3Body = JSON.toJSONString(next3Map);
            //申请人信息下一步
            nextJsoup("https://toas.sbj.cnipa.gov.cn/toas-extra-prod/extra-prod-api/tempsubsq/web/tmzr/appform/save",next3Body,tmToasToken,referer);

        }catch (Exception e){
            log.info("送案失败",e);
            return Result.error("送案失败");
        }
        return Result.ok(appformId);
    }

    /**
     * 功能描述:商标答复提交商标局
     * 场景:
     * @Param: [trademarkAnnexList, request]
     * @Return: java.lang.String
     * @Author: zcs
     * @Date:  2023/5/12
     */
    @GetMapping(value = "/startDfTmsve")
    public Result<?> startDfTmsve(String workbenchId, HttpServletRequest request, HttpServletResponse response) throws InterruptedException, IOException {
        String token = request.getParameter("token");
        String alertScrollTopJs = "document.querySelector('.pop-content').scrollTop=";
        //加载申请人附件数据到本地
        shunchaoTrademarkTmsveService.getTrademarkApplicantInfo(workbenchId,token);

        //加载申请人基本信息
        ShunchaoTrademarkApplicant shunchaoTrademarkApplicant =shunchaoTrademarkTmsveService.getDfSendTsvmeData(workbenchId,token);
        String rootPath = System.getProperty("exe.path");
//        String rootPath ="D:\\DUOUEXE\\duou\\";
        //System.out.println("开始提交程序：=====根目录====="+rootPath);
        driver = ChromeDriverUtils.beforeM(driver,rootPath);
        //driver.navigate().to(url);
        driver.get(url);

        //driver.findElement(By.id("pin")).sendKeys(pinword);
        driver.findElement(By.id("pin")).sendKeys(shunchaoTrademarkApplicant.getTmsvePin());
        driver.findElement(By.id("cipher")).sendKeys(shunchaoTrademarkApplicant.getTmsveCipher());
        //driver.findElement(By.xpath("//*[@id=\"pinWord\"]")).click();
        driver.findElement(By.cssSelector("#pinWord")).click();
        Thread.sleep(3000);

        //移动弹框内滚动条
        ((JavascriptExecutor) driver).executeScript(alertScrollTopJs+1000);

        //driver.findElement(By.xpath("/html/body/div[2]/div[2]/div[1]/div/input")).click();//开发本地电脑可以
        //driver.findElement(By.xpath("//INPUT[@class=\"pop-ok pop-next\"]")).click();//ie8可用
        driver.findElement(By.xpath("//INPUT[@class=\"pop-ok pop-next\"]")).sendKeys(Keys.ENTER);
        //driver.findElement(By.cssSelector(".pop-next")).click();//开发本地电脑可以
        Thread.sleep(2000);

        ((JavascriptExecutor) driver).executeScript(alertScrollTopJs+3000);
        Thread.sleep(2000);

        //driver.findElement(By.xpath("/html/body/div[2]/div[2]/div[2]/div/input[2]")).click();//开发本地电脑可以
        driver.findElement(By.xpath("//INPUT[@class=\"pop-ok pop-close\"]")).click();//ie8可用
        //driver.findElement(By.cssSelector("input.pop-ok:nth-child(2)")).click();//开发本地电脑可以

        //菜单
        driver.findElement(By.xpath("//*[@id=\"menu\"]/UL/LI[8]/A")).click();
        Thread.sleep(2000);
        driver.findElement(By.xpath("//*[@id=\"menu\"]/ul/li[8]/ul/li[2]/a")).click();
        //进入iframe
        driver.switchTo().frame("myframe");

        //申请号
        driver.findElement(By.id("appNum")).sendKeys(shunchaoTrademarkApplicant.getApplyNumber());
        driver.findElement(By.id("but1")).click();
        driver.findElement(By.xpath("//*[@id=\"form1\"]/table[3]/tbody/tr[2]/td[9]/a[2]")).click();

        Thread.sleep(7000);
        //申请人类型
        WebElement el1 = driver.findElement(By.id("appTypeId"));
        Select sel1 = new Select(el1);
        if ("0".equals(shunchaoTrademarkApplicant.getApplicantType())) {
            sel1.selectByValue("100012000000000001");
            //driver.findElement(By.xpath("//SELECT[@id='appTypeId']/option[@value='100012000000000001']")).click();
        } else if ("1".equals(shunchaoTrademarkApplicant.getApplicantType())) {
            sel1.selectByValue("100012000000000002");
        }

        //书式类型
        WebElement el2 = driver.findElement(By.xpath("//select[@id=\"appGjdq\"]"));
        Select sel2 = new Select(el2);
        if ("0".equals(shunchaoTrademarkApplicant.getBookOwnerType())) {
            sel2.selectByValue("100011000000000001");
        } else if ("4".equals(shunchaoTrademarkApplicant.getBookOwnerType())) {
            sel2.selectByValue("100011000000000002");
        }else if ("1".equals(shunchaoTrademarkApplicant.getBookOwnerType())) {
            sel2.selectByValue("100011000000000003");
        }else if ("2".equals(shunchaoTrademarkApplicant.getBookOwnerType())) {
            sel2.selectByValue("100011000000000004");
        }else if ("3".equals(shunchaoTrademarkApplicant.getBookOwnerType())) {
            sel2.selectByValue("100011000000000005");
        }

        //下一步
        driver.findElement(By.cssSelector("td>label:last-child input")).click();

        //申请人名称
        driver.findElement(By.id("appCnName")).clear();
        driver.findElement(By.id("appCnName")).sendKeys(shunchaoTrademarkApplicant.getApplicantName());

        //申请人地址
        driver.findElement(By.id("appCnAddr")).clear();
        driver.findElement(By.id("appCnAddr")).sendKeys(shunchaoTrademarkApplicant.getApplicationAddres());

        if ("0".equals(shunchaoTrademarkApplicant.getBookOwnerType())&&
                ("1".equals(shunchaoTrademarkApplicant.getApplicantType()) || "0".equals(shunchaoTrademarkApplicant.getApplicantType()))) {

            if ("1".equals(shunchaoTrademarkApplicant.getApplicantType())) {
                //证件名称
                WebElement ele3 = driver.findElement(By.id("appCertificateId"));
                Select sel3 = new Select(ele3);
                if ("0".equals(shunchaoTrademarkApplicant.getIdName())) {
                    sel3.selectByValue("200005000400000000");
                } else if ("1".equals(shunchaoTrademarkApplicant.getIdName())) {
                    sel3.selectByValue("200005000500000000");
                }else if ("2".equals(shunchaoTrademarkApplicant.getIdName())) {
                    sel3.selectByValue("200005002100000000");
                }

                //证件号码
                driver.findElement(By.id("appCertificateNum")).clear();
                driver.findElement(By.id("appCertificateNum")).sendKeys(shunchaoTrademarkApplicant.getIdNumber());

                //身份证明文件(中文)
                driver.findElement(By.cssSelector("#fileSfTr > td:nth-child(2) > input:nth-child(3)")).click();
                //driver.findElement(By.cssSelector("#fileSfTr>td.td_2 input:nth-of-type(2)")).click();//开发本地电脑可以
                driver.switchTo().frame("ifr_popup0");
                driver.findElement(By.id("fileSf")).sendKeys(rootPath+shunchaoTrademarkApplicant.getSba0025());
                driver.findElement(By.id("laodBut")).click();
                driver.switchTo().parentFrame();
                driver.findElement(By.id("dialogBoxClose")).click();

            }

            //主体资格证明文件(中文)
            driver.findElement(By.cssSelector("#fileZtTr > td:nth-child(2) > input:nth-child(3)")).click();
            //driver.findElement(By.cssSelector("#fileZtTr>td.td_2 input:nth-of-type(2)")).click();//开发本地电脑可以
            driver.switchTo().frame("ifr_popup0");
            driver.findElement(By.id("fileZt")).sendKeys(rootPath+shunchaoTrademarkApplicant.getSba0027());
            driver.findElement(By.id("laodBut")).click();
            driver.switchTo().parentFrame();
            driver.findElement(By.id("dialogBoxClose")).click();


            //邮政编码（申请人）
            driver.findElement(By.id("appContactZip")).clear();
            driver.findElement(By.id("appContactZip")).sendKeys(shunchaoTrademarkApplicant.getPostalCode());

            //联系人（代理人）
//            driver.findElement(By.id("appContactPerson")).sendKeys(shunchaoTrademarkApplicant.getContactPerson());

            //联系电话（代理人）
//            driver.findElement(By.id("appContactTel")).sendKeys(shunchaoTrademarkApplicant.getContactNumber());

            if (Objects.nonNull(shunchaoTrademarkApplicant.getFaxInAreacode())) {
                //传真（含地区号）
                driver.findElement(By.id("appContactFax")).clear();
                driver.findElement(By.id("appContactFax")).sendKeys(shunchaoTrademarkApplicant.getFaxInAreacode());
            }

        }else {

            //申请人名称(英文)
            driver.findElement(By.id("appEnName")).clear();
            driver.findElement(By.id("appEnName")).sendKeys(shunchaoTrademarkApplicant.getApplicantOwnerEnglishname());

            if ("0".equals(shunchaoTrademarkApplicant.getApplicantType())) {

                if ("4".equals(shunchaoTrademarkApplicant.getBookOwnerType())) {
                    //主体资格证明文件(中文)
                    driver.findElement(By.cssSelector("#fileZtTr > td:nth-child(2) > input:nth-child(3)")).click();
                    //driver.findElement(By.cssSelector("#fileZtTr>td.td_2 input:nth-of-type(2)")).click();//开发本地电脑可以
                    driver.switchTo().frame("ifr_popup0");
                    driver.findElement(By.id("fileZt")).sendKeys(rootPath+shunchaoTrademarkApplicant.getSba0027());
                    driver.findElement(By.id("laodBut")).click();
                    driver.switchTo().parentFrame();
                    driver.findElement(By.id("dialogBoxClose")).click();

                    //主体资格证明原文件(外文)
                    driver.findElement(By.cssSelector("#fileZtEnTr > td:nth-child(2) > input:nth-child(3)")).click();
                    //driver.findElement(By.cssSelector("#fileZtEnTr>td.td_2 input:nth-of-type(2)")).click();//开发本地电脑可以
                    driver.switchTo().frame("ifr_popup0");
                    driver.findElement(By.id("fileZtEn")).sendKeys(rootPath+shunchaoTrademarkApplicant.getSba0028());
                    driver.findElement(By.id("laodBut")).click();
                    driver.switchTo().parentFrame();
                    driver.findElement(By.id("dialogBoxClose")).click();

                }else {
                    if ("1".equals(shunchaoTrademarkApplicant.getWhetherOriginaldocChinese())) {
                        //证明文件原件是否为中文
                        driver.findElement(By.id("fileIsEn1")).click();
                    } else {
                        //证明文件原件是否为中文
                        driver.findElement(By.id("fileIsEn")).click();

                        //主体资格证明原文件(外文)
                        driver.findElement(By.cssSelector("#fileZtEnTr > td:nth-child(2) > input:nth-child(3)")).click();
                        //driver.findElement(By.cssSelector("#fileZtEnTr>td.td_2 input:nth-of-type(2)")).click();//开发本地电脑可以
                        driver.switchTo().frame("ifr_popup0");
                        driver.findElement(By.id("fileZtEn")).sendKeys(rootPath + shunchaoTrademarkApplicant.getSba0028());
                        driver.findElement(By.id("laodBut")).click();
                        driver.switchTo().parentFrame();
                        driver.findElement(By.id("dialogBoxClose")).click();
                    }

                    //主体资格证明文件(中文)
                    driver.findElement(By.cssSelector("#fileZtTr > td:nth-child(2) > input:nth-child(3)")).click();
                    //driver.findElement(By.cssSelector("#fileZtTr>td.td_2 input:nth-of-type(2)")).click();//开发本地电脑可以
                    driver.switchTo().frame("ifr_popup0");
                    driver.findElement(By.id("fileZt")).sendKeys(rootPath + shunchaoTrademarkApplicant.getSba0027());
                    driver.findElement(By.id("laodBut")).click();
                    driver.switchTo().parentFrame();
                    driver.findElement(By.id("dialogBoxClose")).click();
                }

            }else {

                //证件名称
                WebElement ele3 = driver.findElement(By.id("appCertificateId"));
                Select sel3 = new Select(ele3);
                if ("0".equals(shunchaoTrademarkApplicant.getIdName())) {
                    sel3.selectByValue("200005000400000000");
                } else if ("1".equals(shunchaoTrademarkApplicant.getIdName())) {
                    sel3.selectByValue("200005000500000000");
                }else if ("2".equals(shunchaoTrademarkApplicant.getIdName())) {
                    sel3.selectByValue("200005002100000000");
                }

                //证件号码
                driver.findElement(By.id("appCertificateNum")).clear();
                driver.findElement(By.id("appCertificateNum")).sendKeys(shunchaoTrademarkApplicant.getIdNumber());

                //身份证明文件(中文)
                driver.findElement(By.cssSelector("#fileSfTr > td:nth-child(2) > input:nth-child(3)")).click();
                //driver.findElement(By.cssSelector("#fileSfTr>td.td_2 input:nth-of-type(2)")).click();//开发本地电脑可以
                driver.switchTo().frame("ifr_popup0");
                driver.findElement(By.id("fileSf")).sendKeys(rootPath+shunchaoTrademarkApplicant.getSba0025());
                driver.findElement(By.id("laodBut")).click();
                driver.switchTo().parentFrame();
                driver.findElement(By.id("dialogBoxClose")).click();

                if ("4".equals(shunchaoTrademarkApplicant.getBookOwnerType())) {
                    //身份证明原文件(外文)
                    driver.findElement(By.cssSelector("#fileSfEnTr > td:nth-child(2) > input:nth-child(3)")).click();
                    //driver.findElement(By.cssSelector("#fileSfEnTr>td.td_2 input:nth-of-type(2)")).click();//开发本地电脑可以
                    driver.switchTo().frame("ifr_popup0");
                    driver.findElement(By.id("fileSfEn")).sendKeys(rootPath+shunchaoTrademarkApplicant.getSba0026());
                    driver.findElement(By.id("laodBut")).click();
                    driver.switchTo().parentFrame();
                    driver.findElement(By.id("dialogBoxClose")).click();
                }else {
                    if ("0".equals(shunchaoTrademarkApplicant.getWhetherOriginaldocChinese())) {

                        //证明文件原件是否为中文
                        driver.findElement(By.id("fileIsEn")).click();

                        //身份证明原文件(外文)
                        driver.findElement(By.cssSelector("#fileSfEnTr > td:nth-child(2) > input:nth-child(3)")).click();
                        //driver.findElement(By.cssSelector("#fileSfEnTr>td.td_2 input:nth-of-type(2)")).click();//开发本地电脑可以
                        driver.switchTo().frame("ifr_popup0");
                        driver.findElement(By.id("fileSfEn")).sendKeys(rootPath+shunchaoTrademarkApplicant.getSba0026());
                        driver.findElement(By.id("laodBut")).click();
                        driver.switchTo().parentFrame();
                        driver.findElement(By.id("dialogBoxClose")).click();

                    }else {
                        //证明文件原件是否为中文
                        driver.findElement(By.id("fileIsEn1")).click();
                    }
                }

            }

            if ("4".equals(shunchaoTrademarkApplicant.getBookOwnerType())) {
                //国家和地区
                new Select(driver.findElement(By.id("appCrtyId"))).selectByValue(shunchaoTrademarkApplicant.getCountryArea());
            }

            //申请人地址(英文)
            driver.findElement(By.id("appEnAddr")).clear();
            driver.findElement(By.id("appEnAddr")).sendKeys(shunchaoTrademarkApplicant.getApplicationAddressEnglish());

            //申请人国内接收人名称
            driver.findElement(By.id("acceptPerson")).clear();
            driver.findElement(By.id("acceptPerson")).sendKeys(shunchaoTrademarkApplicant.getApplicationMainlandRecipientname());

            //接收人地址
            driver.findElement(By.id("acceptAddr")).clear();
            driver.findElement(By.id("acceptAddr")).sendKeys(shunchaoTrademarkApplicant.getRecipientAddress());

            //接收人邮编
            driver.findElement(By.id("acceptZip")).clear();
            driver.findElement(By.id("acceptZip")).sendKeys(shunchaoTrademarkApplicant.getRecipientPostcode());

        }
        //下一步
        driver.findElements(By.cssSelector("td>label:last-child input")).get(1).click();
        //下一步
        driver.findElements(By.cssSelector("td>label:last-child input")).get(2).click();
        //下一步
        driver.findElements(By.cssSelector("td>label:last-child input")).get(3).click();
        //下一步
        driver.findElements(By.cssSelector("td>label:last-child input")).get(4).click();
        //下一步
        driver.findElements(By.cssSelector("td>label:last-child input")).get(5).click();
        return Result.ok();
    }

}
