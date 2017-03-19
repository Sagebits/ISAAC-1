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



package sh.isaac.mapping.data;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

//~--- non-JDK imports --------------------------------------------------------

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sh.isaac.MetaData;
import sh.isaac.api.component.concept.ConceptSpecification;
import sh.isaac.api.component.sememe.SememeChronology;
import sh.isaac.api.component.sememe.version.SememeVersion;
import sh.isaac.mapping.constants.IsaacMappingConstants;
import sh.isaac.utility.Frills;
import sh.isaac.utility.SimpleDisplayConcept;

//~--- classes ----------------------------------------------------------------

/**
 * {@link MappingUtils}
 *
 * @author <a href="mailto:daniel.armbrust.list@gmail.com">Dan Armbrust</a>
 */
public class MappingUtils {
   protected static final Logger LOG = LoggerFactory.getLogger(MappingUtils.class);
   public static final HashMap<String, ConceptSpecification> CODE_SYSTEM_CONCEPTS = new HashMap<String,
                                                                                       ConceptSpecification>();

   //~--- static initializers -------------------------------------------------

   static {
      CODE_SYSTEM_CONCEPTS.put("SNOMED CT", MetaData.SNOMED_CT_CORE_MODULES);
      CODE_SYSTEM_CONCEPTS.put("SNOMED CT US Extension", MetaData.US_EXTENSION_MODULES);
      CODE_SYSTEM_CONCEPTS.put("LOINC", MetaData.LOINC_MODULES);
      CODE_SYSTEM_CONCEPTS.put("RxNorm", MetaData.RXNORM_MODULES);
      CODE_SYSTEM_CONCEPTS.put("VHAT", MetaData.VHA_MODULES);
   }

   //~--- get methods ---------------------------------------------------------

   public static List<SimpleDisplayConcept> getCodeSystems() {
      List<SimpleDisplayConcept> codeSystems = new ArrayList<SimpleDisplayConcept>();

      CODE_SYSTEM_CONCEPTS.entrySet()
                          .forEach((item) -> codeSystems.add(new SimpleDisplayConcept(item.getKey(),
                                item.getValue().getNid())));
      return codeSystems;
   }

   public static boolean isMapping(SememeChronology<? extends SememeVersion<?>> sc) {
      return Frills.isMapping(sc);
   }

   public static List<SimpleDisplayConcept> getQualifierConcepts()
            throws IOException {
      ArrayList<SimpleDisplayConcept> result = new ArrayList<>();

      for (Integer conSequence: Frills.getAllChildrenOfConcept(IsaacMappingConstants.get().MAPPING_EQUIVALENCE_TYPES
            .getSequence(),
            true,
            false)) {
         result.add(new SimpleDisplayConcept(conSequence));
      }

      Collections.sort(result);
      return result;
   }

   public static List<SimpleDisplayConcept> getStatusConcepts()
            throws IOException {
      // TODO why is this commented out / broken?
      ArrayList<SimpleDisplayConcept> result = new ArrayList<>();

//    for (Integer conSequence : Frills.getAllChildrenOfConcept(IsaacMappingConstants.get().MAPPING_STATUS.getSequence(), true, false))
//    {
//            result.add(new SimpleDisplayConcept(conSequence));
//    }

      Collections.sort(result);
      return result;
   }
}

