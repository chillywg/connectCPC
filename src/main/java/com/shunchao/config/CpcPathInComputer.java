package com.shunchao.config;

public class CpcPathInComputer {

    private static String CPC_BIN_PATH_WINDOWS_COMPUTER;
    private static String CPC_CLIENT_PATH_WINDOWS_COMPUTER;
    private static String CPC_DATA_PATH_WINDOWS_COMPUTER;

    public static String getCpcBinPathWindowsComputer() {
        return CPC_BIN_PATH_WINDOWS_COMPUTER;
    }

    public static void setCpcBinPathWindowsComputer(String cpcBinPathWindowsComputer) {
        CPC_BIN_PATH_WINDOWS_COMPUTER = cpcBinPathWindowsComputer;
    }

    public static String getClientCpcPathWindowsComputer() {
        return CPC_CLIENT_PATH_WINDOWS_COMPUTER;
    }

    public static void setClientCpcPathWindowsComputer(String cpcClientPathWindowsComputer) {
        CPC_CLIENT_PATH_WINDOWS_COMPUTER = cpcClientPathWindowsComputer;
    }

    public static String getCpcDataPathWindowsComputer() {
        return CPC_DATA_PATH_WINDOWS_COMPUTER;
    }

    public static void setCpcDataPathWindowsComputer(String cpcDataPathWindowsComputer) {
        CPC_DATA_PATH_WINDOWS_COMPUTER = cpcDataPathWindowsComputer;
    }

}
