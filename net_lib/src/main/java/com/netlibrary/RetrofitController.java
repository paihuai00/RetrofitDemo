package com.netlibrary;

import android.content.Context;
import com.ihsanbal.logging.Level;
import java.io.File;
import java.util.Map;
import okhttp3.Interceptor;
import okhttp3.MediaType;

/**
 * Date: 2019/12/19
 * create by cuishuxiang
 * description:  {@link RetrofitManager} 参数控制类
 */
public class RetrofitController {
    /**
     * 常见的 Content-type
     */
    //上传json
    public static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");
    //表单上传文件
    public static final MediaType FORM_TYPE = MediaType.parse("multipart/form-data; charset=utf-8");
    //纯文本
    public static final MediaType TEXT_TYPE = MediaType.parse("text/plain; charset=utf-8");
    //图片
    public static final MediaType IMAGE_TYPE = MediaType.parse("image/*; charset=utf-8");


    public Context mContext;
    public File fileCache;//缓存路径
    public String baseUrl;
    public Class apiClass;
    public boolean isPrintLog = true;//是否打印日志
    public boolean isNeedPersistentCookie = false;//是否需要持久化cookie
    public long retryTimeOut;
    public long timeOut;//
    public long readTimeOut;
    public long cacheSize;//缓存大小
    public Level mLogLevel;
    public Map<String, String> headerMap;//添加公共的header

    //拦截器
    public Interceptor[] interceptors;

    public boolean isTrustAllCer = false;//是否信任所有证书
    public String cerString;//信任指定证书
    public int[] cerResIds;//信任指定证书
}
