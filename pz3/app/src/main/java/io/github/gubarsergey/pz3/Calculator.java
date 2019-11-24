package io.github.gubarsergey.pz3;
import java.util.List;
import java.util.Stack;

public class Calculator {
    private RPN rpn;

    public Calculator() {
        this.rpn = new RPN();
    }

    public String calculate(String expression) {
        List<String> rpnExpr = rpn.getRPN(this.correctExpression(expression));

        Stack<Double> numbers = new Stack<>();

        for(int i = 0; i < rpnExpr.size(); i++) {
            String item = rpnExpr.get(i);

            if(this.isNumeric(item)) {
                numbers.push(Double.parseDouble(item));
            }
            else {
                Double currentNumber = numbers.pop();
                Double previousNumber = 0.0;
                if(!(numbers.isEmpty()))
                    previousNumber = numbers.pop();
                Double result = 0.0;

                switch (item) {
                    case "+":
                        result = previousNumber + currentNumber;
                        break;
                    case "-":
                        result = previousNumber - currentNumber;
                        break;
                    case "*":
                        result = previousNumber * currentNumber;
                        break;
                    case "/":
                        result = previousNumber / currentNumber;
                        break;
                }

                numbers.push(result);
            }
        }

        return numbers.pop().toString();
    }

    private String addOmittedBrackets(String expression) {
        StringBuilder ommitedOpenBrackets = new StringBuilder();
        StringBuilder ommitedCloseBrackets = new StringBuilder();

        String bracketSequence = this.getBracketSubsequence(expression);

        for(int i = 0; i < bracketSequence.length(); i++) {
            char character = bracketSequence.charAt(i);

            if(character == '(')
                ommitedCloseBrackets.append(")");
            else {
                if(ommitedCloseBrackets.length() > 0)
                    ommitedCloseBrackets.deleteCharAt(ommitedCloseBrackets.length() - 1);
                else
                    ommitedOpenBrackets.append("(");

            }
        }

        return ommitedOpenBrackets.toString() + expression + ommitedCloseBrackets.toString();
    }

    private String correctExpression(String expression) {
        String correctedExpression = this.addOmittedBrackets(expression);
        correctedExpression = this.setMultiplicationSign(correctedExpression);

        return correctedExpression;
    }

    private  String getBracketSubsequence(String sequence) {
        String bracketSequence = "";

        for(int i = 0; i < sequence.length(); i++) {
            char character = sequence.charAt(i);

            if(character == '(' || character == ')')
                bracketSequence += character;
        }

        return  bracketSequence;
    }

    private Boolean isNumeric(char value) {
        try {
            Double.parseDouble(Character.toString(value));
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    private Boolean isNumeric(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    private Boolean isMultiplicationSignRequired(char currentValue, char nextValue) {

        Boolean isMultiplicationSignRequired = this.isNumeric(currentValue) && nextValue == '(' ||
                currentValue == ')' && this.isNumeric(nextValue) ||
                currentValue == ')' && nextValue == '(';

        return isMultiplicationSignRequired;
    }

    private String setMultiplicationSign(String expression) {
        StringBuilder result = new StringBuilder();

        for(int i = 0; i < expression.length() - 1; i++) {
            result.append(expression.charAt(i));

            if(this.isMultiplicationSignRequired(expression.charAt(i), expression.charAt(i + 1)))
                result.append('*');
        }

        result.append(expression.charAt(expression.length() - 1));

        return result.toString();
    }
}
