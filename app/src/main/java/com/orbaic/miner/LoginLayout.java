package com.orbaic.miner;




import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class LoginLayout extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_layout);

        //register
        TextView register_activity = findViewById(R.id.sign_up);
        register_activity.setOnClickListener(v->{
            Intent intent = new Intent(LoginLayout.this, UserRegister.class);
            startActivity(intent);
        });

        //google login configure
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        //end google login configure

        //Google Login
        ImageView googleLogin = findViewById(R.id.imageView2);
        googleLogin.setOnClickListener(v->{
            signGoogle();
        });

        //forget
        TextView forgetPassword = findViewById(R.id.forget_password_login);
        forgetPassword.setOnClickListener(v->{
            Intent intent = new Intent(LoginLayout.this, ForgetPassword.class);
            startActivity(intent);
        });

        //firebase auto login
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            FirebaseData data = new FirebaseData();
            data.readData();
            reload();

        }

        //firebase login
        EditText username = (EditText) findViewById(R.id.user_login_email);
        EditText Login_password = (EditText) findViewById(R.id.login_password);

        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(v->{
            if (TextUtils.isEmpty(username.getText().toString())){
                username.setError("Please Enter the Email");
                return ;
            }
            if (TextUtils.isEmpty(Login_password.getText().toString())){
                Login_password.setError("Please Enter the Password");
                return;
            }
            progressDialog = new ProgressDialog(LoginLayout.this);
            progressDialog.setTitle("Please wait");
            progressDialog.setMessage("Loading...");
            progressDialog.create();
            progressDialog.setCancelable(false);
            progressDialog.show();
            sign_function();
        });


    }

    private void signGoogle() {
        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent,RC_SIGN_IN);
    }

    private void reload() {
        Intent intent = new Intent(LoginLayout.this, MainActivity2.class);
        startActivity(intent);
        finish();
    }

    private void sign_function() {
        TextView fail_login = (TextView) findViewById(R.id.login_incorrect_password);
        EditText username = (EditText) findViewById(R.id.user_login_email);
        EditText Login_password = (EditText) findViewById(R.id.login_password);
        String email = username.getText().toString().trim();
        String password = Login_password.getText().toString().trim();
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            progressDialog.dismiss();
                            updateUI();

                        } else {
                            // If sign in fails, display a message to the user.
                            fail_login.setVisibility(View.VISIBLE);
                            progressDialog.dismiss();
                        }
                    }
                });
        // [END sign_in_with_email]
    }

    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit");
        builder.setMessage("Do you want to exit");
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finishAffinity();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.create().show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            //Toast.makeText(LoginLayout.this,"code successful",Toast.LENGTH_LONG).show();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
            progressDialog = new ProgressDialog(LoginLayout.this);
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

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInAnonymously()
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // Login successful
                        progressDialog.dismiss();
                        updateUI();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(LoginLayout.this, "You have no account. To create a new account Please clear App data", Toast.LENGTH_SHORT).show();
                    }
                });
        /*mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginLayout.this,"token Successful",Toast.LENGTH_LONG).show();
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser uid = mAuth.getCurrentUser();
                            if (uid != null){
                                progressDialog.dismiss();
                                updateUI();
                            }else {
                                Toast.makeText(LoginLayout.this, "Please create a new account", Toast.LENGTH_SHORT).show();
                            }
                        } else {

                            Toast.makeText(LoginLayout.this,"token Error",Toast.LENGTH_LONG).show();
                            // If sign in fails, display a message to the user.
                        }
                    }
                });*/
    }

    private void updateUI() {
        Intent intent = new Intent(LoginLayout.this, MainActivity2.class);
        startActivity(intent);
        finish();
    }
}