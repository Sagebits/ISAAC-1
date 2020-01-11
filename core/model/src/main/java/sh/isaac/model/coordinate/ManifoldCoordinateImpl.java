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



package sh.isaac.model.coordinate;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import javax.xml.bind.annotation.XmlElement;
import sh.isaac.api.ConfigurationService;
import sh.isaac.api.Get;
import sh.isaac.api.Status;
import sh.isaac.api.collections.NidSet;
import sh.isaac.api.component.concept.ConceptSpecification;
import sh.isaac.api.coordinate.*;
import sh.isaac.api.externalizable.ByteArrayDataBuffer;
import sh.isaac.api.observable.coordinate.ObservableLanguageCoordinate;
import sh.isaac.api.observable.coordinate.ObservableLogicCoordinate;
import sh.isaac.api.observable.coordinate.ObservableStampCoordinate;
import sh.isaac.model.observable.coordinate.ObservableLanguageCoordinateImpl;
import sh.isaac.model.observable.coordinate.ObservableStampCoordinateImpl;

/**
 * The Class ManifoldCoordinateImpl.
 *
 * @author kec
 */
public class ManifoldCoordinateImpl
         implements ManifoldCoordinate {

   /** The taxonomy type. */
   PremiseType taxonomyPremiseType;

   /** The stamp coordinate. */
   StampCoordinateImpl stampCoordinate;

   StampCoordinateImpl destinationStampCoordinate;

   /** The language coordinate. */
   LanguageCoordinateImpl languageCoordinate;

   /** The logic coordinate. */
   LogicCoordinateImpl logicCoordinate;
   
   private Function<int[], int[]> customSorter = null;

   /**
    * Instantiates a new taxonomy coordinate impl.
    */
   @SuppressWarnings("unused")
   private ManifoldCoordinateImpl() {
      // for jaxb
   }

    public ManifoldCoordinateImpl(ByteArrayDataBuffer data) {
        this.taxonomyPremiseType = PremiseType.valueOf(data.getUTF());
        this.stampCoordinate = StampCoordinateImpl.make(data);
        this.destinationStampCoordinate = StampCoordinateImpl.make(data);
        this.languageCoordinate = LanguageCoordinateImpl.make(data);
        this.logicCoordinate = LogicCoordinateImpl.make(data);
    }

    public final void putExternal(ByteArrayDataBuffer out) {
       out.putUTF(this.taxonomyPremiseType.name());
       this.stampCoordinate.putExternal(out);
       this.destinationStampCoordinate.putExternal(out);
       this.languageCoordinate.putExternal(out);
       this.logicCoordinate.putExternal(out);
    }

   /**
    * Instantiates a new taxonomy coordinate impl.
    *
    * @param taxonomyType the taxonomy type
    * @param stampCoordinate the stamp coordinate
    * @param languageCoordinate the language coordinate
    * @param logicCoordinate the logic coordinate
    */
   public ManifoldCoordinateImpl(PremiseType taxonomyType,
                                 StampCoordinate stampCoordinate,
                                 LanguageCoordinate languageCoordinate,
                                 LogicCoordinate logicCoordinate) {
       if (stampCoordinate instanceof ObservableStampCoordinateImpl) {
           stampCoordinate = ((ObservableStampCoordinateImpl) stampCoordinate).getStampCoordinate();
       }
       if (languageCoordinate instanceof ObservableLanguageCoordinateImpl) {
           languageCoordinate = ((ObservableLanguageCoordinateImpl) languageCoordinate).getLanguageCoordinate();
       }
       if (logicCoordinate instanceof ObservableLogicCoordinate) {
           logicCoordinate = ((ObservableLogicCoordinate) logicCoordinate).getLogicCoordinate();
       }
      this.taxonomyPremiseType       = taxonomyType;
       if (stampCoordinate instanceof ManifoldCoordinate) {
           stampCoordinate = ((ManifoldCoordinate) stampCoordinate).getStampCoordinate();
       }
       if (stampCoordinate instanceof ObservableStampCoordinate) {
           stampCoordinate = ((ObservableStampCoordinate) stampCoordinate).getStampCoordinate();
       }
      this.stampCoordinate    = (StampCoordinateImpl) stampCoordinate;
      this.destinationStampCoordinate = (StampCoordinateImpl) stampCoordinate;
       if (languageCoordinate instanceof ManifoldCoordinate) {
           languageCoordinate = ((ManifoldCoordinate) languageCoordinate).getLanguageCoordinate();
       }
      if (languageCoordinate instanceof ObservableLanguageCoordinate) {
          languageCoordinate = ((ObservableLanguageCoordinate) languageCoordinate).getLanguageCoordinate();
      }
      this.languageCoordinate = (LanguageCoordinateImpl) languageCoordinate;
       if (logicCoordinate instanceof ManifoldCoordinate) {
           logicCoordinate = ((ManifoldCoordinate) logicCoordinate).getLogicCoordinate();
       }
       if (logicCoordinate instanceof ObservableLogicCoordinate) {
           logicCoordinate = ((ObservableLogicCoordinate) logicCoordinate).getLogicCoordinate();
       }
      this.logicCoordinate    = (LogicCoordinateImpl) logicCoordinate;
      //this.uuid               //lazy load
   }
   
   /**
    * Instantiates a new taxonomy coordinate impl.
    *
    * @param taxonomyType the taxonomy type
    * @param stampCoordinate the stamp coordinate
    * @param destinationStampCoordinate - if provided, this coordinate will be used to determine which destination concepts 
    * should be included in a source or target return.  If not provided, the stampCoordinate parameter will be used instead. 
    * @param languageCoordinate the language coordinate
    * @param logicCoordinate the logic coordinate
    */
   public ManifoldCoordinateImpl(PremiseType taxonomyType,
                                 StampCoordinate stampCoordinate,
                                 StampCoordinate destinationStampCoordinate,
                                 LanguageCoordinate languageCoordinate,
                                 LogicCoordinate logicCoordinate) {
      this.taxonomyPremiseType       = taxonomyType;
      this.stampCoordinate    = (StampCoordinateImpl) stampCoordinate;
      this.destinationStampCoordinate = (StampCoordinateImpl) (destinationStampCoordinate == null ? stampCoordinate : destinationStampCoordinate);
      this.languageCoordinate = (LanguageCoordinateImpl) languageCoordinate;
      this.logicCoordinate    = (LogicCoordinateImpl) logicCoordinate;
      //this.uuid               //lazy load
   }
   
   /**
    * Instantiates a new taxonomy coordinate impl.  Calls {@link #ManifoldCoordinateImpl(PremiseType, StampCoordinate, LanguageCoordinate, LogicCoordinate)}
    * with a {@link PremiseType#STATED} and the default Logic Coordinate from {@link ConfigurationService}
    *
    * @param stampCoordinate the stamp coordinate
    * @param languageCoordinate - optional - uses default if not provided.  the language coordinate
    */
   public ManifoldCoordinateImpl(StampCoordinate stampCoordinate,
                                 LanguageCoordinate languageCoordinate) {
      this(PremiseType.STATED, stampCoordinate, 
          languageCoordinate == null ? Get.configurationService().getUserConfiguration(Optional.empty()).getLanguageCoordinate() : languageCoordinate,
          Get.configurationService().getUserConfiguration(Optional.empty()).getLogicCoordinate());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   @XmlElement
   public UUID getManifoldCoordinateUuid() {
      return ManifoldCoordinate.super.getManifoldCoordinateUuid(); 
   }
   
   @SuppressWarnings("unused")
   private void setManifoldCoordinateUuid(UUID uuid) {
        // noop for jaxb
   }

    /**
     * Equals.
     *
     * @param obj the obj
     * @return true, if successful
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        
        if (getClass() != obj.getClass()) {
            return false;
        }
        
        final ManifoldCoordinateImpl other = (ManifoldCoordinateImpl) obj;
        
        if (this.taxonomyPremiseType != other.taxonomyPremiseType) {
            return false;
        }
        
        if (!Objects.equals(this.stampCoordinate, other.stampCoordinate)) {
            return false;
        }
        
        if (!Objects.equals(this.destinationStampCoordinate, other.destinationStampCoordinate)) {
            return false;
        }
        
        if (!Objects.equals(this.logicCoordinate, other.logicCoordinate)) {
            return false;
        }
        
        if (this.getCustomTaxonomySortHashCode() != other.getCustomTaxonomySortHashCode() ) {
            return false;
        }
        
        return Objects.equals(this.languageCoordinate, other.languageCoordinate);
    }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      int hash = 3;

      hash = 53 * hash + Objects.hashCode(this.taxonomyPremiseType);
      hash = 53 * hash + Objects.hashCode(this.stampCoordinate);
      hash = 53 * hash + Objects.hashCode(this.destinationStampCoordinate);
      hash = 53 * hash + Objects.hashCode(this.languageCoordinate);
      hash = 53 * hash + getCustomTaxonomySortHashCode();
      return hash;
   }

   /**
    * This implementation adjusts the time of both the stamp coordinate and the destination stamp coordinate 
    * @see sh.isaac.api.coordinate.StampCoordinateProxy#makeCoordinateAnalog(long)
    */
   @Override
   public ManifoldCoordinateImpl makeCoordinateAnalog(long stampPositionTime) {
      ManifoldCoordinateImpl mc = new ManifoldCoordinateImpl(this.taxonomyPremiseType,
                                        this.stampCoordinate.makeCoordinateAnalog(stampPositionTime),
                                        this.destinationStampCoordinate.makeCoordinateAnalog(stampPositionTime),
                                        this.languageCoordinate,
                                        this.logicCoordinate);
      mc.setCustomSorter(this.customSorter);
      return mc;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ManifoldCoordinateImpl makeCoordinateAnalog(PremiseType taxonomyType) {
       ManifoldCoordinateImpl mc = new ManifoldCoordinateImpl(taxonomyType,
                                        this.stampCoordinate,
                                        this.destinationStampCoordinate,
                                        this.languageCoordinate,
                                        this.logicCoordinate);
       mc.setCustomSorter(this.customSorter);
       return mc;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ManifoldCoordinateImpl makeCoordinateAnalog(Status... state) {
       ManifoldCoordinateImpl mc = new ManifoldCoordinateImpl(this.taxonomyPremiseType,
                                        this.stampCoordinate.makeCoordinateAnalog(state),
                                        this.destinationStampCoordinate,
                                        this.languageCoordinate,
                                        this.logicCoordinate);
      mc.setCustomSorter(this.customSorter);
      return mc;
   }


   @Override
   public ManifoldCoordinateImpl makeModuleAnalog(Collection<ConceptSpecification> modules, boolean add) {
       ManifoldCoordinateImpl mc = new ManifoldCoordinateImpl(this.taxonomyPremiseType, 
            this.stampCoordinate.makeModuleAnalog(modules, add), 
            this.destinationStampCoordinate,
            this.languageCoordinate, 
            this.logicCoordinate);
       mc.setCustomSorter(this.customSorter);
       return mc;
   }

    @Override
    public ManifoldCoordinateImpl makePathAnalog(ConceptSpecification pathForPosition) {
             ManifoldCoordinateImpl mc = new ManifoldCoordinateImpl(this.taxonomyPremiseType,
                    this.stampCoordinate.makePathAnalog(pathForPosition),
                    this.destinationStampCoordinate,
                    this.languageCoordinate,
                    this.logicCoordinate);
            mc.setCustomSorter(this.customSorter);
            return mc;
    }

    /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return "ManifoldCoordinateImpl{" + this.taxonomyPremiseType + ",\n   origin coordinate: " + this.stampCoordinate + ", \n   destin coordinate: " + this.destinationStampCoordinate +
              ", \n   lang coordinate: " +
             this.languageCoordinate + ", \n   logic coordinate: " + this.logicCoordinate + ", uuid=" + getCoordinateUuid() + '}';
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public LanguageCoordinate getLanguageCoordinate() {
      return this.languageCoordinate;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public LogicCoordinate getLogicCoordinate() {
      return this.logicCoordinate;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public StampCoordinate getStampCoordinate() {
      return this.stampCoordinate;
   }

   /**
    * @see sh.isaac.api.coordinate.ManifoldCoordinate#optionalDestinationStampCoordinate()
    */
   @Override
   public Optional<? extends StampCoordinate> optionalDestinationStampCoordinate() {
      return Optional.of(destinationStampCoordinate);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public PremiseType getTaxonomyPremiseType() {
      return this.taxonomyPremiseType;
   }

   public void setTaxonomyPremiseType(PremiseType taxonomyPremiseType) {
       this.taxonomyPremiseType = taxonomyPremiseType;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ManifoldCoordinateImpl deepClone() {
      ManifoldCoordinateImpl newCoordinate = new ManifoldCoordinateImpl(taxonomyPremiseType,
                                 stampCoordinate.deepClone(),
                                 destinationStampCoordinate.deepClone(),
                                 languageCoordinate.deepClone(),
                                 logicCoordinate.deepClone());
      newCoordinate.customSorter = this.customSorter;
      return newCoordinate;
   }

   /**
    * @see sh.isaac.api.coordinate.ManifoldCoordinate#hasCustomTaxonomySort()
    */
   @Override
   public boolean hasCustomTaxonomySort() {
      return customSorter != null;
   }
   
   /**
    * @see sh.isaac.api.coordinate.ManifoldCoordinate#getCustomTaxonomySortHashCode()
    */
   @Override
   public int getCustomTaxonomySortHashCode()
   {
      return hasCustomTaxonomySort() ? customSorter.hashCode() : 
         ManifoldCoordinate.super.getCustomTaxonomySortHashCode();
   }

   /**
    * Add a customSorter to this coordinate.  The provided sorter should expect to be passed an array of nids representing concepts, 
    * and should return them sorted.
    * @param customSorter
    */
   public void setCustomSorter(Function<int[], int[]> customSorter) {
      this.customSorter = customSorter;
   }
   
   /**
    * @see sh.isaac.api.coordinate.ManifoldCoordinate#sortConcepts(int[])
    */
   @Override
   public int[] sortConcepts(int[] concepts)
   {
      if (customSorter != null) {
         return customSorter.apply(concepts);
      }
      else {
         return ManifoldCoordinate.super.sortConcepts(concepts);
      }
   }

    @Override
    public Set<ConceptSpecification> getAuthorSpecifications() {
        return getStampCoordinate().getAuthorSpecifications();
    }

    @Override
    public NidSet getAuthorNids() {
        return getStampCoordinate().getAuthorNids();
    }

    @Override
    public StampCoordinateReadOnly getStampCoordinateReadOnly() {
        return getStampCoordinate().getStampCoordinateReadOnly();
    }
}