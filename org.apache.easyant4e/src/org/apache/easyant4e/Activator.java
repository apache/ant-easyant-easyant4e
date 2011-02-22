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
package org.apache.easyant4e;

import org.apache.easyant4e.modules.EasyAnt4EclipseModule;
import org.apache.easyant4e.modules.EasyAntCoreModule;
import org.apache.easyant4e.services.EasyantCoreService;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.google.inject.Inject;


/**
 * This class manage plugin life cycle
 */
public class Activator extends AbstractUIPlugin {

    private EasyAntPlugin easyAntPlugin;
    private EasyAntCoreModule easyAntCoreModule;
    private EasyAnt4EclipseModule easyAnt4EclipseModule;

    private EasyantCoreService easyantCoreService;
    
    @Inject
    public void setEasyantCoreService(EasyantCoreService easyantCoreService) {
        this.easyantCoreService = easyantCoreService;
    }

    public static final String PLUGIN_ID = "org.apache.easyant4e";

    // The shared instance.
    private static Activator plugin;
    

    /**
     * Create an activator and a EasyAntModule
     * 
     */
    public Activator() {
        super();
        if (plugin == null) {
            plugin = this;
            this.easyAntCoreModule = new EasyAntCoreModule();
            this.easyAnt4EclipseModule = new EasyAnt4EclipseModule();
        }
    }

    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        initializeEasyAntPlugin();      
        easyantCoreService.installPluginsRepository();
        Activator.getDefault().getLog().log(
                new Status(IStatus.INFO, Activator.PLUGIN_ID, 0, "EasyAnt For Eclipse started", null));
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        Activator.getDefault().getLog().log(
                new Status(IStatus.INFO, Activator.PLUGIN_ID, 0, "EasyAnt For Eclipse shutdown", null));
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance.
     * 
     * @return the plugin instance
     */
    public static Activator getDefault() {
        return plugin;
    }

    /**
     * Returns the EasyAnt Plugin instance.
     * 
     * @return the EasyAnt plugin instance
     */
    public static EasyAntPlugin getEasyAntPlugin() {
        return getDefault().getEasyAntPluginInstance();
    }

    /**
     * Returns the EasyAntPlugin instance.
     */
    private EasyAntPlugin getEasyAntPluginInstance() {
        if (easyAntPlugin == null) {
            initializeEasyAntPlugin();
        }
        return easyAntPlugin;
    }

    private void initializeEasyAntPlugin() {
        if (easyAntPlugin == null) {
            easyAntPlugin = new EasyAntPlugin(easyAntCoreModule, easyAnt4EclipseModule);
            easyAntPlugin.injectMembers(this);
        }
    }
        
}
