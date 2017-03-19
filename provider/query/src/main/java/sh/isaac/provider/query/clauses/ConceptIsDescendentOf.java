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



package sh.isaac.provider.query.clauses;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import java.util.EnumSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

//~--- non-JDK imports --------------------------------------------------------

import sh.isaac.api.Get;
import sh.isaac.api.collections.ConceptSequenceSet;
import sh.isaac.api.collections.NidSet;
import sh.isaac.api.component.concept.ConceptSpecification;
import sh.isaac.api.component.concept.ConceptVersion;
import sh.isaac.api.coordinate.TaxonomyCoordinate;
import sh.isaac.provider.query.ClauseComputeType;
import sh.isaac.provider.query.ClauseSemantic;
import sh.isaac.provider.query.LeafClause;
import sh.isaac.provider.query.Query;
import sh.isaac.provider.query.WhereClause;

//~--- classes ----------------------------------------------------------------

/**
 * Computes the set of concepts that are descendents of the specified concept
 * spec. The set of descendents of a given concept is the set of concepts that
 * lie beneath the input concept in the terminology hierarchy. This
 * <code>Clause</code> has an optional parameter for a previous
 * <code>ViewCoordinate</code>, which allows for queries of previous versions.
 *
 * @author dylangrald
 */
@XmlRootElement
@XmlAccessorType(value = XmlAccessType.NONE)
public class ConceptIsDescendentOf
        extends LeafClause {
   @XmlElement
   String descendentOfSpecKey;
   @XmlElement
   String viewCoordinateKey;

   //~--- constructors --------------------------------------------------------

   protected ConceptIsDescendentOf() {}

   public ConceptIsDescendentOf(Query enclosingQuery, String kindOfSpecKey, String viewCoordinateKey) {
      super(enclosingQuery);
      this.descendentOfSpecKey = kindOfSpecKey;
      this.viewCoordinateKey   = viewCoordinateKey;
   }

   //~--- methods -------------------------------------------------------------

   @Override
   public NidSet computePossibleComponents(NidSet incomingPossibleComponents) {
      TaxonomyCoordinate taxonomyCoordinate = (TaxonomyCoordinate) this.enclosingQuery.getLetDeclarations()
                                                                                      .get(viewCoordinateKey);
      ConceptSpecification descendentOfSpec = (ConceptSpecification) enclosingQuery.getLetDeclarations()
                                                                                   .get(descendentOfSpecKey);
      int parentNid = descendentOfSpec.getNid();
      ConceptSequenceSet descendentOfSequenceSet = Get.taxonomyService()
                                                      .getChildOfSequenceSet(parentNid, taxonomyCoordinate);

      descendentOfSequenceSet.remove(parentNid);
      getResultsCache().or(NidSet.of(descendentOfSequenceSet));
      return getResultsCache();
   }

   //~--- get methods ---------------------------------------------------------

   @Override
   public EnumSet<ClauseComputeType> getComputePhases() {
      return PRE_ITERATION;
   }

   @Override
   public void getQueryMatches(ConceptVersion conceptVersion) {
      // Nothing to do...
   }

   @Override
   public WhereClause getWhereClause() {
      WhereClause whereClause = new WhereClause();

      whereClause.setSemantic(ClauseSemantic.CONCEPT_IS_DESCENDENT_OF);
      whereClause.getLetKeys()
                 .add(descendentOfSpecKey);
      whereClause.getLetKeys()
                 .add(viewCoordinateKey);
      return whereClause;
   }
}

