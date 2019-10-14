package com.example.lab1gubarsergey;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Date;

public class AddNoteActivity extends AppCompatActivity {

    static Intent makeIntent(Context context) {
        return new Intent(context, AddNoteActivity.class);
    }

    private EditText nameEditText;
    private EditText descriptionEditText;
    private RadioGroup importanceRadiogroup;
    private static final String TAG = AddNoteActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        nameEditText = findViewById(R.id.add_note_name_edit_text);
        descriptionEditText = findViewById(R.id.add_note_desc_edit_text);
        importanceRadiogroup = findViewById(R.id.add_note_radiogroup);

        setupListeners();
    }

    private void setupListeners() {
        findViewById(R.id.add_note_save_button).setOnClickListener((v) -> {
            if (validateData()) {
                save();
            } else {
                Toast.makeText(this, R.string.invalid_data, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateData() {
        Editable name = nameEditText.getText();
        Editable description = descriptionEditText.getText();
        if (name == null || name.toString().isEmpty()) {
            return false;
        }
        if (description == null || description.toString().isEmpty()) {
            return false;
        }
        if (importanceRadiogroup.getCheckedRadioButtonId() == -1) {
            return false;
        }
        return true;
    }

    private void save() {

        Gson gson = new Gson();
        Note note = new Note("name", "description", Importance.HIGH, new Date(), new Date());
        String json = gson.toJson(note);

        Log.d(TAG, "save: json " + json);

        Note jsonNote = gson.fromJson(json, Note.class);
        Log.d(TAG, "save: from json " + jsonNote);

//        FileUtils.writeToFile(this, descriptionEditText.getText().toString());
//        setResult(AddNoteActivity.RESULT_OK);
//        finish();
    }
}
