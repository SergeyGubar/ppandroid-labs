package io.github.gubarsergey.pz3;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ReversePolishNotation {
    public List<String> getRPN(String expression) {
        List<String> rpnExpression = new ArrayList<>();

        Stack<Character> operations = new Stack();
        String number = "";
        for(int i = 0; i < expression.length(); i++){
            char character = expression.charAt(i);

            if(!(this.isNumeric(character) || character == '.') && number != "") {
                rpnExpression.add(number);
                number = "";
            }


            if(this.isNumeric(character) || character == '.')
                number += Character.toString(character);

            else if(character == '(')
                operations.push(character);

            else if(character == ')') {
                while (!(operations.isEmpty()) && operations.peek() != '(')
                    rpnExpression.add(Character.toString(operations.pop()));
                if(!(operations.isEmpty())) //delete open bracket
                    operations.pop();
            }
            else if(this.isOperation(character))
            {
                int currentOperationPriority = this.getPriority(character);

                if(!(operations.empty())) {
                    int previousOperationPriority = this.getPriority(operations.peek());
                    if(currentOperationPriority > previousOperationPriority)
                        operations.push(character);
                    else {
                        char previousOperation = operations.pop();
                        rpnExpression.add(Character.toString(previousOperation));
                        operations.push(character);
                    }
                }
                else {
                    operations.push(character);
                }
            }
        }

        if(number != "")
            rpnExpression.add(number);

        while (!(operations.isEmpty())){
            if(!this.isOperation(operations.peek()))
                operations.pop();
            else
                rpnExpression.add(Character.toString(operations.pop()));
        }
        return rpnExpression;
    }

    private int getPriority(char operation) {
        switch (operation) {
            case '+':
                return 1;
            case '-':
                return 1;
            case '*':
                return 2;
            case '/':
                return 2;
            default:
                return 0;
        }
    }

    private Boolean isNumeric(char value) {
        try {
            Double.parseDouble(Character.toString(value));
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }

    private Boolean isOperation(char value) {
        return  value == '+' || value == '-' || value == '*' || value == '/';
    }
}

