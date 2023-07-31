package com.zkx.springbootminio.vo;

public class FileVo {
    /**
     * 原文件名
     */
    private String oldFileName;
    /**
     * 新文件名
     */
    private String newFileName;
    /**
     * 文件路径
     */
    private String fileUrl;

    public String getOldFileName() {
        return oldFileName;
    }

    public void setOldFileName(String oldFileName) {
        this.oldFileName = oldFileName;
    }

    public String getNewFileName() {
        return newFileName;
    }

    public void setNewFileName(String newFileName) {
        this.newFileName = newFileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
