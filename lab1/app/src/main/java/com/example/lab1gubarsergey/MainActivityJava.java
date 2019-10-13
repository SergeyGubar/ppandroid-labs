package com.example.lab1gubarsergey;

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
    private NotesAdapter adapter;
    private RecyclerView recycler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter = new NotesAdapter(new ArrayList<Note>());

        List<Note> notes = new ArrayList<>();
        notes.add(new Note("name", "desc", Importance.HIGH, new Date(), new Date()));
        notes.add(new Note("name1", "desc1", Importance.MEDIUM, new Date(), new Date()));
        notes.add(new Note("name2", "desc2", Importance.LOW, new Date(), new Date()));
        adapter.addNotes(notes);
        recycler = findViewById(R.id.notes_recycler);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
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
            startActivity(AddNoteActivity.makeIntent(this));
            return true;
        }
        return false;
    }
}
