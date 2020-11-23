package com.shunchao.cpc.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import com.shunchao.config.AccessDBConfig;
import com.shunchao.config.CpcPathInComputer;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AccessDBUtils {
	@Autowired
	private AccessDBConfig accessDBConfig1;
	
	private static AccessDBConfig accessDBConfig;
	
	private static Connection connection;
	
    @PostConstruct
    public void init(){
    	accessDBConfig = this.accessDBConfig1;
    }
    
    
	/**
	 * 
	 * @Title:	getConnection
	 * @Description:	获取Access数据库连接
	 * @param:	@return
	 * @return:	Connection
	 * @author:	FuQiang
	 * @date:	2020年11月23日 上午11:22:12
	 * @throws
	 */
	public static Connection getConnection () {
		Connection conn = null;
		String dataPath = CpcPathInComputer.getCpcDataPathWindowsComputer();
		Properties prop = new Properties();
        prop.put("charSet", accessDBConfig.charSet);   
        prop.put("user", accessDBConfig.user); 
        prop.put("password", accessDBConfig.password); 
        String dbUr = "jdbc:ucanaccess://" + dataPath + ";openExclusive=false;ignoreCase=true"; // openExclusive=false;ignoreCase=true
        try {
			Class.forName("net.ucanaccess.jdbc.UcanaccessDriver");
			conn = DriverManager.getConnection(dbUr, prop);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("获取数据库连接失败，" + e.getMessage());
		}
        connection = conn;
        return conn;
	}
	
	public static List<Map<String, Object>> queryMapListBySql (String sql, String[] column) throws Exception {
		if (connection == null) {
			Connection conn = getConnection();
			
			if (conn == null) {
				throw new Exception ("无法获取Access数据库连接");
			}
		}
		
		Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(sql);
//        ResultSetMetaData metaData = result.getMetaData();
        List<Map<String, Object>> resultList = new ArrayList<>();
        Map<String, Object> paramMap = null;
        while (result.next()) {
        	paramMap = new HashMap<>();
            for (String col : column) {
//            	paramMap.put(col.toLowerCase(), result.getString(col));
            	paramMap.put(col, result.getObject(col));
            }
            resultList.add(paramMap);
        }
        
        return resultList;
	}
	
}
