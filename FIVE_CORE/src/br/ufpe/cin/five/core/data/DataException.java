/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufpe.cin.five.core.data;

public class DataException extends RuntimeException {

    public DataException() {
    }

    public DataException(String message) {
        super(message);
    }

    public DataException(Throwable cause) {
        super(cause);
    }

    public DataException(String message, Throwable cause) {
        super(message, cause);
    }
}