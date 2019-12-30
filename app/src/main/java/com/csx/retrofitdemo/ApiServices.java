package com.csx.retrofitdemo;

import com.csx.retrofitdemo.beans.VideoJsonBean;
import com.netlibrary.net_utils.DownLoadUtils;
import io.reactivex.Observable;
import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * create by cuishuxiang
 *
 * @date : 2019/1/15
 * @description:
 */
public interface ApiServices {

    //=============   GET   =========================
    //无参数
    @GET("6/1")
    Observable<ResponseBody> getGirlData();

    //无参，改变url
    //例如：下面mouth，会将@GET()中的{mouth}替换掉，达到动态url的效果
    @GET("{mouth}/{day}")
    Observable<ResponseBody> getGirlDataByDate(@Path("mouth") String mouth,
                                         @Path("day") String day);

    //有参数get
    @GET("v5/hourly")
    Observable<WeatherBean> getWeatherData(@Query("city") String city,
                                     @Query("key") String key);

    Call<WeatherBean> getWeatherData(@QueryMap Map<String, String> params);

    //直接传入url
    @GET
    Observable<ResponseBody> getDataByUrl(@Url String url);
    /**
     * 文件下载
     *
     * 大文件的时候，要加：@Streaming 不然会报OOM
     */
    @Streaming
    @GET("2012031220134655.jpg")
    Observable<ResponseBody> downLoadFile();

    /**
     * 断点下载 文件，Range：https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Headers/Range
     *
     * @param range : {@link DownLoadUtils#getUrlRange(String, String, String)}
     * @return
     */
    @Streaming
    @GET("")
    Observable<ResponseBody> downLoadApkFile(@Header("Range") String range,@Url String apkUrl);
    //=============   POST   =========================

    @POST("/hotkey/json")
    Observable<ResponseBody> postWithOutParams();

    /**
     * 表单上传，需要添加{@link retrofit2.http.FormUrlEncoded} 注解
     * post参数通过 @Field 添加
     *
     * @param key
     * @param league
     * @return
     */
    @FormUrlEncoded
    @POST("football/league")
    Observable<ResponseBody> postFootBallData(@Field("key") String key,
                                        @Field("league") String league);


    /**
     * 上传 json 到服务器
     * @param jsonBean
     * @return
     */
    @Headers("Content-Type: application/json")//声明为json
    @POST("xbqs/user/GetUserVideoListPage")
    Observable<ResponseBody> postJsonBean(@Body VideoJsonBean jsonBean);

    /**
     * 上传 json 到服务器
     */
    @POST("xbqs/user/GetUserVideoListPage")
    Observable<ResponseBody> postJsonRequestBody(@Body RequestBody requestBody);

    /**
     * 文件上传 图片
     * post: http://118.123.20.133:8080/xbqs/Attachment/UploadAttach
     *
     * body:formData
     *
     * @Multipart 专门用于文件上传的注解
     */
    @Multipart
    @POST("xbqs/Attachment/UploadAttach")
    Observable<ResponseBody> upLoadFile(@Part List<MultipartBody.Part> parts);




    @Headers("Cache-Control: max-age=640000")//静态添加单个header
    @GET("")
    Call<ResponseBody> addHeadData();

    @GET("")
    Call<ResponseBody> addHeadData(@Header("Cache-Control") String headsMap);

    @GET("")
    Call<ResponseBody> addHeadData(@HeaderMap Map<String,String> headsMap);

}


