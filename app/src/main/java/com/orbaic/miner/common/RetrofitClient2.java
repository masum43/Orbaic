package com.orbaic.miner.common;

import com.orbaic.miner.wordpress.WordpressData;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient2 {
    private static final String base_url = "https://api.orbaic.com/";

    public static Retrofit getRetrofitClient(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }

    public static WordpressData getApiService() {
        return getRetrofitClient().create(WordpressData.class);
    }
}
