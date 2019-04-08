package br.ufpe.cin.five.core.extraction.mgcc;

import br.ufpe.cin.five.core.extraction.ExtractionException;
import br.ufpe.cin.five.core.project.ProjectUtil;
import br.ufpe.cin.five.core.util.ShellCommandExecutor;
import java.io.File;

/**
 *
 * @author carlos
 */
public class GVProbabilityDensityFunction {

    private String _mgc;
    private String _lf0;
    private String COMMAND;
    private File TEMPDIR;
    private File GVDIR;
    private String SPTKDIR;
    private String CYGWINDIR;
    private MgccParameters mgcParameters;

    private File gvdata_mgc;
    private File gvdata_lf0;

    public GVProbabilityDensityFunction(File GVDIR, String resourcesPath, String extractionPath, MgccParameters mgcParameters, String audioFile) {
        this.TEMPDIR = new File(resourcesPath + File.separator + "temp");
        this.GVDIR = GVDIR;
        if (!TEMPDIR.exists()) {
            TEMPDIR.mkdirs();
        } else {
            ProjectUtil.emptyDir(TEMPDIR);
        }
        if (!GVDIR.exists()) {
            GVDIR.mkdirs();
        }
        this.SPTKDIR = resourcesPath + File.separator + "sptk" + File.separator;
        this.CYGWINDIR = resourcesPath + File.separator + "cygwin" + File.separator;
        this.mgcParameters = mgcParameters;
        this._mgc = extractionPath + File.separator + "mgc" + File.separator + audioFile.substring(audioFile.lastIndexOf(File.separator) + 1, audioFile.length() - 4) + ".mgc";
        this._lf0 = extractionPath + File.separator + "lf0" + File.separator + audioFile.substring(audioFile.lastIndexOf(File.separator) + 1, audioFile.length() - 4) + ".lf0";
        this.gvdata_mgc = new File(GVDIR + File.separator + "gvdata_mgc");
        this.gvdata_lf0 = new File(GVDIR + File.separator + "gvdata_lf0");
    }

    public  void calculate() throws ExtractionException {
        try {            
            if (new File(_mgc).exists()) {
                COMMAND = SPTKDIR + "vstat -n " + mgcParameters.getMgcorder() + " -o 2 -d " + _mgc + " >> " + gvdata_mgc;
                if (!ShellCommandExecutor.execute(COMMAND, GVDIR)) {
                    throw new Exception("ERROR in GV!");
                }
            }
            if (new File(_lf0).exists()) {
                COMMAND = SPTKDIR + "x2x +fa " + _lf0 + " | " + " grep -v '\\-1e+10' | " + SPTKDIR + "x2x +af | " + SPTKDIR + "vstat -l 1 -o 2 -d >> " + gvdata_lf0;
                if (!ShellCommandExecutor.execute(COMMAND, GVDIR)) {
                    throw new Exception("ERROR in GV!");
                }
            }            
        ProjectUtil.emptyDir(TEMPDIR);
        } catch (Exception ex) {
            throw new ExtractionException(ex.getMessage());
        }
    }

    public void doBeforeCalculate() throws ExtractionException {
        try {
            COMMAND = SPTKDIR + "vstat -n " + mgcParameters.getMgcorder() + " -o 0 -d " + GVDIR + File.separator + "gvdata_mgc > " + GVDIR + File.separator + "gv-mgc.pdf";
            if (!ShellCommandExecutor.execute(COMMAND, GVDIR)) {
                throw new Exception("ERROR in GV!");
            }
            COMMAND = SPTKDIR + "vstat -l 1           -o 0 -d " + GVDIR + File.separator + "gvdata_lf0 > " + GVDIR + File.separator + "gv-lf0.pdf";
            if (!ShellCommandExecutor.execute(COMMAND, GVDIR)) {
                throw new Exception("ERROR in GV!");
            }
            if (mgcParameters.getByteswap() == 1) {
                int MGCDIM = mgcParameters.getMgcorder() + 1;
		int LF0DIM = 1;
		COMMAND = "echo " + MGCDIM + " | " + SPTKDIR + "x2x +ai > " + GVDIR + File.separator + "gv-mgc.pdf.big";
                if (!ShellCommandExecutor.execute(COMMAND, GVDIR)) {
                    throw new Exception("ERROR in GV!");
                }
                COMMAND = "echo " + 1 + " | " + SPTKDIR + "x2x +ai >> " + GVDIR + File.separator + "gv-mgc.pdf.big";
                if (!ShellCommandExecutor.execute(COMMAND, GVDIR)) {
                    throw new Exception("ERROR in GV!");
                }
                COMMAND = "cat " + GVDIR + File.separator + "gv-mgc.pdf           >> " + GVDIR + File.separator + "gv-mgc.pdf.big";
                if (!ShellCommandExecutor.execute(COMMAND, GVDIR)) {
                    throw new Exception("ERROR in GV!");
                }
                COMMAND = "echo " + LF0DIM + " | " + SPTKDIR + "x2x +ai >  " + GVDIR + File.separator + "gv-lf0.pdf.big";
                if (!ShellCommandExecutor.execute(COMMAND, GVDIR)) {
                    throw new Exception("ERROR in GV!");
                }
                COMMAND = "echo " + 1 + " | " + SPTKDIR + "x2x +ai >> " + GVDIR + File.separator + "gv-lf0.pdf.big";
                if (!ShellCommandExecutor.execute(COMMAND, GVDIR)) {
                    throw new Exception("ERROR in GV!");
                }
                COMMAND = "cat " + GVDIR + File.separator + "gv-lf0.pdf           >> gv/gv-lf0.pdf.big";
                if (!ShellCommandExecutor.execute(COMMAND, GVDIR)) {
                    throw new Exception("ERROR in GV!");
                }
            } else {
                int MGCDIM = mgcParameters.getMgcorder() + 1;
		int LF0DIM = 1;
                COMMAND = "echo " + MGCDIM + " | " + SPTKDIR + "x2x +ai | " + SPTKDIR + "swab +f > " + GVDIR + File.separator + "gv-mgc.pdf.big";
                if (!ShellCommandExecutor.execute(COMMAND, GVDIR)) {
                    throw new Exception("ERROR in GV!");
                }
                COMMAND = "echo " + 1 + " | " + SPTKDIR + "x2x +ai | " + SPTKDIR + "swab +f >> " + GVDIR + File.separator + "gv-mgc.pdf.big";
		if (!ShellCommandExecutor.execute(COMMAND, GVDIR)) {
                    throw new Exception("ERROR in GV!");
                }
		COMMAND = SPTKDIR + "swab +f " + GVDIR + File.separator + "gv-mgc.pdf                  >> " + GVDIR + File.separator + "gv-mgc.pdf.big";
		if (!ShellCommandExecutor.execute(COMMAND, GVDIR)) {
                    throw new Exception("ERROR in GV!");
                }
                COMMAND = "echo " + LF0DIM + " | " + SPTKDIR + "x2x +ai | " + SPTKDIR + "swab +f > " + GVDIR + File.separator + "gv-lf0.pdf.big";
		if (!ShellCommandExecutor.execute(COMMAND, GVDIR)) {
                    throw new Exception("ERROR in GV!");
                }
                COMMAND = "echo " + 1 + " | " + SPTKDIR + "x2x +ai | " + SPTKDIR + "swab +f >> " + GVDIR + File.separator + "gv-lf0.pdf.big";
                if (!ShellCommandExecutor.execute(COMMAND, GVDIR)) {
                    throw new Exception("ERROR in GV!");
                }
                COMMAND = SPTKDIR + "swab +f " + GVDIR + File.separator + "gv-lf0.pdf                  >> " + GVDIR + File.separator + "gv-lf0.pdf.big";
                if (!ShellCommandExecutor.execute(COMMAND, GVDIR)) {
                    throw new Exception("ERROR in GV!");
                }
            }

            if (gvdata_mgc.exists()) {
                gvdata_mgc.delete();
            }
            if (gvdata_lf0.exists()) {
                gvdata_lf0.delete();
            }
        } catch (Exception ex) {
            throw new ExtractionException(ex.getMessage());
        }
    }
}
