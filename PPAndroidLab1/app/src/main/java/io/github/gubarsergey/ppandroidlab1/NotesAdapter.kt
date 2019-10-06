package io.github.gubarsergey.ppandroidlab1

import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntegerRes
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Note(
    val name: String,
    val description: String,
    @IntegerRes val icon: Int,
    val creationTime: String,
    val importance: Importance
): Parcelable

enum class Importance {
    LOW,
    MEDIUM,
    HIGH
}

class NotesAdapter(
    val notes: MutableList<Note> = mutableListOf()
) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent))
    }

    override fun getItemCount(): Int = notes.count()

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(notes[position])
    }

    inner class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(item: Note) {

        }
    }
}