package com.shunchao.config;

import com.jianggujin.registry.JExecResult;
import com.jianggujin.registry.JQueryOptions;
import com.jianggujin.registry.JRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class CpcPathConfig {
    @Bean
    public CpcPathInComputer cpcPathInComputer() {
        CpcPathInComputer cpcPathInComputer = new CpcPathInComputer();
        try {
            //参数后期写入配置文件 TODO
//            JExecResult result = JRegistry.query("HKEY_LOCAL_MACHINE\\SOFTWARE\\WOW6432Node\\gwssi\\CPC客户端",
//                    new JQueryOptions().useF("\"CPC\""));
//            log.info(result.toString());
//            String client = result.getLines()[1];
//            String data = result.getLines()[2];
//            String clientPath = client.split("REG_SZ")[1].trim();
//            String dataPath = data.split("REG_SZ")[1].trim();
//            CpcPathInComputer.setClientCpcPathWindowsComputer(clientPath);
//            CpcPathInComputer.setCpcDataPathWindowsComputer(dataPath);
//            CpcPathInComputer.setCpcBinPathWindowsComputer(clientPath.substring(0,clientPath.lastIndexOf("\\")));
//
//            log.info(clientPath.substring(0,clientPath.lastIndexOf("\\")));
//            log.info(clientPath);
            /*System.out.println(clientPath.substring(0,clientPath.lastIndexOf("\\")));
            System.out.println(clientPath);*/
        } catch (Exception e) {
            log.error("获取失败", e);
        }
        return cpcPathInComputer;
    }
}
