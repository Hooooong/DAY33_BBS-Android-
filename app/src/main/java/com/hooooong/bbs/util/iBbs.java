package com.hooooong.bbs.util;

import com.hooooong.bbs.model.Data;
import com.hooooong.bbs.model.Result;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by Android Hong on 2017-11-21.
 */

public interface iBbs {

    @GET("bbs")
    Observable<Result> getData(@Query("type") String sort, @Query("page") int page);

    @POST("bbs")
    Observable<Result> sendPost(@Body Data data);

}
