package com.shunchao.cpc.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSONObject;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;
import com.shunchao.config.CpcPathInComputer;
import com.shunchao.cpc.model.ShunchaoAttachmentInfo;
import com.shunchao.cpc.model.ShunchaoCaseInfo;
import com.shunchao.cpc.service.IShuncaoConnectService;
import com.shunchao.cpc.util.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import org.springframework.util.FileCopyUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
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


    @Transactional(rollbackFor = Exception.class)
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
            if ("100104".equals(shunchaoAttachmentInfo.getTableCode())) {
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
}
