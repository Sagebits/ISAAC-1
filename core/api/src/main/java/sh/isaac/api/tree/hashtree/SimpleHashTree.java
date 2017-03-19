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



package sh.isaac.api.tree.hashtree;

//~--- JDK imports ------------------------------------------------------------

import java.util.Arrays;
import java.util.stream.IntStream;

//~--- non-JDK imports --------------------------------------------------------

import sh.isaac.api.collections.ConceptSequenceSet;

//~--- classes ----------------------------------------------------------------

/**
 * Simple implementation that uses less space, but does not have some of the
 * features of the {@code HashTreeWithBitSets} which caches some tree features
 * as {@code BitSet} objects. Meant for use with short-lived and small trees.
 *
 * @author kec
 */
public class SimpleHashTree
        extends AbstractHashTree {
   public void addChild(int parentSequence, int childSequence) {
      maxSequence = Math.max(parentSequence, maxSequence);
      maxSequence = Math.max(childSequence, maxSequence);

      if (parentSequence_ChildSequenceArray_Map.containsKey(parentSequence)) {
         parentSequence_ChildSequenceArray_Map.put(parentSequence,
               addToArray(parentSequence_ChildSequenceArray_Map.get(parentSequence), childSequence));
      } else {
         parentSequence_ChildSequenceArray_Map.put(parentSequence, new int[] { childSequence });
      }

      if (childSequence_ParentSequenceArray_Map.containsKey(childSequence)) {
         childSequence_ParentSequenceArray_Map.put(childSequence,
               addToArray(childSequence_ParentSequenceArray_Map.get(childSequence), parentSequence));
      } else {
         childSequence_ParentSequenceArray_Map.put(childSequence, new int[] { parentSequence });
      }
   }

   /**
    * NOTE: not a constant time operation.
    *
    * @return number of unique nodes in this tree.
    */
   @Override
   public int size() {
      IntStream.Builder builder = IntStream.builder();

      parentSequence_ChildSequenceArray_Map.forEachPair((int first,
            int[] second) -> {
               builder.accept(first);
               IntStream.of(second)
                        .forEach((sequence) -> builder.add(sequence));
               return true;
            });
      return (int) builder.build()
                          .distinct()
                          .count();
   }

   private static int[] addToArray(int[] array, int toAdd) {
      if (Arrays.binarySearch(array, toAdd) >= 0) {
         return array;
      }

      int   length = array.length + 1;
      int[] result = new int[length];

      System.arraycopy(array, 0, result, 0, array.length);
      result[array.length] = toAdd;
      Arrays.sort(result);
      return result;
   }

   //~--- get methods ---------------------------------------------------------

   @Override
   public IntStream getRootSequenceStream() {
      ConceptSequenceSet parents = new ConceptSequenceSet();

      parentSequence_ChildSequenceArray_Map.forEachKey((int parent) -> {
               parents.add(parent);
               return true;
            });
      parentSequence_ChildSequenceArray_Map.forEachPair((int parent,
            int[] children) -> {
               IntStream.of(children)
                        .forEach((child) -> parents.remove(child));
               return true;
            });
      return parents.stream();
   }

   /**
    * NOTE: not a constant time operation.
    *
    * @return root sequences for this tree.
    */
   @Override
   public int[] getRootSequences() {
      return getRootSequenceStream().toArray();
   }
}

