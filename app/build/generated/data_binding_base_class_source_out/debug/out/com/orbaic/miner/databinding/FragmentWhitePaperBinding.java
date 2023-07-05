// Generated by view binder compiler. Do not edit!
package com.orbaic.miner.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.orbaic.miner.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentWhitePaperBinding implements ViewBinding {
  @NonNull
  private final FrameLayout rootView;

  @NonNull
  public final WebView whitePaper;

  private FragmentWhitePaperBinding(@NonNull FrameLayout rootView, @NonNull WebView whitePaper) {
    this.rootView = rootView;
    this.whitePaper = whitePaper;
  }

  @Override
  @NonNull
  public FrameLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentWhitePaperBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentWhitePaperBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_white_paper, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentWhitePaperBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.white_paper;
      WebView whitePaper = ViewBindings.findChildViewById(rootView, id);
      if (whitePaper == null) {
        break missingId;
      }

      return new FragmentWhitePaperBinding((FrameLayout) rootView, whitePaper);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
