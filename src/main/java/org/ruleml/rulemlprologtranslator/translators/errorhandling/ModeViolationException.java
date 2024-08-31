package org.ruleml.rulemlprologtranslator.translators.errorhandling;

import org.antlr.v4.runtime.InputMismatchException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;

public class ModeViolationException extends InputMismatchException {
    private String message;
    public ModeViolationException(String msg, Parser recognizer, int state, ParserRuleContext ctx) {
        super(recognizer, state, ctx);
        this.message = msg;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
