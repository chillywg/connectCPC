package com.shunchao.cpc.util;

import com.shunchao.config.AccessDBConfig;
import com.shunchao.config.CpcPathInComputer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

@Component
@Slf4j
public class SqliteDBUtils {
	/**
	 *
	 * @Title:	getConnection
	 * @Description:	获取sqlite数据库连接
	 * @param:	@return
	 * @return:	Connection
	 * @throws
	 */
	public static Connection getConnection () {
		Map map = System.getenv();
		String cnipa_client_home = map.get("CNIPA_CLIENT_HOME").toString();
		Connection conn = null;
//		String dataPath = CpcPathInComputer.getCpcDataPathWindowsComputer();
//		Properties prop = new Properties();
//        prop.put("charSet", accessDBConfig.charSet);
//        prop.put("user", accessDBConfig.user);
//        prop.put("password", accessDBConfig.password);
        String dbUr = "jdbc:sqlite://" + cnipa_client_home + "\\plugins\\as\\cpc-main-as\\db\\dzsq-guojia.db"; // openExclusive=false;ignoreCase=true
        try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection(dbUr);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("获取数据库连接失败，" + e.getMessage());
		}
        return conn;
	}

	public static List<Map<String, Object>> queryMapListBySql (String sql, String[] column, String[] column2) throws Exception {
		Connection connection = getConnection();
		if (connection == null) {
			throw new Exception ("无法获取sqlite数据库连接");
		}
		Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(sql);
//        ResultSetMetaData metaData = result.getMetaData();
        List<Map<String, Object>> resultList = new ArrayList<>();
        Map<String, Object> paramMap = null;
        while (result.next()) {
        	paramMap = new HashMap<>();
			int i=0;
            for (String col : column) {
//            	paramMap.put(col.toLowerCase(), result.getString(col));
            	paramMap.put(column2[i], result.getObject(col));
            	i++;
            }
            resultList.add(paramMap);
        }
		result.close();
		statement.close();
		connection.close();
        return resultList;
	}

	public static int update (String sql) throws Exception {
		Connection connection = getConnection();
		if (connection == null) {
			throw new Exception ("无法获取sqlite数据库连接");
		}

		Statement statement = connection.createStatement();
		int executeUpdate = statement.executeUpdate(sql);
//        ResultSetMetaData metaData = result.getMetaData();
		List<Map<String, Object>> resultList = new ArrayList<>();
		statement.close();
		connection.close();
		return executeUpdate;
	}

}
