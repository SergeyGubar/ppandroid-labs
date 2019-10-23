package com.example.lab1gubarsergey;


import android.app.DatePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import java.util.Calendar;
import java.util.Date;

interface EditDeleteListener {
    void deleteClicked();

    void editClicked();
}

@FunctionalInterface
interface ImportanceFilterListener {
    void filter(ImportanceFilter importanceFilter);
}

@FunctionalInterface
interface DateFilterListener {
    void filter(Date date);
}

@FunctionalInterface
interface DateSelectListener {
    void onDateSelected(Date date);
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

    static AlertDialog showFilterDialog(@NonNull Context context,
                                        @NonNull ImportanceFilterListener importanceListener,
                                        @NonNull DateFilterListener dateListener) {

        View view = LayoutInflater.from(context).inflate(R.layout.dialog_filter, null, false);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(R.string.choose_option)
                .setView(view)
                .create();
        dialog.show();

        view.findViewById(R.id.dialog_all_button).setOnClickListener((v) -> {
            importanceListener.filter(ImportanceFilter.ALL);
            dialog.dismiss();
        });

        view.findViewById(R.id.dialog_low_button).setOnClickListener((v) -> {
            importanceListener.filter(ImportanceFilter.LOW);
            dialog.dismiss();
        });

        view.findViewById(R.id.dialog_medium_button).setOnClickListener((v) -> {
            importanceListener.filter(ImportanceFilter.MEDIUM);
            dialog.dismiss();
        });

        view.findViewById(R.id.dialog_high_button).setOnClickListener((v) -> {
            importanceListener.filter(ImportanceFilter.HIGH);
            dialog.dismiss();
        });

        view.findViewById(R.id.dialog_date_button).setOnClickListener(v -> {
            showDatePicker(context, dateListener::filter);
            dialog.dismiss();
        });

        return dialog;
    }


    static DatePickerDialog showDatePicker(@NonNull Context context, @NonNull DateSelectListener listener) {
        Calendar date = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            listener.onDateSelected(calendar.getTime());
        }, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
        dialog.show();
        return dialog;
    }

}