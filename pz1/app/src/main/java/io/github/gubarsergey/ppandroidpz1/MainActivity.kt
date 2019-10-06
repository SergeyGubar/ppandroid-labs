package io.github.gubarsergey.ppandroidpz1

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar

private data class State(
    val red: Int = 0,
    val green: Int = 0,
    val blue: Int = 0
)

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var redSeekbar: SeekBar
    private lateinit var greenSeekbar: SeekBar
    private lateinit var blueSeekbar: SeekBar
    private lateinit var colorView: View

    private var state = State()
        set(value) {
            Log.d(TAG, "stateChanged: $state")
            val color = convertToHex(value)
            Log.d(TAG, "stateChanged color: $color")
            colorView.setBackgroundColor(Color.parseColor(color))
            field = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        redSeekbar = findViewById(R.id.red_seekbar)
        greenSeekbar = findViewById(R.id.green_seekbar)
        blueSeekbar = findViewById(R.id.blue_seekbar)
        colorView = findViewById(R.id.view_color)
        setupListeners()
    }

    private fun convertToHex(state: State): String {
        return "#${String.format("%02X", state.red)}${String.format("%02X", state.green)}${String.format("%02X", state.blue)}"
    }

    private fun setupListeners() {
        redSeekbar.setOnSeekBarChangeListener(ProgressChangeListener {
            state = state.copy(red = it)
        })
        greenSeekbar.setOnSeekBarChangeListener(ProgressChangeListener {
            state = state.copy(green = it)
        })
        blueSeekbar.setOnSeekBarChangeListener(ProgressChangeListener {
            state = state.copy(blue = it)
        })
    }

    inner class ProgressChangeListener(
        private val onChange: (Int) -> Unit
    ) : SeekBar.OnSeekBarChangeListener {

        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            onChange(progress)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

        override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit

    }

}
