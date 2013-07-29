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
package org.apache.easyant4e.ivyde.extension.page;

import java.util.ArrayList;
import java.util.List;

import org.apache.easyant.core.report.PhaseReport;
import org.apache.easyant.core.report.TargetReport;
import org.apache.easyant4e.services.EasyantProjectService;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IFileEditorInput;

import com.google.inject.Inject;

public class BuildLifeCycleContentProvider implements ITreeContentProvider {

    private EasyantProjectService easyantProjectService;

    public BuildLifeCycleContentProvider() {
    }

    @Inject
    public void setEasyantProjectService(EasyantProjectService easyantProjectService) {
        this.easyantProjectService = easyantProjectService;
    }

    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof IFileEditorInput) {
            IFileEditorInput ivyFileEditorInput = (IFileEditorInput) inputElement;
            IFile ivyFile = ivyFileEditorInput.getFile();
            List<PhaseReport> phaseReports = easyantProjectService.getPhases(ivyFile);
            List<TargetReport> targetReports = easyantProjectService.getTargets(ivyFile);
            ArrayList<Object> o = new ArrayList<Object>();
            o.addAll(phaseReports);
            o.addAll(targetReports);
            Object[] input = o.toArray(new Object[o.size()]);
            return input;
        }
        return new Object[0];
    }

    public void dispose() {
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }

    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof PhaseReport) {
            PhaseReport phaseReport = (PhaseReport) parentElement;
            //if (phaseReport.getName().equals("compile")) {
            //  System.out.println("coucou");
            //}
            List<TargetReport> targetReports = phaseReport.getTargetReports();
            return targetReports.toArray(new TargetReport[targetReports.size()]);
        }
        return null;
    }

    public Object getParent(Object element) {
        return null;
    }

    public boolean hasChildren(Object element) {
        if (element instanceof PhaseReport) {
            PhaseReport phaseReport = (PhaseReport) element;
            List<TargetReport> targetReports = phaseReport.getTargetReports();
            return (targetReports != null && targetReports.size() > 0);
        }
        return false;
    }

}
