package com.example.lab1gubarsergey;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class Note {

    String name;
    String description;
    Importance importance;
    Date start;
    Date end;

    public Note(String name, String description, Importance importance, Date start, Date end) {
        this.name = name;
        this.description = description;
        this.importance = importance;
        this.start = start;
        this.end = end;
    }

    @NotNull
    @Override
    public String toString() {
        return "Note{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", importance=" + importance +
                ", start=" + start +
                ", end=" + end +
                '}';
    }
}

enum Importance {
    LOW,
    MEDIUM,
    HIGH
}
