/* 
 *  Copyright 2008-2009 the EasyAnt project
 * 
 *  See the NOTICE file distributed with this work for additional information
 *  regarding copyright ownership. 
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 *  Unless required by applicable law or agreed to in writing, software 
 *  distributed under the License is distributed on an "AS IS" BASIS, 
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and 
 *  limitations under the License.
 */
package org.apache.easyant4e.services;

import java.util.List;

import org.apache.easyant.core.descriptor.PropertyDescriptor;

/**
 * 
 * @author <a href="mailto:jerome@benois.fr">Jerome Benois</a>
 */
public interface EasyantCoreService {
	
	String getPluginsRepositoryPath();
	
	/**
	 * Install the local plugins repository
	 */
	void installPluginsRepository();
	
	String[] getAllCoreBuildTypes();
	
	String[] getAllCorePlugins();
	
	List<PropertyDescriptor> getPropertiesForBuildType(String buildTypeName);

	String getBuildTypeDescription(String buildTypeName);

	String getPluginDescription(String pluginName);
}