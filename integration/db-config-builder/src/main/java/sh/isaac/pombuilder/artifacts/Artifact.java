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



package sh.isaac.pombuilder.artifacts;

/**
 *
 * {@link Artifact}
 * A base class for providing artifact information to the config builder tool.
 * @author <a href="mailto:daniel.armbrust.list@gmail.com">Dan Armbrust</a>
 */
public abstract class Artifact {
   private String groupId_;
   private String artifactId_;
   private String version_;
   private String classifier_;

   //~--- constructors --------------------------------------------------------

   public Artifact(String groupId, String artifactId, String version) {
      this(groupId, artifactId, version, null);
   }

   public Artifact(String groupId, String artifactId, String version, String classifier) {
      groupId_    = groupId;
      artifactId_ = artifactId;
      version_    = version;
      classifier_ = classifier;
   }

   //~--- methods -------------------------------------------------------------

   @Override
   public String toString() {
      return "Artifact [groupId_=" + groupId_ + ", artifactId_=" + artifactId_ + ", version_=" + version_ +
             ", classifier_=" + classifier_ + "]";
   }

   //~--- get methods ---------------------------------------------------------

   public String getArtifactId() {
      return artifactId_;
   }

   public String getClassifier() {
      return classifier_;
   }

   public boolean hasClassifier() {
      if ((classifier_ == null) || (classifier_.trim().length() == 0)) {
         return false;
      }

      return true;
   }

   public String getGroupId() {
      return groupId_;
   }

   public String getVersion() {
      return version_;
   }
}

