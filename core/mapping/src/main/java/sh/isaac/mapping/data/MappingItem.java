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

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

//~--- non-JDK imports --------------------------------------------------------

import javafx.application.Platform;

import javafx.beans.property.SimpleStringProperty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sh.isaac.api.Get;
import sh.isaac.api.component.sememe.version.DynamicSememe;
import sh.isaac.api.component.sememe.version.dynamicSememe.DynamicSememeData;
import sh.isaac.api.component.sememe.version.dynamicSememe.dataTypes.DynamicSememeUUID;
import sh.isaac.api.coordinate.EditCoordinate;
import sh.isaac.api.coordinate.StampCoordinate;
import sh.isaac.api.util.StringUtils;
import sh.isaac.utility.Frills;

//~--- classes ----------------------------------------------------------------

/**
 * {@link MappingItem}
 *
 * @author <a href="mailto:daniel.armbrust.list@gmail.com">Dan Armbrust</a>
 */
public class MappingItem
        extends MappingObject {
   private static final Logger                 LOG              = LoggerFactory.getLogger(MappingItem.class);
   private static final String                 NO_MAP_NAME      = "(not mapped)";
   public static final Comparator<MappingItem> sourceComparator = new Comparator<MappingItem>() {
      @Override
      public int compare(MappingItem o1, MappingItem o2) {
         return StringUtils.compareStringsIgnoreCase(o1.getSourceConceptProperty()
               .get(),
               o2.getSourceConceptProperty()
                 .get());
      }
   };
   public static final Comparator<MappingItem> targetComparator = new Comparator<MappingItem>() {
      @Override
      public int compare(MappingItem o1, MappingItem o2) {
         return StringUtils.compareStringsIgnoreCase(o1.getTargetConceptProperty()
               .get(),
               o2.getTargetConceptProperty()
                 .get());
      }
   };
   public static final Comparator<MappingItem> qualifierComparator = new Comparator<MappingItem>() {
      @Override
      public int compare(MappingItem o1, MappingItem o2) {
         return StringUtils.compareStringsIgnoreCase(o1.getQualifierConceptProperty()
               .get(),
               o2.getQualifierConceptProperty()
                 .get());
      }
   };

   //~--- fields --------------------------------------------------------------

   private transient boolean                    lazyLoadComplete         = false;
   private transient final SimpleStringProperty sourceConceptProperty    = new SimpleStringProperty();
   private transient final SimpleStringProperty targetConceptProperty    = new SimpleStringProperty();
   private transient final SimpleStringProperty qualifierConceptProperty = new SimpleStringProperty();
   private transient final SimpleStringProperty commentsProperty         = new SimpleStringProperty();
   private List<UUID>                           uuids;
   private int                                  sourceConceptNid, mappingSetSequence;
   private UUID                                 qualifierConcept, targetConcept;
   private DynamicSememeData[]                  data_;
   private transient UUID                       mappingSetIDConcept, sourceConcept;
   private transient int                        targetConceptNid, qualifierConceptNid;

   //~--- constructors --------------------------------------------------------

   protected MappingItem(DynamicSememe<?> sememe)
            throws RuntimeException {
      read(sememe);
   }

   //~--- methods -------------------------------------------------------------

   /**
    * Add a comment to this mapping set
    * @param commentText - the text of the comment
    * @return - the added comment
    * @throws IOException
    */
   public MappingItemComment addComment(String commentText,
         StampCoordinate stampCoord,
         EditCoordinate editCoord)
            throws IOException {
      // TODO do we want to utilize the other comment field (don't have to)
      return MappingItemCommentDAO.createMappingItemComment(this.getPrimordialUUID(),
            commentText,
            null,
            stampCoord,
            editCoord);
   }

   public void refreshCommentsProperty(StampCoordinate stampCoord) {
      Get.workExecutors().getExecutor().execute(() -> {
                     StringBuilder commentValue = new StringBuilder();

                     try {
                        List<MappingItemComment> comments = getComments(stampCoord);

                        if (comments.size() > 0) {
                           commentValue.append(comments.get(0)
                                 .getCommentText());
                        }

                        if (comments.size() > 1) {
                           commentValue.append(" (+" + Integer.toString(comments.size() - 1) + " more)");
                        }
                     } catch (IOException e) {
                        LOG.error("Error reading comments!", e);
                     }

                     Platform.runLater(() -> {
                                          commentsProperty.set(commentValue.toString());
                                       });
                  });
   }

   private void lazyLoad() {
      if (!lazyLoadComplete) {
         mappingSetIDConcept = Get.identifierService()
                                  .getUuidPrimordialForNid(mappingSetSequence)
                                  .get();
         setSourceConcept(Get.identifierService()
                             .getUuidPrimordialForNid(sourceConceptNid)
                             .get());

         // TODO remove this
         setEditorStatusConcept((((data_ != null) &&
                                  (data_.length > 2) &&
                                  (data_[2] != null)) ? ((DynamicSememeUUID) data_[2]).getDataUUID()
               : null));
         targetConceptNid    = getNidForUuidSafe(targetConcept);
         qualifierConceptNid = getNidForUuidSafe(qualifierConcept);
      }

      lazyLoadComplete = true;
   }

   private void read(DynamicSememe<?> sememe)
            throws RuntimeException {
      readStampDetails(sememe);
      mappingSetSequence = sememe.getAssemblageSequence();
      sourceConceptNid   = sememe.getReferencedComponentNid();
      uuids              = sememe.getUuidList();
      data_              = sememe.getData();
      setTargetConcept((((data_ != null) &&
                         (data_.length > 0) &&
                         (data_[0] != null)) ? ((DynamicSememeUUID) data_[0]).getDataUUID()
            : null));
      setQualifierConcept((((data_ != null) &&
                            (data_.length > 1) &&
                            (data_[1] != null)) ? ((DynamicSememeUUID) data_[1]).getDataUUID()
            : null));
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * @return Any comments attached to this mapping set.
    * @throws IOException
    */
   public List<MappingItemComment> getComments(StampCoordinate stampCoord)
            throws IOException {
      return MappingItemCommentDAO.getComments(getPrimordialUUID(), stampCoord);
   }

   public SimpleStringProperty getCommentsProperty(StampCoordinate stampCoord) {
      refreshCommentsProperty(stampCoord);
      return sourceConceptProperty;
   }

   public int getMapSetSequence() {
      return mappingSetSequence;
   }

   public UUID getMappingSetIDConcept() {
      lazyLoad();
      return mappingSetIDConcept;
   }

   /**
    * @return the primordialUUID of this Mapping Item.  Note that this doesn't uniquely identify a mapping item within the system
    * as changes to the mapping item will retain the same ID - there will now be multiple versions.  They will differ by date.
    */
   public UUID getPrimordialUUID() {
      return uuids.get(0);
   }

   public UUID getQualifierConcept() {
      return qualifierConcept;
   }

   //~--- set methods ---------------------------------------------------------

   private void setQualifierConcept(UUID qualifierConcept) {
      this.qualifierConcept = qualifierConcept;
      propertyLookup(qualifierConcept, qualifierConceptProperty);
   }

   //~--- get methods ---------------------------------------------------------

   public int getQualifierConceptNid() {
      lazyLoad();
      return qualifierConceptNid;
   }

   public SimpleStringProperty getQualifierConceptProperty() {
      lazyLoad();
      return qualifierConceptProperty;
   }

   public UUID getSourceConcept() {
      lazyLoad();
      return sourceConcept;
   }

   //~--- set methods ---------------------------------------------------------

   private void setSourceConcept(UUID sourceConcept) {
      this.sourceConcept = sourceConcept;
      propertyLookup(sourceConcept, sourceConceptProperty);
   }

   //~--- get methods ---------------------------------------------------------

   public int getSourceConceptNid() {
      return sourceConceptNid;
   }

   public SimpleStringProperty getSourceConceptProperty() {
      lazyLoad();
      return sourceConceptProperty;
   }

   public String getSummary() {
      return (isActive() ? "Active "
                         : "Retired ") + "Mapping: " + Frills.getDescription(sourceConcept).get() + "-" +
                                         Frills.getDescription(mappingSetIDConcept).get() + "-" +
                                         ((targetConcept == null) ? "not mapped"
            : Frills.getDescription(targetConcept)
                    .get()) + "-" + ((qualifierConcept == null) ? "no qualifier"
            : Frills.getDescription(qualifierConcept)
                    .get()) + "-" + ((editorStatusConcept == null) ? "no status"
            : Frills.getDescription(editorStatusConcept)
                    .get()) + "-" + uuids.get(0).toString();
   }

   public UUID getTargetConcept() {
      return targetConcept;
   }

   //~--- set methods ---------------------------------------------------------

   private void setTargetConcept(UUID targetConcept) {
      this.targetConcept = targetConcept;

      if (targetConcept == null) {
         targetConceptProperty.set(NO_MAP_NAME);
      } else {
         propertyLookup(targetConcept, targetConceptProperty);
      }
   }

   //~--- get methods ---------------------------------------------------------

   public int getTargetConceptNid() {
      lazyLoad();
      return targetConceptNid;
   }

   public SimpleStringProperty getTargetConceptProperty() {
      lazyLoad();
      return targetConceptProperty;
   }

   /**
    * @return the UUIDs of this Mapping Item.  Note that this doesn't uniquely identify a mapping item within the system
    * as changes to the mapping item will retain the same ID - there will now be multiple versions.  They will differ by date.
    * There will typically be only one entry in this list (identical to the value of {@link #getPrimordialUUID}
    */
   public List<UUID> getUUIDs() {
      return uuids;
   }
}

