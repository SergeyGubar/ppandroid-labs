package io.github.gubarsergey.lab;


import android.media.MediaMetadataRetriever;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class Track implements Parcelable {
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

    protected Track(Parcel in) {
        name = in.readString();
        filepath = in.readString();
        author = in.readString();
        totalTime = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(filepath);
        dest.writeString(author);
        dest.writeInt(totalTime);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Track> CREATOR = new Parcelable.Creator<Track>() {
        @Override
        public Track createFromParcel(Parcel in) {
            return new Track(in);
        }

        @Override
        public Track[] newArray(int size) {
            return new Track[size];
        }
    };
}