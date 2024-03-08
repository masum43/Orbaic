package com.orbaic.miner.auth;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orbaic.miner.FirebaseData;
import com.orbaic.miner.ForgetPassword;
import com.orbaic.miner.MainActivity2;
import com.orbaic.miner.R;
import com.orbaic.miner.common.Methods;
import com.orbaic.miner.common.ProgressDialog;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class LoginLayout extends AppCompatActivity {

    private static final int RC_SIGN_IN = 100;

    private int REQUEST_LOCATION_PERMISSION = 101;

    Context context;
    private FirebaseAuth mAuth;
//    private ProgressDialog progressDialog;
    private GoogleSignInClient mGoogleSignInClient;
    private String country = "";
    LinearLayout facebookImageRegister;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_layout);

        progressDialog = new ProgressDialog.Builder(LoginLayout.this).build();

        facebookImageRegister = findViewById(R.id.facebookImageRegister);

        //register
        TextView register_activity = findViewById(R.id.sign_up);
        register_activity.setOnClickListener(v->{
            Intent intent = new Intent(LoginLayout.this, UserRegister.class);
            startActivity(intent);
        });

        context = this;

        //google login configure
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        //end google login configure

        //Google Login
        LinearLayout googleLogin = findViewById(R.id.holder_google_signIn);
        googleLogin.setOnClickListener(v->{
            signGoogle();
        });

        //forget
        ImageView forgetPassword = findViewById(R.id.forget_password_login);
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
        }else {
            getLocationPermission();
        }

        //firebase login
        EditText username = findViewById(R.id.user_login_email);
        EditText Login_password = findViewById(R.id.login_password);

        ImageView loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(v->{
            if (TextUtils.isEmpty(username.getText().toString())){
                username.setError("Please Enter the Email");
                return ;
            }
            if (TextUtils.isEmpty(Login_password.getText().toString())){
                Login_password.setError("Please Enter the Password");
                return;
            }
//            progressDialog = new ProgressDialog(LoginLayout.this);
//            progressDialog.setTitle("Please wait");
//            progressDialog.setMessage("Loading...");
//            progressDialog.create();
//            progressDialog.setCancelable(false);
//            progressDialog.show();
            sign_function();
        });


        facebookImageRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Facebook login not available at this moment", Toast.LENGTH_SHORT).show();
            }
        });

        if (Build.VERSION.SDK_INT >= 33){
            getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginLayout.this);
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
            });
        }
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
        EditText username = (EditText) findViewById(R.id.user_login_email);
        EditText Login_password = (EditText) findViewById(R.id.login_password);
        String email = username.getText().toString().trim();
        String password = Login_password.getText().toString().trim();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
//                        progressDialog.dismiss();
                        updateUI();
                    } else {
//                        progressDialog.dismiss();
                        Exception exception = task.getException();
                        if (exception != null) {
                            Log.e("LoginIssue", "onComplete: " + exception.getMessage());
                            if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                                Methods.showErrorDialog(LoginLayout.this, "Authentication Error", "Invalid email or password");
                            } else {
                                Methods.showErrorDialog(LoginLayout.this, "Authentication Error", exception.getMessage());
                            }


                        }

                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
//            progressDialog = new ProgressDialog(LoginLayout.this);
//            progressDialog.setTitle("Please wait");
//            progressDialog.setMessage("Loading...");
//            progressDialog.create();
//            progressDialog.setCancelable(false);
//            progressDialog.show();
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            firebaseAuthWithGoogle(account.getIdToken(), account);
        } catch (ApiException e) {
            System.out.println(e.getMessage());
            Toast.makeText(context, ""+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(String idToken, GoogleSignInAccount account) {
        progressDialog.show();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            if (currentUser != null) {
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
                                String uid = currentUser.getUid();
                                ref.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (!snapshot.exists()) {
                                            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(LoginLayout.this);
                                            if (account == null){
                                                return;
                                            }
                                            insertDataIntoDB("", account.getEmail(), account.getDisplayName(), "");


                                           /* String name = account.getDisplayName();
                                            String email = account.getEmail();

                                            Map<String, String> map = new HashMap<>();
                                            map.put("point","0");
                                            map.put("phone","0");
                                            map.put("click","0");
                                            map.put("country",country);
                                            map.put("birthdate","0");
                                            map.put("referral",code);
                                            map.put("referralButton","ON");
                                            map.put("type","0");
                                            map.put("name",name);
                                            map.put("email",email);
                                            map.put("id",uid);
                                            map.put("extra1","0");
                                            map.put("extra2","0");
                                            map.put("extra3","0");

                                            currentUser.sendEmailVerification();
                                            ref.child(uid).setValue(map);

                                            progressDialog.dismiss();
                                            Toast.makeText(LoginLayout.this, "New account created successfully", Toast.LENGTH_SHORT).show();
                                            updateUI();*/


                                        } else {
                                            Toast.makeText(LoginLayout.this,"You have successfully logged into your account",Toast.LENGTH_LONG).show();
                                            progressDialog.dismiss();
                                            updateUI();
                                        }


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(LoginLayout.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                });
                            } else {
                                Toast.makeText(LoginLayout.this,"Something went wrong. Try again",Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }

                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(LoginLayout.this,"Token Error",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void updateUI(String name, String referBy) {
        Intent i = new Intent(LoginLayout.this, MainActivity2.class);
        i.putExtra("name", name);
        i.putExtra("referBy", referBy);
        startActivity(i);
        finish();
    }

    private void updateUI() {
        Intent intent = new Intent(LoginLayout.this, MainActivity2.class);
        startActivity(intent);
        finish();
    }

    private void getLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // If permission is not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
            }, REQUEST_LOCATION_PERMISSION);
        } else {
            getUserCountry(context);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getUserCountry(context);
            } else {
                Methods.dialogWarningShowing(LoginLayout.this,"Location Permission is denied","Orbaic does not have user location permission. As a result, this issue is occurring. Please uninstall the app and try again, or go to Settings > Apps > Orbaic and manually grant location permission.");
            }
        }
    }



    public void getUserCountry(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        String country = "";

        if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            try {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                }
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (addresses.size() > 0) {
                        country = addresses.get(0).getCountryName();
                        System.out.println(country);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.country = country;

       /* if (country.equals("Philippines")){
            dialogShowing("Not Available", "We are stop to create account from Philippines");
        }*/

    }

    private void insertDataIntoDB(String referByUserId, String email, String name, String referredBy) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference("users");
            String uid = currentUser.getUid();

            String code = Methods.generateReferralCode(uid);

            Map<String, String> map = new HashMap<>();
            map.put("point", "0");
            map.put("referralPoint", "0");
            map.put("phone", "0");
            map.put("click", "0");
            map.put("country", country);
            map.put("birthdate", "0");
            map.put("referral", code);
            map.put("referredBy", referByUserId);
            map.put("referralButton", "ON");
            map.put("type", "0");
            map.put("name", name);
            map.put("email", email);
            map.put("id", uid);
            map.put("extra1", "0");
            map.put("extra2", "0");
            map.put("extra3", "0");

            currentUser.sendEmailVerification();

            ref.child(uid).setValue(map).addOnCompleteListener(task1 -> {
                if (task1.isSuccessful()) {
                    updateReferKey(name, referredBy, database, uid, code);
                } else {
                    progressDialog.dismiss();
                    Exception exception = task1.getException();
                    if (exception != null) {
                        String errorMessage = exception.getMessage();
                        Log.e("RegistrationError", "task1: " + errorMessage);
                        Toast.makeText(LoginLayout.this, errorMessage, Toast.LENGTH_LONG).show();
                    } else {
                        Log.e("RegistrationError", "Unknown error");
                        Toast.makeText(LoginLayout.this, "Unknown error", Toast.LENGTH_LONG).show();
                    }
                }


            });
        } else {
            Toast.makeText(LoginLayout.this, "Something went wrong. Try again", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        }
    }

    private void updateReferKey(String name, String referredBy, FirebaseDatabase database, String uid, String code) {
        DatabaseReference referKeys = database.getReference("referKeys");
        Map<String, String> referKeyMap = new HashMap<>();
        referKeyMap.put("userId", uid);
        referKeyMap.put("name", name);

        Log.e("RegistrationError", "getUid: " + uid);
        Log.e("RegistrationError", "code: " + code);

        referKeys.child(code).setValue(referKeyMap).addOnCompleteListener(task2 -> {
            if (task2.isSuccessful()) {
                progressDialog.dismiss();
                updateUI(name, referredBy);
            } else {
                progressDialog.dismiss();
                String errorMessage = task2.getException().getMessage();
                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                Log.e("RegistrationError", "task2: " + errorMessage);
            }
        });
    }

}