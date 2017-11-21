package com.hooooong.bbs.util;

import com.hooooong.bbs.model.Const;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Android Hong on 2017-11-21.
 */

public class ServiceGenerator {

    public static <T> T createBbs(Class<T> className){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Const.BBS_URL)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(className);
    }
}
