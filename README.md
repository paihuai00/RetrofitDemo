### RxJava + Retrofit2 网络封装

[官网](https://square.github.io/retrofit/)

#### 一、注解说明

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



#### 一、Get请求 **@GET**

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




##### 二、Post请求 **@Post**
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

3.1，Post 上传文件，需要使用到  `@Part`、`@PartMap`、`@Multipart`

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
3.2，上面Post上传文件一直使用的是`@Part`和`@Multipart`注解，可以使用 `@Body`和`MultipartBody`替代

伪代码如下：
```
@POST("Upload")
Call<ResponseBody> upload(@Body MultipartBody multipartBody);
```

所以，有2种方式上传文件
- `@Multipart`注解方法，并用`@Part`注解方法参数，类型是`MultipartBody.Part`
- 用`@Body`注解方法参数，类型是`MultipartBody`


##### 三、请求头
 - **@Headers**： 用于添加固定请求头，可以同时添加多个。通过该注解添加的请求头不会相互覆盖，而是共同存在
- **@Header**：作为方法的参数传入，用于添加不固定值的Header，该注解会更新已有的请求头
