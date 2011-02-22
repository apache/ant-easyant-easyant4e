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

import org.apache.easyant4e.Activator;
import org.apache.easyant4e.services.EasyantProjectService;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.google.inject.Inject;

//import com.google.inject.Inject;

/**
 * Remove the EasyAnt nature to the project.
 */
public class RemoveEasyAntNatureAction implements IObjectActionDelegate {

    // The selected project
    private IProject selectedProject;
    private EasyantProjectService easyantProjectService;

    public RemoveEasyAntNatureAction(){
        Activator.getEasyAntPlugin().injectMembers(this);
    }
    
    @Inject
    public void setEasyantProjectService(EasyantProjectService easyantProjectService) {
        this.easyantProjectService = easyantProjectService;
    }

    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
    }

    public void run(IAction action) {
        if (selectedProject != null) {
            // Remove the nature on the selected project
            easyantProjectService.removeNature(selectedProject);
        }
    }

    public void selectionChanged(IAction action, ISelection selection) {
        if (selection instanceof StructuredSelection) {
            StructuredSelection structuredSelection = (StructuredSelection) selection;
            if (!structuredSelection.isEmpty() && structuredSelection.getFirstElement() instanceof IProject) {
                IProject project = (IProject) structuredSelection.getFirstElement();
                if (project.isAccessible()) {
                    this.selectedProject = project;
                    if (action != null) {
                        action.setEnabled(easyantProjectService.hasEasyAntNature(project));
                    }
                }

            }
        }
    }

}
