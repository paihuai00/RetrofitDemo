package com.csx.retrofitdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @BindView(R.id.btn_no_params_get)
    Button mBtnNoParamsGet;
    @BindView(R.id.btn_params_get)
    Button mBtnParamsGet;
    @BindView(R.id.btn_normal_post)
    Button mBtnNormalPost;
    @BindView(R.id.btn_no_params_path_get)
    Button mBtnNoParamsPathGet;
    @BindView(R.id.btn_url)
    Button mBtnUrl;
    @BindView(R.id.tv_show_result)
    TextView mTvShowResult;
    @BindView(R.id.btn_params_post)
    Button mBtnParamsPost;
    @BindView(R.id.btn_json_post)
    Button mBtnJsonPost;
    @BindView(R.id.btn_upload_post)
    Button mBtnUploadPost;
    @BindView(R.id.btn_download_post)
    Button mBtnDownloadPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

    }

    //1无参
    private void noParamsGet() {
        //http://gank.io/api/data/%E7%A6%8F%E5%88%A9/6/1
        String baseUrl = "http://gank.io/api/data/%E7%A6%8F%E5%88%A9/";//注意，这里的baseUrl：需要以“/”结尾
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiServices serivces = retrofit.create(ApiServices.class);

        Call<ResponseBody> call = serivces.getGirlData();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String successData = response.body().string();
                    mTvShowResult.setText(successData);
                    Log.d(TAG, "onResponse: successData = " + successData);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage().toString());
                mTvShowResult.setText(t.getMessage());
            }
        });


    }

    //2无参，url变化
    private void noParamsPathGet() {
        //http://gank.io/api/data/%E7%A6%8F%E5%88%A9/6/1
        String baseUrl = "http://gank.io/api/data/%E7%A6%8F%E5%88%A9/";//注意，这里的baseUrl：需要以“/”结尾
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiServices serivces = retrofit.create(ApiServices.class);

        Call<ResponseBody> call = serivces.getGirlDataByDate("12", "1");

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String successData = response.body().string();
                    mTvShowResult.setText(successData);
                    Log.d(TAG, "onResponse: successData = " + successData);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage().toString());
                mTvShowResult.setText(t.getMessage());
            }
        });
    }

    //3,有参get请求
    private void paramsGet() {
        //https://free-api.heweather.com/v5/now?city=北京&key=defbffa06a1846fe8bab0b271a9eca6e
        String BaseUrl = "https://free-api.heweather.com/";

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiServices apiServices = retrofit.create(ApiServices.class);

        Call<WeatherBean> call = apiServices.getWeatherData("北京", "defbffa06a1846fe8bab0b271a9eca6e");

        call.enqueue(new Callback<WeatherBean>() {
            @Override
            public void onResponse(Call<WeatherBean> call, Response<WeatherBean> response) {
                //主线程
                Log.d(TAG, "onResponse: ");
            }

            @Override
            public void onFailure(Call<WeatherBean> call, Throwable t) {
                //主线程
                Log.d(TAG, "onFailure: " + t.getMessage().toString());
            }
        });
    }

    //4,直接传入Url
    private void directUrl() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://blog.csdn.net/")//当使用@url注解的时候，baseUrl会被替换
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiServices apiServices = retrofit.create(ApiServices.class);

        Call<ResponseBody> call = apiServices.getDataByUrl("http://gank.io/api/data/%E7%A6%8F%E5%88%A9/6/1");

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //主线程
                Log.d(TAG, "onResponse: ");
                String successData = null;
                try {
                    successData = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mTvShowResult.setText(successData);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //主线程
                Log.d(TAG, "onFailure: " + t.getMessage().toString());
                mTvShowResult.setText(t.getMessage());
            }
        });
    }

    //post 不带参数
    private void postGirl() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://gank.io/api/data/%E7%A6%8F%E5%88%A9/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiServices apiServices = retrofit.create(ApiServices.class);

        Call<ResponseBody> call = apiServices.postGirlData();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //主线程
                Log.d(TAG, "onResponse: ");
                String successData = null;
                try {
                    successData = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mTvShowResult.setText(successData);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //主线程
                Log.d(TAG, "onFailure: " + t.getMessage().toString());
                mTvShowResult.setText(t.getMessage());
            }
        });
    }

    //post 带参数
    private void postParamsFootBallData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://op.juhe.cn/onebox/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiServices apiServices = retrofit.create(ApiServices.class);

        Call<ResponseBody> call = apiServices.postFootBallData("02da4926376156643455fafa48982456",
                "中超");

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //主线程
                Log.d(TAG, "onResponse: ");
                String successData = null;
                try {
                    successData = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mTvShowResult.setText(successData);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //主线程
                Log.d(TAG, "onFailure: " + t.getMessage().toString());
                mTvShowResult.setText(t.getMessage());
            }
        });

    }


    //post Json
    private void postJson() {
        Toast.makeText(getApplicationContext(), "post Json，接口为假具体使用请查看注释！", Toast.LENGTH_SHORT).show();

        if (1 == 2) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ApiServices apiServices = retrofit.create(ApiServices.class);

            //这里将对象传入进去
            Call<PostJsonBean> call = apiServices.postJsonData(new PostJsonBean());

            call.enqueue(new Callback<PostJsonBean>() {
                @Override
                public void onResponse(Call<PostJsonBean> call, Response<PostJsonBean> response) {
                    //主线程
                    Log.d(TAG, "onResponse: ");
                    PostJsonBean successData = null;
                    try {
                        successData = response.body();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    mTvShowResult.setText(successData.toString());
                }

                @Override
                public void onFailure(Call<PostJsonBean> call, Throwable t) {
                    //主线程
                    Log.d(TAG, "onFailure: " + t.getMessage().toString());
                    mTvShowResult.setText(t.getMessage());
                }
            });

        }


    }

    //上传文件
    private void postFile() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://imgup.b2b.hc360.com/imgup/turbine/action/imgup.PicManagementAction/eventsubmit_doPerform/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiServices apiServices = retrofit.create(ApiServices.class);

        //这里将对象传入进去(需要读写权限)
        File file = new File("/storage/emulated/0/Tencent/WeixinWork/doc/offlineRes/quill.offline/rescdn.qqmail.com/node/webdoc/images/CircleLogoBlue48.4d4d70c899.png");

        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestBody);

        //参数
        Map<String, RequestBody> parasmBody = new HashMap<>();
        parasmBody.put("subjectID", toRequestBody("123"));
        parasmBody.put("operType", toRequestBody("upload"));


        Call<ResponseBody> call = apiServices.postFile(parasmBody, part);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //主线程
                Log.d(TAG, "onResponse: ");
                String successData = null;
                try {
                    successData = response.body().string();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mTvShowResult.setText(successData);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //主线程
                Log.d(TAG, "onFailure: " + t.getMessage().toString());
                mTvShowResult.setText(t.getMessage());
            }
        });
    }

    //文件下载
    private void downLoadFile() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.pptbz.com/pptpic/UploadFiles_6909/201203/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiServices apiServices = retrofit.create(ApiServices.class);

        //这需要读写权限
        Call<ResponseBody> call = apiServices.downLoadFile();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                //主线程
                if (response.isSuccessful()) {
                    writeFileToSDCard(response.body());
                    Toast.makeText(getApplicationContext(), "下载成功！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "下载失败！", Toast.LENGTH_SHORT).show();
                }
//                mTvShowResult.setText(successData);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //主线程
                Log.d(TAG, "onFailure: " + t.getMessage().toString());
                mTvShowResult.setText(t.getMessage());
            }
        });
    }

    private boolean writeFileToSDCard(ResponseBody body) {
        try {
            // todo change the file location/name according to your needs
            File futureStudioIconFile = new File(getExternalFilesDir(null) + File.separator + "Future Studio Icon.png");
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
//        Call<PostJsonBean> call = apiServices.postJsonData(new PostJsonBean());
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

    @OnClick({R.id.btn_no_params_get, R.id.btn_no_params_path_get, R.id.btn_params_get, R.id.btn_url, R.id.btn_normal_post, R.id.btn_params_post, R.id.btn_json_post, R.id.btn_upload_post, R.id.btn_download_post})
    public void onClick(View view) {
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
                postGirl();
                break;
            case R.id.btn_params_post:
                postParamsFootBallData();
                break;
            case R.id.btn_json_post:
                postJson();
                break;
            case R.id.btn_upload_post:
                postFile();
                break;
            case R.id.btn_download_post:
                downLoadFile();
                break;
        }
    }

}
