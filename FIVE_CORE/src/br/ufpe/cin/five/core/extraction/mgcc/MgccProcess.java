/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufpe.cin.five.core.extraction.mgcc;

import br.ufpe.cin.five.core.extraction.ExtractionException;
import br.ufpe.cin.five.core.extraction.ExtractionProcess;
import br.ufpe.cin.five.core.project.ProjectType;
import br.ufpe.cin.five.core.project.ProjectUtil;
import br.ufpe.cin.five.core.sample.Audio;
import br.ufpe.cin.five.core.sample.Sample;
import br.ufpe.cin.five.core.utterance.Utterance;
import br.ufpe.cin.five.core.utterance.UtteranceProcess;
import br.ufpe.cin.five.core.utterance.nlp.ContextualLabel;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author Alexandre
 */
public class MgccProcess extends ExtractionProcess {

    private String resourcesPath;
    private String projectPath;
    private String featuresPath;
    private File MGCDIR;
    private File LF0DIR;
    private File CMPDIR;
    private File GVDIR;
    private File LABELSDIR;
    private File LABELSFULLDIR;
    private File LABELSMONODIR;
    private File LABELSGENDIR;
    private File LISTSDIR;
    private File SCPDIR;
    private BufferedWriter BW = null;
    private BufferedReader BR = null;
    private String SIL = "sil";
    private String PAU = "pau";

    public MgccProcess(String projectPath) {

        this.projectPath = projectPath;
        this.featuresPath = projectPath + File.separator + "features";
        this.resourcesPath = projectPath + File.separator + "resources";

        this.MGCDIR = new File(featuresPath + File.separator + "mgc");
        this.LF0DIR = new File(featuresPath + File.separator + "lf0");
        this.CMPDIR = new File(featuresPath + File.separator + "cmp");
        this.GVDIR = new File(featuresPath + File.separator + "gv");
        this.LABELSDIR = new File(featuresPath + File.separator + "labels");
        this.LABELSFULLDIR = new File(featuresPath + File.separator + "labels" + File.separator + "full");
        this.LABELSMONODIR = new File(featuresPath + File.separator + "labels" + File.separator + "mono");
        this.LABELSGENDIR = new File(featuresPath + File.separator + "labels" + File.separator + "gen");
        this.LISTSDIR = new File(featuresPath + File.separator + "lists");
        this.SCPDIR = new File(featuresPath + File.separator + "scp");
    }

    public double[] extract(Object params, ArrayList<Sample> samples) throws ExtractionException {
        try {

            String _wavFile = "";
            String _rawFile = "";
            ArrayList<String> wavFiles = new ArrayList<String>();
            ArrayList<String> wavTexts = new ArrayList<String>();
            System.out.println("Extraindo features...");

            GVProbabilityDensityFunction gv = null;

            MgccParameters extraction = (MgccParameters) params;

            int i=0;
            for (Sample sample : samples) {
               // if(i>140)
               // {
                  _wavFile = projectPath + File.separator + "samples" + File.separator + sample.getAudioFile();
                    _rawFile = _wavFile.replace(".wav", ".raw");

                    System.out.println("Amostra: "+sample.getAudioFile());
/*
                   RawConverter raw = new RawConverter(resourcesPath, extraction, _wavFile);;
                raw.convert();

                    MelGeneralizedCepstralCoefficients mgcc = new MelGeneralizedCepstralCoefficients(MGCDIR, resourcesPath, featuresPath, (MgccParameters) extraction, _rawFile);
                    mgcc.calculate();

                    FundamentalFrequencyLogarithm lf0 = new FundamentalFrequencyLogarithm(LF0DIR, resourcesPath, featuresPath, (MgccParameters) extraction, _rawFile);
                lf0.calculate();

                    TrainingData cmp = new TrainingData(CMPDIR, resourcesPath, featuresPath, (MgccParameters) extraction, _rawFile);
                cmp.compose();

                   gv = new GVProbabilityDensityFunction(GVDIR, resourcesPath, featuresPath, (MgccParameters) extraction, _rawFile);
                    gv.calculate();
*/
                    wavFiles.add(_wavFile);

                    String waveText = sample.getUtterance().getDescription();

                    wavTexts.add(waveText);

                
             //  }
            //    i++;                
            }
   /*         if (gv != null) {
              gv.doBeforeCalculate();
         }
     */      
         
           System.out.println("Criando rótulos contextuais... ");
           createFullLabels(wavFiles, wavTexts);
            
            System.out.println("Definindo conjunto de testes... ");
            defGenLabels();
            
            System.out.println("Realizando alinhamento temporal... ");
            String alignedOut = createAlignment(resourcesPath, wavFiles, wavTexts);
            
            System.out.println("Criando rótulos dos monophones... ");
            createMonoLabels(alignedOut);
            
            System.out.println("Criando arquivos MLF... ");
            createMLFFiles(_rawFile);
            
            System.out.println("Criando listas de fonemas... ");
            createLabelsList();
            
            System.out.println("Criando arquivos SCP... ");
            createSCPFiles();
            
            System.out.println("Criando arquivos de treinamento... ");
            GenerateTrainingFiles gtf = new GenerateTrainingFiles(projectPath, resourcesPath, featuresPath, extraction);
            gtf.generateFiles();
        
        } catch (Exception ex) {
            throw new ExtractionException(ex.getMessage());
        }
        return null;
    }

    private String createAlignment(String resourcesPath, ArrayList<String> audioFiles, ArrayList<String> audioTexts) throws ExtractionException {
        try {
            if (audioFiles.size() != audioTexts.size()) {
                throw new Exception("ERRO: FeatureException->createAlignment(): arrays de tamanhos diferentes!");
            }
            String TEMPDIR = projectPath + File.separator + "temp";
            ForcedAlignment fa = new ForcedAlignment(ProjectType.TTS, resourcesPath, TEMPDIR, TEMPDIR + File.separator + "mfcc", audioFiles, audioTexts);
            fa.start();
            fa.join();
            return fa.getAlignedOut();
        } catch (Exception ex) {
            throw new ExtractionException(ex.getMessage());
        }
    }

    private Boolean createFullLabels(ArrayList<String> audioFiles, ArrayList<String> audioTexts) throws ExtractionException {
        try {
            if (audioFiles.size() != audioTexts.size()) {
                System.out.println("ERRO: FeatureException->createFullLabels(): arrays de tamanhos diferentes!");
                return Boolean.FALSE;
            }
            ProjectUtil.checkExists(this.LABELSFULLDIR);
            for (int a = 0; a < audioTexts.size(); a++) {
                String audioFile = audioFiles.get(a);
                String audioText = audioTexts.get(a);
                String fullLabelFile = this.LABELSFULLDIR + File.separator + audioFile.substring(audioFile.lastIndexOf(File.separator) + 1, audioFile.length() - 4) + ".lab";
            
                Utterance utterance = new Utterance();
                utterance.setDescription(audioText);                
                UtteranceProcess process = new UtteranceProcess(projectPath);                
                utterance.setPhrases(process.convertUtteranceToPhrases(audioText));
                new ContextualLabel(utterance, fullLabelFile).process();
            }
            return Boolean.TRUE;
        } catch (Exception ex) {
            throw new ExtractionException(ex.getMessage());
        }        
    }

    private Boolean defGenLabels() throws ExtractionException {
        try {
            int numberTestPhrases = 10;       
            ProjectUtil.checkExists(this.LABELSGENDIR);
            if (LABELSFULLDIR.list().length < numberTestPhrases) {
                FileUtils.copyDirectory(LABELSFULLDIR, LABELSGENDIR);
            } else {
                int copied = 0;
                for (File file : LABELSFULLDIR.listFiles()) {
                    FileUtils.copyFile(file, new File(file.getPath().replace("full", "gen")));
                    copied++;
                    if (copied == numberTestPhrases) {
                        break;
                    }
                }
            }
            return Boolean.TRUE;
        } catch (Exception ex) {
            throw new ExtractionException(ex.getMessage());
        }
    }

    private Boolean createMonoLabels(String _alignedOut) throws ExtractionException {
        try {
            if (!(new File(_alignedOut)).exists()) {
                System.out.println("ERRO: FeatureException->createMonoLabels(): arquivo de alinhamento inexistente!");
                return Boolean.FALSE;
            }
            ProjectUtil.checkExists(this.LABELSMONODIR);        
            BW = null;
            BR = new BufferedReader(new FileReader(_alignedOut));
            Boolean createNewFile = Boolean.FALSE;
            while (BR.ready()) {
                String readLine = BR.readLine();
                if (readLine.equals("#!MLF!#") || readLine.equals(".")) {
                    createNewFile = Boolean.TRUE;
                    continue;
                }
                if (createNewFile) {
                    if (BW != null) {
                        BW.close();
                    }
                    createNewFile = Boolean.FALSE;
                    String filename = readLine.substring(3, readLine.length() - 4).concat("lab");
                    BW = new BufferedWriter(new FileWriter(this.LABELSMONODIR + File.separator + filename));
                    continue;
                }

                String[] lineArray = readLine.split(" ");
                if (!(lineArray[2].equals("pau") && lineArray[0].equals(lineArray[1]))) {
                    BW.write(lineArray[0] + " " + lineArray[1] + " " + lineArray[2].replaceAll(".*\\-", "").replaceAll("\\+.*", "").replace("pau", PAU).replace("sil", SIL) + "\n");
                }
            }
            if (BW != null) {
                BW.close();
            }
            return Boolean.TRUE;
        } catch (Exception ex) {
            throw new ExtractionException(ex.getMessage());
        }

    }

    private Boolean createMLFFiles(String audioFile) throws ExtractionException {
        try {
            BW = new BufferedWriter(new FileWriter(this.LABELSDIR + File.separator + "mono.mlf"));
            BW.write("#!MLF!#\n\"*/*.lab\" -> \"" + this.LABELSDIR + File.separator + "mono\"\n");
//          BW.write("#!MLF!#\n\"*/" + audioFile.substring(audioFile.lastIndexOf(File.separator) + 1, audioFile.length()-4) + "*.lab\" -> \"" + this.LABELSDIR  + File.separator + "mono\"\n");
            BW.close();
            BW = new BufferedWriter(new FileWriter(this.LABELSDIR + File.separator + "full.mlf"));
            BW.write("#!MLF!#\n\"*/*.lab\" -> \"" + this.LABELSDIR + File.separator + "full\"\n");
//          BW.write("#!MLF!#\n\"*/" + audioFile.substring(audioFile.lastIndexOf(File.separator) + 1, audioFile.length()-4) + "*.lab\" -> \"" + this.LABELSDIR + File.separator + "full\"\n");
            BW.close();
            return Boolean.TRUE;
        } catch (Exception ex) {
            throw new ExtractionException(ex.getMessage());
        }
    }

    private Boolean createLabelsList() throws ExtractionException {
        try {
            ProjectUtil.checkExists(this.LISTSDIR);     
            FilenameFilter filter = new FilenameFilter() {

                public boolean accept(File dir, String name) {
                    return name.endsWith(".lab");
                }
            };
            List<String> list = new ArrayList<String>();
            for (String file : this.LABELSFULLDIR.list(filter)) {
                list.addAll(FileUtils.readLines(new File(this.LABELSFULLDIR + File.separator + file)));
                Collections.sort(list);
                // testar se da append ou cria outro 
                FileUtils.writeLines(new File(this.LISTSDIR + File.separator + "full.list"), list);
            }

            for (String file : this.LABELSGENDIR.list(filter)) {
                list.addAll(FileUtils.readLines(new File(this.LABELSGENDIR + File.separator + file)));
                Collections.sort(list);
                FileUtils.writeLines(new File(this.LISTSDIR + File.separator + "full_all.list"), list);
            }

            list = new ArrayList<String>();

            for (String file : this.LABELSMONODIR.list(filter)) {
                BR = new BufferedReader(new FileReader(new File(this.LABELSMONODIR + File.separator + file)));
                while (BR.ready()) {
                    String readLine = BR.readLine();
                    String[] readLineArray = readLine.split(" ");
                    String monophone = readLineArray[readLineArray.length - 1];
                    if (list.indexOf(monophone) == -1) {
                        list.add(monophone);
                    }
                }
                Collections.sort(list);
                FileUtils.writeLines(new File(this.LISTSDIR + File.separator + "mono.list"), list);
            }
            return Boolean.TRUE;
        } catch (Exception ex) {
            throw new ExtractionException(ex.getMessage());
        }
    }

    private Boolean createSCPFiles() throws ExtractionException {        
        try {
            ProjectUtil.checkExists(this.SCPDIR);
            FilenameFilter filter = new FilenameFilter() {

                public boolean accept(File dir, String name) {
                    return name.endsWith(".cmp");
                }
            };
            BW = new BufferedWriter(new FileWriter(this.SCPDIR + File.separator + "train.scp"));
            for (String file : this.CMPDIR.list(filter)) {
                BW.write(this.CMPDIR + File.separator + file + "\n");
            }
            BW.close();
            filter = new FilenameFilter() {

                public boolean accept(File dir, String name) {
                    return name.endsWith(".lab");
                }
            };
            BW = new BufferedWriter(new FileWriter(this.SCPDIR + File.separator + "gen.scp"));
            for (String file : this.LABELSGENDIR.list(filter)) {
                BW.write(this.LABELSGENDIR + File.separator + file + "\n");
            }
            BW.close();
            return Boolean.TRUE;
        } catch (Exception ex) {
            throw new ExtractionException(ex.getMessage());
        }
    }

    @Override
    public double[] extract(Object params, Audio audio) throws ExtractionException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
