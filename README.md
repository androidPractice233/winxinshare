# 安卓端开发说明

## 网络请求

* 网络请求分为三层，Activity层，NetworkManager层，retrofit Service接口层

​           Activity中：

    ```java
public class MainActivity extends AppCompatActivity {
Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button= (Button) findViewById(R.id.testButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NetworkManager.getInstance().test(new Callback<ResultBean>() {
                    @Override
                    public void onResponse(Call<ResultBean> call, Response<ResultBean> response{
                        ResultBean resultBean=  response.body();
                        Toast.makeText(MainActivity.this,			(String)resultBean.getData(),Toast.LENGTH_SHORT).show();
                   }

                    @Override
                    public void onFailure(Call<ResultBean> call, Throwable t) {
                        Log.e("MainActivity", t.getMessage()  );
                    }
                });
            }
        });
    }
}
    ```

其中callback为回调方法，onResponse为网络连接成功时的逻辑，onFailure为网络失败（连接超时等）的逻辑

**注意：此时规定服务器返回类型统一为ResultBean，具体的返回存放在ResultBean的data成员里**



   NetworkManager 中

```java
 public  void test(Callback<ResultBean> callback){
        TestService testService= retrofit.create(TestService.class);
        Call<ResultBean> call=testService.test("我不想写代码！");
        call.enqueue(callback);
    }
```

其中`call.enqueue(callback);`为异步请求，如果需要同步请求使用`call.execute()`

**注意：一般不使用`call.execute()`，因为安卓规定在主线程中不能使用同步请求，否则导致主线程阻塞**



Retrofit service请求接口

```java
public interface TestService {
    @POST("test")
    Call<ResultBean> test(@Body String test);
}
```

注意两个标签@Body和@POST,@post括号内为请求的url,即网址除去ip地址和端口号后面的部分。

@Body 为请求参数，可以为任何一个自定义对象，不仅仅可以为String。建议传入一个HashMap对象，HashMap对象内保存请求参数。

图片文件上传功能暂时不支持，即**multipart/form-data**类型的表单提交存在问题，后续将会解决。



