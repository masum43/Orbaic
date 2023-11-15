package com.orbaic.miner.wallet;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orbaic.miner.FirebaseData;
import com.orbaic.miner.R;
import com.orbaic.miner.common.Constants;
import com.orbaic.miner.common.SpManager;
import com.orbaic.miner.databinding.FragmentWalletBinding;
import com.orbaic.miner.quiz.LearnEarnViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class WalletFragment extends Fragment {
    private FragmentWalletBinding binding;
    WalletViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWalletBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(WalletViewModel.class);
        readData();
        fetchRewards();
        initClicks();
    }

    private void fetchRewards() {
        binding.rvRewardTokens.setLayoutManager(new LinearLayoutManager(requireContext()));
        RewardAdapter adapter = new RewardAdapter(reward -> {
            if (reward.getCode().equals("quiz")) {
                boolean isGranted = checkQuizEligibility(reward);
                if (isGranted) {
                    FirebaseData data = new FirebaseData();
                    double point = viewModel.getPoint() + Double.parseDouble(reward.getBonus());
                    data.addQuizRewardPoints(String.valueOf(point));
                    Toast.makeText(requireContext(), reward.getBonus()+ " ACI Rewarded Coin has been added to your balance", Toast.LENGTH_SHORT).show();
                    readData();
                }
                else {
                    Toast.makeText(requireContext(), "You are not eligible for this reward!!", Toast.LENGTH_SHORT).show();
                }
            }
            else if (reward.getCode().equals("mining")) {
                boolean isGranted = checkMiningEligibility(reward);
                if (isGranted) {
                    FirebaseData data = new FirebaseData();
                    double point = viewModel.getPoint() + Double.parseDouble(reward.getBonus());
                    data.addMiningRewardPoints(String.valueOf(point));
                    Toast.makeText(requireContext(), reward.getBonus()+ " ACI Rewarded Coin has been added to your balance", Toast.LENGTH_SHORT).show();
                    readData();
                }
                else {
                    Toast.makeText(requireContext(), "You are not eligible for this reward!!", Toast.LENGTH_SHORT).show();
                }
            }
        });


        binding.rvRewardTokens.setAdapter(adapter);

        DatabaseReference rewardsRef = FirebaseDatabase.getInstance().getReference().child("rewards");
        rewardsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<RewardModel> rewardsList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    RewardModel reward = snapshot.getValue(RewardModel.class);
                    rewardsList.add(reward);
                }

                adapter.submitList(rewardsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("FirebaseError", "Error: " + databaseError.getMessage());
            }
        });

    }

    private boolean checkQuizEligibility(RewardModel reward) {
        int quizCount = viewModel.getQuizCount();
        if (quizCount >=  300) {
            return true;
        }
        return false;
    }

    private boolean checkMiningEligibility(RewardModel reward) {
        int miningHoursCount = viewModel.getMiningHoursCount();
        if (miningHoursCount >=  720) {
            return true;
        }
        return false;
    }

    private void initClicks() {
        binding.transfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireContext(), "Coming Soon..", Toast.LENGTH_SHORT).show();
            }
        });

        binding.withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireContext(), "Coming Soon..", Toast.LENGTH_SHORT).show();
            }
        });

        binding.stacking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(requireContext(), "Coming Soon..", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        binding.winnerLayout.setOnClickListener(v -> {
            Dialog dialog = new Dialog(getActivity());
            dialog.setContentView(R.layout.winning_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.findViewById(R.id.okButton).setOnClickListener(view -> {
                dialog.cancel();
            });
            dialog.show();
        });
    }

    public void readData() {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference myRef = firebaseDatabase.getReference("users").child(mAuth.getUid());
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("DATA_READ", "readData");
                String name = snapshot.child("name").getValue().toString();
                String email = snapshot.child("email").getValue().toString();
                String point = snapshot.child("point").getValue().toString();
                viewModel.setPoint(Double.parseDouble(point));
                String qzCountStr = "0";
                if (snapshot.hasChild("qz_count")) {
                    qzCountStr = snapshot.child("qz_count").getValue().toString();
                }
                int qzCount = Integer.parseInt(qzCountStr);
                if (qzCount > 300) qzCount = 300;
                viewModel.setQuizCount(qzCount);
                setUpQuizProgress(qzCount);

                String miningHoursStr = "0";
                if (snapshot.hasChild("mining_count")) {
                    miningHoursStr = snapshot.child("mining_count").getValue().toString();
                }
                int miningHours = Integer.parseInt(miningHoursStr);
                if (miningHours > 720) miningHours = 720;
                viewModel.setMiningHoursCount(miningHours);
                setUpMiningHourProgress(miningHours);

                double Coin = Double.valueOf(point);
                String format = String.format(Locale.getDefault(), "%.5f", Coin);
                binding.tvAciCoin.setText("ACI "+ format);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void setUpQuizProgress(int currentProgress) {
        binding.tvQuizSummary.setText(currentProgress + "/300");
        int totalProgress = 300;
        int progressPercentage = (int) ((float) currentProgress / totalProgress * 100);
//        binding.quizProgressBar.setProgress(progressPercentage);
        animateProgressBar(progressPercentage);
    }

    private void animateProgressBar(int progressPercentage) {
        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(binding.quizProgressBar, "progress", progressPercentage);
        progressAnimator.setDuration(1000); // Set the duration of the animation in milliseconds (adjust as needed)
        progressAnimator.start();
    }


    private void setUpMiningHourProgress(int currentProgress) {
        binding.tvMiningHours.setText(currentProgress + "/720");
        int totalProgress = 720;
        int progressPercentage = (int) ((float) currentProgress / totalProgress * 100);
//        binding.quizProgressBar.setProgress(progressPercentage);
        animateMiningHourProgressBar(progressPercentage);
    }

    private void animateMiningHourProgressBar(int progressPercentage) {
        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(binding.miningProgressBar, "progress", progressPercentage);
        progressAnimator.setDuration(1000); // Set the duration of the animation in milliseconds (adjust as needed)
        progressAnimator.start();
    }
}
