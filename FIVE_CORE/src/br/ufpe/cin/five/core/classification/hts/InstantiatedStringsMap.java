package br.ufpe.cin.five.core.classification.hts;

import java.util.LinkedHashMap;

/**
 *
 * @author Carlos
 */
public class InstantiatedStringsMap extends LinkedHashMap<String, String> {

    public InstantiatedStringsMap(ItemMap... itens) {
        super();
        for (ItemMap item : itens)
            super.put( item.getKey(), String.valueOf( item.getValue() ) );
    }

}
