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

@FunctionalInterface
interface FilterListener {
    void filter(ImportanceFilter importanceFilter);
}

enum ImportanceFilter {
    ALL,
    LOW,
    MEDIUM,
    HIGH
}

class DialogUtil {
    static AlertDialog showEditDeleteDialog(Context context, @NonNull EditDeleteListener listener) {

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

    static AlertDialog showFilterDialog(Context context, @NonNull FilterListener listener) {

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_filter, null, false);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(R.string.choose_option)
                .setView(view)
                .create();
        dialog.show();

        view.findViewById(R.id.dialog_all_button).setOnClickListener((v) -> {
            listener.filter(ImportanceFilter.ALL);
            dialog.dismiss();
        });

        view.findViewById(R.id.dialog_low_button).setOnClickListener((v) -> {
            listener.filter(ImportanceFilter.LOW);
            dialog.dismiss();
        });

        view.findViewById(R.id.dialog_medium_button).setOnClickListener((v) -> {
            listener.filter(ImportanceFilter.MEDIUM);
            dialog.dismiss();
        });

        view.findViewById(R.id.dialog_high_button).setOnClickListener((v) -> {
            listener.filter(ImportanceFilter.HIGH);
            dialog.dismiss();
        });

        return dialog;
    }

}