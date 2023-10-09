package com.orbaic.miner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TeamMembersFragment extends Fragment {
    private View mainView;
    RecyclerView recyclerView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_team_members, container, false);
        recyclerView = mainView.findViewById(R.id.teamMembersRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return mainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        TeamMembersFullAdapter adapter = new TeamMembersFullAdapter(getActivity());
        recyclerView.setAdapter(adapter);


    }
}
