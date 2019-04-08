/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.util;

import java.io.IOException;
import java.io.OutputStream;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;

/**
 * Output Stream que escreve para um javax.swing.text.Document
 * Dessa forma pode servir de controler de uma JTextArea
 */
public class DocumentOutputStream extends OutputStream {

    /** Documento do JTextArea */
    private Document _document;
    /** Buffer com os bytes adicionados */
    private StringBuffer _buffer;
    /** localização do último byte inserido no documento */
    private int _offset;

    public DocumentOutputStream(Document document) {
        _document = document;
    }

    @Override
    public void write(int b) throws IOException {

        try {
            _document.insertString(_offset++, (char) b + "", new SimpleAttributeSet());
        } catch (BadLocationException ex) {
            new IOException(ex);
        }

    }

    @Override
    public void write(byte[] bytes, int off, int len) throws IOException {
        StringBuffer buffer = new StringBuffer();
        for (int indice = off; indice < len; indice++) {
            buffer.append((char) bytes[indice]);
        }
        try {
            _document.insertString(_offset, buffer.toString(), new SimpleAttributeSet());
            _offset += buffer.length();
        } catch (BadLocationException ex) {
            new IOException(ex);
        }
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        StringBuffer buffer = new StringBuffer();
        for (byte b : bytes) {
            buffer.append((char) b);
        }
        try {
            _document.insertString(_offset, buffer.toString(), new SimpleAttributeSet());
            _offset += buffer.length();
        } catch (BadLocationException ex) {
            new IOException(ex);
        }
    }
}
