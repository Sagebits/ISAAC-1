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
package sh.isaac.model.waitfree;

//~--- non-JDK imports --------------------------------------------------------

import sh.isaac.api.externalizable.ByteArrayDataBuffer;
import sh.isaac.model.WaitFreeComparable;

//~--- interfaces -------------------------------------------------------------

/**
 * {@code WaitFreeMergeSerializer} objects enable wait-free serialization to
 * maps, where the objects are append only (data is added to, but never removed
 * from the object).
 * This ability to compare original
 * MD5 checksums with current {@code byte[]} data, enables compare and swap
 * updates to maps so that they may be updated using wait-free algorithms
 * (an algorithm where there is guaranteed per-thread progress). Wait-freedom is
 * the strongest non-blocking guarantee of progress).
 *
 * @author kec
 * @param <T> the generic type
 */
public interface WaitFreeMergeSerializer<T extends WaitFreeComparable> {
   /**
    * Deserialize.
    *
    * @param di the data from which to deserialize the object.
    * @return the t
    */
   T deserialize(ByteArrayDataBuffer di);

   /**
    * Support for merging objects when a compare and swap operation fails,
    * enabling wait-free serialization.
    * @param a the first object to merge.
    * @param b the second object to merge.
    * @param writeSequence  to use as origin of
    * the merged object.
    * @return the merged object.
    */
   T merge(T a, T b, int writeSequence);

   /**
    * Serialize.
    *
    * @param d the data output stream to write to.
    * @param a the object to serialize to the data output stream.
    */
   void serialize(ByteArrayDataBuffer d, T a);
}
