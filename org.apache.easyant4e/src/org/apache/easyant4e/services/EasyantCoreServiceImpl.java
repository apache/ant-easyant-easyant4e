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
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.easyant.core.EasyAntConfiguration;
import org.apache.easyant.core.EasyAntEngine;
import org.apache.easyant.core.descriptor.PropertyDescriptor;
import org.apache.easyant.core.factory.EasyantConfigurationFactory;
import org.apache.easyant.core.report.EasyAntReport;
import org.apache.easyant4e.Activator;
import org.apache.easyant4e.ivyde.extension.RepositoryInstaller;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;

import static org.apache.easyant4e.EasyAntConstants.EASYANT_BUILD_TYPES_ORG;
import static org.apache.easyant4e.EasyAntConstants.EASYANT_BUILD_PLUGINS_ORG;
import static org.apache.easyant4e.EasyAntConstants.PLUGINS_SETTINGS;

import com.google.inject.Inject;

public class EasyantCoreServiceImpl implements EasyantCoreService {

    //TODO a déplacer dans pluginService
    //TODO et ne pas recharger les settings à chaque fois ... faire un push du contexte
    //TODO read workspace preference and project preference to Get the good Ivy instance for the EasyAnt Core
    //IvySettings ivySettings = createIvySettingsForEACore(project);
    //Ivy ivy = Ivy.newInstance(ivySettings);
    //ivySettings.addRepositoryCacheManager(getRepositoryCacheManager());
    //ivySettings.setBaseDir(getBaseDir(project));
    
/*
    public Ivy getIvyInstance() {   
        URL url = Activator.getDefault().getBundle().getResource(PLUGINS_SETTINGS);     
        Ivy ivy = IvyContext.getContext().getIvy();
        try{
            ivy.configure(url);
        } catch (ParseException e) {
            Activator.getEasyAntPlugin().log(IStatus.ERROR, "Cannot parse EasyAnt Ivy settings file: " + url, e);
        } catch (IOException e) {
            Activator.getEasyAntPlugin().log(IStatus.ERROR, "Cannot read EasyAnt Ivy settings file: " + url, e);
        }
        IvySettings ivySettings = ivy.getSettings();
        IvyContext ivyContext = IvyContext.pushNewContext();
        ivyContext.setIvy(ivy);
        return ivy;
    }
    */
    
/*
    private IvySettings createIvySettingsForEACore(IProject project) {
        // Ivy ivy = IvyContext.pushNewCopyContext().getIvy();
        // IvySettings ivySettings = ivy.getSettings();
        IvySettings ivySettings = new IvySettings();

        URL url = Activator.getDefault().getBundle().getResource(PLUGINS_SETTINGS);
        try {
            ivySettings.load(url);
        } catch (ParseException e) {
            Activator.getEasyAntPlugin().log(IStatus.ERROR, "Cannot parse EasyAnt Ivy settings file: " + url, e);
        } catch (IOException e) {
            Activator.getEasyAntPlugin().log(IStatus.ERROR, "Cannot read EasyAnt Ivy settings file: " + url, e);
        }
        //ivySettings.addRepositoryCacheManager(getRepositoryCacheManager());
        ivySettings.setBaseDir(getBaseDir(project));
        
        return ivySettings;
    }
*/

    public static final String CACHE_FILE = "easyant4e-cache-manager";
    public static final String CACHE_NAME = "__easyant4e-cache-manager";

/*  
    // FIXME connect this on project instance
    private static RepositoryCacheManager getRepositoryCacheManager() {
        DefaultRepositoryCacheManager cacheManager = new DefaultRepositoryCacheManager();
        BundleContext bundleContext = IvyPlugin.getDefault().getBundleContext();
        cacheManager.setBasedir(bundleContext.getDataFile(CACHE_FILE));
        cacheManager.setCheckmodified(true);
        cacheManager.setUseOrigin(true);
        cacheManager.setName(WorkspaceResolver.CACHE_NAME);
        return cacheManager;
    }
*/
    private EasyAntEngine easyAntEngine;
    
    @Inject
    public void setEasyAntEngine(EasyAntEngine easyAntEngine){
        this.easyAntEngine = easyAntEngine;
    }
    
    //private static File getBaseDir(IProject project) {
    //  return project.getLocation().toFile();
    //}

    public String getPluginsRepositoryPath() {
        return pluginsRepository.getPath();
    }

    private File pluginsRepository;

    public void installPluginsRepository() {
        String userHome = System.getProperty("user.home");
        File easyAntHome = new File(userHome, ".easyant");
        if (!easyAntHome.exists()) {
            boolean created = easyAntHome.mkdir();
            if (!created) {
                Activator.getEasyAntPlugin().log(IStatus.ERROR, "Cannot create directory: " + easyAntHome);
            }
        }
        this.pluginsRepository = new File(easyAntHome, "easyant-repository");
        installLocalRepository();
    }

    private void installLocalRepository() {
        if (!pluginsRepository.exists()) {
            boolean created = pluginsRepository.mkdir();
            if (!created) {
                Activator.getEasyAntPlugin().log(IStatus.ERROR, "Cannot create directory: " + pluginsRepository);
            } else {
                // populate the repository from the plugin
                // TODO read preference to override default ivysettings.xml use
                // to
                // resolve build plugins
                RepositoryInstaller repositoryInstaller = new RepositoryInstaller();
                repositoryInstaller.install(pluginsRepository);
            }
        }
        // HashMap<String, String> variables = new HashMap<String, String>();
        // variables.put("easyant.repo.dir", pluginsRepository.getPath());
        // ivySettings.addAllVariables(variables);
    }

    public String[] getAllCoreBuildTypes() {
        try {
            //getIvyInstance(null);
            return easyAntEngine.getPluginService().searchModule(EASYANT_BUILD_TYPES_ORG, "*");
        } catch (Exception e) {
            Activator.getEasyAntPlugin().log(IStatus.ERROR, e.getMessage(), e);
        }
        return null;
    }

    public String[] getAllCorePlugins() {
        try {
            //getIvyInstance(null);
            return easyAntEngine.getPluginService().searchModule(EASYANT_BUILD_PLUGINS_ORG, "*");
        } catch (Exception e) {
            Activator.getEasyAntPlugin().log(IStatus.ERROR, e.getMessage(), e);
        }
        return null;
    }
    
    public List<PropertyDescriptor> getPropertiesForBuildType(String buildTypeName){
        //getIvyInstance(null);
        ArrayList<PropertyDescriptor> propertyDescriptors = new ArrayList<PropertyDescriptor>();
        EasyAntReport eaReport = null;
        try {
            eaReport = easyAntEngine.getPluginService().getBuildTypeInfo(buildTypeName);
        } catch (Exception e) {
            Activator.getEasyAntPlugin().log(IStatus.ERROR, e.getMessage(), e);
        }
        if (eaReport != null) {
            Map<String, PropertyDescriptor> properties = eaReport.getAvailableProperties();
            for (Entry<String, PropertyDescriptor> entry : properties.entrySet()) {
                PropertyDescriptor prop = entry.getValue();
                propertyDescriptors.add(prop);
            }
        }
        return propertyDescriptors;
    }

    public String getBuildTypeDescription(String buildTypeName) {
        return easyAntEngine.getPluginService().getBuildTypeDescription(buildTypeName);
    }

    public String getPluginDescription(String pluginName) {
        return easyAntEngine.getPluginService().getPluginDescription(pluginName);
    }
}
