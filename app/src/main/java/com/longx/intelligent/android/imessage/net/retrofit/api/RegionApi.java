package com.longx.intelligent.android.imessage.net.retrofit.api;

import com.longx.intelligent.android.imessage.data.response.OperationData;
import com.xcheng.retrofit.CompletableCall;

import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by LONG on 2025/4/28 at 上午9:56.
 */
public interface RegionApi {

    @GET("region/first_region/all")
    CompletableCall<OperationData> fetchAllFirstRegions();

    @GET("region/second_region/all/{firstRegionAdcode}")
    CompletableCall<OperationData> fetchAllSecondRegions(@Path("firstRegionAdcode") int firstRegionAdcode);

    @GET("region/third_region/all/{secondRegionAdcode}")
    CompletableCall<OperationData> fetchAllThirdRegions(@Path("secondRegionAdcode") int secondRegionAdcode);
}
