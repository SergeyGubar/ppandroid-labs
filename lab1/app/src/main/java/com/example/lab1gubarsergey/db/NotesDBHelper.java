package com.example.lab1gubarsergey.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.lab1gubarsergey.Note;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class NotesDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DB_NAME = "NOTES_DB";


    public static final String TABLE_NAME = "NOTES";


    public static final String COLUMN_NAME = "NAME";
    public static final String COLUMN_DESCRIPTION = "NOTE_DESC";
    public static final String COLUMN_IMPORTANCE = "IMPORTANCE";
    public static final String COLUMN_DATE = "DATE";
    public static final String COLUMN_IMAGE = "IMAGE";
    public static final String COLUMN_ID = "GUID";

    public NotesDBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME +
                "( " + COLUMN_ID + " TEXT PRIMARY KEY, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_IMPORTANCE + " INTEGER, " +
                COLUMN_DATE + " INTEGER, " +
                COLUMN_IMAGE + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT);");

    }


    public List<Note> readNotes(SQLiteDatabase db) {
        List<Note> notes = new ArrayList<>();
        try (Cursor cursor = db.query(NotesDBHelper.TABLE_NAME,
                null,
                null, null, null, null, null)) {

            while (cursor.moveToNext()) {
                Note note = getNote(cursor);
                notes.add(note);
            }
        }
        return notes;
    }

    public void removeNote(Note note, SQLiteDatabase db) {
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{note.guid});
    }

    public void updateNote(Note note, SQLiteDatabase db) {
        ContentValues cv = NotesMapper.toCv(note);
        db.update(TABLE_NAME, cv, COLUMN_ID + " = ?", new String[]{note.guid});
    }

    public Note getNote(String id, SQLiteDatabase db) {
        Cursor cursor = db.query(TABLE_NAME, null, COLUMN_ID + " = ?", new String[]{id}, null, null, null);
        if (cursor.getCount() != 1) throw new IllegalStateException();
        cursor.moveToFirst();
        return getNote(cursor);
    }

    private Note getNote(Cursor cursor) {
        Importance importance;
        int storedImportance = cursor.getInt(cursor.getColumnIndex(NotesDBHelper.COLUMN_IMPORTANCE));

        switch (storedImportance) {
            case 1:
                importance = Importance.LOW;
                break;
            case 2:
                importance = Importance.MEDIUM;
                break;
            case 3:
                importance = Importance.HIGH;
                break;
            default:
                throw new IllegalStateException("Importance is lost :(");
        }

        Date date;
        long storedTime = cursor.getLong(cursor.getColumnIndex(NotesDBHelper.COLUMN_DATE));
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(storedTime);
        date = calendar.getTime();

        Note note = new Note(
                cursor.getString(cursor.getColumnIndex(NotesDBHelper.COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndex(NotesDBHelper.COLUMN_DESCRIPTION)),
                importance,
                date,
                cursor.getString(cursor.getColumnIndex(NotesDBHelper.COLUMN_IMAGE)),
                cursor.getString(cursor.getColumnIndex(NotesDBHelper.COLUMN_ID))

        );
        return note;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1) {
            // TODO
        }
    }
}
