/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufpe.cin.five.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Alexandre
 */
    
public class CharFilter {  
  
   public static String[] REPLACES = { "a", "e", "i", "o", "u", "c" };  
  
   public static Pattern[] PATTERNS = null;  
     
   public static void compilePatterns() {  
      PATTERNS = new Pattern[REPLACES.length];  
      PATTERNS[0] = Pattern.compile("[âãáàä]", Pattern.CASE_INSENSITIVE);  
      PATTERNS[1] = Pattern.compile("[éèêë]", Pattern.CASE_INSENSITIVE);  
      PATTERNS[2] = Pattern.compile("[íìîï]", Pattern.CASE_INSENSITIVE);  
      PATTERNS[3] = Pattern.compile("[óòôõö]", Pattern.CASE_INSENSITIVE);  
      PATTERNS[4] = Pattern.compile("[úùûü]", Pattern.CASE_INSENSITIVE);  
      PATTERNS[5] = Pattern.compile("[ç]", Pattern.CASE_INSENSITIVE);  
   }  
  
   public static String replaceSpecial(String text) {  
      if(PATTERNS == null){  
         compilePatterns();  
      }  
  
      String result = text;  
      for (int i = 0; i < PATTERNS.length; i++) {  
         Matcher matcher = PATTERNS[i].matcher(result);  
         result = matcher.replaceAll(REPLACES[i]);  
      }  
      return result;  
   }  
  
} 
