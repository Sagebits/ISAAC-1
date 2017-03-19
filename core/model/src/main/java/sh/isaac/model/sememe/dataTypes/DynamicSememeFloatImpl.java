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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import sh.isaac.api.component.sememe.version.dynamicSememe.dataTypes.DynamicSememeFloat;

//~--- classes ----------------------------------------------------------------

/**
 *
 * {@link DynamicSememeFloatImpl}
 *
 * @author <a href="mailto:daniel.armbrust.list@gmail.com">Dan Armbrust</a>
 */
public class DynamicSememeFloatImpl
        extends DynamicSememeDataImpl
         implements DynamicSememeFloat {
   private ObjectProperty<Float> property_;

   //~--- constructors --------------------------------------------------------

   protected DynamicSememeFloatImpl(byte[] data) {
      super(data);
   }

   public DynamicSememeFloatImpl(float f) {
      super();
      data_ = DynamicSememeIntegerImpl.intToByteArray(Float.floatToIntBits(f));
   }

   protected DynamicSememeFloatImpl(byte[] data, int assemblageSequence, int columnNumber) {
      super(data, assemblageSequence, columnNumber);
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * @see org.ihtsdo.otf.tcc.api.refexDynamic.data.dataTypes.RefexDynamicFloatBI#getDataFloat()
    */
   @Override
   public float getDataFloat() {
      return Float.intBitsToFloat(DynamicSememeIntegerImpl.getIntFromByteArray(data_));
   }

   /**
    * @see org.ihtsdo.otf.tcc.api.refexDynamic.data.dataTypes.RefexDynamicFloatBI#getDataFloatProperty()
    */
   @Override
   public ReadOnlyObjectProperty<Float> getDataFloatProperty() {
      if (property_ == null) {
         property_ = new SimpleObjectProperty<>(null, getName(), getDataFloat());
      }

      return property_;
   }

   /**
    * @see org.ihtsdo.otf.tcc.api.refexDynamic.data.RefexDynamicDataBI#getDataObject()
    */
   @Override
   public Object getDataObject() {
      return getDataFloat();
   }

   /**
    * @see org.ihtsdo.otf.tcc.api.refexDynamic.data.RefexDynamicDataBI#getDataObjectProperty()
    */
   @Override
   public ReadOnlyObjectProperty<?> getDataObjectProperty() {
      return getDataFloatProperty();
   }
}

