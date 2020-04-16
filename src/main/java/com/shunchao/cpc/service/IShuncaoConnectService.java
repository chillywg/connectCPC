package com.shunchao.cpc.service;


import com.shunchao.cpc.model.ShunchaoAttachmentInfo;
import com.shunchao.cpc.model.ShunchaoCaseInfo;

import java.io.FileNotFoundException;
import java.util.List;

public interface IShuncaoConnectService {
    public void sendCase(ShunchaoCaseInfo shunchaoCaseInfo, List<ShunchaoAttachmentInfo> shunchaoAttachmentInfoList, String token,String category) throws Exception;
}
