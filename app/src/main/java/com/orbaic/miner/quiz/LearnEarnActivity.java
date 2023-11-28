package com.orbaic.miner.quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orbaic.miner.AdMobAds;
import com.orbaic.miner.FirebaseData;
import com.orbaic.miner.MainActivity2;
import com.orbaic.miner.R;
import com.orbaic.miner.common.Constants;
import com.orbaic.miner.common.SpManager;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class LearnEarnActivity extends AppCompatActivity {

    private LearnEarnViewModel learnEarnViewModel;
    int questionsIndexCount = 0, p = 0, countTime = 100000;
    int correctAnsCounter = 0;
    int wrongAnsCounter = 0;
    private TextView question;
    private CountDownTimer count;

    private Button submit;
    private ProgressBar progressBar;
    FirebaseData data = new FirebaseData();
    private RadioGroup radioGroup;
    private RadioButton ans1, ans2, ans3, ans4;
    private String answer, selectedAnswer;
    List<Integer> randomNumbers;
    TextView tvQsCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_earn_activtity);

        SpManager.init(this);
        learnEarnViewModel = new ViewModelProvider(this).get(LearnEarnViewModel.class);
        tvQsCounter = findViewById(R.id.tvQsCounter);
        ans1 = findViewById(R.id.ans1);
        ans2 = findViewById(R.id.ans2);
        ans3 = findViewById(R.id.ans3);
        ans4 = findViewById(R.id.ans4);
        radioGroup = findViewById(R.id.ansGroup);
        progressBar = findViewById(R.id.progress_bar);
        question = findViewById(R.id.learn_questions);
        submit = findViewById(R.id.learn_ans_submit);

        correctAnsCounter = SpManager.getInt(SpManager.KEY_CORRECT_ANS, 0);
        wrongAnsCounter = SpManager.getInt(SpManager.KEY_WRONG_ANS, 0);

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

            learnEarnViewModel.updateQzCount(learnEarnViewModel.getQzCount() + 1);
            data.addQuizCount(String.valueOf(learnEarnViewModel.getQzCount()));

            if (answer.equals(selectedAnswer)){
                correctAnsCounter++;
                SpManager.saveInt(SpManager.KEY_CORRECT_ANS, correctAnsCounter);
                userResultShow("Congratulation! \nYou give the right answer", "Correct Answer");
                learnEarnViewModel.updateUserPoints(learnEarnViewModel.getUserPoints() + 1);
                data.addQuizPoints(String.valueOf(learnEarnViewModel.getUserPoints()));
                mobAds.showRewardedVideo();
            } else {
                wrongAnsCounter++;
                SpManager.saveInt(SpManager.KEY_WRONG_ANS, wrongAnsCounter);
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
        int max = 93;

        String prevState = SpManager.getString(SpManager.KEY_MCQ_STATE, Constants.STATE_NOT_STARTED);
        if (prevState.equals(Constants.STATE_NOT_STARTED)) {
            SpManager.saveString(SpManager.KEY_MCQ_STATE, Constants.STATE_STARTED);
            randomNumbers = generateUniqueRandomNumbers(numberOfRandomNumbers, min, max);
            SpManager.saveString(SpManager.KEY_MCQ_RANDOM_NUMBERS, new Gson().toJson(randomNumbers));
            question(randomNumbers.get(questionsIndexCount));
        }
        else {
            String json = SpManager.getString(SpManager.KEY_MCQ_RANDOM_NUMBERS, null);
            if (json != null) {
                Type type = new TypeToken<List<Integer>>() {}.getType();
                randomNumbers = new Gson().fromJson(json, type);
                questionsIndexCount = SpManager.getInt(SpManager.KEY_LAST_QS_INDEX, 0);
                question(randomNumbers.get(questionsIndexCount));
            }
        }

        progressBar.setProgress(p);

        //countdown();

        readData();
    }

    private void loadQuestion() {
/*        if (questionsIndexCount == 1) {
            long time = System.currentTimeMillis();
            long enableTime = time + 43200000;
            data.anyPath(String.valueOf(enableTime),"extra1");
        }*/

        if(questionsIndexCount < 5) {
            question(randomNumbers.get(questionsIndexCount));
        } else {
            long time = System.currentTimeMillis();
            long enableTime = time + 43200000;
            data.anyPath(String.valueOf(enableTime),"extra1");

            SpManager.saveString(SpManager.KEY_MCQ_STATE, Constants.STATE_NOT_STARTED);

            Dialog dialog = new Dialog(LearnEarnActivity.this);
            dialog.setContentView(R.layout.mcq_result_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            TextView tvCorrectAns = dialog.findViewById(R.id.tvCorrectAns);
            TextView tvWrongAns = dialog.findViewById(R.id.tvWrongAns);
            tvCorrectAns.setText("Correct answer: "+SpManager.getInt(SpManager.KEY_CORRECT_ANS, 0));
            tvWrongAns.setText("Wrong Answer:  "+SpManager.getInt(SpManager.KEY_WRONG_ANS, 0));
            SpManager.saveInt(SpManager.KEY_CORRECT_ANS, 0);
            SpManager.saveInt(SpManager.KEY_WRONG_ANS, 0);
            dialog.findViewById(R.id.okButton).setOnClickListener(view -> {
                Intent intent = new Intent(LearnEarnActivity.this, MainActivity2.class);
                startActivity(intent);
                finish();
            });
            dialog.show();

            long quizFinishTime = System.currentTimeMillis();
            SpManager.saveLong(SpManager.KEY_LAST_QUIZ_FINISH_TIME, quizFinishTime);
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

    private void question(int a) {
        SpManager.saveInt(SpManager.KEY_LAST_QS_INDEX, questionsIndexCount);
        Log.e("QUIZ_ERROR", "a: "+ a );
        Log.e("QUIZ_ERROR", "KEY_LAST_QS_INDEX: "+ questionsIndexCount );
        tvQsCounter.setText("Question No "+(questionsIndexCount+1) +" out of 5");
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("question").child("questions").child(String.valueOf(a));
        reference.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //System.out.println(snapshot);
                Log.e("QUIZ_ERROR", "snapshot: "+ snapshot );
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

    public void readData() {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference myRef = firebaseDatabase.getReference("users").child(Objects.requireNonNull(mAuth.getUid()));
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String point = Objects.requireNonNull(snapshot.child("point").getValue()).toString();
                String qzCount = "0";
                if (snapshot.hasChild("qz_count")) {
                    qzCount = Objects.requireNonNull(snapshot.child("qz_count").getValue()).toString();
                }
                learnEarnViewModel.updateUserPoints(Double.parseDouble(point));
                learnEarnViewModel.updateQzCount(Integer.parseInt(qzCount));
                System.out.println(learnEarnViewModel.getUserPoints());

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