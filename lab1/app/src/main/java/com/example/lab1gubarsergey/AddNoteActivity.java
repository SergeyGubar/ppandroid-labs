package com.example.lab1gubarsergey;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class AddNoteActivity extends AppCompatActivity {

    static Intent makeIntent(Context context) {
        return new Intent(context, AddNoteActivity.class);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
    }
}
