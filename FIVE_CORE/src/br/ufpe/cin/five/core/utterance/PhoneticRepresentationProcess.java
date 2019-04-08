/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufpe.cin.five.core.utterance;

import br.ufpe.cin.five.core.dictionary.PhoneticRepresentation;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 *
 * @author vocallab
 */
public class PhoneticRepresentationProcess {
    
        
    public static void insertPhoneticRepresentation(PhoneticRepresentation phonetic, String xmlLocation)
    {
        String filePath = xmlLocation + phonetic.getDescription().charAt(0)+".dic";
        File f = new File(filePath);
        if(f.exists())
            newPhoneticRepresentation(phonetic, xmlLocation);
        else
            savePhoneticRepresentation(phonetic, xmlLocation);
    }
    
    public static void newPhoneticRepresentation(PhoneticRepresentation phonetic, String xmlLocation)
    {
        String filePath = xmlLocation + phonetic.getDescription().toLowerCase().charAt(0)+".dic";
        
        try {
 
		SAXBuilder builder = new SAXBuilder();
		File xmlFile = new File(filePath);
 
		Document doc = (Document) builder.build(xmlFile);
		Element rootNode = doc.getRootElement();
 
                Element word = new Element("word").addContent(new Element("description").addContent(phonetic.getDescription()));
                for(String s : phonetic.getPhoneticRepresentation())                
                    word.addContent(new Element("representation").addContent(s));
                
                rootNode.addContent(word);
                
		XMLOutputter xmlOutput = new XMLOutputter();
 
		// display nice nice
		xmlOutput.setFormat(Format.getPrettyFormat());
		xmlOutput.output(doc, new FileWriter(filePath));
 
		// xmlOutput.output(doc, System.out);
 		
	  } catch (JDOMException ex) {
            System.out.println("Erro: " + ex.getMessage()+"\n " + ex.getStackTrace().toString());
        } catch (IOException io) {
            System.out.println("Erro: " + io.getMessage() + "\n " + io.getStackTrace().toString());
     }   
    
    }
        
    public static void main(String[] args)
    {
     
           // PhoneticRepresentationProcess.importWords("/home/vocallab-pablo/Downloads/list_westpoint.txt");
            correctPhonetics("/home/vocallab-pablo/NetBeansProjects/FIVE_GUI/resources/dictionary/", "/home/vocallab-pablo/NetBeansProjects/FIVE_GUI/resources/dictionary/new/");
        
    }
    
    public static void importWords(String path) throws FileNotFoundException
    {
        System.out.println("Path: " + path);
               
        try {
            File file = new File(path);  
            FileReader reader = new FileReader(file);  
            BufferedReader buffer = new BufferedReader(reader);  

            String linha = "";                                              
            
            while (linha != null) {  
            {                
                String[] splittedLine = buffer.readLine().split("	");
                PhoneticRepresentation p = new PhoneticRepresentation();
                p.setDescription(splittedLine[0]);
                List<String> phoneticRepresentations = new ArrayList<String>();     
                //System.out.println("Tamanho: " + splittedLine.length + " | Palavra: " + splittedLine[0]);
                try{
                phoneticRepresentations.add(splittedLine[1]);
                
                p.setPhoneticRepresentation(phoneticRepresentations);
                
                PhoneticRepresentationProcess.insertPhoneticRepresentation(p, "/home/vocallab-pablo/NetBeansProjects/FIVE_GUI/resources/dictionary/");
                }catch(Exception ex)
                {
                    System.out.println("Erro!");
                    System.out.println("Tamanho: " + splittedLine.length + " | Palavra: " + splittedLine[0]);
                    System.out.println(ex.getMessage());
                }
            }
                        
             
    }   } catch (IOException ex) {
            Logger.getLogger(UtteranceProcess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void savePhoneticRepresentation(PhoneticRepresentation phonetic, String xmlLocation)
    {
     
       String filePath = xmlLocation + phonetic.getDescription().toLowerCase().charAt(0)+".dic";
        FileWriter projectXmlWriter = null;
        try {
            File projectXmlFile = new File(filePath);
            System.out.println("Aqui: " + projectXmlFile.getAbsolutePath());
            if(projectXmlFile.exists())
                projectXmlFile.delete();
            
            projectXmlFile.createNewFile();
            
            org.jdom.Element rssElement = new org.jdom.Element("Words");
            org.jdom.Element projectElement = new org.jdom.Element("word");            
            projectElement.addContent(new org.jdom.Element("description").addContent(phonetic.getDescription()));
                
            for(String s : phonetic.getPhoneticRepresentation())                
                projectElement.addContent(new Element("representation").addContent(s));
                                    
            rssElement.addContent(projectElement);
            org.jdom.Document projectDocument = new org.jdom.Document(rssElement);
            XMLOutputter projetctOutputter = new XMLOutputter(Format.getPrettyFormat());
            projectXmlWriter = new FileWriter(projectXmlFile);
            projetctOutputter.output(projectDocument, projectXmlWriter);
            projectXmlWriter.close();
        } catch (IOException ex) {
           System.out.println(ex.getMessage());
        } finally {
            try {
                projectXmlWriter.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
    
    
    public static String getPhonetic(String xmlFolder, String word)
    {
        
        String finalPath = xmlFolder + word.toLowerCase().charAt(0)+".dic";
    
        SAXBuilder builder = new SAXBuilder();            
            try {

                Document document = builder.build(finalPath);

                Element root = document.getRootElement();

                for(Object obj :  root.getChildren("word"))
                {
                    Element e = (Element)obj;

                    
                    if(e.getChild("description").getText().equalsIgnoreCase(word))
                    {
                        for(Object o : e.getChildren("representation"))
                            return ((Element)o).getText();
                    }
                                        
            }
                } catch (JDOMException ex) {
            System.out.println("Erro5: " + ex.getMessage());
            } catch (IOException ex) {
            System.out.println("Erro4: " + ex.getMessage());
            }catch(Exception ex)
            {
             System.out.println("Erro3: " + ex.getMessage());
            }
            insertNotFounded(xmlFolder,word);
            return "";
    }
    
    public static void insertNotFounded(String folder,String palavra)
    {
        try{
            // Create file 
            FileWriter fstream = new FileWriter(folder+ "NotFounded.txt",true);
            BufferedWriter out = new BufferedWriter(fstream);   
            
            out.write(palavra + "    " + System.getProperty("line.separator"));
            //Close the output stream
            out.close();
            }catch (Exception e){//Catch exception if any
            System.err.println("Error: " + e.getMessage());
            }
    }
    
    public static void correctPhonetics(String pathIn, String pathOut)
    {
        List<PhoneticRepresentation> total = getAll(pathIn);
        
        for(PhoneticRepresentation p : total)
        {
            List<String> novaRepresentacao = new ArrayList<String>();
            int a =0;
            if(p.getDescription().equals("camaguey"))
            {
                a++;
            }
            for(String pr : p.getPhoneticRepresentation())
            {                
                pr = pr.replaceAll("gj", "g j");
                pr = pr.replaceAll("gi", "g i");
                pr = pr.replaceAll("dz", "d z");
                pr = pr.replaceAll("ge", "g e");
                pr = pr.replaceAll("u~d", "u~ d");
                pr = pr.replaceAll("i~k", "i~ k");
                pr = pr.replaceAll("i~n", "i~ n");
                pr = pr.replaceAll("Sis", "S i s");
                novaRepresentacao.add(pr);
            }
            
            p.setPhoneticRepresentation(novaRepresentacao);
            insertPhoneticRepresentation(p, pathOut);
        }
        
        
        
    }
    
    
    public static List<PhoneticRepresentation> getAll(String filesPath)
    {        
        File diretorio = new File(filesPath);
        File fList[] = diretorio.listFiles();
        
        List<PhoneticRepresentation> phonetics = new ArrayList<PhoneticRepresentation>();
        
        for(File f : fList)
        {
            SAXBuilder builder = new SAXBuilder();
            Document document;
            try {

                document = builder.build(f.getAbsolutePath());

                Element root = document.getRootElement();

                for(Object obj :  root.getChildren("word"))
                {
                    Element e = (Element)obj;

                    PhoneticRepresentation r = new PhoneticRepresentation();

                    r.setDescription(e.getChild("description").getText());
                    List<String> representations = new ArrayList<String>();
                    for(Object o : e.getChildren("representation"))
                        representations.add(((Element)o).getText());
                    
                   r.setPhoneticRepresentation(representations);

                   phonetics.add(r);
            }

            } catch (JDOMException ex) {
            System.out.println("Erro: " + ex.getMessage());
            } catch (IOException ex) {
            System.out.println("Erro: " + ex.getMessage());
            }
        }
        
        return phonetics;
    }
    
}
