/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.apache.easyant4e.natures;

import java.lang.reflect.InvocationTargetException;

import org.apache.easyant4e.Activator;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * Add the EasyAnt nature to the project.
 */
public class AddEasyAntNatureOperation implements IRunnableWithProgress {

    private IProject project;

    public AddEasyAntNatureOperation(IProject project) {
        this.project = project;
    }

    public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
        if (project != null) {
            try {
                if (!project.hasNature(EasyAntNature.NATURE_ID)) {
                    monitor.subTask("Add the EasyAnt nature to the project " + project.getName());
                    IProjectDescription description = project.getDescription();
                    String[] oldNatures = description.getNatureIds();
                    String[] newNatures = new String[oldNatures.length + 1];
                    System.arraycopy(oldNatures, 0, newNatures, 1, oldNatures.length);
                    newNatures[0] = EasyAntNature.NATURE_ID;
                    description.setNatureIds(newNatures);
                    project.setDescription(description, new SubProgressMonitor(monitor, 1));
                }
            } catch (CoreException e) {
                Activator.getEasyAntPlugin().log(e);
            }
            monitor.done();
        }

    }

}
