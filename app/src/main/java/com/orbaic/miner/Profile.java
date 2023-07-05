package com.orbaic.miner;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.content.ClipboardManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Profile extends Fragment {

    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;

    TextView dataSubmit,edit,id,emailName;
    EditText  Username, phone, country;

    public Profile() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        id = view.findViewById(R.id.userID);
        Username = view.findViewById(R.id.userName);
        phone = view.findViewById(R.id.phone);
        country = view.findViewById(R.id.userCountry);
        emailName = view.findViewById(R.id.emailAddress_profile);


        id.setOnClickListener(v-> {
            ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(CLIPBOARD_SERVICE);
            clipboard.setText(id.getText());

            Toast.makeText(getContext(), "Copied your ID", Toast.LENGTH_SHORT).show();
        });

        readFirebaseData();

        dataSubmit = view.findViewById(R.id.profile_submit);
        edit = view.findViewById(R.id.pEdit);
        edit.setOnClickListener(view1 -> {
            Toast.makeText(getContext(), "Edit Your Profile", Toast.LENGTH_SHORT).show();
            Username.setEnabled(true);
            phone.setEnabled(true);
            country.setEnabled(true);
            dataSubmit.setVisibility(View.VISIBLE);
            edit.setVisibility(View.GONE);
        });

        dataSubmit.setOnClickListener(view1 -> {

            if(TextUtils.isEmpty(Username.getText().toString().trim())){
                Username.setError("Please Enter your Username");
                return;
            }
            if(TextUtils.isEmpty(phone.getText().toString().trim())){
                phone.setError("Please Enter your Phone");
                return;
            }
            if(TextUtils.isEmpty(country.getText().toString().trim())){
                country.setError("Please Enter your Country");
                return;
            }
            Username.setEnabled(false);
            phone.setEnabled(false);
            country.setEnabled(false);
            dataSubmit.setVisibility(View.GONE);
            edit.setVisibility(View.VISIBLE);
            dataSendFirebase();



        });











        return view;
    }

    private void dataSendFirebase() {
        String userName = Username.getText().toString().trim();
        String userPhone = phone.getText().toString().trim();
        String userCountry = country.getText().toString().trim();

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference myRef = firebaseDatabase.getReference("users").child(mAuth.getUid());
        myRef.child("name").setValue(userName);
        myRef.child("phone").setValue(userPhone);
        myRef.child("country").setValue(userCountry);


    }

    private void readFirebaseData() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference myRef = firebaseDatabase.getReference("users").child(mAuth.getUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String mName = snapshot.child("name").getValue().toString();
                Username.setText(mName);
                String mPhone = snapshot.child("phone").getValue().toString();
                phone.setText(mPhone);
                String mCountry = snapshot.child("country").getValue().toString();
                country.setText(mCountry);
                String uid = mAuth.getUid();
                id.setText(uid);
                String email = snapshot.child("email").getValue().toString();
                emailName.setText(email);

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}