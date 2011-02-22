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

import org.eclipse.core.internal.registry.ExtensionRegistry;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.ui.IStartup;

// http://wiki.eclipse.org/FAQ_How_do_I_make_my_plug-in_dynamic_aware%3F
// http://wiki.eclipse.org/FAQ_How_do_I_make_my_plug-in_dynamic_enabled%3F
public class Startup implements IStartup {

    public void earlyStartup() {
        //TODO add aspect to kill 
        //ExtensionRegistry -> private boolean checkReadWriteAccess(Object key, boolean persist) {
        
//      System.out.println("--- clean IvyDE extension");
//      IExtensionRegistry reg = RegistryFactory.getRegistry();
//      IExtension[] extensions = reg.getExtensions("org.apache.ivyde.eclipse");        
//      for (IExtension extension : extensions) {
//          if ("org.eclipse.ui.popupMenus".equals(extension.getExtensionPointUniqueIdentifier())) {
//              System.out.println("--> remove "+extension);
//              try{
//                  reg.removeExtension(extension, getUserToken());
//              }catch(Exception e){
//                  e.printStackTrace();
//              }
//              System.out.println("--> extension removed!");
//              break;
//          }
//      }
    }

    /*
    private Object getUserToken() {
        IExtensionRegistry registry = RegistryFactory.getRegistry();
        //return null; // require -Declipse.registry.nulltoken=true
        return ((ExtensionRegistry) registry).getTemporaryUserToken();
    }
*/
    
}
