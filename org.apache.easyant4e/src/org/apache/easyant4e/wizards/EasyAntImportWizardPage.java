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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.easyant4e.Activator;
import org.apache.easyant4e.natures.AddEasyAntNatureOperation;
import org.apache.easyant4e.services.EasyantProjectService;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.wizards.datatransfer.FileSystemStructureProvider;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;

import com.google.inject.Inject;

//import com.google.inject.Inject;

/**
 * This wizard is used to import existing EasyAnt Projects
 * 
 * Inspired from WizardProjectsImportPage
 */
public class EasyAntImportWizardPage extends WizardPage {
    
    private EasyantProjectService easyantProjectService;
    
    @Inject
    public void setEasyantProjectService(EasyantProjectService easyantProjectService) {
        this.easyantProjectService = easyantProjectService;
    }

    public EasyAntImportWizardPage() {
        super("Import EasyAnt projects");
        setPageComplete(false);
        setTitle("Import EasyAnt projects");
        setDescription("Select a directory to search for existing EasyAnt projects.");
    }

    /**
     * The name of the folder containing metadata information for the workspace.
     */
    public static final String METADATA_FOLDER = ".metadata"; //$NON-NLS-1$
    //FIXME hard coded the folder name ?
    public static final String TARGET_FOLDER = "target"; //$NON-NLS-1$
    
    
    private Text directoryPathField;

    private CheckboxTreeViewer projectsList;

    private ProjectRecord[] selectedProjects = new ProjectRecord[0];

    // Keep track of the directory that we browsed to last time
    // the wizard was invoked.
    private static String previouslyBrowsedDirectory = ""; //$NON-NLS-1$

    private Button browseDirectoriesButton;

    private IProject[] wsProjects;

    // The last selected path to minimize searches
    private String lastPath;
    // The last time that the file or folder at the selected path was modified
    // to mimize searches
    private long lastModified;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets
     * .Composite)
     */
    public void createControl(Composite parent) {

        initializeDialogUnits(parent);

        Composite workArea = new Composite(parent, SWT.NONE);
        setControl(workArea);

        workArea.setLayout(new GridLayout());
        workArea.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));

        createProjectsRoot(workArea);
        createProjectsList(workArea);
        createOptionsArea(workArea);
        Dialog.applyDialogFont(workArea);

    }

    /**
     * Create the area with the extra options.
     * 
     * @param workArea
     */
    private void createOptionsArea(Composite workArea) {
        Composite optionsGroup = new Composite(workArea, SWT.NONE);
        optionsGroup.setLayout(new GridLayout());
        optionsGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    }

    /**
     * Create the checkbox list for the found projects.
     * 
     * @param workArea
     */
    private void createProjectsList(Composite workArea) {

        Label title = new Label(workArea, SWT.NONE);        
        title.setText("EasyAnt Projects:");

        Composite listComposite = new Composite(workArea, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginWidth = 0;
        layout.makeColumnsEqualWidth = false;
        listComposite.setLayout(layout);

        listComposite
                .setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH));

        projectsList = new CheckboxTreeViewer(listComposite, SWT.BORDER);
        GridData listData = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH);
        projectsList.getControl().setLayoutData(listData);

        projectsList.setContentProvider(new ITreeContentProvider() {
            public Object[] getChildren(Object parentElement) {
                return null;
            }

            public Object[] getElements(Object inputElement) {
                return getValidProjects();
            }

            public boolean hasChildren(Object element) {
                return false;
            }

            public Object getParent(Object element) {
                return null;
            }

            public void dispose() {

            }

            public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            }

        });

        projectsList.setLabelProvider(new LabelProvider() {
            public String getText(Object element) {
                return ((ProjectRecord) element).getProjectLabel();
            }
        });

        projectsList.addCheckStateListener(new ICheckStateListener() {
            public void checkStateChanged(CheckStateChangedEvent event) {
                setPageComplete(projectsList.getCheckedElements().length > 0);
            }
        });
        projectsList.setInput(this);
        projectsList.setComparator(new ViewerComparator());
        createSelectionButtons(listComposite);
    }

    /**
     * Create the selection buttons in the listComposite.
     * 
     * @param listComposite
     */
    private void createSelectionButtons(Composite listComposite) {
        Composite buttonsComposite = new Composite(listComposite, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        buttonsComposite.setLayout(layout);

        buttonsComposite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

        Button selectAll = new Button(buttonsComposite, SWT.PUSH);
        selectAll.setText("Select All");
        selectAll.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                projectsList.setCheckedElements(selectedProjects);
                setPageComplete(projectsList.getCheckedElements().length > 0);
            }
        });
        Dialog.applyDialogFont(selectAll);
        setButtonLayoutData(selectAll);

        Button deselectAll = new Button(buttonsComposite, SWT.PUSH);
        deselectAll.setText("Deselect All");
        deselectAll.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {

                projectsList.setCheckedElements(new Object[0]);
                setPageComplete(false);
            }
        });
        Dialog.applyDialogFont(deselectAll);
        setButtonLayoutData(deselectAll);

        Button refresh = new Button(buttonsComposite, SWT.PUSH);
        refresh.setText("Refresh");
        refresh.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                updateProjectsList(directoryPathField.getText().trim());
            }
        });
        Dialog.applyDialogFont(refresh);
        setButtonLayoutData(refresh);
    }

    /**
     * Create the area where you select the root directory for the projects.
     * 
     * @param workArea
     *            Composite
     */
    private void createProjectsRoot(Composite workArea) {
        // project specification group
        Composite projectGroup = new Composite(workArea, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        layout.makeColumnsEqualWidth = false;
        layout.marginWidth = 0;
        projectGroup.setLayout(layout);
        projectGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        // project location entry field
        this.directoryPathField = new Text(projectGroup, SWT.BORDER);

        this.directoryPathField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL));

        // browse button
        browseDirectoriesButton = new Button(projectGroup, SWT.PUSH);
        browseDirectoriesButton.setText("Browse");
        setButtonLayoutData(browseDirectoriesButton);

        browseDirectoriesButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                handleLocationDirectoryButtonPressed();
            }

        });

        directoryPathField.addTraverseListener(new TraverseListener() {
            public void keyTraversed(TraverseEvent e) {
                if (e.detail == SWT.TRAVERSE_RETURN) {
                    e.doit = false;
                    updateProjectsList(directoryPathField.getText().trim());
                }
            }

        });

        directoryPathField.addFocusListener(new FocusAdapter() {
            public void focusLost(org.eclipse.swt.events.FocusEvent e) {
                updateProjectsList(directoryPathField.getText().trim());
            }

        });
    }

    /*
     * (non-Javadoc) Method declared on IDialogPage. Set the focus on path
     * fields when page becomes visible.
     */
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            this.directoryPathField.setFocus();
        }
    }

    /**
     * Update the list of projects based on path. Method declared public only
     * for test suite.
     * 
     * @param path
     */
    public void updateProjectsList(final String path) {
        // on an empty path empty selectedProjects
        if (path == null || path.length() == 0) {
            setMessage("Import Projects Description");
            selectedProjects = new ProjectRecord[0];
            projectsList.refresh(true);
            projectsList.setCheckedElements(selectedProjects);
            setPageComplete(projectsList.getCheckedElements().length > 0);
            lastPath = path;
            return;
        }

        final File directory = new File(path);
        long modified = directory.lastModified();
        if (path.equals(lastPath) && lastModified == modified) {
            // since the file/folder was not modified and the path did not
            // change, no refreshing is required
            return;
        }

        lastPath = path;
        lastModified = modified;

        // We can't access the radio button from the inner class so get the
        // status beforehand
        // final boolean dirSelected =
        // this.projectFromDirectoryRadio.getSelection();
        try {
            getContainer().run(true, true, new IRunnableWithProgress() {

                public void run(IProgressMonitor monitor) {
                    monitor.beginTask("Searching", 100);
                    selectedProjects = new ProjectRecord[0];
                    Collection files = new ArrayList();
                    monitor.worked(10);
                    if (directory.isDirectory()) {

                        if (!collectProjectFilesFromDirectory(files, directory, null, monitor)) {
                            return;
                        }
                        Iterator filesIterator = files.iterator();
                        selectedProjects = new ProjectRecord[files.size()];
                        int index = 0;
                        monitor.worked(50);
                        monitor.subTask("Processing");
                        while (filesIterator.hasNext()) {
                            File file = (File) filesIterator.next();
                            ProjectRecord projectRecord = new ProjectRecord(file, lastPath);                            
                            selectedProjects[index] = projectRecord;
                            index++;
                        }
                    } else {
                        monitor.worked(60);
                    }
                    monitor.done();
                }

            });
        } catch (InvocationTargetException e) {
            Activator.getEasyAntPlugin().log(IStatus.ERROR, e.getMessage());
        } catch (InterruptedException e) {
            // Nothing to do if the user interrupts.
        }

        projectsList.refresh(true);
        projectsList.setCheckedElements(getValidProjects());
        if (getValidProjects().length < selectedProjects.length) {
            setMessage("Projects in workspace", WARNING);
        } else {
            setMessage("Import projects description");
        }
        setPageComplete(projectsList.getCheckedElements().length > 0);
    }

    /**
     * Display an error dialog with the specified message.
     * 
     * @param message
     *            the error message
     */
    protected void displayErrorDialog(String message) {
        MessageDialog.openError(getContainer().getShell(), getErrorDialogTitle(), message);
    }

    /**
     * Get the title for an error dialog. Subclasses should override.
     */
    protected String getErrorDialogTitle() {
        // FIXME
        return "internalErrorTitle";
    }

    /**
     * Collect the list of .project files that are under directory into files.
     * 
     * @param files
     * @param directory
     * @param directoriesVisited
     *            Set of canonical paths of directories, used as recursion guard
     * @param monitor
     *            The monitor to report to
     * @return boolean <code>true</code> if the operation was completed.
     */
    private boolean collectProjectFilesFromDirectory(Collection files, File directory, Set directoriesVisited,
            IProgressMonitor monitor) {

        if (monitor.isCanceled()) {
            return false;
        }
        monitor.subTask("Collect project files from "+ directory.getPath());
        File[] contents = directory.listFiles();
        if (contents == null){
            return false;
        }

        // Initialize recursion guard for recursive symbolic links
        if (directoriesVisited == null) {
            directoriesVisited = new HashSet();
            try {
                directoriesVisited.add(directory.getCanonicalPath());
            } catch (IOException exception) {
                Activator.getEasyAntPlugin().log(IStatus.ERROR, exception.getMessage(), exception);
            }
        }
        // first look for EasyAnt description files
        for (int i = 0; i < contents.length; i++) {
            File file = contents[i];
            if (file.isFile() && file.getName().endsWith("ivy")) {
                files.add(file);                
            }else if (contents[i].isDirectory()) {
                if (!contents[i].getName().equals(METADATA_FOLDER) && !contents[i].getName().equals(TARGET_FOLDER)) {
                    try {
                        String canonicalPath = contents[i].getCanonicalPath();
                        if (!directoriesVisited.add(canonicalPath)) {
                            // already been here --> do not recurse
                            continue;
                        }
                    } catch (IOException exception) {
                        Activator.getEasyAntPlugin().log(IStatus.ERROR, exception.getMessage(), exception);

                    }
                    collectProjectFilesFromDirectory(files, contents[i], directoriesVisited, monitor);
                }
            }
        }
        return true;
    }
    /**
     * The browse button has been selected. Select the location.
     */
    protected void handleLocationDirectoryButtonPressed() {
        DirectoryDialog dialog = new DirectoryDialog(directoryPathField.getShell());
        dialog.setMessage("Select directory to scan");

        String dirName = directoryPathField.getText().trim();
        if (dirName.length() == 0) {
            dirName = previouslyBrowsedDirectory;
        }

        if (dirName.length() == 0) {
            dialog.setFilterPath(Activator.getEasyAntPlugin().getPluginWorkspace().getRoot().getLocation().toOSString());
        } else {
            File path = new File(dirName);
            if (path.exists()) {
                dialog.setFilterPath(new Path(dirName).toOSString());
            }
        }

        String selectedDirectory = dialog.open();
        if (selectedDirectory != null) {
            previouslyBrowsedDirectory = selectedDirectory;
            directoryPathField.setText(previouslyBrowsedDirectory);
            updateProjectsList(selectedDirectory);
        }

    }


    /**
     * Create the selected projects
     * 
     * @return boolean <code>true</code> if all project creations were
     *         successful.
     */
    public boolean createProjects() {       
        final Object[] selected = projectsList.getCheckedElements();
        WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
            protected void execute(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
                try {
                    monitor.beginTask("", selected.length); //$NON-NLS-1$
                    if (monitor.isCanceled()) {
                        throw new OperationCanceledException();
                    }
                    for (int i = 0; i < selected.length; i++) {
                        createExistingProject((ProjectRecord) selected[i], new SubProgressMonitor(monitor, 1));
                    }
                } finally {
                    monitor.done();
                }
            }
        };
        // run the new project creation operation
        try {
            getContainer().run(true, true, op);
        } catch (InterruptedException e) {
            return false;
        } catch (InvocationTargetException e) {
            // one of the steps resulted in a core exception
            Throwable t = e.getTargetException();
            Activator.getEasyAntPlugin().log(IStatus.ERROR, t.getMessage(), t);
            String message = "Cannot import project";
            IStatus status;
            if (t instanceof CoreException) {
                status = ((CoreException) t).getStatus();
            } else {
                status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, 1, message, t);
            }
            ErrorDialog.openError(getShell(), message, null, status);
            return false;
        }
        return true;
    }

    /**
     * Create the project described in record. If it is successful return true.
     * 
     * @param record
     * @return boolean <code>true</code> if successful
     * @throws InterruptedException
     */
    private IProject createExistingProject(final ProjectRecord record, IProgressMonitor monitor)
            throws InvocationTargetException, InterruptedException {        
        return easyantProjectService.importProject(record.getDescription()/*, getShell()*/, monitor);
    }

    /**
     * Method used for test suite.
     * 
     * @return CheckboxTreeViewer the viewer containing all the projects found
     */
    public CheckboxTreeViewer getProjectsList() {
        return projectsList;
    }

    /**
     * Retrieve all the projects in the current workspace.
     * 
     * @return IProject[] array of IProject in the current workspace
     */
    private IProject[] getProjectsInWorkspace() {
        if (wsProjects == null) {
            wsProjects = Activator.getEasyAntPlugin().getPluginWorkspace().getRoot().getProjects();
        }
        return wsProjects;
    }

    /**
     * Get the array of valid project records that can be imported from the
     * source workspace or archive, selected by the user. If a project with the
     * same name exists in both the source workspace and the current workspace,
     * it will not appear in the list of projects to import and thus cannot be
     * selected for import.
     * 
     * Method declared public for test suite.
     * 
     * @return ProjectRecord[] array of projects that can be imported into the
     *         workspace
     */
    public ProjectRecord[] getValidProjects() {
        List validProjects = new ArrayList();
        for (int i = 0; i < selectedProjects.length; i++) {
            if (!isProjectInWorkspace(selectedProjects[i].getProjectName())) {
                validProjects.add(selectedProjects[i]);
            }
        }
        return (ProjectRecord[]) validProjects.toArray(new ProjectRecord[validProjects.size()]);
    }

    /**
     * Determine if the project with the given name is in the current workspace.
     * 
     * @param projectName
     *            String the project name to check
     * @return boolean true if the project with the given name is in this
     *         workspace
     */
    private boolean isProjectInWorkspace(String projectName) {
        if (projectName == null) {
            return false;
        }
        IProject[] workspaceProjects = getProjectsInWorkspace();
        for (int i = 0; i < workspaceProjects.length; i++) {
            if (projectName.equals(workspaceProjects[i].getName())) {
                return true;
            }
        }
        return false;
    }
    
}
