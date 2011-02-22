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

import org.apache.easyant.core.report.PhaseReport;
import org.apache.easyant.core.report.TargetReport;
import org.apache.easyant4e.EasyAntConstants;
import org.apache.easyant4e.console.EasyAntConsole;
import org.apache.easyant4e.providers.ImageProvider;
import org.apache.easyant4e.services.EasyantProjectService;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.internal.ui.callhierarchy.HistoryListAction;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.google.inject.Inject;

//import com.google.inject.Inject;

public class BuildLifecycleBlock extends MasterDetailsBlock {
    private FormPage page;  

    private BuildLifeCycleContentProvider buildLifeCycleContentProvider;
    private BuildLifecycleLabelProvider buildLifecycleLabelProvider;
    private EasyantProjectService easyantProjectService;
    private PhaseDetailsPage phaseDetailsPage;
    private TargetDetailsPage targetDetailsPage;
    private EasyAntConsole console;
    private ImageProvider imageProvider;
    private boolean enableInfoLogLevel = false;
    private boolean enableVerboseLogLevel = false;
    private boolean enableDebugLogLevel = false;
    
    
    public BuildLifecycleBlock() {}
    
    @Inject
    public void setImageProvider(ImageProvider imageProvider) {
        this.imageProvider = imageProvider;
    }
    
    @Inject
    public void setConsole(EasyAntConsole console) {
        this.console = console;
    }
    
    @Inject
    public void setPhaseDetailsPage(PhaseDetailsPage phaseDetailsPage) {
        this.phaseDetailsPage = phaseDetailsPage;
    }

    @Inject
    public void setTargetDetailsPage(TargetDetailsPage targetDetailsPage) {
        this.targetDetailsPage = targetDetailsPage;
    }

    @Inject
    public void setEasyantProjectService(EasyantProjectService easyantProjectService) {
        this.easyantProjectService = easyantProjectService;
    }

    @Inject
    public void setBuildLifecycleLabelProvider(BuildLifecycleLabelProvider buildLifecycleLabelProvider) {
        this.buildLifecycleLabelProvider = buildLifecycleLabelProvider;
    }

    @Inject
    public void setBuildLifeCycleContentProvider(BuildLifeCycleContentProvider buildLifeCycleContentProvider) {
        this.buildLifeCycleContentProvider = buildLifeCycleContentProvider;
    }

    public void setPage(FormPage page) {
        this.page = page;
    }
    
    @Override
    protected void createMasterPart(final IManagedForm managedForm, final Composite parent) {
        createBuildLifecyclePart(managedForm, parent);
    }

    protected void createBuildLifecyclePart(final IManagedForm managedForm, final Composite parent) {
        FormToolkit toolkit = managedForm.getToolkit();
        Section section = toolkit.createSection(parent, Section.DESCRIPTION | Section.TITLE_BAR);
        section.setText("Build Lifecycle");
        section.setDescription("The build lifecycle has the following build phases:");
        section.marginWidth = 10;
        section.marginHeight = 5;
        Composite client = toolkit.createComposite(section, SWT.WRAP);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginWidth = 2;
        layout.marginHeight = 2;
        client.setLayout(layout);
        toolkit.paintBordersFor(client);

        section.setClient(client);
        final SectionPart spart = new SectionPart(section);
        managedForm.addPart(spart);
        TreeViewer viewer = new TreeViewer(client);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = 20;
        gd.widthHint = 100;
        viewer.getTree().setLayoutData(gd);
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event) {
                managedForm.fireSelectionChanged(spart, event.getSelection());
            }
        });
        viewer.addDoubleClickListener(new IDoubleClickListener(){
            public void doubleClick(DoubleClickEvent event) {
                ISelection selection = event.getSelection();
                if(selection instanceof StructuredSelection){
                    StructuredSelection structuredSelection = (StructuredSelection)selection;
                    if(!structuredSelection.isEmpty()){
                        Object o = structuredSelection.getFirstElement();                       
                        if(o instanceof PhaseReport){
                            runPhase((PhaseReport)o);
                        }else if(o instanceof TargetReport){                            
                            runTarget((TargetReport)o);
                        }
                    }
                }               
            }           
        });
        viewer.setContentProvider(buildLifeCycleContentProvider);
        viewer.setLabelProvider(buildLifecycleLabelProvider);
        viewer.setInput(page.getEditor().getEditorInput());
        // viewer.expandAll();
    }

    private void runPhase(final PhaseReport phase){
        Job job = new WorkspaceJob("Easyant running phase " + phase.getName() + "...") {
            @Override
            public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
                easyantProjectService.runBuild(getProject(), phase.getName(), getLogLevel(), monitor);
                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }
    
    private void runTarget(final TargetReport target){
        Job job = new WorkspaceJob("Easyant running target " + target.getName() + "...") {
            @Override
            public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {              
                easyantProjectService.runBuild(getProject(), target.getName(), getLogLevel(), monitor);
                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }
    
    private int getLogLevel(){
        int logLevel = EasyAntConstants.ANT_LOGLEVEL_WARN;
        if(enableInfoLogLevel){
            logLevel = EasyAntConstants.ANT_LOGLEVEL_INFO;
        }
        if(enableVerboseLogLevel){
            logLevel = EasyAntConstants.ANT_LOGLEVEL_VERBOSE;
        }       
        if(enableDebugLogLevel){
            logLevel = EasyAntConstants.ANT_LOGLEVEL_DEBUG;
        }
        return logLevel;
    }
    
    protected void registerPages(DetailsPart detailsPart) {
        phaseDetailsPage.setProject(getProject());
        detailsPart.registerPage(PhaseReport.class, phaseDetailsPage);
        targetDetailsPage.setProject(getProject());
        detailsPart.registerPage(TargetReport.class, targetDetailsPage);
    }

    @Override
    protected void createToolBarActions(IManagedForm managedForm) {
        final ScrolledForm form = managedForm.getForm();
        Action setLogLevelAction = new LogLevelDropDownAction();
        form.getToolBarManager().add(setLogLevelAction);
    }

    private IProject getProject() {
        if (page.getEditor().getEditorInput() instanceof IFileEditorInput) {
            IFileEditorInput fileEditorInput = (IFileEditorInput) page.getEditor().getEditorInput();
            IFile ivyFile = fileEditorInput.getFile();
            return ivyFile.getProject();
        }
        return null;
    }
    
    private class LogLevelDropDownAction extends Action implements IMenuCreator {
        private Menu menu;
        
        public LogLevelDropDownAction(){
            super("setDebugLogLevel", Action.AS_DROP_DOWN_MENU);
            setMenuCreator(this);
            setToolTipText("Set the log level in console view.");
            setImageDescriptor(imageProvider.getDebugLogLevelImage());      
        }
        
        public Menu getMenu(Menu parent) { return null; }
        public Menu getMenu(Control parent) {
            if (menu == null){
                menu = new Menu(parent);
                MenuItem info = new MenuItem(menu, SWT.CHECK);
                info.setText("INFO");
                info.addSelectionListener(new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        enableInfoLogLevel=!enableInfoLogLevel;
                        phaseDetailsPage.setLogLevel(getLogLevel());
                        targetDetailsPage.setLogLevel(getLogLevel());
                    }
                    public void widgetDefaultSelected(SelectionEvent e) {}
                });
                MenuItem verbose = new MenuItem(menu, SWT.CHECK);
                verbose.setText("VERBOSE");
                verbose.addSelectionListener(new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        enableVerboseLogLevel=!enableVerboseLogLevel;
                        phaseDetailsPage.setLogLevel(getLogLevel());
                        targetDetailsPage.setLogLevel(getLogLevel());
                    }
                    public void widgetDefaultSelected(SelectionEvent e) {}
                });
                MenuItem debug = new MenuItem(menu, SWT.CHECK);
                debug.setText("DEBUG");
                debug.addSelectionListener(new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        enableDebugLogLevel=!enableDebugLogLevel;
                        phaseDetailsPage.setLogLevel(getLogLevel());
                        targetDetailsPage.setLogLevel(getLogLevel());
                    }
                    public void widgetDefaultSelected(SelectionEvent e) {}
                });
            }
            return menu;
        }
        
        public void dispose() {
            if (menu != null){
                menu.dispose();
                menu=null;
            }
        }
        
        public void run() {}
    }
        
}
