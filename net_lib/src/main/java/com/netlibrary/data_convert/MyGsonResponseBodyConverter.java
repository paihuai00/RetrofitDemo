package com.netlibrary.data_convert;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import java.io.IOException;
import okhttp3.ResponseBody;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Converter;

/**
 * Date: 2020/5/9 create by cuishuxiang description:
 */
public class MyGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {
  private final Gson gson;
  private final TypeAdapter<T> adapter;

  MyGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
    this.gson = gson;
    this.adapter = adapter;
  }

  @Override public T convert(ResponseBody value) throws IOException {
    // 这里就是对返回结果进行处理
//    String jsonString = value.string();
//    try {
//      // ------------------ JsonObject 只做了初略的判断，具体情况自定
//      JSONObject object = new JSONObject(jsonString);
//      int code = object.getInt("code");
//      if (code != 200) {
//        throw new NetErrorException(object.getString("message"), code);
//      }
//      return adapter.fromJson(object.getString("data"));
//
//    } catch (JSONException e) {
//      e.printStackTrace();
//      throw new NetErrorException("数据解析异常", NetErrorException.PARSE_ERROR);
//    } finally {
//      value.close();
//    }

    JsonReader jsonReader = gson.newJsonReader(value.charStream());
    try {
      return adapter.read(jsonReader);
    } finally {
      value.close();
    }
  }
}
