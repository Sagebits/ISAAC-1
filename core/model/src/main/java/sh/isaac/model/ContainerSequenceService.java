/*
 * Copyright 2017 Organizations participating in ISAAC, ISAAC's KOMET, and SOLOR development include the
         US Veterans Health Administration, OSHERA, and the Health Services Platform Consortium..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sh.isaac.model;

import org.jvnet.hk2.annotations.Contract;
import sh.isaac.api.IdentifierService;
import sh.isaac.api.chronicle.VersionType;
import sh.isaac.api.externalizable.IsaacObjectType;

/**
 *
 * @author kec
 */
@Contract
public interface ContainerSequenceService extends IdentifierService {
   
   
   /**
    * This object type is normally set when the first object is written to the assemblage, so there 
    * is no need for calling this method directly. 
    * @param nid
    * @param assemblageNid the required assemblageNid for this nid.
    * @param objectType 
    * @param versionType
    * @return true, if this is the first time setupNid was called for this nid.  Returns false if the nid was previously setup, 
    * and this method call was a no-op.
    * @throws IllegalStateException if the nid was already set up and the previous type(s) don't match, or the nid is unknown.
    */
   boolean setupNid(int nid, int assemblageNid, IsaacObjectType objectType, VersionType versionType) throws IllegalStateException;
   
   /**
    * 
    * @param nid a component nid
    * @param referencingSemanticNid the nid for a semantic that references the component
    */
   void addToSemanticIndex(int nid, int referencingSemanticNid);

   /**
    * 
    * @param componentNid
    * @return the semantic nids associated with the component 
    */
   int[] getSemanticNidsForComponent(int componentNid);
   /**
    * A sequence for the semantic chronology, that is only unique within the 
    * particular assemblage. 
    * @param nid
    * @param assemblageNid
    * @return 
    */
   int getElementSequenceForNid(int nid, int assemblageNid);
   
   /**
    * A sequence for the semantic chronology, that is only unique within the 
    * particular assemblage. 
    * @param nid
    * @return 
    */
   int getElementSequenceForNid(int nid);
   
   /**
    * 
    * @param sequenceInAssemblage the sequence of the semantic chronology
    * @param assemblageNid
    * @return the nid for the associated chronology specified by the assemblageSequence within the assemblage identified by the Nid
    */
   int getNidForElementSequence(int sequenceInAssemblage, int assemblageNid);

   /**
    * 
    * @param assemblageNid 
    * @return get the maximum sequence for this assemblage. 
    */
   int getMaxSequenceForAssemblage(int assemblageNid);
   
}
