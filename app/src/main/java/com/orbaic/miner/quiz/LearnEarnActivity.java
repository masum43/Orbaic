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
import com.orbaic.miner.AdMobAds;
import com.orbaic.miner.FirebaseData;
import com.orbaic.miner.MainActivity2;
import com.orbaic.miner.R;
import com.orbaic.miner.common.Constants;
import com.orbaic.miner.common.GetNetTime;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class LearnEarnActivity extends AppCompatActivity {

    int questionsIndexCount = 0, p = 0, countTime = 60000;
    int correctAnsCounter = 0;

    double userPoint;
    int wrongAnsCounter = 0;
    private TextView question;
    private CountDownTimer count;

    private Button submit;
    private ProgressBar progressBar;
    FirebaseData data = new FirebaseData();
    private RadioGroup radioGroup;

    private GetNetTime netTime = new GetNetTime();

    private long timeStamp;
    private RadioButton ans1, ans2, ans3, ans4;
    private String answer, selectedAnswer;
    ArrayList<Integer> randomNumbers = new ArrayList<>();
    TextView tvQsCounter;
    int qzCountInt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_earn_activtity);

        AdmobDataChange dataChange;

        SpManager.init(this);
        tvQsCounter = findViewById(R.id.tvQsCounter);
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
        timeStamp = netTime.getNetTime(this);
        submit.setVisibility(View.INVISIBLE);

        if(!netTime.isError()){
            timeStamp = netTime.getNetTime(this);
            System.out.println("net time: " + timeStamp);
        } else {
            timeStamp = System.currentTimeMillis();
            System.out.println("system time: " + timeStamp);
        }
        dataChange = new ViewModelProvider(this).get(AdmobDataChange.class);
        dataChange.needData(this, activity);



        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
               dataChange.loadAds();
            }
        });




        dataChange.getAdmobStatus().observe(this, admobData -> {
            //Toast.makeText(this, "work", Toast.LENGTH_SHORT).show();
            if (admobData.equals("on")){
                submit.setVisibility(View.VISIBLE);
            }else {
                submit.setVisibility(View.INVISIBLE);
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

            dataChange.showAds();
            if (answer.equals(selectedAnswer)){
                correctAnsCounter++;
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("point", String.valueOf(userPoint+1));
                dataMap.put("qz_count", String.valueOf(qzCountInt+1));
                data.updateDataWithUid(dataMap, "users");
                String text = "Congratulation!! Your answer is correct";
                String title = "Correct Answer";
                int total = correctAnsCounter + wrongAnsCounter;
                if (total < 5){
                    userResultShow(text, title);
                }else {
                    questionDone(correctAnsCounter, wrongAnsCounter);
                }
            }else {
                wrongAnsCounter++;
                String text = "Ohh, Sorry!! Your answer is wrong, Try next...";
                String title = "Wrong Answer";
                int total = correctAnsCounter + wrongAnsCounter;
                if (total < 5){
                    userResultShow(text, title);
                }else {
                    questionDone(correctAnsCounter, wrongAnsCounter);
                }
            }






            radioGroup.clearCheck();
            count.cancel();
            progressBar.setProgress(p = 0);
            System.gc();
        });

        progressBar.setProgress(p);

        //countdown();

        loadQuestion();

        readData();
    }

    private int getRandomNumbers() {
        Random random = new Random();
        return random.nextInt(381);
    }


    private void loadQuestion() {

        int randomNumber = 0;

        while (true){
            randomNumber = getRandomNumbers();
            if (randomNumbers.isEmpty()){
                randomNumbers.add(randomNumber);
                break;
            }else {
                if (!randomNumbers.contains(randomNumber)){
                    randomNumbers.add(randomNumber);
                    break;
                }
            }

        }
        int t = correctAnsCounter + wrongAnsCounter;
        if (t >= 1) {
            long enableTime = timeStamp + 43200000;
            data.anyPath(String.valueOf(enableTime),"extra1");
        }
        DatabaseReference questionRef = FirebaseDatabase.getInstance()
                .getReference("question")
                .child("questions")
                .child(String.valueOf(randomNumber));

        questionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Object> data = new HashMap<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    data.put(dataSnapshot.getKey(), dataSnapshot.getValue());
                }
                ans1.setText(String.valueOf(data.get("ans1")));
                ans2.setText(String.valueOf(data.get("ans2")));
                ans3.setText(String.valueOf(data.get("ans3")));
                ans4.setText(String.valueOf(data.get("ans4")));
                answer = String.valueOf(data.get("correctAnswer"));
                Log.d("Answer of question", "onDataChange: "+answer);
                question.setText(String.valueOf(data.get("question")));
                questionsIndexCount = questionsIndexCount+1;
                tvQsCounter.setText("Question No "+(questionsIndexCount) +" out of 5");
                countdown();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error.getDetails());
            }
        });
    }

    private void questionDone(int correctAnsCounter, int wrongAnsCounter) {
        SpManager.saveString(SpManager.KEY_MCQ_STATE, Constants.STATE_NOT_STARTED);

        Dialog dialog = new Dialog(LearnEarnActivity.this);
        dialog.setContentView(R.layout.mcq_result_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView tvCorrectAns = dialog.findViewById(R.id.tvCorrectAns);
        TextView tvWrongAns = dialog.findViewById(R.id.tvWrongAns);
        tvCorrectAns.setText("Correct answer: "+correctAnsCounter);
        tvWrongAns.setText("Wrong Answer:  "+wrongAnsCounter);
        /*SpManager.saveInt(SpManager.KEY_CORRECT_ANS, 0);
        SpManager.saveInt(SpManager.KEY_WRONG_ANS, 0);*/
        dialog.findViewById(R.id.okButton).setOnClickListener(view -> {
            finish();
        });
        dialog.show();

        long quizFinishTime = System.currentTimeMillis();
        SpManager.saveLong(SpManager.KEY_LAST_QUIZ_FINISH_TIME, quizFinishTime);
    }

    @Override
    protected void onStop() {
        super.onStop();
        count.cancel();
        //finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    /*private void question(int a) {
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
                Log.e("QUIZ_ERROR", "error: "+ error );

            }
        });
    }*/

    public void readData() {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference myRef = firebaseDatabase.getReference("users").child(Objects.requireNonNull(mAuth.getUid()));
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String point = Objects.requireNonNull(snapshot.child("point").getValue()).toString();
                userPoint = Double.parseDouble(point);
                String qzCount = "0";
                if (snapshot.hasChild("qz_count")) {
                    qzCount = Objects.requireNonNull(snapshot.child("qz_count").getValue()).toString();
                    qzCountInt = Integer.parseInt(qzCount);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("QUIZ_ERROR", "error: "+ error );

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
                loadQuestion();
            }
        });
        builder.create().show();
    }

}