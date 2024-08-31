package org.ruleml.rulemlprologtranslator.translators;

import org.antlr.v4.runtime.*;
import org.ruleml.rulemlprologtranslator.generated.PrologGrammarLexer;
import org.ruleml.rulemlprologtranslator.generated.PrologGrammarParser;
import org.ruleml.rulemlprologtranslator.generated.XMLLexer;
import org.ruleml.rulemlprologtranslator.generated.XMLParser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.ruleml.rulemlprologtranslator.translators.errorhandling.ErrorAggregator;
import org.ruleml.rulemlprologtranslator.translators.errorhandling.ModeViolationException;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class Translator {
    private boolean queryMode = false;

    public void setQueryMode(boolean queryMode) {
        this.queryMode = queryMode;
    }

    public String[] translateToXML(CharStream codePointCharStream) {
        return translateToXML(codePointCharStream,false);
    }

    public String[] translateToXML(CharStream codePointCharStream, boolean prettyPrint) {
        // create a lexer that feeds off of input CharStream
        PrologGrammarLexer lexer = new PrologGrammarLexer(codePointCharStream);
        // create a buffer of tokens pulled from the lexer
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        // create a parser that feeds off the tokens buffer
        PrologGrammarParser parser = new PrologGrammarParser(tokens);
        parser.setQueryMode(queryMode);
        ErrorAggregator errorAggregator = new ErrorAggregator();
        parser.addErrorListener(errorAggregator);
        ParseTree tree = parser.document(); // begin parsing at init rule

        // Create a generic parse tree walker that can trigger callbacks
        ParseTreeWalker walker = new ParseTreeWalker();
        // Walk the tree created during the parse, trigger callbacks
        PrologToXMLTranslator plt = new PrologToXMLTranslator(parser);
        walker.walk(plt, tree);
        List<String> antlrErrors = errorAggregator.getErrorMessages();
        String messages = antlrErrors.stream().collect(Collectors.joining("\n"));
        if (prettyPrint) {
            return new String[]{Utilities.prettyPrintXML(plt.toString()),messages};
        }
        return new String[]{plt.toString(),messages};
    }


    public String[] translateToXML(File f) {
        try {
            CharStream codePointCharStream = CharStreams.fromFileName(f.getAbsolutePath());
            return translateToXML(codePointCharStream);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String[] translateToXML(String str) {
        CharStream codePointCharStream = CharStreams.fromString(str);
        return translateToXML(codePointCharStream);
    }

    public String[] translateToXML(String str, boolean pprint) {
        CharStream codePointCharStream = CharStreams.fromString(str);
        return translateToXML(codePointCharStream, pprint);
    }

    public String[] translateToProlog(File f) {
        try {
            CharStream codePointCharStream = CharStreams.fromFileName(f.getAbsolutePath());
            return translateToProlog(codePointCharStream);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String[] translateToProlog(String str) {
        CharStream codePointCharStream = CharStreams.fromString(str);

        return translateToProlog(codePointCharStream);
    }

    public String[] translateToProlog(CharStream codePointCharStream) {
        // create a lexer that feeds off of input CharStream
        XMLLexer lexer = new XMLLexer(codePointCharStream);
        // create a buffer of tokens pulled from the lexer
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        // create a parser that feeds off the tokens buffer
        XMLParser parser = new XMLParser(tokens);
        ErrorAggregator errorAggregator = new ErrorAggregator();
        parser.addErrorListener(errorAggregator);
        ParseTree tree = parser.document(); // begin parsing at init rule

        // Create a generic parse tree walker that can trigger callbacks
        ParseTreeWalker walker = new ParseTreeWalker();
        // Walk the tree created during the parse, trigger callbacks
        XMLToPrologTranslator xml = new XMLToPrologTranslator();
        walker.walk(xml, tree);
        List<String> antlrErrors = errorAggregator.getErrorMessages();
        String messages = xml.getMsg() + antlrErrors.stream().collect(Collectors.joining("\n"));
        return new String[]{xml.getKB(), xml.getQuery(), messages};
    }

    public static void main(String[] args) {
        Translator tr = new Translator();
        String prologKB = "b(a(B,C)). a(b).\np(a,B) :- q(A,a), q(B, a(A, [H | R])).\ntest(a(b(c,E))):- \\+ test2(a,[a,B | E]), a(b).";
        System.out.println("Prolog KB:\n" + prologKB);
        String[] ruleml = tr.translateToXML(prologKB);
        System.out.println("RuleML translation:\n" + ruleml[0]);
        String[] res= tr.translateToProlog(ruleml[0]);
        System.out.println("Prolog translation:\n" + res[0] + "\n" + res[1]);
    }
}
