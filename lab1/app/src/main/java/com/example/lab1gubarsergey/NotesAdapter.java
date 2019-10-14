package com.example.lab1gubarsergey;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

@FunctionalInterface
interface ClickListener<T> {
    void onClick(T item);
}

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private List<Note> notes;
    private ClickListener<Note> clickListener;


    public NotesAdapter(List<Note> notes, ClickListener<Note> listener) {
        this.notes = notes;
        this.clickListener = listener;
    }

    public void addNotes(List<Note> notes) {
        this.notes.addAll(notes);
        notifyDataSetChanged();
    }

    public void swap(List<Note> notes) {
        this.notes.clear();
        this.notes.addAll(notes);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.bind(notes.get(position));
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        void bind(Note note) {
            itemView.setOnClickListener((v) -> {
                if (clickListener != null) {
                    clickListener.onClick(note);
                }
            });
            ((TextView)itemView.findViewById(R.id.note_name_text_view)).setText(note.name);
            ((TextView)itemView.findViewById(R.id.note_description_text_view)).setText(note.description);
            ((TextView)itemView.findViewById(R.id.note_importance_text_view)).setText(note.importance.toString());
            ((TextView)itemView.findViewById(R.id.note_start_text_view)).setText(note.end.toString());
            ((TextView)itemView.findViewById(R.id.note_end_text_view)).setText(note.start.toString());

        }
    }
}
