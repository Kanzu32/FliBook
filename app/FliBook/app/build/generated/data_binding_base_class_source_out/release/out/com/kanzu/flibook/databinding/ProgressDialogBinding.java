// Generated by view binder compiler. Do not edit!
package com.kanzu.flibook.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.kanzu.flibook.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ProgressDialogBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final TextView labelLoading;

  @NonNull
  public final LinearLayout layoutLoading;

  @NonNull
  public final ProgressBar loading;

  private ProgressDialogBinding(@NonNull LinearLayout rootView, @NonNull TextView labelLoading,
      @NonNull LinearLayout layoutLoading, @NonNull ProgressBar loading) {
    this.rootView = rootView;
    this.labelLoading = labelLoading;
    this.layoutLoading = layoutLoading;
    this.loading = loading;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ProgressDialogBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ProgressDialogBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.progress_dialog, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ProgressDialogBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.label_loading;
      TextView labelLoading = ViewBindings.findChildViewById(rootView, id);
      if (labelLoading == null) {
        break missingId;
      }

      LinearLayout layoutLoading = (LinearLayout) rootView;

      id = R.id.loading;
      ProgressBar loading = ViewBindings.findChildViewById(rootView, id);
      if (loading == null) {
        break missingId;
      }

      return new ProgressDialogBinding((LinearLayout) rootView, labelLoading, layoutLoading,
          loading);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
