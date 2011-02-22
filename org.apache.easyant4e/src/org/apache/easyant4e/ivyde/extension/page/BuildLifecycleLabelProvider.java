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

import org.apache.easyant.core.report.PhaseReport;
import org.apache.easyant.core.report.TargetReport;
import org.apache.easyant4e.providers.ImageProvider;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.graphics.Image;

import com.google.inject.Inject;

//import com.google.inject.Inject;

public class BuildLifecycleLabelProvider extends BaseLabelProvider implements ILabelProvider {

    @Inject 
    private ImageProvider imageProvider;
    
    public BuildLifecycleLabelProvider(){
        
    }
    
    public Image getImage(Object element) {
        if (element instanceof PhaseReport) {
            return imageProvider.getPhaseImage();
        }
        if (element instanceof TargetReport) {
            return imageProvider.getTargetImage();
        }
        return null;
    }

    public String getText(Object element) {
        if (element instanceof PhaseReport) {
            PhaseReport phaseReport = (PhaseReport) element;
            return phaseReport.getName();
        }
        if (element instanceof TargetReport) {
            TargetReport targetReport = (TargetReport) element;
            return targetReport.getName();
        }
        return element.toString();
    }

}
