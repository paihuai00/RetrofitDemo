package com.netlibrary.impls;
import java.io.File;

/**
 * Date: 2019/12/23
 * create by cuishuxiang
 * description: Retrofit  文件下载回调接口
 *
 *
 * 注意： 进度回调在子线程
 */
public interface DownLoadImpl {
    @android.support.annotation.WorkerThread
    void onProgressCallBack(int progress);

    void onDownLoadFinish(File file);

    void onDownLoadFailed(String errorMsg);
}
