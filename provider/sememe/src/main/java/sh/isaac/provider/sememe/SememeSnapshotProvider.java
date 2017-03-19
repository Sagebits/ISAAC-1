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

//~--- JDK imports ------------------------------------------------------------

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

//~--- non-JDK imports --------------------------------------------------------

import sh.isaac.api.ProgressTracker;
import sh.isaac.api.chronicle.LatestVersion;
import sh.isaac.api.collections.SememeSequenceSet;
import sh.isaac.api.collections.StampSequenceSet;
import sh.isaac.api.component.sememe.SememeService;
import sh.isaac.api.component.sememe.SememeSnapshotService;
import sh.isaac.api.component.sememe.SememeType;
import sh.isaac.api.component.sememe.version.SememeVersion;
import sh.isaac.api.coordinate.StampCoordinate;
import sh.isaac.api.snapshot.calculator.RelativePositionCalculator;
import sh.isaac.model.sememe.SememeChronologyImpl;

//~--- classes ----------------------------------------------------------------

/**
 *
 * @author kec
 * @param <V>
 */
public class SememeSnapshotProvider<V extends SememeVersion<?>>
         implements SememeSnapshotService<V> {
   Class<V>                   versionType;
   StampCoordinate            stampCoordinate;
   SememeService              sememeProvider;
   RelativePositionCalculator calculator;

   //~--- constructors --------------------------------------------------------

   public SememeSnapshotProvider(Class<V> versionType, StampCoordinate stampCoordinate, SememeService sememeProvider) {
      this.versionType     = versionType;
      this.stampCoordinate = stampCoordinate;
      this.sememeProvider  = sememeProvider;
      this.calculator      = RelativePositionCalculator.getCalculator(stampCoordinate);
   }

   //~--- get methods ---------------------------------------------------------

   @Override
   public Stream<LatestVersion<V>> getLatestDescriptionVersionsForComponent(int componentNid) {
      return getLatestSememeVersions(sememeProvider.getSememeSequencesForComponent(componentNid)
            .parallelStream()
            .filter(sememeSequence -> (sememeProvider.getSememe(sememeSequence)
                  .getSememeType() == SememeType.DESCRIPTION)));
   }

   @Override
   public Optional<LatestVersion<V>> getLatestSememeVersion(int sememeSequenceOrNid) {
      SememeChronologyImpl<?> sc              = (SememeChronologyImpl<?>) sememeProvider.getSememe(sememeSequenceOrNid);
      IntStream               stampSequences  = sc.getVersionStampSequences();
      StampSequenceSet        latestSequences = calculator.getLatestStampSequencesAsSet(stampSequences);

      if (latestSequences.isEmpty()) {
         return Optional.empty();
      }

      LatestVersion<V> latest = new LatestVersion<>(versionType);

      latestSequences.stream().forEach((stampSequence) -> {
                                 latest.addLatest((V) sc.getVersionForStamp(stampSequence)
                                       .get());
                              });
      return Optional.of(latest);
   }

   private Stream<LatestVersion<V>> getLatestSememeVersions(IntStream sememeSequenceStream,
         ProgressTracker... progressTrackers) {
      return sememeSequenceStream.mapToObj((int sememeSequence) -> {
               try {
                  SememeChronologyImpl<?> sc = (SememeChronologyImpl<?>) sememeProvider.getSememe(sememeSequence);
                  IntStream               stampSequences       = sc.getVersionStampSequences();
                  StampSequenceSet        latestStampSequences =
                     calculator.getLatestStampSequencesAsSet(stampSequences);

                  if (latestStampSequences.isEmpty()) {
                     return Optional.empty();
                  }

                  LatestVersion<V> latest = new LatestVersion<>(versionType);

                  latestStampSequences.stream().forEach((stampSequence) -> {
                                                  latest.addLatest((V) sc.getVersionForStamp(stampSequence)
                                                        .get());
                                               });
                  return Optional.of(latest);
               } finally {
                  Arrays.stream(progressTrackers).forEach((tracker) -> {
                                    tracker.completedUnitOfWork();
                                 });
               }
            })
                                 .filter((optional) -> {
                                            return optional.isPresent();
                                         })
                                 .map((optional) -> (LatestVersion<V>) optional.get());
   }

   private Stream<LatestVersion<V>> getLatestSememeVersions(SememeSequenceSet sememeSequenceSet,
         ProgressTracker... progressTrackers) {
      Arrays.stream(progressTrackers).forEach((tracker) -> {
                        tracker.addToTotalWork(sememeSequenceSet.size());
                     });
      return getLatestSememeVersions(sememeSequenceSet.parallelStream(), progressTrackers);
   }

   @Override
   public Stream<LatestVersion<V>> getLatestSememeVersionsForComponent(int componentNid) {
      return getLatestSememeVersions(sememeProvider.getSememeSequencesForComponent(componentNid));
   }

   @Override
   public Stream<LatestVersion<V>> getLatestSememeVersionsForComponentFromAssemblage(int componentNid,
         int assemblageConceptSequence) {
      return getLatestSememeVersions(sememeProvider.getSememeSequencesForComponentFromAssemblage(componentNid,
            assemblageConceptSequence));
   }

   @Override
   public Stream<LatestVersion<V>> getLatestSememeVersionsFromAssemblage(int assemblageConceptSequence,
         ProgressTracker... progressTrackers) {
      return getLatestSememeVersions(sememeProvider.getSememeSequencesFromAssemblage(assemblageConceptSequence),
                                     progressTrackers);
   }
}

