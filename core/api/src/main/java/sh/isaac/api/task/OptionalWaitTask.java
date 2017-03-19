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



package sh.isaac.api.task;

//~--- JDK imports ------------------------------------------------------------

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

//~--- non-JDK imports --------------------------------------------------------

import javafx.concurrent.Task;

//~--- classes ----------------------------------------------------------------

/**
 * {@link OptionalWaitTask}
 * This class wraps a task, where the task doesn't serve the purpose of calculating a value,
 * but rather, is forcing a wait on a background task.
 *
 * This allows a caller to wait for the background task, or return the value immediately.
 *
 * A use case for this is in the object builders - the object is created and read,
 * but a subtask is issued to write the object to disk.
 *
 * Some callers may not care to wait for the write to disk, while others may.
 *
 * @author <a href="mailto:daniel.armbrust.list@gmail.com">Dan Armbrust</a>
 */
public class OptionalWaitTask<T> {
   private ArrayList<OptionalWaitTask<?>> backgroundTasks = new ArrayList<>();
   private Task<Void>                     primaryTask;
   private T                              value;

   //~--- constructors --------------------------------------------------------

   public OptionalWaitTask(Task<Void> task, T value, List<OptionalWaitTask<?>> nestedTasks) {
      primaryTask = task;

      if (nestedTasks != null) {
         backgroundTasks.addAll(nestedTasks);
      }

      this.value = value;
   }

   //~--- get methods ---------------------------------------------------------

   /**
    * Wait for the background task, then return the value.
    * @throws InterruptedException
    * @throws ExecutionException
    */
   public T get()
            throws InterruptedException, ExecutionException {
      for (OptionalWaitTask<?> t: backgroundTasks) {
         t.get();
      }

      if (primaryTask != null) {
         primaryTask.get();
      }

      return value;
   }

   /**
    * Calls {@link #get() but translates exceptions to runtime exceptions for convenient use in streaming APIs.}
    */
   public T getNoThrow() {
      try {
         return get();
      } catch (InterruptedException | ExecutionException e) {
         throw new RuntimeException(e);
      }
   }

   /**
    * Return the object immediately (but this object may not yet be serialized throughout the system)
    */
   public T getNoWait() {
      return value;
   }
}

