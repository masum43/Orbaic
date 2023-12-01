package com.orbaic.miner.settings;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orbaic.miner.MainActivity2;
import com.orbaic.miner.R;
import com.orbaic.miner.common.SpManager;
import com.orbaic.miner.databinding.FragmentSettingsBinding;
import com.orbaic.miner.profile.Profile;

public class SettingsFragment extends Fragment {
    private FragmentSettingsBinding binding;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSettingsBinding.inflate(getLayoutInflater(), container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        readFirebaseData();
        initClicks();

    }

    private void initClicks() {
        binding.ivCopy.setOnClickListener(v-> {
            ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(CLIPBOARD_SERVICE);
            clipboard.setText(binding.tvUserId.getText());

            Toast.makeText(getContext(), "Copied your ID", Toast.LENGTH_SHORT).show();
        });

        binding.ivCopyMyReferCode.setOnClickListener(v-> {
            ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(CLIPBOARD_SERVICE);
            clipboard.setText(binding.tvMyReferCode.getText());

            Toast.makeText(getContext(), "Copied your ID", Toast.LENGTH_SHORT).show();
        });

        binding.holderEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFragment(new Profile());
            }
        });

        binding.switchNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SpManager.saveBoolean(SpManager.KEY_IS_NOTIFICATION_ENABLED, isChecked);
            if (isChecked) {
                Toast.makeText(requireContext(), "Notification enabled", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(requireContext(), "Notification disabled", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void readFirebaseData() {
        DatabaseReference myRef = firebaseDatabase.getReference("users").child(mAuth.getUid());
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String mName = snapshot.child("name").getValue().toString();
                binding.tvName.setText(mName);
                String myReferCode = snapshot.child("referral").getValue().toString();
                binding.tvMyReferCode.setText(myReferCode);
                String uid = mAuth.getUid();
                binding.tvUserId.setText(uid);
                if (snapshot.hasChild("profile_image")) {
                    String profile_image = snapshot.child("profile_image").getValue().toString();
                    Glide.with(requireContext()).load(profile_image).into(binding.ivProfile);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void loadFragment(Fragment f) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, f);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
