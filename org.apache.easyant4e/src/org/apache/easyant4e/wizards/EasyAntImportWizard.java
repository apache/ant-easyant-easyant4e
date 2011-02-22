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
package org.apache.easyant4e.wizards;

import org.apache.easyant4e.Activator;
import org.apache.easyant4e.providers.ImageProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

import com.google.inject.Inject;

public class EasyAntImportWizard extends Wizard implements IImportWizard {

    private EasyAntImportWizardPage importPage;
    
    private ImageProvider imageProvider;
    
    public EasyAntImportWizard(){
        Activator.getEasyAntPlugin().injectMembers(this);
    }
        
    @Inject
    public void setImageProvider(ImageProvider imageProvider) {
        this.imageProvider = imageProvider;
    }

    @Inject 
    void setEasyAntImportWizardPage(EasyAntImportWizardPage importPage){
        this.importPage=importPage;
    }
    
    public void addPages() {
        //importPage = new EasyAntImportWizardPage();
        super.addPage(importPage);
    }

    @Override
    public boolean performFinish() {
        return importPage.createProjects();
    }

    public void init(IWorkbench workbench, IStructuredSelection selection) {
        setNeedsProgressMonitor(true);
        setWindowTitle("Import EasyAnt Projects");      
        setDefaultPageImageDescriptor(imageProvider.getLogoImageDescriptor());
    }

}
