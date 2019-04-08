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
 * @author Carlos
 */
public class GrammaticalClassification {

    private static final String ARTICLES     = "o|a|os|as|um|uma|uns|umas";

    private static final String PRONOUMS     = "meu|minha|meus|minhas|teu|tua|teus|tuas|seu|sua|seus|suas|nosso|nossa|nossos|nossas|vosso|vossa|vossos|vossas|seu|sua|seus|suas|algo|alguém|nada|ninguém|tudo|cada|outrem|quem|mais|menos|todo|toda|um|uma|algum|alguma|nenhum|nenhuma|certo|certa|muito|muita|outro|outra|pouco|pouca|tanto|tanta|qualquer|quaisquer|cada|qual|quais|cujo|cuja|cujos|cujas|quanto|quanta|quantas|quantos|onde|este|estes|esse|esses|aquele|aqueles|ele|eles|dele|deles|nele|neles|esta|estas|essa|essas|aquela|aquelas|ela|elas|dela|delas|nela|nelas";

    private static final String NUMBERS      = "zero|um|dois|três|quatro|cinco|seis|sete|oito|nove|dez|onze|doze|treze|quatorze|quinze|dezesseis|dezessete|dezoito|dezenove|vinte|trinta|quarenta|cinqüenta|sessenta|setenta|oitenta|noventa|cem|cento|duzentos|trezentos|quatrocentos|quinhentos|seiscentos|setecentos|oitocentos|novecentos|mil|milhão|bilhão|trilhão|quatrilhão|quintilhão|sextilhão|setilhão|octilhão|nonilhão|decilhão|undecilhão|dodecilhão|tredecilhão|quatordecilhão|quindecilhão|sedecilhão|septendecilhão|mil|milhões|bilhões|trilhões|quatrilhões|quintilhões|sextilhões|setilhões|octilhões|decilhões|undecilhões|dodecilhões|tredecilhões|quatordecilhões|quindecilhões|sedecilhões|septendecilhões";

    private static final String CONJUNCTION  = "e|nem|mas|também|como|quanto|porém|todavia|contudo|entretanto|entanto|obstante|ou|ora|já|que|seja|porque|porquanto|pois|logo|portanto|então|se|por|quanto|como|por|isso|vez|visto|apesar|disso|quer|tal|qual|assim|bem|nem|embora|conquanto|ainda|mesmo|caso|quando|contanto|desde|conforme|tão|apenas|mal";

    private static final String PREPOSITION  = "a|ante|após|até|com|contra|de|desde|em|entre|para|pra|per|perante|por|sem|sob|sobre|trás|da|do|das|dos|graça|dentro|perto|junto|neste|à";

    private static final String INTERJECTION = "oba|viva|oh|ah|uhu|eh|gol|bom|oi|olá|salve|adeus|viva|alô|ufa|uf|ah|bem|arre|coragem|avante|firme|vamos|eia|bravo|bis|viva|tomara|oxalá|oh|pudera|ai|ui|oh|lala|hum|hem|raios|diabo|puxa|pô|alô|olá|psiu|socorro|ei|eh|credo|cruzes|uh|ui|silêncio|alto|basta|chega|quietos|yes|ok|arreda|fora|passa|sai|roda|rua|toca|xô|oh|ah|olá|olé|eta|eia|alerta|cuidado|alto|lá|calma|olha|fogo|puxa|ufa|arre|também|coragem|eia|avante|upa|vamos|alô|olá|ó|bis|bem|bravo|viva|apoiado|fiufiu|hup|hurra|isso|muito bem|parabéns|graças|obrigado|obrigada|agradecido|alô|hei|olá|psiu|pst|socorro|ânimo|adiante|avante|eia|coragem|firme|força|toca|upa|vamos|perdão|oh|oxalá|tomara|pudera|queira|dera|adeus|até|logo|bai-bai|tchau|ai|ui|ai|hum|hem|basta|para|alô|ô|olá|uai|hi|ali|ué|ih|oh|poxa|quê|caramba|nossa|opa|virgem|xi|terremoto|barrabás|barbaridade|arre|hum|puxa|raios|ave|olá|ora viva|salve|viva|adeus|ah|oh|psiu|silêncio|caluda|psiu|psit|alto|alto|lá|credo|cruzes|medo|uh|ui|fogo|barbaridade|hei";

    public static String getWordClassification(String word) {
        
        if ( Pattern.compile("^("+NUMBERS+")$").matcher(word).find() )
            return "function";

        else if ( Pattern.compile("^("+ARTICLES+")$").matcher(word).find() )
            return "function";

        else if ( Pattern.compile("^("+PREPOSITION+")$").matcher(word).find() )
            return "function";

        else if ( Pattern.compile("^("+PRONOUMS+")$").matcher(word).find() )
            return "function";

        else if ( Pattern.compile("^("+CONJUNCTION+")$").matcher(word).find() )
            return "function";

        else if ( Pattern.compile("^("+INTERJECTION+")$").matcher(word).find() )
            return "function";

        return "content";
    }

}