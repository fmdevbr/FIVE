package br.ufpe.cin.five.core.util;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Carlos
 */
public class PrintStream extends Thread {

    InputStream  _inputStream  = null;
    StringBuffer _resultString = new StringBuffer();

    public PrintStream(InputStream inputStream) {
        _inputStream = inputStream;
    }

    public StringBuffer getResult() {
        return _resultString;
    }

    @Override
    public void run() {
        try {
            while (this != null) {
                int _ch = _inputStream.read();
                if (_ch != -1)
                    _resultString.append((char) _ch);
                else
                    break;
            }
        }catch(IOException e) {
            System.out.println ( e.getMessage() );
        }
    }
}