package com.shunchao.cpc.controller;

import com.alibaba.fastjson.JSONObject;
import com.shunchao.cpc.model.*;
import com.shunchao.cpc.service.IShunchaoTrademarkTmsveService;
import com.shunchao.cpc.util.ChromeDriverUtils;
import com.shunchao.cpc.util.FoxDriverUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
    @PostMapping(value = "/startUpTmsve")
    public Result<?> startUpTmsve(@RequestBody ShunchaoTrademarkApplicantProduct trademarkApplicantProduct, HttpServletRequest request) {
        String mark = null;
        String alertScrollTopJs = "document.querySelector('.pop-content').scrollTop=";
        String htmlScrolltoJs = "parent.scrollTo(0,600)";
        try {
            String rootPath = System.getProperty("exe.path");
//                    String rootPath ="D:\\DUOUEXE\\duou\\";
//            String rootPath = "D:\\driver\\foxDriver\\geckodriver-v0.34.0-win64\\";
            //System.out.println("开始提交程序：=====根目录====="+rootPath);
            driver = ChromeDriverUtils.beforeM(driver,rootPath);
//            driver.manage().addCookie(new Cookie("Referer","https://wssq.sbj.cnipa.gov.cn:9443/tmsve/"));
            //driver.navigate().to(url);
            driver.get(url);
            //driver.findElement(By.id("pin")).sendKeys(pinword);
            Thread.sleep(3000);
            driver.findElement(By.id("pin")).sendKeys(trademarkApplicantProduct.getTmsvePin());
            driver.findElement(By.id("cipher")).sendKeys(trademarkApplicantProduct.getTmsveCipher());
            //driver.findElement(By.xpath("//*[@id=\"pinWord\"]")).click();
            driver.findElement(By.cssSelector("#pinWord")).click();
            Thread.sleep(5000);

            //移动弹框内滚动条
            ((JavascriptExecutor) driver).executeScript(alertScrollTopJs+1000);

            //driver.findElement(By.xpath("/html/body/div[2]/div[2]/div[1]/div/input")).click();//开发本地电脑可以
            //driver.findElement(By.xpath("//INPUT[@class=\"pop-ok pop-next\"]")).click();//ie8可用
            driver.findElement(By.xpath("//INPUT[@class=\"pop-ok pop-next\"]")).sendKeys(Keys.ENTER);
            //driver.findElement(By.cssSelector(".pop-next")).click();//开发本地电脑可以
            Thread.sleep(5000);

            ((JavascriptExecutor) driver).executeScript(alertScrollTopJs+3000);
            Thread.sleep(5000);

            //driver.findElement(By.xpath("/html/body/div[2]/div[2]/div[2]/div/input[2]")).click();//开发本地电脑可以
            driver.findElement(By.xpath("//INPUT[@class=\"pop-ok pop-close\"]")).click();//ie8可用
            //driver.findElement(By.cssSelector("input.pop-ok:nth-child(2)")).click();//开发本地电脑可以

            //菜单
            driver.findElement(By.xpath("//*[@id=\"menu\"]/UL/LI[1]/A")).click();
            //driver.findElement(By.cssSelector("#menu > ul:nth-child(1) > li:nth-child(1) > a:nth-child(1)")).click();//开发本地电脑可以
            driver.findElement(By.xpath("//*[@id=\"menu\"]/UL/LI[1]/UL/LI/A")).click();
            //driver.findElement(By.cssSelector("#menu > ul:nth-child(1) > li:nth-child(1) > ul:nth-child(2) > li:nth-child(1) > a:nth-child(1)")).click();
//            System.out.println("=============当前页面源码============="+driver.getPageSource());

            Thread.sleep(5000);
//            driver.findElement(By.xpath("/html/body/div[8]/div[2]/div[4]/a")).click();
            driver.findElement(By.xpath("/html/body/div[8]/div[2]/div[4]/a/span/span")).click();
//            driver.findElement(By.className("l-btn l-btn-small")).click();
            //System.out.println("=====当前myframe页面======"+driver.getWindowHandle());
            //System.out.println("=============当前myframe源码============="+driver.getPageSource());
            //进入iframe
            driver.switchTo().frame("myframe");

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
            driver.findElement(By.cssSelector("#fileWtTr > td:nth-child(2) > input:nth-child(5)")).click();//代理委托书上传按钮
            //driver.findElement(By.cssSelector("#fileWtTr>td.td_2 input:nth-of-type(3)")).click();//代理委托书上传按钮//开发本地电脑可以
            driver.switchTo().frame("ifr_popup0");//进入上传文件iframe
            //System.out.println("=====当前ifr_popup0页面======"+driver.getWindowHandle());
            //System.out.println("======当前ifr_popup0源码=========="+driver.getPageSource());

            if (trademarkApplicantProduct.getSba0023().size() > 0) {
                for (ShunchaoTrademarkPow trademarkPow:trademarkApplicantProduct.getSba0023()){
                    String pageSource = driver.getPageSource();
                    System.out.println(pageSource);
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
            driver.findElement(By.id("appCnName")).sendKeys(trademarkApplicantProduct.getApplicantName());

            //移动页面滚动条
            ((JavascriptExecutor) driver).executeScript(htmlScrolltoJs);
            Thread.sleep(1000);

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
                    driver.findElement(By.cssSelector("#fileSfTr > td:nth-child(2) > input:nth-child(3)")).click();
                    //driver.findElement(By.cssSelector("#fileSfTr>td.td_2 input:nth-of-type(2)")).click();//开发本地电脑可以
                    driver.switchTo().frame("ifr_popup0");
                    driver.findElement(By.id("fileSf")).sendKeys(rootPath+trademarkApplicantProduct.getSba0025());
                    driver.findElement(By.id("laodBut")).click();
                    driver.switchTo().parentFrame();
                    driver.findElement(By.id("dialogBoxClose")).click();

                }

                //主体资格证明文件(中文)
                driver.findElement(By.cssSelector("#fileZtTr > td:nth-child(2) > input:nth-child(3)")).click();
                //driver.findElement(By.cssSelector("#fileZtTr>td.td_2 input:nth-of-type(2)")).click();//开发本地电脑可以
                driver.switchTo().frame("ifr_popup0");
                driver.findElement(By.id("fileZt")).sendKeys(rootPath+trademarkApplicantProduct.getSba0027());
                driver.findElement(By.id("laodBut")).click();
                driver.switchTo().parentFrame();
                driver.findElement(By.id("dialogBoxClose")).click();


                //邮政编码（申请人）
                driver.findElement(By.id("appContactZip")).sendKeys(trademarkApplicantProduct.getPostalCode());

                //国内申请人联系地址（代理人）
                driver.findElement(By.cssSelector("#communicationAddr")).sendKeys(trademarkApplicantProduct.getApplicantContactAddress());

                //邮政编码（代理人）
                driver.findElement(By.cssSelector("#communicationZip")).sendKeys(trademarkApplicantProduct.getAgentPostalCode());

                //国内申请人电子邮箱（代理人）
                driver.findElement(By.cssSelector("#appContactEmail")).sendKeys(trademarkApplicantProduct.getApplicantEmail());

                //联系人（代理人）
                driver.findElement(By.id("appContactPerson")).sendKeys(trademarkApplicantProduct.getContactPerson());

                //联系电话（代理人）
                driver.findElement(By.id("appContactTel")).sendKeys(trademarkApplicantProduct.getContactNumber());

                if (Objects.nonNull(trademarkApplicantProduct.getFaxInAreacode())) {
                    //传真（含地区号）
                    driver.findElement(By.id("appContactFax")).sendKeys(trademarkApplicantProduct.getFaxInAreacode());
                }


            }else {

                //申请人名称(英文)
                driver.findElement(By.id("appEnName")).sendKeys(trademarkApplicantProduct.getApplicantOwnerEnglishname());

                if ("0".equals(trademarkApplicantProduct.getApplicantType())) {

                    if ("4".equals(trademarkApplicantProduct.getBookOwnerType())) {
                        //主体资格证明文件(中文)
                        driver.findElement(By.cssSelector("#fileZtTr > td:nth-child(2) > input:nth-child(3)")).click();
                        //driver.findElement(By.cssSelector("#fileZtTr>td.td_2 input:nth-of-type(2)")).click();//开发本地电脑可以
                        driver.switchTo().frame("ifr_popup0");
                        driver.findElement(By.id("fileZt")).sendKeys(rootPath+trademarkApplicantProduct.getSba0027());
                        driver.findElement(By.id("laodBut")).click();
                        driver.switchTo().parentFrame();
                        driver.findElement(By.id("dialogBoxClose")).click();

                        //主体资格证明原文件(外文)
                        driver.findElement(By.cssSelector("#fileZtEnTr > td:nth-child(2) > input:nth-child(3)")).click();
                        //driver.findElement(By.cssSelector("#fileZtEnTr>td.td_2 input:nth-of-type(2)")).click();//开发本地电脑可以
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
                            driver.findElement(By.cssSelector("#fileZtEnTr > td:nth-child(2) > input:nth-child(3)")).click();
                            //driver.findElement(By.cssSelector("#fileZtEnTr>td.td_2 input:nth-of-type(2)")).click();//开发本地电脑可以
                            driver.switchTo().frame("ifr_popup0");
                            driver.findElement(By.id("fileZtEn")).sendKeys(rootPath + trademarkApplicantProduct.getSba0028());
                            driver.findElement(By.id("laodBut")).click();
                            driver.switchTo().parentFrame();
                            driver.findElement(By.id("dialogBoxClose")).click();
                        }

                        //主体资格证明文件(中文)
                        driver.findElement(By.cssSelector("#fileZtTr > td:nth-child(2) > input:nth-child(3)")).click();
                        //driver.findElement(By.cssSelector("#fileZtTr>td.td_2 input:nth-of-type(2)")).click();//开发本地电脑可以
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
                    driver.findElement(By.cssSelector("#fileSfTr > td:nth-child(2) > input:nth-child(3)")).click();
                    //driver.findElement(By.cssSelector("#fileSfTr>td.td_2 input:nth-of-type(2)")).click();//开发本地电脑可以
                    driver.switchTo().frame("ifr_popup0");
                    driver.findElement(By.id("fileSf")).sendKeys(rootPath+trademarkApplicantProduct.getSba0025());
                    driver.findElement(By.id("laodBut")).click();
                    driver.switchTo().parentFrame();
                    driver.findElement(By.id("dialogBoxClose")).click();

                    if ("4".equals(trademarkApplicantProduct.getBookOwnerType())) {
                        //身份证明原文件(外文)
                        driver.findElement(By.cssSelector("#fileSfEnTr > td:nth-child(2) > input:nth-child(3)")).click();
                        //driver.findElement(By.cssSelector("#fileSfEnTr>td.td_2 input:nth-of-type(2)")).click();//开发本地电脑可以
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
                            driver.findElement(By.cssSelector("#fileSfEnTr > td:nth-child(2) > input:nth-child(3)")).click();
                            //driver.findElement(By.cssSelector("#fileSfEnTr>td.td_2 input:nth-of-type(2)")).click();//开发本地电脑可以
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
                driver.findElement(By.cssSelector("#menberRuleFjTr > td:nth-child(2) > input:nth-child(3)")).click();
                //driver.findElement(By.cssSelector("#menberRuleFjTr>td.td_2 input:nth-of-type(2)")).click();//开发本地电脑可以
                driver.switchTo().frame(driver.findElement(By.cssSelector("#dlg_upload>iframe")));
                driver.findElement(By.id("filePdf")).sendKeys(rootPath+trademarkApplicantProduct.getSba0001());
                driver.findElement(By.id("laodBut")).click();
                driver.switchTo().parentFrame();
                driver.findElement(By.cssSelector(".panel .panel-header .panel-tool a.panel-tool-close")).click();

                ((JavascriptExecutor) driver).executeScript(htmlScrolltoJs);
                Thread.sleep(1000);

                if ("2".equals(trademarkApplicantProduct.getTrademarkType())) {
                    //集体成员名单
                    String jss = "document.querySelector('#menberListTr>td.td_2 .ke-container .ke-edit iframe')\n" +
                            "\t\t\t\t\t.contentDocument.all[document\n" +
                            "\t\t\t\t\t\t.querySelector('#menberListTr>td.td_2 .ke-container .ke-edit iframe')\n" +
                            "\t\t\t\t\t\t.contentDocument.all.length - 1].innerHTML=\"" + trademarkApplicantProduct.getCollectiveMembers() + "\"";
                    ((JavascriptExecutor) driver).executeScript(jss);

                    //集体成员名单（附件）
                    driver.findElement(By.cssSelector("#menberListFjTr > td:nth-child(2) > input:nth-child(3)")).click();
                    //driver.findElement(By.cssSelector("#menberListFjTr>td.td_2 input:nth-of-type(2)")).click();//开发本地电脑可以
                    driver.switchTo().frame(driver.findElement(By.cssSelector("#dlg_upload>iframe")));
                    driver.findElement(By.id("filePdf")).sendKeys(rootPath + trademarkApplicantProduct.getSba0030());
                    driver.findElement(By.id("laodBut")).click();
                    driver.switchTo().parentFrame();
                    driver.findElement(By.cssSelector(".panel .panel-header .panel-tool a.panel-tool-close")).click();
                }

                if ("0".equals(trademarkApplicantProduct.getWhetherGeographicalIndication())) {
                    //附件
                    driver.findElement(By.cssSelector("#fdfjTr > td:nth-child(2) > input:nth-child(3)")).click();
                    //driver.findElement(By.cssSelector("#fdfjTr>td.td_2 input:nth-of-type(2)")).click();//开发本地电脑可以
                    driver.switchTo().frame(driver.findElement(By.cssSelector("#dlg_upload>iframe")));
                    driver.findElement(By.id("filePdf")).sendKeys(rootPath + trademarkApplicantProduct.getSba0007());
                    driver.findElement(By.id("laodBut")).click();
                    driver.switchTo().parentFrame();
                    driver.findElement(By.cssSelector(".panel .panel-header .panel-tool a.panel-tool-close")).click();
                }

                if ("1".equals(trademarkApplicantProduct.getWhetherGeographicalIndication())) {
                    //地理标志材料一
                    driver.findElement(By.cssSelector("#cpxyTr > td:nth-child(2) > input:nth-child(3)")).click();
                    //driver.findElement(By.cssSelector("#cpxyTr>td.td_2 input:nth-of-type(2)")).click();//开发本地电脑可以
                    driver.switchTo().frame(driver.findElement(By.cssSelector("#dlg_upload>iframe")));
                    driver.findElement(By.id("filePdf")).sendKeys(rootPath + trademarkApplicantProduct.getSba0002());
                    driver.findElement(By.id("laodBut")).click();
                    driver.switchTo().parentFrame();
                    driver.findElement(By.cssSelector(".panel .panel-header .panel-tool a.panel-tool-close")).click();

                    //地理标志材料二
                    driver.findElement(By.cssSelector("#zfwjTr > td:nth-child(2) > input:nth-child(3)")).click();
                    //driver.findElement(By.cssSelector("#zfwjTr>td.td_2 input:nth-of-type(2)")).click();//开发本地电脑可以
                    driver.switchTo().frame(driver.findElement(By.cssSelector("#dlg_upload>iframe")));
                    driver.findElement(By.id("filePdf")).sendKeys(rootPath + trademarkApplicantProduct.getSba0003());
                    driver.findElement(By.id("laodBut")).click();
                    driver.switchTo().parentFrame();
                    driver.findElement(By.cssSelector(".panel .panel-header .panel-tool a.panel-tool-close")).click();

                    //地理标志材料三
                    driver.findElement(By.cssSelector("#fwwjTr > td:nth-child(2) > input:nth-child(3)")).click();
                    //driver.findElement(By.cssSelector("#fwwjTr>td.td_2 input:nth-of-type(2)")).click();//开发本地电脑可以
                    driver.switchTo().frame(driver.findElement(By.cssSelector("#dlg_upload>iframe")));
                    driver.findElement(By.id("filePdf")).sendKeys(rootPath + trademarkApplicantProduct.getSba0004());
                    driver.findElement(By.id("laodBut")).click();
                    driver.switchTo().parentFrame();
                    driver.findElement(By.cssSelector(".panel .panel-header .panel-tool a.panel-tool-close")).click();

                    //地理标志材料四
                    driver.findElement(By.cssSelector("#hjrwTr > td:nth-child(2) > input:nth-child(3)")).click();
                    //driver.findElement(By.cssSelector("#hjrwTr>td.td_2 input:nth-of-type(2)")).click();//开发本地电脑可以
                    driver.switchTo().frame(driver.findElement(By.cssSelector("#dlg_upload>iframe")));
                    driver.findElement(By.id("filePdf")).sendKeys(rootPath + trademarkApplicantProduct.getSba0005());
                    driver.findElement(By.id("laodBut")).click();
                    driver.switchTo().parentFrame();
                    driver.findElement(By.cssSelector(".panel .panel-header .panel-tool a.panel-tool-close")).click();

                    //地理标志材料五
                    driver.findElement(By.cssSelector("#nljcTr > td:nth-child(2) > input:nth-child(3)")).click();
                    //driver.findElement(By.cssSelector("#nljcTr>td.td_2 input:nth-of-type(2)")).click();//开发本地电脑可以
                    driver.switchTo().frame(driver.findElement(By.cssSelector("#dlg_upload>iframe")));
                    driver.findElement(By.id("filePdf")).sendKeys(rootPath + trademarkApplicantProduct.getSba0006());
                    driver.findElement(By.id("laodBut")).click();
                    driver.switchTo().parentFrame();
                    driver.findElement(By.cssSelector(".panel .panel-header .panel-tool a.panel-tool-close")).click();

                    //申请人是否具备检测能力
                    driver.findElement(By.id("isJbjcnl" + trademarkApplicantProduct.getWhetherApplicantAbilityTest())).click();

                    if ("0".equals(trademarkApplicantProduct.getWhetherApplicantAbilityTest())) {
                        //申请人与具有检测资格的机构签署的委托检测合同（附件）
                        driver.findElement(By.cssSelector("#swhtTr > td:nth-child(2) > input:nth-child(3)")).click();
                        //driver.findElement(By.cssSelector("#swhtTr>td.td_2 input:nth-of-type(2)")).click();//开发本地电脑可以
                        driver.switchTo().frame(driver.findElement(By.cssSelector("#dlg_upload>iframe")));
                        driver.findElement(By.id("filePdf")).sendKeys(rootPath + trademarkApplicantProduct.getSba0012());
                        driver.findElement(By.id("laodBut")).click();
                        driver.switchTo().parentFrame();
                        driver.findElement(By.cssSelector(".panel .panel-header .panel-tool a.panel-tool-close")).click();

                        //受委托机构的单位法人证书（附件）
                        driver.findElement(By.cssSelector("#wtfrTr > td:nth-child(2) > input:nth-child(3)")).click();
                        //driver.findElement(By.cssSelector("#wtfrTr>td.td_2 input:nth-of-type(2)")).click();//开发本地电脑可以
                        driver.switchTo().frame(driver.findElement(By.cssSelector("#dlg_upload>iframe")));
                        driver.findElement(By.id("filePdf")).sendKeys(rootPath + trademarkApplicantProduct.getSba0013());
                        driver.findElement(By.id("laodBut")).click();
                        driver.switchTo().parentFrame();
                        driver.findElement(By.cssSelector(".panel .panel-header .panel-tool a.panel-tool-close")).click();

                        //受委托机构的资质证书（附件）
                        driver.findElement(By.cssSelector("#wtzzTr > td:nth-child(2) > input:nth-child(3)")).click();
                        //driver.findElement(By.cssSelector("#wtzzTr>td.td_2 input:nth-of-type(2)")).click();//开发本地电脑可以
                        driver.switchTo().frame(driver.findElement(By.cssSelector("#dlg_upload>iframe")));
                        driver.findElement(By.id("filePdf")).sendKeys(rootPath + trademarkApplicantProduct.getSba0014());
                        driver.findElement(By.id("laodBut")).click();
                        driver.switchTo().parentFrame();
                        driver.findElement(By.cssSelector(".panel .panel-header .panel-tool a.panel-tool-close")).click();

                        //专业检测设备清单（附件）
                        driver.findElement(By.cssSelector("#wtsbTr > td:nth-child(2) > input:nth-child(3)")).click();
                        //driver.findElement(By.cssSelector("#wtsbTr>td.td_2 input:nth-of-type(2)")).click();//开发本地电脑可以
                        driver.switchTo().frame(driver.findElement(By.cssSelector("#dlg_upload>iframe")));
                        driver.findElement(By.id("filePdf")).sendKeys(rootPath + trademarkApplicantProduct.getSba0015());
                        driver.findElement(By.id("laodBut")).click();
                        driver.switchTo().parentFrame();
                        driver.findElement(By.cssSelector(".panel .panel-header .panel-tool a.panel-tool-close")).click();

                        //专业技术人员名单（附件）
                        driver.findElement(By.cssSelector("#wtryTr > td:nth-child(2) > input:nth-child(3)")).click();
                        //driver.findElement(By.cssSelector("#wtryTr>td.td_2 input:nth-of-type(2)")).click();//开发本地电脑可以
                        driver.switchTo().frame(driver.findElement(By.cssSelector("#dlg_upload>iframe")));
                        driver.findElement(By.id("filePdf")).sendKeys(rootPath + trademarkApplicantProduct.getSba0016());
                        driver.findElement(By.id("laodBut")).click();
                        driver.switchTo().parentFrame();
                        driver.findElement(By.cssSelector(".panel .panel-header .panel-tool a.panel-tool-close")).click();
                    } else if ("1".equals(trademarkApplicantProduct.getWhetherApplicantAbilityTest())) {
                        //申请人检测资质证书（附件）
                        driver.findElement(By.cssSelector("#SqzsTr > td:nth-child(2) > input:nth-child(3)")).click();
                        //driver.findElement(By.cssSelector("#SqzsTr>td.td_2 input:nth-of-type(2)")).click();//开发本地电脑可以
                        driver.switchTo().frame(driver.findElement(By.cssSelector("#dlg_upload>iframe")));
                        driver.findElement(By.id("filePdf")).sendKeys(rootPath + trademarkApplicantProduct.getSba0008());
                        driver.findElement(By.id("laodBut")).click();
                        driver.switchTo().parentFrame();
                        driver.findElement(By.cssSelector(".panel .panel-header .panel-tool a.panel-tool-close")).click();

                        //申请人专业检测设备清单（附件）
                        driver.findElement(By.cssSelector("#SqsbTr > td:nth-child(2) > input:nth-child(3)")).click();
                        //driver.findElement(By.cssSelector("#SqsbTr>td.td_2 input:nth-of-type(2)")).click();//开发本地电脑可以
                        driver.switchTo().frame(driver.findElement(By.cssSelector("#dlg_upload>iframe")));
                        driver.findElement(By.id("filePdf")).sendKeys(rootPath + trademarkApplicantProduct.getSba0009());
                        driver.findElement(By.id("laodBut")).click();
                        driver.switchTo().parentFrame();
                        driver.findElement(By.cssSelector(".panel .panel-header .panel-tool a.panel-tool-close")).click();

                        //申请人专业技术人员名单（附件）
                        driver.findElement(By.cssSelector("#sqryTr > td:nth-child(2) > input:nth-child(3)")).click();
                        //driver.findElement(By.cssSelector("#sqryTr>td.td_2 input:nth-of-type(2)")).click();//开发本地电脑可以
                        driver.switchTo().frame(driver.findElement(By.cssSelector("#dlg_upload>iframe")));
                        driver.findElement(By.id("filePdf")).sendKeys(rootPath + trademarkApplicantProduct.getSba0010());
                        driver.findElement(By.id("laodBut")).click();
                        driver.switchTo().parentFrame();
                        driver.findElement(By.cssSelector(".panel .panel-header .panel-tool a.panel-tool-close")).click();

                        //申请人技术人员证书（附件）
                        driver.findElement(By.cssSelector("#sqjsTr > td:nth-child(2) > input:nth-child(2)")).click();
                        //driver.findElement(By.cssSelector("#sqjsTr>td.td_2 input:nth-of-type(2)")).click();//开发本地电脑可以
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
                driver.findElement(By.cssSelector("#fileSyTr > td:nth-child(2) > input:nth-child(3)")).click();
                //driver.findElement(By.cssSelector("#fileSyTr>td.td_2 input:nth-of-type(2)")).click();//开发本地电脑可以

                driver.switchTo().frame("ifr_popup0");
                driver.findElement(By.id("fileSy")).sendKeys(rootPath+trademarkApplicantProduct.getSba0017());
                driver.findElement(By.id("laodBut")).click();
                driver.switchTo().parentFrame();
                driver.findElement(By.id("dialogBoxClose")).click();
            }

            //商标名称
            driver.findElement(By.id("tmName")).sendKeys(trademarkApplicantProduct.getTrademarkName());

            ((JavascriptExecutor) driver).executeScript(htmlScrolltoJs);
            Thread.sleep(1000);

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
                                driver.findElement(By.cssSelector("#nameCn")).sendKeys(trademarkCoApplicant.getApplicantName());

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
                                    Thread.sleep(4000);
                                    driver.switchTo().alert().dismiss();
                                    driver.switchTo().window(parentWindowsId);
                                    break;
                                }
                            }
                        }
                    }
                }
                driver.switchTo().frame("myframe");

                ((JavascriptExecutor) driver).executeScript(htmlScrolltoJs);
                Thread.sleep(1000);
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
                //driver.findElement(By.cssSelector("#Layer6 > div:nth-child(3) > table:nth-child(2) > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(1) > " +
                //        "table:nth-child(1) > tbody:nth-child(1) > tr:nth-child(3) > td:nth-child(1) > center:nth-child(14) > a:nth-child(1)")).click();
                driver.findElement(By.xpath("/html/body/div/div/div[2]/div/form/div[6]/div[3]/table/tbody/tr/td/table/tbody/tr[3]/td/center/a")).click();//ie8可用

                String parentWindowId = driver.getWindowHandle();
                Set<String> allWindowsId = driver.getWindowHandles();
                //System.out.println("=====商品页面标识===="+allWindowsId);
                for (String id : allWindowsId){
                    if (!parentWindowId.equals(id)) {
                        driver.switchTo().window(id);
                        int a = 0;
                        for (ShunchaoTrademarkProduct trademarkProduct:trademarkApplicantProduct.getTrademarkProducts()){
                            a++;

                            driver.findElement(By.cssSelector("#goods")).clear();
                            driver.findElement(By.cssSelector("#goodsCode")).clear();

                            driver.findElement(By.cssSelector("#goods")).sendKeys(trademarkProduct.getSname());

                            if (Objects.nonNull(trademarkProduct.getScode())) {

                                driver.findElement(By.cssSelector("#goodsCode")).sendKeys(trademarkProduct.getScode());
                            }
                            driver.findElement(By.cssSelector(".button2")).click();
                            Thread.sleep(1000);

                            WebElement table = driver.findElement(By.cssSelector(".chart_list"));
                            List<WebElement> tableTrList = table.findElements(By.tagName("tr"));
                            int size = tableTrList.size()-3;
                            //System.out.println("======table中tr个数："+tableTrList.size());
                            if (size == 1) {

                                driver.findElement(By.cssSelector("td.c3_0:nth-child(1) > input:nth-child(1)")).click();

                                driver.findElement(By.cssSelector(".chart_list > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(1) > input:nth-child(1)")).click();

                            } else if (size == 0) {
                                log.info("======个数为："+size+"个，所以直接跳过循环");
                                throw new Exception (trademarkProduct.getSname()+"产品查询不到");
                            } else if (size > 1) {
                                ii:
                                for (int itr = 3;itr<tableTrList.size();itr++){
                                    //System.out.println("=====循环从索引："+itr+"开始");
                                    List<WebElement> tdList = tableTrList.get(itr).findElements(By.tagName("td"));
                                    jj:
                                    for (int itd=0;itd<tdList.size();itd++){
                                        //System.out.println("======循环在："+itd+"处，td值为："+tdList.get(4).getText());
                                        if (trademarkProduct.getSname().equals(tdList.get(4).getText())) {
                                            //System.out.println("=======找到对应商品名称，在tr索引为："+itr+"处");
                                            driver.findElement(By.cssSelector(".chart_list > tbody:nth-child(1) > tr:nth-child("+(itr+1)+") > td:nth-child(1) > input:nth-child(1)")).click();

                                            driver.findElement(By.cssSelector(".chart_list > tbody:nth-child(1) > tr:nth-child(1) > td:nth-child(1) > input:nth-child(1)")).click();

                                            break ii;
                                        }
                                    }
                                }
                            }

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

                driver.switchTo().frame("myframe");

                ((JavascriptExecutor) driver).executeScript(htmlScrolltoJs);
                Thread.sleep(1000);
            }


            //下一步
            driver.findElements(By.cssSelector("td>label:last-child input")).get(5).click();


            //================商标图样==============

            //选择图样
            String path = rootPath;
            boolean p = false;
            if ("1".equals(trademarkApplicantProduct.getWhetherColorCombination()) || "1".equals(trademarkApplicantProduct.getWhetherColorPattern())) {
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

            ((JavascriptExecutor) driver).executeScript(htmlScrolltoJs);
            Thread.sleep(1000);

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

            mark = "0";//执行完成
        } catch (Exception e) {
            log.info(trademarkApplicantProduct.getTrademarkName()+"向商标局发文失败",e);
            return Result.error(e.getMessage());
        }
        return Result.ok(mark);
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
    public Result<?> startDfTmsve(String workbenchId, HttpServletRequest request, HttpServletResponse response) throws InterruptedException {
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

    /**
     * 功能描述:商标转让转移提交商标局
     * 场景:
     * @Param: [trademarkAnnexList, request]
     * @Return: java.lang.String
     * @Author: zcs
     * @Date: 2023/7/04
     */
    @GetMapping(value = "/startZYTmsve")
    public Result<?> startZYTmsve(String trademarkId, HttpServletRequest request, HttpServletResponse response) throws InterruptedException {
        String token = request.getParameter("token");
        String alertScrollTopJs = "document.querySelector('.pop-content').scrollTop=";
        String htmlScrolltoJs = "parent.scrollTo(0,600)";
        //加载申请人附件数据到本地
        shunchaoTrademarkTmsveService.connectTmsveDownloadData(trademarkId, token);

        //加载申请人基本信息
        ShunchaoTrademarkTmsve shunchaoTrademarkTmsve = shunchaoTrademarkTmsveService.getSendTsvmeData(trademarkId, token);
        String rootPath = System.getProperty("exe.path");
//        String rootPath ="D:\\DUOUEXE\\duou\\";
        //System.out.println("开始提交程序：=====根目录====="+rootPath);
        driver = ChromeDriverUtils.beforeM(driver, rootPath);
        //driver.navigate().to(url);
        driver.get(url);

        //driver.findElement(By.id("pin")).sendKeys(pinword);
        driver.findElement(By.id("pin")).sendKeys(shunchaoTrademarkTmsve.getTmsvePin());
        driver.findElement(By.id("cipher")).sendKeys(shunchaoTrademarkTmsve.getTmsveCipher());
        //driver.findElement(By.xpath("//*[@id=\"pinWord\"]")).click();
        driver.findElement(By.cssSelector("#pinWord")).click();
        Thread.sleep(3000);

        //移动弹框内滚动条
        ((JavascriptExecutor) driver).executeScript(alertScrollTopJs + 1000);

        //driver.findElement(By.xpath("/html/body/div[2]/div[2]/div[1]/div/input")).click();//开发本地电脑可以
        //driver.findElement(By.xpath("//INPUT[@class=\"pop-ok pop-next\"]")).click();//ie8可用
        driver.findElement(By.xpath("//INPUT[@class=\"pop-ok pop-next\"]")).sendKeys(Keys.ENTER);
        //driver.findElement(By.cssSelector(".pop-next")).click();//开发本地电脑可以
        Thread.sleep(2000);

        ((JavascriptExecutor) driver).executeScript(alertScrollTopJs + 3000);
        Thread.sleep(2000);

        //driver.findElement(By.xpath("/html/body/div[2]/div[2]/div[2]/div/input[2]")).click();//开发本地电脑可以
        driver.findElement(By.xpath("//INPUT[@class=\"pop-ok pop-close\"]")).click();//ie8可用
        //driver.findElement(By.cssSelector("input.pop-ok:nth-child(2)")).click();//开发本地电脑可以

        //菜单
        driver.findElement(By.xpath("//*[@id=\"menu\"]/UL/LI[2]/A")).click();
        Thread.sleep(2000);
        driver.findElement(By.xpath("//*[@id=\"menu\"]/ul/li[2]/ul/li[8]/a")).click();

        Thread.sleep(5000);
//            driver.findElement(By.xpath("/html/body/div[8]/div[2]/div[4]/a")).click();
        driver.findElement(By.xpath("/html/body/div[8]/div[2]/div[4]/a/span/span")).click();

        //进入iframe
        driver.switchTo().frame("myframe");

        //选择办理业务
        WebElement el1 = driver.findElement(By.id("userType"));
        Select sel1 = new Select(el1);
        if ("1".equals(shunchaoTrademarkTmsve.getTransferType())) {
            sel1.selectByValue("1");
        } else {
            sel1.selectByValue("2");
        }

        ShunchaoTrademarkApplicant applicant1 = shunchaoTrademarkTmsve.getApplicant1();//转让人
        ShunchaoTrademarkApplicant applicant2 = shunchaoTrademarkTmsve.getApplicant2();//转让人

        //转让人国籍
        WebElement el2 = driver.findElement(By.id("assigneeGjdq"));
        Select sel2 = new Select(el2);
        if ("0".equals(applicant1.getBookOwnerType())) {
            sel2.selectByValue("100011000000000001");
        } else if ("4".equals(applicant1.getBookOwnerType())) {
            sel2.selectByValue("100011000000000002");
        }else if ("1".equals(applicant1.getBookOwnerType())) {
            sel2.selectByValue("100011000000000003");
        }else if ("2".equals(applicant1.getBookOwnerType())) {
            sel2.selectByValue("100011000000000004");
        }else if ("3".equals(applicant1.getBookOwnerType())) {
            sel2.selectByValue("100011000000000005");
        }

        if ("4".equals(applicant2.getBookOwnerType())){//国外
            //国家和地区
            new Select(driver.findElement(By.id("assignorCrtyId"))).selectByValue(applicant2.getCountryArea());
        }

        //转让人类型
        WebElement el3 = driver.findElement(By.id("zdllx"));
        Select sel3 = new Select(el3);
        if ("0".equals(applicant1.getApplicantType())) {
            sel3.selectByValue("2");
        } else if ("1".equals(applicant1.getApplicantType())) {
            sel3.selectByValue("1");
        }else {
            sel3.selectByValue("3");
        }

        //转让人名称(中文)
        driver.findElement(By.id("assigneeCnName")).sendKeys(applicant1.getApplicantName());

        //转让人统一社会信用代码
        if (Objects.nonNull(applicant1.getUnifiedSocialCreditcode())) {
            driver.findElement(By.id("zrCertCode")).sendKeys(applicant1.getUnifiedSocialCreditcode());
        }

        //转让人名称(英文)
        if (Objects.nonNull(applicant1.getApplicantOwnerEnglishname())) {
            driver.findElement(By.id("assigneeEnName")).sendKeys(applicant1.getApplicantOwnerEnglishname());
        }
        //转让人地址(中文)
        driver.findElement(By.id("assigneeCnAddr")).sendKeys(applicant1.getApplicationAddres());

        //转让人地址(英文)
        if (Objects.nonNull(applicant1.getApplicationAddressEnglish())) {
            driver.findElement(By.id("assigneeEnAddr")).sendKeys(applicant1.getApplicationAddressEnglish());
        }

        //受让人国籍
        WebElement el4 = driver.findElement(By.id("appGjdq"));
        Select sel4 = new Select(el4);
        if ("0".equals(applicant2.getBookOwnerType())) {
            sel4.selectByValue("100011000000000001");
        } else if ("4".equals(applicant2.getBookOwnerType())) {
            sel4.selectByValue("100011000000000002");
        }else if ("1".equals(applicant2.getBookOwnerType())) {
            sel4.selectByValue("100011000000000003");
        }else if ("2".equals(applicant2.getBookOwnerType())) {
            sel4.selectByValue("100011000000000004");
        }else if ("3".equals(applicant2.getBookOwnerType())) {
            sel4.selectByValue("100011000000000005");
        }



        //受让人类型
        WebElement el5 = driver.findElement(By.id("sdllx"));
        Select sel5 = new Select(el5);
        if ("0".equals(applicant2.getApplicantType())) {
            sel5.selectByValue("2");
        } else if ("1".equals(applicant2.getApplicantType())) {
            sel5.selectByValue("1");
        }else {
            sel5.selectByValue("3");
        }

        //受让人名称(中文)
        driver.findElement(By.id("assignorCnName")).sendKeys(applicant2.getApplicantName());

        //受让人统一社会信用代码
        if (Objects.nonNull(applicant2.getUnifiedSocialCreditcode())) {
            driver.findElement(By.id("srCertCode")).sendKeys(applicant2.getUnifiedSocialCreditcode());
        }

        //受让人名称(英文)
        if (Objects.nonNull(applicant2.getApplicantOwnerEnglishname())) {
            driver.findElement(By.id("assignorEnName")).sendKeys(applicant2.getApplicantOwnerEnglishname());
        }
        //受让人地址(中文)
        driver.findElement(By.id("assignorCnAddr")).sendKeys(applicant2.getApplicationAddres());

        //受让人地址(英文)
        if (Objects.nonNull(applicant2.getApplicationAddressEnglish())) {
            driver.findElement(By.id("assignorEnAddr")).sendKeys(applicant2.getApplicationAddressEnglish());
        }

        //受让人邮政编码
        if (Objects.nonNull(applicant2.getPostalCode())) {
            driver.findElement(By.id("assigneeContactZip")).sendKeys(applicant2.getPostalCode());
        }

        if ("0".equals(applicant2.getBookOwnerType())){
            //国内受让人联系地址
            driver.findElement(By.id("communicationAddr")).sendKeys(applicant2.getRecipientAddress());

            //国内受让人邮政编码
            driver.findElement(By.cssSelector("#communicationZip")).sendKeys(applicant2.getRecipientPostcode());

            //国内受让人电子邮箱
            driver.findElement(By.cssSelector("#appContactEmail")).sendKeys(applicant2.getRecipientEmail());

            //联系人
            driver.findElement(By.id("assigneeContactPerson")).sendKeys(applicant2.getContactName());

            //联系电话
            driver.findElement(By.id("assigneeContactTel")).sendKeys(applicant2.getContactPhone());
        }else{
            if ("4".equals(applicant2.getBookOwnerType())) {
                //国家和地区
                new Select(driver.findElement(By.id("assignorCrtyId"))).selectByValue(applicant2.getCountryArea());
            }

            //国内接收人名称
            driver.findElement(By.id("acceptPerson")).sendKeys(applicant2.getApplicationMainlandRecipientname());

            //国内接收人地址
            driver.findElement(By.id("acceptAddr")).sendKeys(applicant2.getRecipientAddress());

            //国内接收人邮政编码
            driver.findElement(By.id("acceptZip")).sendKeys(applicant2.getRecipientPostcode());
        }

        //代理文号
        if (Objects.nonNull(shunchaoTrademarkTmsve.getAgentNumber())) {
            driver.findElement(By.id("agentFilenum")).sendKeys(shunchaoTrademarkTmsve.getAgentNumber());
        }

        //代理人姓名
        driver.findElement(By.id("agentPerson")).sendKeys(shunchaoTrademarkTmsve.getAgentName());

        //转让人委托书
        driver.findElement(By.xpath("//*[@id=\"zrsrwts\"]/td[2]/input[4]")).click();//转让人委托书上传按钮
        driver.switchTo().frame("ifr_popup0");//进入上传文件iframe

        driver.findElement(By.id("file1")).sendKeys(rootPath+shunchaoTrademarkTmsve.getSba0034());
        driver.findElement(By.className("buttonhpb")).click();

        driver.switchTo().parentFrame();//回到上一个iframe
        driver.findElement(By.id("dialogBoxClose")).click();

        //受让人委托书
        driver.findElement(By.xpath("//*[@id=\"zrsrwts2\"]/td[2]/input[4]")).click();//受让人委托书上传按钮
        driver.switchTo().frame("ifr_popup0");//进入上传文件iframe

        driver.findElement(By.id("file1")).sendKeys(rootPath+shunchaoTrademarkTmsve.getSba0035());
        driver.findElement(By.className("buttonhpb")).click();

        driver.switchTo().parentFrame();//回到上一个iframe
        driver.findElement(By.id("dialogBoxClose")).click();

        //转让人上传文件的语言类型
        WebElement el6 = driver.findElement(By.id("zwjlx"));
        Select sel6 = new Select(el6);
        if(applicant1.getLanguageType().equals("1")){
            sel6.selectByValue("1");
        }else{
            sel6.selectByValue("2");
        }

        //受让人上传文件的语言类型
        WebElement el7 = driver.findElement(By.id("swjlx"));
        Select sel7 = new Select(el7);
        if(applicant2.getLanguageType().equals("1")){
            sel7.selectByValue("1");
        }else{
            sel7.selectByValue("2");
        }

        //转让人上传文件
        //自然人死亡/企业或其他组织注销证明
        if(Objects.nonNull(applicant1.getSba0027())){
            driver.findElement(By.xpath("//*[@id=\"gqcxTr\"]/td[2]/input[2]")).click();
            WebElement iframe = driver.findElement(By.xpath("//*[@id=\"dlg_upload\"]/iframe"));
            driver.switchTo().frame(iframe);//进入上传文件iframe
            driver.findElement(By.id("filePdf")).sendKeys(rootPath+applicant1.getSba0033());
            driver.findElement(By.id("laodBut")).click();

            driver.switchTo().parentFrame();//回到上一个iframe
            driver.findElement(By.xpath("/html/body/div[8]/div[1]/div[2]/a")).click();

        }

        //同意转让声明或商标移转证明
        driver.findElement(By.xpath("//*[@id=\"wsOrzyzm\"]/td[2]/input[2]")).click();
        WebElement iframe4 = driver.findElement(By.xpath("//*[@id=\"dlg_upload\"]/iframe"));//
        driver.switchTo().frame(iframe4);//进入上传文件iframe
        driver.findElement(By.id("filePdf")).sendKeys(rootPath+applicant1.getSba0032());
        driver.findElement(By.id("laodBut")).click();

        driver.switchTo().parentFrame();//回到上一个iframe
        driver.findElement(By.xpath("/html/body/div[8]/div[1]/div[2]/a")).click();

        if (applicant1.getApplicantType().equals("0")){//法人或其他组织
            //转让人主体资格证明文件(中文)
            driver.findElement(By.xpath("//*[@id=\"zrZtCnTr\"]/td[2]/input[2]")).click();
            WebElement iframe1 = driver.findElement(By.xpath("//*[@id=\"dlg_upload\"]/iframe"));//
            driver.switchTo().frame(iframe1);//进入上传文件iframe
            driver.findElement(By.id("filePdf")).sendKeys(rootPath+applicant1.getSba0027());
            driver.findElement(By.id("laodBut")).click();

            driver.switchTo().parentFrame();//回到上一个iframe
            driver.findElement(By.xpath("/html/body/div[8]/div[1]/div[2]/a")).click();

            if(applicant1.getLanguageType().equals("2")){
                //转让人主体资格证明文件(外文)
                driver.findElement(By.xpath("//*[@id=\"zrZtEnTr\"]/td[2]/input[2]")).click();
                WebElement iframe2 = driver.findElement(By.xpath("//*[@id=\"dlg_upload\"]/iframe"));//
                driver.switchTo().frame(iframe2);//进入上传文件iframe
                driver.findElement(By.id("filePdf")).sendKeys(rootPath+applicant1.getSba0027());
                driver.findElement(By.id("laodBut")).click();

                driver.switchTo().parentFrame();//回到上一个iframe
                driver.findElement(By.xpath("/html/body/div[8]/div[1]/div[2]/a")).click();
            }
        }else{//自然人,无营业执照自然人
            //转让人证件名称
            WebElement el8 = driver.findElement(By.id("certType"));
            Select sel8 = new Select(el8);
            if ("0".equals(applicant1.getIdName())) {
                sel8.selectByValue("200005000400000000");
            } else if ("1".equals(applicant1.getIdName())) {
                sel8.selectByValue("200005000500000000");
            }else if ("2".equals(applicant1.getIdName())) {
                sel8.selectByValue("200005002100000000");
            }
            driver.findElement(By.id("certNo")).sendKeys(applicant1.getIdNumber());

            //转让人身份证明文件(中文)
            driver.findElement(By.xpath("//*[@id=\"zrSfCnTr\"]/td[2]/input[2]")).click();
            WebElement iframe1 = driver.findElement(By.xpath("//*[@id=\"dlg_upload\"]/iframe"));//
            driver.switchTo().frame(iframe1);//进入上传文件iframe
            driver.findElement(By.id("filePdf")).sendKeys(rootPath+applicant1.getSba0027());
            driver.findElement(By.id("laodBut")).click();

            driver.switchTo().parentFrame();//回到上一个iframe
            driver.findElement(By.xpath("/html/body/div[8]/div[1]/div[2]/a")).click();
            if(applicant1.getLanguageType().equals("1")){
                //转让人主体资格证明文件(中文)
                driver.findElement(By.xpath("//*[@id=\"zrZtCnTr\"]/td[2]/input[2]")).click();
                WebElement iframe3 = driver.findElement(By.xpath("//*[@id=\"dlg_upload\"]/iframe"));
                driver.switchTo().frame(iframe3);//进入上传文件iframe
                driver.findElement(By.id("filePdf")).sendKeys(rootPath+applicant1.getSba0027());
                driver.findElement(By.id("laodBut")).click();

                driver.switchTo().parentFrame();//回到上一个iframe
                driver.findElement(By.xpath("/html/body/div[8]/div[1]/div[2]/a")).click();
            }else{
                //转让人身份证明文件(外文)
                driver.findElement(By.xpath("//*[@id=\"zrSfEnTr\"]/td[2]/input[2]")).click();
                WebElement iframe3 = driver.findElement(By.xpath("//*[@id=\"dlg_upload\"]/iframe"));
                driver.switchTo().frame(iframe3);//进入上传文件iframe
                driver.findElement(By.id("filePdf")).sendKeys(rootPath+applicant1.getSba0027());
                driver.findElement(By.id("laodBut")).click();

                driver.switchTo().parentFrame();//回到上一个iframe
                driver.findElement(By.xpath("/html/body/div[8]/div[1]/div[2]/a")).click();
            }
        }

        //受让人上传文件
        if (applicant2.getApplicantType().equals("0")){//法人或其他组织
            //受让人主体资格证明文件(中文)
            driver.findElement(By.xpath("//*[@id=\"srZtCnTr\"]/td[2]/input[2]")).click();
            WebElement iframe1 = driver.findElement(By.xpath("//*[@id=\"dlg_upload\"]/iframe"));//
            driver.switchTo().frame(iframe1);//进入上传文件iframe
            driver.findElement(By.id("filePdf")).sendKeys(rootPath+applicant2.getSba0027());
            driver.findElement(By.id("laodBut")).click();

            driver.switchTo().parentFrame();//回到上一个iframe
            driver.findElement(By.xpath("/html/body/div[8]/div[1]/div[2]/a")).click();

            if(applicant2.getLanguageType().equals("2")){
                //转让人主体资格证明文件(外文)
                driver.findElement(By.xpath("//*[@id=\"srZtEnTr\"]/td[2]/input[2]")).click();
                WebElement iframe2 = driver.findElement(By.xpath("//*[@id=\"dlg_upload\"]/iframe"));//
                driver.switchTo().frame(iframe2);//进入上传文件iframe
                driver.findElement(By.id("filePdf")).sendKeys(rootPath+applicant2.getSba0027());
                driver.findElement(By.id("laodBut")).click();

                driver.switchTo().parentFrame();//回到上一个iframe
                driver.findElement(By.xpath("/html/body/div[8]/div[1]/div[2]/a")).click();
            }
        }else{//自然人,无营业执照自然人
            //受让人证件名称
            WebElement el9 = driver.findElement(By.id("scertType"));
            Select sel9 = new Select(el9);
            if ("0".equals(applicant2.getIdName())) {
                sel9.selectByValue("200005000400000000");
            } else if ("1".equals(applicant2.getIdName())) {
                sel9.selectByValue("200005000500000000");
            }else if ("2".equals(applicant2.getIdName())) {
                sel9.selectByValue("200005002100000000");
            }
            driver.findElement(By.id("scertNo")).sendKeys(applicant2.getIdNumber());

            //受让人身份证明文件(中文)
            driver.findElement(By.xpath("//*[@id=\"srSfCnTr\"]/td[2]/input[2]")).click();
            WebElement iframe1 = driver.findElement(By.xpath("//*[@id=\"dlg_upload\"]/iframe"));//
            driver.switchTo().frame(iframe1);//进入上传文件iframe
            driver.findElement(By.id("filePdf")).sendKeys(rootPath+applicant2.getSba0025());
            driver.findElement(By.id("laodBut")).click();

            driver.switchTo().parentFrame();//回到上一个iframe
            driver.findElement(By.xpath("/html/body/div[8]/div[1]/div[2]/a")).click();
            if(applicant2.getLanguageType().equals("1")){
                //受让人主体资格证明文件(中文)
                driver.findElement(By.xpath("//*[@id=\"srZtCnTr\"]/td[2]/input[2]")).click();
                WebElement iframe3 = driver.findElement(By.xpath("//*[@id=\"dlg_upload\"]/iframe"));
                driver.switchTo().frame(iframe3);//进入上传文件iframe
                driver.findElement(By.id("filePdf")).sendKeys(rootPath+applicant2.getSba0027());
                driver.findElement(By.id("laodBut")).click();

                driver.switchTo().parentFrame();//回到上一个iframe
                driver.findElement(By.xpath("/html/body/div[8]/div[1]/div[2]/a")).click();
            }else{
                //受让人身份证明文件(外文)
                driver.findElement(By.xpath("//*[@id=\"srSfEnTr\"]/td[2]/input[2]")).click();
                WebElement iframe3 = driver.findElement(By.xpath("//*[@id=\"dlg_upload\"]/iframe"));
                driver.switchTo().frame(iframe3);//进入上传文件iframe
                driver.findElement(By.id("filePdf")).sendKeys(rootPath+applicant2.getSba0026());
                driver.findElement(By.id("laodBut")).click();

                driver.switchTo().parentFrame();//回到上一个iframe
                driver.findElement(By.xpath("/html/body/div[8]/div[1]/div[2]/a")).click();
            }
        }

        //商标类型
        driver.findElement(By.id("radio_p")).click();//radio_p:普通商标,radio_j:集体商标 radio_z:证明商标

        if ("0".equals(shunchaoTrademarkTmsve.getWhetherApplyJointly())) {
            driver.findElement(By.id("ifShareTm2")).click();
        } else if ("1".equals(shunchaoTrademarkTmsve.getWhetherApplyJointly())) {
            driver.findElement(By.id("ifShareTm1")).click();

            //共有人知情转让转移证明
            driver.findElement(By.xpath("//*[@id=\"gtzqTr\"]/td[2]/input[2]")).click();
            WebElement iframe3 = driver.findElement(By.xpath("//*[@id=\"dlg_upload\"]/iframe"));
            driver.switchTo().frame(iframe3);//进入上传文件iframe
            driver.findElement(By.id("filePdf")).sendKeys(rootPath+applicant2.getSba0026());
            driver.findElement(By.id("laodBut")).click();

            driver.switchTo().parentFrame();//回到上一个iframe
            driver.findElement(By.xpath("/html/body/div[8]/div[1]/div[2]/a")).click();

            driver.findElement(By.xpath("//*[@id=\"gtInfoTr\"]/td[2]/a")).click();

            String parentWindowsId = driver.getWindowHandle();
            System.out.println(parentWindowsId);
            Set<String> allWindowsId = driver.getWindowHandles();
            System.out.println(allWindowsId);
            for (String id : allWindowsId){
                if (!parentWindowsId.equals(id)) {

                    driver.switchTo().window(id);
                    List<ShunchaoTrademarkApplicant> trademarkCoApplicants = shunchaoTrademarkTmsve.getTrademarkCoApplicants();
                    int a = 0;
                    for(ShunchaoTrademarkApplicant shunchaoTrademarkApplicant:trademarkCoApplicants){
                        a++;
                        driver.findElement(By.xpath("/html/body/div[2]/div[2]/div[4]/a/span/span")).click();

                        if ("1".equals(shunchaoTrademarkApplicant.getTransferorFlag())) {
                            driver.findElement(By.id("expand1")).click();
                        } else {
                            driver.findElement(By.id("expand2")).click();
                        }

                        driver.findElement(By.id("appCnName")).sendKeys(shunchaoTrademarkApplicant.getApplicantName());//共有人名称(中文)
                        if (Objects.nonNull(shunchaoTrademarkApplicant.getApplicantOwnerEnglishname())) {
                            driver.findElement(By.id("appEnName")).sendKeys(shunchaoTrademarkApplicant.getApplicantOwnerEnglishname());//共有人名称(英文)
                        }
                        if (Objects.nonNull(shunchaoTrademarkApplicant.getApplicationAddres())) {
                            driver.findElement(By.id("appCnAddr")).sendKeys(shunchaoTrademarkApplicant.getApplicationAddres());//共有人地址(中文)
                        }
                        if (Objects.nonNull(shunchaoTrademarkApplicant.getApplicationAddressEnglish())) {
                            driver.findElement(By.id("appEnAddr")).sendKeys(shunchaoTrademarkApplicant.getApplicationAddressEnglish());//共有人地址(英文)
                        }
                        //共有人类型
                        WebElement el10 = driver.findElement(By.id("appTypeId"));
                        Select sel10 = new Select(el10);
                        if ("0".equals(shunchaoTrademarkApplicant.getApplicantType())) {
                            sel10.selectByValue("1");
                        }else {
                            sel10.selectByValue("0");
                        }

                        //上传文件的语言类型
                        WebElement el11 = driver.findElement(By.id("scwjId"));
                        Select sel11 = new Select(el11);
                        if(shunchaoTrademarkApplicant.getLanguageType().equals("1")){
                            sel11.selectByValue("0");
                        }else{
                            sel11.selectByValue("1");
                        }

                        if (shunchaoTrademarkApplicant.getApplicantType().equals("0")){//法人或其他组织
                            //共有人主体资格证明文件(中文)
                            driver.findElement(By.xpath("//*[@id=\"fileZtTr\"]/td[2]/input[2]")).click();
                            WebElement iframe1 = driver.findElement(By.xpath("//*[@id=\"dlg_upload\"]/iframe"));
                            driver.switchTo().frame(iframe1);//进入上传文件iframe
                            driver.findElement(By.id("filePdf")).sendKeys(rootPath+shunchaoTrademarkApplicant.getSba0027());
                            driver.findElement(By.id("laodBut")).click();

                            driver.switchTo().parentFrame();//回到上一个iframe
                            driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[2]/a")).click();

                            if(shunchaoTrademarkApplicant.getLanguageType().equals("2")){
                                //共有人主体资格证明文件(外文)
                                driver.findElement(By.xpath("//*[@id=\"fileZtEnTr\"]/td[2]/input[2]")).click();
                                WebElement iframe2 = driver.findElement(By.xpath("//*[@id=\"dlg_upload\"]/iframe"));
                                driver.switchTo().frame(iframe2);//进入上传文件iframe
                                driver.findElement(By.id("filePdf")).sendKeys(rootPath+shunchaoTrademarkApplicant.getSba0027());
                                driver.findElement(By.id("laodBut")).click();

                                driver.switchTo().parentFrame();//回到上一个iframe
                                driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[2]/a")).click();
                            }
                        }else{//自然人,无营业执照自然人

                            //共有人身份证明文件(中文)
                            driver.findElement(By.xpath("//*[@id=\"fileSfTr\"]/td[2]/input[2]")).click();
                            WebElement iframe1 = driver.findElement(By.xpath("//*[@id=\"dlg_upload\"]/iframe"));
                            driver.switchTo().frame(iframe1);//进入上传文件iframe
                            driver.findElement(By.id("filePdf")).sendKeys(rootPath+shunchaoTrademarkApplicant.getSba0027());
                            driver.findElement(By.id("laodBut")).click();

                            driver.switchTo().parentFrame();//回到上一个iframe
                            driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[2]/a")).click();
                            if(shunchaoTrademarkApplicant.getLanguageType().equals("1")){
                                //共有人主体资格证明文件(中文)
                                driver.findElement(By.xpath("//*[@id=\"fileZtTr\"]/td[2]/input[2]")).click();
                                WebElement iframe5 = driver.findElement(By.xpath("//*[@id=\"dlg_upload\"]/iframe"));
                                driver.switchTo().frame(iframe5);//进入上传文件iframe
                                driver.findElement(By.id("filePdf")).sendKeys(rootPath+shunchaoTrademarkApplicant.getSba0027());
                                driver.findElement(By.id("laodBut")).click();

                                driver.switchTo().parentFrame();//回到上一个iframe
                                driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[2]/a")).click();
                            }else{
                                //共有人身份证明文件(外文)
                                driver.findElement(By.xpath("//*[@id=\"fileSfEnTr\"]/td[2]/input[2]")).click();
                                WebElement iframe5 = driver.findElement(By.xpath("//*[@id=\"dlg_upload\"]/iframe"));
                                driver.switchTo().frame(iframe5);//进入上传文件iframe
                                driver.findElement(By.id("filePdf")).sendKeys(rootPath+shunchaoTrademarkApplicant.getSba0027());
                                driver.findElement(By.id("laodBut")).click();

                                driver.switchTo().parentFrame();//回到上一个iframe
                                driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[2]/a")).click();
                            }
                        }

                        //确认提交
                        driver.findElement(By.xpath("//*[@id=\"form1\"]/div/label[1]/input")).click();
                        Thread.sleep(2000);

                        if (a < shunchaoTrademarkTmsve.getTrademarkCoApplicants().size()) {
                            driver.switchTo().alert().accept();
                            Thread.sleep(3000);
                        } else {
                            driver.switchTo().alert().dismiss();
                            driver.switchTo().window(parentWindowsId);
                            Thread.sleep(3000);
                            break;
                        }

                    }

                }
            }
            driver.switchTo().frame("myframe");

            ((JavascriptExecutor) driver).executeScript(htmlScrolltoJs);
            Thread.sleep(1000);
        }
        //商标注册号,手动输入
        driver.findElement(By.xpath("//*[@id=\"tmzrsq\"]/tbody/tr[69]/td[2]/div/a[2]")).click();
        driver.findElement(By.id("regnum1")).sendKeys(shunchaoTrademarkTmsve.getRegistrationNumber());
        driver.findElement(By.id("ad")).click();
        driver.switchTo().alert().dismiss();

        if (Objects.nonNull(shunchaoTrademarkTmsve.getSba0022())) {
            //有关说明文件(外文)
            driver.findElement(By.xpath("//*[@id=\"fileYgTr\"]/td[2]/input[2]")).click();
            WebElement iframe3 = driver.findElement(By.xpath("//*[@id=\"dlg_upload\"]/iframe"));
            driver.switchTo().frame(iframe3);//进入上传文件iframe
            driver.findElement(By.id("filePdf")).sendKeys(rootPath+shunchaoTrademarkTmsve.getSba0022());
            driver.findElement(By.id("laodBut")).click();

            driver.switchTo().parentFrame();//回到上一个iframe
            driver.findElement(By.xpath("/html/body/div[8]/div[1]/div[2]/a")).click();
        }

        return Result.ok();
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
        try{


        String token = request.getParameter("token");
        String alertScrollTopJs = "document.querySelector('.pop-content').scrollTop=";
        String htmlScrolltoJs = "parent.scrollTo(0,600)";
        //加载申请人附件数据到本地
        shunchaoTrademarkTmsveService.connectTmsveDownloadData(trademarkId, token);

        //加载申请人基本信息
        ShunchaoTrademarkTmsve shunchaoTrademarkTmsve = shunchaoTrademarkTmsveService.getSendTsvmeData(trademarkId, token);
        String rootPath = System.getProperty("exe.path");
//        String rootPath ="D:\\DUOUEXE\\duou\\";
//        String rootPath = "D:\\driver\\foxDriver\\geckodriver-v0.34.0-win64\\";
        //System.out.println("开始提交程序：=====根目录====="+rootPath);
        driver = ChromeDriverUtils.beforeM(driver, rootPath);
        //driver.navigate().to(url);
        driver.get(url);
        Thread.sleep(3000);

        //driver.findElement(By.id("pin")).sendKeys(pinword);
        driver.findElement(By.id("pin")).sendKeys(shunchaoTrademarkTmsve.getTmsvePin());
        driver.findElement(By.id("cipher")).sendKeys(shunchaoTrademarkTmsve.getTmsveCipher());
        //driver.findElement(By.xpath("//*[@id=\"pinWord\"]")).click();
        driver.findElement(By.id("pinWord")).click();
        Thread.sleep(3000);

        //移动弹框内滚动条
        ((JavascriptExecutor) driver).executeScript(alertScrollTopJs + 1000);

        //driver.findElement(By.xpath("/html/body/div[2]/div[2]/div[1]/div/input")).click();//开发本地电脑可以
        //driver.findElement(By.xpath("//INPUT[@class=\"pop-ok pop-next\"]")).click();//ie8可用
        driver.findElement(By.xpath("//INPUT[@class=\"pop-ok pop-next\"]")).sendKeys(Keys.ENTER);
        //driver.findElement(By.cssSelector(".pop-next")).click();//开发本地电脑可以
        Thread.sleep(2000);

        ((JavascriptExecutor) driver).executeScript(alertScrollTopJs + 3000);
        Thread.sleep(2000);

        //driver.findElement(By.xpath("/html/body/div[2]/div[2]/div[2]/div/input[2]")).click();//开发本地电脑可以
        driver.findElement(By.xpath("//INPUT[@class=\"pop-ok pop-close\"]")).click();//ie8可用
        //driver.findElement(By.cssSelector("input.pop-ok:nth-child(2)")).click();//开发本地电脑可以

        //菜单
        driver.findElement(By.xpath("//*[@id=\"menu\"]/UL/LI[2]/A")).click();
        Thread.sleep(2000);
        driver.findElement(By.xpath("//*[@id=\"menu\"]/ul/li[2]/ul/li[2]/a")).click();

        Thread.sleep(5000);
//            driver.findElement(By.xpath("/html/body/div[8]/div[2]/div[4]/a")).click();
        driver.findElement(By.xpath("/html/body/div[8]/div[2]/div[4]/a/span/span")).click();

        //进入iframe
        driver.switchTo().frame("myframe");

        ShunchaoTrademarkApplicant applicant1 = shunchaoTrademarkTmsve.getApplicant2();//变更后申请人
        ShunchaoTrademarkApplicant applicant2 = shunchaoTrademarkTmsve.getApplicant1();//变更前申请人

        //变更后申请人国籍
        WebElement el2 = driver.findElement(By.id("appGjdq"));
        Select sel2 = new Select(el2);
        if ("0".equals(applicant1.getBookOwnerType())) {
            sel2.selectByValue("100011000000000001");
        } else if ("4".equals(applicant1.getBookOwnerType())) {
            sel2.selectByValue("100011000000000002");
        }else if ("1".equals(applicant1.getBookOwnerType())) {
            sel2.selectByValue("100011000000000003");
        }else if ("2".equals(applicant1.getBookOwnerType())) {
            sel2.selectByValue("100011000000000004");
        }else if ("3".equals(applicant1.getBookOwnerType())) {
            sel2.selectByValue("100011000000000005");
        }

        if ("4".equals(applicant1.getBookOwnerType())){//国外
            //国家和地区
            new Select(driver.findElement(By.id("appCrtyId"))).selectByValue(applicant1.getCountryArea());
        }

        //变更后申请人类型
        WebElement el3 = driver.findElement(By.id("appTypeId"));
        Select sel3 = new Select(el3);
        if ("0".equals(applicant1.getApplicantType())) {
            sel3.selectByValue("1");
        } else if ("1".equals(applicant1.getApplicantType())) {
            sel3.selectByValue("0");
        }else {
            sel3.selectByValue("2");
        }

        if(shunchaoTrademarkTmsve.getChangeType() == 1){
            driver.findElement(By.id("radio_m")).click();
        }else if (shunchaoTrademarkTmsve.getChangeType() == 2) {
            driver.findElement(By.id("radio_d")).click();
        }else {
            driver.findElement(By.id("radio_md")).click();
        }
        //变更后申请人名称(中文)
        driver.findElement(By.id("txt_sqrmyzw")).sendKeys(applicant1.getApplicantName());

        //变更后申请人统一社会信用代码
        if (Objects.nonNull(applicant1.getUnifiedSocialCreditcode())) {
            driver.findElement(By.id("certCode")).sendKeys(applicant1.getUnifiedSocialCreditcode());
        }

        //变更后申请人名称(英文)
        if (Objects.nonNull(applicant1.getApplicantOwnerEnglishname())) {
            driver.findElement(By.id("txt_sqrmyyw")).sendKeys(applicant1.getApplicantOwnerEnglishname());
        }
        //变更后申请人地址(中文)
        driver.findElement(By.id("txt_sqrdzzw")).sendKeys(applicant1.getApplicationAddres());

        //变更后申请人地址(英文)
        if (Objects.nonNull(applicant1.getApplicationAddressEnglish())) {
            driver.findElement(By.id("txt_sqrdzyw")).sendKeys(applicant1.getApplicationAddressEnglish());
        }

        //变更后申请人邮政编码
        if (Objects.nonNull(applicant1.getPostalCode())) {
            driver.findElement(By.id("txt_yzbm")).sendKeys(applicant1.getPostalCode());
        }

        if ("0".equals(applicant1.getBookOwnerType())){
            //国内申请人联系地址
            driver.findElement(By.id("communicationAddr")).sendKeys(applicant1.getRecipientAddress());

            //国内申请人邮政编码
            driver.findElement(By.cssSelector("#communicationZip")).sendKeys(applicant1.getRecipientPostcode());

            //国内申请人电子邮箱
            driver.findElement(By.cssSelector("#appContactEmail")).sendKeys(applicant1.getRecipientEmail());
        }
        //联系人
        if (Objects.nonNull(applicant1.getContactName())) {
            driver.findElement(By.id("txt_lxr")).sendKeys(applicant1.getContactName());
        }
        //联系电话
        if (Objects.nonNull(applicant1.getContactPhone())) {
            driver.findElement(By.id("txt_dh")).sendKeys(applicant1.getContactPhone());
        }

        //代理文号
        if (Objects.nonNull(shunchaoTrademarkTmsve.getAgentNumber())) {
            driver.findElement(By.id("agentFilenum")).sendKeys(shunchaoTrademarkTmsve.getAgentNumber());
        }

        //代理人姓名
        driver.findElement(By.id("agentPerson")).sendKeys(shunchaoTrademarkTmsve.getAgentName());

        //代理委托书
        driver.findElement(By.xpath("//*[@id=\"fileWtTr\"]/td[2]/input[5]")).click();//代理委托书上传按钮
        WebElement ifr_popup0 = driver.findElement(By.xpath("//*[@id=\"ifr_popup0\"]"));
        driver.switchTo().frame(ifr_popup0);//进入上传文件iframe

        if (shunchaoTrademarkTmsve.getSba0023().size() > 0) {
            for (ShunchaoTrademarkPow trademarkPow:shunchaoTrademarkTmsve.getSba0023()){
//                WebElement file1 = driver.findElement(By.xpath("//*[@id=\"file1\"]"));
                WebElement file1 = driver.findElement(By.xpath("/html/body/form/input[1]"));
                file1.sendKeys(rootPath+trademarkPow.getSba0023());
//                driver.findElement(By.xpath("/html/body/form/input[1]")).sendKeys(rootPath+trademarkPow.getSba0023());
                driver.findElement(By.className("buttonhpb")).click();
            }
        }
        driver.switchTo().parentFrame();//回到上一个iframe
        driver.findElement(By.id("dialogBoxClose")).click();

        //转让人上传文件的语言类型
        WebElement el6 = driver.findElement(By.id("scwjId"));
        Select sel6 = new Select(el6);
        if(applicant1.getLanguageType().equals("1")){
            sel6.selectByValue("0");
        }else{
            sel6.selectByValue("1");
        }

        if (applicant1.getApplicantType().equals("0")){//法人或其他组织
            //变更后申请人主体资格证明文件(中文)
            driver.findElement(By.xpath("//*[@id=\"fileZtTr\"]/td[2]/input[2]")).click();
            WebElement iframe1 = driver.findElement(By.xpath("//*[@id=\"dlg_upload\"]/iframe"));//
            driver.switchTo().frame(iframe1);//进入上传文件iframe
            driver.findElement(By.id("filePdf")).sendKeys(rootPath+applicant1.getSba0027());
            driver.findElement(By.id("laodBut")).click();

            driver.switchTo().parentFrame();//回到上一个iframe
            driver.findElement(By.xpath("/html/body/div[8]/div[1]/div[2]/a")).click();

            if(applicant1.getLanguageType().equals("2")){
                //转让人主体资格证明文件(外文)
                driver.findElement(By.xpath("//*[@id=\"fileZtEnTr\"]/td[2]/input[2]")).click();
                WebElement iframe2 = driver.findElement(By.xpath("//*[@id=\"dlg_upload\"]/iframe"));//
                driver.switchTo().frame(iframe2);//进入上传文件iframe
                driver.findElement(By.id("filePdf")).sendKeys(rootPath+applicant1.getSba0027());
                driver.findElement(By.id("laodBut")).click();

                driver.switchTo().parentFrame();//回到上一个iframe
                driver.findElement(By.xpath("/html/body/div[8]/div[1]/div[2]/a")).click();
            }
        }else{//自然人,无营业执照自然人
            //变更后申请人证件名称
            WebElement el8 = driver.findElement(By.id("certType"));
            Select sel8 = new Select(el8);
            if ("0".equals(applicant1.getIdName())) {
                sel8.selectByValue("200005000400000000");
            } else if ("1".equals(applicant1.getIdName())) {
                sel8.selectByValue("200005000500000000");
            }else if ("2".equals(applicant1.getIdName())) {
                sel8.selectByValue("200005002100000000");
            }
            driver.findElement(By.id("certNo")).sendKeys(applicant1.getIdNumber());

            //变更后申请人身份证明文件(中文)
            driver.findElement(By.xpath("//*[@id=\"fileSfTr\"]/td[2]/input[2]")).click();
            WebElement iframe1 = driver.findElement(By.xpath("//*[@id=\"dlg_upload\"]/iframe"));//
            driver.switchTo().frame(iframe1);//进入上传文件iframe
            driver.findElement(By.id("filePdf")).sendKeys(rootPath+applicant1.getSba0027());
            driver.findElement(By.id("laodBut")).click();

            driver.switchTo().parentFrame();//回到上一个iframe
            driver.findElement(By.xpath("/html/body/div[8]/div[1]/div[2]/a")).click();
            if(applicant1.getLanguageType().equals("1")){
                if(StringUtils.isNotBlank(rootPath+applicant1.getSba0027())){
                    //变更后申请人主体资格证明文件(中文)
                    driver.findElement(By.xpath("//*[@id=\"fileZtTr\"]/td[2]/input[2]")).click();
                    WebElement iframe3 = driver.findElement(By.xpath("//*[@id=\"dlg_upload\"]/iframe"));
                    driver.switchTo().frame(iframe3);//进入上传文件iframe
                    driver.findElement(By.id("filePdf")).sendKeys(rootPath+applicant1.getSba0027());
                    driver.findElement(By.id("laodBut")).click();

                    driver.switchTo().parentFrame();//回到上一个iframe
                    driver.findElement(By.xpath("/html/body/div[8]/div[1]/div[2]/a")).click();
                }
            }else{
                //变更后申请人身份证明文件(外文)
                driver.findElement(By.xpath("//*[@id=\"fileSfEnTr\"]/td[2]/input[2]")).click();
                WebElement iframe3 = driver.findElement(By.xpath("//*[@id=\"dlg_upload\"]/iframe"));
                driver.switchTo().frame(iframe3);//进入上传文件iframe
                driver.findElement(By.id("filePdf")).sendKeys(rootPath+applicant1.getSba0027());
                driver.findElement(By.id("laodBut")).click();

                driver.switchTo().parentFrame();//回到上一个iframe
                driver.findElement(By.xpath("/html/body/div[8]/div[1]/div[2]/a")).click();
            }
        }

        if ("0".equals(shunchaoTrademarkTmsve.getWhetherApplyJointly())) {
            driver.findElement(By.id("ck_no")).click();
        } else if ("1".equals(shunchaoTrademarkTmsve.getWhetherApplyJointly())) {
            driver.findElement(By.id("ck_yes")).click();

            List<ShunchaoTrademarkApplicant> trademarkCoApplicantList1 = shunchaoTrademarkTmsve.getTrademarkCoApplicantList1();
            List<ShunchaoTrademarkApplicant> trademarkCoApplicantList2 = shunchaoTrademarkTmsve.getTrademarkCoApplicantList2();
            int a = 0;
            for(ShunchaoTrademarkApplicant shunchaoTrademarkApplicant1:trademarkCoApplicantList1){

                driver.findElement(By.xpath("//*[@id=\"div_dg\"]/div/div/div[1]/table/tbody/tr/td[1]/a")).click();

                driver.switchTo().parentFrame();
                String pageSource = driver.getPageSource();
                Thread.sleep(2000);
                driver.findElement(By.xpath("/html/body/div[9]/div[2]/div[4]/a/span/span")).click();
                //进入iframe
                driver.switchTo().frame("myframe");
                WebElement iframe3 = driver.findElement(By.xpath("//*[@id=\"dlg_addgyr\"]/iframe"));
                driver.switchTo().frame(iframe3);//进入共有人iframe

                ShunchaoTrademarkApplicant shunchaoTrademarkApplicant2 = trademarkCoApplicantList2.get(a);

                //变更前共有人信息
                driver.findElement(By.id("beNameCn")).sendKeys(shunchaoTrademarkApplicant1.getApplicantName());//变更前共有人名称(中文)
                if (Objects.nonNull(shunchaoTrademarkApplicant1.getApplicantOwnerEnglishname())) {
                    driver.findElement(By.id("beNameEn")).sendKeys(shunchaoTrademarkApplicant1.getApplicantOwnerEnglishname());//变更前共有人名称(英文)
                }
                if (Objects.nonNull(shunchaoTrademarkApplicant1.getApplicationAddres())) {
                    driver.findElement(By.id("beAddrCn")).sendKeys(shunchaoTrademarkApplicant1.getApplicationAddres());//变更前共有人地址(中文)
                }
                if (Objects.nonNull(shunchaoTrademarkApplicant1.getApplicationAddressEnglish())) {
                    driver.findElement(By.id("beAddrEn")).sendKeys(shunchaoTrademarkApplicant1.getApplicationAddressEnglish());//变更前共有人地址(英文)
                }
                //共有人类型
                WebElement el10 = driver.findElement(By.id("beappTypeId"));
                Select sel10 = new Select(el10);
                if ("0".equals(shunchaoTrademarkApplicant1.getApplicantType())) {
                    sel10.selectByValue("1");
                }else {
                    sel10.selectByValue("0");
                }

                //上传文件的语言类型
                WebElement el11 = driver.findElement(By.id("bescwjId"));
                Select sel11 = new Select(el11);
                if(shunchaoTrademarkApplicant1.getLanguageType().equals("1")){
                    sel11.selectByValue("0");
                }else{
                    sel11.selectByValue("1");
                }

                if (shunchaoTrademarkApplicant1.getApplicantType().equals("0")){//法人或其他组织
                    //共有人主体资格证明文件(中文)
                    driver.findElement(By.xpath("//*[@id=\"befileZtTr\"]/td[2]/input[2]")).click();
                    WebElement iframe1 = driver.findElement(By.xpath("//*[@id=\"dlg_upload\"]/iframe"));
                    driver.switchTo().frame(iframe1);//进入上传文件iframe
                    driver.findElement(By.id("filePdf")).sendKeys(rootPath+shunchaoTrademarkApplicant1.getSba0027());
                    driver.findElement(By.id("laodBut")).click();

                    driver.switchTo().parentFrame();//回到上一个iframe
                    driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[2]/a")).click();

                    if(shunchaoTrademarkApplicant1.getLanguageType().equals("2")){
                        //共有人主体资格证明文件(外文)
                        driver.findElement(By.xpath("//*[@id=\"befileZtEnTr\"]/td[2]/input[2]")).click();
                        WebElement iframe2 = driver.findElement(By.xpath("//*[@id=\"dlg_upload\"]/iframe"));
                        driver.switchTo().frame(iframe2);//进入上传文件iframe
                        driver.findElement(By.id("filePdf")).sendKeys(rootPath+shunchaoTrademarkApplicant1.getSba0027());
                        driver.findElement(By.id("laodBut")).click();

                        driver.switchTo().parentFrame();//回到上一个iframe
                        driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[2]/a")).click();
                    }
                }else{//自然人,无营业执照自然人

                    //共有人身份证明文件(中文)
                    driver.findElement(By.xpath("//*[@id=\"befileSfTr\"]/td[2]/input[2]")).click();
                    WebElement iframe1 = driver.findElement(By.xpath("//*[@id=\"dlg_upload\"]/iframe"));
                    driver.switchTo().frame(iframe1);//进入上传文件iframe
                    driver.findElement(By.id("filePdf")).sendKeys(rootPath+shunchaoTrademarkApplicant1.getSba0027());
                    driver.findElement(By.id("laodBut")).click();

                    driver.switchTo().parentFrame();//回到上一个iframe
                    driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[2]/a")).click();
                    if(shunchaoTrademarkApplicant1.getLanguageType().equals("1")){
                        //共有人主体资格证明文件(中文)
                        driver.findElement(By.xpath("//*[@id=\"befileZtTr\"]/td[2]/input[2]")).click();
                        WebElement iframe5 = driver.findElement(By.xpath("//*[@id=\"dlg_upload\"]/iframe"));
                        driver.switchTo().frame(iframe5);//进入上传文件iframe
                        driver.findElement(By.id("filePdf")).sendKeys(rootPath+shunchaoTrademarkApplicant1.getSba0027());
                        driver.findElement(By.id("laodBut")).click();

                        driver.switchTo().parentFrame();//回到上一个iframe
                        driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[2]/a")).click();
                    }else{
                        //共有人身份证明文件(外文)
                        driver.findElement(By.xpath("//*[@id=\"befileSfEnTr\"]/td[2]/input[2]")).click();
                        WebElement iframe5 = driver.findElement(By.xpath("//*[@id=\"dlg_upload\"]/iframe"));
                        driver.switchTo().frame(iframe5);//进入上传文件iframe
                        driver.findElement(By.id("filePdf")).sendKeys(rootPath+shunchaoTrademarkApplicant1.getSba0027());
                        driver.findElement(By.id("laodBut")).click();

                        driver.switchTo().parentFrame();//回到上一个iframe
                        driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[2]/a")).click();
                    }
                }

                //变更后共有人信息
                driver.findElement(By.id("afNameCn")).sendKeys(shunchaoTrademarkApplicant2.getApplicantName());//变更后共有人名称(中文)
                if (Objects.nonNull(shunchaoTrademarkApplicant2.getApplicantOwnerEnglishname())) {
                    driver.findElement(By.id("afNameEn")).sendKeys(shunchaoTrademarkApplicant2.getApplicantOwnerEnglishname());//变更后共有人名称(英文)
                }
                if (Objects.nonNull(shunchaoTrademarkApplicant2.getApplicationAddres())) {
                    driver.findElement(By.id("afAddrCn")).sendKeys(shunchaoTrademarkApplicant2.getApplicationAddres());//变更后共有人地址(中文)
                }
                if (Objects.nonNull(shunchaoTrademarkApplicant2.getApplicationAddressEnglish())) {
                    driver.findElement(By.id("afAddrEn")).sendKeys(shunchaoTrademarkApplicant2.getApplicationAddressEnglish());//变更后共有人地址(英文)
                }
                //变更后共有人类型
                WebElement el12 = driver.findElement(By.id("afappTypeId"));
                Select sel12 = new Select(el12);
                if ("0".equals(shunchaoTrademarkApplicant2.getApplicantType())) {
                    sel12.selectByValue("1");
                }else {
                    sel12.selectByValue("0");
                }

                //上传文件的语言类型
                WebElement el13 = driver.findElement(By.id("afscwjId"));
                Select sel13 = new Select(el13);
                if(shunchaoTrademarkApplicant2.getLanguageType().equals("1")){
                    sel13.selectByValue("0");
                }else{
                    sel13.selectByValue("1");
                }

                if (shunchaoTrademarkApplicant2.getApplicantType().equals("0")){//法人或其他组织
                    //共有人主体资格证明文件(中文)
                    driver.findElement(By.xpath("//*[@id=\"affileZtTr\"]/td[2]/input[2]")).click();
                    WebElement iframe1 = driver.findElement(By.xpath("//*[@id=\"dlg_upload\"]/iframe"));
                    driver.switchTo().frame(iframe1);//进入上传文件iframe
                    driver.findElement(By.id("filePdf")).sendKeys(rootPath+shunchaoTrademarkApplicant2.getSba0027());
                    driver.findElement(By.id("laodBut")).click();

                    driver.switchTo().parentFrame();//回到上一个iframe
                    driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[2]/a")).click();

                    if(shunchaoTrademarkApplicant2.getLanguageType().equals("2")){
                        //共有人主体资格证明文件(外文)
                        driver.findElement(By.xpath("//*[@id=\"affileZtEnTr\"]/td[2]/input[2]")).click();
                        WebElement iframe2 = driver.findElement(By.xpath("//*[@id=\"dlg_upload\"]/iframe"));
                        driver.switchTo().frame(iframe2);//进入上传文件iframe
                        driver.findElement(By.id("filePdf")).sendKeys(rootPath+shunchaoTrademarkApplicant2.getSba0027());
                        driver.findElement(By.id("laodBut")).click();

                        driver.switchTo().parentFrame();//回到上一个iframe
                        driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[2]/a")).click();
                    }
                }else{//自然人,无营业执照自然人

                    //共有人身份证明文件(中文)
                    driver.findElement(By.xpath("//*[@id=\"affileSfTr\"]/td[2]/input[2]")).click();
                    WebElement iframe1 = driver.findElement(By.xpath("//*[@id=\"dlg_upload\"]/iframe"));
                    driver.switchTo().frame(iframe1);//进入上传文件iframe
                    driver.findElement(By.id("filePdf")).sendKeys(rootPath+shunchaoTrademarkApplicant2.getSba0027());
                    driver.findElement(By.id("laodBut")).click();

                    driver.switchTo().parentFrame();//回到上一个iframe
                    driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[2]/a")).click();
                    if(shunchaoTrademarkApplicant2.getLanguageType().equals("1")){
                        //共有人主体资格证明文件(中文)
                        driver.findElement(By.xpath("//*[@id=\"affileZtTr\"]/td[2]/input[2]")).click();
                        WebElement iframe5 = driver.findElement(By.xpath("//*[@id=\"dlg_upload\"]/iframe"));
                        driver.switchTo().frame(iframe5);//进入上传文件iframe
                        driver.findElement(By.id("filePdf")).sendKeys(rootPath+shunchaoTrademarkApplicant2.getSba0027());
                        driver.findElement(By.id("laodBut")).click();

                        driver.switchTo().parentFrame();//回到上一个iframe
                        driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[2]/a")).click();
                    }else{
                        //共有人身份证明文件(外文)
                        driver.findElement(By.xpath("//*[@id=\"affileSfEnTr\"]/td[2]/input[2]")).click();
                        WebElement iframe5 = driver.findElement(By.xpath("//*[@id=\"dlg_upload\"]/iframe"));
                        driver.switchTo().frame(iframe5);//进入上传文件iframe
                        driver.findElement(By.id("filePdf")).sendKeys(rootPath+shunchaoTrademarkApplicant2.getSba0027());
                        driver.findElement(By.id("laodBut")).click();

                        driver.switchTo().parentFrame();//回到上一个iframe
                        driver.findElement(By.xpath("/html/body/div[2]/div[1]/div[2]/a")).click();
                    }
                }

                //确认提交
                driver.findElement(By.xpath("//*[@id=\"form1\"]/table/tbody/tr[22]/td/label[1]/input")).click();
                Thread.sleep(2000);

                driver.switchTo().alert().accept();
                driver.switchTo().parentFrame();
                a++;
            }

            ((JavascriptExecutor) driver).executeScript(htmlScrolltoJs);
            Thread.sleep(1000);
        }


        //变更前申请人名称(中文)
        if (Objects.nonNull(applicant2.getApplicantName())) {
            driver.findElement(By.id("txt_bgqmyzw")).sendKeys(applicant2.getApplicantName());
        }
        //变更前申请人名称(英文)
        if (Objects.nonNull(applicant2.getApplicantOwnerEnglishname())) {
            driver.findElement(By.id("txt_bgqmyyw")).sendKeys(applicant2.getApplicantOwnerEnglishname());
        }
        //变更前申请人地址(中文)
        driver.findElement(By.id("txt_bgqdzzw")).sendKeys(applicant2.getApplicationAddres());

        //变更前申请人地址(英文)
        if (Objects.nonNull(applicant2.getApplicationAddressEnglish())) {
            driver.findElement(By.id("txt_bgqdzyw")).sendKeys(applicant2.getApplicationAddressEnglish());
        }
        //变更前联系地址
        if (Objects.nonNull(applicant2.getRecipientAddress())) {
            driver.findElement(By.id("bfchangedCommunicationAddr")).sendKeys(applicant2.getRecipientAddress());
        }

        if(shunchaoTrademarkTmsve.getChangeType()!=2){
            if(applicant2.getChangeFileType().equals("1")){
                driver.findElement(By.id("ifBgzmEn1")).click();
            }else{
                driver.findElement(By.id("ifBgzmEn")).click();

                //变更证明文件(英文)
                driver.findElement(By.xpath("//*[@id=\"tr_bgzmwjen\"]/td[2]/input[2]")).click();
                WebElement iframe3 = driver.findElement(By.xpath("//*[@id=\"dlg_upload\"]/iframe"));
                driver.switchTo().frame(iframe3);//进入上传文件iframe
                driver.findElement(By.id("filePdf")).sendKeys(rootPath+applicant2.getSba0037());
                driver.findElement(By.id("laodBut")).click();

                driver.switchTo().parentFrame();//回到上一个iframe
                if("1".equals(shunchaoTrademarkTmsve.getWhetherApplyJointly())){
                    driver.findElement(By.xpath("/html/body/div[11]/div[1]/div[2]/a")).click();
                }else{
                    driver.findElement(By.xpath("/html/body/div[8]/div[1]/div[2]/a")).click();
                }

            }
            //变更证明文件(中文)
            driver.findElement(By.xpath("//*[@id=\"tr_bgzmwj\"]/td[2]/input[2]")).click();
            WebElement iframe3 = driver.findElement(By.xpath("//*[@id=\"dlg_upload\"]/iframe"));
            driver.switchTo().frame(iframe3);//进入上传文件iframe
            driver.findElement(By.id("filePdf")).sendKeys(rootPath+applicant2.getSba0036());
            driver.findElement(By.id("laodBut")).click();

            driver.switchTo().parentFrame();//回到上一个iframe
            if("1".equals(shunchaoTrademarkTmsve.getWhetherApplyJointly())){
                driver.findElement(By.xpath("/html/body/div[11]/div[1]/div[2]/a")).click();
            }else{
                driver.findElement(By.xpath("/html/body/div[8]/div[1]/div[2]/a")).click();
            }
        }
        //商标注册号,手动输入
        driver.findElement(By.xpath("//*[@id=\"tmbgsq\"]/table/tbody/tr[43]/td[2]/a[2]")).click();
        driver.findElement(By.id("regnum1")).sendKeys(shunchaoTrademarkTmsve.getRegistrationNumber());
        driver.findElement(By.id("ad")).click();
        driver.switchTo().alert().dismiss();

        if (Objects.nonNull(shunchaoTrademarkTmsve.getSba0022())) {
            //有关说明文件
            driver.findElement(By.xpath("//*[@id=\"fileYgTr\"]/td[2]/input[2]")).click();
            WebElement iframe4 = driver.findElement(By.xpath("//*[@id=\"dlg_upload\"]/iframe"));
            driver.switchTo().frame(iframe4);//进入上传文件iframe
            driver.findElement(By.id("filePdf")).sendKeys(rootPath+shunchaoTrademarkTmsve.getSba0022());
            driver.findElement(By.id("laodBut")).click();
            driver.switchTo().parentFrame();//回到上一个iframe
            driver.findElement(By.xpath("/html/body/div[8]/div[1]/div[2]/a")).click();
        }
        } catch (Exception e){
            log.info("获取失败",e);
            return Result.error("获取失败");
        }
        return Result.ok();
    }
}
