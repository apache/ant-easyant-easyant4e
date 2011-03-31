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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.easyant4e.Activator;
import org.apache.easyant4e.EasyAntPlugin;
import org.apache.easyant4e.services.EasyantCoreService;
import org.apache.easyant4e.services.EasyantProjectService;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.junit.After;
import org.junit.Before;

import com.google.inject.Inject;

public abstract class AbstractEasyAntTest {
    protected IProject testProject;
    protected IJavaProject testJavaProject;
    
    @Inject
    EasyantCoreService coreService;
    
    @Inject
    EasyantProjectService projectService;
    
    @Before
    public void setUp() throws Exception {
        EasyAntPlugin easyAntPlugin = Activator.getEasyAntPlugin();
        assertNotNull(easyAntPlugin);
        easyAntPlugin.injectMembers(this);
        assertNotNull(coreService);
        assertNotNull(projectService);
        
        //System.out.println("setUp");
        String testProjectName = "TestProject";
        this.testProject = EclipseProjectBuilder.createProject(testProjectName);
        assertNotNull(testProject);
        IFile testModuleDesc = EclipseProjectBuilder.createModuleDescriptorFile(testProject, "org.apache.easyant");
        assertNotNull(testModuleDesc);
        assertTrue(testModuleDesc.exists());
        
        String testJavaProjectName = "TestJavaProject";
        this.testJavaProject = EclipseProjectBuilder.createJavaProject(testJavaProjectName);
        assertNotNull(testProject);
        IFile testJavaModuleDesc = EclipseProjectBuilder.createModuleDescriptorFile(testJavaProject.getProject(), "org.apache.easyant");
        assertNotNull(testJavaModuleDesc);
        assertTrue(testJavaModuleDesc.exists());   
        
		//TODO register error log handler Activator.getEasyAntPlugin().log
    }
    
    @After
    public void tearDown() throws CoreException {
		//TODO check assert empty error log Activator.getEasyAntPlugin().log
        if(this.testProject!=null){
            EclipseProjectBuilder.deleteProject(testProject);
            this.testProject = null;
        }
        if(this.testJavaProject!=null){
            EclipseProjectBuilder.deleteProject(testJavaProject.getProject());
            this.testJavaProject = null;            
        }
    }
}
