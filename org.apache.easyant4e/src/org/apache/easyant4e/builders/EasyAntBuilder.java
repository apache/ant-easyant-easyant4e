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
package org.apache.easyant4e.builders;

import java.util.Map;

import org.apache.easyant4e.Activator;
import org.apache.easyant4e.EasyAntConstants;
import org.apache.easyant4e.services.EasyantProjectService;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;

import com.google.inject.Inject;

//import com.google.inject.Inject;

/**
 * The EastAnt builder.
 */
public class EasyAntBuilder extends IncrementalProjectBuilder {

    private IProgressMonitor monitor;

    private EasyantProjectService easyantProjectService;
        
    public EasyAntBuilder(){
        Activator.getEasyAntPlugin().injectMembers(this);
    }
    
    @Inject
    public void setEasyantProjectService(EasyantProjectService easyantProjectService) {
        this.easyantProjectService = easyantProjectService;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    protected IProject[] build(final int kind, final Map args, final IProgressMonitor monitor) throws CoreException {
        this.monitor = monitor;
        if (!getProject().exists()) {
            return new IProject[] {};
        } else {
            try {
                if (FULL_BUILD == kind) {
                    doBuild();
                } else { // FIXME maybe check INCREMENTAL_BUILD or AUTO_BUILD
                    IResourceDelta delta = getDelta(getProject());
                    if (delta == null) {
                        doBuild();
                    } else {
                        doBuild(delta);
                    }
                }
            } catch (OperationCanceledException e) {
                Activator.getEasyAntPlugin().log(IStatus.CANCEL, "EasyAnt build canceled", e);
            }
            return null;
        }
    }

    protected void doBuild() throws CoreException {
        IWorkspaceRunnable buildOperation = createBuildOperation(); 
        buildOperation.run(monitor);
    }

    protected void doBuild(IResourceDelta delta) throws CoreException {
        doBuild();
    }
    
    private IWorkspaceRunnable createBuildOperation(){
        // TODO maybe use WorkspaceModifyOperation
        return new IWorkspaceRunnable(){            
            public void run(IProgressMonitor monitor) throws CoreException {
                // TODO call good target
                easyantProjectService.runBuild(getProject(), "compile", EasyAntConstants.ANT_LOGLEVEL_WARN, monitor);
            }           
        };
    }

}
