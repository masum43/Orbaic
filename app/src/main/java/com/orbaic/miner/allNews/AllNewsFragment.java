package com.orbaic.miner.allNews;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.orbaic.miner.R;
import com.orbaic.miner.common.RetrofitClient2;
import com.orbaic.miner.databinding.FragmentAllNewsBinding;
import com.orbaic.miner.home.Post2;
import com.orbaic.miner.wordpress.Post;
import com.orbaic.miner.wordpress.PostAdapter;
import com.orbaic.miner.wordpress.PostAdapter2;
import com.orbaic.miner.wordpress.RetrofitClient;
import com.orbaic.miner.wordpress.WordpressData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AllNewsFragment extends Fragment {
    private FragmentAllNewsBinding binding;
    private List<Post> postItemList;
    private List<Post2.Post2Item> postItemList2;

    public AllNewsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAllNewsBinding.inflate(getLayoutInflater(), container, false);
        newsFromWordpressBlog2();
        return binding.getRoot();
    }



    private void newsFromWordpressBlog() {

        WordpressData api = RetrofitClient.getApiService();
        Call<List<Post>> call = api.getPost();




        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                Log.d("RetrofitResponse", "Status Code " + response.code());
                postItemList = response.body();
                binding.rvNews.setHasFixedSize(true);
                binding.rvNews.setLayoutManager(new LinearLayoutManager(getContext()));

                Collections.sort(postItemList, (o1, o2) -> {
                    if (o1.getFeatured_media() == 1 && o2.getFeatured_media() != 1) {
                        return -1; // o1 comes first
                    } else if (o1.getFeatured_media() != 1 && o2.getFeatured_media() == 1) {
                        return 1; // o2 comes first
                    } else {
                        return 0; // maintain the original order if both or neither have featured_media == 1
                    }
                });

                binding.rvNews.setAdapter(new PostAdapter(getContext(), postItemList));
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Log.d("RetrofitResponse", "Error");
            }
        });

    }

    private void newsFromWordpressBlog2() {

        WordpressData api = RetrofitClient2.getApiService();
        Call<List<Post2.Post2Item>> call = api.getPost2();


        call.enqueue(new Callback<List<Post2.Post2Item>>() {
            @Override
            public void onResponse(Call<List<Post2.Post2Item>> call, Response<List<Post2.Post2Item>> response) {
                Log.d("RetrofitResponse", "Status Code " + response.code());
                postItemList2 = response.body();
                binding.rvNews.setHasFixedSize(true);
                binding.rvNews.setLayoutManager(new LinearLayoutManager(getContext()));

                List<Post2.Post2Item> firstFiveItems = new ArrayList<>();
                if (postItemList2.size() >= 5) {
                    firstFiveItems.addAll(postItemList2.subList(0, 5));
                } else {
                    firstFiveItems.addAll(postItemList2);
                }
/*                Collections.sort(firstFiveItems, (o1, o2) -> {
                    if (o1.getFeatured_media() == 1 && o2.getFeatured_media() != 1) {
                        return -1; // o1 comes first
                    } else if (o1.getFeatured_media() != 1 && o2.getFeatured_media() == 1) {
                        return 1; // o2 comes first
                    } else {
                        return 0; // maintain the original order if both or neither have featured_media == 1
                    }
                });*/

                Log.e("enque1122", "onResponse: "+ new Gson().toJson(firstFiveItems));
                binding.rvNews.setAdapter(new PostAdapter2(getContext(), firstFiveItems));



            }

            @Override
            public void onFailure(Call<List<Post2.Post2Item>> call, Throwable t) {
                Log.d("RetrofitResponse", "Error");
            }
        });

    }
}