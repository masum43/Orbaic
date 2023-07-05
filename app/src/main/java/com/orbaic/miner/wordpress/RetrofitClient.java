package com.orbaic.miner.wordpress;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static final String base_url = "https://blog.orbaic.com/wp-json/wp/v2/";

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
