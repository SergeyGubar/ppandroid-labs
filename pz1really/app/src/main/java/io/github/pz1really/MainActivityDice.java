package io.github.pz1really;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivityDice extends AppCompatActivity {

    private TextView diceTextView;
    private EditText maxNumberEditText;
    private int currentRandom = 0;

    private static final String KEY = "KEY";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_dice);
        Button rollButton = findViewById(R.id.roll_button);
        diceTextView = findViewById(R.id.result_text_view);
        maxNumberEditText = findViewById(R.id.max_number_edit_text);
        if (savedInstanceState != null) {
            restoreState(savedInstanceState);
        }

        rollButton.setOnClickListener((v) -> roll());
    }

    private void roll() {
        if (maxNumberEditText.getText() == null || maxNumberEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.wrong_number), Toast.LENGTH_SHORT).show();
            return;
        }
        currentRandom = new Random().nextInt(Integer.parseInt(maxNumberEditText.getText().toString()));
        diceTextView.setText(String.valueOf(currentRandom));
    }

    private void restoreState(Bundle savedInstanceState) {
        diceTextView.setText(String.valueOf(savedInstanceState.getInt(KEY)));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY, currentRandom);
    }
}
