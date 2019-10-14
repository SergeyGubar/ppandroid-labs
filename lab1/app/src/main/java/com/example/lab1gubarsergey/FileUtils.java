package com.example.lab1gubarsergey;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.stream.IntStream;

public class FileUtils {

    private static final String TAG = FileUtils.class.getSimpleName();
    private static final String FILE_NAME = "storage.json";

    private static void writeToFile(Context context, String content) {
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            outputStream.write(content.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String readFromFile(Context context) {
        String content;
        try {
            FileInputStream file = context.openFileInput(FILE_NAME);
            try {
                content = convertStreamToString(file);
            } catch (Exception ex) {
                Log.e(TAG, "Something went wrong " + ex.toString());
                return null;
            }

        } catch (FileNotFoundException ex) {
            Log.e(TAG, "File was not found");
            return null;
        }
        return content;
    }

    static List<Note> readNotes(Context context) {
        String content = readFromFile(context);
        if (content == null) {
            Log.e(TAG, "readNotes: error - read from file return null");
            return null;
        }
        Gson gson = new Gson();
        return gson.fromJson(content, new TypeToken<List<Note>>(){}.getType());
    }

    static void appendNotes(Context context, List<Note> newNotes) {
        List<Note> notes = readNotes(context);
        if (notes == null) {
            notes = new ArrayList<>();
        }
        notes.addAll(newNotes);
        Gson gson = new Gson();
        writeToFile(context, gson.toJson(notes));
    }

    static void writeNotes(Context context, List<Note> newNotes) {
        Gson gson = new Gson();
        writeToFile(context, gson.toJson(newNotes));
    }

    static void appendNote(Context context, Note newNote) {
        List<Note> notes = readNotes(context);
        if (notes == null) {
            notes = new ArrayList<>();
        }
        notes.add(newNote);
        Gson gson = new Gson();
        writeToFile(context, gson.toJson(notes));
    }

    static Note findNote(Context context, String id) {
        List<Note> notes = readNotes(context);
        Objects.requireNonNull(notes);
        OptionalInt index = IntStream.range(0, notes.size())
                .filter(i -> notes.get(i).guid.equals(id))
                .findFirst();
        if (index.isPresent()) {
            return notes.get(index.getAsInt());
        } else {
            Log.e(TAG, "findNote: error, note not found");
            return null;
        }
    }

    static void updateNote(Context context, Note note) {
        List<Note> notes = readNotes(context);
        Objects.requireNonNull(notes);
        int index = notes.indexOf(note);
        if (index == -1) throw new IllegalStateException("Note not found");
        notes.set(index, note);
        writeNotes(context, notes);
    }

    private static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }
}
