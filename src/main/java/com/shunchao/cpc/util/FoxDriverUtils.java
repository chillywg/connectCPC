package com.shunchao.cpc.util;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.Arrays;

public class FoxDriverUtils {
    public static FirefoxDriver foxDriver(String path){
        //        System.setProperty("webdriver.gecko.driver", uploadpath + "\\firefox\\geckodriver.exe");
        System.setProperty("webdriver.firefox.bin","D:\\Program Files\\Mozilla Firefox\\firefox.exe");
        System.setProperty("webdriver.gecko.driver", path);
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/111.0");
//        options.addArguments("-private");  // 隐私模式
        options.addPreference("--disable-blink-features=AutomationControlled",false);
        options.addPreference("--browser.privatebrowsing.autostart", true);
        options.addPreference("--privacy.trackingprotection.enabled", true);
        options.addPreference("--dom.webdriver.enabled", false);
        options.addPreference("window.navigator.webdriver", false);
        //        options.addArguments("--headless");
        FirefoxDriver driver = new FirefoxDriver(options);
        return driver;
    }
}
