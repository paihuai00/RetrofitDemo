package com.netlibrary.net_utils;

import com.google.gson.Gson;
import com.netlibrary.impls.UploadImpl;
import com.netlibrary.interceptors.ProgressRequestBody;
import com.netlibrary.widgets.ErrorHandleConsumer;
import com.netlibrary.widgets.NetErrorException;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.ResponseBody;

/**
 * Date: 2020/5/9 create by cuishuxiang description:
 *
 * 1,使用 {@link ErrorHandleConsumer} 对异常进行处理
 *
 *    封装数据回调{@link ResponseCallBack}
 *
 * 2，外部调用的时候，直接调用{@link RequestUtils#requestCall(Observable, Class, ResponseCallBack)}
 *
 * 3,
 *  - 使用{@link Observable#flatMap}操作符
 *    嵌套请求->一个接口的请求依赖另一个API请求返回的数据
 *
 *  - 合并请求：
 *    使用{@link Observable#zip(Iterable, Function)}操作符
 *    {@link Observable#merge(Iterable)}
 *
 *  - 轮询请求：
 *    使用{@link Observable#interval(long, TimeUnit)}
 *    可以使用{@link Observable#takeUntil(ObservableSource)}}, 当达到 xx条件结束轮询
 *
 *
 *
 *
 *
 */
public class RequestUtils {

  public static Gson gson = new Gson();

  /**
   *  发起网络请求
   * @param call
   * @param clazz  自己需要的实体bean类型
   * @param callBack 返回结果回调
   * @param <T>    所需要的class 类型

   * @return   Disposable 用于外部管理生命周期，防止内存泄漏
   */
  public static <T> Disposable requestCall(
      Observable<ResponseBody> call,
      final Class<T> clazz,
      final ResponseCallBack<T> callBack) {

    Disposable disposable=call.map(new Function<ResponseBody, T>() {
      @Override
      public T apply(ResponseBody responseBody) throws Exception {
        // 转换出想要的bean
        String responseJsonString = responseBody.string();

        T tClass = gson.fromJson(responseJsonString, clazz);

        return tClass;
      }
    }).compose(SchedulersHelper.<T>changeSchedulerObservable())
        .subscribe(new Consumer<T>() {
          @Override
          public void accept(T tClass) throws Exception {
            if (callBack != null) {
              callBack.onHttpCallSuccess(tClass);
            }

          }
        }, new ErrorHandleConsumer() {
          @Override
          protected void onFail(NetErrorException error) {
            if (callBack != null) {
              callBack.onHttpCallFailed(error);
            }
          }
        });

    return disposable;
  }

  /**
   * 默认请求方法，得到原始json
   * @param call
   * @param callBack
   * @param <T>
   * @return
   */
  public static Disposable requestCallOriginal(
      Observable<ResponseBody> call,
      final ResponseCallBack<String> callBack) {

    Disposable disposable=call.map(new Function<ResponseBody, String>() {
      @Override
      public String apply(ResponseBody responseBody) throws Exception {
        // 转换出想要的bean
        String responseJsonString = responseBody.string();

//        T tClass = gson.fromJson(responseJsonString, clazz);

        return responseJsonString;
      }
    }).compose(SchedulersHelper.<String>changeSchedulerObservable())
        .subscribe(new Consumer<String>() {
          @Override
          public void accept(String tClass) throws Exception {
            if (callBack != null) {
              callBack.onHttpCallSuccess(tClass);
            }

          }
        }, new ErrorHandleConsumer() {
          @Override
          protected void onFail(NetErrorException error) {
            if (callBack != null) {
              callBack.onHttpCallFailed(error);
            }
          }
        });

    return disposable;
  }


  /**
   * 默认请求方法，得到原始json
   * @param call
   * @param callBack
   * @param <T>
   * @return
   */
//  public static Disposable requestCallUploadFile(
//      Observable<ProgressRequestBody> call,
//      final ResponseCallBack<String> callBack) {
//
//    Disposable disposable=call.map(new Function<ProgressRequestBody, String>() {
//      @Override
//      public String apply(ProgressRequestBody responseBody) throws Exception {
//        // 转换出想要的bean
//        String responseJsonString = responseBody.string();
//
////        T tClass = gson.fromJson(responseJsonString, clazz);
//
//        return responseJsonString;
//      }
//    }).compose(SchedulersHelper.<String>changeSchedulerObservable())
//        .subscribe(new Consumer<String>() {
//          @Override
//          public void accept(String tClass) throws Exception {
//            if (callBack != null) {
//              callBack.onHttpCallSuccess(tClass);
//            }
//
//          }
//        }, new ErrorHandleConsumer() {
//          @Override
//          protected void onFail(NetErrorException error) {
//            if (callBack != null) {
//              callBack.onHttpCallFailed(error);
//            }
//          }
//        });
//
//    return disposable;
//  }

  /**
   * 数据回调
   * @param <T>
   */
  public interface ResponseCallBack<T>{
    void onHttpCallSuccess(T clazz);

    void onHttpCallFailed(Throwable throwable);
  }

}
