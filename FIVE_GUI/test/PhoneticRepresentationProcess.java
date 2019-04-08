/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.*;
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

    private static final String xmlLocation = "/home/alexandre/Desktop/svn/trunk/products/five/development/FIVE_GUI/resources/dictionary";
    private static File aFile = new File(xmlLocation + File.separator + "a.dic");
    private static File áFile = new File(xmlLocation + File.separator + "á.dic");
    private static File àFile = new File(xmlLocation + File.separator + "à.dic");
    private static File âFile = new File(xmlLocation + File.separator + "â.dic");
    private static File ãFile = new File(xmlLocation + File.separator + "ã.dic");
    private static File bFile = new File(xmlLocation + File.separator + "b.dic");
    private static File cFile = new File(xmlLocation + File.separator + "c.dic");
    private static File dFile = new File(xmlLocation + File.separator + "d.dic");
    private static File eFile = new File(xmlLocation + File.separator + "e.dic");
    private static File éFile = new File(xmlLocation + File.separator + "é.dic");
    private static File êFile = new File(xmlLocation + File.separator + "ê.dic");
    private static File fFile = new File(xmlLocation + File.separator + "f.dic");
    private static File gFile = new File(xmlLocation + File.separator + "g.dic");
    private static File hFile = new File(xmlLocation + File.separator + "h.dic");
    private static File iFile = new File(xmlLocation + File.separator + "i.dic");
    private static File íFile = new File(xmlLocation + File.separator + "í.dic");
    private static File jFile = new File(xmlLocation + File.separator + "j.dic");
    private static File kFile = new File(xmlLocation + File.separator + "k.dic");
    private static File lFile = new File(xmlLocation + File.separator + "l.dic");
    private static File mFile = new File(xmlLocation + File.separator + "m.dic");
    private static File nFile = new File(xmlLocation + File.separator + "n.dic");
    private static File oFile = new File(xmlLocation + File.separator + "o.dic");
    private static File óFile = new File(xmlLocation + File.separator + "ó.dic");
    private static File ôFile = new File(xmlLocation + File.separator + "ô.dic");
    private static File pFile = new File(xmlLocation + File.separator + "p.dic");
    private static File qFile = new File(xmlLocation + File.separator + "q.dic");
    private static File rFile = new File(xmlLocation + File.separator + "r.dic");
    private static File sFile = new File(xmlLocation + File.separator + "s.dic");
    private static File tFile = new File(xmlLocation + File.separator + "t.dic");
    private static File uFile = new File(xmlLocation + File.separator + "u.dic");
    private static File úFile = new File(xmlLocation + File.separator + "ú.dic");
    private static File vFile = new File(xmlLocation + File.separator + "v.dic");
    private static File wFile = new File(xmlLocation + File.separator + "w.dic");
    private static File xFile = new File(xmlLocation + File.separator + "x.dic");
    private static File yFile = new File(xmlLocation + File.separator + "y.dic");
    private static File zFile = new File(xmlLocation + File.separator + "z.dic");

    public static void main(String[] args) {

        String dictionary = "/home/alexandre/Desktop/list_westpoint1.txt";

        try {
            File dictionaryFile = new File(dictionary);
            FileReader reader = new FileReader(dictionaryFile);
            BufferedReader buffer = new BufferedReader(reader);

            while (buffer.ready()) {
                String[] splittedLine = buffer.readLine().split("\t");

                PhoneticRepresentation p = new PhoneticRepresentation();

                p.setDescription(splittedLine[0]);
                p.setPhoneticRepresentation(splittedLine[1]);

                switch (splittedLine[0].charAt(0)) {
                    case 'a':
                        insert(p, aFile);
                        break;
                    case 'á':
                        insert(p,áFile);
                        break;
                    case 'à':
                        insert(p,àFile);
                        break;
                    case 'â':
                        insert(p,âFile);
                        break;
                    case 'ã':
                        insert(p,ãFile);
                        break;
                    case 'b':
                        insert(p,bFile);
                        break;
                    case 'c':
                        insert(p,cFile);
                        break;
                    case 'd':
                        insert(p,dFile);
                        break;
                    case 'e':
                        insert(p,eFile);
                        break;
                    case 'é':
                        insert(p,éFile);
                        break;
                    case 'ê':
                        insert(p,êFile);
                        break;
                    case 'f':
                        insert(p,fFile);
                        break;
                    case 'g':
                        insert(p,gFile);
                        break;
                    case 'h':
                        insert(p,hFile);
                        break;
                    case 'i':
                        insert(p,iFile);
                        break;
                    case 'í':
                        insert(p,íFile);
                        break;
                    case 'j':
                        insert(p,jFile);
                        break;
                    case 'k':
                        insert(p,kFile);
                        break;
                    case 'l':
                        insert(p,lFile);
                        break;
                    case 'm':
                        insert(p,mFile);
                        break;
                    case 'n':
                        insert(p,nFile);
                        break;
                    case 'o':
                        insert(p,oFile);
                        break;
                    case 'ó':
                        insert(p,óFile);
                        break;
                    case 'ô':
                        insert(p,ôFile);
                        break;
                    case 'p':
                        insert(p,pFile);
                        break;
                    case 'q':
                        insert(p,qFile);
                        break;
                    case 'r':
                        insert(p,rFile);
                        break;
                    case 's':
                        insert(p,sFile);
                        break;
                    case 't':
                        insert(p,tFile);
                        break;
                    case 'u':
                        insert(p,uFile);
                        break;
                    case 'ú':
                        insert(p,úFile);
                        break;
                    case 'v':
                        insert(p,vFile);
                        break;
                    case 'w':
                        insert(p,wFile);
                        break;
                    case 'y':
                        insert(p,yFile);
                        break;
                    case 'x':
                        insert(p,xFile);
                        break;
                    case 'z':
                        insert(p,zFile);
                        break;
                }
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void insert(PhoneticRepresentation phonetic, File xmlFile) {

        try {

            SAXBuilder builder = new SAXBuilder();

            Document doc = (Document) builder.build(xmlFile);
            Element rootNode = doc.getRootElement();

            Element word = new Element("word").addContent(new Element("description").addContent(phonetic.getDescription()));
            word.addContent(new Element("representation").addContent(phonetic.getPhoneticRepresentation()));

            rootNode.addContent(word);

            XMLOutputter xmlOutput = new XMLOutputter();

            // display nice nice
            xmlOutput.setFormat(Format.getPrettyFormat());
            xmlOutput.output(doc, new FileWriter(xmlFile));

            // xmlOutput.output(doc, System.out);

        } catch (JDOMException ex) {
            System.out.println("Erro: " + ex.getMessage() + "\n " + ex.getStackTrace().toString());
        } catch (IOException io) {
            System.out.println("Erro: " + io.getMessage() + "\n " + io.getStackTrace().toString());
        }

    }
    
}
