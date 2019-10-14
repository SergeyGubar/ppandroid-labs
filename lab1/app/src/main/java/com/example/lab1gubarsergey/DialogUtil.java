package com.example.lab1gubarsergey;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

interface EditDeleteListener {
    void deleteClicked();
    void editClicked();
}

class DialotUtil {
    AlertDialog showEditDeleteDialog(Context context, @NonNull EditDeleteListener listener) {

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_delete, null, false);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(R.string.choose_option)
                .setView(view)
                .create();
        dialog.show();

        view.findViewById(R.id.dialog_edit_button).setOnClickListener((v) -> {
            listener.editClicked();
            dialog.dismiss();
        });

        view.findViewById(R.id.dialog_delete_button).setOnClickListener((v) -> {
            listener.deleteClicked();
            dialog.dismiss();
        });
        return dialog;
    }
}