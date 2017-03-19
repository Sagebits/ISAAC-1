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



package sh.isaac.mojo.ibdf.diff;

//~--- JDK imports ------------------------------------------------------------

import java.io.File;

import java.util.List;
import java.util.Map;
import java.util.Set;

//~--- non-JDK imports --------------------------------------------------------

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

import org.jvnet.hk2.annotations.Service;

import sh.isaac.api.LookupService;
import sh.isaac.api.externalizable.BinaryDataDifferService;
import sh.isaac.api.externalizable.BinaryDataDifferService.ChangeType;
import sh.isaac.api.externalizable.OchreExternalizable;
import sh.isaac.api.externalizable.OchreExternalizableObjectType;
import sh.isaac.mojo.external.QuasiMojo;

//~--- classes ----------------------------------------------------------------

/**
 * Examines two ibdf files containing two distinct versions of the same
 * terminology and identifies the new/inactivated/modified content between the
 * two versions.
 *
 * Once identified, a new changeset file is generated containing these changes.
 * This file can then be imported into an existing database contining the old
 * version of the terminology. This will upgrade it to the new terminology.
 *
 * {@link QuasiMojo}
 *
 * @author <a href="mailto:jefron@westcoastinformatics.com">Jesse Efron</a>
 */
@Service(name = "diff-ibdfs")
public class BinaryDataDifferMojo
        extends QuasiMojo {
   @Parameter
   private Boolean diffOnStatus        = false;
   @Parameter
   private Boolean diffOnTimestamp     = false;
   @Parameter
   private Boolean diffOnAuthor        = false;
   @Parameter
   private Boolean diffOnModule        = false;
   @Parameter
   private Boolean diffOnPath          = false;
   @Parameter
   private Boolean createAnalysisFiles = false;

   /**
    * {@code ibdf format} files to import.
    */
   @Parameter(required = true)
   private File oldVersionFile;

   /**
    * {@code ibdf format} files to import.
    */
   @Parameter(required = true)
   private File newVersionFile;

   /**
    * {@code ibdf format} files to import.
    */
   @Parameter(required = true)
   private String analysisFilesOutputDir;

   /**
    * {@code ibdf format} files to import.
    */
   @Parameter(required = true)
   private String ibdfFileOutputDir;
   @Parameter
   private String importDate;
   @Parameter(required = true)
   String         changesetFileName;

   //~--- methods -------------------------------------------------------------

   public void execute()
            throws MojoExecutionException {
      BinaryDataDifferService differService = LookupService.getService(BinaryDataDifferService.class);

      differService.initialize(analysisFilesOutputDir,
                               ibdfFileOutputDir,
                               changesetFileName,
                               createAnalysisFiles,
                               diffOnStatus,
                               diffOnTimestamp,
                               diffOnAuthor,
                               diffOnModule,
                               diffOnPath,
                               importDate);

      try {
         Map<OchreExternalizableObjectType, Set<OchreExternalizable>> oldContentMap =
            differService.processVersion(oldVersionFile);
         Map<OchreExternalizableObjectType, Set<OchreExternalizable>> newContentMap =
            differService.processVersion(newVersionFile);
         Map<ChangeType, List<OchreExternalizable>> changedComponents =
            differService.identifyVersionChanges(oldContentMap,
                                                 newContentMap);

         differService.generateDiffedIbdfFile(changedComponents);

         if (createAnalysisFiles) {
            differService.writeFilesForAnalysis(oldContentMap,
                  newContentMap,
                  changedComponents,
                  ibdfFileOutputDir,
                  analysisFilesOutputDir);
         }
      } catch (Exception e) {
         throw new MojoExecutionException(e.getMessage(), e);
      }
   }
}

