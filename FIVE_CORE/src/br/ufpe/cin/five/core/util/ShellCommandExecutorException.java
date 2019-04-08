/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufpe.cin.five.core.util;

/**
 *
 * @author Vocallab
 */
public class ShellCommandExecutorException extends Exception {

    public ShellCommandExecutorException(String message) {
        super("ShellCommandExecutorException: " + message);
    }
}
