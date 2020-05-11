package com.netlibrary.beans;

/**
 * Date: 2020/5/9 create by cuishuxiang description:
 *
 * http返回结果基类
 */
public class BaseHttpResult<T> {

  T data;
  int code;
  String message;


  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
