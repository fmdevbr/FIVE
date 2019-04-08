/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.gui.dialogs;

import javax.swing.SwingWorker;

/**
 *
 * @author Alexandre
 */
public abstract class FiveWorker<T,V> extends SwingWorker<T, V> {

    private Exception exception;

    public void setException(Exception exception){
        this.exception = exception;
    }

    public Exception getException(){
        return this.exception;
    }

    public void publish(V text) {
        super.publish(text);
    }

    public void incProgress(int progress) {
        this.setProgress(progress);
    }

    @Override
    /**
     * Necessário sobrescrever o metodo done para que a exception seja capturada
     * e assim poder ser tratada.
     * Também imprime o erro no output.
     */
    protected void done(){
        try{
            get();
        }catch(Exception e){
            System.out.println("## Ocorreu um erro: "+e.getMessage());
            this.setException(e);
        }
    }

}
