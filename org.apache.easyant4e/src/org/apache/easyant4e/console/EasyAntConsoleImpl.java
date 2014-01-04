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
package org.apache.easyant4e.console;

import org.apache.easyant4e.Activator;
import org.apache.easyant4e.EasyAntPlugin;
import org.apache.easyant4e.providers.ImageProvider;
import org.apache.ivyde.internal.eclipse.ui.console.IvyConsole;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;

import com.google.inject.Inject;

public class EasyAntConsoleImpl extends IvyConsole implements EasyAntConsole {
    
    private boolean isOpen=false;
    
    public boolean isOpen() {
        //TODO get the stat
        return isOpen;
    }

    public void setOpen(boolean isOpen) {
        this.isOpen = isOpen;
    }
    
    @Inject
    public EasyAntConsoleImpl(ImageProvider imageProvider) {        
        super("EasyAnt", imageProvider.getConsoleImageDescriptor());        
    }

    public void show() {        
        IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();
        IConsole[] existing = manager.getConsoles();
        boolean exists = false;
        for (int i = 0; i < existing.length; i++) {
            if (this == existing[i]) {
                exists = true;
            }
        }
        if (!exists) {
            manager.addConsoles(new IConsole[] { this });
        }
        manager.showConsoleView(this);
        setOpen(true);
        
        //show the Error log view
        showErrorLogView();
        
    }
    
    private void showErrorLogView(){
        if (PlatformUI.getWorkbench().getActiveWorkbenchWindow() != null) {
            if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage() != null) {
                    try {
                        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView("org.eclipse.pde.runtime.LogView", null, IWorkbenchPage.VIEW_ACTIVATE);
                    } catch (PartInitException e) {
                        Activator.getEasyAntPlugin().log(IStatus.ERROR, "Cannot show the Error Log view.", e);
                    }
            }
        }
    }

    @Override
    public void error(String msg) {
        showErrorLogView();
        super.error(msg);
    }

    public void close() {
        IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();
        manager.removeConsoles(new IConsole[] { this });
        //ConsolePlugin.getDefault().getConsoleManager().addConsoleListener(new MyLifecycle());
        setOpen(false);
    }
    
}
