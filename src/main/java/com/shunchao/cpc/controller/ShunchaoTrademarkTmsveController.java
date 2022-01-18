package com.shunchao.cpc.controller;

import com.shunchao.cpc.model.ShunchaoTrademarkApplicantProduct;
import com.shunchao.cpc.model.ShunchaoTrademarkCoApplicant;
import com.shunchao.cpc.model.ShunchaoTrademarkPow;
import com.shunchao.cpc.model.ShunchaoTrademarkProduct;
import com.shunchao.cpc.util.SeleniumUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Action;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/trademark/connectTmsve")
public class ShunchaoTrademarkTmsveController {

    private WebDriver driver;
    //String url = "http://www.baidu.com";
    //private String url = "http://wssq.sbj.cnipa.gov.cn:9080/tmsve/";

    @Value("${trademark.URL}")
    private String url;
    @Value("${trademark.PINWORD}")
    private String pinword;

    /**
     * 功能描述:商标注册
     * 场景:
     * @Param: [trademarkApplicantProduct, request]
     * @Return: void
     * @Author: Ironz
     * @Date: 2021/12/30 13:37
     */
    @PostMapping(value = "/startUpTmsve")
    public void startUpTmsve(@RequestBody ShunchaoTrademarkApplicantProduct trademarkApplicantProduct, HttpServletRequest request) throws Exception{

        //String rootPath = System.getProperty("exe.path");
        String rootPath ="D:\\connectttt\\";
        driver = SeleniumUtils.beforeM(driver,rootPath);

        //driver.navigate().to(url);
        driver.get(url);
        driver.findElement(By.id("pin")).sendKeys(pinword);
        //driver.findElement(By.xpath("//*[@id=\"pinWord\"]")).click();
        driver.findElement(By.cssSelector("#pinWord")).click();
        Thread.sleep(3000);
        //driver.findElement(By.xpath("//INPUT[@class=\"pop-ok pop-next\"]")).click();
        driver.findElement(By.cssSelector(".pop-next")).click();
        Thread.sleep(3000);
        //driver.findElement(By.xpath("//INPUT[@class=\"pop-ok pop-close\"]")).click();
        driver.findElement(By.cssSelector("input.pop-ok:nth-child(2)")).click();

        //菜单
        //driver.findElement(By.xpath("//*[@id=\"menu\"]/UL/LI[1]/A")).click();
        driver.findElement(By.cssSelector("#menu > ul:nth-child(1) > li:nth-child(1) > a:nth-child(1)")).click();
        //driver.findElement(By.xpath("//*[@id=\"menu\"]/UL/LI[1]/UL/LI/A")).click();
        driver.findElement(By.cssSelector("#menu > ul:nth-child(1) > li:nth-child(1) > ul:nth-child(2) > li:nth-child(1) > a:nth-child(1)")).click();

        //System.out.println("=============当前页面源码============="+driver.getPageSource());
        //进入iframe
        driver.switchTo().frame("myframe");
        //System.out.println("=====当前myframe页面======"+driver.getWindowHandle());
        //System.out.println("=============当前myframe源码============="+driver.getPageSource());


        //申请人类型
        WebElement el1 = driver.findElement(By.id("appTypeId"));
        Select sel1 = new Select(el1);
        if ("0".equals(trademarkApplicantProduct.getApplicantType())) {
            sel1.selectByValue("100012000000000001");
            //driver.findElement(By.xpath("//SELECT[@id='appTypeId']/option[@value='100012000000000001']")).click();
        } else if ("1".equals(trademarkApplicantProduct.getApplicantType())) {
            sel1.selectByValue("100012000000000002");
        }

        //书式类型
        WebElement el2 = driver.findElement(By.xpath("//select[@id=\"appGjdq\"]"));
        Select sel2 = new Select(el2);
        if ("0".equals(trademarkApplicantProduct.getBookOwnerType())) {
            sel2.selectByValue("100011000000000001");
        } else if ("4".equals(trademarkApplicantProduct.getBookOwnerType())) {
            sel2.selectByValue("100011000000000002");
        }else if ("1".equals(trademarkApplicantProduct.getBookOwnerType())) {
            sel2.selectByValue("100011000000000003");
        }else if ("2".equals(trademarkApplicantProduct.getBookOwnerType())) {
            sel2.selectByValue("100011000000000004");
        }else if ("3".equals(trademarkApplicantProduct.getBookOwnerType())) {
            sel2.selectByValue("100011000000000005");
        }

        //下一步
        //((JavascriptExecutor)driver).executeScript("document.querySelectorAll('td label input')[document.querySelectorAll('td label input').length - 1].click()");
        driver.findElement(By.cssSelector("td>label:last-child input")).click();

        //System.out.println("======法人其他组织和大陆源码=========="+driver.getPageSource());

        //代理文号
        if (Objects.nonNull(trademarkApplicantProduct.getAgentNumber())) {
            driver.findElement(By.id("agentFilenum")).sendKeys(trademarkApplicantProduct.getAgentNumber());
        }

        //代理人姓名
        driver.findElement(By.id("agentPerson")).sendKeys(trademarkApplicantProduct.getAgentName());

        //代理委托书
        driver.findElement(By.cssSelector("#fileWtTr>td.td_2 input:nth-of-type(3)")).click();//代理委托书上传按钮
        driver.switchTo().frame("ifr_popup0");//进入上传文件iframe
        //System.out.println("=====当前ifr_popup0页面======"+driver.getWindowHandle());
        //System.out.println("======当前ifr_popup0源码=========="+driver.getPageSource());

        if (trademarkApplicantProduct.getSba0023().size() > 0) {
            for (ShunchaoTrademarkPow trademarkPow:trademarkApplicantProduct.getSba0023()){
                driver.findElement(By.id("fileWt")).sendKeys(rootPath+trademarkPow.getSba0023());
                driver.findElement(By.id("laodBut")).click();
            }
        }

        //System.out.println("======上传成功后页面源码=========="+driver.getPageSource());
        /*Actions act = new Actions(driver);
        act.doubleClick(driver.findElement(By.id("dialogClose"))).build().perform();*/
        driver.switchTo().parentFrame();//回到上一个iframe
        driver.findElement(By.id("dialogBoxClose")).click();

        //申请人名称
        driver.findElement(By.id("appCnName")).sendKeys(trademarkApplicantProduct.getApplicantOwnerChinesename());

        //统一社会信用代码
        if (Objects.nonNull(trademarkApplicantProduct.getUnifiedSocialCreditcode())) {
            driver.findElement(By.id("certCode")).sendKeys(trademarkApplicantProduct.getUnifiedSocialCreditcode());
        }

        //申请人地址
        driver.findElement(By.id("appCnAddr")).sendKeys(trademarkApplicantProduct.getApplicationAddres());

        if ("0".equals(trademarkApplicantProduct.getBookOwnerType())&&
                ("1".equals(trademarkApplicantProduct.getApplicantType()) || "0".equals(trademarkApplicantProduct.getApplicantType()))) {


            if ("1".equals(trademarkApplicantProduct.getApplicantType())) {
                //证件名称
                WebElement ele3 = driver.findElement(By.id("appCertificateId"));
                Select sel3 = new Select(ele3);
                if ("0".equals(trademarkApplicantProduct.getIdName())) {
                    sel3.selectByValue("200005000400000000");
                } else if ("1".equals(trademarkApplicantProduct.getIdName())) {
                    sel3.selectByValue("200005000500000000");
                }else if ("2".equals(trademarkApplicantProduct.getIdName())) {
                    sel3.selectByValue("200005002100000000");
                }

                //证件号码
                driver.findElement(By.id("appCertificateNum")).sendKeys(trademarkApplicantProduct.getIdNumber());

                //身份证明文件(中文)
                driver.findElement(By.cssSelector("#fileSfTr>td.td_2 input:nth-of-type(2)")).click();
                driver.switchTo().frame("ifr_popup0");
                driver.findElement(By.id("fileSf")).sendKeys(rootPath+trademarkApplicantProduct.getSba0025());
                driver.findElement(By.id("laodBut")).click();
                driver.switchTo().parentFrame();
                driver.findElement(By.id("dialogBoxClose")).click();

            }

            //主体资格证明文件(中文)
            driver.findElement(By.cssSelector("#fileZtTr>td.td_2 input:nth-of-type(2)")).click();
            driver.switchTo().frame("ifr_popup0");
            driver.findElement(By.id("fileZt")).sendKeys(rootPath+trademarkApplicantProduct.getSba0027());
            driver.findElement(By.id("laodBut")).click();
            driver.switchTo().parentFrame();
            driver.findElement(By.id("dialogBoxClose")).click();


            //联系人
            driver.findElement(By.id("appContactPerson")).sendKeys(trademarkApplicantProduct.getContactPerson());

            //联系电话
            driver.findElement(By.id("appContactTel")).sendKeys(trademarkApplicantProduct.getContactNumber());

            if (Objects.nonNull(trademarkApplicantProduct.getFaxInAreacode())) {
                //传真（含地区号）
                driver.findElement(By.id("appContactFax")).sendKeys(trademarkApplicantProduct.getFaxInAreacode());
            }

            //邮政编码
            driver.findElement(By.id("appContactZip")).sendKeys(trademarkApplicantProduct.getPostalCode());

        }else {

            //申请人名称(英文)
            driver.findElement(By.id("appEnName")).sendKeys(trademarkApplicantProduct.getApplicantOwnerEnglishname());

            if ("0".equals(trademarkApplicantProduct.getApplicantType())) {

                if ("4".equals(trademarkApplicantProduct.getBookOwnerType())) {
                    //主体资格证明文件(中文)
                    driver.findElement(By.cssSelector("#fileZtTr>td.td_2 input:nth-of-type(2)")).click();
                    driver.switchTo().frame("ifr_popup0");
                    driver.findElement(By.id("fileZt")).sendKeys(rootPath+trademarkApplicantProduct.getSba0027());
                    driver.findElement(By.id("laodBut")).click();
                    driver.switchTo().parentFrame();
                    driver.findElement(By.id("dialogBoxClose")).click();

                    //主体资格证明原文件(外文)
                    driver.findElement(By.cssSelector("#fileZtEnTr>td.td_2 input:nth-of-type(2)")).click();
                    driver.switchTo().frame("ifr_popup0");
                    driver.findElement(By.id("fileZtEn")).sendKeys(rootPath+trademarkApplicantProduct.getSba0028());
                    driver.findElement(By.id("laodBut")).click();
                    driver.switchTo().parentFrame();
                    driver.findElement(By.id("dialogBoxClose")).click();

                }else {
                    if ("1".equals(trademarkApplicantProduct.getWhetherOriginaldocChinese())) {
                        //证明文件原件是否为中文
                        driver.findElement(By.id("fileIsEn1")).click();
                    } else {
                        //证明文件原件是否为中文
                        driver.findElement(By.id("fileIsEn")).click();

                        //主体资格证明原文件(外文)
                        driver.findElement(By.cssSelector("#fileZtEnTr>td.td_2 input:nth-of-type(2)")).click();
                        driver.switchTo().frame("ifr_popup0");
                        driver.findElement(By.id("fileZtEn")).sendKeys(rootPath + trademarkApplicantProduct.getSba0028());
                        driver.findElement(By.id("laodBut")).click();
                        driver.switchTo().parentFrame();
                        driver.findElement(By.id("dialogBoxClose")).click();
                    }

                    //主体资格证明文件(中文)
                    driver.findElement(By.cssSelector("#fileZtTr>td.td_2 input:nth-of-type(2)")).click();
                    driver.switchTo().frame("ifr_popup0");
                    driver.findElement(By.id("fileZt")).sendKeys(rootPath + trademarkApplicantProduct.getSba0027());
                    driver.findElement(By.id("laodBut")).click();
                    driver.switchTo().parentFrame();
                    driver.findElement(By.id("dialogBoxClose")).click();
                }

            }else {

                //证件名称
                WebElement ele3 = driver.findElement(By.id("appCertificateId"));
                Select sel3 = new Select(ele3);
                if ("0".equals(trademarkApplicantProduct.getIdName())) {
                    sel3.selectByValue("200005000400000000");
                } else if ("1".equals(trademarkApplicantProduct.getIdName())) {
                    sel3.selectByValue("200005000500000000");
                }else if ("2".equals(trademarkApplicantProduct.getIdName())) {
                    sel3.selectByValue("200005002100000000");
                }

                //证件号码
                driver.findElement(By.id("appCertificateNum")).sendKeys(trademarkApplicantProduct.getIdNumber());

                //身份证明文件(中文)
                driver.findElement(By.cssSelector("#fileSfTr>td.td_2 input:nth-of-type(2)")).click();
                driver.switchTo().frame("ifr_popup0");
                driver.findElement(By.id("fileSf")).sendKeys(rootPath+trademarkApplicantProduct.getSba0025());
                driver.findElement(By.id("laodBut")).click();
                driver.switchTo().parentFrame();
                driver.findElement(By.id("dialogBoxClose")).click();

                if ("4".equals(trademarkApplicantProduct.getBookOwnerType())) {
                    //身份证明原文件(外文)
                    driver.findElement(By.cssSelector("#fileSfEnTr>td.td_2 input:nth-of-type(2)")).click();
                    driver.switchTo().frame("ifr_popup0");
                    driver.findElement(By.id("fileSfEn")).sendKeys(rootPath+trademarkApplicantProduct.getSba0026());
                    driver.findElement(By.id("laodBut")).click();
                    driver.switchTo().parentFrame();
                    driver.findElement(By.id("dialogBoxClose")).click();
                }else {
                    if ("0".equals(trademarkApplicantProduct.getWhetherOriginaldocChinese())) {

                        //证明文件原件是否为中文
                        driver.findElement(By.id("fileIsEn")).click();

                        //身份证明原文件(外文)
                        driver.findElement(By.cssSelector("#fileSfEnTr>td.td_2 input:nth-of-type(2)")).click();
                        driver.switchTo().frame("ifr_popup0");
                        driver.findElement(By.id("fileSfEn")).sendKeys(rootPath+trademarkApplicantProduct.getSba0026());
                        driver.findElement(By.id("laodBut")).click();
                        driver.switchTo().parentFrame();
                        driver.findElement(By.id("dialogBoxClose")).click();

                    }else {
                        //证明文件原件是否为中文
                        driver.findElement(By.id("fileIsEn1")).click();
                    }
                }

            }

            if ("4".equals(trademarkApplicantProduct.getBookOwnerType())) {
                //国家和地区
                new Select(driver.findElement(By.id("appCrtyId"))).selectByValue(trademarkApplicantProduct.getCountryArea());
            }

            //申请人地址(英文)
            driver.findElement(By.id("appEnAddr")).sendKeys(trademarkApplicantProduct.getApplicationAddressEnglish());

            //国内申请人联系地址
            driver.findElement(By.cssSelector("#communicationAddr")).sendKeys(trademarkApplicantProduct.getApplicantContactAddress());

            //邮政编码
            driver.findElement(By.cssSelector("#communicationZip")).sendKeys(trademarkApplicantProduct.getPostalCode());

            //国内申请人电子邮箱
            driver.findElement(By.cssSelector("#appContactEmail")).sendKeys(trademarkApplicantProduct.getApplicantEmail());

            //申请人国内接收人名称
            driver.findElement(By.id("acceptPerson")).sendKeys(trademarkApplicantProduct.getApplicationMainlandRecipientname());

            //接收人地址
            driver.findElement(By.id("acceptAddr")).sendKeys(trademarkApplicantProduct.getRecipientAddress());

            //接收人邮编
            driver.findElement(By.id("acceptZip")).sendKeys(trademarkApplicantProduct.getRecipientPostcode());

        }

        //下一步
        //int size = driver.findElements(By.cssSelector("td>label:last-child input")).size();
        driver.findElements(By.cssSelector("td>label:last-child input")).get(1).click();

        //===============商标声明===================
        //System.out.println("=============商标声明============="+driver.getPageSource());

        //商标类型
        driver.findElement(By.id("tmType"+trademarkApplicantProduct.getTrademarkType())).click();
        //System.out.println("=============商标声明集体============="+driver.getPageSource());

        if ("2".equals(trademarkApplicantProduct.getTrademarkType()) || "3".equals(trademarkApplicantProduct.getTrademarkType())) {

            //是否地理标志
            driver.findElement(By.id("isDlbz"+trademarkApplicantProduct.getWhetherGeographicalIndication())).click();

            //商标使用管理规则
            //driver.switchTo().frame(driver.findElement(By.cssSelector("#menberRuleTr>td.td_2 .ke-container .ke-edit iframe")));
            String js = "document.querySelector('#menberRuleTr>td.td_2 .ke-container .ke-edit iframe')\n" +
                    "\t\t.contentDocument.all[document\n" +
                    "\t\t\t.querySelector('#menberRuleTr>td.td_2 .ke-container .ke-edit iframe')\n" +
                    "\t\t\t.contentDocument.all.length - 1].innerHTML=\""+trademarkApplicantProduct.getCertificationManagementRulesTxt()+"\"";

            ((JavascriptExecutor) driver).executeScript(js);

            //商标使用管理规则(附件)
            driver.findElement(By.cssSelector("#menberRuleFjTr>td.td_2 input:nth-of-type(2)")).click();
            driver.switchTo().frame(driver.findElement(By.cssSelector("#dlg_upload>iframe")));
            driver.findElement(By.id("filePdf")).sendKeys(rootPath+trademarkApplicantProduct.getSba0001());
            driver.findElement(By.id("laodBut")).click();
            driver.switchTo().parentFrame();
            driver.findElement(By.cssSelector(".panel .panel-header .panel-tool a.panel-tool-close")).click();

            if ("2".equals(trademarkApplicantProduct.getTrademarkType())) {
                //集体成员名单
                String jss = "document.querySelector('#menberListTr>td.td_2 .ke-container .ke-edit iframe')\n" +
                        "\t\t\t\t\t.contentDocument.all[document\n" +
                        "\t\t\t\t\t\t.querySelector('#menberListTr>td.td_2 .ke-container .ke-edit iframe')\n" +
                        "\t\t\t\t\t\t.contentDocument.all.length - 1].innerHTML=\"" + trademarkApplicantProduct.getCollectiveMembers() + "\"";
                ((JavascriptExecutor) driver).executeScript(jss);

                //集体成员名单（附件）
                driver.findElement(By.cssSelector("#menberListFjTr>td.td_2 input:nth-of-type(2)")).click();
                driver.switchTo().frame(driver.findElement(By.cssSelector("#dlg_upload>iframe")));
                driver.findElement(By.id("filePdf")).sendKeys(rootPath + trademarkApplicantProduct.getSba0030());
                driver.findElement(By.id("laodBut")).click();
                driver.switchTo().parentFrame();
                driver.findElement(By.cssSelector(".panel .panel-header .panel-tool a.panel-tool-close")).click();
            }

            if ("0".equals(trademarkApplicantProduct.getWhetherGeographicalIndication())) {
                //附件
                driver.findElement(By.cssSelector("#fdfjTr>td.td_2 input:nth-of-type(2)")).click();
                driver.switchTo().frame(driver.findElement(By.cssSelector("#dlg_upload>iframe")));
                driver.findElement(By.id("filePdf")).sendKeys(rootPath + trademarkApplicantProduct.getSba0007());
                driver.findElement(By.id("laodBut")).click();
                driver.switchTo().parentFrame();
                driver.findElement(By.cssSelector(".panel .panel-header .panel-tool a.panel-tool-close")).click();
            }

            if ("1".equals(trademarkApplicantProduct.getWhetherGeographicalIndication())) {
                //地理标志材料一
                driver.findElement(By.cssSelector("#cpxyTr>td.td_2 input:nth-of-type(2)")).click();
                driver.switchTo().frame(driver.findElement(By.cssSelector("#dlg_upload>iframe")));
                driver.findElement(By.id("filePdf")).sendKeys(rootPath + trademarkApplicantProduct.getSba0002());
                driver.findElement(By.id("laodBut")).click();
                driver.switchTo().parentFrame();
                driver.findElement(By.cssSelector(".panel .panel-header .panel-tool a.panel-tool-close")).click();

                //地理标志材料二
                driver.findElement(By.cssSelector("#zfwjTr>td.td_2 input:nth-of-type(2)")).click();
                driver.switchTo().frame(driver.findElement(By.cssSelector("#dlg_upload>iframe")));
                driver.findElement(By.id("filePdf")).sendKeys(rootPath + trademarkApplicantProduct.getSba0003());
                driver.findElement(By.id("laodBut")).click();
                driver.switchTo().parentFrame();
                driver.findElement(By.cssSelector(".panel .panel-header .panel-tool a.panel-tool-close")).click();

                //地理标志材料三
                driver.findElement(By.cssSelector("#fwwjTr>td.td_2 input:nth-of-type(2)")).click();
                driver.switchTo().frame(driver.findElement(By.cssSelector("#dlg_upload>iframe")));
                driver.findElement(By.id("filePdf")).sendKeys(rootPath + trademarkApplicantProduct.getSba0004());
                driver.findElement(By.id("laodBut")).click();
                driver.switchTo().parentFrame();
                driver.findElement(By.cssSelector(".panel .panel-header .panel-tool a.panel-tool-close")).click();

                //地理标志材料四
                driver.findElement(By.cssSelector("#hjrwTr>td.td_2 input:nth-of-type(2)")).click();
                driver.switchTo().frame(driver.findElement(By.cssSelector("#dlg_upload>iframe")));
                driver.findElement(By.id("filePdf")).sendKeys(rootPath + trademarkApplicantProduct.getSba0005());
                driver.findElement(By.id("laodBut")).click();
                driver.switchTo().parentFrame();
                driver.findElement(By.cssSelector(".panel .panel-header .panel-tool a.panel-tool-close")).click();

                //地理标志材料五
                driver.findElement(By.cssSelector("#nljcTr>td.td_2 input:nth-of-type(2)")).click();
                driver.switchTo().frame(driver.findElement(By.cssSelector("#dlg_upload>iframe")));
                driver.findElement(By.id("filePdf")).sendKeys(rootPath + trademarkApplicantProduct.getSba0006());
                driver.findElement(By.id("laodBut")).click();
                driver.switchTo().parentFrame();
                driver.findElement(By.cssSelector(".panel .panel-header .panel-tool a.panel-tool-close")).click();

                //申请人是否具备检测能力
                driver.findElement(By.id("isJbjcnl" + trademarkApplicantProduct.getWhetherApplicantAbilityTest())).click();

                if ("0".equals(trademarkApplicantProduct.getWhetherApplicantAbilityTest())) {
                    //申请人与具有检测资格的机构签署的委托检测合同（附件）
                    driver.findElement(By.cssSelector("#swhtTr>td.td_2 input:nth-of-type(2)")).click();
                    driver.switchTo().frame(driver.findElement(By.cssSelector("#dlg_upload>iframe")));
                    driver.findElement(By.id("filePdf")).sendKeys(rootPath + trademarkApplicantProduct.getSba0012());
                    driver.findElement(By.id("laodBut")).click();
                    driver.switchTo().parentFrame();
                    driver.findElement(By.cssSelector(".panel .panel-header .panel-tool a.panel-tool-close")).click();

                    //受委托机构的单位法人证书（附件）
                    driver.findElement(By.cssSelector("#wtfrTr>td.td_2 input:nth-of-type(2)")).click();
                    driver.switchTo().frame(driver.findElement(By.cssSelector("#dlg_upload>iframe")));
                    driver.findElement(By.id("filePdf")).sendKeys(rootPath + trademarkApplicantProduct.getSba0013());
                    driver.findElement(By.id("laodBut")).click();
                    driver.switchTo().parentFrame();
                    driver.findElement(By.cssSelector(".panel .panel-header .panel-tool a.panel-tool-close")).click();

                    //受委托机构的资质证书（附件）
                    driver.findElement(By.cssSelector("#wtzzTr>td.td_2 input:nth-of-type(2)")).click();
                    driver.switchTo().frame(driver.findElement(By.cssSelector("#dlg_upload>iframe")));
                    driver.findElement(By.id("filePdf")).sendKeys(rootPath + trademarkApplicantProduct.getSba0014());
                    driver.findElement(By.id("laodBut")).click();
                    driver.switchTo().parentFrame();
                    driver.findElement(By.cssSelector(".panel .panel-header .panel-tool a.panel-tool-close")).click();

                    //专业检测设备清单（附件）
                    driver.findElement(By.cssSelector("#wtsbTr>td.td_2 input:nth-of-type(2)")).click();
                    driver.switchTo().frame(driver.findElement(By.cssSelector("#dlg_upload>iframe")));
                    driver.findElement(By.id("filePdf")).sendKeys(rootPath + trademarkApplicantProduct.getSba0015());
                    driver.findElement(By.id("laodBut")).click();
                    driver.switchTo().parentFrame();
                    driver.findElement(By.cssSelector(".panel .panel-header .panel-tool a.panel-tool-close")).click();

                    //专业技术人员名单（附件）
                    driver.findElement(By.cssSelector("#wtryTr>td.td_2 input:nth-of-type(2)")).click();
                    driver.switchTo().frame(driver.findElement(By.cssSelector("#dlg_upload>iframe")));
                    driver.findElement(By.id("filePdf")).sendKeys(rootPath + trademarkApplicantProduct.getSba0016());
                    driver.findElement(By.id("laodBut")).click();
                    driver.switchTo().parentFrame();
                    driver.findElement(By.cssSelector(".panel .panel-header .panel-tool a.panel-tool-close")).click();
                } else if ("1".equals(trademarkApplicantProduct.getWhetherApplicantAbilityTest())) {
                    //申请人检测资质证书（附件）
                    driver.findElement(By.cssSelector("#SqzsTr>td.td_2 input:nth-of-type(2)")).click();
                    driver.switchTo().frame(driver.findElement(By.cssSelector("#dlg_upload>iframe")));
                    driver.findElement(By.id("filePdf")).sendKeys(rootPath + trademarkApplicantProduct.getSba0008());
                    driver.findElement(By.id("laodBut")).click();
                    driver.switchTo().parentFrame();
                    driver.findElement(By.cssSelector(".panel .panel-header .panel-tool a.panel-tool-close")).click();

                    //申请人专业检测设备清单（附件）
                    driver.findElement(By.cssSelector("#SqsbTr>td.td_2 input:nth-of-type(2)")).click();
                    driver.switchTo().frame(driver.findElement(By.cssSelector("#dlg_upload>iframe")));
                    driver.findElement(By.id("filePdf")).sendKeys(rootPath + trademarkApplicantProduct.getSba0009());
                    driver.findElement(By.id("laodBut")).click();
                    driver.switchTo().parentFrame();
                    driver.findElement(By.cssSelector(".panel .panel-header .panel-tool a.panel-tool-close")).click();

                    //申请人专业技术人员名单（附件）
                    driver.findElement(By.cssSelector("#sqryTr>td.td_2 input:nth-of-type(2)")).click();
                    driver.switchTo().frame(driver.findElement(By.cssSelector("#dlg_upload>iframe")));
                    driver.findElement(By.id("filePdf")).sendKeys(rootPath + trademarkApplicantProduct.getSba0010());
                    driver.findElement(By.id("laodBut")).click();
                    driver.switchTo().parentFrame();
                    driver.findElement(By.cssSelector(".panel .panel-header .panel-tool a.panel-tool-close")).click();

                    //申请人技术人员证书（附件）
                    driver.findElement(By.cssSelector("#sqjsTr>td.td_2 input:nth-of-type(2)")).click();
                    driver.switchTo().frame(driver.findElement(By.cssSelector("#dlg_upload>iframe")));
                    driver.findElement(By.id("filePdf")).sendKeys(rootPath + trademarkApplicantProduct.getSba0011());
                    driver.findElement(By.id("laodBut")).click();
                    driver.switchTo().parentFrame();
                    driver.findElement(By.cssSelector(".panel .panel-header .panel-tool a.panel-tool-close")).click();
                }
            }

        }

        //是否三维标志
        String a1 = "ifSolidTm";
        if ("1".equals(trademarkApplicantProduct.getWhetherGeographicalIndication())) {
            a1 = a1 + 1;
        } else if ("0".equals(trademarkApplicantProduct.getWhetherThreedimensionLogo())) {
            a1 = a1 + 1;
        } else if ("1".equals(trademarkApplicantProduct.getWhetherThreedimensionLogo())) {
            a1 = a1;
        }
        driver.findElement(By.id(a1)).click();

        //是否颜色组合
        String a2 = "colourSign";
        if ("1".equals(trademarkApplicantProduct.getWhetherGeographicalIndication())) {
            a2 = a2 + 1;
        } else if ("0".equals(trademarkApplicantProduct.getWhetherColorCombination())) {
            a2 = a2 + 1;
        } else if ("1".equals(trademarkApplicantProduct.getWhetherColorCombination())) {
            a2 = a2 + 2;
        }
        driver.findElement(By.id(a2)).click();

        //声音商标
        if ("0".equals(trademarkApplicantProduct.getWhetherGeographicalIndication()) && "1".equals(trademarkApplicantProduct.getSoundMark())) {
            driver.findElement(By.id("tmFormType1")).click();

            //声音文件
            driver.findElement(By.cssSelector("#fileSyTr>td.td_2 input:nth-of-type(2)")).click();

            driver.switchTo().frame("ifr_popup0");
            driver.findElement(By.id("fileSy")).sendKeys(rootPath+trademarkApplicantProduct.getSba0017());
            driver.findElement(By.id("laodBut")).click();
            driver.switchTo().parentFrame();
            driver.findElement(By.id("dialogBoxClose")).click();
        }

        //商标名称
        driver.findElement(By.id("tmName")).sendKeys(trademarkApplicantProduct.getTrademarkName());

        //商标说明
        driver.findElement(By.id("tmDesignDeclare")).sendKeys(trademarkApplicantProduct.getTrademarkDescription());

        //下一步
        driver.findElements(By.cssSelector("td>label:last-child input")).get(2).click();

        //=============共同申请信息==================

        //是否共同申请
        String a3 = "ifShareTm";
        if ("0".equals(trademarkApplicantProduct.getWhetherApplyJointly())) {
            a3 = a3+1;
        } else if ("1".equals(trademarkApplicantProduct.getWhetherApplyJointly())) {
            a3 = a3;
        }
        driver.findElement(By.id(a3)).click();


        if ("1".equals(trademarkApplicantProduct.getWhetherApplyJointly())) {
            //点击添加共同申请人信息
            driver.findElement(By.cssSelector("#gtInfo > td:nth-child(1) > a:nth-child(1)")).click();

            String parentWindowsId = driver.getWindowHandle();
            Set<String> allwindowsId = driver.getWindowHandles();
            for (String id : allwindowsId) {
                if (!parentWindowsId.equals(id)) {
                    driver.switchTo().window(id);
                    if (trademarkApplicantProduct.getTrademarkCoApplicants().size() > 0) {
                        int a = 0;
                        for (ShunchaoTrademarkCoApplicant trademarkCoApplicant : trademarkApplicantProduct.getTrademarkCoApplicants()) {
                            a++;
                            //共有人类型
                            String g = "00000";
                            if ("0".equals(trademarkCoApplicant.getApplicantType())) {
                                g = g + 1;
                            } else if ("1".equals(trademarkCoApplicant.getApplicantType())) {
                                g = g + 2;
                            }
                            new Select(driver.findElement(By.id("appTypeId"))).selectByValue(g);

                            //共有人国籍
                            String g1 = "00000";
                            if ("0".equals(trademarkCoApplicant.getBookOwnerType())) {
                                g1 = g1 + 1;
                            } else if ("1".equals(trademarkCoApplicant.getBookOwnerType())) {
                                g1 = g1 + 2;
                            } else if ("2".equals(trademarkCoApplicant.getBookOwnerType())) {
                                g1 = g1 + 3;
                            } else if ("3".equals(trademarkCoApplicant.getBookOwnerType())) {
                                g1 = g1 + 4;
                            } else if ("4".equals(trademarkCoApplicant.getBookOwnerType())) {
                                g1 = g1 + 5;
                            }
                            new Select(driver.findElement(By.id("appGjdq"))).selectByValue(g1);

                            //共有人名称中文
                            driver.findElement(By.cssSelector("#nameCn")).sendKeys(trademarkCoApplicant.getApplicantOwnerChinesename());

                            if ("0".equals(trademarkCoApplicant.getApplicantType())) {

                                if (!("0".equals(trademarkCoApplicant.getBookOwnerType()) || "4".equals(trademarkCoApplicant.getBookOwnerType()))) {

                                    //证明文件原件是否为中文
                                    String d = "gtfileIsEn";
                                    if ("0".equals(trademarkCoApplicant.getWhetherOriginaldocChinese())) {
                                        d = d;
                                    } else if ("1".equals(trademarkCoApplicant.getWhetherOriginaldocChinese())) {
                                        d = d + 1;
                                    }
                                    driver.findElement(By.id(d)).click();
                                }

                                if ("4".equals(trademarkCoApplicant.getBookOwnerType())) {
                                    //共有人名称英文
                                    driver.findElement(By.cssSelector("#nameEn")).sendKeys(trademarkCoApplicant.getApplicantOwnerEnglishname());
                                }

                                //主体资格证明文件
                                driver.findElement(By.cssSelector("#gtFileZtTr > td:nth-child(2) > input:nth-child(3)")).click();
                                driver.switchTo().frame("ifr_popup0");
                                driver.findElement(By.id("fileZt")).sendKeys(rootPath + trademarkCoApplicant.getSba0027());
                                driver.findElement(By.id("laodBut")).click();
                                driver.switchTo().parentFrame();
                                driver.findElement(By.id("dialogBoxClose")).click();

                                if ("0".equals(trademarkCoApplicant.getWhetherOriginaldocChinese()) || "4".equals(trademarkCoApplicant.getBookOwnerType())) {

                                    //主体资格证明文件(外文)
                                    driver.findElement(By.cssSelector("#gtFileZtEnTr > td:nth-child(2) > input:nth-child(3)")).click();
                                    driver.switchTo().frame("ifr_popup0");
                                    driver.findElement(By.cssSelector("#fileZtEn")).sendKeys(rootPath + trademarkCoApplicant.getSba0028());
                                    driver.findElement(By.cssSelector("#laodBut")).click();
                                    driver.switchTo().parentFrame();
                                    driver.findElement(By.cssSelector("#dialogBoxClose")).click();
                                }

                            } else if ("1".equals(trademarkCoApplicant.getApplicantType())) {

                                if ("4".equals(trademarkCoApplicant.getBookOwnerType())) {
                                    //共有人名称英文
                                    driver.findElement(By.cssSelector("#nameEn")).sendKeys(trademarkCoApplicant.getApplicantOwnerEnglishname());
                                }

                                //证件名称
                                String d = "00000";
                                if ("0".equals(trademarkCoApplicant.getIdName())) {
                                    d = d + 1;
                                } else if ("1".equals(trademarkCoApplicant.getIdName())) {
                                    d = d + 2;
                                } else if ("2".equals(trademarkCoApplicant.getIdName())) {
                                    d = d + 4;
                                }
                                new Select(driver.findElement(By.cssSelector("#cardName"))).selectByValue(d);

                                //证件号
                                driver.findElement(By.cssSelector("#cardId")).sendKeys(trademarkCoApplicant.getIdNumber());

                                if ("1".equals(trademarkCoApplicant.getBookOwnerType()) || "2".equals(trademarkCoApplicant.getBookOwnerType())
                                        || "3".equals(trademarkCoApplicant.getBookOwnerType())) {
                                    //证明文件原件是否为中文
                                    String ds = "gtfileIsEn";
                                    if ("0".equals(trademarkCoApplicant.getWhetherOriginaldocChinese())) {
                                        ds = ds;
                                    } else if ("1".equals(trademarkCoApplicant.getWhetherOriginaldocChinese())) {
                                        ds = ds + 1;
                                    }
                                    driver.findElement(By.id(ds)).click();
                                }

                                //身份证明文件
                                driver.findElement(By.cssSelector("#gtFileSfTr > td:nth-child(2) > input:nth-child(3)")).click();
                                driver.switchTo().frame("ifr_popup0");
                                driver.findElement(By.cssSelector("#fileSf")).sendKeys(rootPath + trademarkCoApplicant.getSba0025());
                                driver.findElement(By.cssSelector("#laodBut")).click();
                                driver.switchTo().parentFrame();
                                driver.findElement(By.cssSelector("#dialogBoxClose")).click();

                                if ("0".equals(trademarkCoApplicant.getBookOwnerType())) {
                                    //主体资格证明文件
                                    driver.findElement(By.cssSelector("#gtFileZtTr > td:nth-child(2) > input:nth-child(3)")).click();
                                    driver.switchTo().frame("ifr_popup0");
                                    driver.findElement(By.id("fileZt")).sendKeys(rootPath + trademarkCoApplicant.getSba0027());
                                    driver.findElement(By.id("laodBut")).click();
                                    driver.switchTo().parentFrame();
                                    driver.findElement(By.id("dialogBoxClose")).click();
                                }

                                if ("4".equals(trademarkCoApplicant.getBookOwnerType()) || "0".equals(trademarkCoApplicant.getWhetherOriginaldocChinese())) {
                                    //身份证明文件（外文）
                                    driver.findElement(By.cssSelector("#gtFileSfEnTr > td:nth-child(2) > input:nth-child(3)")).click();
                                    driver.switchTo().frame("ifr_popup0");
                                    driver.findElement(By.cssSelector("#fileSfEn")).sendKeys(rootPath + trademarkCoApplicant.getSba0026());
                                    driver.findElement(By.cssSelector("#laodBut")).click();
                                    driver.switchTo().parentFrame();
                                    driver.findElement(By.cssSelector("#dialogBoxClose")).click();
                                }

                            }

                            driver.findElement(By.cssSelector(".bg_04 > label:nth-child(2) > input:nth-child(1)")).click();
                            Thread.sleep(2000);

                            if (a < trademarkApplicantProduct.getTrademarkCoApplicants().size()) {
                                driver.switchTo().alert().accept();
                                Thread.sleep(6000);
                            } else {
                                driver.switchTo().alert().dismiss();
                                driver.switchTo().parentFrame();
                            }
                        }
                    }
                }
            }
        }

        //下一步
        driver.findElements(By.cssSelector("td>label:last-child input")).get(3).click();

        //=============优先权信息==============

        //优先权声明
        String pr = "priorityType";
        if ("0".equals(trademarkApplicantProduct.getPriorityStatement())) {
            pr = pr+1;
        } else if ("1".equals(trademarkApplicantProduct.getPriorityStatement())) {
            pr = pr+2;
        }else if ("2".equals(trademarkApplicantProduct.getPriorityStatement())){
            pr = pr+3;
        }
        driver.findElement(By.cssSelector("#"+pr)).click();

        if (!"0".equals(trademarkApplicantProduct.getPriorityStatement())) {

            //是否上传优先权证明文件
            String qx = "isLoadYx";
            if ("0".equals(trademarkApplicantProduct.getWhetherUploadPriorityDoc())) {
                qx = qx+1;
            } else if ("1".equals(trademarkApplicantProduct.getWhetherUploadPriorityDoc())) {
                qx = qx;
            }
            driver.findElement(By.cssSelector("#"+qx)).click();

            if ("1".equals(trademarkApplicantProduct.getWhetherUploadPriorityDoc())) {

                //优先权证明文件
                driver.findElement(By.cssSelector("#fileYxTr > td:nth-child(2) > input:nth-child(3)")).click();
                driver.switchTo().frame("ifr_popup0");
                driver.findElement(By.cssSelector("#fileYx")).sendKeys(rootPath+trademarkApplicantProduct.getSba0018());
                driver.findElement(By.cssSelector("#laodBut")).click();
                driver.switchTo().parentFrame();
                driver.findElement(By.cssSelector("#dialogBoxClose")).click();
            }

            //申请/展出国家/地区
            driver.findElement(By.cssSelector("#priorityBaseCrty")).sendKeys(trademarkApplicantProduct.getApplicationArea());

            //申请/展出日期
            driver.findElement(By.cssSelector("#priorityAppDate")).sendKeys(trademarkApplicantProduct.getApplicationData());

            if ("1".equals(trademarkApplicantProduct.getPriorityStatement())) {
                //申请号
                driver.findElement(By.cssSelector("#priorityNum")).sendKeys(trademarkApplicantProduct.getApplicationNumber());
            }
        }

        //下一步
        driver.findElements(By.cssSelector("td>label:last-child input")).get(4).click();

        //=========商品===============
        if (trademarkApplicantProduct.getTrademarkProducts().size()>0) {
            //点击添加商品/服务项目
            driver.findElement(By.cssSelector("#Layer6 > div:nth-child(3) > table:nth-child(2) > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(1) > " +
                    "table:nth-child(1) > tbody:nth-child(1) > tr:nth-child(3) > td:nth-child(1) > center:nth-child(14) > a:nth-child(1)")).click();

            String parentWindowId = driver.getWindowHandle();
            Set<String> allWindowsId = driver.getWindowHandles();
            //System.out.println("=====商品页面标识===="+allWindowsId);
            for (String id : allWindowsId){
                if (!parentWindowId.equals(id)) {
                    driver.switchTo().window(id);
                    int a = 0;
                    for (ShunchaoTrademarkProduct trademarkProduct:trademarkApplicantProduct.getTrademarkProducts()){
                        a++;

                        driver.findElement(By.cssSelector("#goodsCode")).clear();

                        driver.findElement(By.cssSelector("#goodsCode")).sendKeys(trademarkProduct.getScode());

                        driver.findElement(By.cssSelector(".button2")).click();
                        Thread.sleep(1000);

                        driver.findElement(By.cssSelector("td.c3_0:nth-child(1) > input:nth-child(1)")).click();

                        driver.findElement(By.cssSelector(".chart_list > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(1) > input:nth-child(1)")).click();

                        Thread.sleep(1000);
                        driver.switchTo().alert().accept();

                        if (a == trademarkApplicantProduct.getTrademarkProducts().size()) {
                            driver.findElement(By.cssSelector(".login_zylj > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(1) > a:nth-child(2)")).click();
                            driver.switchTo().window(parentWindowId);
                            break;
                        }
                    }
                }
            }

        }


        //下一步
        driver.switchTo().frame("myframe");
        driver.findElements(By.cssSelector("td>label:last-child input")).get(5).click();


        //================商标图样==============

        //选择图样
        String path = rootPath;
        boolean p = false;
        if ("1".equals(trademarkApplicantProduct.getWhetherColorCombination())) {
            path = path+trademarkApplicantProduct.getSba0019();
            p = true;
        } else if ("0".equals(trademarkApplicantProduct.getWhetherColorCombination())) {
            path = path+trademarkApplicantProduct.getSba0020();
        }
        driver.findElement(By.cssSelector("#Layer7 > div:nth-child(3) > table:nth-child(2) > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(1) > " +
                "table:nth-child(1) > tbody:nth-child(1) > tr:nth-child(2) > td:nth-child(2) > input:nth-child(4)")).click();
        driver.switchTo().frame("ifr_popup0");
        driver.findElement(By.cssSelector("#file1")).sendKeys(path);
        driver.findElement(By.cssSelector(".buttonhpb")).click();
        driver.switchTo().parentFrame();
        driver.findElement(By.cssSelector("#dialogBoxClose")).click();

        //黑白稿
        if (p) {
           driver.findElement(By.cssSelector("#Layer7 > div:nth-child(3) > table:nth-child(2) > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(1) > " +
                   "table:nth-child(1) > tbody:nth-child(1) > tr:nth-child(3) > td:nth-child(1) > input:nth-child(2)")).click();
           driver.switchTo().frame("ifr_popup0");
           driver.findElement(By.cssSelector("#file1")).sendKeys(rootPath+trademarkApplicantProduct.getSba0020());
           driver.findElement(By.cssSelector(".buttonhpb")).click();
           driver.switchTo().parentFrame();
           driver.findElement(By.cssSelector("#dialogBoxClose")).click();
        }

        //以肖像作为商标申请注册
        if ("1".equals(trademarkApplicantProduct.getApplicationRegistrationApplication())) {
            driver.findElement(By.cssSelector("#isPersonPhoto")).click();

            //证明文件（公正文件）
            driver.findElement(By.cssSelector("#fileTpTr > td:nth-child(1) > input:nth-child(3)")).click();
            driver.switchTo().frame("ifr_popup0");
            driver.findElement(By.cssSelector("#fileTp")).sendKeys(rootPath+trademarkApplicantProduct.getSba0021());
            driver.findElement(By.cssSelector("#laodBut")).click();
            driver.switchTo().parentFrame();
            driver.findElement(By.cssSelector("#dialogBoxClose")).click();
        }

        //有关说明文件
        if (Objects.nonNull(trademarkApplicantProduct.getSba0022())) {
            driver.findElement(By.cssSelector("#fileYgTr > td:nth-child(2) > input:nth-child(2)")).click();
            driver.switchTo().frame(driver.findElement(By.cssSelector("#dlg_upload > iframe:nth-child(1)")));
            driver.findElement(By.cssSelector("#filePdf")).sendKeys(rootPath+trademarkApplicantProduct.getSba0022());
            driver.findElement(By.cssSelector("#laodBut")).click();
            driver.switchTo().parentFrame();
            driver.findElement(By.cssSelector(".panel-tool-close")).click();
        }

        //确认
        driver.findElement(By.cssSelector("#Layer7 > div:nth-child(3) > table:nth-child(2) > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(1) > " +
                "table:nth-child(1) > tbody:nth-child(1) > tr:nth-child(8) > td:nth-child(1) > label:nth-child(2) > input:nth-child(1)")).click();


        //driver.switchTo().parentFrame();


    }

}
