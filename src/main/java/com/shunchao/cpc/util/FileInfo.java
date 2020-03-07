package com.shunchao.cpc.util;

import lombok.Data;

@Data
public class FileInfo {
    private String baseFileName;
    private Long size;
    private String ownerId;
    private Long version;
    private String sha256;
    private Boolean allowExternalMarketplace;
    private Boolean userCanWrite;
    private Boolean supportsUpdate;
    private Boolean supportsLocks;

}
