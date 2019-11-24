package io.github.gubarsergey.pz3;

import java.beans.PropertyChangeSupport;

public class Expression {
    public PropertyChangeSupport propChangeSupport;

    private StringBuilder expression;

    public Expression() {
        this.expression = new StringBuilder();
        this.propChangeSupport = new PropertyChangeSupport(this);
    }

    public Expression(String value) {
        this.expression = new StringBuilder(value);
        this.propChangeSupport = new PropertyChangeSupport(this);
    }

    public void append(String value) {
        this.expression.append(value);
        this.expressionPropertyChanged();
    }

    public void clear() {
        int lastSymbolIndex = this.expression.length();
        this.expression.delete(0, lastSymbolIndex);
        this.expressionPropertyChanged();
    }

    public void deleteLastSymbol() {
        if (this.expression.length() > 0) {
            int lastSymbolIndex = this.expression.length() - 1;
            this.expression.deleteCharAt(lastSymbolIndex);
            this.expressionPropertyChanged();
        }
    }

    public String getExpression() {
        return this.expression.toString();
    }

    public String getFormatedExpresion() {
        String input = "";

        for (int i = 0; i < this.expression.length(); i++) {
            if (isOperation(this.expression.charAt(i))) {
                input += " " + this.expression.charAt(i) + " ";
            } else {
                input += this.expression.charAt(i);
            }
        }

        return input;
    }

    public char getLastSymbol() {
        return this.expression.charAt(this.expression.length() - 1);
    }

    private void expressionPropertyChanged() {
        this.propChangeSupport.firePropertyChange("expression", "expression", this.getExpression());
    }

    public static Boolean isOperation(Character value){
        Boolean isOperation = value == '+' ||
                value == '-' ||
                value == '*' ||
                value == '/';
        return  isOperation;
    }
}
