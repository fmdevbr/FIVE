/**
 * Copyright 2011 Federal University of Pernambuco.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * This file is part of FIVE (Framework for an Integrated Voice Environment).
 *
 */
package br.ufpe.cin.five.interfaces;

import br.ufpe.cin.five.core.project.Project;
import br.ufpe.cin.five.core.project.ProjectException;

/**
 *
 * @author Alexandre
 */
public interface IProjectRepository {

    public abstract void create(Project project) throws ProjectException;
    public abstract Project open(String projectFile) throws ProjectException;
    public abstract void save(Project project) throws ProjectException;
    public abstract void close(Project project) throws ProjectException;
    
}
