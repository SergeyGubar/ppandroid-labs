package com.example.lab1gubarsergey;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtils {

    private static final String TAG = FileUtils.class.getSimpleName();
    private static final String FILE_NAME = "storage.txt";

    static void writeToFile(Context context, String content) {
        FileOutputStream outputStream;
        try {
            outputStream = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            outputStream.write(content.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static String readFromFile(Context context) {
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

    public static String convertStreamToString(InputStream is) throws Exception {
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
