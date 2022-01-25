package com.shunchao.cpc.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.http.HttpException;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;
import com.shunchao.config.CpcPathInComputer;
import com.shunchao.cpc.model.Result;
import com.shunchao.cpc.model.ShunchaoAttachmentInfo;
import com.shunchao.cpc.model.ShunchaoCaseInfo;
import com.shunchao.cpc.model.ShunchaoTrademarkAnnex;
import com.shunchao.cpc.service.IShuncaoConnectService;
import com.shunchao.cpc.util.DBHelper;
import com.shunchao.cpc.util.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.FileCopyUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;
import java.util.stream.Collectors;

//import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class IShuncaoConnectServiceImpl implements IShuncaoConnectService {
    @Value(value = "${connecturl}")
    private String connecturl;

    @Value(value = "${jeecg.path.cases.basecpc}")
    private String basecpc;
    @Value(value = "${jeecg.path.cases.inventions}")
    private String inventions;
    @Value(value = "${jeecg.path.cases.utility_models}")
    private String utility_models;
    @Value(value = "${jeecg.path.cases.designs}")
    private String designs;
    @Value(value = "${jeecg.path.cases.pCT_inventions}")
    private String pCT_inventions;
    @Value(value = "${jeecg.path.cases.pCT_utility}")
    private String pCT_utility;
    @Value(value = "${jeecg.path.cases.reexamination}")
    private String reexamination;
    @Value(value = "${jeecg.path.cases.invalidation}")
    private String invalidation;
    @Value(value = "${jeecg.path.cases.notices}")
    private String notices;

    private final static ForkJoinPool forkJoinPool = new ForkJoinPool();


    /**
     * 功能描述:加载商标局官网所需附件
     * 场景:
     * @Param: [trademarkAnnexList, token]
     * @Return: java.lang.String
     * @Author: Ironz
     * @Date: 2022/1/12 14:05
     */
    public String getTrademarkAnnex(List<ShunchaoTrademarkAnnex> trademarkAnnexList,String token){
        String answer = null;
        if (trademarkAnnexList.size() > 0) {
            String exePath = System.getProperty("exe.path");
            //String exePath = "D:/connectttt/";
            System.out.println("=======================根目录：=============="+exePath);
            String execute = null;
            String message = null;
            try {
                for (ShunchaoTrademarkAnnex trademarkAnnex:trademarkAnnexList){
                    String savePath = exePath;
                    savePath = savePath+trademarkAnnex.getRelativePath();
                    File file = new File(savePath);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    Map<String,Object> map = new HashMap<>();
                    map.put("id",trademarkAnnex.getId());
                    HttpResponse httpResponse = HttpRequest.get(connecturl+"/trademark/shunchaoTrademarkAnnex/downloadAnnexByServices")
                            .header("X-Access-Token",token).form(map).execute();
                    execute = httpResponse.body();
                    JSONObject j = JSON.parseObject(execute);
                    message = j.getString("message");
                    byte[] bytes = Base64Utils.decodeFromString(message);
                    savePath = file.getPath()+File.separator+trademarkAnnex.getUploadName();
                    try {
                        FileCopyUtils.copy(bytes,new BufferedOutputStream(new FileOutputStream(new File(savePath))));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                answer = "0";//数据加载完成
            } catch (HttpException e) {
                answer = "1";//数据问题
                e.printStackTrace();
            }
        }else {
            answer = "2";//没有所需数据
        }
        return answer;
    }

//    @Transactional(rollbackFor = Exception.class)
    @Override
    public void sendCase(ShunchaoCaseInfo shunchaoCaseInfo, List<ShunchaoAttachmentInfo> shunchaoAttachmentInfoList, String token, String category, String fillMode) throws Exception {
        String dataPath = CpcPathInComputer.getCpcDataPathWindowsComputer();
        Database db = DatabaseBuilder.open(new File(dataPath));

        Table table = db.getTable("DZSQ_KHD_SHENQINGXX");
        //此uuid既是申请编号，又是新申请的路径组成部分，务必保持一致，不然向CPC送案之后，无法通过签名，会报路径错误
        String shenqingbhAndPath = UUID.randomUUID().toString();
        String shenqingbh = "{" + (shenqingbhAndPath.toUpperCase()) + "}";//(申请号4)、委内编号6、国际申请号7、
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/M/d");
        String date = DateUtils.getDate("yyyy/M/d");
        Date applicationDate = DateUtils.str2Date(date, simpleDateFormat);
        Map<String, Object> mapS = new HashMap<>();
        mapS.put("SHENQINGBH", shenqingbh);
        mapS.put("SHENQINGLX", shunchaoCaseInfo.getApplicationType());
        mapS.put("ZHUANLIMC", shunchaoCaseInfo.getCaseName());
        mapS.put("SHENQINGH", shunchaoCaseInfo.getPatentNumber());
//        mapS.put("ZHUANLIH", shunchaoCaseInfo.getPatentNumber());
        mapS.put("SHENQINGR", shunchaoCaseInfo.getApplicationDate());
        mapS.put("CHUANGJIANRQ", new Date());
        mapS.put("ZHUANGTAI", "0");
        mapS.put("WEINEIBH", shunchaoCaseInfo.getWeineibh());
        table.addRowFromMap(mapS);
//        table.addRow(shenqingbh,shunchaoCaseInfo.getApplicationType(),shunchaoCaseInfo.getCaseName(),shunchaoCaseInfo.getBusinessNumber(),shunchaoCaseInfo.getPatentNumber(),"","",applicationDate,new Date(),"0");

        Table ajTable = db.getTable("DZSQ_KHD_AJ");
        String anjuanbhAndPath = UUID.randomUUID().toString();
        String anjuanbh = "{" + anjuanbhAndPath.toUpperCase() + "}";
        //案卷号2、案卷名称3、(填写模式4)、注册代码8、签名日期10、案卷包编号（ZIP）11、案卷包路径（ZIP）12、备注13、签名CN14、
//        ajTable.addRow("{"+anjuanbh+"}","","","0","0","{"+shenqingbh+"}",new Date(),"","0","","","","","","内部编号");
        Map<String, Object> map = new HashMap<>();
        map.put("ANJUANBH", anjuanbh);
        map.put("ANJUANMC", shunchaoCaseInfo.getCaseName());
//        map.put()
        map.put("TIANXIEMS", fillMode);
        //0；新申请 1：中间件
        map.put("ANJUANLX", category);
        map.put("ANJUANZT", "0");
        map.put("CHUANGJIANRQ", new Date());
        map.put("NEIBUBH", shunchaoCaseInfo.getInternalNumber());
        ajTable.addRowFromMap(map);

        for (Row row : ajTable) {
            if (anjuanbh.equalsIgnoreCase(row.getString("ANJUANBH"))) {
                System.out.println(row.getString("ANJUANBH"));
                row.put("SHENQINGBH", shenqingbh);
                System.out.println(row);
                ajTable.updateRow(row);
            }

        }

        String cpcBinPathWindowsComputer = CpcPathInComputer.getCpcBinPathWindowsComputer();
        String uuid1 = shenqingbhAndPath;
        String uuid2 = anjuanbhAndPath;
        String filePath = "";
        //0:普通申请--发明 1:普通申请--新型 2:普通申请--外观 3:PCT申请--发明 4:PCT申请--新型 5:复审 6:无效 TODO
        if ("0".equals(shunchaoCaseInfo.getApplicationType())) {
            if ("0".equals(category)) {
                filePath = File.separator + basecpc + File.separator + inventions + File.separator + uuid1 + File.separator + "new";
            } else {
                filePath = File.separator + basecpc + File.separator + inventions + File.separator + uuid1 + File.separator + "others" + File.separator + uuid2;
            }
        } else if ("1".equals(shunchaoCaseInfo.getApplicationType())) {
            if ("0".equals(category)) {
                filePath = File.separator + basecpc + File.separator + utility_models + File.separator + uuid1 + File.separator + "new";
            } else {
                filePath = File.separator + basecpc + File.separator + utility_models + File.separator + uuid1 + File.separator + "others" + File.separator + uuid2;
            }

        } else if ("2".equals(shunchaoCaseInfo.getApplicationType())) {
            if ("0".equals(category)) {
                filePath = File.separator + basecpc + File.separator + designs + File.separator + uuid1 + File.separator + "new";
            } else {
                filePath = File.separator + basecpc + File.separator + designs + File.separator + uuid1 + File.separator + "others" + File.separator + uuid2;
            }
        } else if ("3".equals(shunchaoCaseInfo.getApplicationType())) {
            if ("0".equals(category)) {
                filePath = File.separator + basecpc + File.separator + pCT_inventions + File.separator + uuid1 + File.separator + "new";
            } else {
                filePath = File.separator + basecpc + File.separator + pCT_inventions + File.separator + uuid1 + File.separator + "others" + File.separator + uuid2;
            }

        } else if ("4".equals(shunchaoCaseInfo.getApplicationType())) {
            if ("0".equals(category)) {
                filePath = File.separator + basecpc + File.separator + pCT_utility + File.separator + uuid1 + File.separator + "new";
            } else {
                filePath = File.separator + basecpc + File.separator + pCT_utility + File.separator + uuid1 + File.separator + "others" + File.separator + uuid2;
            }

        } else if ("5".equals(shunchaoCaseInfo.getApplicationType())) {
            if ("0".equals(category)) {
                filePath = File.separator + basecpc + File.separator + reexamination + File.separator + uuid1 + File.separator + "new";
            } else {
                filePath = File.separator + basecpc + File.separator + reexamination + File.separator + uuid1 + File.separator + "others" + File.separator + uuid2;
            }

        } else if ("6".equals(shunchaoCaseInfo.getApplicationType())) {
            if ("0".equals(category)) {
                filePath = File.separator + basecpc + File.separator + invalidation + File.separator + uuid1 + File.separator + "new";
            } else {
                filePath = File.separator + basecpc + File.separator + invalidation + File.separator + uuid1 + File.separator + "others" + File.separator + uuid2;
            }

        }
        for (ShunchaoAttachmentInfo shunchaoAttachmentInfo : shunchaoAttachmentInfoList) {
            String path = cpcBinPathWindowsComputer + filePath + File.separator + shunchaoAttachmentInfo.getTableCode();
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();// 创建文件根目录
            }
            String attachmentSuffix = shunchaoAttachmentInfo.getAttachmentSuffix();
            if (".xml".equals(attachmentSuffix)) {
                attachmentSuffix = ".doc";
            }
            //相对路径
            String fileName = "";
            if ("100104".equals(shunchaoAttachmentInfo.getTableCode()) || "100112".equals(shunchaoAttachmentInfo.getTableCode())) {
                fileName = shunchaoAttachmentInfo.getSysFileName().substring(0, shunchaoAttachmentInfo.getSysFileName().indexOf(".")) + ".doc";

            } else {
                fileName = shunchaoAttachmentInfo.getTableCode() + attachmentSuffix;
            }
            String relative = filePath + File.separator + shunchaoAttachmentInfo.getTableCode() + File.separator + fileName;
            //保存路径
            String savePath = file.getPath() + File.separator + shunchaoAttachmentInfo.getSysFileName();

            HashMap<String, Object> mapPara = new HashMap<>();
            mapPara.put("id", shunchaoAttachmentInfo.getId());
            HttpResponse execute = HttpRequest.get(connecturl + "/attachment/shunchaoAttachmentInfo/downloadAttachment").
                    header("X-Access-Token", token).form(mapPara).execute();
            String body1 = execute.body();
            JSONObject parseObject = JSONObject.parseObject(body1);
            String message = parseObject.getString("message");
            byte[] bytes1 = Base64Utils.decodeFromString(message);

            FileCopyUtils.copy(bytes1, new BufferedOutputStream(new FileOutputStream(new File(savePath))));

            if (StringUtils.isBlank(shunchaoAttachmentInfo.getParentId())) {

                Table wjtable = db.getTable("DZSQ_KHD_SQWJ");
                Map<String, Object> mapWj = new HashMap<>();
                mapWj.put("WENJIANMC", shunchaoAttachmentInfo.getAttachmentName());
                mapWj.put("BIAOGEDM", shunchaoAttachmentInfo.getTableCode());
                mapWj.put("WENJIANLX", shunchaoAttachmentInfo.getFileType());//CPC文件类型 0:新申请 1:附加文件 2;中间文件(除附加文件外) 3:修改译文 4:修改文本
            /*if (".xml".equals(attachmentSuffix)) {
                mapWj.put("CHUANGJIANLX","0");//新建
            } else {
                mapWj.put("CHUANGJIANLX","1");//导入
            }*/
                mapWj.put("CHUANGJIANLX", shunchaoAttachmentInfo.getCpcCreateType());//CPC文件创建类型 0:新建 1:导入
                mapWj.put("CHUANGJIANRQ", new Date());
                mapWj.put("CUNCHULJ", relative);
                mapWj.put("WENJIANZT", "0");
                mapWj.put("COUNTS", shunchaoAttachmentInfo.getCounts() == null ? 0 : Integer.parseInt(shunchaoAttachmentInfo.getCounts()));
                mapWj.put("PAGES", shunchaoAttachmentInfo.getPages() == null ? 0 : Integer.parseInt(shunchaoAttachmentInfo.getPages()));
                wjtable.addRowFromMap(mapWj);

                for (Row row : wjtable) {
                    if (row.getString("CUNCHULJ") != null && row.getString("CUNCHULJ").contains(uuid1) && row.getString("ANJUANBH") == null) {
                        System.out.println("进入修改");
                        row.put("ANJUANBH", anjuanbh);
                        wjtable.updateRow(row);
                    }
                }
            }

        }
    }

    @Override
    public String getNoticesByPatentNo(String callback, String token, HttpServletRequest req) {
        //todo 获取系统的所有案件的内部编号
        String bodyInternal = HttpRequest.get(connecturl + "/caseinfo/shunchaoCaseInfo/selectCaseInfoUsedCpc").
                header("X-Access-Token", token).execute().body();

        JSONObject jsonObjectCaseInfo = JSONObject.parseObject(bodyInternal);
        JSONArray jsonArray = (JSONArray) jsonObjectCaseInfo.get("result");
        List<HashMap> mapList = JSONObject.parseArray(jsonArray.toJSONString(), HashMap.class);
        int count = 0;

        Connection conn = null;
        try {
            //获取官文数量
            String[] column;
            String[] dateFormatColumn = {"FAWENRQ", "DAFURQ", "XIAZAIRQ", "GongBuR", "JinRuSSR", "ShouQuanGGR"};

            conn = DBHelper.getConnection();
            List<Map<String, Object>> queryMapListBySql = forkJoinPool.submit(new GetNoticesTask(0, mapList.size(), mapList, conn)).get();
            List<Object> tongzhisbh = queryMapListBySql.stream().map(e -> e.get("TONGZHISBH")).collect(Collectors.toList());
            log.info("tongzhisbh : " + tongzhisbh);

            Map<String, Object> paramMap = null;
            for (Map<String, Object> queryMap : queryMapListBySql) {
                    paramMap = new HashMap<>();
                    ShunchaoAttachmentInfo t = new ShunchaoAttachmentInfo();

                    for (Map.Entry<String, Object> entry : queryMap.entrySet()) {
                        if (Arrays.asList(dateFormatColumn).contains(entry.getKey())) {
                            paramMap.put(entry.getKey().toLowerCase(), DateUtil.format((Date) entry.getValue(), "yyyy-MM-dd"));
                        }else {
                            paramMap.put(entry.getKey().toLowerCase(), entry.getValue());
                        }
                    }
                    String tongzhishubh = (String) queryMap.get("TONGZHISBH");
                    File file = new File(CpcPathInComputer.getCpcBinPathWindowsComputer() + File.separator + basecpc + File.separator + notices + File.separator + tongzhishubh);

                    if (file.exists()) {
                        paramMap.put("file", ZipUtil.zip(file));
                        HttpResponse execute = HttpRequest.post(connecturl + "/notice/shunchaoDzsqKhdTzs/upload").
                                header("X-Access-Token", token).form(paramMap).execute();
                        String body = execute.body();
                        JSONObject jsonObject = JSONObject.parseObject(body);
                        Boolean success = (Boolean) jsonObject.get("success");
                        Integer code = (Integer) jsonObject.get("code");
                        if (!success) {
                            if (40002 == code) {
                                log.info("通知书编号：" + tongzhishubh + " 对应的通知书系统已经获取，无需重复获取，发明名称为：" + (String) queryMap.get("FAMINGMC") + "，内部编号为：" + (String) queryMap.get("NEIBUBH"));
                            } else {
                                log.info("通知书编号：" + tongzhishubh + " 对应的通知书获取失败，发明名称为：" + (String) queryMap.get("FAMINGMC") + "，内部编号为：" + (String) queryMap.get("NEIBUBH"));
                                return JSONObject.toJSONString(Result.error(500, "从CPC获取官文失败"));
                            }
                        } else {
                            count++;
                        }
                    }
                }
                //String internalNumber = mapList.get(i).get("internalNumber").toString();
        } catch (Exception e) {
            log.error("从CPC获取官文失败", e);
            if (StringUtils.isNotBlank(callback)) {
                String string = JSONObject.toJSONString(Result.error(500, "从CPC获取官文失败"));
                return callback + "(" + string + ")";
            } else {
                return JSONObject.toJSONString(Result.error(500, "从CPC获取官文失败"));
            }
        }
        if (StringUtils.isNotBlank(callback)) {
            String string = JSONObject.toJSONString(Result.ok("成功获取官文：" + count));
            return callback + "(" + string + ")";
        } else {
            return JSONObject.toJSONString(Result.ok("成功获取官文：" + count));
        }
    }


}

class GetNoticesTask extends RecursiveTask<List<Map<String, Object>>> {
    public GetNoticesTask (int begin, int end, List<HashMap> paramList, Connection conn) {
        this.begin = begin;
        this.end = end;
        this.paramList = paramList;
        this.conn = conn;
    }

    private static final  Integer  ADJUST_VALUE  =  10;
    private int begin;
    private int end;
    List<HashMap> paramList;
    Connection conn;
    ConcurrentLinkedQueue<RuntimeException> exp = new ConcurrentLinkedQueue();
    List<Map<String, Object>> datas = new ArrayList<>();
    @Override
    protected List<Map<String, Object>> compute() {
        if(end - begin <= ADJUST_VALUE){
            String[] column = null;
            StringBuilder sql = null;
            List<Map<String, Object>> resultMaps = null;
            for (HashMap param : paramList.subList(begin, end)) {
                sql = new StringBuilder("");
                String patentNumber = param.get("patentNumber").toString();
                String internalNumber = param.get("internalNumber").toString();
                if(StringUtils.isNotBlank(patentNumber)){
                    sql.append("select SQXX.SHENQINGH, TZS.TONGZHISBH, TZS.TONGZHISDM, TZS.FAMINGMC, TZS.FAWENXLH, TZS.TONGZHISMC, TZS.SHENQINGBH, TZS.FAWENRQ" +
                            ", TZS.DAFURQ,TZS.QIANMINGXX, TZS.ZHUCEDM, TZS.XIAZAIRQ, TZS.XIAZAICS, TZS.ZHUANGTAI, TZS.SHIFOUSC, TZS.NEIBUBH, TZS.GongBuH" +
                            ", TZS.GongBuR,TZS.JinRuSSR,TZS.ShouCiNFND, TZS.WaiGuanFLH,TZS.ShouQuanGGH,TZS.ShouQuanGGR,TZS.DaoChuZT,TZS.QIANZHANGBJ " +
                            "from DZSQ_KHD_TZS TZS LEFT JOIN DZSQ_KHD_SHENQINGXX SQXX ON SQXX.SHENQINGBH = TZS.SHENQINGBH WHERE SHIFOUSC = '0' " +
                            "AND SQXX.SHENQINGH = '" + patentNumber +"'");
                    column = new String[]{"TONGZHISBH", "TONGZHISDM", "FAMINGMC", "FAWENXLH", "TONGZHISMC", "SHENQINGBH","SHENQINGH",
                            "FAWENRQ", "DAFURQ", "QIANMINGXX", "ZHUCEDM", "XIAZAIRQ", "XIAZAICS", "ZHUANGTAI", "SHIFOUSC",
                            "NEIBUBH", "GongBuH", "GongBuR", "JinRuSSR", "ShouCiNFND", "WaiGuanFLH", "ShouQuanGGH", "ShouQuanGGR",
                            "DaoChuZT", "QIANZHANGBJ"};
                }else {
                    sql.append("select TZS.TONGZHISBH, TZS.TONGZHISDM, TZS.FAMINGMC, TZS.FAWENXLH, TZS.TONGZHISMC, TZS.SHENQINGBH, TZS.FAWENRQ" +
                            ", TZS.DAFURQ,TZS.QIANMINGXX, TZS.ZHUCEDM, TZS.XIAZAIRQ, TZS.XIAZAICS, TZS.ZHUANGTAI, TZS.SHIFOUSC, TZS.NEIBUBH, TZS.GongBuH" +
                            ", TZS.GongBuR,TZS.JinRuSSR,TZS.ShouCiNFND, TZS.WaiGuanFLH,TZS.ShouQuanGGH,TZS.ShouQuanGGR,TZS.DaoChuZT,TZS.QIANZHANGBJ" +
                            " from DZSQ_KHD_TZS TZS WHERE SHIFOUSC = '0' AND NEIBUBH = '" + internalNumber + "'");
                    column = new String[]{"TONGZHISBH", "TONGZHISDM", "FAMINGMC", "FAWENXLH", "TONGZHISMC", "SHENQINGBH",
                            "FAWENRQ", "DAFURQ", "QIANMINGXX", "ZHUCEDM", "XIAZAIRQ", "XIAZAICS", "ZHUANGTAI", "SHIFOUSC",
                            "NEIBUBH", "GongBuH", "GongBuR", "JinRuSSR", "ShouCiNFND", "WaiGuanFLH", "ShouQuanGGH", "ShouQuanGGR",
                            "DaoChuZT", "QIANZHANGBJ"};
                }

                try {
                    resultMaps = DBHelper.queryMapListBySql(conn, sql.toString(), column);
                }catch (Exception e) {
                    exp.add(new RuntimeException("专利号：" + patentNumber + "；内部编号：" + internalNumber + "从CPC获取官文失败",e));
                    e.printStackTrace();
                }

                isError(exp); //异常抛出
                datas.addAll(resultMaps);
            }
        }else {
            int middle = (begin + end)/2;
            GetNoticesTask leftTask = new GetNoticesTask(begin, middle, paramList, conn);
            GetNoticesTask rightTask = new GetNoticesTask(middle, end, paramList, conn);
            leftTask.fork();
            rightTask.fork();
            datas.addAll(leftTask.join());
            datas.addAll(rightTask.join());
        }
        return datas;
    }

    public void isError(ConcurrentLinkedQueue<RuntimeException> exceptions){
        if(!exceptions.isEmpty()) {
            throw exceptions.poll();
        }
    }
}
