package com.example.lab1gubarsergey;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

@FunctionalInterface
interface ClickListener<T> {
    void onClick(T item);
}

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {

    private List<Note> notes = new ArrayList<>();
    private ClickListener<Note> clickListener;


    public NotesAdapter(List<Note> notes, ClickListener<Note> listener) {
        this.notes.addAll(notes);
        this.clickListener = listener;
    }

    public void swap(List<Note> notes) {
        this.notes.clear();
        this.notes.addAll(notes);
        notifyDataSetChanged();
    }

    public void removeItem(int index) {
        this.notes.remove(index);
        this.notifyItemRemoved(index);
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


            Bitmap bitmap = BitmapUtils.fromBase64(note.image);

            int color = 0;

            switch (note.importance) {
                case LOW:
                    color = Color.parseColor("#00ff00");
                    break;
                case MEDIUM:
                    color = Color.parseColor("#ffa500");
                    break;
                case HIGH:
                    color = Color.parseColor("#ff0000");
                    break;
            }

            ((ImageView) itemView.findViewById(R.id.note_image_view)).setImageBitmap(bitmap);
            ((TextView) itemView.findViewById(R.id.note_name_text_view)).setText(note.name);
            ((TextView) itemView.findViewById(R.id.note_description_text_view)).setText(note.description);
            ((TextView) itemView.findViewById(R.id.note_end_text_view)).setText(note.end.toString());
            itemView.findViewById(R.id.note_importance_image_view).setBackgroundColor(color);

        }
    }
}
