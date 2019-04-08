package br.ufpe.cin.five.core.classification.hts;

import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author Carlos
 */
public class InstantiatedListsMap extends LinkedHashMap<String, List<String>> {

    public InstantiatedListsMap(ItemMap... itens) {
        super();
        for (ItemMap item : itens)
            super.put( item.getKey(), ( List<String> ) item.getValue() );
    }

}
