/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved. Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.utterance.nlp;

import java.util.regex.Pattern;

/**
 *
 * @author tacio
 */
public class ExtensoHoras {

    private static final String ZERO = "zero";
    private static final String[] HORAS = {"hora","horas"};
    private static final String[] MINUTOS = {""," minuto"," minutos"};
    private static final String SEPARADOR = "e";

    private static String[] horas = {ZERO,"uma", "duas", "três", "quatro", "cinco", "seis", "sete", "oito", "nove",
    "dez", "onze", "doze", "treze", "quatorze", "quinze", "dezesseis", "dezessete", "dezoito", "dezenove",
    "vinte", "vinte e uma", "vinte e duas", "vinte e três","vinte e quatro"};

    public static String convertTimetoLiteral(String time){
        
        
        int hours = 0;
        Long minutes = 0l;
        String complement = "";
        
        if(time.contains("h"))        
            time = time.replaceAll(Pattern.quote("h"), "");            

        
        String[] splitTime = splitTime(time);
        hours = getHour(splitTime);
        minutes = getMinutes(splitTime);
        if(existsComplement(splitTime)){
            complement = getComplement(splitTime, time);
        }
            
        
            
        String timeLiteral = "";
        
        
       
            
            
        String hourLiteral = HORAS[1];
        if(hours == 1){
            hourLiteral = HORAS[0];
        }

        String minuteLiteral = MINUTOS[2];
        if (minutes == 1) {
            minuteLiteral = MINUTOS[1];
        } else if (minutes == 0){
            minuteLiteral = MINUTOS[0];
        }

        if(!complement.equals("")){

             if(minuteLiteral.equals(MINUTOS[0])){
                 timeLiteral = horas[hours] + " " + hourLiteral + " " + complement;
             } else {
                 timeLiteral = horas[hours] + " " + hourLiteral + " " + SEPARADOR + " " + NumberToWord.getExtenseText(minutes) + minuteLiteral + complement;
                 
             }

        } else if(complement.equals("")){

            if(minuteLiteral.equals(MINUTOS[0])){
                timeLiteral = horas[hours] + " " + hourLiteral;
            } else {
                timeLiteral = horas[hours] + " " + hourLiteral + " " + SEPARADOR + "" + NumberToWord.getExtenseText(minutes) + minuteLiteral;
            }

        } 
        
        return timeLiteral;
    }

    private static int getHour(String[] splitTime){
        int minutes = (Integer.valueOf(splitTime[0].trim()) ).intValue();
        return minutes;
    }

    private static Long getMinutes(String[] splitTime){
        Long minutes=0l;
        
        if(splitTime.length>1)
            minutes = (Long.valueOf(splitTime[1].substring(0, 2)) ).longValue();
        
        return minutes;
    }

    private static String[] splitTime(String time){

        String[] splitTimeArray;
        
        if(time.contains(":"))
            splitTimeArray = time.split(":");
        else            
                splitTimeArray = time.split(" ");
        
        return splitTimeArray;        
    }

    private static boolean existsComplement(String[] splitTime){
        boolean exists = true;
        if(splitTime.length>1)
        {
            if (splitTime[1].length() > 2) {
                return exists;
            } else {
                return !exists;
            }
        }else
            return !exists;
    }

    private static String getComplement(String[] splitTime, String time){
        String complement;
        complement = splitTime[1].substring(2).trim();

        if (complement.equalsIgnoreCase("am")) {
            if (getHour(splitTime(time)) >=6) {
                return complement = "da manhã";
            } else {
                return complement = "da madrugada";
            }
        }

        if(complement.equalsIgnoreCase("pm")){
            if(getHour(splitTime(time)) >= 6){
                return complement = "da noite";
            } else {
                return complement = "da tarde";
            }
        }

        return complement;
    }

}