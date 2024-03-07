package com.orbaic.miner.myTeam;

import android.app.AlertDialog;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orbaic.miner.HorizontalListAdapter;
import com.orbaic.miner.R;
import com.orbaic.miner.TeamMembersFragment;
import com.orbaic.miner.common.Constants;
import com.orbaic.miner.common.Loading;
import com.orbaic.miner.common.SpManager;

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
    TextView tvTeamMemberCount, viewAll;
    List<Team> teamList = new ArrayList<>();
    FirebaseDatabase database;
    FirebaseAuth mAuth;
    Loading loadingDialog;
    private String myName = "";
    TeamViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_team, container, false);
        initViews();
        SpManager.init(requireContext());
        viewModel = new ViewModelProvider(this).get(TeamViewModel.class);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        loadingDialog = new Loading(requireContext());

        initClicks();
        readData();
        getMyTeam();
        return mainView;
    }


    private void initClicks() {
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
                //Toast.makeText(requireContext(), "Coming very soon...", Toast.LENGTH_SHORT).show();
                if (!viewModel.isMyDataRead()) {
                    Toast.makeText(requireContext(), "Failed to get your data. Please check internet connection or try again.", Toast.LENGTH_SHORT).show();
                    return;
                }
                String referBy = etReferByCode.getText().toString();
                if (referBy.isEmpty() || referBy.equals(tvMyReferCode.getText().toString())) {
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
    }

    private void initViews() {
        tvTeamMemberCount = mainView.findViewById(R.id.tvTeamMemberCount);
        viewAll = mainView.findViewById(R.id.viewAll);
        recyclerView = mainView.findViewById(R.id.horizontalRecyclerView);
        etReferByCode = mainView.findViewById(R.id.etReferByCode);
        tvSubmit = mainView.findViewById(R.id.tvSubmit);
        tvMyReferCode = mainView.findViewById(R.id.tvMyReferCode);
        tvTotalPoint = mainView.findViewById(R.id.tvTotalPoint);
        tvCopy = mainView.findViewById(R.id.tvCopy);
    }


    private void validateReferralCode(final String desiredReferKey) {
        loadingDialog.showLoadingDialog();
        DatabaseReference referKeysRef = FirebaseDatabase.getInstance().getReference().child("referKeys");
        DatabaseReference specificReferKeyRef = referKeysRef.child(desiredReferKey);

        specificReferKeyRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userId = dataSnapshot.child("userId").getValue(String.class);
                    addBonusPointToMyReferralUser(userId);
                    addMeIntoReferralTeam(userId, myName);
                    updateMyReferCodeAndAddBonusPoint(userId, desiredReferKey);
                } else {
                    loadingDialog.closeLoadingDialog();
                    Toast.makeText(requireContext(), "Invalid referral code. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(requireContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });





/*        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("referKeys");
        Query query = usersRef.orderByChild("referral").equalTo(enteredReferralCode);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot mSnap : dataSnapshot.getChildren()) {
                        String name = mSnap.child("name").getValue().toString();
                        String userId = mSnap.getKey();
                        addIntoReferTeam(userId, name);
                        updateReferCode(enteredReferralCode);
                    }

                    Log.e("validateReferralCode", "name: "+ dataSnapshot);
                } else {
                    loadingDialog.closeLoadingDialog();
                    Toast.makeText(requireContext(), "Invalid referral code. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors, if any
            }
        });*/
    }

    private void addBonusPointToMyReferralUser(String userId) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("users").child(userId);
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String point = "0";
                if (snapshot.hasChild("referralPoint")) {
                    point = snapshot.child("referralPoint").getValue().toString();
                }
                double bonusPoint = Double.parseDouble(point) + 3;
                myRef.child("referralPoint").setValue(String.valueOf(bonusPoint));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addMeIntoReferralTeam(String userId, String name) {
        if (userId != null && !userId.isEmpty()) {
            DatabaseReference referralRef = database.getReference("referralUser");
            HashMap<String, String> map = new HashMap<>();
            map.put("name", name);
            map.put("status", viewModel.getMiningStartTime());
            referralRef.child(userId).child(mAuth.getUid().toString()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        loadingDialog.closeLoadingDialog();
                    }
                }
            });
        }
    }

    private void updateMyReferCodeAndAddBonusPoint(String userId, String enteredReferralCode) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("users");
        Map<String, Object> map = new HashMap<>();
        map.put("referredBy", userId);
        map.put("referredByCode", enteredReferralCode);
        String myReferralPoint = tvMyReferCode.getTag().toString();
        double bonusPoint = Double.parseDouble(myReferralPoint) + 3; // add point on current user
        map.put("referralPoint", String.valueOf(bonusPoint));

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        myRef.child(mAuth.getUid()).updateChildren(map)
                .addOnSuccessListener(aVoid -> {
                    etReferByCode.setClickable(false);
                    etReferByCode.setEnabled(false);
                    tvSubmit.setVisibility(View.GONE);
                    showSuccessDialog();

                })
                .addOnFailureListener(e -> {
                    // Handle the error
                });
    }


    public void readData() {
        viewModel.setMyDataRead(false);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference myRef = firebaseDatabase.getReference("users").child(mAuth.getUid());
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("DATA_READ", "Team: readData");
                myName = snapshot.child("name").getValue().toString();
                String point = snapshot.child("point").getValue().toString();
                String referralPoint = "0";
                if (snapshot.hasChild("referralPoint")) {
                    referralPoint = snapshot.child("referralPoint").getValue().toString();
                }
                String referral = snapshot.child("referral").getValue().toString();

                tvMyReferCode.setTag(referralPoint);

                if (snapshot.hasChild("miningStartTime")) {
                    String miningStartTime = snapshot.child("miningStartTime").getValue().toString();
                    viewModel.setMiningStartTime(miningStartTime);
                }
                else {
                    viewModel.setMiningStartTime("-1");
                }
                viewModel.setPoint(Double.parseDouble(point));
                viewModel.setMyDataRead(true);



 /*               referralStatus = snapshot.child("referralButton").getValue().toString();
                if (snapshot.child("miningStartTime").exists()) {
                    miningStartTime = snapshot.child("miningStartTime").getValue().toString();
                }
                else miningStartTime = "-1";*/

//                String miningStatus = checkMiningStatus(miningStartTime);

                etReferByCode.setClickable(true);
                etReferByCode.setEnabled(true);
                tvSubmit.setVisibility(View.VISIBLE);

                if (snapshot.child("referredByCode").exists()) {
                    String referralBy = snapshot.child("referredByCode").getValue().toString();
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


    private void getMyTeam() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        DatabaseReference ref = database.getReference("referralUser").child(mAuth.getUid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {

            public void onDataChange(@NonNull DataSnapshot snapshot) {
                teamList.clear();
                double totalPoint = 0;
                Log.e("DATA_READ", "Team: getMyTeam");
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

                String format = String.format(Locale.ENGLISH, "%.5f", totalPoint);
                tvTotalPoint.setText(format + " ACI");

                HorizontalListAdapter adapter = new HorizontalListAdapter(getActivity(), teamList);
                recyclerView.setAdapter(adapter);

                tvTeamMemberCount.setText("My Team (" + teamList.size() + ")");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("" + error);
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

    private void showSuccessDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Success!");
        builder.setMessage("Your referral code was successful, and you've been credited with 3 ACI tokens. Please verify your balance.");

        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
