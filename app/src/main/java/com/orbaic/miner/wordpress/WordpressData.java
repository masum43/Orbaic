package com.orbaic.miner.wordpress;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface WordpressData {
    @GET("posts")
    Call<List<Post>> getPost();
}
