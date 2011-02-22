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
package org.apache.easyant4e.ivyde.extension;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.easyant4e.Activator;
import org.eclipse.core.runtime.IStatus;

/**
 * This class unpack the default EasyAnt repository in local file system.
 */
public class RepositoryInstaller {
    static final int BUFFER_SIZE = 102400;
    static final String REPOSITORY_ARCHIVE_PATH = "repository.zip";
    private URL interpreterZipUrl;

    public void install(File installDir) {
        this.interpreterZipUrl = Activator.getDefault().getBundle().getResource(REPOSITORY_ARCHIVE_PATH);
        try {
            unPackArchive(installDir);
        } catch (IOException e) {
            Activator.getEasyAntPlugin().log(IStatus.ERROR, "Cannot install EasyAnt repository", e);
        }
    }

    private void unPackArchive(File installDir) throws IOException {
        InputStream inputStream = this.interpreterZipUrl.openStream();
        ZipInputStream zipFileStream = new ZipInputStream(inputStream);
        ZipEntry zipEntry = zipFileStream.getNextEntry();
        while (zipEntry != null) {
            File destination = new File(installDir.getPath(), zipEntry.getName());
            if (!zipEntry.isDirectory()) {
                /*
                 * Copy files (and make sure parent directory exist)
                 */
                File parentFile = destination.getParentFile();
                boolean parentFileExist = isFileExists(parentFile);
                if (!parentFileExist) {
                    parentFileExist = parentFile.mkdirs();
                }
                if (parentFileExist) {
                    OutputStream os = null;
                    try {
                        os = new FileOutputStream(destination);
                        byte[] buffer = new byte[BUFFER_SIZE];
                        while (true) {
                            int len = zipFileStream.read(buffer);
                            if (zipFileStream.available() == 0) {
                                break;
                            }
                            os.write(buffer, 0, len);
                        }
                    } finally {
                        if (null != os) {
                            os.close();
                        }
                    }
                } else {
                    Activator.getEasyAntPlugin().log(IStatus.ERROR,
                            "Installing EasyAnt repository, but " + parentFile + " already exists!");
                }
            } else {
                boolean created = destination.mkdirs();
                if (!created) {
                    Activator.getEasyAntPlugin().log(IStatus.ERROR,
                            "Installing EasyAnt repository. Cannot create directory: " + destination);
                }
            }
            zipFileStream.closeEntry();
            zipEntry = zipFileStream.getNextEntry();
        }
        inputStream.close();
        zipFileStream.close();
    }

    private boolean isFileExists(File file) {
        return (null != file && file.exists());
    }
}
