package com.orbaic.miner;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class Withdrawal extends Fragment {

    private RecyclerView recyclerView;
    public Withdrawal() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_withdrawal, container, false);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //current date
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String currentDate = sdf.format(c.getTime());
        TextView date1 = (TextView) view.findViewById(R.id.cashOut_login_date_show);
        date1.setText(currentDate);

        //RecycleView Data input
        recyclerView = view.findViewById(R.id.RecyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        ArrayList list = new ArrayList<>();
        CustomAdapter customAdapter = new CustomAdapter(getContext(), list);
        recyclerView.setAdapter(customAdapter);
        //end RecycleView Data*/

        DatabaseReference ref = database.getReference("cashout").child(mAuth.getUid());
        ref.addValueEventListener(new ValueEventListener() {

            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                    DataReturn data = dataSnapshot.getValue(DataReturn.class);
                    //Toast.makeText(CashOutActivity.this, ""+dataSnapshot.getValue(),Toast.LENGTH_LONG).show();
                    list.add(data);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        TextView u_name = (TextView) view.findViewById(R.id.cashOut_username);
        TextView u_point = (TextView) view.findViewById(R.id.cashOut_login_point);
        DatabaseReference myRef = database.getReference("users").child(mAuth.getUid());
        // [START post_value_event_listener]
        myRef.addValueEventListener(new ValueEventListener() {

            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                //Toast.makeText(CashOutActivity.this, ""+datasnapshot.getValue(),Toast.LENGTH_LONG).show();
                String name = datasnapshot.child("name").getValue().toString();
                u_name.setText(name);
                String point = datasnapshot.child("point").getValue().toString();
                u_point.setText(point);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),""+error.toException(),Toast.LENGTH_LONG).show();

            }
        });
        //end Firebase data read


        //Firebase Data Update and new data write
        Button submit = (Button) view.findViewById(R.id.cashOut_submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView u_point = view.findViewById(R.id.cashOut_login_point);
                if(TextUtils.isEmpty(u_point.getText().toString().trim())){
                    Toast.makeText(getContext(),"Please check Internet Connection",Toast.LENGTH_LONG).show();
                    return;}
                String pointNumber = u_point.getText().toString();
                double oldValue = Double.valueOf(pointNumber);

                //write data element
                EditText number = (EditText) view.findViewById(R.id.cashOut_number_input);
                if(TextUtils.isEmpty(number.getText().toString().trim())){
                    number.setError("Enter Wallet Address");
                    return;
                }

                EditText newValueAdd = (EditText) view.findViewById(R.id.cashOut_point_input);
                if(TextUtils.isEmpty(newValueAdd.getText().toString().trim())){
                    newValueAdd.setError("Enter Amount");
                    return;
                }

                EditText inputMethod = (EditText) view.findViewById(R.id.cashOut_method_input);
                if(TextUtils.isEmpty(inputMethod.getText().toString().trim())){
                    inputMethod.setError("Enter Wallet Name");
                    return;
                }
                //end write data element*/
                String addValue = newValueAdd.getText().toString().trim();
                Float valueAdd = Float.valueOf(addValue);
                double i  = valueAdd;
                double o = oldValue;
                if (o<i){
                    newValueAdd.setError("Insufficient Coin");
                    return;
                }
                if(oldValue <= 99){
                    newValueAdd.setError("Your Coin is less then minimum Coin");
                    return;
                }else if(valueAdd == null){
                    newValueAdd.setError("Please Enter only Number");
                    return;

                }else if(valueAdd <= 99){
                    newValueAdd.setError("Your input Coin is less then minimum Coin");
                    return;
                }else {
                    double newNumber = oldValue - valueAdd;
                    String s = String.valueOf(newNumber);


                    DatabaseReference myRef = database.getReference("users");
                    myRef.child(mAuth.getUid()).child("point").setValue(s);
                }


                String point = newValueAdd.getText().toString().trim();
                String mNumber = number.getText().toString().trim();
                String method = inputMethod.getText().toString().trim();
                DatabaseReference Ref = database.getReference("cashout").child(mAuth.getUid());
                String uuid = Ref.push().getKey();
                Ref.child(uuid).child("status").setValue("unpaid");
                Ref.child(uuid).child("date").setValue(currentDate);
                Ref.child(uuid).child("method").setValue(method);
                Ref.child(uuid).child("point").setValue(point);
                Ref.child(uuid).child("number").setValue(mNumber);



            }
        });









        return view;
    }
}