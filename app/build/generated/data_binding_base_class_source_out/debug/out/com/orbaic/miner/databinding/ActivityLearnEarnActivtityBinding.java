// Generated by view binder compiler. Do not edit!
package com.orbaic.miner.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.orbaic.miner.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityLearnEarnActivtityBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final RadioButton ans1;

  @NonNull
  public final RadioButton ans2;

  @NonNull
  public final RadioButton ans3;

  @NonNull
  public final RadioButton ans4;

  @NonNull
  public final RadioGroup ansGroup;

  @NonNull
  public final ImageView backButton;

  @NonNull
  public final LinearLayout learnAndEarnHeader;

  @NonNull
  public final Button learnAnsSubmit;

  @NonNull
  public final TextView learnQuestions;

  @NonNull
  public final ProgressBar progressBar;

  private ActivityLearnEarnActivtityBinding(@NonNull RelativeLayout rootView,
      @NonNull RadioButton ans1, @NonNull RadioButton ans2, @NonNull RadioButton ans3,
      @NonNull RadioButton ans4, @NonNull RadioGroup ansGroup, @NonNull ImageView backButton,
      @NonNull LinearLayout learnAndEarnHeader, @NonNull Button learnAnsSubmit,
      @NonNull TextView learnQuestions, @NonNull ProgressBar progressBar) {
    this.rootView = rootView;
    this.ans1 = ans1;
    this.ans2 = ans2;
    this.ans3 = ans3;
    this.ans4 = ans4;
    this.ansGroup = ansGroup;
    this.backButton = backButton;
    this.learnAndEarnHeader = learnAndEarnHeader;
    this.learnAnsSubmit = learnAnsSubmit;
    this.learnQuestions = learnQuestions;
    this.progressBar = progressBar;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityLearnEarnActivtityBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityLearnEarnActivtityBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_learn_earn_activtity, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityLearnEarnActivtityBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.ans1;
      RadioButton ans1 = ViewBindings.findChildViewById(rootView, id);
      if (ans1 == null) {
        break missingId;
      }

      id = R.id.ans2;
      RadioButton ans2 = ViewBindings.findChildViewById(rootView, id);
      if (ans2 == null) {
        break missingId;
      }

      id = R.id.ans3;
      RadioButton ans3 = ViewBindings.findChildViewById(rootView, id);
      if (ans3 == null) {
        break missingId;
      }

      id = R.id.ans4;
      RadioButton ans4 = ViewBindings.findChildViewById(rootView, id);
      if (ans4 == null) {
        break missingId;
      }

      id = R.id.ansGroup;
      RadioGroup ansGroup = ViewBindings.findChildViewById(rootView, id);
      if (ansGroup == null) {
        break missingId;
      }

      id = R.id.backButton;
      ImageView backButton = ViewBindings.findChildViewById(rootView, id);
      if (backButton == null) {
        break missingId;
      }

      id = R.id.learnAndEarnHeader;
      LinearLayout learnAndEarnHeader = ViewBindings.findChildViewById(rootView, id);
      if (learnAndEarnHeader == null) {
        break missingId;
      }

      id = R.id.learn_ans_submit;
      Button learnAnsSubmit = ViewBindings.findChildViewById(rootView, id);
      if (learnAnsSubmit == null) {
        break missingId;
      }

      id = R.id.learn_questions;
      TextView learnQuestions = ViewBindings.findChildViewById(rootView, id);
      if (learnQuestions == null) {
        break missingId;
      }

      id = R.id.progress_bar;
      ProgressBar progressBar = ViewBindings.findChildViewById(rootView, id);
      if (progressBar == null) {
        break missingId;
      }

      return new ActivityLearnEarnActivtityBinding((RelativeLayout) rootView, ans1, ans2, ans3,
          ans4, ansGroup, backButton, learnAndEarnHeader, learnAnsSubmit, learnQuestions,
          progressBar);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
