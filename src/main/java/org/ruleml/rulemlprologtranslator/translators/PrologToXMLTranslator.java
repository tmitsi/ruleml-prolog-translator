package org.ruleml.rulemlprologtranslator.translators;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.ruleml.rulemlprologtranslator.generated.PrologGrammarBaseListener;
import org.ruleml.rulemlprologtranslator.generated.PrologGrammarParser;
import org.ruleml.rulemlprologtranslator.translators.errorhandling.ModeViolationException;

import javax.xml.transform.ErrorListener;
import java.util.List;

public class PrologToXMLTranslator extends PrologGrammarBaseListener {
    private final StringBuilder sb;
    private boolean queryMode;
    PrologGrammarParser pp;
    private static final String xmlVersion = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    private static final String xmlModel = "<?xml-model href=\"https://raw.githubusercontent.com/RuleML/deliberation-ruleml/1.03/relaxng/nafhologeq_relaxed.rnc\"?>";
    private static final String xmlns = "xmlns=\"http://ruleml.org/spec\" ";

    private static final String xmlnsXS = "xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"";
//    private static final String xmlSchemaLocation = "xsi:schemaLocation=\"http://ruleml.org/spec http://deliberation.ruleml.org/1.03/xsd/nafhologeq.xsd\"";
    private static final String xmlSchemaLocation = "xsi:schemaLocation=\"http://ruleml.org/spec https://raw.githubusercontent.com/RuleML/deliberation-ruleml/1.03/xsd/nafhologeq.xsd\"";
    public PrologToXMLTranslator(PrologGrammarParser pp) {
        sb = new StringBuilder();
        this.pp = pp;
        queryMode = pp.getQueryMode();
    }

    public String toString() {
        return sb.toString();
    }

    @Override
    public void enterDocument(PrologGrammarParser.DocumentContext ctx) {
        sb.append(xmlVersion).append(xmlModel);
        sb.append("<RuleML ").append(xmlns).append(xmlnsXS).append(" ")
                .append(" ").append(xmlSchemaLocation).append(">");
    }
    @Override
    public void exitDocument(PrologGrammarParser.DocumentContext ctx) {
        sb.append("</RuleML>");
    }

    @Override
    public void enterAssert(PrologGrammarParser.AssertContext ctx) {
        sb.append("<Assert mapClosure=\"universal\">");
    }

    @Override
    public void exitAssert(PrologGrammarParser.AssertContext ctx) {
        sb.append("</Assert>");
    }

    @Override
    public void enterQuery(PrologGrammarParser.QueryContext ctx) {
        sb.append("<Query closure=\"existential\">");
    }

    @Override
    public void exitQuery(PrologGrammarParser.QueryContext ctx) {
        sb.append("</Query>");
    }

    @Override
    public void enterImplies(PrologGrammarParser.ImpliesContext ctx) {
        if (queryMode && (ctx.getParent() instanceof PrologGrammarParser.DocumentContext)) {
            try {
                throw new ModeViolationException("Warning: cannot have a top-level rule in query mode, at line: " +
                        ctx.getStart().getLine(), pp, pp.getState(), ctx);
            }
            catch (ModeViolationException e) {
                pp.notifyErrorListeners(e.getOffendingToken(), e.getMessage(), e);
            }
        }
        sb.append("<Implies>");
    }

    @Override
    public void exitImplies(PrologGrammarParser.ImpliesContext ctx) {
        sb.append("</Implies>");
    }

    @Override
    public void enterConclusion(PrologGrammarParser.ConclusionContext ctx) {
        if (!(ctx.getParent() instanceof PrologGrammarParser.FactContext)) {
            sb.append("<then>");
        }
    }

    @Override
    public void exitConclusion(PrologGrammarParser.ConclusionContext ctx) {
        if (!(ctx.getParent() instanceof  PrologGrammarParser.FactContext)) {
            sb.append("</then>");
        }
    }

    @Override
    public void enterGoal(PrologGrammarParser.GoalContext ctx) {
        if (ctx.getParent() instanceof PrologGrammarParser.ImpliesContext) {
            sb.append("<if>");
        }
    }

    @Override
    public void exitGoal(PrologGrammarParser.GoalContext ctx) {
        if (ctx.getParent() instanceof PrologGrammarParser.ImpliesContext) {
            sb.append("</if>");
        }
    }

    @Override
    public void enterConjunction(PrologGrammarParser.ConjunctionContext ctx) {
        sb.append("<And>");
    }

    @Override
    public void exitConjunction(PrologGrammarParser.ConjunctionContext ctx) {
        sb.append("</And>");
    }

    @Override
    public void enterDisjunction(PrologGrammarParser.DisjunctionContext ctx) {
        sb.append("<Or>");
    }

    @Override
    public void exitDisjunction(PrologGrammarParser.DisjunctionContext ctx) {
        sb.append("</Or>");
    }

    @Override
    public void enterNaf(PrologGrammarParser.NafContext ctx) {
        sb.append("<Naf>");
    }

    @Override
    public void exitNaf(PrologGrammarParser.NafContext ctx) {
        sb.append("</Naf>");
    }

    @Override
    public void enterCompound_term(PrologGrammarParser.Compound_termContext ctx) {
        if (! (ctx.getChild(0) instanceof PrologGrammarParser.EqualityContext)) {
            sb.append("<Atom>");
        }
    }

    @Override
    public void exitCompound_term(PrologGrammarParser.Compound_termContext ctx) {
        if (! (ctx.getChild(0) instanceof PrologGrammarParser.EqualityContext)) {
            sb.append("</Atom>");
        }
    }

    @Override
    public void enterEquality(PrologGrammarParser.EqualityContext ctx) {
        sb.append("<Equal>");
    }

    @Override
    public void exitEquality(PrologGrammarParser.EqualityContext ctx) {
        sb.append("</Equal>");
    }
    @Override
    public void enterList(PrologGrammarParser.ListContext ctx) {
        sb.append("<Plex>");
    }

    @Override
    public void exitList(PrologGrammarParser.ListContext ctx) {
        sb.append("</Plex>");
    }

    @Override
    public void enterFunction(PrologGrammarParser.FunctionContext ctx) {
        sb.append("<Expr>");
    }

    @Override
    public void exitFunction(PrologGrammarParser.FunctionContext ctx) {
        sb.append("</Expr>");
    }

    @Override
    public void enterRepo(PrologGrammarParser.RepoContext ctx) {
        sb.append("<repo>");
    }

    @Override
    public void exitRepo(PrologGrammarParser.RepoContext ctx) {
        sb.append("</repo>");
    }

    @Override
    public void enterAtom(PrologGrammarParser.AtomContext ctx) {
        if (ctx.getParent() instanceof PrologGrammarParser.Compound_termContext) {
            sb.append("<Rel>");
            return;
        }
        if (ctx.getParent() instanceof PrologGrammarParser.FunctionContext) {
            sb.append("<Fun>");
            return;
        }
        sb.append("<Ind>");
    }

    @Override
    public void exitAtom(PrologGrammarParser.AtomContext ctx) {
        sb.append(ctx.getText());
        if (ctx.getParent() instanceof PrologGrammarParser.Compound_termContext) {
            sb.append("</Rel>");
            return;
        }
        if (ctx.getParent() instanceof PrologGrammarParser.FunctionContext) {
            sb.append("</Fun>");
            return;
        }
        sb.append("</Ind>");
    }

    @Override
    public void enterVariable(PrologGrammarParser.VariableContext ctx) {
        sb.append("<Var>");
        String varText = ctx.getText();
        if (varText.equals("_")) {
            return;
        }
        sb.append(varText);
    }

    @Override
    public void exitVariable(PrologGrammarParser.VariableContext ctx) {
        sb.append("</Var>");
    }

    @Override
    public void enterInteger(PrologGrammarParser.IntegerContext ctx) {
        sb.append("<Data xsi:type=\"xs:integer\">");
        sb.append(ctx.getText());
    }

    @Override
    public void exitInteger(PrologGrammarParser.IntegerContext ctx) {
        sb.append("</Data>");
    }

    @Override
    public void enterFloat(PrologGrammarParser.FloatContext ctx) {
        sb.append("<Data xsi:type=\"xs:float\">");
        sb.append(ctx.getText());
    }

    @Override
    public void exitFloat(PrologGrammarParser.FloatContext ctx) {
        sb.append("</Data>");
    }
}
