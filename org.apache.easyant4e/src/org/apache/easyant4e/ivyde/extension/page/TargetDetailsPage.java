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

import org.apache.easyant.core.report.TargetReport;
import org.apache.easyant4e.EasyAntConstants;
import org.apache.easyant4e.providers.ImageProvider;
import org.apache.easyant4e.services.EasyantProjectService;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import com.google.inject.Inject;

public class TargetDetailsPage implements IDetailsPage {
    private IManagedForm mform;
    private TargetReport target;

    private IProject project;

    private Text description;
    private Text depends;
    
    private EasyantProjectService easyantProjectService;

    private ImageProvider imageProvider;    

    private int logLevel=EasyAntConstants.ANT_LOGLEVEL_WARN;
    
    public void setLogLevel(int logLevel){
        this.logLevel=logLevel;
    }
    
    public TargetDetailsPage() {}
    
    public void setProject(IProject project) {
        this.project = project;
    }
    
    @Inject
    public void setImageProvider(ImageProvider imageProvider) {
        this.imageProvider = imageProvider;
    }
    
    
    @Inject
    public void setEasyantProjectService(EasyantProjectService easyantProjectService) {
        this.easyantProjectService = easyantProjectService;
    }

    public void createContents(Composite parent) {
        TableWrapLayout layout = new TableWrapLayout();
        layout.topMargin = 5;
        layout.leftMargin = 5;
        layout.rightMargin = 2;
        layout.bottomMargin = 2;
        parent.setLayout(layout);

        FormToolkit toolkit = mform.getToolkit();
        Section section1 = toolkit.createSection(parent, Section.DESCRIPTION | Section.TITLE_BAR);
        section1.marginWidth = 10;
        section1.setText("Target Details");
        section1.setDescription("Targets define a build unit.");        
        TableWrapData td = new TableWrapData(TableWrapData.FILL, TableWrapData.TOP);
        td.grabHorizontal = true;
        section1.setLayoutData(td);
        Composite client = toolkit.createComposite(section1);
        GridLayout glayout = new GridLayout();
        glayout.marginWidth = glayout.marginHeight = 0;
        client.setLayout(glayout);

        createSpacer(toolkit, client, 2);

        toolkit.createLabel(client, "Description:");
        description = toolkit.createText(client, "", SWT.MULTI | SWT.WRAP);
        GridData gdDescription = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
        gdDescription.widthHint = 10;
        description.setLayoutData(gdDescription);

        createSpacer(toolkit, client, 2);
        
        toolkit.createLabel(client, "Depends:");
        depends = toolkit.createText(client, "", SWT.MULTI | SWT.WRAP);
        GridData gdDepends = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
        gdDepends.widthHint = 10;
        depends.setLayoutData(gdDepends);

        createSpacer(toolkit, client, 2);
        
        ImageHyperlink buildLink = toolkit.createImageHyperlink(client, SWT.NULL);
        buildLink.setText("Run this target...");
        buildLink.setForeground(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_BLUE));
        buildLink.setImage(imageProvider.getBuildImage());
        buildLink.addHyperlinkListener(new HyperlinkAdapter() {
            public void linkActivated(HyperlinkEvent e) {
                Job job = new WorkspaceJob("Easyant running target " + target.getName() + "...") {
                    @Override
                    public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
                        easyantProjectService.runBuild(project, target.getName(), logLevel, monitor);
                        return Status.OK_STATUS;
                    }
                };
                job.schedule();
            }
        });
        section1.setClient(client);
    }

    private void createSpacer(FormToolkit toolkit, Composite parent, int span) {
        Label spacer = toolkit.createLabel(parent, ""); //$NON-NLS-1$
        GridData gd = new GridData();
        gd.horizontalSpan = span;
        spacer.setLayoutData(gd);
    }
    
    public void initialize(IManagedForm form) {
        this.mform = form;
    }

    public void selectionChanged(IFormPart part, ISelection selection) {
        IStructuredSelection ssel = (IStructuredSelection) selection;
        if (ssel.size() == 1) {
            target = (TargetReport) ssel.getFirstElement();
        } else {
            target = null;
        }
        update();
    }

    private void update() {
        if(target.getDescription()!=null){
            description.setText(target.getDescription());
        }
        if(target.getDepends()!=null){
            depends.setText(target.getDepends());
        }
    }


    public void commit(boolean onSave) {
    }

    public void dispose() {
    }
    
    public boolean isDirty() {
        return false;
    }

    public boolean isStale() {
        return false;
    }

    public void refresh() {
    }

    public void setFocus() {
    }

    public boolean setFormInput(Object input) {
        return false;
    }

}
