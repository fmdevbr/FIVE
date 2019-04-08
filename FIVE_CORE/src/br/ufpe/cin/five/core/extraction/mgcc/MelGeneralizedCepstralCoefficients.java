package br.ufpe.cin.five.core.extraction.mgcc;

import br.ufpe.cin.five.core.extraction.ExtractionException;
import br.ufpe.cin.five.core.project.ProjectUtil;
import br.ufpe.cin.five.core.util.ShellCommandExecutor;
import java.io.File;

/**
 *
 * @author carlos
 */
public class MelGeneralizedCepstralCoefficients {

    private String COMMAND;
    private File TEMPDIR;
    private File MGCDIR;
    private String SPTKDIR;
    private MgccParameters mgccParameters;
    private String _audioFile;
    private String _mgcFile;

    public MelGeneralizedCepstralCoefficients(File MGCDIR, String resourcesPath, String extractionPath, MgccParameters mgccParameters, String audioFile) {
        this.TEMPDIR = new File(resourcesPath + File.separator + "temp");
        this.MGCDIR = MGCDIR;
        //ProjectUtil.checkExists(this.TEMPDIR, this.MGCDIR);
        this.SPTKDIR = resourcesPath + File.separator + "sptk" + File.separator;
        this.mgccParameters = mgccParameters;
        this._audioFile = audioFile;
        this._mgcFile = MGCDIR + File.separator + _audioFile.substring(_audioFile.lastIndexOf(File.separator) + 1, _audioFile.length() - 4) + ".mgc";
    }

    public void calculate() throws ExtractionException {
        try {
            if (mgccParameters.getByteswap() == 1) {
                COMMAND = SPTKDIR + "swab +s " + _audioFile + " | " + SPTKDIR + "x2x +sf > " + TEMPDIR + File.separator + "mgc_x2x";
                if (!ShellCommandExecutor.execute(COMMAND, TEMPDIR)) {
                    throw new Exception("Error in MGC!");
                }
            } else {
                COMMAND = SPTKDIR + "x2x +sf " + _audioFile + " > " + TEMPDIR + File.separator + "mgc_x2x";
                if (!ShellCommandExecutor.execute(COMMAND, TEMPDIR)) {
                                        
                    throw new Exception("Error in MGC!");
                }
            } 
            if (mgccParameters.getGamma() == 0) {
                COMMAND = SPTKDIR + "frame +f -l " + mgccParameters.getFramelen() + " -p " + mgccParameters.getFrameshift() + " " + TEMPDIR + File.separator + "mgc_x2x | "
                        + SPTKDIR + "window -l " + mgccParameters.getFramelen() + " -L " + mgccParameters.getFftlen() + " -w " + mgccParameters.getWindowtype() + " -n " + mgccParameters.getNormalize() + " | "
                        + SPTKDIR + "mcep -a " + mgccParameters.getFreqwarp() + " -m " + mgccParameters.getMgcorder() + " -l " + mgccParameters.getFftlen() + " -e 10 > " + _mgcFile;
                if (!ShellCommandExecutor.execute(COMMAND, MGCDIR)) {
                    throw new Exception("Error in MGC!");
                }
            } else {
                int SAMPKHZ = (mgccParameters.getSampfreq() / 1000);
                String GAINOPT = (mgccParameters.getLngain() == 1) ? " -l " : "";
                COMMAND = SPTKDIR + "frame +f -l " + mgccParameters.getFramelen() + " -p " + mgccParameters.getFrameshift() + " " + TEMPDIR + File.separator + "mgc_x2x | "
                        + SPTKDIR + "window -l " + mgccParameters.getFramelen() + " -L " + mgccParameters.getFftlen() + " -w " + mgccParameters.getWindowtype() + " -n " + mgccParameters.getNormalize() + " | "
                        + SPTKDIR + "mcep -e -a " + mgccParameters.getFreqwarp() + " -c " + mgccParameters.getGamma() + " -m " + mgccParameters.getMgcorder() + " -l " + mgccParameters.getFftlen() + " -o 4 -e 10 | "
                        + SPTKDIR + "lpc2lsp -m " + mgccParameters.getMgcorder() + " -s " + SAMPKHZ + GAINOPT + " -n " + mgccParameters.getFftlen() + " -p 8 -d 1e-6 > " + _mgcFile;
                if (!ShellCommandExecutor.execute(COMMAND, MGCDIR)) {
                    throw new Exception("Error in MGC!");
                }
            }
            ProjectUtil.emptyDir(TEMPDIR);
        } catch (Exception ex) {
            throw new ExtractionException(ex.getMessage());
        }
    }
}
