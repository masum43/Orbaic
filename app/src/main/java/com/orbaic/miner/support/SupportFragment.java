package com.orbaic.miner.support;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.orbaic.miner.HorizontalListAdapter;
import com.orbaic.miner.R;
import com.orbaic.miner.TeamMembersFragment;
import com.orbaic.miner.common.Constants;
import com.orbaic.miner.databinding.FragmentSupportBinding;
import com.orbaic.miner.myTeam.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SupportFragment extends Fragment {
    FragmentSupportBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSupportBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initClicks();

    }

    private void initClicks() {
        binding.tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String subject = binding.etSubject.getText().toString();
                String desc = binding.etDescription.getText().toString();
                if (subject.isEmpty()) {
                    Toast.makeText(getContext(), "Subject field can't be empty!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (desc.isEmpty()) {
                    Toast.makeText(getContext(), "Description field can't be empty!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendEmail(subject, desc);
            }
        });

        binding.facebook.setOnClickListener(v -> {
            String url = "https://www.facebook.com/orbaic/";

            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);

        });

        binding.twitter.setOnClickListener(v -> {
            String url = "https://twitter.com/Orbaicproject?s=08";

            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });

        binding.telegram.setOnClickListener(v -> {
            String url = "https://t.me/OrbaicEnglish";

            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);

        });

        binding.instagram.setOnClickListener(v -> {
            String url = "https://www.instagram.com/orbaicproject/";
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
    }


    private void sendEmail(String subject, String description) {
        String[] recipientEmails = {"orbaic@gmail.com"};
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, recipientEmails);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, description);

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(Intent.createChooser(intent, "Send Email"));
        } else {
            // Handle the case where no email client is available
        }
    }
}
