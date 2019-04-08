package br.ufpe.cin.five.core.extraction.mgcc;

import br.ufpe.cin.five.core.extraction.ExtractionException;
import br.ufpe.cin.five.core.project.ProjectUtil;
import br.ufpe.cin.five.core.util.ShellCommandExecutor;
import java.io.File;

/**
 *
 * @author carlos
 */
public class TrainingData {

    private String _mgc;
    private String _lf0;
    private String _cmp;
    private String COMMAND;
    private File TEMPDIR;
    private File CMPDIR;
    private String HTSSCRIPTSDIR;
    private String PERLDIR;
    private String SPTKDIR;
    private MgccParameters mgccParameters;
    private String MGCWIN;
    private String LF0WIN;
    private int NMGCWIN = 3;
    private int NLF0WIN = 3;

    public TrainingData(File CMPDIR, String resourcesPath, String extractionPath, MgccParameters mgccParameters, String audioFile) {
        this.TEMPDIR = new File(resourcesPath + File.separator + "temp");
        this.CMPDIR = CMPDIR;
        //ProjectUtil.checkExists(this.TEMPDIR, this.CMPDIR);
        MGCWIN = resourcesPath + File.separator + "scripts" + File.separator + "training" + File.separator + "data" + File.separator + "win" + File.separator + "mgc.win";
        LF0WIN = resourcesPath + File.separator + "scripts" + File.separator + "training" + File.separator + "data" + File.separator + "win" + File.separator + "lf0.win";
        this.HTSSCRIPTSDIR = resourcesPath + File.separator + "scripts" + File.separator + "HTS-2.1.1" + File.separator;
        this.PERLDIR = resourcesPath + File.separator + "perl" + File.separator;
        this.SPTKDIR = resourcesPath + File.separator + "sptk" + File.separator;
        this.mgccParameters = mgccParameters;
        this._mgc = extractionPath + File.separator + "mgc" + File.separator + audioFile.substring(audioFile.lastIndexOf(File.separator) + 1, audioFile.length() - 4) + ".mgc";
        this._lf0 = extractionPath + File.separator + "lf0" + File.separator + audioFile.substring(audioFile.lastIndexOf(File.separator) + 1, audioFile.length() - 4) + ".lf0";
        this._cmp = CMPDIR + File.separator + File.separator + audioFile.substring(audioFile.lastIndexOf(File.separator) + 1, audioFile.length() - 4) + ".cmp";
    }

    public void compose() throws ExtractionException {
        
        int MGCDIM = mgccParameters.getMgcorder() + 1;
        int LF0DIM = 1;
        double MGCWINDIM = NMGCWIN * MGCDIM;
        double LF0WINDIM = NLF0WIN * LF0DIM;
        double BYTEPERFRAME = (4 * (MGCWINDIM + LF0WINDIM));

        String MGCWINS = "";
        String LF0WINS = "";
        int winNum;

        try {
            if (new File(_mgc).exists() && new File(_lf0).exists()) {
                winNum = 1;
                while (winNum <= NMGCWIN) {
                    MGCWINS += " " + MGCWIN + winNum++;
                }
                COMMAND = "perl " + HTSSCRIPTSDIR + "window.pl " + MGCDIM + " " + _mgc + " " + MGCWINS + " > " + TEMPDIR + File.separator + "tmp.mgc";
                if (!ShellCommandExecutor.execute(COMMAND, TEMPDIR)) {
                    System.out.println("ERROR in CMP!");
                }
                winNum = 1;
                while (winNum <= NLF0WIN) {
                    LF0WINS += " " + LF0WIN + winNum++;
                }
                COMMAND = "perl " + HTSSCRIPTSDIR + "window.pl " + LF0DIM + " " + _lf0 + " " + LF0WINS + " > " + TEMPDIR + File.separator + "tmp.lf0";
                if (!ShellCommandExecutor.execute(COMMAND, TEMPDIR)) {
                    System.out.println("ERROR in CMP!");
                }
                COMMAND = SPTKDIR + "merge +f -s 0 -l " + LF0WINDIM + " -L " + MGCWINDIM + " " + TEMPDIR + File.separator + "tmp.mgc < " + TEMPDIR + File.separator + "tmp.lf0 > " + TEMPDIR + File.separator + "tmp.cmp";
                if (!ShellCommandExecutor.execute(COMMAND, TEMPDIR)) {
                    System.out.println("ERROR in CMP!");
                }
                COMMAND = "perl " + HTSSCRIPTSDIR + "addhtkheader.pl " + mgccParameters.getSampfreq() + " " + mgccParameters.getFrameshift() + " " + BYTEPERFRAME + " 9 " + TEMPDIR + File.separator + "tmp.cmp > " + _cmp;
                if (!ShellCommandExecutor.execute(COMMAND, CMPDIR)) {
                    System.out.println("ERROR in CMP!");
                }
            }
            ProjectUtil.emptyDir(TEMPDIR);
        } catch (Exception ex) {
            throw new ExtractionException(ex.getMessage());
        }
    }
}
