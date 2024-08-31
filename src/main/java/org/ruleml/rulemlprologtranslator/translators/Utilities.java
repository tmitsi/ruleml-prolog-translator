package org.ruleml.rulemlprologtranslator.translators;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import org.dom4j.DocumentException;
import org.dom4j.Element;


public class Utilities {
    public static String prettyPrintXML(String xml) {
        String result = null;
        try {
            Document doc = DocumentHelper.parseText(xml);
            StringWriter sw = new StringWriter();
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setExpandEmptyElements(true);
            XMLWriter xw = new XMLWriter(sw, format);
            xw.write(doc);
            result = sw.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String combineXML(String xml1, String xml2, boolean pprint) {
        try {
            // Create a SAXReader to parse the XML strings
            SAXReader reader = new SAXReader();

            // Parse the first XML string
            Document doc1 = reader.read(new StringReader(xml1));
            Element root1 = doc1.getRootElement();

            // Parse the second XML string
            Document doc2 = reader.read(new StringReader(xml2));
            Element root2 = doc2.getRootElement();

            // Append the child elements from the second document to the first document
            for (Element element : root2.elements()) {
                root1.add((Element) element.clone());
            }
            StringWriter writer = new StringWriter();
            XMLWriter xmlWriter;
            if (pprint) {
                OutputFormat format = OutputFormat.createPrettyPrint();
                format.setExpandEmptyElements(true);
                xmlWriter = new XMLWriter(writer,format);
            }
            else {
                xmlWriter = new XMLWriter(writer);
            }


            xmlWriter.write(doc1);
            xmlWriter.close();

            return writer.toString();
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            return null; // or handle the exception as needed
        }
    }
}
