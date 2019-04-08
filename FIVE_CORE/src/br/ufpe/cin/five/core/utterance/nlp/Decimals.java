/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved. Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.core.utterance.nlp;

/**
 *
 * @author tacio
 */
public class Decimals {

    public static String[] splitIfDecimal(String number) {

        String[] splitNumber = new String[2];
        String[] orderedSplitNumber = new String[3];
        boolean isDecimal = false;
        String[] separador = {"vírgula", "ponto"};

        if (number.contains(".") & number.contains(",") & number.lastIndexOf(".") < number.lastIndexOf(",")) {
            splitNumber = number.split(",");
            orderedSplitNumber[0] = splitNumber[0];
            orderedSplitNumber[1] = separador[0];
            orderedSplitNumber[2] = splitNumber[1];
        } else if (number.contains(".") & number.contains(",") & number.lastIndexOf(".") > number.lastIndexOf(",")) {
            splitNumber = number.split("\\.");
            orderedSplitNumber[0] = splitNumber[0];
            orderedSplitNumber[1] = separador[1];
            orderedSplitNumber[2] = splitNumber[1];
        } else if (number.contains(".") & !number.contains(",") & number.indexOf(".") == number.lastIndexOf(".")) {
            if (number.substring(number.lastIndexOf(".")).length() <= 3) {
                isDecimal = true;
                splitNumber = number.split("\\.");
                orderedSplitNumber[0] = splitNumber[0];
                orderedSplitNumber[1] = separador[1];
                orderedSplitNumber[2] = splitNumber[1];
            }
        } else if (!number.contains(".") & number.contains(",") & number.indexOf(",") == number.lastIndexOf(",")) {
            if (number.substring(number.lastIndexOf(",")).length() <= 3) {
                isDecimal = true;
                splitNumber = number.split(",");
                orderedSplitNumber[0] = splitNumber[0];
                orderedSplitNumber[1] = separador[0];
                orderedSplitNumber[2] = splitNumber[1];
            }
        } else {
            isDecimal = false;
            number = number.replaceAll("[,\\.]", "");
        }

        if (isDecimal) {
            Long a = Long.valueOf(orderedSplitNumber[0]).longValue();
            orderedSplitNumber[0] = NumberToWord.getExtenseText(a);
            Long b = Long.valueOf(orderedSplitNumber[2]).longValue();
            orderedSplitNumber[2] = NumberToWord.getExtenseText(b);
        } else {
            Long a = Long.valueOf(number).longValue();
            orderedSplitNumber[0] = NumberToWord.getExtenseText(a);
        }
        return orderedSplitNumber;
    }
}
