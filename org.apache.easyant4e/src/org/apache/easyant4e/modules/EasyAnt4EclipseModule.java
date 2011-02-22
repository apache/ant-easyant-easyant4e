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
package org.apache.easyant4e.modules;

import org.apache.easyant4e.console.EasyAntConsole;
import org.apache.easyant4e.console.EasyAntConsoleImpl;
import org.apache.easyant4e.ivyde.extension.page.BuildLifeCycleContentProvider;
import org.apache.easyant4e.ivyde.extension.page.BuildLifecycleBlock;
import org.apache.easyant4e.ivyde.extension.page.BuildLifecycleLabelProvider;
import org.apache.easyant4e.ivyde.extension.page.PhaseDetailsPage;
import org.apache.easyant4e.ivyde.extension.page.TargetDetailsPage;
import org.apache.easyant4e.providers.ImageProvider;
import org.apache.easyant4e.providers.ImageProviderImpl;
import org.apache.easyant4e.services.EasyantCoreService;
import org.apache.easyant4e.services.EasyantCoreServiceImpl;
import org.apache.easyant4e.services.EasyantProjectService;
import org.apache.easyant4e.services.EasyantProjectServiceImpl;
import org.apache.easyant4e.wizards.EasyAntImportWizardPage;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class EasyAnt4EclipseModule extends AbstractModule{


    @Override
    protected void configure() {        
        bind(EasyAntConsole.class).to(EasyAntConsoleImpl.class).in(Singleton.class);
        bind(ImageProvider.class).to(ImageProviderImpl.class).in(Singleton.class);
        bind(EasyantCoreService.class).to(EasyantCoreServiceImpl.class).in(Singleton.class);
        bind(EasyantProjectService.class).to(EasyantProjectServiceImpl.class).in(Singleton.class);
        
        bind(BuildLifecycleBlock.class).in(Singleton.class);
        bind(BuildLifeCycleContentProvider.class).in(Singleton.class);
        bind(BuildLifecycleLabelProvider.class).in(Singleton.class);        
        bind(PhaseDetailsPage.class).in(Singleton.class);
        bind(TargetDetailsPage.class).in(Singleton.class);
        
        bind(EasyAntImportWizardPage.class);//.in(Singleton.class);
        
        // Bind to instance
        //binder.bind(Appendable.class).toInstance(System.out);
        
    }

    
}
