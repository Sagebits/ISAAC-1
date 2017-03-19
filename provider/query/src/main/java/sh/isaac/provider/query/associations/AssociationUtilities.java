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



package sh.isaac.provider.query.associations;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

//~--- non-JDK imports --------------------------------------------------------

import sh.isaac.api.Get;
import sh.isaac.api.LookupService;
import sh.isaac.api.chronicle.LatestVersion;
import sh.isaac.api.component.sememe.SememeChronology;
import sh.isaac.api.component.sememe.version.DynamicSememe;
import sh.isaac.api.component.sememe.version.SememeVersion;
import sh.isaac.api.component.sememe.version.dynamicSememe.DynamicSememeColumnInfo;
import sh.isaac.api.component.sememe.version.dynamicSememe.DynamicSememeUsageDescription;
import sh.isaac.api.constants.DynamicSememeConstants;
import sh.isaac.api.coordinate.StampCoordinate;
import sh.isaac.api.index.SearchResult;
import sh.isaac.model.sememe.DynamicSememeUtilityImpl;
import sh.isaac.model.sememe.dataTypes.DynamicSememeStringImpl;
import sh.isaac.provider.query.lucene.indexers.SememeIndexer;
import sh.isaac.utility.Frills;

//~--- classes ----------------------------------------------------------------

/**
 * {@link AssociationUtilities}
 *
 * @author <a href="mailto:daniel.armbrust.list@gmail.com">Dan Armbrust</a>
 */
public class AssociationUtilities {
   private static int associationSequence = Integer.MIN_VALUE;

   //~--- methods -------------------------------------------------------------

   /**
    * @param assemblageNidOrSequence
    */
   protected static int findTargetColumnIndex(int assemblageNidOrSequence) {
      DynamicSememeUsageDescription rdud = LookupService.get()
                                                        .getService(DynamicSememeUtilityImpl.class)
                                                        .readDynamicSememeUsageDescription(assemblageNidOrSequence);

      for (DynamicSememeColumnInfo rdci: rdud.getColumnInfo()) {
         if (rdci.getColumnDescriptionConcept()
                 .equals(DynamicSememeConstants.get().DYNAMIC_SEMEME_COLUMN_ASSOCIATION_TARGET_COMPONENT
                       .getUUID())) {
            return rdci.getColumnOrder();
         }
      }

      return Integer.MIN_VALUE;
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Get a particular associations
    * @param associationNid
    * @param stamp - optional - if not provided, uses the default from the config service
    * @return the found associationInstance, if present on the provided stamp path
    */
   public static Optional<AssociationInstance> getAssociation(int associationNid, StampCoordinate stamp) {
      StampCoordinate localStamp = (stamp == null) ? Get.configurationService()
                                                        .getDefaultStampCoordinate()
            : stamp;
      @SuppressWarnings("rawtypes")
      SememeChronology                          sc     = Get.sememeService()
                                                            .getSememe(associationNid);
      @SuppressWarnings("unchecked")
      Optional<LatestVersion<DynamicSememe<?>>> latest = sc.getLatestVersion(DynamicSememe.class, localStamp);

      if (latest.isPresent()) {
         return Optional.of(AssociationInstance.read(latest.get()
               .value(), stamp));
      }

      return Optional.empty();
   }

   public static boolean isAssociation(SememeChronology<? extends SememeVersion<?>> sc) {
      return Frills.isAssociation(sc);
   }

   /**
    * Get a list of all of the concepts that identify a type of association - returning their concept sequence identifier.
    * @return
    */
   public static Set<Integer> getAssociationConceptSequences() {
      HashSet<Integer> result = new HashSet<>();

      Get.sememeService()
         .getSememesFromAssemblage(getAssociationSequence())
         .forEach(associationC -> {
                     result.add(Get.identifierService()
                                   .getConceptSequence(associationC.getReferencedComponentNid()));
                  });
      return result;
   }

   private static int getAssociationSequence() {
      if (associationSequence == Integer.MIN_VALUE) {
         associationSequence = DynamicSememeConstants.get().DYNAMIC_SEMEME_ASSOCIATION_SEMEME
               .getSequence();
      }

      return associationSequence;
   }

   /**
    *
    * @param associationTypeConceptNid
    * @param stamp - optional - if not provided, uses the default from the config service
    * @return
    */
   public static List<AssociationInstance> getAssociationsOfType(int associationTypeConceptNid, StampCoordinate stamp) {
      ArrayList<AssociationInstance> results = new ArrayList<>();
      StampCoordinate localStamp             = (stamp == null) ? Get.configurationService()
                                                                    .getDefaultStampCoordinate()
            : stamp;

      Get.sememeService()
         .getSememesFromAssemblage(associationTypeConceptNid)
         .forEach(associationC -> {
                     @SuppressWarnings({ "unchecked", "rawtypes" })
                     Optional<LatestVersion<DynamicSememe<?>>> latest =
                        ((SememeChronology) associationC).getLatestVersion(DynamicSememe.class, localStamp);

                     if (latest.isPresent()) {
                        results.add(AssociationInstance.read(latest.get()
                              .value(), stamp));
                     }
                  });
      return results;
   }

   /**
    * Get all associations that originate on the specified componentNid
    * @param componentNid
    * @param stamp - optional - if not provided, uses the default from the config service
    */
   public static List<AssociationInstance> getSourceAssociations(int componentNid, StampCoordinate stamp) {
      ArrayList<AssociationInstance> results = new ArrayList<>();
      StampCoordinate localStamp             = (stamp == null) ? Get.configurationService()
                                                                    .getDefaultStampCoordinate()
            : stamp;

      Get.sememeService().getSememesForComponentFromAssemblages(componentNid, getAssociationConceptSequences()).forEach(associationC -> {
                     @SuppressWarnings({ "unchecked", "rawtypes" })
                     Optional<LatestVersion<DynamicSememe<?>>> latest =
                        ((SememeChronology) associationC).getLatestVersion(DynamicSememe.class, localStamp);

                     if (latest.isPresent()) {
                        results.add(AssociationInstance.read(latest.get()
                              .value(), stamp));
                     }
                  });
      return results;
   }

   /**
    * Get all association instances that have a target of the specified componentNid
    * @param componentNid
    * @param stamp - optional - if not provided, uses the default from the config service
    */

   // TODO should probabaly have a method here that takes in a target UUID, since that seems to be how I stored them?
   public static List<AssociationInstance> getTargetAssociations(int componentNid, StampCoordinate stamp) {
      ArrayList<AssociationInstance> result  = new ArrayList<>();
      SememeIndexer                  indexer = LookupService.getService(SememeIndexer.class);

      if (indexer == null) {
         throw new RuntimeException("Required index is not available");
      }

      UUID               uuid             = Get.identifierService()
                                               .getUuidPrimordialForNid(componentNid)
                                               .orElse(null);
      ArrayList<Integer> associationTypes = new ArrayList<>();

//    ArrayList<Integer> colIndex = new ArrayList<>();
      for (Integer associationTypeSequenece: getAssociationConceptSequences()) {
         associationTypes.add(associationTypeSequenece);

//       colIndex.add(findTargetColumnIndex(associationTypeSequenece));
      }

      try {
         // TODO when issue with colIndex restrictions is fixed, put it back.
         List<SearchResult> refexes = indexer.query(new DynamicSememeStringImpl(componentNid + ((uuid == null) ? ""
               : " OR " + uuid)),
                                                    false,
                                                    associationTypes.toArray(new Integer[associationTypes.size()]),
                                                    null,
                                                    Integer.MAX_VALUE,
                                                    null);

         for (SearchResult sr: refexes) {
            @SuppressWarnings("rawtypes")
            Optional<LatestVersion<DynamicSememe>> latest = Get.sememeService()
                                                               .getSnapshot(DynamicSememe.class,
                                                                     (stamp == null) ? Get.configurationService()
                                                                           .getDefaultStampCoordinate()
                  : stamp)
                                                               .getLatestSememeVersion(sr.getNid());

            if (latest.isPresent()) {
               result.add(AssociationInstance.read(latest.get()
                     .value(), stamp));
            }
         }
      } catch (Exception e) {
         throw new RuntimeException(e);
      }

      return result;
   }
}

