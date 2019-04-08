/**
 * Copyright 2011 Federal University of Pernambuco. All Rights Reserved. Use is
 * subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.register;

import br.ufpe.cin.five.core.data.ConcreteDAO;
import br.ufpe.cin.five.core.extraction.Extraction;
import br.ufpe.cin.five.core.extraction.ExtractionTechnique;
import br.ufpe.cin.five.core.extraction.htk.HtkParameters;
import br.ufpe.cin.five.core.extraction.htk.HtkProcess;
import br.ufpe.cin.five.core.extraction.mfcc.MfccParameters;
import br.ufpe.cin.five.core.extraction.mfcc.MfccProcess;
import br.ufpe.cin.five.core.extraction.mgcc.MgccParameters;
import br.ufpe.cin.five.core.extraction.mgcc.MgccProcess;
import br.ufpe.cin.five.core.project.Project;
import br.ufpe.cin.five.core.project.ProjectUtil;
import br.ufpe.cin.five.core.sample.Audio;
import br.ufpe.cin.five.core.sample.Sample;
import br.ufpe.cin.five.core.sample.SampleFilter;
import br.ufpe.cin.five.core.sample.SampleUtil;
import br.ufpe.cin.five.exception.RegisterException;
import br.ufpe.cin.five.facade.Facade;
import br.ufpe.cin.five.gui.dialogs.FiveWorker;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Alexandre
 */
public class ExtractionRegister {

    private Project project;
    private SampleRegister sampleRegister;
    

    public ExtractionRegister(Project project ) {
        this.project = project;
        this.sampleRegister = new SampleRegister(project);
        
    }

    public void insert(Extraction extraction) throws RegisterException {
        if (search(extraction.getId()) == null) {
            Facade.getInstance().getConcreteDAO().create(extraction.getSampleFilter());
            Facade.getInstance().getConcreteDAO().create(extraction);
            this.project.getExtractions().add(extraction);
            Facade.getInstance().getConcreteDAO().update(this.project);
        } else {
            throw new RegisterException("Já existe uma extração com este ID: " + extraction.getId() + " no banco de dados.");
        }
    }

    public void update(Extraction extraction) throws RegisterException {
        if (search(extraction.getId()) != null) {
            int classificationIndex = this.project.getExtractions().indexOf(extraction);
            Facade.getInstance().getConcreteDAO().update(extraction);
            this.project.getExtractions().set(classificationIndex, extraction);
            Facade.getInstance().getConcreteDAO().update(this.project);
        } else {
            throw new RegisterException("Não existe uma extração com este ID: " + extraction.getId() + " no banco de dados.");
        }
    }

    public void remove(Extraction extraction) throws RegisterException {
        if (search(extraction.getId()) != null) {
            this.project.getExtractions().remove(extraction);
            Facade.getInstance().getConcreteDAO().update(this.project);
            SampleFilter sampleFilter = extraction.getSampleFilter();
            extraction.setSampleFilter(null);
            Facade.getInstance().getConcreteDAO().update(extraction);
            sampleFilter.setSpeakers(null);
            sampleFilter.setUtterances(null);
            Facade.getInstance().getConcreteDAO().update(sampleFilter);
            Facade.getInstance().getConcreteDAO().delete(sampleFilter);
            Facade.getInstance().getConcreteDAO().delete(extraction);
        } else {
            throw new RegisterException("Não existe uma extração com este ID: " + extraction.getId() + " no banco de dados.");
        }
    }

    public Extraction search(int id) throws RegisterException {
        for (Extraction extraction : this.project.getExtractions()) {
            if (id != -1 && extraction.getId() == id) {
                return extraction;
            }
        }
        return null;
    }

    public Extraction search(boolean active) throws RegisterException {
        for (Extraction extraction : this.project.getExtractions()) {
            if (extraction.isActive()) {
                
                return extraction;
            }
        }
        return null;
    }
    
    public void active(Extraction extraction) throws RegisterException {
        extraction.setActive(true);
        update(extraction);        
    }    

    public void process(Extraction extraction) throws RegisterException {
        try {
            new ExtractionWorker(extraction, project).execute();
        } catch (Exception ex) {
            throw new RegisterException(ex.getMessage());
        }
    }

    public FiveWorker getExtractionWorker(Extraction extraction, Project project) throws RegisterException {
        return new ExtractionWorker(extraction, project);
    }

    private class ExtractionWorker extends FiveWorker<Void, String> {

        private final Extraction extraction;
        private final Project project;

        ExtractionWorker(Extraction extraction, Project project) throws RegisterException {
            this.extraction = extraction;
            this.project = project;
        }

        @Override
        protected Void doInBackground() throws RegisterException {

            try {
                int progressCounter = 0;
                int amountOperation = 1;

                String samplesPath = project.getDirectory() + File.separator + "samples";
                String featuresPath = project.getDirectory() + File.separator + "features" + File.separator + extraction.getDescription();

                publish("Apagando extracoes anteriores...\n");
                ProjectUtil.deleteDirectory(new File(featuresPath), false, ".mfc");

                publish("Iniciando extracao caracteristicas...\n");

                ArrayList<Sample> samples = new ArrayList(sampleRegister.search(extraction.getSampleFilter()));

                amountOperation = samples.size();

                if (extraction.getTechnique().equals(ExtractionTechnique.MGCC)) {
                    
                    MgccParameters mgccParameters = (MgccParameters) extraction;                        
                    MgccProcess mgcc = new MgccProcess(project.getDirectory());
                    mgcc.extract(mgccParameters, samples);

                } else {
                
                    for (Sample sample : samples) {

                        publish("Amostra: " + sample.getAudioFile());

                        Audio audio = SampleUtil.waveRead(new File(samplesPath + File.separator + sample.getAudioFile()));

                        if (extraction.getTechnique().equals(ExtractionTechnique.MFCC)) {

                            MfccParameters mfccParameters = (MfccParameters) extraction;
                            MfccProcess mfcc = new MfccProcess(project.getDirectory());
                            double[] answer = mfcc.extract(mfccParameters, audio);                       

                            BufferedWriter out = new BufferedWriter(new FileWriter(featuresPath + File.separator + sample.getFeatureFile()));
                            for (int k = 0; k < answer.length; k++) {
                                out.write(answer[k] + " ");
                            }
                            out.write("\n");
                            out.close();  

                        } else if (extraction.getTechnique().equals(ExtractionTechnique.HTK)) {

                            HtkParameters htkParameters = (HtkParameters) extraction;
                            htkParameters.setDecodeProcess(false);
                            HtkProcess htk = new HtkProcess(project.getDirectory(),featuresPath);
                            htk.extract(htkParameters, audio);

                        }                         
                        setProgress((++progressCounter * 100) / amountOperation);
                    }
                }
                                              
                active(extraction);
                publish("\nConcluido");
                firePropertyChange("done", null, null);
                return null;
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new RegisterException(ex.getMessage());
            }
        }

        @Override
        protected void process(List<String> chunks) {
            for (String chunk : chunks) {
                System.out.println(chunk);
            }
        }
    }
}
