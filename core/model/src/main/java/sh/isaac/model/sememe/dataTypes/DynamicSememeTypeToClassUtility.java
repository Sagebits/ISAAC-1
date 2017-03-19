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



package sh.isaac.model.sememe.dataTypes;

//~--- non-JDK imports --------------------------------------------------------

import sh.isaac.api.component.sememe.version.dynamicSememe.DynamicSememeData;
import sh.isaac.api.component.sememe.version.dynamicSememe.DynamicSememeDataType;

//~--- classes ----------------------------------------------------------------

/**
 * {@link DynamicSememeTypeToClassUtility}
 *
 * @author <a href="mailto:daniel.armbrust.list@gmail.com">Dan Armbrust</a>
 */
public class DynamicSememeTypeToClassUtility {
   public static DynamicSememeDataImpl typeToClass(DynamicSememeDataType type,
         byte[] data,
         int assemblageSequence,
         int columnNumber) {
      switch (type) {
      case ARRAY:
         return new DynamicSememeArrayImpl<DynamicSememeData>(data, assemblageSequence, columnNumber);

      case BOOLEAN:
         return new DynamicSememeBooleanImpl(data, assemblageSequence, columnNumber);

      case BYTEARRAY:
         return new DynamicSememeByteArrayImpl(data, assemblageSequence, columnNumber);

      case DOUBLE:
         return new DynamicSememeDoubleImpl(data, assemblageSequence, columnNumber);

      case FLOAT:
         return new DynamicSememeFloatImpl(data, assemblageSequence, columnNumber);

      case INTEGER:
         return new DynamicSememeIntegerImpl(data, assemblageSequence, columnNumber);

      case LONG:
         return new DynamicSememeLongImpl(data, assemblageSequence, columnNumber);

      case NID:
         return new DynamicSememeNidImpl(data, assemblageSequence, columnNumber);

      case STRING:
         return new DynamicSememeStringImpl(data, assemblageSequence, columnNumber);

      case UUID:
         return new DynamicSememeUUIDImpl(data, assemblageSequence, columnNumber);

      case SEQUENCE:
         return new DynamicSememeSequenceImpl(data, assemblageSequence, columnNumber);

      case POLYMORPHIC:
      case UNKNOWN:
         throw new RuntimeException("No implementation exists for type unknown");

      default:
         throw new RuntimeException("Implementation error");
      }
   }

   protected static Class<? extends DynamicSememeData> implClassForType(DynamicSememeDataType type) {
      switch (type) {
      case ARRAY:
         return DynamicSememeArrayImpl.class;

      case BOOLEAN:
         return DynamicSememeBooleanImpl.class;

      case BYTEARRAY:
         return DynamicSememeByteArrayImpl.class;

      case DOUBLE:
         return DynamicSememeDoubleImpl.class;

      case FLOAT:
         return DynamicSememeFloatImpl.class;

      case INTEGER:
         return DynamicSememeIntegerImpl.class;

      case LONG:
         return DynamicSememeLongImpl.class;

      case NID:
         return DynamicSememeNidImpl.class;

      case STRING:
         return DynamicSememeStringImpl.class;

      case UUID:
         return DynamicSememeUUIDImpl.class;

      case SEQUENCE:
         return DynamicSememeSequenceImpl.class;

      case UNKNOWN:
      case POLYMORPHIC:
         throw new RuntimeException("Should be impossible");

      default:
         throw new RuntimeException("Design failure");
      }
   }

   protected static DynamicSememeData typeToClass(DynamicSememeDataType type, byte[] data) {
      switch (type) {
      case ARRAY:
         return new DynamicSememeArrayImpl<DynamicSememeData>(data);

      case BOOLEAN:
         return new DynamicSememeBooleanImpl(data);

      case BYTEARRAY:
         return new DynamicSememeByteArrayImpl(data);

      case DOUBLE:
         return new DynamicSememeDoubleImpl(data);

      case FLOAT:
         return new DynamicSememeFloatImpl(data);

      case INTEGER:
         return new DynamicSememeIntegerImpl(data);

      case LONG:
         return new DynamicSememeLongImpl(data);

      case NID:
         return new DynamicSememeNidImpl(data);

      case STRING:
         return new DynamicSememeStringImpl(data);

      case UUID:
         return new DynamicSememeUUIDImpl(data);

      case SEQUENCE:
         return new DynamicSememeSequenceImpl(data);

      case UNKNOWN:
      case POLYMORPHIC:
         throw new RuntimeException("Should be impossible");

      default:
         throw new RuntimeException("Design failure");
      }
   }
}

