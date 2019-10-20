package com.su.pojo;


/**
 * @author su
 * @date 2019/10/20 11:24
 */
public class UploadConfig {
    private String suffix;
    private Integer filesizetourists;
    private Integer filesizeuser;
    private Integer imgcounttourists;
    private Integer imgcountuser;
    private Integer urltype;
    private Integer api;
    /**
     * 已被配置文件代替
     * 游客可用内存, 单位: KB, 值为 -1 则表示无限制
     */
    private Integer visitormemory;
    private Integer usermemory;

    public UploadConfig() {
    }

    public UploadConfig(String suffix, Integer filesizetourists, Integer filesizeuser, Integer imgcounttourists, Integer imgcountuser, Integer urltype, Integer api, Integer visitormemory, Integer usermemory) {
        this.suffix = suffix;
        this.filesizetourists = filesizetourists;
        this.filesizeuser = filesizeuser;
        this.imgcounttourists = imgcounttourists;
        this.imgcountuser = imgcountuser;
        this.urltype = urltype;
        this.api = api;
        this.visitormemory = visitormemory;
        this.usermemory = usermemory;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public Integer getFilesizetourists() {
        return filesizetourists;
    }

    public void setFilesizetourists(Integer filesizetourists) {
        this.filesizetourists = filesizetourists;
    }

    public Integer getFilesizeuser() {
        return filesizeuser;
    }

    public void setFilesizeuser(Integer filesizeuser) {
        this.filesizeuser = filesizeuser;
    }

    public Integer getImgcounttourists() {
        return imgcounttourists;
    }

    public void setImgcounttourists(Integer imgcounttourists) {
        this.imgcounttourists = imgcounttourists;
    }

    public Integer getImgcountuser() {
        return imgcountuser;
    }

    public void setImgcountuser(Integer imgcountuser) {
        this.imgcountuser = imgcountuser;
    }

    public Integer getUrltype() {
        return urltype;
    }

    public void setUrltype(Integer urltype) {
        this.urltype = urltype;
    }


    public Integer getApi() {
        return api;
    }

    public void setApi(Integer api) {
        this.api = api;
    }

    public Integer getVisitormemory() {
        return visitormemory;
    }

    public void setVisitormemory(Integer visitormemory) {
        this.visitormemory = visitormemory;
    }

    public Integer getUsermemory() {
        return usermemory;
    }

    public void setUsermemory(Integer usermemory) {
        this.usermemory = usermemory;
    }
}
