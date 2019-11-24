package io.github.gubarsergey.pz3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private int[] buttons = new int[]{
            R.id.button_1,
            R.id.button_2,
            R.id.button_3,
            R.id.button_4,
            R.id.button_5,
            R.id.button_6,
            R.id.button_7,
            R.id.button_8,
            R.id.button_9,
            R.id.button_minus,
            R.id.button_plus,
            R.id.button_multiply,
            R.id.button_divide,
            R.id.button_clear,
            R.id.button_equals
    };

    private TextView expressionTextView;
    private TextView resultTextView;
    private Calculator calculator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        expressionTextView = findViewById(R.id.expression_text_view);
        resultTextView = findViewById(R.id.result_text_view);
        calculator = new Calculator();
        // TODO: restore state
        setupListeners();
    }

    private void setupListeners() {
        for (int buttonId : buttons) {
            findViewById(buttonId).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_1:
                expressionTextView.append("1");
                break;
            case R.id.button_2:
                expressionTextView.append("2");
                break;
            case R.id.button_3:
                expressionTextView.append("3");
                break;
            case R.id.button_4:
                expressionTextView.append("4");
                break;
            case R.id.button_5:
                expressionTextView.append("5");
                break;
            case R.id.button_6:
                expressionTextView.append("6");
                break;
            case R.id.button_7:
                expressionTextView.append("7");
                break;
            case R.id.button_8:
                expressionTextView.append("8");
                break;
            case R.id.button_9:
                expressionTextView.append("9");
                break;
            case R.id.button_plus:
                expressionTextView.append("+");
                break;
            case R.id.button_minus:
                expressionTextView.append("-");
                break;
            case R.id.button_multiply:
                expressionTextView.append("*");
                break;
            case R.id.button_divide:
                expressionTextView.append("/");
                break;
            case R.id.button_clear:
                break;
            case R.id.button_equals:
                resultTextView.setText(calculator.calculate(expressionTextView.getText().toString()));
                break;
            default:
                throw new IllegalArgumentException("Handling clicks on " + v.getId() + " is not allowed!");
        }
    }
}
