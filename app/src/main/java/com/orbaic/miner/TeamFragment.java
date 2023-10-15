package com.orbaic.miner;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TeamFragment extends Fragment {
    View mainView;
    RecyclerView recyclerView;
    EditText etReferByCode;
    TextView tvSubmit, tvMyReferCode, tvTotalPoint, tvCopy;
    HorizontalListAdapter horizontalListAdapter;
    TextView tvTeamMemberCount, viewAll;
    List<Team> teamList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_team, container, false);
        tvTeamMemberCount = mainView.findViewById(R.id.tvTeamMemberCount);
        viewAll = mainView.findViewById(R.id.viewAll);
        recyclerView = mainView.findViewById(R.id.horizontalRecyclerView);
        etReferByCode = mainView.findViewById(R.id.etReferByCode);
        tvSubmit = mainView.findViewById(R.id.tvSubmit);
        tvMyReferCode = mainView.findViewById(R.id.tvMyReferCode);
        tvTotalPoint = mainView.findViewById(R.id.tvTotalPoint);
        tvCopy = mainView.findViewById(R.id.tvCopy);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        return mainView;
    }

    @Override
    public void onStart() {
        super.onStart();

/*        List<Integer> dataList = new ArrayList<>();
        for(int i = 0; i < 20; i++) {
            dataList.add(R.drawable.demo_avatar);
        }*/
        // Add more items to the dataList as needed
   /*     horizontalListAdapter = new HorizontalListAdapter(getActivity(), dataList);
        recyclerView.setAdapter(horizontalListAdapter);*/
        viewAll.setOnClickListener(view -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, new TeamMembersFragment())
                    .addToBackStack(null)
                    .commit();
        });

        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String referBy = etReferByCode.getText().toString();
                if (referBy.isEmpty()) {
                    Toast.makeText(requireContext(), "Please enter valid refer code", Toast.LENGTH_SHORT).show();
                    return;
                }
                validateReferralCode(referBy);
            }
        });

        tvCopy.setOnClickListener(view -> {
            String textToCopy = tvMyReferCode.getText().toString();
            if (textToCopy.isEmpty()) {
                Toast.makeText(requireContext(), "Nothing to copy!!", Toast.LENGTH_SHORT).show();
                return;
            }
            ClipboardManager clipboardManager = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("label", textToCopy);
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(requireContext(), "Text copied to clipboard", Toast.LENGTH_SHORT).show();
        });



        readData();
        getMyTeam();
    }

    private void validateReferralCode(final String enteredReferralCode) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        Query query = usersRef.orderByChild("referral").equalTo(enteredReferralCode);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    updateReferCode(enteredReferralCode);
                } else {
                    Toast.makeText(requireContext(), "Invalid referral code. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors, if any
            }
        });
    }

    private void updateReferCode(String enteredReferralCode) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        Map<String, Object> map = new HashMap<>();
        map.put("referredBy", enteredReferralCode);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        myRef.child(mAuth.getUid()).updateChildren(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        etReferByCode.setClickable(false);
                        etReferByCode.setEnabled(false);
                        tvSubmit.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle the error
                    }
                });
    }

    public void readData() {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference myRef = firebaseDatabase.getReference("users").child(mAuth.getUid());
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue().toString();
                String point = snapshot.child("point").getValue().toString();
 /*               referralStatus = snapshot.child("referralButton").getValue().toString();
                if (snapshot.child("miningStartTime").exists()) {
                    miningStartTime = snapshot.child("miningStartTime").getValue().toString();
                }
                else miningStartTime = "-1";*/

//                String miningStatus = checkMiningStatus(miningStartTime);

                etReferByCode.setClickable(true);
                etReferByCode.setEnabled(true);
                tvSubmit.setVisibility(View.VISIBLE);

                if (snapshot.child("referredBy").exists()) {
                    String referralBy = snapshot.child("referredBy").getValue().toString();
                    if (!referralBy.isEmpty()) {
                        etReferByCode.setText(referralBy);
                        etReferByCode.setClickable(false);
                        etReferByCode.setEnabled(false);
                        tvSubmit.setVisibility(View.GONE);
                    }

                }

                String myReferCode = snapshot.child("referral").getValue().toString();
                tvMyReferCode.setText(myReferCode);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

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

                    teamList.add(new Team(userId, userName, userEmail, "", miningStartTime, miningStatus));

                    totalPoint += Double.parseDouble(point);

                }

                String format = String.format(Locale.getDefault(), "%.5f", totalPoint);
                tvTotalPoint.setText(format + " ACI");

                HorizontalListAdapter adapter = new HorizontalListAdapter(getActivity(), teamList);
                recyclerView.setAdapter(adapter);

                tvTeamMemberCount.setText("My Team ("+teamList.size()+")");
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
                double totalPoint = 0;
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

                    teamList.add(new Team(userId, userName, "", "", miningStartTime, miningStatus));

                    totalPoint += Double.parseDouble(point);

                }

                String format = String.format(Locale.getDefault(), "%.5f", totalPoint);
                tvTotalPoint.setText(format + " ACI");

                HorizontalListAdapter adapter = new HorizontalListAdapter(getActivity(), teamList);
                recyclerView.setAdapter(adapter);

                tvTeamMemberCount.setText("My Team ("+teamList.size()+")");

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
