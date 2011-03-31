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
package org.apache.easyant4e.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.apache.easyant4e.Activator;
import org.apache.easyant4e.EasyAntPlugin;
import org.apache.easyant4e.services.EasyantProjectService;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.pde.core.plugin.IPluginModelBase;
import org.eclipse.pde.core.plugin.PluginRegistry;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Inject;

public class ImportProjectTest{
    @Inject
    EasyantProjectService projectService;
    
    @Before
    public void setUp() throws Exception {
        EasyAntPlugin easyAntPlugin = Activator.getEasyAntPlugin();
        assertNotNull(easyAntPlugin);
        easyAntPlugin.injectMembers(this);
        assertNotNull(projectService);
    }   
    
    IProject simpleJavaProject;
    @Test
    public void testImportSimpleProject() throws Exception {
        final String projectName = "simplejavaproject";
        IProjectDescription description = Activator.getEasyAntPlugin().getPluginWorkspace().newProjectDescription(projectName);
        assertNotNull(description);
        URL url = getClass().getResource("/datas/"+projectName);
        assertNotNull(url);
        String filePath = url.getFile();
        assertNotNull(filePath);
        //String testWorkspacePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString();
        
        IPath projectLocation = getSimpleJavaProjectLocation(filePath);
        assertNotNull(projectLocation);
        description.setLocation(projectLocation);
        final String comment = projectName+ " description";
        description.setComment(comment);
        String[] newNatures= {};
        description.setNatureIds(newNatures);   
    
        simpleJavaProject = projectService.importProject(description, new NullProgressMonitor());
        assertNotNull(simpleJavaProject);
        assertTrue(simpleJavaProject.exists());
        assertTrue(simpleJavaProject.isOpen());
        assertEquals(projectName, simpleJavaProject.getName());
        try {
            assertEquals(comment, simpleJavaProject.getDescription().getComment());
        } catch (CoreException e) {
            fail(e.getMessage());
        }
        assertTrue(projectService.hasEasyAntNature(simpleJavaProject));
        assertSourceFolders(simpleJavaProject);
        //TODO assert classpath
        //org.eclipse.jdt.launching.JRE_CONTAINER
        //org.apache.ivyde.eclipse.cpcontainer.IVYDE_CONTAINER/module.ivy/*
    }
    
    private void assertSourceFolders(IProject project) throws Exception{
    	assertTrue(project.hasNature(JavaCore.NATURE_ID));    
        IJavaProject javaProject = (IJavaProject)project.getNature(JavaCore.NATURE_ID);
        assertNotNull(javaProject);
        List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
        entries.addAll(Arrays.asList(javaProject.getRawClasspath()));
        assertTrue(entries.size()>0);
        HashSet<String> sourceFolders = new HashSet<String>();
        for(IClasspathEntry entry:entries){
            if(IClasspathEntry.CPE_SOURCE==entry.getEntryKind()){
                String path = entry.getPath().toOSString();
                assertNotNull(path);
                sourceFolders.add(path);
            }
        }
        assertTrue(sourceFolders.contains("/simplejavaproject/src/main/java"));
        assertTrue(sourceFolders.contains("/simplejavaproject/src/test/java"));
        assertTrue(sourceFolders.contains("/simplejavaproject/src/main/resources"));
        assertTrue(sourceFolders.contains("/simplejavaproject/src/test/resources"));
    }
    
    private IPath getSimpleJavaProjectLocation(String projectPath){
        IPluginModelBase model = PluginRegistry.findModel(TestPlugin.PLUGIN_ID);
        assertNotNull(model);
        IPath projectLocation=null;
        if (model != null && model.getInstallLocation() != null) {
            File modelNode = new File(model.getInstallLocation());
            String pluginPath = modelNode.getAbsolutePath();
            projectLocation = new Path(pluginPath + "/" + projectPath);
        }
        return projectLocation;
    }

    @After
    public void tearDown() throws CoreException {
        if(simpleJavaProject!=null){
            EclipseProjectBuilder.deleteProject(simpleJavaProject,false);
        }
    }
}
