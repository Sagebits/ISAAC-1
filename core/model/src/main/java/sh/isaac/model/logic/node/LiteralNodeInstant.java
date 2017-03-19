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



package sh.isaac.model.logic.node;

//~--- JDK imports ------------------------------------------------------------

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;

import java.time.Instant;

import java.util.UUID;

//~--- non-JDK imports --------------------------------------------------------

import sh.isaac.api.DataTarget;
import sh.isaac.api.logic.LogicNode;
import sh.isaac.api.logic.NodeSemantic;
import sh.isaac.api.util.UuidT5Generator;
import sh.isaac.model.logic.LogicalExpressionOchreImpl;

//~--- classes ----------------------------------------------------------------

/**
 * Created by kec on 12/9/14.
 */
public class LiteralNodeInstant
        extends LiteralNode {
   Instant literalValue;

   //~--- constructors --------------------------------------------------------

   public LiteralNodeInstant(LogicalExpressionOchreImpl logicGraphVersion,
                             DataInputStream dataInputStream)
            throws IOException {
      super(logicGraphVersion, dataInputStream);
      literalValue = Instant.ofEpochSecond(dataInputStream.readLong());
   }

   public LiteralNodeInstant(LogicalExpressionOchreImpl logicGraphVersion, Instant literalValue) {
      super(logicGraphVersion);
      this.literalValue = literalValue;
   }

   //~--- methods -------------------------------------------------------------

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

      LiteralNodeInstant that = (LiteralNodeInstant) o;

      return literalValue.equals(that.literalValue);
   }

   @Override
   public int hashCode() {
      int result = super.hashCode();

      result = 31 * result + literalValue.hashCode();
      return result;
   }

   @Override
   public String toString() {
      return toString("");
   }

   @Override
   public String toString(String nodeIdSuffix) {
      return "Instant literal[" + getNodeIndex() + nodeIdSuffix + "]" + literalValue + super.toString(nodeIdSuffix);
   }

   @Override
   protected int compareFields(LogicNode o) {
      LiteralNodeInstant that = (LiteralNodeInstant) o;

      return this.literalValue.compareTo(that.literalValue);
   }

   @Override
   protected UUID initNodeUuid() {
      return UuidT5Generator.get(getNodeSemantic().getSemanticUuid(), literalValue.toString());
   }

   @Override
   protected void writeNodeData(DataOutput dataOutput, DataTarget dataTarget)
            throws IOException {
      super.writeData(dataOutput, dataTarget);
      dataOutput.writeLong(literalValue.getEpochSecond());
   }

   //~--- get methods ---------------------------------------------------------

   public Instant getLiteralValue() {
      return literalValue;
   }

   @Override
   public NodeSemantic getNodeSemantic() {
      return NodeSemantic.LITERAL_INSTANT;
   }
}

