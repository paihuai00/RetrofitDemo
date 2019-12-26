package com.netlibrary.interceptors;


import com.netlibrary.net_utils.JsonUtil;
import com.netlibrary.net_utils.NetLogUtil;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * create by cuishuxiang
 *
 * @date : 2019/1/25
 * @description: 网络请求拦截器
 */
public class HttpLogInterceptor implements HttpLoggingInterceptor.Logger {
    private static final String TAG = "HttpLogInterceptor";
    private StringBuilder mMessage = new StringBuilder();

    @Override
    public void log(String message) {
        //在这里处理Log数据
        // 请求或者响应开始
        if (message.startsWith("--> Start")) {
            mMessage.setLength(0);
        }
        // 以{}或者[]形式的说明是响应结果的json数据，需要进行格式化
        if ((message.startsWith("{") && message.endsWith("}"))
                || (message.startsWith("[") && message.endsWith("]"))) {
            message = JsonUtil.formatJson(message);
        }
        mMessage.append(message.concat("\n"));
        // 请求或者响应结束，打印整条日志
        if (message.startsWith("<-- END HTTP")) {
            NetLogUtil.d(mMessage.toString());
        }
    }
}
