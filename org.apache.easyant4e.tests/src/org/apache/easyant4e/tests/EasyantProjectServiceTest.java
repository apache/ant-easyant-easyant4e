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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.apache.easyant.core.descriptor.PropertyDescriptor;
import org.apache.easyant.core.report.PhaseReport;
import org.apache.easyant.core.report.TargetReport;
import org.apache.easyant4e.EasyAntConstants;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.junit.Test;

public class EasyantProjectServiceTest extends AbstractEasyAntTest {

	@Test
	public void testGetProjectProperty() {
		IProject javaProject = testJavaProject.getProject();
		assertNotNull(javaProject);
		PropertyDescriptor srcMainJavaProp = projectService.getProperty(
				javaProject, "src.main.java");
		assertNotNull(srcMainJavaProp);
	}

	@Test
	public void testAddNatureOnJavaProject() {
		IProject javaProject = testJavaProject.getProject();
		assertFalse(projectService.hasEasyAntNature(javaProject));
		assertFalse(projectService.hasEasyAntBuilder(javaProject));
		projectService.addNature(javaProject);
		assertTrue(projectService.hasEasyAntNature(javaProject));
		assertTrue(projectService.hasEasyAntBuilder(javaProject));
		// TODO check classpath and source folder	
	}

	@Test
	public void testRemoveNatureOnJavaProject() {
		IProject javaProject = testJavaProject.getProject();
		assertFalse(projectService.hasEasyAntNature(javaProject));
		projectService.addNature(javaProject);
		assertTrue(projectService.hasEasyAntNature(javaProject));
		assertTrue(projectService.hasEasyAntBuilder(javaProject));
		projectService.removeNature(javaProject);
		assertFalse(projectService.hasEasyAntNature(javaProject));
		assertFalse(projectService.hasEasyAntBuilder(javaProject));
		// TODO check classpath and source folder
	}

	@Test
	public void testGetPhases() {
		IProject javaProject = testJavaProject.getProject();
		IFile ivyFile = javaProject.getFile("module.ivy");
		assertNotNull(ivyFile);
		List<PhaseReport> phases = projectService.getPhases(ivyFile);
		assertNotNull(phases);
		assertTrue("nb phases:" + phases.size(), phases.size() > 0);
	}

	@Test
	public void testGetTargets() {
		IProject javaProject = testJavaProject.getProject();
		IFile ivyFile = javaProject.getFile("module.ivy");
		assertNotNull(ivyFile);
		List<TargetReport> targets = projectService.getTargets(ivyFile);
		assertNotNull(targets);
		assertTrue("nb targets:" + targets.size(), targets.size() > 0);
	}

	@Test
	public void testRunBuild() {
		try {
			projectService.runBuild(testJavaProject.getProject(), "compile",
					EasyAntConstants.ANT_LOGLEVEL_DEBUG, null);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

}
