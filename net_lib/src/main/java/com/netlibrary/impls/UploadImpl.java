package com.netlibrary.impls;

/**
 * Date: 2020/1/6
 * create by cuishuxiang
 * description: 上传文件进度回调
 */
public interface UploadImpl {
    //void onUploadSuccess();

    //注意该方法在子线程
    @android.support.annotation.WorkerThread
    void onUploadProgress(int progress);

    //void onUploadFailed();
}
