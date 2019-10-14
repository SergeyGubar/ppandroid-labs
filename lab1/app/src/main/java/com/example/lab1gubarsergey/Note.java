package com.example.lab1gubarsergey;

import android.os.Parcel;
import android.os.Parcelable;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class Note implements Parcelable {

    String name;
    String description;
    Importance importance;
    Date end;
    String image;
    String guid = UUID.randomUUID().toString();

    public Note(String name, String description, Importance importance, Date end, String image) {
        this.name = name;
        this.description = description;
        this.importance = importance;
        this.end = end;
        this.image = image;
    }

    protected Note(Parcel in) {
        name = in.readString();
        description = in.readString();
        image = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(image);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Note> CREATOR = new Creator<Note>() {
        @Override
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        @Override
        public Note[] newArray(int size) {
            return new Note[size];
        }
    };

    @NotNull
    @Override
    public String toString() {
        return "Note{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", importance=" + importance +
                ", end=" + end +
                ", guid='" + guid + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return note.guid.equals(guid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, importance, end);
    }
}

enum Importance {
    LOW,
    MEDIUM,
    HIGH
}
