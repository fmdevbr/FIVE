/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufpe.cin.five.core.wordException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author vocallab
 */
public class WordExceptionProcess {
    
    
    private List<WordException> getWords(String xmlLocation)
    {
        List<WordException> words= new ArrayList<WordException>();
        SAXBuilder builder = new SAXBuilder();
        Document document;
        try {
            
            document = builder.build(xmlLocation);
            
            Element root = document.getRootElement();
            
            for(Object obj :  root.getChildren("exception"))
            {
                Element e = (Element)obj;

                WordException w = new WordException();

                w.setRepresentation(e.getChild("representation").getText());
                w.setException(e.getChild("word").getText());                
                words.add(w);
        }
        
        } catch (JDOMException ex) {
          System.out.println("Erro: " + ex.getMessage());
        } catch (IOException ex) {
          System.out.println("Erro: " + ex.getMessage());
        }
        
        
        return words;
    }
    
    public String process(String phrase, String xmlLocation)
    {
        List<WordException> words = getWords(xmlLocation);
                
        for(WordException word : words)        
        {            
            if(phrase.contains(word.getException()))
                phrase = phrase.replaceAll(Pattern.quote(word.getException()), word.getRepresentation());
        }
        
        return phrase;
    }
    
}
