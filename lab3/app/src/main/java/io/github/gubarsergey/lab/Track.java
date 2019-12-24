package io.github.gubarsergey.lab;


import android.media.MediaMetadataRetriever;
import android.text.TextUtils;

public class Track {
    public String name;
    public String filepath;
    public String author;
    public int totalTime;

    public Track(String filepath, String name, String author, int totalTime) {
        if (TextUtils.isEmpty(name)) {
            name = "Unknown";
        }
        if (TextUtils.isEmpty(author)) {
            author = "Unknown";
        }
        this.filepath = filepath;
        this.name = name;
        this.author = author;
        this.totalTime = totalTime;
    }

    public static Track fromFile(String filepath) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(filepath);

        String name = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        String author = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

        return new Track(filepath, name, author, Integer.parseInt(duration));
    }

    @Override
    public String toString() {
        return "Track \"" + this.name + "\", by " + this.author
                + "\n\tDuration: " + this.totalTime / 1000 + "ms.";
    }
}
