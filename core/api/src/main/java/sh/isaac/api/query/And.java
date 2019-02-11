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
package sh.isaac.api.query;

//~--- JDK imports ------------------------------------------------------------

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

//~--- non-JDK imports --------------------------------------------------------
import sh.isaac.api.collections.NidSet;
import sh.isaac.api.component.concept.ConceptSpecification;
import static sh.isaac.api.query.ForSet.deepClone;

//~--- classes ----------------------------------------------------------------
/**
 * <code>ParentClause</code> that computes the intersection of the set results
 * from the enclosed <code>Clauses</code>.
 *
 * @author kec
 */
@XmlRootElement(name = "and")
@XmlAccessorType(value = XmlAccessType.NONE)
public class And
        extends ParentClause {

    /**
     * Default no arg constructor for Jaxb.
     */
    public And() {
        super();
    }

    /**
     * Instantiates a new and.
     *
     * @param enclosingQuery the enclosing query
     * @param clauses the clauses
     */
    public And(Query enclosingQuery, Clause... clauses) {
        super(enclosingQuery, clauses);
    }

    //~--- methods -------------------------------------------------------------
    /**
     * Compute components.
     *
     * @param incomingComponents the components
     * @return the nid set
     */
    @Override
    public Map<ConceptSpecification, NidSet> computeComponents(Map<ConceptSpecification, NidSet> incomingComponents) {
        Set<ConceptSpecification> iteratedAssemblages = new HashSet<>();
        Map<ConceptSpecification, NidSet> outgoingComponents = deepClone(incomingComponents);
        
        for (Clause child: getChildren()) {
            Map<ConceptSpecification, NidSet> computedComponents = child.computeComponents(deepClone(incomingComponents));
            if (iteratedAssemblages.contains(child.getAssemblageForIteration())) {
                // Do an or with existing nid set...
                NidSet childNids = computedComponents.get(child.getAssemblageForIteration());
                outgoingComponents.put(child.getAssemblageForIteration(), childNids.and(outgoingComponents.get(child.getAssemblageForIteration())));
            } else {
                // Initilize with computed nid set. 
                iteratedAssemblages.add(child.getAssemblageForIteration());
                outgoingComponents.put(child.getAssemblageForIteration(), computedComponents.get(child.getAssemblageForIteration()));
            }
        }
        return outgoingComponents;
    }

    /**
     * Compute possible components.
     *
     * @param incomingPossibleComponents the incoming possible components
     * @return the nid set
     */
    @Override
    public Map<ConceptSpecification, NidSet> computePossibleComponents(Map<ConceptSpecification, NidSet> incomingPossibleComponents) {
        Set<ConceptSpecification> iteratedAssemblages = new HashSet<>();
        Map<ConceptSpecification, NidSet> outgoingPossibleComponents = deepClone(incomingPossibleComponents);
        
        for (Clause child: getChildren()) {
            Map<ConceptSpecification, NidSet> computedComponents = child.computePossibleComponents(deepClone(incomingPossibleComponents));
            if (iteratedAssemblages.contains(child.getAssemblageForIteration())) {
                // Do an or with existing nid set...
                NidSet childNids = computedComponents.get(child.getAssemblageForIteration());
                outgoingPossibleComponents.put(child.getAssemblageForIteration(), childNids.and(outgoingPossibleComponents.get(child.getAssemblageForIteration())));
            } else {
                // Initilize with computed nid set. 
                iteratedAssemblages.add(child.getAssemblageForIteration());
                outgoingPossibleComponents.put(child.getAssemblageForIteration(), computedComponents.get(child.getAssemblageForIteration()));
            }
        }
        return outgoingPossibleComponents;
    }
  
    //~--- get methods ---------------------------------------------------------
    /**
     * Gets the where clause.
     *
     * @return the where clause
     */
    @Override
    public WhereClause getWhereClause() {
        final WhereClause whereClause = new WhereClause();

        whereClause.setSemantic(ClauseSemantic.AND);

        for (final Clause clause : getChildren()) {
            whereClause.getChildren()
                    .add(clause.getWhereClause());
        }

        return whereClause;
    }

    @Override
    public ClauseSemantic getClauseSemantic() {
        return ClauseSemantic.AND;
    }

    @Override
    public Clause[] getAllowedSubstutitionClauses() {
        return getParentClauses();
    }

    @Override
    public Clause[] getAllowedChildClauses() {
        return getAllClauses();
    }

    @Override
    public Clause[] getAllowedSiblingClauses() {
        return getAllClauses();
    }

    @Override
    public void resetResults() {
        // no cached data in task. 
    }

    
}
