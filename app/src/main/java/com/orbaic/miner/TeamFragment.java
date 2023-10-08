package com.orbaic.miner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TeamFragment extends Fragment {
    View mainView;
    RecyclerView recyclerView;
    HorizontalListAdapter horizontalListAdapter;
    TextView viewAll;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
             mainView = inflater.inflate(R.layout.fragment_team, container, false);
             viewAll = mainView.findViewById(R.id.viewAll);
             recyclerView = mainView.findViewById(R.id.horizontalRecyclerView);
             recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
             return mainView;
    }

    @Override
    public void onStart() {
        super.onStart();

        List<Integer> dataList = new ArrayList<>();
        for(int i = 0; i < 20; i++) {
            dataList.add(R.drawable.demo_avatar);
        }
        // Add more items to the dataList as needed
        horizontalListAdapter = new HorizontalListAdapter(getActivity(), dataList);
        recyclerView.setAdapter(horizontalListAdapter);
        viewAll.setOnClickListener( view -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, new TeamMembersFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }
}
