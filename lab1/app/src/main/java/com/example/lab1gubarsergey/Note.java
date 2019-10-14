package com.example.lab1gubarsergey;

import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.Objects;

public class Note {

    String name;
    String description;
    Importance importance;
    Date end;
    String image;

    public Note(String name, String description, Importance importance, Date end, String image) {
        this.name = name;
        this.description = description;
        this.importance = importance;
        this.end = end;
        this.image = image;
    }

    @NotNull
    @Override
    public String toString() {
        return "Note{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", importance=" + importance +
                ", end=" + end +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return Objects.equals(name, note.name) &&
                Objects.equals(description, note.description) &&
                importance == note.importance &&
                Objects.equals(end, note.end);
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
