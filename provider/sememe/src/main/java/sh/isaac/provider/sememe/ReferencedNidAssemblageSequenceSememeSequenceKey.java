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



package sh.isaac.provider.sememe;

/**
 * Created by kec on 12/18/14.
 */
public class ReferencedNidAssemblageSequenceSememeSequenceKey
         implements Comparable<ReferencedNidAssemblageSequenceSememeSequenceKey> {
   int referencedNid;
   int assemblageSequence;
   int sememeSequence;

   //~--- constructors --------------------------------------------------------

   public ReferencedNidAssemblageSequenceSememeSequenceKey(int referencedNid,
         int assemblageSequence,
         int sememeSequence) {
      this.referencedNid      = referencedNid;
      this.assemblageSequence = assemblageSequence;
      this.sememeSequence     = sememeSequence;
   }

   //~--- methods -------------------------------------------------------------

   @Override
   public int compareTo(ReferencedNidAssemblageSequenceSememeSequenceKey o) {
      if (referencedNid != o.referencedNid) {
         if (referencedNid < o.referencedNid) {
            return -1;
         }

         return 1;
      }

      if (assemblageSequence != o.assemblageSequence) {
         if (assemblageSequence < o.assemblageSequence) {
            return -1;
         }

         return 1;
      }

      if (sememeSequence == o.sememeSequence) {
         return 0;
      }

      if (sememeSequence < o.sememeSequence) {
         return -1;
      }

      return 1;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      }

      if ((o == null) || (getClass() != o.getClass())) {
         return false;
      }

      ReferencedNidAssemblageSequenceSememeSequenceKey sememeKey = (ReferencedNidAssemblageSequenceSememeSequenceKey) o;

      if (referencedNid != sememeKey.referencedNid) {
         return false;
      }

      if (assemblageSequence != sememeKey.assemblageSequence) {
         return false;
      }

      return sememeSequence == sememeKey.sememeSequence;
   }

   @Override
   public int hashCode() {
      int result = referencedNid;

      result = 31 * result + assemblageSequence;
      result = 31 * result + sememeSequence;
      return result;
   }

   @Override
   public String toString() {
      return "Key{" + "referencedNid=" + referencedNid + ", assemblageSequence=" + assemblageSequence +
             ", sememeSequence=" + sememeSequence + '}';
   }

   //~--- get methods ---------------------------------------------------------

   public int getAssemblageSequence() {
      return assemblageSequence;
   }

   public int getReferencedNid() {
      return referencedNid;
   }

   public int getSememeSequence() {
      return sememeSequence;
   }
}

