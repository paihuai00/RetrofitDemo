package com.csx.retrofitdemo;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.csx.retrofitdemo.beans.VideoJsonBean;
import com.csx.retrofitdemo.utils.InstallApkUtils;
import com.csx.retrofitdemo.utils.PictureUtils;
import com.csx.retrofitdemo.utils.RxPermissionUtils;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.netlibrary.net_utils.NetLogUtil;
import com.netlibrary.RetrofitController;
import com.netlibrary.RetrofitManager;
import com.netlibrary.impls.DownLoadImpl;
import com.netlibrary.net_utils.DownLoadUtils;
import com.netlibrary.net_utils.RetrofitHelper;
import com.netlibrary.net_utils.SPUtils;
import com.netlibrary.net_utils.SchedulersHelper;
import com.tbruyelle.rxpermissions2.RxPermissions;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * 1,Get - 无参              {@link MainActivity#noParamsGet()}
 *
 * 2,Get - Url动态变化       {@link MainActivity#noParamsPathGet()}
 *
 * 3,Get - 有参              {@link MainActivity#paramsGet()}
 *
 * 4,直接传入Url             {@link MainActivity#directUrl()}
 *
 * 5,Post - 不带参数          {@link MainActivity#postWithOutParams()}
 *
 * 6,Post - 带参数           {@link MainActivity#postParamsFootBallData()}
 *
 * 7,Post - 上传json          {@link MainActivity#postJsonBean()}
 * - 上传json          {@link MainActivity#postJsonByRequestBody()}
 *
 * 8,Post - 上传file          {@link MainActivity#postFile(String)}
 *
 * 9，文件下载                 {@link MainActivity#downLoadFile()}
 *                    断点续传 {@link MainActivity#downLoadApkFile()}
 *
 * 9,常用类
 * - {@link RetrofitManager},Retrofit 管理类
 * - {@link RetrofitHelper}，用于生成 RequestBody、MultipartBody
 * - {@link RetrofitController} , 配置类，包含常用的 MediaType，以及RetrofitManager参数
 * - {@link DownLoadUtils}      ,下载utils
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @BindView(R.id.btn_no_params_get) Button mBtnNoParamsGet;
    @BindView(R.id.btn_params_get) Button mBtnParamsGet;
    @BindView(R.id.btn_normal_post) Button mBtnNormalPost;
    @BindView(R.id.btn_no_params_path_get) Button mBtnNoParamsPathGet;
    @BindView(R.id.btn_url) Button mBtnUrl;
    @BindView(R.id.tv_show_result) TextView mTvShowResult;
    @BindView(R.id.btn_params_post) Button mBtnParamsPost;
    @BindView(R.id.btn_json_post) Button mBtnJsonPost;
    @BindView(R.id.btn_upload_post) Button mBtnUploadPost;
    @BindView(R.id.btn_download_post) Button mBtnDownloadPost;
    @BindView(R.id.btn_json_post_requestbody) Button mBtnJsonRequest;
    private String imageLocalPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mTvShowResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTvShowResult.setText("");
            }
        });
    }

    /**
     * 常规Get请求
     */
    private void noParamsGet() {
        //http://gank.io/api/data/%E7%A6%8F%E5%88%A9/6/1
        String baseUrl = "http://gank.io/api/data/%E7%A6%8F%E5%88%A9/";//注意，这里的baseUrl：需要以“/”结尾
        RetrofitManager<ApiServices> retrofitManager =
                new RetrofitManager.NetBuilder(this).setBaseUrl(baseUrl)
                        .setApiClass(ApiServices.class)
                        .build();

        ApiServices serivces = retrofitManager.getInstance();

        Observable<ResponseBody> call = serivces.getGirlData();

        call.compose(SchedulersHelper.<ResponseBody>changeSchedulerObservable())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        try {
                            String successData = responseBody.string();
                            mTvShowResult.setText(successData);
                            Log.d(TAG, "onResponse: successData = " + successData);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d(TAG, "onFailure: " + throwable.getMessage().toString());
                        mTvShowResult.setText(throwable.getMessage());
                    }
                });
    }

    /**
     * Url动态变化 get请求
     * {@link Path} 注解
     */
    private void noParamsPathGet() {
        //http://gank.io/api/data/%E7%A6%8F%E5%88%A9/6/1
        String baseUrl = "http://gank.io/api/data/%E7%A6%8F%E5%88%A9/";//注意，这里的baseUrl：需要以“/”结尾

        RetrofitManager<ApiServices> retrofitManager =
                new RetrofitManager.NetBuilder(this).setBaseUrl(baseUrl)
                        .setApiClass(ApiServices.class)
                        .build();

        ApiServices serivces = retrofitManager.getInstance();

        Observable<ResponseBody> call = serivces.getGirlDataByDate("12", "1");

        call.compose(SchedulersHelper.<ResponseBody>changeSchedulerObservable())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        try {
                            String successData = responseBody.string();
                            mTvShowResult.setText(successData);
                            Log.d(TAG, "onResponse: successData = " + successData);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d(TAG, "onFailure: " + throwable.getMessage().toString());
                        mTvShowResult.setText(throwable.getMessage());
                    }
                });
    }

    //3,有参get请求

    /**
     * 带参数Get请求
     * {@link Query}注解使用
     */
    private void paramsGet() {
        //https://free-api.heweather.com/v5/now?city=北京&key=defbffa06a1846fe8bab0b271a9eca6e
        String baseUrl = "https://free-api.heweather.com/";

        RetrofitManager<ApiServices> retrofitManager =
                new RetrofitManager.NetBuilder(this).setBaseUrl(baseUrl)
                        .setApiClass(ApiServices.class)
                        .build();

        ApiServices serivces = retrofitManager.getInstance();

        Observable<WeatherBean> call =
                serivces.getWeatherData("北京", "defbffa06a1846fe8bab0b271a9eca6e");

        call.compose(SchedulersHelper.<WeatherBean>changeSchedulerObservable())
                .subscribe(new Consumer<WeatherBean>() {
                    @Override
                    public void accept(WeatherBean weatherBean) throws Exception {
                        try {
                            String successData = weatherBean.toString();
                            mTvShowResult.setText(successData);
                            Log.d(TAG, "onResponse: successData = " + successData);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        try {
                            mTvShowResult.setText(throwable.getMessage());
                            Log.d(TAG, "onResponse: throwable = " + throwable.getMessage());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    //4,直接传入Url

    /**
     * {@link Url}  @url注解的时候，baseUrl会被替换
     */
    private void directUrl() {
        String baseUrl = "https://free-api.heweather.com/";

        RetrofitManager<ApiServices> retrofitManager =
                new RetrofitManager.NetBuilder(this).setBaseUrl(baseUrl)
                        .setApiClass(ApiServices.class)
                        //.setIsPrintLog(true)
                        .build();

        ApiServices serivces = retrofitManager.getInstance();

        Observable<ResponseBody> call =
                serivces.getDataByUrl("http://gank.io/api/data/%E7%A6%8F%E5%88%A9/6/1");
        call.compose(SchedulersHelper.<ResponseBody>changeSchedulerObservable())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        try {
                            //主线程
                            String successData = responseBody.string();
                            mTvShowResult.setText(successData);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mTvShowResult.setText(throwable.getMessage());
                    }
                });
    }

    //post 不带参数
    private void postWithOutParams() {
        String baseUrl = "https://www.wanandroid.com/";

        RetrofitManager<ApiServices> retrofitManager =
                new RetrofitManager.NetBuilder(this).setBaseUrl(baseUrl)
                        .setApiClass(ApiServices.class)
                        //.setIsPrintLog(true)
                        .build();

        ApiServices serivces = retrofitManager.getInstance();

        Observable<ResponseBody> responseBodyObservable = serivces.postWithOutParams();

        responseBodyObservable.compose(SchedulersHelper.<ResponseBody>changeSchedulerObservable())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        String successData = null;
                        try {
                            successData = responseBody.string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mTvShowResult.setText(successData);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        NetLogUtil.d(TAG + " post 不带参数 " + throwable.getMessage());
                        mTvShowResult.setText("post 不带参数:  请求失败");
                    }
                });
    }


    /**
     * post 带参数,请求
     * 表单上传使用：@FormUrlEncoded + @Field
     *
     * 非表单上传直接使用：@Query
     */
    private void postParamsFootBallData() {
        String baseUrl = "http://op.juhe.cn/onebox/";

        RetrofitManager<ApiServices> retrofitManager =
                new RetrofitManager.NetBuilder(this).setBaseUrl(baseUrl)
                        .setApiClass(ApiServices.class)
                        //.setIsPrintLog(true)
                        .build();

        ApiServices apiServices = retrofitManager.getInstance();

        Observable<ResponseBody> observable =
                apiServices.postFootBallData("02da4926376156643455fafa48982456", "中超");

        observable.compose(SchedulersHelper.<ResponseBody>changeSchedulerObservable())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        //主线程
                        Log.d(TAG, "onResponse: ");
                        String successData = null;
                        try {
                            successData = responseBody.string();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mTvShowResult.setText(successData);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable t) throws Exception {
                        //主线程
                        Log.d(TAG, "onFailure: " + t.getMessage().toString());
                        mTvShowResult.setText(t.getMessage());
                    }
                });
    }

    /**
     * post Json
     * 方式1:  构建对象@Body对象上传
     */
    private void postJsonBean() {
        // http://118.123.20.133:8080/xbqs/user/GetUserVideoListPage
        String baseUrl = "http://118.123.20.133:8080/";

        RetrofitManager<ApiServices> retrofitManager =
                new RetrofitManager.NetBuilder(this).setBaseUrl(baseUrl)
                        .setApiClass(ApiServices.class)
                        //.setIsPrintLog(true)
                        .build();

        ApiServices apiServices = retrofitManager.getInstance();

        VideoJsonBean jsonBean = new VideoJsonBean();
        jsonBean.setPageindex(1);
        jsonBean.setPageSize(10);
        jsonBean.setUserId("5dfe7030-595f-4d67-aa56-98c8d9e43837");

        Observable<ResponseBody> bodyObservable = apiServices.postJsonBean(jsonBean);
        bodyObservable.compose(SchedulersHelper.<ResponseBody>changeSchedulerObservable())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        String successData = "";
                        try {
                            successData = responseBody.string();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mTvShowResult.setText(successData.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable t) throws Exception {
                        mTvShowResult.setText(t.getMessage());
                    }
                });
    }

    /**
     * post Json
     * 方式2:  构建对象@Body  RequestBody 上传
     *
     * {@link RetrofitHelper} 可以创建常用的body
     */
    private void postJsonByRequestBody() {
        // http://118.123.20.133:8080/xbqs/user/GetUserVideoListPage
        String baseUrl = "http://118.123.20.133:8080/";

        RetrofitManager<ApiServices> retrofitManager =
                new RetrofitManager.NetBuilder(this).setBaseUrl(baseUrl)
                        .setApiClass(ApiServices.class)
                        //.setIsPrintLog(true)
                        .build();

        ApiServices apiServices = retrofitManager.getInstance();

        //VideoJsonBean jsonBean = new VideoJsonBean();
        //jsonBean.setPageindex(1);
        //jsonBean.setPageSize(10);
        //jsonBean.setUserId("5dfe7030-595f-4d67-aa56-98c8d9e43837");
        Map<String, Object> stringObjectMap = new HashMap<>();
        stringObjectMap.put("pageindex", 1);
        stringObjectMap.put("pageSize", 10);
        stringObjectMap.put("userId", "5dfe7030-595f-4d67-aa56-98c8d9e43837");
        RequestBody requestBody = RetrofitHelper.getRequestBody(stringObjectMap);

        Observable<ResponseBody> bodyObservable = apiServices.postJsonRequestBody(requestBody);
        bodyObservable.compose(SchedulersHelper.<ResponseBody>changeSchedulerObservable())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        String successData = "";
                        try {
                            successData = responseBody.string();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mTvShowResult.setText(successData.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable t) throws Exception {
                        mTvShowResult.setText(t.getMessage());
                    }
                });
    }

    /**
     * 上传图片到服务器
     *
     * (需要读写权限)
     */
    private void postFile(String imagePath) {
        File file = new File(imagePath);
        if (!file.exists()) {
            showToast("图片选择有误，请重新选择");
            return;
        }

        // http://118.123.20.133:8080/xbqs/user/GetUserVideoListPage
        String baseUrl = "http://118.123.20.133:8080/";

        RetrofitManager<ApiServices> retrofitManager =
                new RetrofitManager.NetBuilder(this).setBaseUrl(baseUrl)
                        .setApiClass(ApiServices.class)
                        //.setIsPrintLog(true)
                        .build();

        ApiServices apiServices = retrofitManager.getInstance();

        Map<String, File> fileMap = new HashMap<>();
        fileMap.put("file", file);

        List<MultipartBody.Part> partList = RetrofitHelper.getMultipartBodyPartList(fileMap);

        Observable<ResponseBody> responseBodyObservable = apiServices.upLoadFile(partList);

        responseBodyObservable.compose(SchedulersHelper.<ResponseBody>changeSchedulerObservable())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        //主线程
                        Log.d(TAG, "onResponse: ");
                        String successData = null;
                        try {
                            successData = responseBody.string();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mTvShowResult.setText(successData);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable t) throws Exception {
                        //主线程
                        Log.d(TAG, "onFailure: " + t.getMessage().toString());
                        mTvShowResult.setText(t.getMessage());
                    }
                });
    }

    /**
     * 文件下载
     * 注意:
     * 1，{@link Streaming} 注解是否添加
     * 2，统一返回的类型为{@link ResponseBody},统一处理
     * 3, 保存在本地为耗时操作，放在子线程，下载进度回调也在子线程
     */
    private void downLoadFile() {
        String baseUrl = "http://www.pptbz.com/pptpic/UploadFiles_6909/201203/";

        String fileDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;

        final String fileName = "b74c3119b666237bd4af92be5.jpg";

        RetrofitManager<ApiServices> retrofitManager =
                new RetrofitManager.NetBuilder(this).setBaseUrl(baseUrl)
                        .setApiClass(ApiServices.class)
                        //.setIsPrintLog(true)
                        .build();

        ApiServices apiServices = retrofitManager.getInstance();

        //这需要读写权限
        Observable<ResponseBody> call = apiServices.downLoadFile();
        DownLoadUtils.downLoad(call, fileDir,fileName, new DownLoadImpl() {
            @Override
            public void onProgressCallBack(final int progress) {
                /**
                 * 进度回调在子线程
                 */
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTvShowResult.setText("下载进度：" + progress);
                    }
                });
            }

            @Override
            public void onDownLoadFinish(File file) {
                mTvShowResult.setText("下载完成,文件路径为：：" + file.getAbsolutePath());
            }

            @Override
            public void onDownLoadFailed(String errorMsg) {
                mTvShowResult.setText("下载失败：" + errorMsg);
            }
        });

        //call.compose(SchedulersHelper.<ResponseBody>changeSchedulerObservable())
        //        .map(new Function<ResponseBody, File>() {
        //            @Override
        //            public File apply(ResponseBody responseBody) throws Exception {
        //                return new File("");
        //            }
        //        })
        //        .subscribe(new Consumer<File>() {
        //            @Override
        //            public void accept(File file) throws Exception {
        //
        //            }
        //        }, new Consumer<Throwable>() {
        //            @Override
        //            public void accept(Throwable throwable) throws Exception {
        //
        //            }
        //        });

        //call.enqueue(new Callback<ResponseBody>() {
        //    @Override
        //    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
        //        //主线程
        //        if (response.isSuccessful()) {
        //            writeFileToSDCard(response.body());
        //            Toast.makeText(getApplicationContext(), "下载成功！", Toast.LENGTH_SHORT).show();
        //        } else {
        //            Toast.makeText(getApplicationContext(), "下载失败！", Toast.LENGTH_SHORT).show();
        //        }
        //        //                mTvShowResult.setText(successData);
        //    }
        //
        //    @Override
        //    public void onFailure(Call<ResponseBody> call, Throwable t) {
        //        //主线程
        //        Log.d(TAG, "onFailure: " + t.getMessage().toString());
        //        mTvShowResult.setText(t.getMessage());
        //    }
        //});
    }

    /**
     * 通过 http range 实现 断点续传下载
     */
    private void downLoadApkFile() {
        //http://dldir1.qq.com/qqmi/aphone_p2p/TencentVideo_V6.0.0.14297_848.apk
        String apkUrl = "http://dldir1.qq.com/qqmi/aphone_p2p/TencentVideo_V6.0.0.14297_848.apk";

        final String fileDir = Environment.getExternalStorageDirectory() +File.separator+"down_apk";
        final String fileName ="tencentvideo.apk";

        RetrofitManager<ApiServices> retrofitManager =
                new RetrofitManager.NetBuilder(this)
                        .setBaseUrl("http://dldir1.qq.com/qqmi/aphone_p2p/")//这里使用 @Url 注解，可以不设置base
                        .setApiClass(ApiServices.class)
                        //.setIsPrintLog(true)
                        .build();

        ApiServices apiServices = retrofitManager.getInstance();

        //这需要读写权限
        Observable<ResponseBody> call = apiServices.downLoadApkFile(DownLoadUtils.getUrlRange(apkUrl,fileDir,fileName),apkUrl);
        DownLoadUtils.downLoadWithRange(apkUrl,call, fileDir, fileName, new DownLoadImpl() {
            @Override
            public void onProgressCallBack(final int progress) {
                /**
                 * 进度回调在子线程
                 */
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTvShowResult.setText("下载进度：" + progress);
                    }
                });
            }

            @Override
            public void onDownLoadFinish(File file) {
                mTvShowResult.setText("下载完成,文件路径为：：" + file.getAbsolutePath());
                System.out.println("下载成功");
                // 跳转到系统安装页面
                InstallApkUtils.installApk(MainActivity.this, file);
            }

            @Override
            public void onDownLoadFailed(String errorMsg) {
                mTvShowResult.setText("下载失败：" + errorMsg);
            }
        });
    }

    private boolean writeFileToSDCard(ResponseBody body) {
        try {
            // todo change the file location/name according to your needs
            File futureStudioIconFile =
                    new File(getExternalFilesDir(null) + File.separator + "Future Studio Icon.png");
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }
                outputStream.flush();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    private RequestBody toRequestBody(String value) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), value);
        return requestBody;
    }

    /**
     * okhttp 可以进行的设置，retrofit也可以
     */
    private void addInterceptor() {
        //        //1,生成okhttp3对象
        //        OkHttpClient client = new OkHttpClient.Builder()
        //                .readTimeout()
        //                .retryOnConnectionFailure()
        //                .connectTimeout()
        //                .addInterceptor()//在这里添加自己的拦截器
        //                .build();
        //
        //
        //        Retrofit retrofit = new Retrofit.Builder()
        //                .baseUrl("")
        //                .client(client)//在retrofit中设置刚创建的 okhttp3 的对象
        //                .addConverterFactory(GsonConverterFactory.create())
        //                .build();
        //
        //        ApiServices apiServices = retrofit.create(ApiServices.class);
        //
        //        //这里将对象传入进去
        //        Call<PostJsonBean> call = apiServices.postJsonBean(new PostJsonBean());
        //
        //        call.enqueue(new Callback<PostJsonBean>() {
        //            @Override
        //            public void onResponse(Call<PostJsonBean> call, Response<PostJsonBean> response) {
        //                //主线程
        //                Log.d(TAG, "onResponse: ");
        //                PostJsonBean successData = null;
        //                try {
        //                    successData = response.body();
        //                } catch (Exception e) {
        //                    e.printStackTrace();
        //                }
        //                mTvShowResult.setText(successData.toString());
        //            }
        //
        //            @Override
        //            public void onFailure(Call<PostJsonBean> call, Throwable t) {
        //                //主线程
        //                Log.d(TAG, "onFailure: " + t.getMessage().toString());
        //                mTvShowResult.setText(t.getMessage());
        //            }
        //        });
    }

    @OnClick({
            R.id.btn_no_params_get, R.id.btn_no_params_path_get, R.id.btn_params_get, R.id.btn_url,
            R.id.btn_normal_post, R.id.btn_params_post, R.id.btn_json_post, R.id.btn_upload_post,
            R.id.btn_download_post, R.id.btn_json_post_requestbody, R.id.btn_download_apk
    })
    public void onClick(View view) {
        Toast.makeText(getApplicationContext(), "所有都已经过测试，如果调不调可以直接替换成自己的接口进行测试", Toast.LENGTH_LONG)
                .show();
        switch (view.getId()) {
            case R.id.btn_no_params_get:
                noParamsGet();
                break;
            case R.id.btn_no_params_path_get:
                noParamsPathGet();
                break;
            case R.id.btn_params_get:
                paramsGet();
                break;
            case R.id.btn_url:
                directUrl();
                break;
            case R.id.btn_normal_post:
                postWithOutParams();
                break;
            case R.id.btn_params_post:
                postParamsFootBallData();
                break;
            case R.id.btn_json_post:
                postJsonBean();
                break;
            case R.id.btn_json_post_requestbody:
                postJsonByRequestBody();
                break;
            case R.id.btn_upload_post:
                //需要检测权限
                RxPermissionUtils.requestPermission(this,
                        new RxPermissionUtils.OnRxPermissionCallBack() {
                            @Override
                            public void onGrant() {
                                //同意权限，打开图片选择
                                PictureUtils.chooseImage(MainActivity.this, true);
                            }

                            @Override
                            public void onRefuse(boolean isNeverAskAgain) {

                            }
                        }, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA);

                break;
            case R.id.btn_download_post:
                //需要检测权限
                RxPermissionUtils.requestPermission(this,
                        new RxPermissionUtils.OnRxPermissionCallBack() {
                            @Override
                            public void onGrant() {
                                downLoadFile();
                            }

                            @Override
                            public void onRefuse(boolean isNeverAskAgain) {

                            }
                        }, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
                break;

            case R.id.btn_download_apk:
                //需要检测权限
                RxPermissionUtils.requestPermission(this,
                        new RxPermissionUtils.OnRxPermissionCallBack() {
                            @Override
                            public void onGrant() {
                                downLoadApkFile();
                            }

                            @Override
                            public void onRefuse(boolean isNeverAskAgain) {

                            }
                        }, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PictureUtils.ImageRequestCode://图片选择回调
                List<LocalMedia> selectListPic = PictureSelector.obtainMultipleResult(data);
                if (selectListPic == null || selectListPic.size() == 0) {
                    showToast("图片选择失败");
                    return;
                }
                LocalMedia localMedia = selectListPic.get(0);
                // 1.media.getPath(); 为原图path
                // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true  注意：音视频除外
                // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true  注意：音视频除外
                // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的

                imageLocalPath = selectListPic.get(0).getCompressPath();
                NetLogUtil.d(TAG + "  选择图片本地地址为：" + imageLocalPath);
                postFile(imageLocalPath);
                break;
            case RxPermissionUtils.SetInstallRequestCode:
                boolean isAgreeInstallApk=RxPermissionUtils.isAgreeInstallPackage(MainActivity.this);
                Log.d(TAG , "  onActivityResult 是否允许安装apk isAgreeInstallApk = "+isAgreeInstallApk);
                if (!isAgreeInstallApk) {
                    showToast("请开启改权限，安装最新的应用！");
                } else {
                    downLoadApkFile();
                }
                break;
        }
    }

    public void showToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }


}
