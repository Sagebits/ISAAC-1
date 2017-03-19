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



package sh.isaac.provider.query;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import java.util.Optional;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

//~--- non-JDK imports --------------------------------------------------------

import sh.isaac.api.Get;
import sh.isaac.api.collections.ConceptSequenceSet;
import sh.isaac.api.collections.NidSet;
import sh.isaac.api.component.concept.ConceptChronology;
import sh.isaac.api.component.concept.ConceptVersion;

//~--- classes ----------------------------------------------------------------

/**
 * Returns components that are in the incoming For set and not in the set
 * returned from the computation of the clauses that are descendents of the
 * <code>Not</code> clause.
 *
 * @author kec
 */
@XmlRootElement()
public class Not
        extends ParentClause {
   @XmlTransient
   NidSet forSet;
   @XmlTransient
   NidSet notSet;

   //~--- constructors --------------------------------------------------------

   /**
    * Default no arg constructor for Jaxb.
    */
   protected Not() {
      super();
   }

   public Not(Query enclosingQuery, Clause child) {
      super(enclosingQuery, child);
   }

   //~--- methods -------------------------------------------------------------

   @Override
   public NidSet computeComponents(NidSet incomingComponents) {
      this.forSet = enclosingQuery.getForSet();
      assert forSet != null;

      ConceptSequenceSet activeSet = new ConceptSequenceSet();

      Get.conceptService().getConceptChronologyStream(ConceptSequenceSet.of(incomingComponents)).forEach((ConceptChronology cc) -> {
                     Optional<ConceptVersion> latestVersion =
                        cc.getLatestVersion(ConceptVersion.class, getEnclosingQuery().getStampCoordinate());

                     if (latestVersion.isPresent()) {
                        activeSet.add(cc.getNid());
                     }
                  });
      getChildren().stream().forEach((c) -> {
                               notSet.or(c.computeComponents(incomingComponents));
                            });
      forSet = NidSet.of(activeSet);
      forSet.andNot(notSet);
      return forSet;
   }

   @Override
   public NidSet computePossibleComponents(NidSet incomingPossibleComponents) {
      this.notSet = new NidSet();

      for (Clause c: getChildren()) {
         for (ClauseComputeType cp: c.getComputePhases()) {
            switch (cp) {
            case PRE_ITERATION:
               notSet.or(c.computePossibleComponents(incomingPossibleComponents));
               break;

            case ITERATION:
               c.computePossibleComponents(incomingPossibleComponents);
               break;

            case POST_ITERATION:
               c.computePossibleComponents(incomingPossibleComponents);
               break;
            }
         }
      }

      return incomingPossibleComponents;
   }

   //~--- get methods ---------------------------------------------------------

   @Override
   public WhereClause getWhereClause() {
      WhereClause whereClause = new WhereClause();

      whereClause.setSemantic(ClauseSemantic.NOT);

      for (Clause clause: getChildren()) {
         whereClause.getChildren()
                    .add(clause.getWhereClause());
      }

      return whereClause;
   }
}

