// Generated by view binder compiler. Do not edit!
package com.orbaic.miner.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.orbaic.miner.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ItemViewBinding implements ViewBinding {
  @NonNull
  private final CardView rootView;

  @NonNull
  public final TextView itemViewContent;

  @NonNull
  public final TextView itemViewTitle;

  @NonNull
  public final ImageView ivMedia;

  @NonNull
  public final ImageView ivShare;

  @NonNull
  public final LinearLayout newsHolder2;

  @NonNull
  public final TextView tvDate;

  @NonNull
  public final View view1;

  private ItemViewBinding(@NonNull CardView rootView, @NonNull TextView itemViewContent,
      @NonNull TextView itemViewTitle, @NonNull ImageView ivMedia, @NonNull ImageView ivShare,
      @NonNull LinearLayout newsHolder2, @NonNull TextView tvDate, @NonNull View view1) {
    this.rootView = rootView;
    this.itemViewContent = itemViewContent;
    this.itemViewTitle = itemViewTitle;
    this.ivMedia = ivMedia;
    this.ivShare = ivShare;
    this.newsHolder2 = newsHolder2;
    this.tvDate = tvDate;
    this.view1 = view1;
  }

  @Override
  @NonNull
  public CardView getRoot() {
    return rootView;
  }

  @NonNull
  public static ItemViewBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ItemViewBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.item_view, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ItemViewBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.itemViewContent;
      TextView itemViewContent = ViewBindings.findChildViewById(rootView, id);
      if (itemViewContent == null) {
        break missingId;
      }

      id = R.id.itemViewTitle;
      TextView itemViewTitle = ViewBindings.findChildViewById(rootView, id);
      if (itemViewTitle == null) {
        break missingId;
      }

      id = R.id.ivMedia;
      ImageView ivMedia = ViewBindings.findChildViewById(rootView, id);
      if (ivMedia == null) {
        break missingId;
      }

      id = R.id.ivShare;
      ImageView ivShare = ViewBindings.findChildViewById(rootView, id);
      if (ivShare == null) {
        break missingId;
      }

      id = R.id.newsHolder2;
      LinearLayout newsHolder2 = ViewBindings.findChildViewById(rootView, id);
      if (newsHolder2 == null) {
        break missingId;
      }

      id = R.id.tvDate;
      TextView tvDate = ViewBindings.findChildViewById(rootView, id);
      if (tvDate == null) {
        break missingId;
      }

      id = R.id.view1;
      View view1 = ViewBindings.findChildViewById(rootView, id);
      if (view1 == null) {
        break missingId;
      }

      return new ItemViewBinding((CardView) rootView, itemViewContent, itemViewTitle, ivMedia,
          ivShare, newsHolder2, tvDate, view1);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
