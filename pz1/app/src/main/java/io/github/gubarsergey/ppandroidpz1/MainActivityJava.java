package io.github.gubarsergey.ppandroidpz1;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivityJava extends AppCompatActivity {

    public static final String TAG = MainActivityJava.class.getSimpleName();

    private SeekBar redSeekbar;
    private SeekBar greenSeekbar;
    private SeekBar blueSeekbar;
    private View colorView;
    private State state;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");
        redSeekbar = findViewById(R.id.red_seekbar);
        greenSeekbar = findViewById(R.id.green_seekbar);
        blueSeekbar = findViewById(R.id.blue_seekbar);
        colorView = findViewById(R.id.view_color);
        state = new State(0,0,0);
        setupListeners();
    }

    private void setupListeners() {
        ProgressChangeListener redListener = new ProgressChangeListener((progress) -> {
            state = new State(progress, state.green, state.blue);
            onStateChanged();
        });
        ProgressChangeListener greenListener = new ProgressChangeListener((progress) -> {
            state = new State(state.red, progress, state.blue);
            onStateChanged();
        });
        ProgressChangeListener blueListener = new ProgressChangeListener((progress) -> {
            state = new State(state.red, state.green, progress);
            onStateChanged();
        });

        redSeekbar.setOnSeekBarChangeListener(redListener);
        greenSeekbar.setOnSeekBarChangeListener(greenListener);
        blueSeekbar.setOnSeekBarChangeListener(blueListener);
    }

    private void onStateChanged() {
        Log.d(TAG, "stateChanged " + state);
        String color = convertStateToHex();
        Log.d(TAG, "stateChanged color: $color");
        colorView.setBackgroundColor(Color.parseColor(color));
    }

    private String convertStateToHex() {
        String hex = "%02X";
        return "#" + String.format(
                hex,
                state.red
        ) + String.format(
                hex,
                state.green
        ) + String.format(
                hex,
                state.blue
        );
    }

    @FunctionalInterface
    interface OnChangeListener {
        void onProgressChanged(int progress);
    }

    class ProgressChangeListener implements  SeekBar.OnSeekBarChangeListener {

        private OnChangeListener listener;

        public ProgressChangeListener(OnChangeListener listener) {
            this.listener = listener;
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            listener.onProgressChanged(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // Ignore
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // Ignore
        }
    }

    class State {
        private int red;
        private int green;
        private int blue;

        public State(int red, int green, int blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        @NonNull
        @Override
        public String toString() {
            return "Red: " + red + " Green: " + green + " Blue: " + blue;
        }
    }
}
