/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved. Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.utterance.nlp;

import br.ufpe.cin.five.core.speaker.Gender;

/**
 *
 * @author Alexandre
 */
public class Ordinal {

    private static String ZERO = "";

    private static String[] unidadesMasculino = {
            "", "primeiro", "segundo", "terceiro", "quarto", "quinto", "sexto", "sétimo", "oitavo", "nono",
    };

    private static String[] unidadesFeminino = {
            "", "primeira", "segunda", "terceira", "quarta", "quinta", "sexta", "sétima", "oitava", "nona",
    };

    private static String[] dezenasMasculino = {
            "","décimo", "vigésimo", "trigésimo", "quadragésimo", "quinquagésimo", "sexagésimo", "septuagésimo", "octagésimo", "nonagésimo",
    };

    private static String[] dezenasFeminino = {
            "","décima", "vigésima", "trigésima", "quadragésima", "quinquagésima", "sexagésima", "septuagésima", "octagésima", "nonagésima",
    };

    private static String[] centenasMasculino = {
            "", "centésimo", "ducentésimo", "tricentésimo", "quadringentésimo", "quingentésimo", "sexcentésimo", "septingentésimo", "octingentésimo", "noningentésimo"
    };

    private static String[] centenasFeminino = {
            "", "centésima", "ducentésima", "tricentésima", "quadringentésima", "quingentésima", "sexcentésima", "septingentésima", "octingentésima", "noningentésima"
    };

    private static String SEPARADOR_MENOR = " ";
    private static String SEPARADOR_MAIOR   = " "; //2 espacos para ficar igual ao " e "

    private static String[] milharesMasculino = {
            "", "milésimo"
    };

    private static String[] milharesFeminino = {
            "", "milésima"
    };

    private static long CEM = 1000;
    private static long NUMERO_MAXIMO = 999999999;


    private static String unidades(int numero, Gender gender) {
            if (numero == 0)
                    return "";
            if(gender == Gender.MASCULINO){
                return SEPARADOR_MENOR + unidadesMasculino[numero];
            } else {
                return SEPARADOR_MENOR + unidadesFeminino[numero];
            }
    }

    private static String dezenas(int numero, Gender gender) {
            if (numero == 0)
                    return "";
            if (numero < 10)
                    return unidades(numero, gender);
            if(gender == Gender.MASCULINO){
                return SEPARADOR_MENOR + dezenasMasculino[numero / 10] + unidades(numero % 10, gender);
            } else {
                return SEPARADOR_MENOR + dezenasFeminino[numero / 10] + unidades(numero % 10, gender);
            }
    }

    private static String centenas(int numero, Gender gender) {
            if (numero == 0)
                    return "";
            if (numero < 100)
                    return dezenas(numero, gender);
            if(gender == Gender.MASCULINO){
                return SEPARADOR_MENOR + centenasMasculino[numero / 100] + dezenas(numero % 100, gender);
            } else {
                return SEPARADOR_MENOR + centenasFeminino[numero / 100] + dezenas(numero % 100, gender);
            }
    }

    private static String milhares(int numero, Gender gender) {
            int centena = (int)(numero % 1000);
            long milhar  = numero / 1000;
            if(gender == Gender.MASCULINO){
                if(milhar != 1){
                    return NumberToWord.getExtenseText(milhar) + SEPARADOR_MENOR + milharesMasculino[1] + centenas(centena, gender);
                } else {
                    return milharesMasculino[1] + centenas(centena, gender);
                }
            } else {
                if(milhar != 1){
                    return NumberToWord.getExtenseText(milhar) + SEPARADOR_MENOR + milharesFeminino[1] + centenas(centena, gender);
                } else {
                    return milharesFeminino[1] + centenas(centena, gender);
                }
            }
    }

    private static String ordens(long numero, Gender gender) {
            if (numero == 0)
                    return "";
            if (numero < 1000)
                    return centenas ((int)numero, gender);
            return milhares((int)numero, gender);
    }

    
    public static String converte(long numero, Gender gender) {
            if (numero == 0)
                    return ZERO;
            return ordens(numero, gender); // tira o espaco extra
    }

}
