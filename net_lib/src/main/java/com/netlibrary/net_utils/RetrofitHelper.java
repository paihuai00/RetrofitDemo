package com.netlibrary.net_utils;

import com.netlibrary.RetrofitController;
import com.netlibrary.impls.UploadImpl;
import com.netlibrary.interceptors.ProgressRequestBody;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.http.Part;

/**
 * Date: 2019/12/19
 * create by cuishuxiang
 * description: Retrofit 帮助类
 */
public class RetrofitHelper {
    private static final String TAG = "RetrofitHelper";

    /**
     * 获取 ， 表单上传文件
     * @return 需要使用 {@link Part}
     *
     * key ： 接口key字段
     * value：需要上传的文件
     */
    public static List<MultipartBody.Part> getMultipartBodyPartList(Map<String,File> fileMap) {
        List<MultipartBody.Part> parts = new ArrayList<>();

        try {
            for (Map.Entry<String, File> entry : fileMap.entrySet()) {
                File f = entry.getValue();
                RequestBody requestBody = RequestBody.create(RetrofitController.FORM_TYPE, f);
                MultipartBody.Part part = MultipartBody.Part.createFormData(entry.getKey(), f.getName(), requestBody);
                parts.add(part);
            }
        } catch (Exception e) {
            NetLogUtil.d(TAG + " upLoadFile " + e.getMessage());
        }

        return parts;
    }
    /**
     * 获取 ， 表单上传文件， 带进度回调
     * @return 需要使用 {@link Part}
     *
     * key ： 接口key字段
     * value：需要上传的文件
     */
    public static  List<MultipartBody.Part> getMultipartBodyPartList(Map<String,File> fileMap, UploadImpl uploadImpl) {
        List<MultipartBody.Part> parts = new ArrayList<>();

        try {
            for (Map.Entry<String, File> entry : fileMap.entrySet()) {
                File f = entry.getValue();
                RequestBody requestBody = RequestBody.create(RetrofitController.FORM_TYPE, f);

                //这里需要将原始的RequestBody进行包装
                ProgressRequestBody progressRequestBody = new ProgressRequestBody(requestBody, uploadImpl);

                MultipartBody.Part part = MultipartBody.Part.createFormData(entry.getKey(), f.getName(), progressRequestBody);
                parts.add(part);
            }
        } catch (Exception e) {
            NetLogUtil.d(TAG + " upLoadFile " + e.getMessage());
        }

        return parts;
    }

    /**
     * 上传json需要的RequestBody
     * @param paramsMap
     * @return
     */
    public static RequestBody getRequestBody(Map<String, Object> paramsMap) {
        JSONObject jsonObject = new JSONObject();
        try {
            //将map中参数转化成 json
            for (Map.Entry<String, Object> entry : paramsMap.entrySet()) {
                jsonObject.put(entry.getKey(), entry.getValue());
            }

        } catch (JSONException e) {
            e.printStackTrace();
            NetLogUtil.d(TAG + " getRequestBody " + e.getMessage());
        }

        return RequestBody.create(MediaType.parse("application/json;charset=UTF-8"), jsonObject.toString());
    }


    //获取上传文件需要的 MultiPartBody.Part
    public static MultipartBody.Part getMultipartBodyPart(Map<String, Object> paramsMap) {
        MultipartBody.Part part = null;

        try {
            for (Map.Entry<String, Object> entry : paramsMap.entrySet()) {
                if (entry.getValue() instanceof File) {
                    File file = (File) entry.getValue();

                    RequestBody fileBody = RequestBody.create(MediaType.parse("application/otcet-stream"), file);
                    part = MultipartBody.Part.createFormData(entry.getKey(), file.getName(), fileBody);
                }
            }
        } catch (Exception e) {
            NetLogUtil.d(TAG + " getMultipartBody " + e.getMessage());
        }

        return part;
    }

}
