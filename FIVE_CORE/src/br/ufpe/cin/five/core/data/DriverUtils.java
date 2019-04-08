/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufpe.cin.five.core.data;

import org.hibernate.cfg.Configuration;

/**
 *
 * @author vocallab
 */
public class DriverUtils {
        
 public static DriverType getDriver(String driver)
 {
     if(driver.equals("Mysql")) return DriverType.Mysql;
     if(driver.equals("Postgres")) return DriverType.Postgres;
     else  return null;          
    
 }
 
 
 public static String getConnectionURL(String url, DriverType driver)
 {
     switch(driver)
     {
         case Mysql:
             return "jdbc:mysql://"+url;
         case Postgres:
             return "jdbc:postgres://"+url;
     }
     
     return "";
 }
 
 public static Configuration getConfiguration(String url, String login, String password, DriverType driver)
 {
     Configuration configuration = new Configuration();
     configuration.configure("/br/ufpe/cin/five/core/data/hibernate.cfg.xml");

     switch(driver)
     {
         case Mysql:
             configuration.setProperty("hibernate.connection.url", url+"?createDatabaseIfNotExist=true&connectionCollation=latin1_bin");
             configuration.setProperty("hibernate.connection.username", login);
             configuration.setProperty("hibernate.connection.password", password); 
             configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect"); 
             configuration.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver"); 
             break;
         case Postgres:
             configuration.setProperty("hibernate.connection.url", url+"");
             configuration.setProperty("hibernate.connection.username", login);
             configuration.setProperty("hibernate.connection.password", password); 
             configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect"); 
             configuration.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver"); 
             break;
     }
     
     return configuration;
 }
  
 
}
