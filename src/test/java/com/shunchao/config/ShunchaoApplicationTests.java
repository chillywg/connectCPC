package com.shunchao.config;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.healthmarketscience.jackcess.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Base64Utils;
import org.springframework.util.FileCopyUtils;

import java.io.*;
import java.util.*;

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

    @Test
    public void testMap() {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("voiceFlag","0");
        paramMap.put("threeDimFlag","0");
        paramMap.put("colrCombFlag","0");
        String sba0023Body = "{\"code\":0,\"message\":\"操作成功\",\"type\":\"success\",\"data\":{\"ossId\":\"2026051266057249225490433\",\"fileName\":\"代理委托书.pdf\",\"fileSourceId\":null,\"fileSourceCode\":null,\"fileSize\":1345,\"originalName\":\"代理委托书.pdf\",\"fileSuffix\":\".pdf\",\"url\":\"2026/2026/05/26/d5941de66e6c4afbb0243d5a9af7b8eb.pdf\",\"createTime\":\"2026-05-26 15:39:30\",\"creatorName\":\"安徽顺超知识产权代理事务所（特殊普通合伙）\",\"parentId\":null,\"originType\":null,\"oldId\":null,\"pageNum\":null,\"pageSize\":null,\"configKey\":\"2026\"},\"traceId\":\"6a154e32e4b0c9ca1d509440\"}";
        Map<String, String> sba0023Map = JSON.parseObject(sba0023Body, new TypeReference<Map<String, String>>() {});
        String sba0023Data = sba0023Map.get("data");
        Map<String, String> sba0023DataMap = JSON.parseObject(sba0023Data, new TypeReference<Map<String, String>>() {});
        Map<String, Object>  agentPoaAttJsonMap = new HashMap();
        agentPoaAttJsonMap.put("fileName",sba0023DataMap.get("fileName"));
        agentPoaAttJsonMap.put("attId",sba0023DataMap.get("ossId"));
        agentPoaAttJsonMap.put("attName",sba0023DataMap.get("originalName"));
        agentPoaAttJsonMap.put("ossId",sba0023DataMap.get("ossId"));
        agentPoaAttJsonMap.put("url",sba0023DataMap.get("url"));
        agentPoaAttJsonMap.put("size",Integer.valueOf(sba0023DataMap.get("fileSize")));
        agentPoaAttJsonMap.put("fileSize",Integer.valueOf(sba0023DataMap.get("fileSize")));
        List<Map<String, Object>> mapList = new ArrayList<>();
        mapList.add(agentPoaAttJsonMap);
        String agentPoaAttJson = JSON.toJSONString(mapList);
        paramMap.put("agentPoaAttJson",agentPoaAttJson);
        String next1Body = JSON.toJSONString(paramMap);
        System.out.println(next1Body);
    }

    @Test
    public void testMap2() {
        String goodsText = "{\"code\":0,\"message\":\"操作成功\",\"type\":\"success\",\"data\":{\"pageIndex\":1,\"pageSize\":2000,\"total\":1,\"pages\":1,\"list\":[{\"goodsId\":\"3B882173A76644EE9A31264057A560F4\",\"goodsCode\":\"010039\",\"goodsCnName\":\"碱土金属\",\"goodsEnName\":\"alkaline-earth metals\",\"similarCode\":\"0101\",\"goodsType\":\"尼斯商品\",\"goodsVersion\":\"10\",\"validFlag\":1,\"intlCls\":\"1\",\"intlClsName\":\"第一类\",\"similarName\":\"工业气体，单质\",\"goodsValidFlag\":1}]},\"traceId\":\"6a2275e5e4b038cb8056b566\"}";
        Map<String, String> goodsTextMap = JSON.parseObject(goodsText, new TypeReference<Map<String, String>>() {});
        String goodsData = goodsTextMap.get("data");
        Map<String, String> goodsDataMap = JSON.parseObject(goodsData, new TypeReference<Map<String, String>>() {});
        String list = goodsDataMap.get("list");
        List<Map<String, String>> list2 = JSON.parseObject(list, new TypeReference<List<Map<String, String>>>() {});
        System.out.println(list2);
    }

    @Test
    public void testMap3() {
        HashMap<String, Object> paramMap = new HashMap<>();
        String [] stringArray = {"1","1000"};
        paramMap.put("regionCodeList",Arrays.asList(stringArray));
        paramMap.put("nationType","1");
        String next1Body = JSON.toJSONString(paramMap);
        System.out.println(next1Body);
    }
    @Test
    public void testMap4() {
        String relativePath = "trademark/annex/1862038999332188161/sba0023/1732779743943.jpg";
        int lastIndexOf = relativePath.lastIndexOf("/");
        System.out.println(lastIndexOf);
        String substring = relativePath.substring(0,lastIndexOf);
        System.out.println(substring);
    }
}
