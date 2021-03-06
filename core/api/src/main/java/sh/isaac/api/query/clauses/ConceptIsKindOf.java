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



package sh.isaac.api.query.clauses;

//~--- JDK imports ------------------------------------------------------------

import java.util.EnumSet;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

//~--- non-JDK imports --------------------------------------------------------

import sh.isaac.api.Get;
import sh.isaac.api.TaxonomySnapshot;
import sh.isaac.api.collections.NidSet;
import sh.isaac.api.component.concept.ConceptSpecification;
import sh.isaac.api.query.ClauseComputeType;
import sh.isaac.api.query.ClauseSemantic;
import sh.isaac.api.query.LeafClause;
import sh.isaac.api.query.Query;
import sh.isaac.api.query.WhereClause;
import sh.isaac.api.query.LetItemKey;
import sh.isaac.api.query.properties.ConceptClause;
import sh.isaac.api.query.properties.ManifoldClause;

//~--- classes ----------------------------------------------------------------

/**
 * Calculates the set of concepts that are a kind of the specified concept. The
 * calculated set is the union of the input concept and all concepts that lie
 * lie beneath the input concept in the terminology hierarchy.
 *
 * @author kec
 */
@XmlAccessorType(value = XmlAccessType.NONE)
public class ConceptIsKindOf
        extends LeafClause implements ConceptClause, ManifoldClause {
   /** The kind of spec key. */
   @XmlElement
   LetItemKey kindOfSpecKey;

   /** the manifold coordinate key. */
   @XmlElement
   LetItemKey manifoldCoordinateKey;

   //~--- constructors --------------------------------------------------------

   /**
    * Instantiates a new concept is kind of.
    */
   public ConceptIsKindOf() {
      super();
   }

   /**
    * Instantiates a new concept is kind of.
    *
    * @param enclosingQuery the enclosing query
    * @param kindOfSpecKey the kind of spec key
    * @param manifoldCoordinateKey the manifold coordinate key
    */
   public ConceptIsKindOf(Query enclosingQuery, LetItemKey kindOfSpecKey, LetItemKey manifoldCoordinateKey) {
      super(enclosingQuery);
      this.kindOfSpecKey     = kindOfSpecKey;
      this.manifoldCoordinateKey = manifoldCoordinateKey;
   }

   //~--- methods -------------------------------------------------------------


   /**
    * Compute possible components.
    *
    * @param incomingPossibleComponents the incoming possible components
    * @return the nid set
    */
   @Override
   public Map<ConceptSpecification, NidSet> computePossibleComponents(Map<ConceptSpecification, NidSet> incomingPossibleComponents) {
      final int                parentNid         = ((ConceptSpecification) getLetItem(kindOfSpecKey)).getNid();
      final TaxonomySnapshot kindOfSnapshot = Get.taxonomyService().getSnapshot(getLetItem(manifoldCoordinateKey));
      
      NidSet possibleComponents = incomingPossibleComponents.get(getAssemblageForIteration());
        
      for (int nid: possibleComponents.asArray()) {
          if (!test(kindOfSnapshot, nid, parentNid)) {
              possibleComponents.remove(nid);
          }
      }
      return incomingPossibleComponents;
   }

    protected boolean test(final TaxonomySnapshot kindOfSnapshot, int nid, final int parentNid) {
        return kindOfSnapshot.isKindOf(nid, parentNid);
    }

   //~--- get methods ---------------------------------------------------------

   /**
    * Gets the compute phases.
    *
    * @return the compute phases
    */
   @Override
   public EnumSet<ClauseComputeType> getComputePhases() {
      return PRE_ITERATION;
   }

    @Override
    public ClauseSemantic getClauseSemantic() {
        return ClauseSemantic.CONCEPT_IS_KIND_OF;
    }
   

   /**
    * Gets the where clause.
    *
    * @return the where clause
    */
   @Override
   public WhereClause getWhereClause() {
      final WhereClause whereClause = new WhereClause();

      whereClause.setSemantic(ClauseSemantic.CONCEPT_IS_KIND_OF);
      whereClause.getLetKeys()
                 .add(this.kindOfSpecKey);
      whereClause.getLetKeys()
                 .add(this.manifoldCoordinateKey);
      return whereClause;
   }

    public LetItemKey getKindOfSpecKey() {
        return kindOfSpecKey;
    }

    public void setKindOfSpecKey(LetItemKey kindOfSpecKey) {
        this.kindOfSpecKey = kindOfSpecKey;
    }

    @Override
    public LetItemKey getConceptSpecKey() {
        return getKindOfSpecKey();
    }

    @Override
    public void setConceptSpecKey(LetItemKey conceptSpecKey) {
        setKindOfSpecKey(conceptSpecKey);
    }

    
   @Override
    public LetItemKey getManifoldCoordinateKey() {
        return manifoldCoordinateKey;
    }

   @Override
    public void setManifoldCoordinateKey(LetItemKey manifoldCoordinateKey) {
        this.manifoldCoordinateKey = manifoldCoordinateKey;
    }

}

