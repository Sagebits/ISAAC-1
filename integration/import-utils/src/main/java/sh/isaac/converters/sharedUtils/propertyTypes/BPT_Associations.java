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



package sh.isaac.converters.sharedUtils.propertyTypes;

//~--- JDK imports ------------------------------------------------------------

import java.util.HashSet;
import java.util.UUID;

//~--- non-JDK imports --------------------------------------------------------

import sh.isaac.api.component.sememe.version.dynamicSememe.DynamicSememeColumnInfo;

//~--- classes ----------------------------------------------------------------

/**
 * The Class BPT_Associations.
 *
 * @author Daniel Armbrust
 *
 * Associations get loaded using the new add-on association API (internally represented as sememes)
 *
 * These get ignored by the classifier, for example.
 */
public class BPT_Associations
        extends PropertyType {
   /** The all associations. */
   private static HashSet<UUID> allAssociations = new HashSet<>();

   //~--- constructors --------------------------------------------------------

   /**
    * Instantiates a new BP T associations.
    */
   public BPT_Associations() {
      super("Association Types", false, null);
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Adds the property.
    *
    * @param property the property
    * @return the property
    */
   @Override
   public Property addProperty(Property property) {
      if (!(property instanceof PropertyAssociation)) {
         throw new RuntimeException("Must add PropertyAssociation objects to BPT_Associations type");
      }

      final Property p = super.addProperty(property);

      allAssociations.add(p.getUUID());  // For stats, later
      return p;
   }

   /**
    * Adds the property.
    *
    * @param propertyNameFSN the property name FSN
    * @return the property
    */

   // Override all of these as unsupported, as, we require only PropertyAssociation object here.
   @Override
   public Property addProperty(String propertyNameFSN) {
      throw new UnsupportedOperationException();
   }

   /**
    * Adds the property.
    *
    * @param propertyNameFSN the property name FSN
    * @param propertySubType the property sub type
    * @return the property
    */
   @Override
   public Property addProperty(String propertyNameFSN, int propertySubType) {
      throw new UnsupportedOperationException();
   }

   /**
    * Adds the property.
    *
    * @param propertyNameFSN the property name FSN
    * @param minVersion the min version
    * @param maxVersion the max version
    * @return the property
    */
   @Override
   public Property addProperty(String propertyNameFSN, int minVersion, int maxVersion) {
      throw new UnsupportedOperationException();
   }

   /**
    * Adds the property.
    *
    * @param sourcePropertyNameFSN the source property name FSN
    * @param sourcePropertyPreferredName the source property preferred name
    * @param sourcePropertyDefinition the source property definition
    * @return the property
    */
   @Override
   public Property addProperty(String sourcePropertyNameFSN,
                               String sourcePropertyPreferredName,
                               String sourcePropertyDefinition) {
      throw new UnsupportedOperationException();
   }

   /**
    * Adds the property.
    *
    * @param propertyNameFSN the property name FSN
    * @param minVersion the min version
    * @param maxVersion the max version
    * @param disabled the disabled
    * @return the property
    */
   @Override
   public Property addProperty(String propertyNameFSN, int minVersion, int maxVersion, boolean disabled) {
      throw new UnsupportedOperationException();
   }

   /**
    * Adds the property.
    *
    * @param sourcePropertyNameFSN the source property name FSN
    * @param sourcePropertyAltName the source property alt name
    * @param sourcePropertyDefinition the source property definition
    * @param disabled the disabled
    * @param propertySubType the property sub type
    * @param dataColumnForDynamicRefex the data column for dynamic refex
    * @return the property
    */
   @Override
   public Property addProperty(String sourcePropertyNameFSN,
                               String sourcePropertyAltName,
                               String sourcePropertyDefinition,
                               boolean disabled,
                               int propertySubType,
                               DynamicSememeColumnInfo[] dataColumnForDynamicRefex) {
      throw new UnsupportedOperationException();
   }

   /**
    * Adds the property.
    *
    * @param sourcePropertyNameFSN the source property name FSN
    * @param sourcePropertyPreferredName the source property preferred name
    * @param sourcePropertyDefinition the source property definition
    * @param minVersion the min version
    * @param maxVersion the max version
    * @param disabled the disabled
    * @param propertySubType the property sub type
    * @return the property
    */
   @Override
   public Property addProperty(String sourcePropertyNameFSN,
                               String sourcePropertyPreferredName,
                               String sourcePropertyDefinition,
                               int minVersion,
                               int maxVersion,
                               boolean disabled,
                               int propertySubType) {
      throw new UnsupportedOperationException();
   }

   /**
    * Register as association.
    *
    * @param uuid the uuid
    */
   public static void registerAsAssociation(UUID uuid) {
      allAssociations.add(uuid);
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Checks if association.
    *
    * @param uuid the uuid
    * @return true, if association
    */
   public static boolean isAssociation(UUID uuid) {
      return allAssociations.contains(uuid);
   }
}
