package com.csx.retrofitdemo;

import android.app.Application;
import com.netlibrary.RetrofitManager;

/**
 * Date: 2019/12/19
 * create by cuishuxiang
 * description:
 */
public class MyApp extends Application {
    public static ApiServices baseApi;
    @Override
    public void onCreate() {
        super.onCreate();
        RetrofitManager<ApiServices> retrofitManager = new RetrofitManager.NetBuilder(this)
                .setBaseUrl("")
                .build();

        baseApi = retrofitManager.getInstance();
    }
}
