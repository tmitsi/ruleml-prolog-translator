package org.ruleml.rulemlprologtranslator.translators.errorhandling;

import org.antlr.v4.runtime.*;

import java.util.ArrayList;
import java.util.List;

public class ErrorAggregator extends BaseErrorListener {
    private final List<String> errorMessages = new ArrayList<>();

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                            Object offendingSymbol,
                            int line, int charPositionInLine,
                            String msg,
                            RecognitionException e) {
        String errorMessage;
        if (e instanceof ModeViolationException) {
            errorMessage = "Warning: cannot have a top-level rule in query mode, at line: " + line;
        }
        else {
            errorMessage = "line " + line + ":" + charPositionInLine + " " + msg;
        }
        errorMessages.add(errorMessage);
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }
}
