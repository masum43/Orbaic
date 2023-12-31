// Generated by view binder compiler. Do not edit!
package com.orbaic.miner.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.orbaic.miner.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ItemviewForRefBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final TextView activeStatus;

  @NonNull
  public final TextView textView7;

  @NonNull
  public final TextView userIdInRef;

  private ItemviewForRefBinding(@NonNull ConstraintLayout rootView, @NonNull TextView activeStatus,
      @NonNull TextView textView7, @NonNull TextView userIdInRef) {
    this.rootView = rootView;
    this.activeStatus = activeStatus;
    this.textView7 = textView7;
    this.userIdInRef = userIdInRef;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ItemviewForRefBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ItemviewForRefBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.itemview_for_ref, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ItemviewForRefBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.active_status;
      TextView activeStatus = ViewBindings.findChildViewById(rootView, id);
      if (activeStatus == null) {
        break missingId;
      }

      id = R.id.textView7;
      TextView textView7 = ViewBindings.findChildViewById(rootView, id);
      if (textView7 == null) {
        break missingId;
      }

      id = R.id.user_id_in_ref;
      TextView userIdInRef = ViewBindings.findChildViewById(rootView, id);
      if (userIdInRef == null) {
        break missingId;
      }

      return new ItemviewForRefBinding((ConstraintLayout) rootView, activeStatus, textView7,
          userIdInRef);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
