package com.orbaic.miner;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.orbaic.miner.common.Constants;
import com.orbaic.miner.myTeam.GridBindAdapter;
import com.orbaic.miner.myTeam.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TeamMembersFragment extends Fragment {
    private View mainView;
    private TextView tvInactiveTeamCount;
    RecyclerView recyclerView;
    EditText etSearch;
    List<Team> teamList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_team_members, container, false);
        recyclerView = mainView.findViewById(R.id.teamMembersRecyclerView);
        tvInactiveTeamCount = mainView.findViewById(R.id.tvInactiveTeamCount);
        etSearch = mainView.findViewById(R.id.etSearch);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s = charSequence.toString();
                if (s.isEmpty()) {
                    TeamMembersFullAdapter adapter = new TeamMembersFullAdapter(getActivity(), teamList);
                    recyclerView.setAdapter(adapter);
                }
                else {
                    List<Team> filterList = new ArrayList<>();
                    for (Team item : teamList) {
                        if (item.getUserName().contains(s)) {
                            filterList.add(item);
                        }
                    }
                    TeamMembersFullAdapter adapter = new TeamMembersFullAdapter(getActivity(), filterList);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return mainView;
    }



    @Override
    public void onStart() {
        super.onStart();
        getMyTeam();
    }


  /*  public void readData() {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference myRef = firebaseDatabase.getReference("users").child(mAuth.getUid());
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue().toString();
                String point = snapshot.child("point").getValue().toString();
                String myReferCode = snapshot.child("referral").getValue().toString();
                getMyTeam2(myReferCode);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }*/

    private void getMyTeam2(String myReferCode) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseRef = database.getReference();
        Query referredUsersQuery = databaseRef.child("users")
                .orderByChild("referredBy")
                .equalTo(myReferCode);

        referredUsersQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                teamList.clear();
                double totalPoint = 0;
                int inactiveTeamCount = 0;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    Log.e("getMyTeam", "userSnapshot: " + userSnapshot);
                    String userId = userSnapshot.child("id").getValue(String.class);
                    String userName = userSnapshot.child("name").getValue(String.class);
                    String userEmail = userSnapshot.child("email").getValue(String.class);
                    String point = userSnapshot.child("point").getValue(String.class);
                    String miningStartTime = "-1";
                    if (userSnapshot.child("miningStartTime").exists()) {
                        miningStartTime = userSnapshot.child("miningStartTime").getValue(String.class);
                    }
                    Log.e("getMyTeam", "miningStartTime: " + miningStartTime);

                    String miningStatus = checkMiningStatus(miningStartTime);
                    Log.e("getMyTeam", "miningStatus: " + miningStatus);
                    if (miningStatus.equals(Constants.STATUS_OFF)) {
                        inactiveTeamCount ++;
                    }

                    teamList.add(new Team(userId, userName, userEmail, "", miningStartTime, miningStatus));

                    totalPoint += Double.parseDouble(point);

                }


                TeamMembersFullAdapter adapter = new TeamMembersFullAdapter(getActivity(), teamList);
                recyclerView.setAdapter(adapter);

                tvInactiveTeamCount.setText(String.valueOf(inactiveTeamCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle any errors that occur
            }
        });
    }

    private void getMyTeam() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference ref = database.getReference("referralUser").child(mAuth.getUid());
        ref.addValueEventListener(new ValueEventListener() {

            public void onDataChange(@NonNull DataSnapshot snapshot) {
                teamList.clear();
                int inactiveTeamCount = 0;
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    Log.e("getMyTeam", "userSnapshot: " + userSnapshot);
                    String userId = userSnapshot.getKey();
                    String userName = userSnapshot.child("name").getValue(String.class);
//                    String userEmail = userSnapshot.child("email").getValue(String.class);
                    String point = "0";
                    if (userSnapshot.child("point").exists()) {
                        point = userSnapshot.child("point").getValue(String.class);
                    }
                    String miningStartTime = "-1";
                    if (userSnapshot.child("status").exists()) {
                        miningStartTime = userSnapshot.child("status").getValue(String.class);
                    }
                    Log.e("getMyTeam", "miningStartTime: " + miningStartTime);

                    String miningStatus = checkMiningStatus(miningStartTime);
                    Log.e("getMyTeam", "miningStatus: " + miningStatus);
                    if (miningStatus.equals(Constants.STATUS_OFF)) {
                        inactiveTeamCount ++;
                    }

                    teamList.add(new Team(userId, userName, "", "", miningStartTime, miningStatus));
                }


                TeamMembersFullAdapter adapter = new TeamMembersFullAdapter(getActivity(), teamList);
                recyclerView.setAdapter(adapter);

                tvInactiveTeamCount.setText(String.valueOf(inactiveTeamCount));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(""+error);
            }
        });


    }

    private String checkMiningStatus(String miningStartTime) {
        long now = System.currentTimeMillis();
        long miningStartTimeLong = Long.parseLong(miningStartTime);
        long timeElapsed = now - miningStartTimeLong;

        Log.e("checkMiningStatus", "now: " + now);
        Log.e("checkMiningStatus", "miningStartTimeLong: " + miningStartTimeLong);
        Log.e("checkMiningStatus", "timeElapsed: " + timeElapsed);

        if (timeElapsed >= 24 * 60 * 60 * 1000) {
            // If more than 24 hours have elapsed, do something
            // Your code here
            return Constants.STATUS_OFF;
        } else {
            // If less than 24 hours have elapsed, do something else
            // Your code here
            return Constants.STATUS_ON;
        }
    }
}
