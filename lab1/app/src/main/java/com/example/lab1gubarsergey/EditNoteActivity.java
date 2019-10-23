package com.example.lab1gubarsergey;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class EditNoteActivity extends AppCompatActivity {

    private static final String NOTE_KEY = "NOTE_KEY";
    private static final int PICK_IMAGE = 4040;

    static Intent makeIntent(Context context, String id) {
        Intent intent = new Intent(context, EditNoteActivity.class);
        intent.putExtra(NOTE_KEY, id);
        return intent;
    }

    private EditText nameEditText;
    private EditText descriptionEditText;
    private RadioGroup importanceRadiogroup;
    private Button dateButton;
    private Button saveButton;
    private ImageView noteImage;

    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        note = FileUtils.findNote(this, getIntent().getStringExtra(NOTE_KEY));
        Objects.requireNonNull(note);
        findViews();
        initUI();
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
            DatePickerDialog dialog = new DatePickerDialog(EditNoteActivity.this, (view, year, month, dayOfMonth) -> {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                EditNoteActivity.this.note.end = calendar.getTime();
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
        if (note.end == null) {
            return false;
        }
        if (note.image == null) {
            return false;
        }
        return true;
    }

    private void save() {
        this.note.name = nameEditText.getText().toString();
        this.note.description = descriptionEditText.getText().toString();
        Importance importance;
        switch (importanceRadiogroup.getCheckedRadioButtonId()) {
            case R.id.edit_note_low_radiobutton:
                importance = Importance.LOW;
                break;
            case R.id.edit_note_medium_radiobutton:
                importance = Importance.MEDIUM;
                break;
            case R.id.edit_note_high_radiobutton:
                importance = Importance.HIGH;
                break;
            default:
                throw new IllegalStateException("Radiobutton is not checked!");
        }
        this.note.importance = importance;
        FileUtils.updateNote(this, note);
        setResult(Activity.RESULT_OK);
        finish();
    }

    private void findViews() {
        nameEditText = findViewById(R.id.edit_note_name_edit_text);
        descriptionEditText = findViewById(R.id.edit_note_desc_edit_text);
        importanceRadiogroup = findViewById(R.id.edit_note_radiogroup);
        dateButton = findViewById(R.id.edit_note_date_button);
        saveButton = findViewById(R.id.edit_note_save_button);
        noteImage = findViewById(R.id.edit_note_image);
    }

    private void initUI() {
        nameEditText.setText(note.name);
        descriptionEditText.setText(note.description);
        noteImage.setImageBitmap(BitmapUtils.fromBase64(note.image));
        switch (note.importance) {
            case LOW:
                ((RadioButton) findViewById(R.id.edit_note_low_radiobutton)).setChecked(true);
                break;
            case MEDIUM:
                ((RadioButton) findViewById(R.id.edit_note_medium_radiobutton)).setChecked(true);
                break;
            case HIGH:
                ((RadioButton) findViewById(R.id.edit_note_high_radiobutton)).setChecked(true);
                break;
        }
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
                    this.note.image = BitmapUtils.toBase64(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
