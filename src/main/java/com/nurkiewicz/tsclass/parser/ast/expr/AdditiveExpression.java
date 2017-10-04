package com.nurkiewicz.tsclass.parser.ast.expr;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
public class AdditiveExpression extends BinaryExpression {

    private final Operator operator;

    public AdditiveExpression(Expression left, Operator operator, Expression right) {
        super(left, right);
        this.operator = operator;
    }

    public enum Operator {
        PLUS("+"), MINUS("-");

        private final String s;

        Operator(String s) {
            this.s = s;
        }

        public static Operator of(String s) {
            switch(s) {
                case "+": return PLUS;
                case "-": return MINUS;
                default:
                    throw new IllegalArgumentException(s);
            }
        }


        @Override
        public String toString() {
            return s;
        }
    }

    public String toString() {
        return getLeft() + " " + operator + " " + getRight();
    }

}
