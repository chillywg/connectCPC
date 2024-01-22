package com.shunchao.config;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.healthmarketscience.jackcess.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Base64Utils;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.sql.Types;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class ShunchaoApplicationTests {

    @Test
    void contextLoads() {
        /*byte[] b = new byte[10];
        b[0] = 1;
        b[1] = 2;
        String s = Base64Utils.encodeToString(b);
        System.out.println(s);

        byte[] bytes = Base64Utils.decodeFromString(s);
        System.out.println(bytes.toString());*/

        File file = new File("D:\\upFiles\\cases\\ceshibianhao20200228\\20200229\\100001.pdf");
        BufferedInputStream inputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(file));
            byte[] buf = new byte[inputStream.available()];

            int len;
            String content = "";
            while ((len = inputStream.read(buf)) > 0) {
                content = content + Base64Utils.encodeToString(buf);
            }
            System.out.println(content);

            byte[] fromString = Base64Utils.decodeFromString(content);
            System.out.println(fromString.length);
            FileCopyUtils.copy(fromString, new BufferedOutputStream(new FileOutputStream(new File("D:\\abc\\01010.pdf"))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testJackcess() {
        String dataPath = CpcPathInComputer.getCpcDataPathWindowsComputer();
        try {
            Database db = DatabaseBuilder.open(new File(dataPath));
            Table table = db.getTable("DZSQ_KHD_SHENQINGXX");
            UUID shenqingbh = UUID.randomUUID();

            Table ajTable = db.getTable("DZSQ_KHD_AJ");
            UUID anjuanbh = UUID.randomUUID();
            table.addRow("{"+shenqingbh+"}","","","","","","",new Date(),new Date(),"");

            Map<String, Object> map = new HashMap<>();
            map.put("ANJUANBH", "{" + anjuanbh + "}");
            map.put("TIANXIEMS", "0");
            map.put("ANJUANLX", "0");
            map.put("ANJUANZT", "0");
//            map.put(foreignKeyIndex.getName(), "{"+shenqingbh+"}");
            map.put("CHUANGJIANRQ", new Date());
            map.put("NEIBUBH", "NEIBU20200001");
            ajTable.addRowFromMap(map);
            String qw = "{" + anjuanbh + "}";
            System.out.println("{" + anjuanbh + "}");

//            db.flush();
            for (Row row : ajTable) {
//                System.out.println(row.getString("ANJUANBH"));
                if (qw.equalsIgnoreCase(row.getString("ANJUANBH"))) {
                    System.out.println(row.getString("ANJUANBH"));
                    String cc = shenqingbh.toString().toUpperCase();
                    row.put("SHENQINGBH", "{"+cc+"}");
                    System.out.println(row);
                    ajTable.updateRow(row);
                }

            }
//            db.flush();
            /*for(Row row : table) {
                //顺序取出表中的字段和值
                //字段名区分大小写，如果不一致会导致取值为null
                System.out.println("--城市名字--" + row.get("SHENQINGBH"));
            }*/
            /*Database db = DatabaseBuilder.create(Database.FileFormat.V2000, new File(dataPath));
            Table newTable = new TableBuilder("NewTable1")
                    .addColumn(new ColumnBuilder("a")
                            .setSQLType(Types.INTEGER))
                    .addColumn(new ColumnBuilder("b")
                            .setSQLType(Types.VARCHAR))
                    .toTable(db);
            newTable.addRow(1, "foo");*/
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testUUID() {
        System.out.println(UUID.randomUUID().toString().toUpperCase());

    }

    @Test
    public void testInsert() {

        String dataPath = CpcPathInComputer.getCpcDataPathWindowsComputer();
        try {
            Database db = DatabaseBuilder.open(new File(dataPath));
            Table table = db.getTable("DZSQ_KHD_SQWJ");


            Map<String, Object> map = new HashMap<>();
            map.put("WENJIANMC", "0");
            map.put("BIAOGEDM", "0");
            map.put("WENJIANLX","0");
            map.put("CHUANGJIANLX","1");
            map.put("CHUANGJIANRQ", new Date());
            map.put("ANJUANBH", "{"+UUID.randomUUID().toString().toUpperCase()+"}");
            table.addRowFromMap(map);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    @Test
    public void testUploadToCPC() {
        HashMap<String, Object> paramMap = new HashMap<>();
        //文件上传只需将参数中的键指定（默认file），值设为文件对象即可，对于使用者来说，文件上传与普通表单提交并无区别
//        byte[] gzip = ZipUtil.gzip(new File("D:\\notices\\2020030228430943"));
//        File file = new File(gzip);
        paramMap.put("file", ZipUtil.zip(new File("D:\\notices\\2020030228430943")));
//        paramMap.put("X-Access-Token", "");

//        String result= HttpUtil.post("http://localhost:8080/jeecg-boot/sys/common/upload", paramMap);
        HttpResponse execute = HttpRequest.post("http://localhost:8080/jeecg-boot" + "/notice/shunchaoDzsqKhdTzs/upload").
                header("X-Access-Token", "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE1ODQxNjAwMTMsInVzZXJuYW1lIjoid2VpZ2FuIn0.HN2z2HGt68ESUI2jbxYnTAqgtjffQOSdYFtmhoFhA1E").form(paramMap).execute();
        System.out.println(execute);

    }
}
