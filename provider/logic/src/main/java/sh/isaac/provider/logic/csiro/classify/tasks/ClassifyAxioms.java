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



package sh.isaac.provider.logic.csiro.classify.tasks;

import sh.isaac.api.Get;
import sh.isaac.api.task.AggregateTaskInput;
import sh.isaac.api.task.TimedTaskWithProgressTracker;
import sh.isaac.provider.logic.csiro.classify.ClassifierData;

/**
 * The Class ClassifyAxioms.
 *
 * @author kec
 */
public class ClassifyAxioms
        extends TimedTaskWithProgressTracker<ClassifierData> implements AggregateTaskInput {

   ClassifierData inputData;

   /**
    * Instantiates a new classify axioms.
    */
   public ClassifyAxioms() {
      updateTitle("Classify axioms");
   }
   
   /**
    * Must pass in a {@link ClassifierData} prior to executing this task 
    * @see sh.isaac.api.task.AggregateTaskInput#setInput(java.lang.Object)
    */
   @Override
   public void setInput(Object inputData)  {
      if (!(inputData instanceof ClassifierData)) {
         throw new RuntimeException("Input data to LoadAxioms must be " + ClassifierData.class.getName());
      }
      this.inputData = (ClassifierData)inputData;
   }

   @Override
   protected ClassifierData call()
            throws Exception {
      Get.activeTasks().add(this);
       try {
          if (inputData == null) {
              throw new RuntimeException("Input data to ClassifyAxioms must be specified by calling setInput prior to executing");
           }
           
           inputData.classify();
           return inputData;
       } finally {
           Get.activeTasks().remove(this);
       }
   }
}