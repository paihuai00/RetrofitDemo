package com.csx.retrofitdemo.beans;

/**
 * Date: 2019/12/23
 * create by cuishuxiang
 * description:
 */
public class VideoJsonBean {

    /**
     * pageindex : 1
     * pageSize : 10
     * userId : 5dfe7030-595f-4d67-aa56-98c8d9e43837
     */

    private int pageindex;
    private int pageSize;
    private String userId;

    public int getPageindex() {
        return pageindex;
    }

    public void setPageindex(int pageindex) {
        this.pageindex = pageindex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
