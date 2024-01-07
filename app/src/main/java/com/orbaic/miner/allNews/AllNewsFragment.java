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
import com.orbaic.miner.databinding.FragmentAllNewsBinding;
import com.orbaic.miner.wordpress.Post;
import com.orbaic.miner.wordpress.PostAdapter;
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

    public AllNewsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAllNewsBinding.inflate(getLayoutInflater(), container, false);
        newsFromWordpressBlog();
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
}