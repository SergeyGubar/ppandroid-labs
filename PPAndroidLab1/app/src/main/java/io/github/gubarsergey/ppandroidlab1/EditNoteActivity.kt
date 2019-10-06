package io.github.gubarsergey.ppandroidlab1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class EditNoteActivity : AppCompatActivity() {

    companion object {

        private const val EDIT_NOTE_KEY = "EDIT_NOTE_KEY"

        fun newIntent(note: Note): Intent {
            return Intent().apply {
                putExtra(EDIT_NOTE_KEY, note)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)
    }
}
