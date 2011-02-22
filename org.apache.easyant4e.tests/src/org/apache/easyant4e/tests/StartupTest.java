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

import org.apache.easyant4e.Activator;
import org.apache.easyant4e.EasyAntPlugin;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.navigator.ResourceNavigator;
import org.junit.Test;

public class StartupTest extends AbstractEasyAntTest{
    
    @Test
    public void testActivatorStartup() {
        EasyAntPlugin easyAntPlugin = Activator.getEasyAntPlugin(); 
        assertNotNull(easyAntPlugin);
    }

    @Test
    public void testEasyAntCoreServiceInit() {
        assertNotNull(coreService);         
    }
    
    @Test
    public void testEasyAntProjectServiceInit() {
        assertNotNull(projectService);                  
    }
        
    @Test
    public void testGetCurrentProject() {           
        ResourceNavigator resourceNavigator = getResourceNavigator();
        assertNotNull(resourceNavigator);
        
        resourceNavigator.selectReveal(new StructuredSelection(testProject));
        IProject project1 = Activator.getEasyAntPlugin().getCurrentProject();
        assertNotNull(project1);
        assertEquals(testProject, project1);
        
        resourceNavigator.selectReveal(new StructuredSelection(testJavaProject));
        IProject project2 = Activator.getEasyAntPlugin().getCurrentProject();
        assertNotNull(project2);
        assertEquals(testJavaProject.getProject(), project2);
    }
    
    private ResourceNavigator getResourceNavigator(){
        ResourceNavigator navigator = null;
        try {
            navigator = (ResourceNavigator) PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getActivePage().showView("org.eclipse.ui.views.ResourceNavigator");
        } catch (PartInitException e) {
            throw new RuntimeException(e);
        }   
        return navigator;
    }
    
}
