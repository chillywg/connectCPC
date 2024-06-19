package com.shunchao.cpc.util;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.Arrays;

public class FoxDriverUtils {
    public static WebDriver foxDriver(WebDriver driver, String path){
        System.setProperty("webdriver.gecko.driver",path+"geckodriver.exe");
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--disable-infobars", "--start-maximized", "--disable-blink-features=AutomationControlled");
        driver = new FirefoxDriver();
        return driver;
    }
    public static WebDriver foxDriver2(WebDriver driver, String path){
        System.setProperty("webdriver.gecko.driver",path+"geckodriver.exe");
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--headless");//不开启浏览器
        driver = new FirefoxDriver(options);
        return driver;
    }
    public static void afterM(WebDriver driver){
        driver.quit();
    }
}
