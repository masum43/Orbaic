package com.orbaic.miner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class ForgetPassword extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        //Already an account
        TextView alreadyAccount = findViewById(R.id.alreadyAccount);
        alreadyAccount.setOnClickListener(v->{
            Intent intent = new Intent(ForgetPassword.this, LoginLayout.class);
            startActivity(intent);
        });
        //register a new account
        TextView registerAccount = findViewById(R.id.forgetPasswordRegister);
        registerAccount.setOnClickListener(v->{
            Intent i  = new Intent(ForgetPassword.this, UserRegister.class);
            startActivity(i);
        });

        //firebase auth
        mAuth = FirebaseAuth.getInstance();
        TextView incorrectEmail = findViewById(R.id.incorrect_password_forget_password);
        incorrectEmail.setVisibility(View.GONE);

        EditText forgetEmail = findViewById(R.id.forgetPasswordEditText);
        Button forgetButton =findViewById(R.id.forgetPasswordButton);
        forgetButton.setOnClickListener(v->{
            
            if(TextUtils.isEmpty(forgetEmail.getText().toString().trim())){
                forgetEmail.setError("Please Enter your email");
                return;
            }
            mAuth.sendPasswordResetEmail(forgetEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    incorrectEmail.setVisibility(View.VISIBLE);
                    if(task.isSuccessful()){
                        incorrectEmail.setTextColor(getResources().getColor(R.color.green_pastel));
                        incorrectEmail.setText("Password successfully resend. Please check Email.");
                    }else{
//                        incorrectEmail.setText("Incorrect Email or You have no Account");
//                        incorrectEmail.setVisibility(View.VISIBLE);
                        String errorMessage = "Password reset email not sent. Error: ";
                        if (task.getException() != null) {
                            errorMessage += task.getException().getMessage();
                        } else {
                            errorMessage += "Unknown error occurred.";
                        }
                        Log.e("PasswordReset", errorMessage);
                        incorrectEmail.setText(errorMessage);
                    }
                }
            });
        });
    }
}