package com.shunchao.cpc.util;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Arrays;

public class ChromeDriverUtils {
    public static WebDriver beforeM(WebDriver driver, String path){

        System.setProperty("webdriver.chrome.driver",path+"chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-infobars", "--start-maximized", "--disable-blink-features=AutomationControlled");
        options.setExperimentalOption("excludeSwitches", Arrays.asList("enable-automation"));
        driver = new ChromeDriver(options);
        return driver;
    }

    public static void afterM(WebDriver driver){
        driver.quit();
    }
}
