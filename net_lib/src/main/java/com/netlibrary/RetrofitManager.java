package com.netlibrary;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.ihsanbal.logging.Level;
import com.ihsanbal.logging.LoggingInterceptor;
import java.io.File;
import java.util.concurrent.TimeUnit;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.internal.platform.Platform;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Date: 2019/12/19
 * create by cuishuxiang
 * description:  Retrofit 管理
 *
 * 变量参数{@link RetrofitController}
 */
public class RetrofitManager<T> {
    private OkHttpClient okHttpClient;
    private long retryTimeOut = 60;//默认超时时间，不在Builder中设置，就使用下面的默认时间，单位：秒
    private long connectTimeOut = 60;//连接超时
    private long readTimeOut = 60;
    private long cacheSize = 20 * 1024 * 1024;//缓存大小为：20m
    private File fileCache;//缓存路径

    //默认Retrofit实例
    private static Retrofit normalRetrofit;
    private static String baseUrl;//需要设置baseUrl

    //private static Retrofit otherRetrofit;

    //private static HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

    private  Context context;

    private Class<T> apiClass;

    private boolean isShowLog = true;//是否显示日志
    private Interceptor[] interceptors;//拦截器

    //日志打印级别，默认是 BASIC
    private Level mLevel = Level.BASIC;

    //如果api比较多的时候，可以考虑通过map、存储创建的apis
    //    private Map<Class<?>, Object> apis = new HashMap<>();

    private RetrofitManager(Context context) {
        this.context = context;
        //设置缓存路径
        fileCache = new File(context.getCacheDir(), "net_cache");

        NetLogUtil.init(isShowLog, isShowLog);
    }

    public void setRetryTimeOut(long retryTimeOut) {
        this.retryTimeOut = retryTimeOut;
    }

    public void setConnectTimeOut(long connectTimeOut) {
        this.connectTimeOut = connectTimeOut;
    }

    public void setReadTimeOut(long readTimeOut) {
        this.readTimeOut = readTimeOut;
    }

    public void setCacheSize(long cacheSize) {
        this.cacheSize = cacheSize;
    }

    public void setFileCache(File fileCache) {
        this.fileCache = fileCache;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public void setApiClass(Class<T> apiClass) {
        this.apiClass = apiClass;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setIsPrintLog(boolean isPrintLog) {
        this.isShowLog = isPrintLog;
        NetLogUtil.init(isPrintLog, isShowLog);
    }

    public void setLogLevel(Level level) {
        this.mLevel = level;
    }

    /**
     * 动态添加拦截器
     */
    public void setsetInterceptors(Interceptor... interceptors) {
        this.interceptors = interceptors;
    }

    /**
     * 获得默认的 定义的接口，ApiServer
     */
    public T getInstance() {
        if (TextUtils.isEmpty(baseUrl)) {
            throw new IllegalArgumentException("RetrofitManager ： Please set BaseUrl！");
        }

        initOkhttpClient();

        normalRetrofit = new Retrofit.Builder().client(okHttpClient)
                .baseUrl(baseUrl)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return normalRetrofit.create(apiClass);
    }

    /**
     * 得到 不同 baseUrl，
     */
    //public static Class<?> getApiService(Class<?> cls, String baseUrl) {
    //    if (TextUtils.isEmpty(baseUrl)) {
    //        throw new IllegalArgumentException("RetrofitManager ： Please set BaseUrl！");
    //    }
    //
    //    initOkhttpClient();
    //
    //    otherRetrofit = new Retrofit.Builder().client(okHttpClient)
    //            .baseUrl(baseUrl)
    //            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    //            .addConverterFactory(GsonConverterFactory.create())
    //            .build();
    //
    //    return (Class<?>) otherRetrofit.create(cls);
    //}

    /**
     * 初始化OkHttpClient
     */
    private void initOkhttpClient() {
        if (okHttpClient == null) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder().readTimeout(retryTimeOut, TimeUnit.SECONDS)
                            .retryOnConnectionFailure(true)
                            .cache(new Cache(fileCache, cacheSize))//缓存
                            .connectTimeout(connectTimeOut, TimeUnit.SECONDS)
                            .readTimeout(readTimeOut, TimeUnit.SECONDS);

                    // 信任Https,忽略Https证书验证
                    // https认证,如果要使用https且为自定义证书 可以去掉这两行注释，并自行配制证书。
                    //.sslSocketFactory(SSLSocketTrust.getSSLSocketFactory())
                    //.hostnameVerifier(SSLSocketTrust.getHostnameVerifier())

            //用于打印网络请求
            if (isShowLog) {
                //打印http请求
                //HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor(new HttpLogger());
                //logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);//打印请求体
                //builder.addInterceptor(logInterceptor);

                LoggingInterceptor loggingInterceptor = new LoggingInterceptor.Builder()
                        .log(Platform.INFO)
                        .loggable(true)
                        .setLevel(mLevel)
                        .request("Net_Request")
                        .response("Net_Response")
                        .build();
                builder.addInterceptor(loggingInterceptor);
            }

            //将手动添加的拦截器组装起来
            if (interceptors != null && interceptors.length != 0) {
                for (Interceptor i : interceptors) {
                    builder.addInterceptor(i);
                }
            }

            okHttpClient = builder.build();
        }
    }

    /**
     * Builder模式构建 管理类的参数
     */
    public static class NetBuilder<T> {
        private RetrofitController retrofitController;

        public NetBuilder(Context context) {
            retrofitController = new RetrofitController();
            retrofitController.mContext = context;
        }

        public NetBuilder setBaseUrl(String baseUrl) {
            retrofitController.baseUrl = baseUrl;
            return this;
        }

        public NetBuilder setApiClass(Class<T> apiClass) {
            retrofitController.apiClass = apiClass;
            return this;
        }

        public NetBuilder setCacheFile(File fileCache) {
            retrofitController.fileCache = fileCache;
            return this;
        }

        /**
         * 设置缓存文件大小
         */
        public NetBuilder setCacheFile(long fileCacheSize) {
            retrofitController.cacheSize = fileCacheSize;
            return this;
        }

        public NetBuilder setRetryTime(long retryTime) {
            retrofitController.retryTimeOut = retryTime;
            return this;
        }

        public NetBuilder setReadTimeOutTime(long readTimeOutTime) {
            retrofitController.readTimeOut = readTimeOutTime;
            return this;
        }

        /**
         * 设置超时时间
         */
        public NetBuilder setTimeOutTime(long timeOutTime) {
            retrofitController.timeOut = timeOutTime;
            return this;
        }

        public NetBuilder setIsPrintLog(boolean isPrintLog) {
            retrofitController.isPrintLog = isPrintLog;
            return this;
        }

        //设置日志打印级别
        public NetBuilder setIsPrintLog(Level level) {
            retrofitController.mLogLevel = level;
            return this;
        }

        //添加拦截器
        public NetBuilder setInterceptors(Interceptor... interceptors) {
            retrofitController.interceptors = interceptors;
            return this;
        }

        public RetrofitManager build() {
            RetrofitManager retrofitManager = new RetrofitManager(retrofitController.mContext);

            if (!TextUtils.isEmpty(retrofitController.baseUrl)) {
                retrofitManager.setBaseUrl(retrofitController.baseUrl);
            }

            if (retrofitController.apiClass != null) {
                retrofitManager.setApiClass(retrofitController.apiClass);
            }

            //组装参数
            if (retrofitController.fileCache != null) {
                retrofitManager.setFileCache(retrofitController.fileCache);
            }

            if (retrofitController.cacheSize != 0) {
                retrofitManager.setCacheSize(retrofitController.cacheSize);
            }

            if (retrofitController.retryTimeOut != 0) {
                retrofitManager.setRetryTimeOut(retrofitController.retryTimeOut);
            }

            if (retrofitController.readTimeOut != 0) {
                retrofitManager.setReadTimeOut(retrofitController.readTimeOut);
            }

            //是否打印日志，默认都是true
            retrofitManager.setIsPrintLog(retrofitController.isPrintLog);

            if (retrofitController.timeOut != 0) {
                retrofitManager.setConnectTimeOut(retrofitController.timeOut);
            }

            return retrofitManager;
        }
    }
}
