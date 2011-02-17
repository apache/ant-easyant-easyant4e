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

import static org.junit.Assert.assertNotNull;

import org.eclipse.core.internal.registry.ExtensionRegistry;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.core.runtime.spi.IDynamicExtensionRegistry;
import org.junit.Test;

public class ExtensionRegistryTest {

	@Test
	public void testRemoveExtension() {
		IExtensionRegistry reg = RegistryFactory.getRegistry();		
		
		//assertNull(reg.getExtension("org.apache.ivyde.eclipse.ui.actions.ResolveAction"));
		//assertNull(reg.getExtension("org.eclipse.ui.popupMenus","org.apache.ivyde.eclipse.ui.actions.ResolveAction"));
		//assertNull(reg.getExtension("org.eclipse.ui","popupMenus","org.apache.ivyde.eclipse.ui.actions.ResolveAction"));

		IDynamicExtensionRegistry reg2 = (IDynamicExtensionRegistry)reg;
		for(IContributor contributor : reg2.getAllContributors()){			
			if("org.apache.ivyde.eclipse".equals(contributor.getName())){
				System.out.println("--> contributor "+contributor);
				reg2.removeContributor(contributor, ((ExtensionRegistry) reg2).getTemporaryUserToken());
				System.out.println("--> remove all extensions ");
			}
		}		
		
//		IExtension[] extensions = reg.getExtensions("org.apache.ivyde.eclipse");
//		assertNotNull(extensions);
//		for(IExtension extension:extensions){
//			if("org.eclipse.ui.popupMenus".equals(extension.getExtensionPointUniqueIdentifier())){
//				reg.removeExtension(extension, ((ExtensionRegistry) reg).getTemporaryUserToken());
//			}
//			System.out.println(extension.getNamespaceIdentifier() +" - "+extension.getExtensionPointUniqueIdentifier());			
//			final IConfigurationElement[] configElements = extension.getConfigurationElements();
//	        for (IConfigurationElement element:configElements) {
//	        	System.out.println("\t--> "+element.getName()+" : "+element.getAttribute("id"));		        	
//	        }		        
//		}
		
	
	}

//	private void removeExtension(String extensionPointId, String extensionId) {
//		IExtensionRegistry reg = RegistryFactory.getRegistry();
//		IExtension extension = reg.getExtension(extensionPointId, extensionId);
//		if (extension != null) {
//			reg.removeExtension(extension, getUserToken());
//		}
//	}
//
//	private Object getUserToken() {
//		IExtensionRegistry registry = RegistryFactory.getRegistry();
//		// return null; // require -Declipse.registry.nulltoken=true
//		return ((ExtensionRegistry) registry).getTemporaryUserToken();
//	}
}
