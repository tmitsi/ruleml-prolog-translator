package org.ruleml.rulemlprologtranslator.backend;

import org.ruleml.rulemlprologtranslator.backend.api.TranslationApiDelegate;
import org.ruleml.rulemlprologtranslator.model.Prolog;
import org.ruleml.rulemlprologtranslator.model.RuleML;
import org.ruleml.rulemlprologtranslator.translators.Translator;
import org.ruleml.rulemlprologtranslator.translators.Utilities;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * @author ralph
 */
@Service
public class TranslationApiDelegateImpl implements TranslationApiDelegate {
    Translator tr = new Translator();
    @Override
    public ResponseEntity<Prolog> getPrologTranslation(String ruleMLText) {
        Prolog p = new Prolog();
        String[] result = tr.translateToProlog(ruleMLText);
        p.setKb(result[0]);
        p.setQueries(result[1]);
        p.setMessages(result[2]);
        return new ResponseEntity<>(p, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<RuleML> getRuleMLTranslation(String kb, String query, Boolean pprint) {
        StringBuilder sbRuleML;
        StringBuilder sbMessages;
        RuleML r = new RuleML();
        tr.setQueryMode(false);

        String[] resultKB;
        if (!query.isEmpty()) {
            resultKB = tr.translateToXML(kb, false);
        }
        else {
            resultKB = tr.translateToXML(kb, pprint);
        }
        if (query.isEmpty()) {
            r.setXml(resultKB[0]);
            r.setMessages(resultKB[1]);
            return new ResponseEntity<>(r, HttpStatus.OK);
        }
        tr.setQueryMode(true);
        String[] resultQuery = tr.translateToXML(query, false);
        String xml = Utilities.combineXML(resultKB[0], resultQuery[0], pprint);
        r.setXml(xml);
        r.setMessages(resultKB[1] + resultQuery[1]);
        return new ResponseEntity<>(r, HttpStatus.OK);
    }


}
