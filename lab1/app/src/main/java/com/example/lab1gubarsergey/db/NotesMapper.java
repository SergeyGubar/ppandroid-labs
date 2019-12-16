package com.example.lab1gubarsergey.db;

import android.content.ContentValues;

import com.example.lab1gubarsergey.Note;

public class NotesMapper {

    private static final int IMPORTANCE_LOW = 1;
    private static final int IMPORTANCE_MEDIUM = 2;
    private static final int IMPORTANCE_HIGH = 3;


    public static ContentValues toCv(Note note) {
        ContentValues cv = new ContentValues();

        long time = note.end.getTime();
        cv.put(NotesDBHelper.COLUMN_DATE, time);
        cv.put(NotesDBHelper.COLUMN_DESCRIPTION, note.description);
        cv.put(NotesDBHelper.COLUMN_IMAGE, note.image);


        int importance;
        switch (note.importance) {
            case LOW:
                importance = IMPORTANCE_LOW;
                break;
            case MEDIUM:
                importance = IMPORTANCE_MEDIUM;
                break;
            case HIGH:
                importance = IMPORTANCE_HIGH;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + note.importance);
        }

        cv.put(NotesDBHelper.COLUMN_IMPORTANCE, importance);
        cv.put(NotesDBHelper.COLUMN_NAME, note.name);
        cv.put(NotesDBHelper.COLUMN_ID, note.guid);
        return cv;
    }
}
