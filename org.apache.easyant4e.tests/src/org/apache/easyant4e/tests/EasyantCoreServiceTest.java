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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.easyant4e.Activator;
import org.apache.easyant4e.services.EasyantCoreService;
import org.junit.Before;
import org.junit.Test;

public class EasyantCoreServiceTest extends AbstractEasyAntTest {

    @Test
    public void testGetPluginsRepositoryPath() {
        String pluginsRepositoryPath = coreService.getPluginsRepositoryPath();
        assertNotNull(pluginsRepositoryPath);
        String pluginPath = System.getProperty("user.home") + "/.easyant/easyant-repository";
        assertEquals(pluginPath, pluginsRepositoryPath);
    }

    @Test
    public void testInstallPluginsRepository() {
        String pluginsRepositoryPath = coreService.getPluginsRepositoryPath();
        File pluginsRepositoryDir = new File(pluginsRepositoryPath);
        assertTrue(deleteDirectory(pluginsRepositoryDir));
        assertFalse(pluginsRepositoryDir.exists());
        coreService.installPluginsRepository();
        assertTrue(pluginsRepositoryDir.exists());
    }

    private boolean deleteDirectory(File path) {
        boolean resultat = true;

        if (path.exists()) {
            File[] files = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    resultat &= deleteDirectory(files[i]);
                } else {
                    resultat &= files[i].delete();
                }
            }
        }
        resultat &= path.delete();
        return (resultat);
    }

}
