package com.shunchao.cpc.util;

import com.shunchao.config.CpcPathConfig;
import com.shunchao.config.CpcPathInComputer;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

@Slf4j
public class DBHelper {

    private static final String driver = "net.ucanaccess.jdbc.UcanaccessDriver";
    private static final String charSet = "gbk";

    private static Connection conn = null;

    static {
        try{
            Class.forName(driver);
        }catch (Exception e){
            e.printStackTrace();
            log.info("Access数据库驱动加载失败！");
        }
    }

    public static Connection getConnection() throws Exception{
        Properties prop = new Properties();
        prop.put("charSet",charSet);
        CpcPathConfig cpcPathConfig = new CpcPathConfig();
        cpcPathConfig.cpcPathInComputer();
        String dataPath = CpcPathInComputer.getCpcDataPathWindowsComputer();
        String url = "jdbc:ucanaccess://" + dataPath + ";openExclusive=false;ignoreCase=true";
        if(conn == null){
            conn = DriverManager.getConnection(url,prop);
            return conn;
        }else {
            return conn;
        }
    }

    public static List<Map<String, Object>> queryMapListBySql (Connection conn,String sql, String[] column) throws Exception {
        //Connection conn = getConnection();
        Statement statement = conn.createStatement();
        ResultSet result = statement.executeQuery(sql);
        List<Map<String, Object>> resultList = new ArrayList<>();
        Map<String, Object> paramMap = null;
        while (result.next()) {
            paramMap = new HashMap<>();
            for (String col : column) {
                paramMap.put(col, result.getObject(col));
            }
            resultList.add(paramMap);
        }
        result.close();
        return resultList;
    }
}
