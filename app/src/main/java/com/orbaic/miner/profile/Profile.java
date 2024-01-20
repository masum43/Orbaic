package com.orbaic.miner.profile;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.orbaic.miner.R;

public class Profile extends Fragment {

    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    TextView id,emailName, changePhone, changeName;
    EditText  Username, phone;
    ImageView ivCamera, profilePic, ivIdCopy;
    private static final int PICK_IMAGE_REQUEST = 1;

    public Profile() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        id = view.findViewById(R.id.userID);
        ivIdCopy = view.findViewById(R.id.ivIdCopy);
        Username = view.findViewById(R.id.userName);
        changePhone = view.findViewById(R.id.changePhone);
        changeName = view.findViewById(R.id.changeName);
        phone = view.findViewById(R.id.phone);
        emailName = view.findViewById(R.id.emailAddress_profile);
        ivCamera = view.findViewById(R.id.ivCamera);
        profilePic = view.findViewById(R.id.profile_pic);

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        readFirebaseData();
        initClicks();

        return view;
    }

    private void initClicks() {
        ivIdCopy.setOnClickListener(v-> {
            ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(CLIPBOARD_SERVICE);
            clipboard.setText(id.getText());

            Toast.makeText(getContext(), "Copied your ID", Toast.LENGTH_SHORT).show();
        });

        id.setOnClickListener(v-> {
            ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(CLIPBOARD_SERVICE);
            clipboard.setText(id.getText());

            Toast.makeText(getContext(), "Copied your ID", Toast.LENGTH_SHORT).show();
        });

        ivCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        changePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                renamePhone();
            }
        });

        changeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                renameName();
            }
        });
    }

    private void renameName() {
        String username = Username.getText().toString().trim();
        if (username.isEmpty()) {
            Toast.makeText(requireContext(), "Name can't be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference myRef = firebaseDatabase.getReference("users").child(mAuth.getUid());
        myRef.child("name").setValue(username).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(requireContext(), "Name changed successfully", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void renamePhone() {
        String userPhone = phone.getText().toString().trim();
        if (userPhone.isEmpty()) {
            Toast.makeText(requireContext(), "Please provide correct number", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference myRef = firebaseDatabase.getReference("users").child(mAuth.getUid());
        myRef.child("phone").setValue(userPhone).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(requireContext(), "Phone number changed successfully", Toast.LENGTH_SHORT).show();
            }
        });

    }


    private void readFirebaseData() {
        DatabaseReference myRef = firebaseDatabase.getReference("users").child(mAuth.getUid());
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String mName = snapshot.child("name").getValue().toString();
                Username.setText(mName);
                String mPhone = snapshot.child("phone").getValue().toString();
                phone.setText(mPhone);
                String mCountry = snapshot.child("country").getValue().toString();
//                country.setText(mCountry);
                String uid = mAuth.getUid();
                id.setText(uid);
                String email = snapshot.child("email").getValue().toString();
                emailName.setText(email);
                if (snapshot.hasChild("profile_image")) {
                    String profile_image = snapshot.child("profile_image").getValue().toString();
                    //Glide.with(requireContext()).load(profile_image).into(profilePic);
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            uploadImageToFirebaseStorage(imageUri);
        }
    }

    private void uploadImageToFirebaseStorage(Uri imageUri) {
        mAuth = FirebaseAuth.getInstance();
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseStorage.getReference().child("profile_images/" + mAuth.getUid());

        UploadTask uploadTask = storageReference.putFile(imageUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Image uploaded successfully
            // Get the download URL
            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                String profileImageUrl = uri.toString();

                // Update the user's node in Firebase Realtime Database with the profile image URL
                DatabaseReference myRef = firebaseDatabase.getReference("users").child(mAuth.getUid());
                myRef.child("profile_image").setValue(profileImageUrl)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(requireContext(), "Profile picture updated", Toast.LENGTH_SHORT).show();
                                readFirebaseData();
                            } else {
                                Toast.makeText(requireContext(), "Failed to update profile picture", Toast.LENGTH_SHORT).show();
                            }
                        });
            });
        }).addOnFailureListener(e -> {
            // Handle unsuccessful uploads
            Toast.makeText(requireContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}