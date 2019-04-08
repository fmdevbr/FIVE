/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved. Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.project;

import br.ufpe.cin.five.core.data.DriverUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * This is an abstract class that specifies and implements the main methods necessary for the project process.
 */
public abstract class ProjectProcess {         
            
    /**
     * Creates a projet config file and folder structure of a Project specifying the Project attributes.
     * @param project
     */     
    public static void create(Project project) throws ProjectException {
        
        try {                                    
            //CRIA PASTA RAIZ DO PROJETO
            String projectPath = project.getDirectory();            
            ProjectUtil.createFolder(projectPath);                                    
            
            //CRIA ARQUIVO .XML DO PROJETO NA PASTA RAIZ                        
            File projectXmlFile = new File(projectPath, (project.getName() + ".xml"));
            
            Element projectElement = new Element("project");
            projectElement.addContent(new Element("name").addContent(project.getName()));
            projectElement.addContent(new Element("type").addContent(project.getType().toString()));
            projectElement.addContent(new Element("directory").addContent(project.getDirectory())); 
            
            Element databaseElement = new Element("database");
            
            Element urlElement = new Element("url");
            urlElement.addContent(project.getDbUrl());
            databaseElement.addContent(urlElement);
            Element userElement = new Element("username");
            userElement.addContent(project.getDbLogin());
            databaseElement.addContent(userElement);
            Element passwordElement = new Element("password");
            passwordElement.addContent(project.getDbPassword());            
            databaseElement.addContent(passwordElement);
            Element driverElement = new Element("driver");
            driverElement.addContent(project.getDbDriver().toString());            
            databaseElement.addContent(driverElement);
            
            projectElement.addContent(databaseElement);
            
            Document projectDocument = new Document(projectElement);
            XMLOutputter projetctOutputter = new XMLOutputter(Format.getPrettyFormat());
            FileWriter projectXmlWriter = new FileWriter(projectXmlFile);
            projetctOutputter.output(projectDocument, projectXmlWriter);
            projectXmlWriter.close();                                    

            //CRIA A PASTA ENGINE ABAIXO DA RAIZ
            ProjectUtil.createFolder(projectPath + File.separator + "engines");

            //CRIA A PASTA FEATURES ABAIXO DA RAIZ
            ProjectUtil.createFolder(projectPath + File.separator + "features");

            //CRIA A PASTA MODELS ABAIXO DA RAIZ
            ProjectUtil.createFolder(projectPath + File.separator + "models");
            
            //CRIA A PASTA RESOURCES ABAIXO DA RAIZ
            ProjectUtil.copyDirectory(System.getProperty("user.dir") + File.separator + "resources", projectPath + File.separator + "resources");                         
                        
            //CRIA A PASTA RESULTS ABAIXO DA RAIZ
            ProjectUtil.createFolder(projectPath + File.separator + "results");
            
            //CRIA A PASTA SAMPLES ABAIXO DA RAIZ
            ProjectUtil.createFolder(projectPath + File.separator + "samples");                       
            
        } catch (Exception ex) {
            throw new ProjectException(ex.getMessage());
        }
    }  
    
    /**
     * Open a projet from a specific config file.
     * @param project
     */     
    public static Project open(File projectFile) throws ProjectException {    
        
        Project project = new Project();
        
        try {
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(projectFile);
            Element root = document.getRootElement();           
            
            project.setName(root.getChild("name").getText());
            project.setType(ProjectUtil.getProjectType(root.getChild("type").getText()));
            project.setDirectory(root.getChild("directory").getText());                                    
            project.setDbUrl(root.getChild("database").getChild("url").getText());
            project.setDbLogin(root.getChild("database").getChild("username").getText());
            project.setDbPassword(root.getChild("database").getChild("password").getText());
            project.setDbDriver(DriverUtils.getDriver(root.getChild("database").getChild("driver").getText()));                        
        
        } catch (Exception ex){
        
        }        
        return project;
    }
    
}
