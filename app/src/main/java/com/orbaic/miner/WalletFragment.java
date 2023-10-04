package com.orbaic.miner;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class WalletFragment extends Fragment {
    View mainView;

    LinearLayout winnerLayout;

    private ProgressBar quizProgressBar, miningProgressBar;
    private int progressStatus = 0;
    private Handler handler = new Handler();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       mainView = inflater.inflate(R.layout.fragment_wallet, container, false);
        quizProgressBar = mainView.findViewById(R.id.quizProgressBar);
        miningProgressBar = mainView.findViewById(R.id.miningProgressBar);
        winnerLayout = mainView.findViewById(R.id.winnerLayout);
        return mainView;
    }

    @Override
    public void onStart() {
        super.onStart();
        winnerLayout.setOnClickListener(v -> {
            Dialog dialog = new Dialog(getActivity());
            dialog.setContentView(R.layout.winning_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.findViewById(R.id.okButton).setOnClickListener(view -> {
                dialog.cancel();
            });
            dialog.show();
        });
    }
}
