package org.ruleml.rulemlprologtranslator.translators;

import org.antlr.v4.runtime.ParserRuleContext;
import org.ruleml.rulemlprologtranslator.generated.XMLParser;
import org.ruleml.rulemlprologtranslator.generated.XMLParserBaseListener;

import java.util.HashSet;
import java.util.Set;

public class XMLToPrologTranslator extends XMLParserBaseListener {
    private final StringBuilderHandler sb;
    private final Set<String> unsupported;
    private final StringBuilder msg;
    private boolean inQuery;

    public XMLToPrologTranslator() {
        sb = new StringBuilderHandler();
        msg = new StringBuilder();
        unsupported = new HashSet<>();
        inQuery = false;
    }
    public String getKB() {
        return sb.getKB();
    }

    public String getQuery() {
        return sb.getQuery();
    }

    public String getMsg() {
        return msg.toString();
    }

    @Override
    public void exitDocument(XMLParser.DocumentContext ctx) {
        if (unsupported.isEmpty()) {
            return;
        }
        msg.append("Unsupported tags found: ").append(unsupported).append("\n");
    }

    @Override
    public void enterUnsupported(XMLParser.UnsupportedContext ctx) {
        unsupported.add(ctx.Name(0).getText());
    }

    @Override
    public void enterQuery(XMLParser.QueryContext ctx) {
        inQuery = true;
    }

    @Override
    public void exitQuery(XMLParser.QueryContext ctx) {
        sb.append(".\n");
        inQuery = false;
    }

    @Override
    public void enterIf(XMLParser.IfContext ctx) {
        sb.append(" :-\n\t");
    }

    public void exitImplies(XMLParser.ImpliesContext ctx) {
        sb.append(".\n");
    }

    @Override
    public void enterNaf(XMLParser.NafContext ctx) {
        sb.append("\\+");
    }
    @Override
    public void exitNaf(XMLParser.NafContext ctx) {
        commaParEqualOrColon(ctx);
    }

    @Override
    public void enterAnd(XMLParser.AndContext ctx) {
        if (ctx.getParent().getParent().getParent() instanceof XMLParser.NafContext
                || ctx.getParent().getParent().getParent() instanceof XMLParser.AndContext
                || ctx.getParent().getParent().getParent() instanceof XMLParser.OrContext ) {
            if (ctx.getParent().getParent().getParent() instanceof XMLParser.NafContext) {
                sb.append(" ");
            }
            sb.append("(");
        }
    }

    @Override
    public void exitAnd(XMLParser.AndContext ctx) {
        if (ctx.getParent().getParent().getParent() instanceof XMLParser.NafContext
                || ctx.getParent().getParent().getParent() instanceof XMLParser.AndContext
                || ctx.getParent().getParent().getParent() instanceof XMLParser.OrContext ) {
            sb.append(")");
        }
        commaParEqualOrColon(ctx);
    }

    @Override
    public void enterOr(XMLParser.OrContext ctx) {
        if (ctx.getParent().getParent().getParent() instanceof XMLParser.NafContext
                || ctx.getParent().getParent().getParent() instanceof XMLParser.AndContext
                || ctx.getParent().getParent().getParent() instanceof XMLParser.OrContext ) {
            if (ctx.getParent().getParent().getParent() instanceof XMLParser.NafContext) {
                sb.append(" ");
            }
            sb.append("(");
        }
    }

    @Override
    public void exitOr(XMLParser.OrContext ctx) {
        if (ctx.getParent().getParent().getParent() instanceof XMLParser.NafContext
                || ctx.getParent().getParent().getParent() instanceof XMLParser.AndContext
                || ctx.getParent().getParent().getParent() instanceof XMLParser.OrContext ) {
            sb.append(")");
        }
        commaParEqualOrColon(ctx);
    }

    public void enterAtom(XMLParser.AtomContext ctx) {
        if (ctx.getParent().getParent().getParent() instanceof XMLParser.NafContext) {
            sb.append(" ");
        }
    }
    @Override
    public void exitAtom(XMLParser.AtomContext ctx) {
        commaParEqualOrColon(ctx);
    }

    @Override
    public void exitEqual(XMLParser.EqualContext ctx) {
        commaParEqualOrColon(ctx);
    }

    @Override
    public void exitExpr(XMLParser.ExprContext ctx) {
        commaParEqualOrColon(ctx);
    }

    @Override
    public void enterRel(XMLParser.RelContext ctx) {
        writePrologCompatName(ctx);
        if (ctx.getParent().getParent().getRuleContext().getChildCount()==1) {
            return;
        }
        sb.append("(");
    }


    @Override
    public void enterFun(XMLParser.FunContext ctx) {
        writePrologCompatName(ctx);
        sb.append("(");
    }

    @Override
    public void enterPlex(XMLParser.PlexContext ctx) {
        sb.append("[");
    }

    @Override
    public void exitPlex(XMLParser.PlexContext ctx) {
        sb.append("]");
        commaParEqualOrColon(ctx);
    }
    @Override
    public void enterRepo(XMLParser.RepoContext ctx) {
        // rest deletes the comma of the previous element
        sb.setLength(Math.max(sb.length() - 1, 0));
        sb.append(" | ");
    }

    @Override
    public void enterInd(XMLParser.IndContext ctx) {
        writePrologCompatName(ctx);
    }
    public void enterVar(XMLParser.VarContext ctx) {
        writePrologCompatName(ctx);
    }

    public void enterData(XMLParser.DataContext ctx) {
        writePrologCompatName(ctx);
    }

    private void writePrologCompatName(ParserRuleContext ctx) {

        if (ctx instanceof XMLParser.VarContext) {
            String text = ((XMLParser.VarContext) ctx).content().getText();
            if (text.isEmpty()) {
                sb.append("_");
                commaParEqualOrColon(ctx);
                return;
            }
            if (Character.isLowerCase(text.codePointAt(0))) {
                msg.append("Warning: Lower case variable \"").append(text).append("\" at line ")
                        .append(ctx.getStart().getLine())
                        .append(" changed\n");
                sb.append("V");
            }
            sb.append(text);
            commaParEqualOrColon(ctx);
            return;
        }
        String text = null;
        if (ctx instanceof XMLParser.RelContext) {
            text = ((XMLParser.RelContext) ctx).content().getText();
        }
        else if (ctx instanceof XMLParser.FunContext) {
            text = ((XMLParser.FunContext) ctx).content().getText();
        }
        else if (ctx instanceof XMLParser.IndContext) {
            text = ((XMLParser.IndContext) ctx).content().getText();
        }
        else if (ctx instanceof XMLParser.DataContext) {
            text = ((XMLParser.DataContext) ctx).content().getText();
        }
        if (text == null) {
            msg.append("Empty name at line ").append(ctx.getStart().getLine()).append("\n");
            return;
        }
        if (Character.isUpperCase(text.codePointAt(0))) {
            msg.append("Warning: Upper case name \"").append(text).append("\" at line ")
                    .append(ctx.getStart().getLine())
                    .append(" changed\n");
            sb.append("'");
            sb.append(text);
            sb.append("'");
            if (ctx instanceof XMLParser.IndContext || ctx instanceof XMLParser.DataContext) {
                commaParEqualOrColon(ctx);
            }
            return;
        }
        sb.append(text);
        if (ctx instanceof XMLParser.IndContext || ctx instanceof XMLParser.DataContext) {
            commaParEqualOrColon(ctx);
        }
    }

    public void commaParEqualOrColon(ParserRuleContext ctx) {
        XMLParser.ContentContext parentContentContext= (XMLParser.ContentContext) ctx.getParent().getParent();
        int noOfChildren = parentContentContext.getChildCount();

        if (parentContentContext.getParent() instanceof XMLParser.EqualContext) {
            if (ctx.getParent() != parentContentContext.element(noOfChildren-1)){  // not the last child
                sb.append(" = ");
                return;
            }
        }

        if (ctx.getParent() != parentContentContext.element(noOfChildren-1)){  // not the last child
            if (parentContentContext.getParent() instanceof XMLParser.OrContext) {
                sb.append("; ");
                return;
            }

            if (parentContentContext.getParent() instanceof XMLParser.AssertContext) {
                sb.append(".\n");
                return;
            }
            sb.append(",");
            if (parentContentContext.getParent() instanceof XMLParser.AndContext) {
                sb.append(" ");
            }
            return;
        }
        if (parentContentContext.getChild(0).getChild(0) instanceof XMLParser.RelContext) {
            sb.append(")");
        }
        if (parentContentContext.getChild(0).getChild(0) instanceof XMLParser.FunContext) {
            sb.append(")");
        }
        if (parentContentContext.getParent() instanceof XMLParser.AssertContext) {
            sb.append(".\n");
        }
    }

    private class StringBuilderHandler {
        private final StringBuilder kb;
        private final StringBuilder query;
        StringBuilderHandler() {
            kb = new StringBuilder();
            query = new StringBuilder();
        }

        String getKB() {
            return kb.toString();
        }
        String getQuery() {
            return query.toString();
        }

        void append(String s) {
            if (inQuery) {
                query.append(s);
            }
            else {
                kb.append(s);
            }
        }
        void setLength(int length) {
            if (inQuery) {
                query.setLength(length);
            }
            else
            {
                kb.setLength(length);
            }
        }

        int length() {
            if (inQuery) {
                return query.length();
            }
            return kb.length();
        }
    }
}
