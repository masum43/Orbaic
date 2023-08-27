package com.orbaic.miner.quiz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.orbaic.miner.R;
import com.orbaic.miner.databinding.ActivityQuizStartBinding;

import java.util.ArrayList;
import java.util.List;

public class QuizStartActivity extends AppCompatActivity {
    private ActivityQuizStartBinding binding;
    private ItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizStartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.rv.setLayoutManager(new LinearLayoutManager(this));

        List<Item> itemList = new ArrayList<>();
        itemList.add(new Item("How many reward for each quiz?", "Each correct answer in the quiz will earn you <b>1 ACI token</b>"));
        itemList.add(new Item("If I do wrong answer what will happen?", "If you answer a question incorrectly, you will bot receive any rewards for that question."));
        itemList.add(new Item("Where will add reward?", "To receive your reword tokens, you must complete all 5 quiz questions and submit your answers. Upon successful completion, your reword will be added to your wallet balance."));
        itemList.add(new Item("If unable to complete then how?", "If you leave the quiz halfway through, you will not receive any reword. You must complete all quiz questions to be eligible for the rewords."));
        itemList.add(new Item("Every day how many time can do?", "Users can participate in the quiz once every 24 hours. After the 24-hour period, a new set of quiz questions will be appear for you to attempt and earn more reword."));


        adapter = new ItemAdapter(itemList);
        binding.rv.setAdapter(adapter);

        binding.btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(QuizStartActivity.this, LearnEarnActivity.class));
            }
        });

    }
}