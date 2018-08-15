package com.first.testreactnativeapp.http;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by admin on 2018/8/15.
 * 客户端接口服务
 */

public interface ApiService {

    @GET
    Call<ResponseBody> downloadFileWithDynamicUrlSync(@Url String fileUrl);
}
