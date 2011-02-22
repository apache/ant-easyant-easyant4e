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

public interface EasyAntConstants {

    
    public static final String IVY_MODULE_TAG = "ivy-module";
    public static final String IVY_INFO_TAG = "info";
    //public static final String IVY_DESCRIPTION_TAG = "description";

    public static final String EASYANT_TAG = "ea:build";
    public static final String EASYANT_TYPE_ATTR = "type";

    public static final String EASYANT_PROPERTY_TAG = "ea:property";
    public static final String EASYANT_PROPERTY_NAME_ATTR = "name";
    public static final String EASYANT_PROPERTY_VALUE_ATTR = "value";
    public static final String EASYANT_PROPERTY_FILE_ATTR = "file";

    public static final String EASYANT_PLUGIN_TAG = "ea:plugin";
    public static final String EASYANT_PLUGIN_MODULE_ATTR = "module";
    public static final String EASYANT_PLUGIN_AS_ATTR = "as";
    public static final String EASYANT_PLUGIN_MODE_ATTR = "mode";
    public static final String EASYANT_PLUGIN_MODE_INCLUDE = "include";
    public static final String EASYANT_PLUGIN_MODE_IMPORT = "import";
    public static final String EASYANT_PLUGIN_MANDATORY_ATTR = "mandatory";

    public static final String EASYANT_BUILD_TYPES_ORG = "org.apache.easyant.buildtypes";
    public static final String EASYANT_BUILD_PLUGINS_ORG = "org.apache.easyant.plugins";

    public static final String PLUGINS_SETTINGS = "ivysettings.xml";
    
    //Ant log level
    public static final int ANT_LOGLEVEL_ERR = 0;
    public static final int ANT_LOGLEVEL_WARN = 1;
    public static final int ANT_LOGLEVEL_INFO = 2;
    public static final int ANT_LOGLEVEL_VERBOSE = 3;
    public static final int ANT_LOGLEVEL_DEBUG = 4;

}
