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
package org.apache.easyant4e.providers;

import java.net.URL;

import org.apache.easyant4e.Activator;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

public class ImageProviderImpl implements ImageProvider {

    public static final String IMG_HORIZONTAL = "horizontal";

    public static final String IMG_FORM_BG = "formBg";

    public static final String IMG_PHASE = "phase";

    public static final String IMG_TARGET = "target";

    public static final String IMG_CONSOLE = "console";

    public static final String IMG_BUILD = "build";

    public static final String IMG_EASYANT_LOGO = "easyant-logo";
    
    public static final String IMG_INFO_LOG_LEVEL = "info-log-level";
    
    public static final String IMG_DEBUG_LOG_LEVEL = "debug-log-level";

    public ImageProviderImpl() {        
        initializeImageRegistry(getImageRegistry());
    }

    public ImageDescriptor getConsoleImageDescriptor() {
        return getImageDescriptor(IMG_CONSOLE);
    }
    public ImageDescriptor getLogoImageDescriptor() {
        return getImageDescriptor(IMG_EASYANT_LOGO);
    }

    public Image getPhaseImage(){
        return getImage(IMG_PHASE);
    }
    
    public Image getTargetImage(){
        return getImage(IMG_TARGET);
    }
    
    public Image getFormBackgroundImage(){
        return getImage(IMG_FORM_BG);
    }
    
    public Image getBuildImage(){
        return getImage(IMG_BUILD);
    }
    
    public ImageDescriptor getInfoLogLevelImage(){
        return getImageDescriptor(IMG_INFO_LOG_LEVEL);
    }
    
    public ImageDescriptor getDebugLogLevelImage(){
        return getImageDescriptor(IMG_DEBUG_LOG_LEVEL);
    }
    
    private void initializeImageRegistry(ImageRegistry registry) {
        registerImage(registry, IMG_FORM_BG, "form_banner.gif");
        registerImage(registry, IMG_PHASE, "phase.gif");
        registerImage(registry, IMG_TARGET, "target.gif");
        registerImage(registry, IMG_CONSOLE, "console.png");
        registerImage(registry, IMG_BUILD, "build.gif");
        registerImage(registry, IMG_EASYANT_LOGO, "EasyAnt-logo.gif");
        registerImage(registry, IMG_INFO_LOG_LEVEL, "info_loglevel.gif");
        registerImage(registry, IMG_DEBUG_LOG_LEVEL, "debug_loglevel.gif"); 
    }

    private void registerImage(ImageRegistry registry, String key, String fileName) {
        try {
            Bundle bundle = Platform.getBundle(Activator.PLUGIN_ID);
            IPath path = new Path("icons/" + fileName);
            URL url = FileLocator.find(bundle, path, null);
            if (url != null) {
                ImageDescriptor desc = ImageDescriptor.createFromURL(url);
                registry.put(key, desc);
            }
        } catch (Exception e) {
            Activator.getEasyAntPlugin().log(IStatus.ERROR, e.getMessage(), e);
        }
    }

    private Image getImage(String key) {
        return getImageRegistry().get(key);
    }
    
    private ImageDescriptor getImageDescriptor(String key) {
         return getImageRegistry().getDescriptor(key);
    }
    
    private ImageRegistry getImageRegistry() {
        return Activator.getDefault().getImageRegistry();
    }


    

}
