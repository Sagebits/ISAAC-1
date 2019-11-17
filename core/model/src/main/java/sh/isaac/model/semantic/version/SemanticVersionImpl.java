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



package sh.isaac.model.semantic.version;

//~--- non-JDK imports --------------------------------------------------------

import sh.isaac.api.Get;
import sh.isaac.api.chronicle.Version;
import sh.isaac.api.chronicle.VersionType;
import sh.isaac.api.coordinate.EditCoordinate;
import sh.isaac.api.externalizable.ByteArrayDataBuffer;
import sh.isaac.api.transaction.Transaction;
import sh.isaac.model.semantic.SemanticChronologyImpl;
import sh.isaac.api.component.semantic.SemanticChronology;

//~--- classes ----------------------------------------------------------------

/**
 * The Class SemanticVersionImpl.
 *
 * @author kec
 */
public class SemanticVersionImpl
        extends AbstractVersionImpl {
   /**
    * Instantiates a new semantic version impl.
    *
    * @param container the container
    * @param stampSequence the stamp sequence
    */
   public SemanticVersionImpl(SemanticChronology container, int stampSequence) {
      super(container, stampSequence);
   }
   

   private SemanticVersionImpl(SemanticVersionImpl other, int stampSequence) {
      super(other.getChronology(), stampSequence);
   }

   @Override
   public <V extends Version> V makeAnalog(EditCoordinate ec) {
      final int stampSequence = Get.stampService()
              .getStampSequence(
                      this.getStatus(),
                      Long.MAX_VALUE,
                      ec.getAuthorNid(),
                      this.getModuleNid(),
                      ec.getPathNid());
      SemanticChronologyImpl chronologyImpl = (SemanticChronologyImpl) this.chronicle;
      final SemanticVersionImpl newVersion = new SemanticVersionImpl(this, stampSequence);

      chronologyImpl.addVersion(newVersion);
      return (V) newVersion;
   }
   @Override
   public <V extends Version> V makeAnalog(Transaction transaction, int authorNid) {
      final int stampSequence = Get.stampService()
              .getStampSequence(transaction,
                      this.getStatus(),
                      Long.MAX_VALUE,
                      authorNid,
                      this.getModuleNid(),
                      this.getPathNid());
      return (V) setupAnalog(stampSequence);
   }

   @Override
   public <V extends Version> V setupAnalog(int stampSequence) {
      SemanticChronologyImpl chronologyImpl = (SemanticChronologyImpl) this.chronicle;
      final SemanticVersionImpl newVersion = new SemanticVersionImpl(this, stampSequence);

      chronologyImpl.addVersion(newVersion);
      return (V) newVersion;
   }

   //~--- methods -------------------------------------------------------------

   /**
    * To string.
    *
    * @return the string
    */
   @Override
   public String toString() {
      return getSemanticType().toString() + super.toString();
   }

   /**
    * Write version data.
    *
    * @param data the data
    */
   @Override
   public void writeVersionData(ByteArrayDataBuffer data) {
      super.writeVersionData(data);
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Gets the semantic type.
    *
    * @return the semantic type
    */
   @Override
   public final VersionType getSemanticType() {
      return VersionType.MEMBER;
   }
   
   @Override
   protected final boolean deepEquals3(AbstractVersionImpl other) {
      // no new fields
      return other instanceof SemanticVersionImpl;
   }

   @Override
   protected final int editDistance3(AbstractVersionImpl other, int editDistance) {
      // no new fields
      return editDistance;
   }
   
}

