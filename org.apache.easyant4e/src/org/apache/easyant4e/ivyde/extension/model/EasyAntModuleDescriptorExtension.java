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
package org.apache.easyant4e.ivyde.extension.model;

import static org.apache.easyant4e.EasyAntConstants.EASYANT_BUILD_PLUGINS_ORG;
import static org.apache.easyant4e.EasyAntConstants.EASYANT_BUILD_TYPES_ORG;
import static org.apache.easyant4e.EasyAntConstants.EASYANT_PLUGIN_AS_ATTR;
import static org.apache.easyant4e.EasyAntConstants.EASYANT_PLUGIN_MANDATORY_ATTR;
import static org.apache.easyant4e.EasyAntConstants.EASYANT_PLUGIN_MODE_ATTR;
import static org.apache.easyant4e.EasyAntConstants.EASYANT_PLUGIN_MODE_IMPORT;
import static org.apache.easyant4e.EasyAntConstants.EASYANT_PLUGIN_MODE_INCLUDE;
import static org.apache.easyant4e.EasyAntConstants.EASYANT_PLUGIN_MODULE_ATTR;
import static org.apache.easyant4e.EasyAntConstants.EASYANT_PLUGIN_TAG;
import static org.apache.easyant4e.EasyAntConstants.EASYANT_PROPERTY_FILE_ATTR;
import static org.apache.easyant4e.EasyAntConstants.EASYANT_PROPERTY_NAME_ATTR;
import static org.apache.easyant4e.EasyAntConstants.EASYANT_PROPERTY_TAG;
import static org.apache.easyant4e.EasyAntConstants.EASYANT_PROPERTY_VALUE_ATTR;
import static org.apache.easyant4e.EasyAntConstants.EASYANT_TAG;
import static org.apache.easyant4e.EasyAntConstants.EASYANT_TYPE_ATTR;
import static org.apache.easyant4e.EasyAntConstants.IVY_INFO_TAG;
import static org.apache.easyant4e.EasyAntConstants.IVY_MODULE_TAG;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.easyant.core.descriptor.PropertyDescriptor;
import org.apache.easyant4e.Activator;
import org.apache.easyant4e.services.EasyantCoreService;
import org.apache.ivyde.common.ivyfile.IvyModuleDescriptorModel;
import org.apache.ivyde.common.model.IDocumentedValueProvider;
import org.apache.ivyde.common.model.IvyFile;
import org.apache.ivyde.common.model.IvyTag;
import org.apache.ivyde.common.model.IvyTagAttribute;
import org.apache.ivyde.eclipse.extension.ModuleDescriptorExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.google.inject.Inject;

/**
 * This class provide extension in order to add EasyAnt semantic information in
 * Ivy module descriptor model.
 */
public class EasyAntModuleDescriptorExtension implements ModuleDescriptorExtension {

    private IvyModuleDescriptorModel model;
    private IvyTag rootTag;
    private EasyantCoreService easyantCoreService;
        
    @Inject
    public void setEasyantCoreService(EasyantCoreService easyantCoreService) {
        this.easyantCoreService=easyantCoreService;
    }
    
    public EasyAntModuleDescriptorExtension() {
        super();
        Activator.getEasyAntPlugin().injectMembers(this);
    }

    public IvyModuleDescriptorModel contributeModel(IvyModuleDescriptorModel model) {
        this.model = model;
        this.rootTag = model.getRootIvyTag();
        contributeEasyAntTag();
        return model;
    }

    private IvyTag contributeEasyAntTag() {
        IvyTag easyAntTag = createEasyAndTag();
        return easyAntTag;
    }

    private IvyTag createEasyAndTag() {
        IvyTag easyAntTag = new IvyTag(EASYANT_TAG, "Describes how to build this module with EasyAnt.");
        easyAntTag.addAttribute(new IvyTagAttribute(EASYANT_TYPE_ATTR, "The name of the build type used to build this module.", true,
                createBuildTypesValueProvider()));
        model.addTag(easyAntTag);
        getDescriptionTag().addChildIvyTag(easyAntTag);
        easyAntTag = createEasyAndTagChilds(easyAntTag);
        return easyAntTag;
    }

    private IvyTag createEasyAndTagChilds(IvyTag easyAntTag) {
        createPropertyTag(rootTag, easyAntTag);
        createPluginTag(rootTag, easyAntTag);
        return easyAntTag;
    }

    private IvyTag getDescriptionTag() {
        return model.getIvyTag(IVY_INFO_TAG, IVY_MODULE_TAG);
    }

    private IDocumentedValueProvider createBuildTypesValueProvider() {
        final List<String> buildTypes = getBuildTypes();
        IDocumentedValueProvider buildTypesValueProvider = new IDocumentedValueProvider() {
            public String[] getValuesfor(IvyTagAttribute att, IvyFile ivyFile) {
                return buildTypes.toArray(new String[buildTypes.size()]);
            }
            public String getDocForValue(String value, IvyFile ivyFile) {
                String description = easyantCoreService.getBuildTypeDescription(value);
                return description;
            }
        };
        return buildTypesValueProvider;
    }

    private List<String> getBuildTypes() {
        // load default build types
        final ArrayList<String> buildTypes = new ArrayList<String>();
        Job job = new Job("Easyant - Getting list of Build Types (" + EASYANT_BUILD_TYPES_ORG + ") " + "from repository ...") {
            protected IStatus run(IProgressMonitor monitor) {
                for (String module : easyantCoreService.getAllCoreBuildTypes()) {
                    String shortName = module.substring(module.indexOf("#")+1);
                    buildTypes.add(shortName);
                    buildTypes.add(module);
                }
                return Status.OK_STATUS;
            }
        };
        job.setPriority(Job.LONG);
        job.schedule();
        return buildTypes;
    }

    private IvyTag createPropertyTag(IvyTag root, IvyTag parent) {
        // Contribute <property name="" value=""> Tag
        IvyTag propertyTag = new IvyTag(EASYANT_PROPERTY_TAG, "Define a property in order to configure the build type or plugins.");
        propertyTag.addAttribute(new IvyTagAttribute(EASYANT_PROPERTY_NAME_ATTR, "The name of property. (associated with the 'value' tag)", false,
                createPropertyNameValueProvider()));
        propertyTag.addAttribute(new IvyTagAttribute(EASYANT_PROPERTY_VALUE_ATTR, "The value of property identified by the 'name' tag", false,
                createPropertyValueProvider()));
        propertyTag.addAttribute(new IvyTagAttribute(EASYANT_PROPERTY_FILE_ATTR, "Define a property file containing some properties 'name=value' pairs.", false));
        model.addTag(propertyTag);
        parent.addChildIvyTag(propertyTag);
        return propertyTag;
    }

    private IvyTag createPluginTag(IvyTag root, IvyTag parent) {
        // Contribute <plugin module="" as=""> Tag
        IvyTag propertyTag = new IvyTag(EASYANT_PLUGIN_TAG, "Define a plugin used to contribute additionnal parts of the build process.");
        propertyTag.addAttribute(new IvyTagAttribute(EASYANT_PLUGIN_MODULE_ATTR, "The name of the plugin.", true,
                createPluginModuleValueProvider()));
        propertyTag.addAttribute(new IvyTagAttribute(EASYANT_PLUGIN_AS_ATTR, "The name of the prefix used to call plugin's targets, i.e. for 'as=\"myplugin\"': myplugin:target1", true));
        propertyTag.addAttribute(new IvyTagAttribute(EASYANT_PLUGIN_MODE_ATTR, "The kind of import.", true,
                createPluginModeValueProvider()));
        propertyTag.addAttribute(new IvyTagAttribute(EASYANT_PLUGIN_MANDATORY_ATTR, "The mandatory status", false,
                createPluginMandatoryValueProvider()));
        model.addTag(propertyTag);
        parent.addChildIvyTag(propertyTag);
        return propertyTag;
    }

    private IDocumentedValueProvider createPluginModuleValueProvider() {
        final List<String> buildPlugins = getBuildPlugins();
        IDocumentedValueProvider pluginModuleValueProvider = new IDocumentedValueProvider() {
            public String[] getValuesfor(IvyTagAttribute att, IvyFile ivyFile) {
                return buildPlugins.toArray(new String[buildPlugins.size()]);
            }
            public String getDocForValue(String value, IvyFile ivyFile) {
                String description = easyantCoreService.getPluginDescription(value);
                return description;
            }
        };
        return pluginModuleValueProvider;
    }

    private List<String> getBuildPlugins() {
        final ArrayList<String> buildPlugins = new ArrayList<String>();
        // load default build plugins
        Job job = new Job("Easyant - Getting list of Plugins (" + EASYANT_BUILD_PLUGINS_ORG + ") from repository ...") {
            protected IStatus run(IProgressMonitor monitor) {
                for (String module : easyantCoreService.getAllCorePlugins()) {
                    String shortName = module.substring(module.indexOf("#")+1);
                    buildPlugins.add(shortName);
                    buildPlugins.add(module);
                }
                return Status.OK_STATUS;
            }
        };
        job.setPriority(Job.LONG);
        job.schedule();
        return buildPlugins;
    }

    private IDocumentedValueProvider createPluginModeValueProvider() {
        IDocumentedValueProvider pluginModeValueProvider = new IDocumentedValueProvider() {
            public String[] getValuesfor(IvyTagAttribute att, IvyFile ivyFile) {
                return new String[] { EASYANT_PLUGIN_MODE_IMPORT, EASYANT_PLUGIN_MODE_INCLUDE };
            }
            public String getDocForValue(String value, IvyFile ivyFile) {
                //TODO get documentation for import and include
                return null;
            }
        };
        return pluginModeValueProvider;
    }

    private IDocumentedValueProvider createPluginMandatoryValueProvider() {
        IDocumentedValueProvider pluginMandatoryValueProvider = new IDocumentedValueProvider() {
            public String[] getValuesfor(IvyTagAttribute att, IvyFile ivyFile) {
                return new String[] { Boolean.TRUE.toString(), Boolean.FALSE.toString() };
            }
            public String getDocForValue(String value, IvyFile ivyFile) {
                return null;
            }
        };
        return pluginMandatoryValueProvider;
    }

    private IDocumentedValueProvider createPropertyValueProvider() {
        IDocumentedValueProvider propertyValueValueProvider = new IDocumentedValueProvider() {

            public String[] getValuesfor(IvyTagAttribute att, IvyFile ivyFile) {
                PropertyDescriptor propertyDescriptor = getPropertyDescriptor(att, ivyFile);
                if (propertyDescriptor != null) {
                    String value = propertyDescriptor.getDefaultValue();
                    if(value==null){
                        value = propertyDescriptor.getValue();
                    }
                    if(value!=null){
                        return new String[] { value };
                    }
                    return new String[] {};  
                }
                return new String[] {};
            }
            public String getDocForValue(String value, IvyFile ivyFile) {
                return null;
            }
        };
        return propertyValueValueProvider;
    }

    private IDocumentedValueProvider createPropertyNameValueProvider() {
        IDocumentedValueProvider propertyNameValueProvider = new IDocumentedValueProvider() {
            public String[] getValuesfor(IvyTagAttribute att, IvyFile ivyFile) {
                String buildType = getBuildType(ivyFile);
                if (buildType != null) {
                    List<PropertyDescriptor> propertyDescriptors = easyantCoreService.getPropertiesForBuildType(buildType);
                    String[] names = new String[propertyDescriptors.size()];
                    int i = 0;
                    for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                        names[i] = propertyDescriptor.getName();
                        i++;
                    }
                    return names;
                }
                return new String[] {};
            }
            public String getDocForValue(String value, IvyFile ivyFile) {
                String buildType = getBuildType(ivyFile);
                if(buildType!=null){
                    PropertyDescriptor propertyDescriptor = getPropertyDescriptor(buildType, value);
                    if(propertyDescriptor!=null){
                        return propertyDescriptor.getDescription();
                    }
                }
                return null;
            }
        };
        return propertyNameValueProvider;
    }

    List<PropertyDescriptor> propertyDescriptorsCache;
    private PropertyDescriptor getPropertyDescriptor(String buildType, String propertyName) {
        if(propertyDescriptorsCache==null){
            propertyDescriptorsCache= easyantCoreService.getPropertiesForBuildType(buildType);
        }
        for (PropertyDescriptor propertyDescriptor : propertyDescriptorsCache) {
            if (propertyName.equals(propertyDescriptor.getName())) {
                return propertyDescriptor;
            }
        }
        return null;
    }
    
    private PropertyDescriptor getPropertyDescriptor(IvyTagAttribute att, IvyFile ivyFile) {
        String buildType = getBuildType(ivyFile);
        if (ivyFile.getAllAttsValues().containsKey(EASYANT_PROPERTY_NAME_ATTR)) {
            String propertyNameValue = ivyFile.getAllAttsValues().get(EASYANT_PROPERTY_NAME_ATTR).toString();
            if (buildType != null && propertyNameValue != null) {
                List<PropertyDescriptor> propertyDescriptors = easyantCoreService.getPropertiesForBuildType(buildType);
                for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                    if (propertyNameValue.equals(propertyDescriptor.getName())) {
                        return propertyDescriptor;
                    }
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private String getBuildType(IvyFile ivyFile) {
        if (EASYANT_TAG.equals(ivyFile.getParentTagName())) {
            int[] indexes = ivyFile.getParentTagIndex();
            if (indexes != null && indexes.length > 0) {
                Map<String, String> allAttsValuesForEasyAntTag = ivyFile.getAllAttsValues(indexes[0] + 1);
                String typeValue = allAttsValuesForEasyAntTag.get(EASYANT_TYPE_ATTR);
                return typeValue;
            }
        }else if (IVY_INFO_TAG.equals(ivyFile.getParentTagName())) {
            Map<String, String> allAttsValuesForEasyAntTag = ivyFile.getAllAttsValues(ivyFile.getOffset() + 1);
            String typeValue = allAttsValuesForEasyAntTag.get(EASYANT_TYPE_ATTR);
            return typeValue;
        }
        return null;
    }
    
}
