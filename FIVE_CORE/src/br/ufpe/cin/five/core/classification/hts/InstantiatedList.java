package br.ufpe.cin.five.core.classification.hts;

import java.util.ArrayList;

/**
 *
 * @author Carlos
 */
public class InstantiatedList extends ArrayList<String> {

    public InstantiatedList( String[] letters ) {
        super();
        for (int i = 0; i < letters.length; i++)
            super.add( letters[i] );
    }

}