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
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

//~--- non-JDK imports --------------------------------------------------------

import javafx.beans.property.SimpleStringProperty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sh.isaac.MetaData;
import sh.isaac.api.Get;
import sh.isaac.api.chronicle.LatestVersion;
import sh.isaac.api.component.concept.ConceptVersion;
import sh.isaac.api.component.sememe.SememeChronology;
import sh.isaac.api.component.sememe.SememeType;
import sh.isaac.api.component.sememe.version.DescriptionSememe;
import sh.isaac.api.component.sememe.version.DynamicSememe;
import sh.isaac.api.component.sememe.version.dynamicSememe.dataTypes.DynamicSememeString;
import sh.isaac.api.component.sememe.version.dynamicSememe.dataTypes.DynamicSememeUUID;
import sh.isaac.api.constants.DynamicSememeConstants;
import sh.isaac.api.coordinate.StampCoordinate;
import sh.isaac.api.util.StringUtils;
import sh.isaac.utility.Frills;

//~--- classes ----------------------------------------------------------------

/**
 * {@link MappingSet}
 *
 * A Convenience class to hide unnecessary OTF bits from the Mapping APIs.
 *
 * @author <a href="mailto:daniel.armbrust.list@gmail.com">Dan Armbrust</a>
 */
public class MappingSet
        extends MappingObject {
   private static final Logger                LOG            = LoggerFactory.getLogger(MappingSet.class);
   public static final Comparator<MappingSet> nameComparator = new Comparator<MappingSet>() {
      @Override
      public int compare(MappingSet o1, MappingSet o2) {
         return StringUtils.compareStringsIgnoreCase(o1.getName(), o2.getName());
      }
   };
   public static final Comparator<MappingSet> purposeComparator = new Comparator<MappingSet>() {
      @Override
      public int compare(MappingSet o1, MappingSet o2) {
         return StringUtils.compareStringsIgnoreCase(o1.getPurpose(), o2.getPurpose());
      }
   };
   public static final Comparator<MappingSet> descriptionComparator = new Comparator<MappingSet>() {
      @Override
      public int compare(MappingSet o1, MappingSet o2) {
         return StringUtils.compareStringsIgnoreCase(o1.getDescription(), o2.getDescription());
      }
   };

   //~--- fields --------------------------------------------------------------

   private final SimpleStringProperty nameProperty        = new SimpleStringProperty();
   private final SimpleStringProperty purposeProperty     = new SimpleStringProperty();
   private final SimpleStringProperty descriptionProperty = new SimpleStringProperty();
   private final SimpleStringProperty inverseNameProperty = new SimpleStringProperty();

   // private String name, inverseName, description, purpose;
   private UUID primordialUUID;

   //~--- constructors --------------------------------------------------------

   /**
    *
    * Read an existing mapping set from the database
    *
    * @param refex DynamicSememeChronicleBI<?>
    * @throws IOException
    */
   protected MappingSet(DynamicSememe<?> refex, StampCoordinate stampCoord)
            throws RuntimeException {
      this.readFromRefex(refex, stampCoord);  // Sets Name, inverseName and Description, etc
   }

   //~--- methods -------------------------------------------------------------

   private void readFromRefex(DynamicSememe<?> refex, StampCoordinate stampCoord)
            throws RuntimeException {
      Optional<ConceptVersion<?>> mappingConcept = MappingSetDAO.getMappingConcept(refex, stampCoord);

      if (mappingConcept.isPresent()) {
         primordialUUID = mappingConcept.get()
                                        .getPrimordialUuid();
         readStampDetails(mappingConcept.get());

         // setEditorStatusConcept((refex.getData().length > 0 && refex.getData()[0] != null ? ((DynamicSememeUUID) refex.getData()[0]).getDataUUID() : null));
         if ((refex.getData().length > 0) && (refex.getData()[0] != null)) {
            setPurpose(((DynamicSememeString) refex.getData()[0]).getDataString());
         }

         Get.sememeService().getSememesForComponent(mappingConcept.get()
               .getNid()).filter(s -> s.getSememeType() == SememeType.DESCRIPTION).forEach(descriptionC -> {
                        if ((getName() != null) && (getDescription() != null) && (getInverseName() != null)) {
                           // noop... sigh... can't short-circuit in a forEach....
                        } else {
                           @SuppressWarnings({ "rawtypes", "unchecked" })
                           Optional<LatestVersion<DescriptionSememe<?>>> latest =
                              ((SememeChronology) descriptionC).getLatestVersion(DescriptionSememe.class, stampCoord);

                           // TODO handle contradictions
                           if (latest.isPresent()) {
                              DescriptionSememe<?> ds = latest.get()
                                                              .value();

                              if (ds.getDescriptionTypeConceptSequence() == MetaData.SYNONYM.getConceptSequence()) {
                                 if (Frills.isDescriptionPreferred(ds.getNid(), null)) {
                                    setName(ds.getText());
                                 } else

                                 // see if it is the inverse name
                                 {
                                    if (Get.sememeService()
                                           .getSememesForComponentFromAssemblage(ds.getNid(),
                                                 DynamicSememeConstants.get().DYNAMIC_SEMEME_ASSOCIATION_INVERSE_NAME
                                                       .getSequence())
                                           .anyMatch(sememeC -> {
                                                        return sememeC.isLatestVersionActive(stampCoord);
                                                     })) {
                                       setInverseName(ds.getText());
                                    }
                                 }
                              } else if (ds.getDescriptionTypeConceptSequence() ==
                                         MetaData.DEFINITION_DESCRIPTION_TYPE.getConceptSequence()) {
                                 if (Frills.isDescriptionPreferred(ds.getNid(), null)) {
                                    setDescription(ds.getText());
                                 }
                              }
                           }
                        }
                     });
      } else {
         String error = "cannot read mapping concept!";

         LOG.error(error);
         throw new RuntimeException(error);
      }
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * @return Any comments attached to this mapping set.
    * @throws RuntimeException
    */
   public List<MappingItemComment> getComments(StampCoordinate stampCoord)
            throws RuntimeException {
      return MappingItemCommentDAO.getComments(getPrimordialUUID(), stampCoord);
   }

   /**
    * @return - The user specified description of the mapping set.
    */
   public String getDescription() {
      return descriptionProperty.get();
   }

   //~--- set methods ---------------------------------------------------------

   /**
    * @param description - specify the description of the mapping set
    */
   public void setDescription(String description) {
      this.descriptionProperty.set(description);
   }

   //~--- get methods ---------------------------------------------------------

   public SimpleStringProperty getDescriptionProperty() {
      return descriptionProperty;
   }

   /**
    * @return - The inverse name of the mapping set - may return null
    */
   public String getInverseName() {
      return inverseNameProperty.get();
   }

   //~--- set methods ---------------------------------------------------------

   /**
    * @param inverseName - Change the inverse name of the mapping set
    */
   public void setInverseName(String inverseName) {
      this.inverseNameProperty.set(inverseName);
   }

   //~--- get methods ---------------------------------------------------------

   public SimpleStringProperty getInverseNameProperty() {
      return inverseNameProperty;
   }

   public List<MappingItem> getMappingItems(StampCoordinate stampCoord) {
      List<MappingItem> mappingItems = null;

      try {
         mappingItems = MappingItemDAO.getMappingItems(this.getPrimordialUUID(), stampCoord);
      } catch (Exception e) {
         LOG.error("Error retrieving Mapping Items for " + this.getName(), e);
         mappingItems = new ArrayList<MappingItem>();
      }

      return mappingItems;
   }

   /**
    * @return the name of the mapping set
    */
   public String getName() {
      return nameProperty.get();
   }

   //~--- set methods ---------------------------------------------------------

   /**
    * @param name - Change the name of the mapping set
    */
   public void setName(String name) {
      this.nameProperty.set(name);
   }

   //~--- get methods ---------------------------------------------------------

   public SimpleStringProperty getNameProperty() {
      return nameProperty;
   }

   /**
    * @return the identifier of this mapping set
    */
   public UUID getPrimordialUUID() {
      return primordialUUID;
   }

   /**
    * @return - the 'purpose' of the mapping set - may be null
    */
   public String getPurpose() {
      return purposeProperty.get();
   }

   //~--- set methods ---------------------------------------------------------

   /**
    * @param purpose - The 'purpose' of the mapping set. May specify null.
    */
   public void setPurpose(String purpose) {
      purposeProperty.set(purpose);
   }

   //~--- get methods ---------------------------------------------------------

   public SimpleStringProperty getPurposeProperty() {
      return purposeProperty;
   }

   /**
    * @return The summary of the mapping set
    */
   public String getSummary(StampCoordinate stampCoord) {
      List<MappingItem> mappingItems;

      mappingItems = this.getMappingItems(stampCoord);
      return Integer.toString(mappingItems.size()) + " Mapping Items";
   }
}

