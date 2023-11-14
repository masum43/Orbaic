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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orbaic.miner.R;
import com.orbaic.miner.common.Constants;
import com.orbaic.miner.common.SpManager;
import com.orbaic.miner.databinding.FragmentWalletBinding;

import java.util.Locale;

public class WalletFragment extends Fragment {
    private FragmentWalletBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWalletBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        readData();
        initClicks();
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
                String qzCountStr = "0";
                if (snapshot.hasChild("qz_count")) {
                    qzCountStr = snapshot.child("qz_count").getValue().toString();
                }
                int qzCount = Integer.parseInt(qzCountStr);
                if (qzCount > 300) qzCount = 300;
                setUpQuizProgress(qzCount);

                String miningHoursStr = "0";
                if (snapshot.hasChild("mining_hours")) {
                    miningHoursStr = snapshot.child("mining_hours").getValue().toString();
                }
                int miningHours = Integer.parseInt(miningHoursStr);
                if (miningHours > 300) miningHours = 300;
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
        int totalProgress = 300;
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
