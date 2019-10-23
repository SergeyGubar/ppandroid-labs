package com.example.lab1gubarsergey;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MainActivityJava extends AppCompatActivity {

    private static final String TAG = MainActivityJava.class.getSimpleName();
    private static final int NEW_NOTE_REQUEST_CODE = 787;
    private static final int EDIT_NOTE_REQUEST_CODE = 786;

    private NotesAdapter adapter;
    private RecyclerView recycler;
    private List<Note> notes = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter = new NotesAdapter(notes, this::onNoteClicked);

        recycler = findViewById(R.id.notes_recycler);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        loadDataFromFile();
        handleIntent(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)) {
                    searchNote(null);
                }
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_note:
                startActivityForResult(AddNoteActivity.makeIntent(this), NEW_NOTE_REQUEST_CODE);
                return true;
            case R.id.filter:
                DialogUtil.showFilterDialog(this, this::filterImportance, this::filterDate);
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == NEW_NOTE_REQUEST_CODE || requestCode == EDIT_NOTE_REQUEST_CODE) && resultCode == Activity.RESULT_OK) {
            loadDataFromFile();
        }
    }

    private void loadDataFromFile() {
        List<Note> content = FileUtils.readNotes(this);
        Log.d(TAG, "loadDataFromFile: data " + content);
        if (content != null) {
            this.notes = content;
            adapter.swap(notes);
        } else {
            Toast.makeText(this, R.string.notes_are_empty, Toast.LENGTH_SHORT).show();
        }
    }

    private void onNoteClicked(Note note) {
        DialogUtil.showEditDeleteDialog(this, new EditDeleteListener() {
            @Override
            public void deleteClicked() {
                int index = notes.indexOf(note);
                if (index != -1) {
                    notes.remove(index);
                    FileUtils.writeNotes(MainActivityJava.this, notes);
                    adapter.removeItem(index);
                } else {
                    Log.e(TAG, "deleteClicked: remove failed no such element");
                }
            }

            @Override
            public void editClicked() {
                startActivityForResult(EditNoteActivity.makeIntent(MainActivityJava.this, note.guid), EDIT_NOTE_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            searchNote(query);
        }
    }

    private void filterImportance(ImportanceFilter filter) {
        Predicate<? super Note> predicate = null;
        switch (filter) {
            case ALL:
                predicate = note -> true;
                break;
            case LOW:
                predicate = note -> note.importance == Importance.LOW;
                break;
            case MEDIUM:
                predicate = note -> note.importance == Importance.MEDIUM;
                break;
            case HIGH:
                predicate = note -> note.importance == Importance.HIGH;
                break;
        }
        Objects.requireNonNull(predicate);
        filterNotes(predicate);
    }

    private void filterDate(Date date) {
        Predicate<? super Note> predicate = (note) -> {


            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            Calendar noteCalendar = Calendar.getInstance();
            noteCalendar.setTime(note.end);
            return calendar.get(Calendar.DAY_OF_MONTH) == noteCalendar.get(Calendar.DAY_OF_MONTH)
                    && calendar.get(Calendar.MONTH) == noteCalendar.get(Calendar.MONTH)
                    && calendar.get(Calendar.YEAR) == noteCalendar.get(Calendar.YEAR);
        };
        Objects.requireNonNull(predicate);
        filterNotes(predicate);
    }

    private void filterNotes(Predicate<? super Note> predicate) {
        List<Note> notes = this.notes.stream().filter(predicate).collect(Collectors.toList());
        adapter.swap(notes);
    }

    private void searchNote(String query) {
        if (query == null || query.isEmpty()) {
            adapter.swap(notes);
            return;
        }
        List<Note> notes = this.notes.stream().filter(n -> {
            Pattern pattern = Pattern.compile(query, Pattern.CASE_INSENSITIVE);
            return pattern.matcher(n.description).find();
        }).collect(Collectors.toList());
        adapter.swap(notes);
    }
}
