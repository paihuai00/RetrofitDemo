### RxJava + Retrofit2 网络封装

[官网](https://square.github.io/retrofit/)

项目使用依赖为：

    // Okhttp库
      implementation 'com.squareup.okhttp3:okhttp:3.11.0'
      
      // Retrofit库
      implementation 'com.squareup.retrofit2:retrofit:2.1.0'
      
      implementation 'com.squareup.retrofit2:adapter-rxjava2:2.2.0'
      
      implementation 'com.squareup.retrofit2:converter-gson:2.3.0'//retrofit Gson 转换器
      
      implementation 'com.squareup.retrofit2:converter-scalars:2.3.0'//Retrofit String 转换器
    
      //RxJava
      
      implementation 'io.reactivex.rxjava2:rxjava:2.0.1'
      
      implementation 'io.reactivex.rxjava2:rxandroid:2.0.1'
      
      implementation 'com.orhanobut:logger:2.2.0'//日志打印
      
      //Okhttp 日志打印拦截器：https://github.com/ihsanbal/LoggingInterceptor
      
      implementation('com.github.ihsanbal:LoggingInterceptor:3.0.0') {
        exclude group: 'org.json', module: 'json'
      }
      
如果与自己项目冲突，可以 **exclude** 掉对应依赖

## 一、注解说明

**1，常用请求方法注解**

注解|说明
----|----
@GET	|get请求
@POST	|post请求

**2，参数注解**

参数注解|说明
----|----
@Url	|指定请求路径
@Path		|url路径中的占位符
@Query	|Get请求中指定参数
@QueryMap	|Get请求中指定参数，可以放置多个
@Body	|post请求发送非表单数据, 常用于：post方式传递json格式数据
@Filed |post请求中表单字段,Filed和FieldMap需要FormUrlEncoded结合使用
@FiledMap |post请求中表单字段,@Filed作用一致，用于不确定表单参数
@Part |post请求中表单字段,Part和PartMap与Multipart注解结合使用, 常用于文件上传的情况
@PartMap |post请求中表单字段,@Part作用一致，默认接受的类型是 **Map**，可用于实现**多文件上传**


**3，head注解**

请求头注解|说明
----|----
@Headers	|用于添加固定请求头，可以同时添加多个。通过该注解添加的请求头不会相互覆盖，而是**共同存在**
@Header		|作为方法的参数传入，用于添加不固定值的Header，该注解会更新已有的请求头


**4，请求格式注解**

注解|说明
----|----
@FormUrlEncoded	| 请求发送的是**表单**数据，每个键值对需要使用@Field注解
@Multipart		|请求发送multipart数据，需要配合使用@Part，常用于上传文件
@Streaming		|表示响应用字节流的形式返回,如果没使用该注解，默认会把数据全部载入到内存中，该注解在在下载大文件的特别有用

### 请求头
 - **@Headers**： 用于添加固定请求头，可以同时添加多个。通过该注解添加的请求头不会相互覆盖，而是共同存在
- **@Header**：作为方法的参数传入，用于添加不固定值的Header，该注解会更新已有的请求头



### Get请求 **@GET**


**1，无参Get**

伪代码：
```
  @GET("你的url")
  Call<ResponseBody> getWithOutParams();
```

2，有参Get，分2种情况

- 直接替换接口中的参数
，需要`@Path` 注解，用于动态替换 url ，如下所示，page将直接替换url中的`{pageIndex}`

伪代码：
```
  @GET("url{pageIndex}")
  Call<ResponseBody> getWithParams(@Path("pageIndex") int page);
```
- 拼接到接口后面参数，需要使用 `@Query` 或 `@QueryMap` 

伪代码：
```
  @GET("urlxxxxxxx}")
  Call<ResponseBody> getWithParams(@Query("page") int page);
```
上面伪代码的完整请求为：`https://urlxxxxxxx?page=xxxx` ， `@QueryMap` 就是使用map将参数组装

3，文件下载@Get

注意：文件下载一般会保存在本地，所以返回值使用`ResponseBody`来进行统一处理

伪代码：
在下载大文件的时候，注意添加`@Streaming`

```
    /**
     * 文件下载
     *
     * 大文件的时候，要加：@Streaming 不然会报OOM
     */
    @Streaming
    @GET("2012031220134655.jpg")
    Observable<ResponseBody> downLoadFile();
```


### Post请求 **@Post**
1，post上传**json字符串**，需要使用 `@Body` 有两种方式

- 使用bean对象

伪代码如下：
```
@POST("xxxxx")
Call<ResponseBody> login(@Body User user);//会将user对象，转成json上传到服务器
```


- 直接上传json ， 需要在请求head中声明上传类型使用到 `@Headers`，还需要构建一个`RequestBody`

伪代码如下：
```
@Headers({"Content-Type:application/json", "Accept:application/json"})
@POST("xxxxx")
Call<ResponseBody> login(@Body RequestBody body);//在headers中声明上传的类型
```

构建 `RequestBody` 伪代码如下：
```
JSONObject requestObject = new JSONObject();
try {
     //在这里放传递的json数据key - value 形式
     requestObject .put("xxxx", "xxx");
     requestObject .put("xxxx", "xx");
    } catch (Exception e) {
     e.printStackTrace();
 }
RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), requestObject.toString());
```

2，Post 表单形式上传键值对需要使用 `@Field` 、`@FieldMap` 、`@FormUrlEncoded`

伪代码如下：
```
@FormUrlEncoded
@POST("xxxxx")
//这里的 @Field 可以使用  @FieldMap 替换
Call<ResponseBody> login(@Field("xxx") String xxx, @Field("xx") String xx);
```

3，Post 上传文件，需要使用到  `@Part`、`@PartMap`、`@Multipart`

- 单文件上传

伪代码如下：
```
@Multipart
@POST("uploadFile")
Call<ResponseBody> upload(@Part MultipartBody.Part file);
//如果需要携带别的参数，可以这样调用
Call<ResponseBody> upload(@Part MultipartBody.Part file,@Part(“params”) RequestBody requestBody);
```
这里需要注意， `MultipartBody.Part` 构建需要使用`RequestBody`，类型必须为 **multipart/form-data**，代码如下：
```
//注意必须为：MediaType.parse("multipart/form-data")
RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
MultipartBody.Part part = MultipartBody.Part.createFormData("file", f.getName(), requestBody);   
```

- 多文件上传, 跟单文件上传类似，不过参数变成了 `List<MultipartBody.Part>`


```
@Multipart
@POST("xxxx")
Call<ResponseBody> upLoadFiles(@Part List<MultipartBody.Part> parts);
```


## 二、使用说明

#### 1、初始化
建议在 `Application`中，进行Retrofit的初始化，得到唯一全局的 `ApiServices`。

常规的值(超时、log打印等)都有默认配置，可以在 **NetBuilder** 中进行自己的配置，注意必须设置自己的Retrofit interface类，代码如下：
```
RetrofitManager<ApiServices> retrofitManager =
                new RetrofitManager.NetBuilder(this)
                        .setBaseUrl(baseUrl)//设置BaseUrl
                        .setApiClass(ApiServices.class)//设置自己的类型，必须设置！！！
                        //.setIsPrintLog(true)
                        .build();

ApiServices apiServices = retrofitManager.getInstance();
```

#### 2、使用

2.1，常用类

    Retrofit管理类： RetrofitManager
    
    配置：RetrofitController
    
    帮助类(用于生成不同请求的body): RetrofitHelper
    
    下载：DownLoadUtils

2.2，具体各个get、post、上传、下载方法的调用，见demo，已经添加相关注释。

注：项目demo中的例子都已经测试通过，如果调用不通请换成自己的接口(接口网上找的说不定啥时候就GG了)
