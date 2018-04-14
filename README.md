# 安卓端开发说明

## 网络请求

* 网络请求分为三层，Activity层，NetworkManager层，retrofit Service接口层

     ### Activity中：

     ​

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



### NetworkManager 中

```java
 public  void test(Callback<ResultBean> callback){
        TestService testService= retrofit.create(TestService.class);
        Call<ResultBean> call=testService.test("我不想写代码！");
        call.enqueue(callback);
    }
```

其中`call.enqueue(callback);`为异步请求，如果需要同步请求使用`call.execute()`

**注意：一般不使用`call.execute()`，因为安卓规定在主线程中不能使用同步请求，否则导致主线程阻塞**





###  Retrofit service请求接口

```java
public interface TestService {
    @POST("test")
    Call<ResultBean> test(@Body String test);
}
```

注意两个标签@Body和@POST,@post括号内为请求的url,即网址除去ip地址和端口号后面的部分。

@Body 为请求参数，可以为任何一个自定义对象，不仅仅可以为String。建议传入一个HashMap对象，HashMap对象内保存请求参数。

### 图片及文件上传

```java
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by skyluo on 2018/4/14.
 * ！！！！
 * ！！！！
 * 注意：所有multipart/form-data类型的请求请写在这里！！！！
 * ！！！！
 * ！！！！
 *
 */

public interface MultipartService {
    @Multipart
    @POST("MultipartTest")
    Call<ResultBean> test(@Part("test") String test1, @Part List<MultipartBody.Part> file);
}
```

**所有文件上传操作规定写在此接口之下 **

统一按照上面写法

* 如果是文件的话` @Part List<MultipartBody.Part> file)`,注意此时不能@Part（"xxx"），文件名需要在构建`MultipartBody.Part`时指定而不能通过@Part括号内值指定。

* 如果是普通变量的话使用`@Part("test") String test1`,@Part括号内的为参数名称。

  ​

NetworkManager中

```java
 public  void MutiprtTest(Callback<ResultBean> callback, List<File> fileList) throws IOException {
        MultipartService multipartService=multipartRetrofit.create(MultipartService.class);
        Call<ResultBean> call=multipartService.test("卧槽", NetworkUtils.filesToMultipartBodyParts(fileList,"fileList"));
        call.enqueue(callback);
    }
```



```java
public class NetworkUtils {
    /**
     * 将文件加入multipartBody
     * @param files
     * @return
     */
    public static List<MultipartBody.Part> filesToMultipartBodyParts(List<File> files,String fileListName) {
        List<MultipartBody.Part> parts = new ArrayList<>(files.size());
        for (File file : files) {
            // TODO: 16-4-2  这里为了简单起见，没有判断file的类型
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/png"), file);
            MultipartBody.Part part = MultipartBody.Part.createFormData(fileListName, file.getName(), requestBody);
            parts.add(part);
        }
        return parts;
    }
}
```

如果是多个文件上传可以直接用上面的写法，上面的fileListName是你们之前写在接口文档的参数名称。

如果是单个文件的话，可以仿造`NetworkUtils. filesToMultipartBodyParts()`，很容易的。

## 图片相册api

```java
   btnPopPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PictureSelector.create(MainActivity.this)
                        .openGallery(PictureMimeType.ofImage())//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio(
                        .maxSelectNum(9)// 最大图片选择数量 int
                        .imageSpanCount(4)// 每行显示个数 int
                        .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                        .previewImage(false)// 是否可预览图片 true or false
                        .isCamera(true)// 是否显示拍照按钮 true or false
                        .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                        .sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                        .setOutputCameraPath("/CustomPath")// 自定义拍照保存路径,可不填
                        .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
            }
        });
```



该方法能直接打开相册并让用户选择图片

```java
  @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        List<File> fileList=new ArrayList<>();
        if (requestCode == PictureConfig.CHOOSE_REQUEST) {
            if (resultCode == RESULT_OK) {
                List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
//                StringBuilder sb = new StringBuilder();

                for (LocalMedia p : selectList) {
//                    sb.append(p);
//                    sb.append("\n");
                    fileList.add( new File(p.getPath()));
                }
                try {
                    NetworkManager.getInstance().MutiprtTest(new Callback<ResultBean>() {
                        @Override
                        public void onResponse(Call<ResultBean> call, Response<ResultBean> response) {
                            ResultBean resultBean=  response.body();
                            Toast.makeText(MainActivity.this,(String)resultBean.getData(),Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Call<ResultBean> call, Throwable t) {
                            Log.e("MainActivity", t.getMessage()  );
                        }
                    },fileList);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //yjPublishEdit.setText(sb.toString());
            }
        }

       
  
```

该回调方法用于用户选择完图片回调，获得图片File.

