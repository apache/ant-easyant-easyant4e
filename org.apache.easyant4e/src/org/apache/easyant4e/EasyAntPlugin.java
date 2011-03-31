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
package org.apache.easyant4e;

import org.apache.easyant4e.services.EasyantProjectService;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.internal.ui.packageview.ClassPathContainer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

public class EasyAntPlugin {

    private final Injector injector;

    public EasyAntPlugin(Module... modules) {
        injector = Guice.createInjector(modules);
        injector.injectMembers(this);
    }

    public void injectMembers(Object instance) {
        injector.injectMembers(instance);
    }

    /**
     * Return the {@link EasyantCoreService}.
     */
    /*public EasyantCoreService getEasyantCoreService() {
        return getInstance(EasyantCoreService.class);
    }*/

    /**
     * Return the {@link EasyantProjectService}.
     */
    /*
    public EasyantProjectService getEasyantProjectService() {
        return getInstance(EasyantProjectService.class);
    }*/

    public IProject getCurrentProject() {
        try {
            return getSelectedProject();
        } catch (CoreException e) {
            log(e);
        }
        return null;
    }

    // private IJavaProject getJavaProject() throws CoreException {
    // IProject project = getSelectedProject();
    // IJavaProject javaProject=null;
    // if(project.hasNature(JavaCore.NATURE_ID)){
    // javaProject = (IJavaProject)project.getNature(JavaCore.NATURE_ID);
    // }
    // return javaProject;
    //}

    private IProject getSelectedProject() throws CoreException {
        IProject project = null;
        Object selectedResource = getSelectedResource();
        if (selectedResource instanceof IResource) {
            IResource resource = (IResource) selectedResource;
            project = resource.getProject();
        } else if (selectedResource instanceof ClassPathContainer) {
            // FIXME maybe use Adaptable and WorkbenchAdapter to resolve project
            // container
            project = ((ClassPathContainer) selectedResource).getJavaProject().getProject();
        }
        return project;
    }

    private Object getSelectedResource() {
        IStructuredSelection selection = getStructuredSelection();
        if (selection != null) {
            Object element = selection.getFirstElement();
            return element;
        }
        return null;
    }

    private IStructuredSelection getStructuredSelection() {
        ISelection selection = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService()
                .getSelection();
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection ss = (IStructuredSelection) selection;
            return ss;
        }
        return null;
    }

    public void log(int severity, String message) {
        log(severity, message, null);
    }

    public void log(int severity, String message, Throwable e) {
        log(new Status(severity, Activator.PLUGIN_ID, 0, message, e));
    }

    public void log(CoreException e) {
        log(e.getStatus().getSeverity(), "EasyAnt For Eclipse - Internal Error", e);
    }

    public void log(IStatus status) {
        Activator.getDefault().getLog().log(status);
    }

    /**
     * Return the workspace used by the workbench
     * 
     * This method is internal to the workbench and must not be called by any
     * plugins.
     */
    public IWorkspace getPluginWorkspace() {
        return ResourcesPlugin.getWorkspace();
    }

    /**
     * Get instances from the injector.
     * 
     * @param type
     *            the type to get an instance of
     * @return the instance
     */
    public <T> T getInstance(Class<T> type) {
        return injector.getInstance(type);
    }

}
