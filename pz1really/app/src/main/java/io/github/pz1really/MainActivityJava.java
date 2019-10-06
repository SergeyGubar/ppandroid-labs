package io.github.pz1really;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivityJava extends AppCompatActivity {

    private static final String TAG = MainActivityJava.class.getSimpleName();
    private static final String COUNTER_KEY = TAG + "COUNTER_KEY";

    private TextView counterTextView;

    private int counter = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button counterButton = findViewById(R.id.counter_button);
        counterTextView = findViewById(R.id.counter_text_view);

        if (savedInstanceState != null) {
            restoreState(savedInstanceState);
        }

        counterButton.setOnClickListener((v) -> {
            counter++;
            onCounterIncremented();
        });
    }

    private void restoreState(Bundle savedInstanceState) {
        counterTextView.setText(String.valueOf(savedInstanceState.getInt(COUNTER_KEY)));
    }

    private void onCounterIncremented() {
        counterTextView.setText(String.valueOf(counter));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(COUNTER_KEY, counter);
    }
}
