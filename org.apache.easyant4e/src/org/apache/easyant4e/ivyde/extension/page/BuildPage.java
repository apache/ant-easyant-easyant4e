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
package org.apache.easyant4e.ivyde.extension.page;

import org.apache.easyant4e.Activator;
import org.apache.easyant4e.console.EasyAntConsole;
import org.apache.easyant4e.providers.ImageProvider;
import org.apache.ivyde.eclipse.extension.IvyEditorPage;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import com.google.inject.Inject;

public class BuildPage extends FormPage implements IvyEditorPage {

    private static final String ID = "build";
    private static final String NAME = "Build";
    private BuildLifecycleBlock buildLifecycleBlock;
    private ImageProvider imageProvider;
    private EasyAntConsole console;

    @Inject
    public void setEasyAntConsole(EasyAntConsole console){
        this.console = console;
    }
    
    @Inject
    public void setBuildLifecycleBlock(BuildLifecycleBlock buildLifecycleBlock) {
        this.buildLifecycleBlock = buildLifecycleBlock;
    }
    
    @Inject
    public void setImageProvider(ImageProvider imageProvider) {
        this.imageProvider = imageProvider;
    }

    public BuildPage() {
        super(ID, NAME);
        Activator.getEasyAntPlugin().injectMembers(this);
    }

    protected void createFormContent(IManagedForm managedForm) {
        super.createFormContent(managedForm);
        buildLifecycleBlock.setPage(this);
        
        final ScrolledForm form = managedForm.getForm();
        form.setText("EasyAnt Build");
        form.setBackgroundImage(imageProvider.getFormBackgroundImage());
        buildLifecycleBlock.createContent(managedForm);
        
        console.show(); 
    }

    public String getPageName() {
        return NAME;
    }

}
