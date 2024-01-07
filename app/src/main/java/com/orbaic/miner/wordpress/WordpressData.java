package com.orbaic.miner.wordpress;

import com.orbaic.miner.home.Post2;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface WordpressData {
    @GET("posts")
    Call<List<Post>> getPost();

    @GET("wp/v1/posts")
    Call<List<Post2.Post2Item>> getPost2();
}
