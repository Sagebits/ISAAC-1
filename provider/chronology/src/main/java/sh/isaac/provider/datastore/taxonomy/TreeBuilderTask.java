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



package sh.isaac.provider.datastore.taxonomy;

//~--- JDK imports ------------------------------------------------------------

import sh.isaac.api.bootstrap.TermAux;
import sh.isaac.api.coordinate.PremiseType;
import sh.isaac.api.snapshot.calculator.RelativePositionCalculator;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

//~--- non-JDK imports --------------------------------------------------------

import sh.isaac.api.Get;
import sh.isaac.api.LookupService;
import sh.isaac.api.coordinate.ManifoldCoordinate;
import sh.isaac.api.progress.Stoppable;
import sh.isaac.api.task.TimedTaskWithProgressTracker;
import sh.isaac.api.tree.Tree;
import sh.isaac.model.taxonomy.GraphCollectorIsolated;
import sh.isaac.model.taxonomy.TaxonomyFlag;
import sh.isaac.model.tree.HashTreeBuilderIsolated;

//~--- classes ----------------------------------------------------------------

/**
 *
 * @author kec
 */
public class TreeBuilderTask
        extends TimedTaskWithProgressTracker<Tree> implements Stoppable {
   private static final String             stopMessage = "Stop requested during compute";

   private String                          message           = "setting up taxonomy collection";
   private final int                       conceptCount;
   private final IntFunction<int[]>        taxonomyDataProvider;
   private final int                       conceptAssemblageNid;
   private boolean                         stopRequested = false;

   private final RelativePositionCalculator relativePositionCalculator;
   private final int taxonomyFlags;
   private final Optional<RelativePositionCalculator> optionalDestinationCalculator;
   private final Optional<Function<int[],int[]>> optionalSortFunction;
   private final PremiseType premiseType;

/*
    public GraphCollectorIsolated(IntFunction<int[]> taxonomyDataProvider,
                                  RelativePositionCalculator relativePositionCalculator,
                                  int taxonomyFlags,
                                  Optional<RelativePositionCalculator> optionalDestinationCalculator,
                                  Optional<Function<int[],int[]>> optionalSortFunction) {

 */


   private static final Logger LOG = LogManager.getLogger();

   //~--- constructors --------------------------------------------------------

   public TreeBuilderTask(IntFunction<int[]> taxonomyDataProvider,
                          ManifoldCoordinate manifoldCoordinate) {
      if (taxonomyDataProvider == null) {
         throw new IllegalStateException("taxonomyDataProvider cannot be null");
      }
      this.premiseType = manifoldCoordinate.getTaxonomyPremiseType();
      this.taxonomyDataProvider               = taxonomyDataProvider;
      this.relativePositionCalculator = manifoldCoordinate.getRelativePositionCalculator();
      this.taxonomyFlags = TaxonomyFlag.getFlagsFromManifoldCoordinate(manifoldCoordinate);
      if (manifoldCoordinate.optionalDestinationStampCoordinate().isPresent()) {

         this.optionalDestinationCalculator = Optional.of(
                 manifoldCoordinate.optionalDestinationStampCoordinate().get().getRelativePositionCalculator());
      } else {
         this.optionalDestinationCalculator = Optional.empty();
      }

      if (manifoldCoordinate.hasCustomTaxonomySort()) {
         this.optionalSortFunction = Optional.of(manifoldCoordinate::sortConcepts);
      } else {
         this.optionalSortFunction = Optional.empty();
      }
      this.conceptAssemblageNid               = manifoldCoordinate.getLogicCoordinate()
            .getConceptAssemblageNid();
      LookupService.registerStoppable(this, LookupService.SL_L5_ISAAC_STARTED_RUNLEVEL);
      this.conceptCount = (int) Get.identifierService()
                                   .getNidsForAssemblage(conceptAssemblageNid)
                                   .count();
      this.addToTotalWork(conceptCount * 2); // once to construct tree, ones to traverse tree
      this.updateTitle("Generating " + manifoldCoordinate.getTaxonomyPremiseType() + " snapshot");
      this.setProgressMessageGenerator(
          (task) -> {
             updateMessage(message);
          });
      setCompleteMessageGenerator(
          (task) -> {
             updateMessage(getState() + " in " + getFormattedDuration());
          });
      Get.activeTasks()
         .add(this);
   }

   //~--- methods -------------------------------------------------------------

   @Override
   protected Tree call()
            throws Exception {
      try {
         return compute();
      }
      catch (Exception e) {
         if (!(stopMessage.equals(e.getMessage()))) {
            LOG.error("Error in Tree Builder task", e);
         }
         else {
            LOG.info("Tree build interrupted by shutdown request");
         }
         throw e;
      }
      finally {
         Get.activeTasks()
            .remove(this);
      }
   }

   private Tree compute() {

      GraphCollectorIsolated  collector = new GraphCollectorIsolated(this.taxonomyDataProvider,
              this.relativePositionCalculator, this.taxonomyFlags,
              this.optionalDestinationCalculator,
              this.optionalSortFunction);
      IntStream       conceptNidStream = Get.identifierService()
                                            .getNidsForAssemblage(conceptAssemblageNid);
      long count = conceptNidStream.count();
      if (count == 0) {
         LOG.info("Empty concept stream in TreeBuilderTask");
      } 
      
      if (stopRequested) {
         throw new RuntimeException("Stop requested during compute");
      }
      conceptNidStream = Get.identifierService()
                                            .getNidsForAssemblage(conceptAssemblageNid);
      HashTreeBuilderIsolated graphBuilder     = (HashTreeBuilderIsolated) conceptNidStream.filter(
                                             (conceptNid) -> {
                                                if (conceptNid == TermAux.SOLOR_METADATA.getNid()) {
                                                   System.out.println("Found 1: " + TermAux.SOLOR_METADATA.getFullyQualifiedName());
                                                }

               completedUnitOfWork();
               return true;
            })
                                                         .collect(
                                                               () -> new HashTreeBuilderIsolated(
                                                                     this.premiseType,
                                                                           this.conceptAssemblageNid),
                                                                     collector,
                                                                     collector);

      message = "searching for redundancies and cycles";

      Tree tree = graphBuilder.getSimpleDirectedGraph(this);

      message = "complete";
      LOG.info("Tree build completed for {}", this.relativePositionCalculator.getDestination());
      return tree;
   }

   /** 
    * {@inheritDoc}
    */
   @Override
   public void stopJob() {
      stopRequested = true;
   }

   @Override
   public void completedUnitOfWork()
   {
      if (stopRequested) {
         throw new RuntimeException(stopMessage);
      }
      super.completedUnitOfWork();
   }

   @Override
   public void completedUnitsOfWork(long unitsCompleted)
   {
      if (stopRequested) {
         throw new RuntimeException(stopMessage);
      }
      super.completedUnitsOfWork(unitsCompleted);
   }
}

