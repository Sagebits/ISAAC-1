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



/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
 */
package sh.isaac.model.logic.node.external;

//~--- JDK imports ------------------------------------------------------------

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;

import java.util.UUID;

//~--- non-JDK imports --------------------------------------------------------

import sh.isaac.api.DataTarget;
import sh.isaac.api.Get;
import sh.isaac.api.logic.LogicNode;
import sh.isaac.api.logic.NodeSemantic;
import sh.isaac.api.util.UuidT5Generator;
import sh.isaac.model.logic.LogicalExpressionOchreImpl;
import sh.isaac.model.logic.node.AbstractLogicNode;
import sh.isaac.model.logic.node.internal.TemplateNodeWithSequences;

//~--- classes ----------------------------------------------------------------

/**
 *
 * @author kec
 */
public class TemplateNodeWithUuids
        extends AbstractLogicNode {
   /**
    * Sequence of the concept that defines the template
    */
   UUID templateConceptUuid;

   /**
    * Sequence of the assemblage concept that provides the substitution values
    * for the template.
    */
   UUID assemblageConceptUuid;

   //~--- constructors --------------------------------------------------------

   public TemplateNodeWithUuids(TemplateNodeWithSequences internalForm) {
      super(internalForm);
      this.templateConceptUuid = Get.identifierService()
                                    .getUuidPrimordialFromConceptId(internalForm.getTemplateConceptSequence())
                                    .get();
      this.assemblageConceptUuid = Get.identifierService()
                                      .getUuidPrimordialFromConceptId(internalForm.getAssemblageConceptSequence())
                                      .get();
   }

   public TemplateNodeWithUuids(LogicalExpressionOchreImpl logicGraphVersion,
                                DataInputStream dataInputStream)
            throws IOException {
      super(logicGraphVersion, dataInputStream);
      templateConceptUuid   = new UUID(dataInputStream.readLong(), dataInputStream.readLong());
      assemblageConceptUuid = new UUID(dataInputStream.readLong(), dataInputStream.readLong());
   }

   public TemplateNodeWithUuids(LogicalExpressionOchreImpl logicGraphVersion,
                                UUID templateConceptUuid,
                                UUID assemblageConceptUuid) {
      super(logicGraphVersion);
      this.templateConceptUuid   = templateConceptUuid;
      this.assemblageConceptUuid = assemblageConceptUuid;
   }

   //~--- methods -------------------------------------------------------------

   @Override
   public final void addChildren(LogicNode... children) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }

      if ((o == null) || (getClass() != o.getClass())) {
         return false;
      }

      if (!super.equals(o)) {
         return false;
      }

      TemplateNodeWithUuids that = (TemplateNodeWithUuids) o;

      if (!assemblageConceptUuid.equals(that.assemblageConceptUuid)) {
         return false;
      }

      return templateConceptUuid.equals(that.templateConceptUuid);
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();

      result = 31 * result + templateConceptUuid.hashCode();
      result = 31 * result + assemblageConceptUuid.hashCode();
      return result;
   }

   @Override
   public String toString() {
      return toString("");
   }

   @Override
   public String toString(String nodeIdSuffix) {
      return "TemplateNode[" + getNodeIndex() + nodeIdSuffix + "] " + "assemblage: " +
             Get.conceptService().getConcept(assemblageConceptUuid).toUserString() + ", template: " +
             Get.conceptService().getConcept(templateConceptUuid).toUserString() + super.toString(nodeIdSuffix);
   }

   @Override
   public void writeNodeData(DataOutput dataOutput, DataTarget dataTarget)
            throws IOException {
      switch (dataTarget) {
      case EXTERNAL:
         super.writeData(dataOutput, dataTarget);
         dataOutput.writeLong(templateConceptUuid.getMostSignificantBits());
         dataOutput.writeLong(templateConceptUuid.getLeastSignificantBits());
         dataOutput.writeLong(assemblageConceptUuid.getMostSignificantBits());
         dataOutput.writeLong(assemblageConceptUuid.getLeastSignificantBits());
         break;

      case INTERNAL:
         TemplateNodeWithSequences internalForm = new TemplateNodeWithSequences(this);

         internalForm.writeNodeData(dataOutput, dataTarget);
         break;

      default:
         throw new UnsupportedOperationException("Can't handle dataTarget: " + dataTarget);
      }
   }

   @Override
   protected int compareFields(LogicNode o) {
      TemplateNodeWithUuids that = (TemplateNodeWithUuids) o;

      if (!assemblageConceptUuid.equals(that.assemblageConceptUuid)) {
         return assemblageConceptUuid.compareTo(that.assemblageConceptUuid);
      }

      return templateConceptUuid.compareTo(that.templateConceptUuid);
   }

   @Override
   protected UUID initNodeUuid() {
      return UuidT5Generator.get(getNodeSemantic().getSemanticUuid(),
                                 templateConceptUuid.toString() + assemblageConceptUuid.toString());
   }

   //~--- get methods ---------------------------------------------------------

   public UUID getAssemblageConceptUuid() {
      return assemblageConceptUuid;
   }

   @Override
   public final AbstractLogicNode[] getChildren() {
      return new AbstractLogicNode[0];
   }

   @Override
   public NodeSemantic getNodeSemantic() {
      return NodeSemantic.TEMPLATE;
   }

   public UUID getTemplateConceptUuid() {
      return templateConceptUuid;
   }
}

