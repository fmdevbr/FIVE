/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved. Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.utterance.nlp;

import java.math.BigInteger;

/**
 * @author Luiz Angelo Daros de Luca
 * @version 1.0
 *
 * Esta classe converte numero de 0 ateh 10^55 -1
 * por extenso para o Português do Brasil. Para o português de Portugal,
 * deve-se mudar os valores em ordensSingular e ordensPlural a partir de "Bilh(ão|ões)"
 *
 * Caso encontrem erros, email-me por favor:
 *
 * luizd@inf.ufsc.br
 *
 * Adaptações: Carlos Souza
 */
public class NumberToWord {

    private static final String ZERO = "zero";
    private static final String[] UNITS = {
        "", "um", "dois", "três", "quatro", "cinco", "seis", "sete", "oito", "nove",
        "dez", "onze", "doze", "treze", "quatorze", "quinze", "dezesseis", "dezessete", "dezoito", "dezenove"
    };
    private static final String[] TENS = {
        "", "", "vinte", "trinta", "quarenta", "cinquenta", "sessenta", "setenta", "oitenta", "noventa", "cem"
    };
    private static final String[] HUNDREDS = {
        "", "cento", "duzentos", "trezentos", "quatrocentos", "quinhentos", "seiscentos", "setecentos", "oitocentos", "novecentos"
    };
    private static final String SMALLER_SEPARATOR = " e ";
    private static final String BIGGER_SEPARATOR = ", ";
    private static final String[] SINGULAR_ORDERS = {
        "", "mil", "milhão", "bilhão", "trilhão", "quatrilhão", "quintilhão", "sextilhão", "setilhão", "octilhão", "nonilhão",
        "decilhão", "undecilhão", "dodecilhão", "tredecilhão", "quatordecilhão", "quindecilhão", "sedecilhão", "septendecilhão"
    };
    private static final String[] PLURAL_ORDERS = {
        "", "mil", "milhões", "bilhões", "trilhões", "quatrilhões", "quintilhões", "sextilhões", "setilhões", "octilhões",
        "decilhões", "undecilhões", "dodecilhões", "tredecilhões", "quatordecilhões", "quindecilhões", "sedecilhões", "septendecilhões"
    };
    private static final BigInteger HUNDRED = BigInteger.valueOf(1000);
    private static final BigInteger MAX_NUMBER = new BigInteger("999999999999999999999999999999999999999999999999999999");

    private static String units(int numero) {
        if (numero == 0) {
            return "";
        }
        return SMALLER_SEPARATOR + UNITS[numero];
    }

    private static String tens(int number) {
        if (number == 0) {
            return "";
        }
        if (number < 20) {
            return units(number);
        }
        return SMALLER_SEPARATOR + TENS[number / 10] + units(number % 10);
    }

    private static String hundreds(int number) {
        if (number == 0) {
            return "";
        }
        if (number <= 100) {
            return tens(number);
        }
        return BIGGER_SEPARATOR + HUNDREDS[number / 100] + tens(number % 100);
    }

    private static String orders(long number, int magnitude) {
        if (number == 0) {
            return "";
        }
        if (number < 1000) {
            return hundreds((int) number);
        }

        int smaller = (int) (number % 1000);
        long bigger = number / 1000;
        int smallerNext = (int) (bigger % 1000);

        if (smallerNext == 0) {
            return orders(bigger, magnitude + 1) + hundreds(smaller);
        }
        if (smallerNext == 1) {
            return orders(bigger, magnitude + 1) + " " + SINGULAR_ORDERS[magnitude] + hundreds(smaller);
        }
        return orders(bigger, magnitude + 1) + " " + PLURAL_ORDERS[magnitude] + hundreds(smaller);
    }

    private static String orders(BigInteger number, int magnitude) {
        if (number.equals(BigInteger.ZERO)) {
            return "";
        }
        if (number.compareTo(HUNDRED) == -1) {
            return hundreds((int) number.longValue());
        }

        BigInteger[] result = number.divideAndRemainder(HUNDRED);

        int smaller = (int) result[1].longValue();
        BigInteger bigger = result[0];
        int smallerNext = (int) bigger.remainder(HUNDRED).longValue();

        if (smallerNext == 0) {
            return orders(bigger, magnitude + 1) + hundreds(smaller);
        }
        if (smallerNext == 1) {
            return orders(bigger, magnitude + 1) + " " + SINGULAR_ORDERS[magnitude] + hundreds(smaller);
        }
        return orders(bigger, magnitude + 1) + " " + PLURAL_ORDERS[magnitude] + hundreds(smaller);
    }

    public static String getExtenseText(long number) {
        if (number == 0) {
            return ZERO;
        }
        return orders(number, 1).substring(2);
    }

    public static String getExtenseText(BigInteger number) {
        if (number.equals(BigInteger.ZERO)) {
            return ZERO;
        }
        if (MAX_NUMBER.compareTo(number) < 0) {
            throw new RuntimeException("Number out of allowed!");
        }
        return orders(number, 1).substring(2);
    }
}
