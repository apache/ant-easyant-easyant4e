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
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import org.apache.easyant.core.EasyAntConfiguration;
import org.apache.easyant.core.EasyAntEngine;
import org.apache.easyant.core.EasyAntMagicNames;
import org.apache.easyant.core.descriptor.EasyAntModuleDescriptor;
import org.apache.easyant.core.descriptor.PropertyDescriptor;
import org.apache.easyant.core.factory.EasyantConfigurationFactory;
import org.apache.easyant.core.report.EasyAntReport;
import org.apache.easyant.core.report.PhaseReport;
import org.apache.easyant.core.report.TargetReport;
import org.apache.easyant4e.Activator;
import org.apache.easyant4e.EasyAntConstants;
import org.apache.easyant4e.EasyAntPlugin;
import org.apache.easyant4e.console.EasyAntConsole;
import org.apache.easyant4e.natures.AddEasyAntNatureOperation;
import org.apache.easyant4e.natures.EasyAntNature;
import org.apache.easyant4e.natures.RemoveEasyAntNatureOperation;
import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.IOverwriteQuery;
import org.eclipse.ui.wizards.datatransfer.FileSystemStructureProvider;
import org.eclipse.ui.wizards.datatransfer.ImportOperation;

import com.google.inject.Inject;

public class EasyantProjectServiceImpl implements EasyantProjectService{

    private EasyAntConsole console;
    
    private EasyantCoreService easyantCoreService;
    
    private EasyAntEngine easyAntEngine;
    
    @Inject
    public void setEasyAntEngine(EasyAntEngine easyAntEngine){
        this.easyAntEngine = easyAntEngine;
    }

    @Inject
    public void setEasyantCoreService(EasyantCoreService easyantCoreService) {
        this.easyantCoreService = easyantCoreService;
    }

    @Inject 
    public void setEasyAntConsole(EasyAntConsole console) {
        this.console = console;
    }

/*  
    public Ivy getIvyInstance(IProject project) {
        //TODO read workspace and project preference to load good Ivy instance must be used by the given project
        return easyantCoreService.getIvyInstance(project);// by default return the ivy instance used by Easyant core
    }
*/
    public void runBuild(IProject project, String buildTaskName, int logLevel, IProgressMonitor monitor) {
        console.show();
        console.info("");
        console.info(project.getName() + "$ easyant " + buildTaskName+".");
        
        IFile ivyFile = project.getFile("module.ivy");
        String ivyFilePath = ivyFile.getLocation().toOSString();
        File buildModule = new File(ivyFilePath);
        
        URL easyantIvySettingsUrl = Activator.getDefault().getBundle().getResource(EasyAntConstants.PLUGINS_SETTINGS);
        
        Properties properties = new Properties();
        properties.put("basedir",project.getLocation().toOSString());
        properties.put("build.compiler", "org.eclipse.jdt.core.JDTCompilerAdapter");
        properties.put("easyant.modules.dir", easyantCoreService.getPluginsRepositoryPath());
                
        //junit plugin under eclipse
        //if property is true : Forked Java VM exited abnormally. Please note the time in the report does not reflect the time until the VM exit.
        properties.put("test.run.fork", "false");
        
        EasyAntConfiguration configuration = EasyantConfigurationFactory.getInstance().createDefaultConfiguration();
        configuration.setShowMemoryDetails(true);
        //configuration.setBuildFile(buildFile); //TODO manage module.ant file
        configuration.setBuildModule(buildModule);
        configuration.setDefinedProps(properties);
        configuration.setMsgOutputLevel(logLevel);
        Vector<String> targets = new Vector<String>();
        targets.add(buildTaskName);
        configuration.setTargets(targets);
        configuration.setEasyantIvySettingsUrl(easyantIvySettingsUrl.toString());
        
        PrintStream outStream = getConsoleOutStream();
        configuration.setOut(outStream);
        
        PrintStream errStream = getConsoleErrStream();
        configuration.setErr(errStream);
        
        //TODO lancer cela dans une WorkspaceModifyOperation
        try{
            EasyAntEngine.runBuild(configuration);
        }catch (Exception be) {
            Activator.getEasyAntPlugin().log(IStatus.WARNING, be.getMessage());
        }
        
        outStream.close();
        errStream.close();
        
    }

    private PrintStream getConsoleOutStream(){      
        console.show(true);
        return new PrintStream(new OutputStream(){
            private StringBuffer sb = new StringBuffer();
            @Override
            public void write(int b) throws IOException {
                if(b!=10){
                    sb.append((char)b);
                }else{
                    String message = sb.toString();
                    console.info(message);
                    sb = new StringBuffer();
                }               
            }
            
        });
    }

    private PrintStream getConsoleErrStream(){      
        console.show(true);
        return new PrintStream(new OutputStream(){
            private StringBuffer sb = new StringBuffer();
            @Override
            public void write(int b) throws IOException {
                if(b!=10){
                    sb.append((char)b);
                }else{
                    String message = sb.toString();
                    console.error(message);
                    sb = new StringBuffer();
                }               
            }
            
        });
    }

    public boolean hasEasyAntNature(IProject project){
        try {
            return project.hasNature(EasyAntNature.NATURE_ID);
        } catch (CoreException e) {
            Activator.getEasyAntPlugin().log(e);
        }
        return false;
    }
    
    public boolean hasEasyAntBuilder(IProject project){
        ICommand[] commands=null;
        try {
            commands = project.getProject().getDescription().getBuildSpec();
        } catch (CoreException e) {
            Activator.getEasyAntPlugin().log(e);
        }       
        boolean found = false;
        if(commands!=null){     
            for (int i = 0; i < commands.length; ++i) {
                if (commands[i].getBuilderName().equals(EasyAntNature.BUILDER_ID)){
                    found = true;
                }
            }   
        }   
        return found;
    }
    

    public void addNature(IProject project) {
        addNature(project, null); 
    }
    
    private void addNature(IProject project, IProgressMonitor monitor) {

        AddEasyAntNatureOperation addNature = new AddEasyAntNatureOperation(project);
        try {
            if(monitor==null){
                PlatformUI.getWorkbench().getProgressService().run(false, false, addNature);
            }else{
                addNature.run(monitor);
            }
        } catch (InvocationTargetException e) {
            Activator.getEasyAntPlugin().log(IStatus.ERROR, "Cannot add EasyAnt nature.", e);
        } catch (InterruptedException e) {
            Activator.getEasyAntPlugin().log(IStatus.CANCEL, "Add EasyAnt nature operation aborted!", e);
        }
        // TODO launch resolve (ask to user)
        // TODO initialize IvyDE classpath container
        // TODO maybe launch the first build ?? 
    }
    
    public void removeNature(IProject project) {
        // Remove the nature on the given project
        RemoveEasyAntNatureOperation removeNature = new RemoveEasyAntNatureOperation(project);
        try {
            PlatformUI.getWorkbench().getProgressService().run(false, false, removeNature);
        } catch (InvocationTargetException e) {
            Activator.getEasyAntPlugin().log(IStatus.ERROR, "Cannot remove EasyAnt nature.", e);
        } catch (InterruptedException e) {
            Activator.getEasyAntPlugin().log(IStatus.CANCEL, "Remove EasyAnt nature operation aborted!", e);
        }
        // TODO remove IvyDE classpath container
        
    }

    public IProject importProject(IProjectDescription projectDescription/*,  Shell messageShell*/, IProgressMonitor monitor) {
        //String projectName = importProjectDescriptor.getProjectName();
        //IProjectDescription projectDescription = importProjectDescriptor.getDescription();
        String projectName = projectDescription.getName();
        final IWorkspace workspace = ResourcesPlugin.getWorkspace();
        final IProject project = workspace.getRoot().getProject(projectName);
//      if (projectDescription == null) {
//          // error case
//          projectDescription = workspace.newProjectDescription(projectName);
//          IPath locationPath = new Path(importProjectDescriptor.getProjectSystemFile().getAbsolutePath());
//
//          // If it is under the root use the default location
//          if (Platform.getLocation().isPrefixOf(locationPath)) {
//              projectDescription.setLocation(null);
//          } else {
//              projectDescription.setLocation(locationPath);
//          }
//      } else {
//          projectDescription.setName(projectName);
//      }

//      if (importProjectDescriptor.isFromIvyDescription()) {
            // import form Ivy description
            File importSource = new File(projectDescription.getLocationURI());
            IWorkspaceRoot rootWorkspace = workspace.getRoot();
            ImportOperation importOperation = new ImportOperation(rootWorkspace.getFullPath(),importSource,FileSystemStructureProvider.INSTANCE, new IOverwriteQuery(){
                    public String queryOverwrite(String pathString) { return ALL; }
            });
            //importOperation.setContext(messageShell);//FIXME is it required?
            try {
                //PlatformUI.getWorkbench().getProgressService().run(false, false, importOperation);
                importOperation.run(monitor);
            } catch (InvocationTargetException e) {
                Activator.getEasyAntPlugin().log(IStatus.ERROR, "Cannot import project.", e);
            } catch (InterruptedException e) {
                Activator.getEasyAntPlugin().log(IStatus.ERROR, "Cannot import project.", e);
            }           
//      }

        try {
            monitor.beginTask("Creating project ...", 100);
            project.create(projectDescription, new SubProgressMonitor(monitor, 30));
            project.open(IResource.BACKGROUND_REFRESH, new SubProgressMonitor(monitor, 70));
            // configure project, apply EasyAnt nature
//          if (importProjectDescriptor.isFromIvyDescription()) {
                addNature(project/*, messageShell*/, monitor);
//          }
        } catch (CoreException e) {
            Activator.getEasyAntPlugin().log(IStatus.ERROR, "Cannot configure imported project.", e);
            return null;
        } finally {
            monitor.done();     
        }
        return project;
        
    }
    
    public List<PhaseReport> getPhases(IFile ivyFile) {
        EasyAntReport easyAntReport = null;
        try {
            //loadEasyAntIvySettings(ivyFile.getProject());
            File f = new File(ivyFile.getLocation().toPortableString());
            easyAntReport = easyAntEngine.getPluginService().generateEasyAntReport(f);
        } catch (Exception e) {
            Activator.getEasyAntPlugin().log(IStatus.ERROR, e.getMessage(), e);
        }
        if (easyAntReport != null) {
            return easyAntReport.getAvailablePhases();
        }
        return Collections.emptyList();
    }

    //private File getBaseDir(IProject project) {
    //  return project.getLocation().toFile();
    //}
    
    public List<TargetReport> getTargets(IFile ivyFile) {
        EasyAntReport easyAntReport = null;
        try {
            File f = new File(ivyFile.getLocation().toPortableString());
            easyAntReport = easyAntEngine.getPluginService().generateEasyAntReport(f);
        } catch (Exception e) {
            Activator.getEasyAntPlugin().log(IStatus.ERROR, e.getMessage(), e);
        }
        if (easyAntReport != null) {
            return easyAntReport.getUnboundTargets();
        }
        return Collections.emptyList();
    }

    public EasyAntModuleDescriptor getEasyAntModuleDescriptor(File file) {
        EasyAntModuleDescriptor moduleDescriptor=null;
        try {
            moduleDescriptor = easyAntEngine.getPluginService().getEasyAntModuleDescriptor(file);
        } catch (Exception e) {
            Activator.getEasyAntPlugin().log(IStatus.ERROR, e.getMessage(), e);
        }
        return moduleDescriptor;
    }
    
    public Map<String, PropertyDescriptor> getProperties(IProject project){
        IFile ivyFile = project.getFile("module.ivy");
        String ivyFilePath = ivyFile.getLocation().toOSString();
        File moduleDescriptor = new File(ivyFilePath);
        EasyAntReport easyAntReport=null;
        try {
            easyAntReport = easyAntEngine.getPluginService().generateEasyAntReport(moduleDescriptor);           
        } catch (Exception e) {
            Activator.getEasyAntPlugin().log(IStatus.ERROR, e.getMessage(), e);
        }               
        if(easyAntReport!=null){
            return easyAntReport.getAvailableProperties();
        }
        return Collections.emptyMap();
    }
    
    public PropertyDescriptor getProperty(IProject project, String name){
        Map<String, PropertyDescriptor> properties = getProperties(project);
        if(properties.containsKey(name)){
            PropertyDescriptor propertyDescriptor = properties.get(name);           
            return propertyDescriptor;
        }else{
            return null;
        }
    }
    
}
