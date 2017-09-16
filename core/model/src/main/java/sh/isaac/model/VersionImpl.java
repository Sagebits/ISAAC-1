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
package sh.isaac.model;

//~--- JDK imports ------------------------------------------------------------
import java.util.List;
import java.util.UUID;

//~--- non-JDK imports --------------------------------------------------------
import sh.isaac.api.Get;
import sh.isaac.api.State;
import sh.isaac.api.chronicle.Chronology;
import sh.isaac.api.chronicle.Version;
import sh.isaac.api.commit.CommitStates;
import sh.isaac.api.externalizable.ByteArrayDataBuffer;

//~--- classes ----------------------------------------------------------------
/**
 * The Class VersionImpl.
 *
 * @author kec
 */
public abstract class VersionImpl
        implements Version {

   /**
    * The chronicle.
    */
   protected final Chronology chronicle;

   /**
    * The stamp sequence.
    */
   private int stampSequence;

   /**
    * The version sequence.
    */
   private short versionSequence;

   //~--- constructors --------------------------------------------------------
   /**
    * Instantiates a new object version impl.
    *
    * @param chronicle the chronicle
    * @param stampSequence the stamp sequence
    * @param versionSequence the version sequence
    */
   public VersionImpl(Chronology chronicle, int stampSequence, short versionSequence) {
      this.chronicle = chronicle;
      this.stampSequence = stampSequence;
      this.versionSequence = versionSequence;
   }

   //~--- methods -------------------------------------------------------------
   /**
    * Cancel.
    */
   public void cancel() {
      if (!isUncommitted()) {
         throw new RuntimeException("Attempt to cancel an already committed version: " + this);
      }

      this.stampSequence = -1;
   }

   /**
    * Equals uses just STAMP comparison for fast evaluation, which works form committed versions, and most other cases.
    * For more complete evaluation, use deepEquals.
    *
    *
    * @param obj the obj
    * @return true, if successful
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (obj == null) {
         return false;
      }

      if (getClass() != obj.getClass()) {
         return false;
      }

      final VersionImpl other = (VersionImpl) obj;

      if (this.stampSequence != other.stampSequence) {
         return false;
      }

      return this.chronicle.getNid() == other.chronicle.getNid();
   }

   /**
    * DeepEquals considers all fields, not just the stamp and the assumptions that the commit manager will not allow
    * more one version for a given stamp. This extra consideration is necessary to support uncommitted versions, that
    * may change in a multi-user environment, including that an individual author may make changes on more than one path
    * at a time.
    *
    * @param other the object to compare.
    * @return true if all fields are equal, otherwise false.
    */
   public final boolean deepEquals(Object other) {
      if (!(other instanceof VersionImpl)) {
         return false;
      }
      VersionImpl otherVersion = (VersionImpl) other;
      if (this.stampSequence != otherVersion.stampSequence) {
         return false;
      }
      return deepEquals2(otherVersion);
   }

   protected abstract boolean deepEquals2(VersionImpl other);

   /**
    * A representation of how different two versions are from each other. The author field is weighted such that a
    * difference of author is considered greater than all the other fields combined. Edit distance is always positive.
    *
    * @param other the version to compute the edit distance with respect to.
    * @return the edit distance.
    */
   public int editDistance(VersionImpl other) {
      int editDistance = 0;
      if (this.getState() != other.getState()) {
         editDistance++;
      }
      if (this.getTime() != other.getTime()) {
         editDistance++;
      }
      if (this.getAuthorSequence() != other.getAuthorSequence()) {
         // weight author to overwhelm all others... 
         editDistance = editDistance + 1000;
      }
      if (this.getModuleSequence() != other.getModuleSequence()) {
         // weight module to overwhelm all except author... 
         editDistance = editDistance + 100;
      }
      if (this.getPathSequence() != other.getPathSequence()) {
         // weight path... 
         editDistance = editDistance + 10;
      }
      return editDistance2(other, editDistance);
   }

   protected abstract int editDistance2(VersionImpl other, int editDistance);

   /**
    * Hash code.
    *
    * @return the int
    */
   @Override
   public int hashCode() {
      int hash = 7;

      hash = 29 * hash + this.stampSequence;
      return hash;
   }

   /**
    * To string.
    *
    * @return the string
    */
   @Override
   public String toString() {
      return toString(new StringBuilder()).toString();
   }

   /**
    * To string.
    *
    * @param builder the builder
    * @return the string builder
    */
   public StringBuilder toString(StringBuilder builder) {
      builder.append(" ")
              .append(Get.stampService()
                      .describeStampSequence(this.stampSequence));
      return builder;
   }

   /**
    * To user string.
    *
    * @return the string
    */
   @Override
   public String toUserString() {
      return toString();
   }

   /**
    * Check uncommitted.
    *
    * @throws RuntimeException the runtime exception
    */
   protected void checkUncommitted()
           throws RuntimeException {
      if (!this.isUncommitted()) {
         throw new RuntimeException("Component is already committed");
      }
   }

   /**
    * Write version data.
    *
    * @param data the data
    */
   protected void writeVersionData(ByteArrayDataBuffer data) {
      data.putStampSequence(this.stampSequence);
      data.putShort(this.versionSequence);
   }

   //~--- get methods ---------------------------------------------------------
   /**
    * Gets the author sequence.
    *
    * @return the author sequence
    */
   @Override
   public int getAuthorSequence() {
      return Get.stampService()
              .getAuthorSequenceForStamp(this.stampSequence);
   }

   //~--- set methods ---------------------------------------------------------
   /**
    * Sets the author sequence.
    *
    * @param authorSequence the new author sequence
    */
   @Override
   public void setAuthorSequence(int authorSequence) {
      if (this.stampSequence != -1) {
         checkUncommitted();
         this.stampSequence = Get.stampService()
                 .getStampSequence(getState(),
                         getTime(),
                         authorSequence,
                         getModuleSequence(),
                         getPathSequence());
      }
   }

   //~--- get methods ---------------------------------------------------------
   /**
    * Gets the commit state.
    *
    * @return the commit state
    */
   @Override
   public CommitStates getCommitState() {
      if (isUncommitted()) {
         return CommitStates.UNCOMMITTED;
      }

      return CommitStates.COMMITTED;
   }

   /**
    * Gets the module sequence.
    *
    * @return the module sequence
    */
   @Override
   public int getModuleSequence() {
      return Get.stampService()
              .getModuleSequenceForStamp(this.stampSequence);
   }

   //~--- set methods ---------------------------------------------------------
   /**
    * Sets the module sequence.
    *
    * @param moduleSequence the new module sequence
    */
   @Override
   public void setModuleSequence(int moduleSequence) {
      if (this.stampSequence != -1) {
         checkUncommitted();
         this.stampSequence = Get.stampService()
                 .getStampSequence(getState(),
                         getTime(),
                         getAuthorSequence(),
                         moduleSequence,
                         getPathSequence());
      }
   }

   /**
    * Sets the state.
    *
    * @param state the new state
    */
   @Override
   public void setStatus(State state) {
      if (this.stampSequence != -1) {
         checkUncommitted();
         this.stampSequence = Get.stampService()
                 .getStampSequence(state,
                         getTime(),
                         getAuthorSequence(),
                         getModuleSequence(),
                         getPathSequence());
      }
   }

   //~--- get methods ---------------------------------------------------------
   /**
    * Gets the nid.
    *
    * @return the nid
    */
   @Override
   public int getNid() {
      return this.chronicle.getNid();
   }

   /**
    * Gets the path sequence.
    *
    * @return the path sequence
    */
   @Override
   public int getPathSequence() {
      return Get.stampService()
              .getPathSequenceForStamp(this.stampSequence);
   }

   //~--- set methods ---------------------------------------------------------
   /**
    * Sets the path sequence.
    *
    * @param pathSequence the new path sequence
    */
   @Override
   public void setPathSequence(int pathSequence) {
      if (this.stampSequence != -1) {
         checkUncommitted();
         this.stampSequence = Get.stampService()
                 .getStampSequence(getState(),
                         getTime(),
                         getAuthorSequence(),
                         getModuleSequence(),
                         pathSequence);
      }
   }

   //~--- get methods ---------------------------------------------------------
   /**
    * Gets the primordial uuid.
    *
    * @return the primordial uuid
    */
   @Override
   public UUID getPrimordialUuid() {
      return this.chronicle.getPrimordialUuid();
   }

   /**
    * Gets the stamp sequence.
    *
    * @return the stamp sequence
    */
   @Override
   public int getStampSequence() {
      return this.stampSequence;
   }

   /**
    * Gets the state.
    *
    * @return the state
    */
   @Override
   public State getState() {
      return Get.stampService()
              .getStatusForStamp(this.stampSequence);
   }

   /**
    * Gets the time.
    *
    * @return the time
    */
   @Override
   public long getTime() {
      return Get.stampService()
              .getTimeForStamp(this.stampSequence);
   }

   //~--- set methods ---------------------------------------------------------
   /**
    * Sets the time.
    *
    * @param time the new time
    */
   @Override
   public void setTime(long time) {
      if (this.stampSequence != -1) {
         checkUncommitted();
         this.stampSequence = Get.stampService()
                 .getStampSequence(getState(),
                         time,
                         getAuthorSequence(),
                         getModuleSequence(),
                         getPathSequence());
      }
   }

   //~--- get methods ---------------------------------------------------------
   /**
    * Checks if uncommitted.
    *
    * @return true, if uncommitted
    */
   @Override
   public boolean isUncommitted() {
      return this.getTime() == Long.MAX_VALUE;
   }

   /**
    * Gets the uuid list.
    *
    * @return the uuid list
    */
   @Override
   public List<UUID> getUuidList() {
      return this.chronicle.getUuidList();
   }

   /**
    * Gets the version sequence.
    *
    * @return the version sequence
    */
   public short getVersionSequence() {
      return this.versionSequence;
   }

   //~--- set methods ---------------------------------------------------------
   /**
    * Sets the version sequence.
    *
    * @param versionSequence the new version sequence
    */
   public void setVersionSequence(short versionSequence) {
      this.versionSequence = versionSequence;
   }
}
