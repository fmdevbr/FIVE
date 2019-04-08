/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufpe.cin.five.register;

import br.ufpe.cin.five.core.wordException.WordException;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author Pablo Barros
 */
public class WordExceptionRegister {
    

    
    public void saveWords(List<WordException> words )
    {
/*        FileWriter projectXmlWriter = null;
        try {
            File projectXmlFile = new File("resources/exceptions.xml");
            System.out.println("Aqui: " + projectXmlFile.getAbsolutePath());
            if(projectXmlFile.exists())
                projectXmlFile.delete();
            
            projectXmlFile.createNewFile();
            
            Element rssElement = new Element("WordExceptions");
            for(WordException w : words)
            {
                Element projectElement = new Element("exception");            
                projectElement.addContent(new Element("word").addContent(w.getException()));
                projectElement.addContent(new Element("representation").addContent(w.getRepresentation()));

                rssElement.addContent(projectElement);
            }
            Document projectDocument = new Document(rssElement);
            XMLOutputter projetctOutputter = new XMLOutputter(Format.getPrettyFormat());
            projectXmlWriter = new FileWriter(projectXmlFile);
            projetctOutputter.output(projectDocument, projectXmlWriter);
            projectXmlWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(WordExceptionRegister.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                projectXmlWriter.close();
            } catch (IOException ex) {
                Logger.getLogger(WordExceptionRegister.class.getName()).log(Level.SEVERE, null, ex);
            }
        }*/
    }
    
    public List<WordException> getWords()
    {
        List<WordException> words= new ArrayList<WordException>();
        /*SAXBuilder builder = new SAXBuilder();
        Document document;
        try {
            
            document = builder.build("resources/exceptions.xml");
            
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
        
        */
        return words;
    }
    
}
