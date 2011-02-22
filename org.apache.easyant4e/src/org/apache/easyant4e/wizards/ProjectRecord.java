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

import org.apache.easyant.core.descriptor.EasyAntModuleDescriptor;
import org.apache.easyant4e.Activator;
import org.apache.easyant4e.services.EasyantProjectService;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;

import com.google.inject.Inject;

public class ProjectRecord {
    
    private EasyantProjectService easyantProjectService;
    
    private String lastPath;
    
    private File projectSystemFile;

    public File getProjectSystemFile() {
        return projectSystemFile;
    }

    private String projectName;

    private boolean fromIvyDescription = false;

    private IProjectDescription description;

    public IProjectDescription getDescription() {
        return description;
    }

    @Inject
    public void setEasyantProjectService(EasyantProjectService easyantProjectService){
        this.easyantProjectService = easyantProjectService;
    }

    /**
     * Create a record for a project based on the info in the file.
     * 
     * @param file
     */
    ProjectRecord(File file, String lastPath) {
        Activator.getEasyAntPlugin().injectMembers(this);
        this.projectSystemFile = file;
        this.lastPath = lastPath;
        setProjectName();
    }

    /**
     * Set the name of the project based on the projectFile.
     */
    private void setProjectName() {
        try {
            // If we don't have the project name try again
            IPath path = new Path(projectSystemFile.getPath());
            if (projectName == null && !path.toOSString().endsWith("ivy")) {
                // if the file is in the default location, use the directory
                // name as the project name
                if (isDefaultLocation(path)) {
                    projectName = path.segment(path.segmentCount() - 2);
                    description = Activator.getEasyAntPlugin().getPluginWorkspace().newProjectDescription(projectName);
                } else {
                    description = Activator.getEasyAntPlugin().getPluginWorkspace().loadProjectDescription(path);
                    projectName = description.getName();
                }

            } else if (path.toOSString().endsWith("ivy")) {
                fromIvyDescription = true;
                // Load EasyantFile
                File f = new File(path.toPortableString());
                EasyAntModuleDescriptor moduleDescriptor = easyantProjectService.getEasyAntModuleDescriptor(f);
                projectName = moduleDescriptor.getName();
                description = Activator.getEasyAntPlugin().getPluginWorkspace().newProjectDescription(projectName);                 
                if(lastPath.endsWith(projectName)){
                    description.setLocation(new Path(lastPath));
                }else{
                    description.setLocation(new Path(lastPath+"/"+ projectName));
                }
                description.setComment(moduleDescriptor.getDescription());
                String[] newNatures= {};
                description.setNatureIds(newNatures);                   
            }
        } catch (Exception e) {
            Activator.getEasyAntPlugin().log(IStatus.ERROR, e.getMessage(), e);
        }
    }

    /**
     * Returns whether the given project description file path is in the
     * default location for a project
     * 
     * @param path
     *            The path to examine
     * @return Whether the given path is the default location for a project
     */
    private boolean isDefaultLocation(IPath path) {
        // The project description file must at least be within the project,
        // which is within the workspace location
        if (path.segmentCount() < 2)
            return false;
        return path.removeLastSegments(2).toFile().equals(Platform.getLocation().toFile());
    }

    /**
     * Get the name of the project
     * 
     * @return String
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Gets the label to be used when rendering this project record in the
     * UI.
     * 
     * @return String the label
     */
    public String getProjectLabel() {
        return projectName;
    }

    public boolean isFromIvyDescription() {
        return fromIvyDescription;
    }
}
