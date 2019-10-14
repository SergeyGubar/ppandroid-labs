package com.example.lab1gubarsergey;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class AddNoteActivity extends AppCompatActivity {

    static Intent makeIntent(Context context) {
        return new Intent(context, AddNoteActivity.class);
    }

    private static final int PICK_IMAGE = 460;

    private EditText nameEditText;
    private EditText descriptionEditText;
    private RadioGroup importanceRadiogroup;
    private Button dateButton;
    private Button saveButton;
    private ImageView noteImage;
    private Date date;
    private String image;
    private static final String TAG = AddNoteActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        nameEditText = findViewById(R.id.add_note_name_edit_text);
        descriptionEditText = findViewById(R.id.add_note_desc_edit_text);
        importanceRadiogroup = findViewById(R.id.add_note_radiogroup);
        dateButton = findViewById(R.id.add_note_date_button);
        noteImage = findViewById(R.id.add_note_image);
        saveButton = findViewById(R.id.add_note_save_button);
        setupListeners();
    }

    private void setupListeners() {
        noteImage.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
        });
        saveButton.setOnClickListener((v) -> {
            if (validateData()) {
                save();
            } else {
                Toast.makeText(this, R.string.invalid_data, Toast.LENGTH_SHORT).show();
            }
        });
        dateButton.setOnClickListener((v) -> {
            Calendar date = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(AddNoteActivity.this, (view, year, month, dayOfMonth) -> {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                AddNoteActivity.this.date = calendar.getTime();
            }, date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH));
            dialog.show();
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
        if (date == null) {
            return false;
        }
        if (image == null) {
            return false;
        }
        return true;
    }

    private void save() {
        int checkedRadiobuttonId = importanceRadiogroup.getCheckedRadioButtonId();
        Importance importance = null;
        switch (checkedRadiobuttonId) {
            case R.id.add_note_low_radiobutton:
                importance = Importance.LOW;
                break;
            case R.id.add_note_medium_radiobutton:
                importance = Importance.MEDIUM;
                break;
            case R.id.add_note_high_radiobutton:
                importance = Importance.HIGH;
                break;
        }
        Note note = new Note(nameEditText.getText().toString(), descriptionEditText.getText().toString(), importance, date, image);
        Log.d(TAG, "save: note = " + note);
        FileUtils.appendNote(this, note);
        setResult(AddNoteActivity.RESULT_OK);
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri selectedImage = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    noteImage.setImageBitmap(bitmap);
                    this.image = BitmapUtils.toBase64(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
