package com.example.contacts;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.TextView;

public class loading_alertdialog_box {
    private final Activity activity;
    private AlertDialog alertDialog;
    loading_alertdialog_box(Activity mactivity) {
        activity = mactivity;
    }

    void start_dialog_box() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        builder.setView(layoutInflater.inflate(R.layout.loading_dialog, null));
        alertDialog = builder.create();
        alertDialog.show();
    }

    void dismiss() {
        alertDialog.dismiss();
    }
}
