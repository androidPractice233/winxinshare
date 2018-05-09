package com.scut.weixinshare.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by skyluo on 2018/4/14.
 */

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

    public static boolean isLoginFailed(String error){
        return error != null && error.contains("401");
    }
}
