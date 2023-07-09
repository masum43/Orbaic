package com.orbaic.miner;





import static android.content.Context.CLIPBOARD_SERVICE;

import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class TeamReferral extends Fragment {

    TextView code;
    TextView codeSubmit,member,memberEarn,teamShow,share;
    public String inputRefCode,currentUserPoint,userName;
    Float userPoint;
    FirebaseData input;
    public TeamReferral() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_team_referral, container, false);

        code = view.findViewById(R.id.codeRef);
        codeSubmit = view.findViewById(R.id.codeSubmit);
        EditText inputCode = view.findViewById(R.id.inputCode);
        member = view.findViewById(R.id.totalMember);
        memberEarn = view.findViewById(R.id.textView15);
        teamShow = view.findViewById(R.id.teamMemberShow);
        share = view.findViewById(R.id.share_button);

        memberDataCollection();


        share.setOnClickListener(v->{
            String refcode = code.getText().toString();
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Earn extra ACI coin");
            sendIntent.putExtra(Intent.EXTRA_TITLE, "Use my referral code and get extra bonus \n " +
                    "\n" +
                    "CODE: " +
                    ""+refcode+"" +
                    "\n" +
                    "APP Link:  https://play.google.com/store/apps/details?id=com.orbaic.miner" +
                    "\n \nDownload Now");
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Use my referral code and get extra bonus \n " +
                    "\n" +
                    "CODE: " +
                    ""+refcode+"" +
                    "\n" +
                    "APP Link:  https://play.google.com/store/apps/details?id=com.orbaic.miner" +
                    "\n \nDownload Now");
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
        });


        teamShow.setOnClickListener(v->{
            startActivity(new Intent(getContext(), ReferralData.class));
        });

        code.setOnClickListener(v->{
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(CLIPBOARD_SERVICE);
                clipboard.setText(code.getText());

                Toast.makeText(getContext(), "Copied your referral Code", Toast.LENGTH_SHORT).show();
            }

        });

         readData();

         codeSubmit.setOnClickListener(v -> {

             inputRefCode = inputCode.getText().toString().trim();

             if (TextUtils.isEmpty(inputRefCode)) {
                 inputCode.setError("Please enter Referral Code");
                 return;
             } else if (inputRefCode.equals(code.getText().toString())) {
                 Toast.makeText(getContext(), "Invalid Code", Toast.LENGTH_SHORT).show();
             }else{
                 referralCode(inputRefCode);
             }

         });

        return view;
    }

    private void memberDataCollection() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("referralUser").child(mAuth.getUid());
        String q = mAuth.getUid();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long total = snapshot.getChildrenCount();
                member.setText(String.valueOf(total));
                long totalEarn = total*3;
                memberEarn.setText(String.valueOf(totalEarn+ " ACI"));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void readData(){

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference myRef = firebaseDatabase.getReference("users").child(mAuth.getUid());
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String referralCode = snapshot.child("referral").getValue().toString();
                String button = snapshot.child("referralButton").getValue().toString();
                currentUserPoint = snapshot.child("point").getValue().toString();
                userName = snapshot.child("name").getValue().toString();
                System.out.println(button);
                System.out.println(userName);


                if (button.equals("ON")){
                    codeSubmit.setVisibility(View.VISIBLE);
                }else{
                    codeSubmit.setVisibility(View.INVISIBLE);
                }

                code.setText(referralCode);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void referralCode(String code){

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference myRef = firebaseDatabase.getReference("users");

        Query search = myRef.orderByChild("referral").equalTo(code);
        search.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot data: snapshot.getChildren()) {

                    String id = data.getKey();
                    System.out.println(id);
                    myRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            String oldUserPoint = snapshot.child("point").getValue().toString();
                            Float oldUserPoints = Float.valueOf(oldUserPoint)+3.0F;
                            String v = String.valueOf(oldUserPoints);
                            DatabaseReference myRef1 = firebaseDatabase.getReference("users")
                                    .child(id);
                            myRef1.child("point").setValue(v);
                            System.out.println(oldUserPoint);

                            Float currentUserPoints = Float.valueOf(currentUserPoint)+3.0F;
                            String p = String.valueOf(currentUserPoints);


                            DatabaseReference myRef3 = firebaseDatabase.getReference("users").child(mAuth.getUid());
                            myRef3.child("referralButton").setValue("OFF");
                            myRef3.child("referredBy").setValue(id);
                            myRef3.child("point").setValue(p);
                            DatabaseReference myRef2 = firebaseDatabase.getReference("referralUser").child(id).child(mAuth.getUid());
                            myRef2.child("name").setValue(userName);
                            myRef2.child("status").setValue("0");


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                            System.out.println("error!");

                            Toast.makeText(getContext(), "Code not Found"  , Toast.LENGTH_SHORT).show();

                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Code not Found", Toast.LENGTH_SHORT).show();
                System.out.println("error!!");
            }
        });


    }


}