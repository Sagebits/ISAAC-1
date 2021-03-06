/* 
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * You may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributions from 2013-2017 where performed either by US government 
 * employees, or under US Veterans Health Administration contracts. 
 *
 * US Veterans Health Administration contributions by government employees
 * are work of the U.S. Government and are not subject to copyright
 * protection in the United States. Portions contributed by government 
 * employees are USGovWork (17USC §105). Not subject to copyright. 
 * 
 * Contribution by contractors to the US Veterans Health Administration
 * during this period are contractually contributed under the
 * Apache License, Version 2.0.
 *
 * See: https://www.usa.gov/government-works
 * 
 * Contributions prior to 2013:
 *
 * Copyright (C) International Health Terminology Standards Development Organisation.
 * Licensed under the Apache License, Version 2.0.
 *
 */



package sh.isaac.converters.sharedUtils.config;

//~--- non-JDK imports --------------------------------------------------------

import org.jvnet.hk2.annotations.Contract;

import sh.isaac.pombuilder.converter.ConverterOptionParam;

//~--- interfaces -------------------------------------------------------------

/**
 *
 * {@link ConfigOptionsDescriptor}
 *
 * A class that should be implemented in each conversion mojo that describes what options
 * it takes in via maven, so that we can pick them up via a GUI, later.
 *
 * @author <a href="mailto:daniel.armbrust.list@gmail.com">Dan Armbrust</a>
 */
@Contract
public interface ConfigOptionsDescriptor {
   /**
    * Which options are applicable for this converter.
    *
    * @return the config options
    */
   public ConverterOptionParam[] getConfigOptions();

   /**
    * What converter is this config option describing.  This should be set to the name of the @Mojo that is is describing.
    *
    * @return the name
    */
   public String getName();
}

