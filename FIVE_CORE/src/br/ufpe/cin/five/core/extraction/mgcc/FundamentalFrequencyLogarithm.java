package br.ufpe.cin.five.core.extraction.mgcc;


import br.ufpe.cin.five.core.extraction.ExtractionException;
import br.ufpe.cin.five.core.project.ProjectUtil;
import br.ufpe.cin.five.core.util.ShellCommandExecutor;
import java.io.File;

/**
 *
 * @author carlos
 */
public class FundamentalFrequencyLogarithm {

    private String _audioFile;
    private String _lf0;
    private String COMMAND;
    private File TEMPDIR;
    private File LF0DIR;
    private String SPTKDIR;
    private String HTSSCRIPTSDIR;
    private String CYGWINDIR;
    private String ACTIVETCLDIR;
    private MgccParameters mgcParameters;

    public FundamentalFrequencyLogarithm(File LF0DIR, String resourcesPath, String extractionPath, MgccParameters mgcParameters, String audioFile) {
        this.TEMPDIR = new File(resourcesPath + File.separator + "temp");
        this.LF0DIR = LF0DIR;
        //ProjectUtil.checkExists(this.TEMPDIR, this.LF0DIR);
        this.HTSSCRIPTSDIR = resourcesPath + File.separator + "scripts" + File.separator + "HTS-2.1.1" + File.separator;
        this.SPTKDIR = resourcesPath + File.separator + "sptk" + File.separator;
        this.CYGWINDIR = resourcesPath + File.separator + "cygwin" + File.separator;
        this.ACTIVETCLDIR = resourcesPath + File.separator + "activeTcl" + File.separator;
        this.mgcParameters = mgcParameters;
        this._audioFile = audioFile;
        this._lf0 = LF0DIR + File.separator + _audioFile.substring(_audioFile.lastIndexOf(File.separator) + 1, _audioFile.length() - 4) + ".lf0";
    }

    public void calculate() {
        try {
            int COUNT = (int) Math.round(0.005 * mgcParameters.getSampfreq());
            COMMAND = SPTKDIR + "step -l " + COUNT + " -v 0.0 | " + SPTKDIR + "x2x +fs > " + TEMPDIR + File.separator + "tmp.head";
            if (!ShellCommandExecutor.execute(COMMAND, TEMPDIR)) {
                System.out.println("ERROR in LF0!");
            }
            COUNT = (int) Math.round(0.025 * mgcParameters.getSampfreq());
            COMMAND = SPTKDIR + "step -l " + COUNT + " -v 0.0 | " + SPTKDIR + "x2x +fs > " + TEMPDIR + File.separator + "tmp.tail";
            if (!ShellCommandExecutor.execute(COMMAND, TEMPDIR)) {
                System.out.println("ERROR in LF0!");
            }
            COMMAND = "cat " + TEMPDIR + File.separator + "tmp.head " + _audioFile + " " + TEMPDIR + File.separator + "tmp.tail | " + SPTKDIR + "x2x +sf > " + TEMPDIR + File.separator + "tmp";
            if (!ShellCommandExecutor.execute(COMMAND, TEMPDIR)) {
                System.out.println("ERROR in LF0!");
            }
            COMMAND = SPTKDIR + "x2x +fa " + TEMPDIR + File.separator + "tmp | wc -l";
            String LENG = ShellCommandExecutor.executeAndGetResult(COMMAND, TEMPDIR).toString().replaceAll("\n", "").trim();
            if (LENG.equals("")) {
                System.out.println("ERROR in LF0!");
            } else {
                COMMAND = SPTKDIR + "nrand -l " + LENG + " | " + SPTKDIR + "sopr -m " + mgcParameters.getNoisemask() + " | " + SPTKDIR + "vopr -a " + TEMPDIR + File.separator + "tmp" + " | " + SPTKDIR + "x2x +fs > " + TEMPDIR + File.separator + "tmp.raw";
                if (!ShellCommandExecutor.execute(COMMAND, TEMPDIR)) {
                    System.out.println("ERROR in LF0!");
                }
            }
            COMMAND = "/opt/ActiveTcl-8.4/bin/" + "tclsh " + HTSSCRIPTSDIR + "getf0.tcl -l -lf0 -H " + mgcParameters.getUpperf0() + " -L " + mgcParameters.getLowerf0() + " -p " + mgcParameters.getFrameshift() + " -r " + mgcParameters.getSampfreq() + " " + TEMPDIR + File.separator + "tmp.raw | " + SPTKDIR + "x2x +af > " + _lf0;
            StringBuffer result = ShellCommandExecutor.executeAndGetResult(COMMAND,LF0DIR);
            if (result.toString().equals("Unable to open mixer /dev/mixer") || result.length()==0)                                       
                System.out.println("ERROR in LF0!");
            
            ProjectUtil.emptyDir(TEMPDIR);
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}
