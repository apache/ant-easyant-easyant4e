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
package org.apache.easyant4e.natures;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.easyant.core.descriptor.PropertyDescriptor;
import org.apache.easyant4e.Activator;
import org.apache.easyant4e.services.EasyantProjectService;
import org.apache.ivyde.eclipse.cp.IvyClasspathContainer;
import org.apache.ivyde.eclipse.cp.IvyClasspathContainerHelper;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;

import com.google.inject.Inject;

/**
 * The EastAnt nature.
 */
public class EasyAntNature implements IProjectNature {

    public static final String NATURE_ID = "org.apache.easyant4e.EasyAntNature";

    public static final String BUILDER_ID = "org.apache.easyant4e.EasyAntBuilder";

    public EasyAntNature(){
        Activator.getEasyAntPlugin().injectMembers(this);
    }
    
    /**
     * The project to which this project nature applies.
     */
    private IProject project = null;
    
    private EasyantProjectService easyantProjectService;
    
    @Inject
    public void setEasyantProjectService(EasyantProjectService easyantProjectService){
        this.easyantProjectService = easyantProjectService;
    }
    

    public void configure() throws CoreException {
        //TODO maybe add an extension point to delegate this step for java, scala, ...
        //Configurer.configure(IProject, EasyAntModuleDescriptor);      
        //Add Java Nature
        //TODO add checkbox or list of configurer (reuse by skeleton) in import and new project wizard to add JavaNature, ScalaNature, GroovyNature ...
        //or use a dedicated eclipse plugin ?
        if (!project.hasNature(JavaCore.NATURE_ID)) {           
            IProjectDescription desc = project.getDescription();
            List<String> natures = new ArrayList<String>();
            natures.addAll(Arrays.asList(desc.getNatureIds()));
            natures.add(JavaCore.NATURE_ID);            
            desc.setNatureIds(natures.toArray(new String[natures.size()]));
            project.setDescription(desc, null);
        }
        
        if (project.hasNature(JavaCore.NATURE_ID)) {    
            IJavaProject javaProject = (IJavaProject)project.getNature(JavaCore.NATURE_ID);
            //flush classpath
            javaProject.setRawClasspath(new IClasspathEntry[0], null);                      
            addSourceFolders(javaProject);
            addDefaultJREContainer(javaProject);
            addIvyClasspathContainer();
            addBuilder(BUILDER_ID);
        }
    }
    
    private void addIvyClasspathContainer() {
        /*
        IPath projectRelativePath = new Path("module.ivy");
        IJavaProject javaProject = JavaCore.create(project);
        IClasspathEntry newEntry = JavaCore.newContainerEntry(new Path(
        IvyClasspathContainer.CONTAINER_ID).append(projectRelativePath).append(confs));
        */
        IJavaProject javaProject = JavaCore.create(project);    
        //Configure project preferences
        StringBuffer projectConf = new StringBuffer();
        //TODO search *.ivy file
        projectConf.append("?ivyXmlPath=module.ivy");
        try {
            appendConfProp(projectConf, "confs", "*");
            appendConfProp(projectConf, "resolveInWorkspace", Boolean.TRUE.toString());
            String projectIvySettings = easyantProjectService.getProperty(javaProject.getProject(), "project.ivy.settings.url").getValue();
            if(projectIvySettings==null){
                projectIvySettings = easyantProjectService.getProperty(javaProject.getProject(), "project.ivy.settings.file").getValue();
                if(projectIvySettings!=null){
                    projectIvySettings = "file://"+resolveRelativePath(projectIvySettings);
                }
            }
            if(projectIvySettings!=null){
                appendConfProp(projectConf, "ivySettingsPath", projectIvySettings);
            }
        } catch (UnsupportedEncodingException e) {
            Activator.getEasyAntPlugin().log(IStatus.ERROR, e.getMessage(), e);
        }
        
        // add the classpath entry
        IPath path = new Path(IvyClasspathContainer.ID).append(projectConf.toString());
        IClasspathEntry newEntry = JavaCore.newContainerEntry(path);
        addClassPathEntry(javaProject, newEntry);
    }
    
    private String resolveRelativePath(String path){
        if(path!=null && path.startsWith(".")){
            String projectPath = project.getLocation().toPortableString();
            path= new File(projectPath+"/"+path).getAbsolutePath();
        }
        return path;
    }
    
    private void appendConfProp(StringBuffer path, String name, String value) throws UnsupportedEncodingException {
         path.append('&');
         path.append(name);
         path.append('=');
         path.append(URLEncoder.encode(value, "UTF-8"));
    }

    //FIXME with scala project or others
    private void addSourceFolders(IJavaProject javaProject)throws CoreException {       
        PropertyDescriptor srcMainJavaProp = easyantProjectService.getProperty(project, "src.main.java");
        String srcMainJava = removeBaseDirVar(srcMainJavaProp.getValue());
        PropertyDescriptor srcMainResourcesProp = easyantProjectService.getProperty(project, "src.main.resources");
        String srcMainResources = removeBaseDirVar(srcMainResourcesProp.getValue());
        PropertyDescriptor srcTestJavaProp = easyantProjectService.getProperty(project, "src.test.java");
        String srcTestJava = removeBaseDirVar(srcTestJavaProp.getValue());
        PropertyDescriptor srcTestResourcesProp = easyantProjectService.getProperty(project, "src.test.resources");
        String srcTestResources = removeBaseDirVar(srcTestResourcesProp.getValue());
        addSourceFolders(javaProject, srcMainJava,srcMainResources, srcTestJava, srcTestResources);
    }
    
    private String removeBaseDirVar(String s){
        String baseDirVar = "${basedir}";
        if(s!=null && s.contains(baseDirVar)){
            return s.substring(s.indexOf(baseDirVar)+baseDirVar.length()+1);
        }else{
            return s;
        }
    }
    
    public void deconfigure() throws CoreException {
        if (project.hasNature(JavaCore.NATURE_ID)) {    
            removeBuilder(BUILDER_ID);
            removeIvyClasspathContainer();
        }
    }

    public IProject getProject() {
        return project;
    }

    public void setProject(IProject project) {
        this.project = project;

    }
    
    private void addDefaultJREContainer(IJavaProject javaProject) throws JavaModelException{        
        List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
        entries.addAll(Arrays.asList(javaProject.getRawClasspath()));
        entries.add(JavaRuntime.getDefaultJREContainerEntry());
        javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[entries.size()]), null);

    }
    
    private void addSourceFolders(IJavaProject javaProject, String...path) throws JavaModelException{       
        List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
        entries.addAll(Arrays.asList(javaProject.getRawClasspath()));
        IResource outputLocation = javaProject.getProject().findMember("target");
        if(outputLocation==null){
            outputLocation = createSourceFolder(javaProject.getProject(), "target");
        }
        javaProject.setOutputLocation(outputLocation.getFullPath(), null);
        for(String p : path){   
            IResource sourceFolder = javaProject.getProject().findMember(p);
            if(sourceFolder==null){
                sourceFolder = createSourceFolder(javaProject.getProject(), p);
            }
            if(sourceFolder!=null){
                IPath[] exclusionPatterns = new IPath[] { outputLocation.getFullPath() };
                IClasspathEntry srcEntry = JavaCore.newSourceEntry(sourceFolder.getFullPath(),exclusionPatterns, outputLocation.getFullPath());             
                entries.add(srcEntry);
            }
        }
        javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[entries.size()]), null);
    }
    
     private IResource createSourceFolder( IProject project, String sourcefolder ){
            IPath path = new Path(sourcefolder);
            IContainer container = project;
            try {
                for (int i = 1; i <= path.segmentCount(); i++) {
                    IFolder newSourceFolder = project.getFolder( path.uptoSegment( i ) );
                    if (!newSourceFolder.exists()) {
                        newSourceFolder.create( true, true, null );
                    }
                    container = newSourceFolder;
                }
                return container;
            } catch (CoreException e) {
                Activator.getEasyAntPlugin().log(e);
                return null;
            }
        }
    
    private void addClassPathEntry(IJavaProject javaProject, IClasspathEntry newEntry){
        try {
            IClasspathEntry[] entries = javaProject.getRawClasspath();
            List newEntries = new ArrayList(Arrays.asList(entries));
            newEntries.add(newEntry);
            entries = (IClasspathEntry[]) newEntries.toArray(new IClasspathEntry[newEntries.size()]);

            javaProject.setRawClasspath(entries, javaProject.getOutputLocation(), null);
        } catch (CoreException e) {
            // unless there are issues with the JDT, this should never happen
            Activator.getEasyAntPlugin().log(IStatus.ERROR, "Cannot add ClassPath entry", e);
        }
    }
    
    private void removeIvyClasspathContainer() {
        IJavaProject javaProject = JavaCore.create(project);
        ArrayList<IClasspathEntry> newEntries = new ArrayList<IClasspathEntry>();
        //FIXME call IvyDE command
        try {
            IClasspathEntry[] entries = javaProject.getRawClasspath();
            for (int i = 0; i < entries.length; i++) {
                IClasspathEntry entry = entries[i];
                if (entry != null && entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
                    IPath path = entry.getPath();
                    if (IvyClasspathContainerHelper.isIvyClasspathContainer(path)) {
                        continue;
                    }
                }
                newEntries.add(entry);
            }
            // TODO add progress monitor
            IClasspathEntry[] newClasspathEntries = newEntries.toArray(new IClasspathEntry[newEntries.size()]);
            javaProject.setRawClasspath(newClasspathEntries, null);
        } catch (JavaModelException e) {
            Activator.getEasyAntPlugin().log(IStatus.ERROR, "Cannot remove Ivy ClassPath container", e);
        }
    }

    private void addBuilder(String builderID) throws CoreException {
        if (!isBuilderApplied(builderID)) {
            IProjectDescription description = getProject().getDescription();
            ICommand[] commands = description.getBuildSpec();
            ICommand command = description.newCommand();
            command.setBuilderName(builderID);
            ICommand[] newCommands = new ICommand[commands.length + 1];
            System.arraycopy(commands, 0, newCommands, 0, commands.length);
            newCommands[commands.length] = command;
            description.setBuildSpec(newCommands);
            getProject().setDescription(description, null);
        }
    }

    private void removeBuilder(String builderID) throws CoreException {
        if (isBuilderApplied(builderID)) {
            IProjectDescription description = getProject().getDescription();
            ICommand[] commands = description.getBuildSpec();
            ICommand[] newCommands = new ICommand[commands.length - 1];
            int i = 0;
            for (ICommand command : commands) {
                if (!command.getBuilderName().equals(builderID)) {
                    // Remove the given builder
                    newCommands[i] = command;
                    i++;
                }
            }
            description.setBuildSpec(newCommands);
            getProject().setDescription(description, null);
        }
    }

    private boolean isBuilderApplied(String builderID) throws CoreException {
        IProjectDescription description = getProject().getDescription();
        for (ICommand cmd : description.getBuildSpec()) {
            if (cmd.getBuilderName().equals(builderID)) {
                return true;
            }
        }
        return false;
    }

}
