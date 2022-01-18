package com.shunchao.cpc.util;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;

public class SeleniumUtils {

    public static WebDriver beforeM(WebDriver driver,String path){

        System.setProperty("webdriver.ie.driver",path+"IEDriverServer.exe");
        DesiredCapabilities desiredCapabilities = DesiredCapabilities.internetExplorer();
        desiredCapabilities.setCapability("ignoreZoomSetting",true);
        desiredCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,true);
        desiredCapabilities.setCapability("ignoreProtectedModeSettings",true);
        driver = new InternetExplorerDriver(desiredCapabilities);

        //driver.manage().window().setSize(new Dimension(1920,1080));

        return driver;
    }

    public static void afterM(WebDriver driver){
        driver.quit();
    }
}
