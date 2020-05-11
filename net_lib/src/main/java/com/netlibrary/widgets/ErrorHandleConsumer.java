package com.netlibrary.widgets;

import com.google.gson.JsonParseException;
import io.reactivex.functions.Consumer;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import org.json.JSONException;

/**
 * Date: 2020/5/9
 * create by cuishuxiang
 *
 * description: 判断异常类型 处理类
 */
public abstract class ErrorHandleConsumer implements Consumer<java.lang.Throwable> {


  @Override
  public void accept(java.lang.Throwable e) throws Exception {
    NetErrorException error = null;
    if (e != null) {
      // 对不是自定义抛出的错误进行解析
      if (!(e instanceof NetErrorException)) {
        if (e instanceof UnknownHostException) {
          error = new NetErrorException(e, NetErrorException.NoConnectError);
        } else if (e instanceof JSONException || e instanceof JsonParseException) {
          error = new NetErrorException(e, NetErrorException.PARSE_ERROR);
        } else if (e instanceof SocketTimeoutException) {
          error = new NetErrorException(e, NetErrorException.SocketTimeoutError);
        } else if (e instanceof ConnectException) {
          error = new NetErrorException(e, NetErrorException.ConnectExceptionError);
        } else {
          error = new NetErrorException(e, NetErrorException.OTHER);
        }
      } else {
        error = new NetErrorException(e, NetErrorException.OTHER);
      }
    }

    onFail(error);
  }

  protected abstract void onFail(NetErrorException error);

}
