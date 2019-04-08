package br.ufpe.cin.five.core.extraction.mgcc;

import br.ufpe.cin.five.core.extraction.ExtractionException;
import br.ufpe.cin.five.core.util.ShellCommandExecutor;
import java.io.File;

/**
 *
 * @author carlos
 */
public class RawConverter{
    
    private String _wavFile;
    private String _rawFile;
    private String COMMAND;
    private MgccParameters mgccParameters;
    private String SOXDIR;

    public RawConverter(String resourcesPath, MgccParameters mgccParameters, String audioFile) {
        this._wavFile = audioFile;
        this._rawFile = _wavFile.replace(".wav", ".raw");
        this.SOXDIR = resourcesPath + File.separator + "sox"+ File.separator;
        this.mgccParameters = mgccParameters;
    }

    public void convert() throws ExtractionException {
        try {
            COMMAND = "sox -c 1 -s -2 -t wav -r " + mgccParameters.getSampfreq() + " " + _wavFile + " -c 1 -s -2 -t raw -r " + mgccParameters.getSampfreq() + " " + _rawFile;
            System.out.println("Comando:" + COMMAND);
            ShellCommandExecutor.execute(COMMAND, _wavFile.substring(0, _wavFile.lastIndexOf(File.separator)));
        } catch (Exception ex) {
            throw new ExtractionException(ex.getMessage());
        }
    }
}
