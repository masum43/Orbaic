package com.orbaic.miner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Random;

public class UserRegister extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    int userTotal;


    private GoogleSignInClient mGoogleSignInClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        mAuth = FirebaseAuth.getInstance();

        //id create
        idCreate();

        //google login configure
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        //end google login configure

        //facebook login
        ImageView facebookLogin = findViewById(R.id.facebookImageRegister);
        facebookLogin.setOnClickListener(v->{
            Toast.makeText(UserRegister.this, "Coming soon", Toast.LENGTH_LONG).show();
        });


        //google login
        ImageView googleLogin = findViewById(R.id.googleImageRegister);
        googleLogin.setOnClickListener(v->{

               sign_in();

        });



            //login page
        TextView loginPage = findViewById(R.id.sign_up);
        loginPage.setOnClickListener(v->{
            Intent intent = new Intent(UserRegister.this, LoginLayout.class);
            startActivity(intent);
        });

        //forget password
        TextView forgetPassword = findViewById(R.id.forget_password_registration);
        forgetPassword.setOnClickListener(v->{
            Intent intent = new Intent(UserRegister.this, ForgetPassword.class);
            startActivity(intent);
        });



        EditText user_name = (EditText) findViewById(R.id.user_name_registration);
        EditText register_email = (EditText) findViewById(R.id.user_email_registration);
        EditText register_password = (EditText) findViewById(R.id.registration_password);
        TextView register_fail = (TextView) findViewById(R.id.registration_incorrect_password);

        //Sign Up with Password & Email
        Button signUp = findViewById(R.id.register_button);
        signUp.setOnClickListener(v->{
            register_fail.setVisibility(View.GONE);
            if (TextUtils.isEmpty(user_name.getText().toString())){
                user_name.setError("Enter a  Username");
                return;
            }
            if (TextUtils.isEmpty(register_email.getText().toString())){
                register_email.setError("Please Enter the Email");
                return;
            }
            if (TextUtils.isEmpty(register_password.getText().toString())){
                register_password.setError("Please Enter 6 character Password");
                return;
            }
            progressDialog = new ProgressDialog(UserRegister.this);
            progressDialog.setTitle("Please wait");
            progressDialog.setMessage("Loading...");
            progressDialog.create();
            progressDialog.setCancelable(false);
            progressDialog.show();
            sign_up();
        });

    }

    private void idCreate() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("userId");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String id = snapshot.child("totalUserId").getValue().toString();
                userTotal = Integer.parseInt(id);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(""+ error.getMessage());

            }
        });
    }

    private void sign_in() {
        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent,RC_SIGN_IN);
    }

    private void sign_up() {

        EditText user_name = (EditText) findViewById(R.id.user_name_registration);
        EditText register_email = (EditText) findViewById(R.id.user_email_registration);
        EditText register_password = (EditText) findViewById(R.id.registration_password);
        TextView register_fail = (TextView) findViewById(R.id.registration_incorrect_password);
        String email = register_email.getText().toString().trim();
        String password = register_password.getText().toString().trim();
        String name = user_name.getText().toString();


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            int id = userTotal +1;
                            String userID = String.valueOf(id);

                            Random a = new Random();
                            int b = a.nextInt(9999);
                            int c = a.nextInt(9999);
                            String code = String.valueOf(b)+String.valueOf(c);


                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("users");
                            myRef.child(mAuth.getUid()).child("point").setValue("0");
                            myRef.child(mAuth.getUid()).child("phone").setValue("0");
                            myRef.child(mAuth.getUid()).child("click").setValue("0");
                            myRef.child(mAuth.getUid()).child("country").setValue("0");
                            myRef.child(mAuth.getUid()).child("birthdate").setValue("0");
                            myRef.child(mAuth.getUid()).child("referral").setValue(code);
                            myRef.child(mAuth.getUid()).child("referralButton").setValue("ON");
                            myRef.child(mAuth.getUid()).child("type").setValue("0");
                            myRef.child(mAuth.getUid()).child("id").setValue(userID);
                            myRef.child(mAuth.getUid()).child("extra1").setValue("0");
                            myRef.child(mAuth.getUid()).child("extra2").setValue("0");
                            myRef.child(mAuth.getUid()).child("extra3").setValue("0");
                            myRef.child(mAuth.getUid()).child("name").setValue(name);
                            myRef.child(mAuth.getUid()).child("email").setValue(email);

                            DatabaseReference reference = database.getReference("userId");
                            reference.child("totalUserId").setValue(userID);
                            progressDialog.dismiss();
                            // Sign in success, update UI with the signed-in user's information
                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            register_fail.setVisibility(View.VISIBLE);
                            register_password.setError("Enter minimum 6 character Password");
                            progressDialog.dismiss();

                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Toast.makeText(UserRegister.this,"code successful",Toast.LENGTH_LONG).show();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
            progressDialog = new ProgressDialog(UserRegister.this);
            progressDialog.setTitle("Please wait");
            progressDialog.setMessage("Loading...");
            progressDialog.create();
            progressDialog.setCancelable(false);
            progressDialog.show();
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            firebaseAuthWithGoogle(account.getIdToken());
        } catch (ApiException e) {
            System.out.println(e.getMessage());
        }
    }
    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            int id = userTotal +1;
                            String userID = String.valueOf(id);

                            Random a = new Random();
                            int b = a.nextInt(9999);
                            int c = a.nextInt(9999);
                            String code = String.valueOf(b)+String.valueOf(c);

                            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(UserRegister.this);
                            String name = account.getDisplayName();
                            String email = account.getEmail();

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference("users");
                            myRef.child(mAuth.getUid()).child("point").setValue("0");
                            myRef.child(mAuth.getUid()).child("phone").setValue("0");
                            myRef.child(mAuth.getUid()).child("click").setValue("0");
                            myRef.child(mAuth.getUid()).child("country").setValue("0");
                            myRef.child(mAuth.getUid()).child("birthdate").setValue("0");
                            myRef.child(mAuth.getUid()).child("referral").setValue(code);
                            myRef.child(mAuth.getUid()).child("id").setValue(userID);
                            myRef.child(mAuth.getUid()).child("referralButton").setValue("ON");
                            myRef.child(mAuth.getUid()).child("type").setValue("0");
                            myRef.child(mAuth.getUid()).child("extra1").setValue("0");
                            myRef.child(mAuth.getUid()).child("extra2").setValue("0");
                            myRef.child(mAuth.getUid()).child("extra3").setValue("0");
                            myRef.child(mAuth.getUid()).child("name").setValue(name);
                            myRef.child(mAuth.getUid()).child("email").setValue(email);

                            DatabaseReference reference = database.getReference("userId");
                            reference.child("totalUserId").setValue(userID);

                            Toast.makeText(UserRegister.this,"token Successful",Toast.LENGTH_LONG).show();
                            // Sign in success, update UI with the signed-in user's information
                            progressDialog.dismiss();
                            updateUI();
                        } else {

                            Toast.makeText(UserRegister.this,"token Error",Toast.LENGTH_LONG).show();
                            // If sign in fails, display a message to the user.
                        }
                    }
                });
    }

    private void updateUI() {
        Intent i = new Intent(UserRegister.this,MainActivity2.class);
        startActivity(i);
        finish();
    }


    // [END auth_with_google]

}