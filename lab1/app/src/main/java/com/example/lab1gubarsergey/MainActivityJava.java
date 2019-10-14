package com.example.lab1gubarsergey;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivityJava extends AppCompatActivity {

    private static final String TAG = MainActivityJava.class.getSimpleName();
    private static final int NEW_NOTE_REQUEST_CODE = 787;
    private static final int EDIT_NOTE_REQUEST_CODE = 786;

    private NotesAdapter adapter;
    private RecyclerView recycler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter = new NotesAdapter(new ArrayList<>());

//        List<Note> notes = new ArrayList<>();
//        notes.add(new Note("name", "desc", Importance.HIGH, new Date(), new Date()));
//        notes.add(new Note("name1", "desc1", Importance.MEDIUM, new Date(), new Date()));
//        notes.add(new Note("name2", "desc2", Importance.LOW, new Date(), new Date()));
//        adapter.addNotes(notes);


        recycler = findViewById(R.id.notes_recycler);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        loadDataFromFile();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.add_note) {
            startActivityForResult(AddNoteActivity.makeIntent(this), NEW_NOTE_REQUEST_CODE);
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_NOTE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            loadDataFromFile();
        }
    }

    private void loadDataFromFile() {
        String content = FileUtils.readFromFile(this);
        Log.d(TAG, "loadDataFromFile: data " + content);
        if (content != null) {
            List<Note> newNotes = new ArrayList<>();
            newNotes.add(new Note("Name", "name", Importance.HIGH, new Date(), new Date()));
            adapter.addNotes(newNotes);
        }
    }
}
