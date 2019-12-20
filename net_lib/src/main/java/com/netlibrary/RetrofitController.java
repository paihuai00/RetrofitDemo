package com.netlibrary;

import android.content.Context;
import java.io.File;
import okhttp3.Interceptor;

/**
 * Date: 2019/12/19
 * create by cuishuxiang
 * description:  {@link RetrofitManager} 参数控制类
 */
public class RetrofitController {
    public Context mContext;
    public File fileCache;//缓存路径
    public String baseUrl;
    public boolean isPrintLog = true;//是否打印日志
    public long retryTimeOut;
    public long timeOut;//
    public long readTimeOut;
    public long cacheSize;//缓存大小

    //
    public Interceptor[] interceptors;
}
