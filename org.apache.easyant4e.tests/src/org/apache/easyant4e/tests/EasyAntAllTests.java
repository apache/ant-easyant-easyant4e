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
package org.apache.easyant4e.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * 
 * @author <a href="mailto:jerome@benois.fr">Jerome Benois</a>
 */
@RunWith(Suite.class)
@SuiteClasses(value = { 
		ImportProjectTest.class,
		StartupTest.class, 
		EasyantCoreServiceTest.class,
		EasyantProjectServiceTest.class	
		
})
public class EasyAntAllTests {

}