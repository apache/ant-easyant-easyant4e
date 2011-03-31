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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;

/**
 * This is a modified version of the TestProject class written by Erich Gamma
 * and Kent Beck and described in their book
 * "Contributing to Eclipse: Principles, Patterns, and Plug-Ins" It's largely
 * inspired by Spring IDE TestSuite
 */
public class EclipseProjectBuilder {

    // private IProject project;
    // private IJavaProject javaProject;
    // private IPackageFragmentRoot sourceFolder;

    public EclipseProjectBuilder() throws CoreException {
        // IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        // project = root.getProject("TestProject");
        // project.create(null);
        // project.open(null);
        // javaProject = JavaCore.create(project);
        //
        // IFolder binFolder = createBinFolder();
        //
        // setJavaNature();
        // javaProject.setRawClasspath(new IClasspathEntry[0], null);
        //
        // createOutputFolder(binFolder);
        // addSystemLibraries();
    }

    public static IProject createProject(String name) throws CoreException {
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IProject project = root.getProject(name);
        project.create(null);
        project.open(null);
        return project;
    }

    public static IJavaProject createJavaProject(String name) throws CoreException {
        IProject project = createProject(name);
        IJavaProject javaProject = JavaCore.create(project);
        addJavaNature(project);
        javaProject.setRawClasspath(new IClasspathEntry[0], null);
        createOutputFolder(javaProject);
        addSystemLibraries(javaProject);
        return javaProject;
    }

    private static void addJavaNature(IProject project) throws CoreException {
        IProjectDescription description = project.getDescription();
        description.setNatureIds(new String[] { JavaCore.NATURE_ID });
        project.setDescription(description, null);
    }

    private static void createOutputFolder(IJavaProject javaProject) throws CoreException {
        IFolder binFolder = createBinFolder(javaProject.getProject());
        IPath outputLocation = binFolder.getFullPath();
        javaProject.setOutputLocation(outputLocation, null);
    }

    private static IFolder createBinFolder(IProject project) throws CoreException {
        IFolder binFolder = project.getFolder("bin");
        binFolder.create(false, true, null);
        return binFolder;
    }

    private static void addSystemLibraries(IJavaProject javaProject) throws JavaModelException {
        IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
        IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
        System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
        newEntries[oldEntries.length] = JavaRuntime.getDefaultJREContainerEntry();
        javaProject.setRawClasspath(newEntries, null);
    }

    public static IFile createModuleDescriptorFile(IProject project, String org) throws CoreException {
        IFile moduleDescFile = project.getFile("module.ivy");
        String content = "" 
                + "<ivy-module version=\"2.0\" xmlns:ea=\"http://www.easyant.org\">"
                + "  <info organisation=\"" + org + "\" module=\"" + project.getName() + "\" status=\"integration\" >"
                + "    <description>project description</description>" 
                + "    <ea:build organisation=\"org.apache.easyant.buildtypes\" module=\"build-std-java\" revision=\"0.2\"/>"
                + "  </info>" 
                + "</ivy-module>";
        InputStream in = new ByteArrayInputStream(content.getBytes());
        moduleDescFile.create(in, true, null);
        return moduleDescFile;
    }

    public static void deleteProject(IProject project) throws CoreException {
        // waitForIndexer();
        deleteProject(project, true);
    }
    
    public static void deleteProject(IProject project, boolean deleteContent) throws CoreException {
        // waitForIndexer();
        project.delete(deleteContent, true, new NullProgressMonitor());
    }

    // public IJavaProject getJavaProject() {
    // return javaProject;
    // }

    // public void addJar(String plugin, String jar) throws
    // MalformedURLException,
    // IOException, JavaModelException {
    // Path result = findFileInPlugin(plugin, jar);
    // IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
    // IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length +
    // 1];
    // System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
    // newEntries[oldEntries.length] = JavaCore.newLibraryEntry(result, null,
    // null);
    // javaProject.setRawClasspath(newEntries, null);
    // }
    //
    // public IPackageFragment createPackage(String name) throws CoreException {
    // if (sourceFolder == null)
    // sourceFolder = createSourceFolder();
    // return sourceFolder.createPackageFragment(name, false, null);
    // }
    //
    // public IType createType(IPackageFragment pack, String cuName, String
    // source)
    // throws JavaModelException {
    // StringBuffer buf = new StringBuffer();
    // buf.append("package " + pack.getElementName() + ";
//");
    // buf.append("
//");
    // buf.append(source);
    // ICompilationUnit cu = pack.createCompilationUnit(cuName,
    // buf.toString(), false, null);
    // return cu.getTypes()[0];
    // }
    //
    // /**
    // * Wait for autobuild notification to occur, that is
    // * for the autbuild to finish.
    // */
    // public void waitForAutoBuild() {
    // boolean wasInterrupted = false;
    // do {
    // try {
    // Platform.getJobManager().join(
    // ResourcesPlugin.FAMILY_AUTO_BUILD, null);
    // wasInterrupted = false;
    // } catch (OperationCanceledException e) {
    // throw (e);
    // } catch (InterruptedException e) {
    // wasInterrupted = true;
    // }
    // } while (wasInterrupted);
    // }
    //
    // public void dispose() throws CoreException {
    // waitForIndexer();
    // project.delete(true, true, null);
    // }
    //

    //
    // public IFolder createXmlFolder() throws CoreException {
    // IFolder xmlFolder = project.getFolder("xml");
    // if (!xmlFolder.exists()) {
    // xmlFolder.create(false, true, null);
    // }
    // return xmlFolder;
    // }
    //

    //
    // private IPackageFragmentRoot createSourceFolder() throws CoreException {
    // IFolder folder = project.getFolder("src");
    // folder.create(false, true, null);
    // IPackageFragmentRoot root = javaProject.getPackageFragmentRoot(folder);
    //
    // IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
    // IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length +
    // 1];
    // System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
    // newEntries[oldEntries.length] = JavaCore.newSourceEntry(root.getPath());
    // javaProject.setRawClasspath(newEntries, null);
    // return root;
    // }
    //

    // private Path findFileInPlugin(String plugin, String file)
    // throws MalformedURLException, IOException {
    // IPluginRegistry registry = Platform.getPluginRegistry();
    // IPluginDescriptor descriptor = registry.getPluginDescriptor(plugin);
    // URL pluginURL = descriptor.getInstallURL();
    // URL jarURL = new URL(pluginURL, file);
    // URL localJarURL = Platform.asLocalURL(jarURL);
    // return new Path(localJarURL.getPath());
    // }
    //
    // private void waitForIndexer() throws JavaModelException {
    // new SearchEngine().searchAllTypeNames(ResourcesPlugin.getWorkspace(),
    // null, null, IJavaSearchConstants.EXACT_MATCH,
    // IJavaSearchConstants.CASE_SENSITIVE,
    // IJavaSearchConstants.CLASS, SearchEngine
    // .createJavaSearchScope(new IJavaElement[0]),
    // new ITypeNameRequestor() {
    // public void acceptClass(char[] packageName,
    // char[] simpleTypeName, char[][] enclosingTypeNames,
    // String path) {
    // }
    //
    // public void acceptInterface(char[] packageName,
    // char[] simpleTypeName, char[][] enclosingTypeNames,
    // String path) {
    // }
    // }, IJavaSearchConstants.WAIT_UNTIL_READY_TO_SEARCH, null);
    // }
}
