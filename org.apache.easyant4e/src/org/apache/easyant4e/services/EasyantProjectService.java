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
package org.apache.easyant4e.services;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.easyant.core.descriptor.EasyAntModuleDescriptor;
import org.apache.easyant.core.descriptor.PropertyDescriptor;
import org.apache.easyant.core.report.PhaseReport;
import org.apache.easyant.core.report.TargetReport;
import org.apache.easyant4e.EasyAntConstants;
//import org.apache.ivy.Ivy;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Shell;

public interface EasyantProjectService {
    
    IProject importProject(IProjectDescription projectDescription/*, Shell messageShell*/, IProgressMonitor monitor);
    
    boolean hasEasyAntNature(IProject project);
    
    boolean hasEasyAntBuilder(IProject project);
    
    void addNature(IProject project);
    
    void removeNature(IProject project);
    
    /**
     * @param project
     * @param buildTaskName
     * @param logLevel @see {@link EasyAntConstants#ANT_LOGLEVEL_DEBUG}, ...
     * @param monitor
     */
    void runBuild(IProject project, String buildTaskName, int logLevel, IProgressMonitor monitor);
    
    /**
     * @return a list of available phases
     */
    List<PhaseReport> getPhases(IFile ivyFile);
    
    /**
     * @return a list of available targets
     */
    List<TargetReport> getTargets(IFile ivyFile);
    
    EasyAntModuleDescriptor getEasyAntModuleDescriptor(File file);
    
    Map<String, PropertyDescriptor> getProperties(IProject project);
    
    PropertyDescriptor getProperty(IProject project, String name);
    
}
