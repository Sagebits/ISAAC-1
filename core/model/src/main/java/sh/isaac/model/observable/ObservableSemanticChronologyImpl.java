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
package sh.isaac.model.observable;

//~--- non-JDK imports --------------------------------------------------------
import javafx.beans.property.IntegerProperty;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sh.isaac.api.State;
import sh.isaac.api.chronicle.LatestVersion;
import sh.isaac.api.chronicle.Version;
import sh.isaac.api.chronicle.VersionType;
import sh.isaac.api.component.semantic.version.ComponentNidVersion;
import sh.isaac.api.coordinate.EditCoordinate;
import sh.isaac.model.observable.version.ObservableDescriptionVersionImpl;
import sh.isaac.model.semantic.version.DescriptionVersionImpl;
import sh.isaac.api.component.semantic.version.DescriptionVersion;
import sh.isaac.api.component.semantic.version.LogicGraphVersion;
import sh.isaac.api.component.semantic.version.LongVersion;
import sh.isaac.api.component.semantic.version.StringVersion;
import sh.isaac.api.coordinate.StampCoordinate;
import sh.isaac.api.externalizable.ByteArrayDataBuffer;
import sh.isaac.api.externalizable.IsaacObjectType;
import sh.isaac.api.observable.ObservableVersion;
import sh.isaac.model.observable.version.ObservableComponentNidVersionImpl;
import sh.isaac.model.observable.version.ObservableLogicGraphVersionImpl;
import sh.isaac.model.observable.version.ObservableLongVersionImpl;
import sh.isaac.model.observable.version.ObservableSemanticVersionImpl;
import sh.isaac.model.observable.version.ObservableStringVersionImpl;
import sh.isaac.api.component.semantic.SemanticChronology;
import sh.isaac.api.component.semantic.version.MutableSemanticVersion;
import sh.isaac.api.component.semantic.version.SemanticVersion;
import sh.isaac.api.observable.semantic.ObservableSemanticChronology;

//~--- classes ----------------------------------------------------------------
/**
 * The Class ObservableSemanticChronologyImpl.
 *
 * @author kec
 */
public class ObservableSemanticChronologyImpl
        extends ObservableChronologyImpl
        implements ObservableSemanticChronology {

   private static final Logger LOG = LogManager.getLogger();

   /**
    * The assemblage nid property.
    */
   private IntegerProperty assemblageNidProperty;

   /**
    * The referenced component nid property.
    */
   private IntegerProperty referencedComponentNidProperty;

   //~--- constructors --------------------------------------------------------
   /**
    * Instantiates a new observable sememe chronology impl.
    *
    * @param chronicledObjectLocal the chronicled object local
    */
   public ObservableSemanticChronologyImpl(SemanticChronology chronicledObjectLocal) {
      super(chronicledObjectLocal);
   }

   protected SemanticChronology getSemanticChronology() {
      return (SemanticChronology) this.chronicledObjectLocal;
   }
   //~--- methods -------------------------------------------------------------

   /**
    * Assemblage sequence property.
    *
    * @return the integer property
    */
   @Override
   public IntegerProperty assemblageNidProperty() {
      if (this.assemblageNidProperty == null) {
         this.assemblageNidProperty = new CommitAwareIntegerProperty(this,
                 ObservableFields.ASSEMBLAGE_NID_FOR_CHRONICLE.toExternalString(),
                 getAssemblageNid());
      }

      return this.assemblageNidProperty;
   }

   /**
    * Creates the mutable version.
    *
    * @param stampSequence the stamp sequence
    * @return the m
    */
   @Override
   public <V extends Version> V createMutableVersion(int stampSequence) {
      return (V) wrapInObservable(getSemanticChronology().createMutableVersion(stampSequence));
   }

   /**
    * Creates the mutable version.
    *
    * @param status the status
    * @param ec the ec
    * @return the m
    */
   @Override
   public <V extends Version> V createMutableVersion(State status, EditCoordinate ec) {
      return (V) wrapInObservable(getSemanticChronology().createMutableVersion(status, ec));
   }

   /**
    * Referenced component nid property.
    *
    * @return the integer property
    */
   @Override
   public IntegerProperty referencedComponentNidProperty() {
      if (this.referencedComponentNidProperty == null) {
         this.referencedComponentNidProperty = new CommitAwareIntegerProperty(this,
                 ObservableFields.REFERENCED_COMPONENT_NID_FOR_SEMANTIC_CHRONICLE.toExternalString(),
                 getReferencedComponentNid());
      }

      return this.referencedComponentNidProperty;
   }

   @Override
   protected <OV extends ObservableVersion>
           OV wrapInObservable(Version version) {
              SemanticVersion sememeVersion = (SemanticVersion) version;
      switch (sememeVersion.getChronology().getVersionType()) {
         case DESCRIPTION:
            return (OV) new ObservableDescriptionVersionImpl((DescriptionVersionImpl) sememeVersion,
                    (ObservableSemanticChronology) this);
         case COMPONENT_NID:
            return (OV) new ObservableComponentNidVersionImpl((ComponentNidVersion) sememeVersion,
                    (ObservableSemanticChronology) this);
         case MEMBER:
            return (OV) new ObservableSemanticVersionImpl(sememeVersion,
                    (ObservableSemanticChronology) this);
         case LONG:
            return (OV) new ObservableLongVersionImpl((LongVersion) sememeVersion,
                    (ObservableSemanticChronology) this);
         case STRING:
            return (OV) new ObservableStringVersionImpl((StringVersion) sememeVersion,
                    (ObservableSemanticChronology) this);
         case LOGIC_GRAPH:
            return (OV) new ObservableLogicGraphVersionImpl((LogicGraphVersion) sememeVersion,
                    (ObservableSemanticChronology) this);
         case DYNAMIC:
            LOG.warn("Incomplete implementation of sememe: " + 
                    sememeVersion.getClass().getSimpleName() + " " + sememeVersion);
            return (OV) new ObservableSemanticVersionImpl(sememeVersion,
                    (ObservableSemanticChronology) this);
            
           // fall through to default...
         case UNKNOWN:
         default:
            throw new UnsupportedOperationException("Can't convert to observable "
                    + sememeVersion.getChronology().getVersionType() + "from \n:    "
                    + sememeVersion);
      }

   }

   //~--- get methods ---------------------------------------------------------
   /**
    * Gets the assemblage nid.
    *
    * @return the assemblage nid
    */
   @Override
   public int getAssemblageNid() {
      if (this.assemblageNidProperty != null) {
         return this.assemblageNidProperty.get();
      }

      return getSemanticChronology().getAssemblageNid();
   }

   /**
    * Gets the observable version list.
    *
    * @return the observable version list
    */
   @Override
   protected ObservableList<ObservableVersion> getObservableVersionList() {
      if (this.versionListProperty != null && this.versionListProperty.get() != null) {
         return this.versionListProperty.get();
      }
      final ObservableList<ObservableVersion> observableList = FXCollections.observableArrayList();

      this.chronicledObjectLocal.getVersionList().stream().forEach((sememeVersion) -> {
         observableList.add(wrapInObservable((SemanticVersion) sememeVersion));
      });
      return observableList;
   }

   /**
    * Gets the referenced component nid.
    *
    * @return the referenced component nid
    */
   @Override
   public int getReferencedComponentNid() {
      if (this.referencedComponentNidProperty != null) {
         return this.referencedComponentNidProperty.get();
      }

      return getSemanticChronology().getReferencedComponentNid();
   }

   /**
    * Gets the sememe type.
    *
    * @return the sememe type
    */
   @Override
   public VersionType getVersionType() {
      return getSemanticChronology().getVersionType();
   }

   /**
    * Gets the sv for ov.
    *
    * @param <M> the generic type
    * @param <T> the generic type
    * @param type the type
    * @return the sv for ov
    */
   private <M extends MutableSemanticVersion, T>
           Class<T> getSvForOv(Class<M> type) {
      if (type.isAssignableFrom(ObservableDescriptionVersionImpl.class)) {
         return (Class<T>) DescriptionVersion.class;
      }

      throw new UnsupportedOperationException("Can't convert " + type);
   }

   @Override
   public <V extends Version> LatestVersion<V> getLatestVersion(StampCoordinate coordinate) {
      return getSemanticChronology().getLatestVersion(coordinate);
   }

   @Override
   public boolean isLatestVersionActive(StampCoordinate coordinate) {
      return getSemanticChronology().isLatestVersionActive(coordinate);
   }

   @Override
   public void putExternal(ByteArrayDataBuffer out) {
      getSemanticChronology().putExternal(out);
   }

   @Override
   public IsaacObjectType getIsaacObjectType() {
      return getSemanticChronology().getIsaacObjectType();
   }

   @Override
   public String toString() {
      return "ObservableSememeChronologyImpl{" + getSemanticChronology().toUserString() + '}';
   }
}