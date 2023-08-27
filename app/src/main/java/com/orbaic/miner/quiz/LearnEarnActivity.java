package com.orbaic.miner.quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orbaic.miner.AdMobAds;
import com.orbaic.miner.FirebaseData;
import com.orbaic.miner.R;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class LearnEarnActivity extends AppCompatActivity {

    int questionsIndexCount = 0, p = 0, countTime = 100000;
    private TextView question;

    private double userPoints;
    private CountDownTimer count;

    private Button submit;
    private ProgressBar progressBar;
    FirebaseData data = new FirebaseData();
    private RadioGroup radioGroup;
    private RadioButton ans1, ans2, ans3, ans4;
    private String answer, selectedAnswer;
    List<Integer> randomNumbers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_earn_activtity);

        ans1 = findViewById(R.id.ans1);
        ans2 = findViewById(R.id.ans2);
        ans3 = findViewById(R.id.ans3);
        ans4 = findViewById(R.id.ans4);
        radioGroup = findViewById(R.id.ansGroup);
        progressBar = findViewById(R.id.progress_bar);
        question = findViewById(R.id.learn_questions);
        submit = findViewById(R.id.learn_ans_submit);

        Activity activity = LearnEarnActivity.this.getParent();

        AdMobAds mobAds = new AdMobAds(this, activity);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
               mobAds.loadRewardedAd();
            }
        });


        submit.setOnClickListener(v -> {
            /*count.cancel();
            progressBar.setProgress(p = 0);
            System.gc();
            countdown();*/

            if (ans1.isChecked()){
                selectedAnswer = ans1.getText().toString();
            }else if (ans2.isChecked()){
                selectedAnswer = ans2.getText().toString();
            } else if (ans3.isChecked()) {
                selectedAnswer = ans3.getText().toString();
            } else if (ans4.isChecked()) {
                selectedAnswer = ans4.getText().toString();
            }else {
                Toast.makeText(LearnEarnActivity.this, "You don't select any option", Toast.LENGTH_SHORT).show();
                return;
            }

            if (answer.equals(selectedAnswer)){
                userResultShow("Congratulation! \nYou give the right answer", "Correct Answer");
                userPoints = userPoints + 1;
                data.sentData(String.valueOf(userPoints));
                mobAds.showRewardedVideo();

            }else {
                userResultShow("Opp! \nYou give the wrong answer", "Wrong Answer");
                mobAds.showRewardedVideo();
            }
            radioGroup.clearCheck();
            count.cancel();
            progressBar.setProgress(p = 0);
            System.gc();
        });


        int numberOfRandomNumbers = 5;
        int min = 0;
        int max = 94;
        randomNumbers = generateUniqueRandomNumbers(numberOfRandomNumbers, min, max);
        question(randomNumbers.get(questionsIndexCount));
        readData();

        progressBar.setProgress(p);

        //countdown();



    }

    private void loadQuestion() {
        if (questionsIndexCount == 1){
            long time = System.currentTimeMillis();
            long enableTime = time + 43200000;
            data.anyPath(String.valueOf(enableTime),"extra1");
        }

        if(questionsIndexCount < 5){
            question(randomNumbers.get(questionsIndexCount));
        }else {
            Toast.makeText(LearnEarnActivity.this,
                    "You have finished all questions", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        count.cancel();
        questionsIndexCount = 0;
    }

    private void countdown() {
        count = new CountDownTimer(countTime,1000){
            @Override
            public void onTick(long l) {
                p = p+1;
                progressBar.setProgress(p);
            }
            @Override
            public void onFinish() {
                Toast.makeText(LearnEarnActivity.this, "Timeout", Toast.LENGTH_SHORT).show();
                submit.setVisibility(View.GONE);
            }
        }.start();
    }

    private void question(int a){
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("question").child("questions").child(String.valueOf(a));
        reference.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //System.out.println(snapshot);
                String q1 = Objects.requireNonNull(snapshot.child("question").getValue()).toString();
                question.setText(q1);
                String a1 = Objects.requireNonNull(snapshot.child("ans1").getValue()).toString();
                ans1.setText(a1);
                String a2 = Objects.requireNonNull(snapshot.child("ans2").getValue()).toString();
                ans2.setText(a2);
                String a3 = Objects.requireNonNull(snapshot.child("ans3").getValue()).toString();
                ans3.setText(a3);
                String a4 = Objects.requireNonNull(snapshot.child("ans4").getValue()).toString();
                ans4.setText(a4);
                //System.out.println(q1);
                answer = Objects.requireNonNull(snapshot.child("correctAnswer").getValue()).toString();
                //System.out.println(answer);

                countdown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void readData(){

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference myRef = firebaseDatabase.getReference("users").child(Objects.requireNonNull(mAuth.getUid()));
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String point = Objects.requireNonNull(snapshot.child("point").getValue()).toString();
                userPoints = Double.parseDouble(point);
                System.out.println(userPoints);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void userResultShow(String text, String Title){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(Title);
        builder.setMessage(text);
        builder.setCancelable(false);
        builder.setPositiveButton("Next Question", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                questionsIndexCount++;
                loadQuestion();
            }
        });
        builder.create().show();
    }

    public List<Integer> generateUniqueRandomNumbers(int count, int min, int max) {
        if (count > (max - min + 1)) {
            throw new IllegalArgumentException("Count should be less than or equal to the range size.");
        }

        List<Integer> randomNumbers = new ArrayList<>();
        Set<Integer> usedNumbers = new HashSet<>();
        Random random = new Random();

        while (randomNumbers.size() < count) {
            int randomNumber = random.nextInt(max - min + 1) + min;
            if (!usedNumbers.contains(randomNumber)) {
                usedNumbers.add(randomNumber);
                randomNumbers.add(randomNumber);
            }
        }

        return randomNumbers;
    }
}