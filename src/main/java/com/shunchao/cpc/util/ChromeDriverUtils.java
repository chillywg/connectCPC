package com.shunchao.cpc.util;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

public class ChromeDriverUtils {
    public static WebDriver beforeM(WebDriver driver, String path) throws IOException, InterruptedException {
        String cmd= "C:\\Users\\admin\\Desktop\\google.bat";
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(cmd);
        InputStreamReader isr = new InputStreamReader(process.getInputStream(),"gbk");
        BufferedReader br = new BufferedReader(isr);
        br.close();
        //等待cmd 命令执行完成
        process.waitFor();

        ChromeOptions options = new ChromeOptions();
//        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36");
//        options.addArguments("--disable-gpu", "--no-sandbox", "--disable-blink-features=AutomationControlled");
//        options.setExperimentalOption("excludeSwitches", Arrays.asList("enable-automation"));
        options.setExperimentalOption("debuggerAddress","127.0.0.1:9222");
//        options.addArguments("headless");
        driver = new ChromeDriver(options);
        return driver;
    }
    public static WebDriver beforeM2(WebDriver driver, String path){

        System.setProperty("webdriver.chrome.driver",path+"chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36");
        options.addArguments("--disable-gpu", "--no-sandbox", "--disable-blink-features=AutomationControlled");
        // 从 Chrome 的启动参数中排除 enable-automation 开关。添加后隐藏浏览器顶部“正受到自动测试软件的控制”这一条
        options.setExperimentalOption("excludeSwitches", Arrays.asList("enable-automation"));
        //禁用 Chrome 的其他自动化扩展,我不知道添加后是否生效，貌似没有明显标志
        options.setExperimentalOption("useAutomationExtension", false);
        driver = new ChromeDriver(options);
        String js = "Object.defineProperty(navigator, 'webdriver', { get: () => undefined });";
        ((JavascriptExecutor) driver).executeScript(js);
//        driver.manage().window().maximize();
        return driver;
    }

    public static void afterM(WebDriver driver){
        driver.quit();
    }
}
