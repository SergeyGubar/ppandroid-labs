package io.github.gubarsergey.pz3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String EXPRESSION_KEY = "EXPRESSION";

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
            R.id.button_equals,
            R.id.button_zero
    };

    private TextView expressionTextView;
    private TextView resultTextView;
    private Calculator calculator = new Calculator();
    private String expression = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        expressionTextView = findViewById(R.id.expression_text_view);
        resultTextView = findViewById(R.id.result_text_view);
        restore(savedInstanceState);
        setupListeners();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXPRESSION_KEY, expression);
    }

    private void restore(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            String saved = savedInstanceState.getString(EXPRESSION_KEY);
            this.expression = saved;
            calculate();
            expressionTextView.setText(expression);

        }
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
                appendSign("+");
                break;
            case R.id.button_minus:
                appendSign("-");
                break;
            case R.id.button_multiply:
                appendSign("*");
                break;
            case R.id.button_divide:
                appendSign("/");
                break;
            case R.id.button_zero:
                expressionTextView.append("0");
                break;
            case R.id.button_clear:
                clearSymbol();
                break;
            case R.id.button_equals:
                calculate();
                break;

            default:
                throw new IllegalArgumentException("Handling clicks on " + v.getId() + " is not allowed!");

        }
        this.expression = expressionTextView.getText().toString();


    }

    private void clearSymbol() {
        if (TextUtils.isEmpty(this.expression)) {
            return;
        }
        this.expression = this.expression.substring(0, this.expression.length() - 1);
        expressionTextView.setText(expression);
    }

    private void appendSign(String sign) {
        if (this.expression.length() > 0 && !isLastCharacterSign()) {
            expressionTextView.append(sign);
        }
    }

    private void calculate() {
        if (TextUtils.isEmpty(expression)) {
            return;
        }
        if (!isLastCharacterSign()) {
            resultTextView.setText(calculator.calculate(expression));
        }
    }


    private Boolean isLastCharacterSign() {

        Boolean result = this.expression.length() > 0 && isSign(this.expression.charAt(this.expression.length() - 1));
        Log.d("CALC", "isLasSign expr " + this.expression + " result " + result);
        return result;
    }

    private Boolean isSign(Character c) {
        return c == '+' || c == '-' || c == '/' || c == '*';
    }
}
