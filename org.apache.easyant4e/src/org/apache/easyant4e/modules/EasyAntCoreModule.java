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

import static org.apache.easyant4e.EasyAntConstants.PLUGINS_SETTINGS;

import java.net.URL;

import org.apache.easyant.core.EasyAntConfiguration;
import org.apache.easyant.core.EasyAntEngine;
import org.apache.easyant.core.factory.EasyantConfigurationFactory;
import org.apache.easyant4e.Activator;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Singleton;

public class EasyAntCoreModule extends AbstractModule{

    @Override
    protected void configure() {    
        bind(EasyAntEngine.class).toProvider(new Provider<EasyAntEngine>() {
            public EasyAntEngine get() {
                URL url = Activator.getDefault().getBundle().getResource(PLUGINS_SETTINGS);     
                EasyAntConfiguration configuration= EasyantConfigurationFactory.getInstance().createDefaultConfiguration();
                configuration.setEasyantIvySettingsUrl(url);
                return new EasyAntEngine(configuration);
            }
    
        }).in(Singleton.class);
    }

}
