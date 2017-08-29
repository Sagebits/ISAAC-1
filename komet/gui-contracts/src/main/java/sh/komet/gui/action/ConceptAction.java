/*
 * Copyright 2017 Organizations participating in ISAAC, ISAAC's KOMET, and SOLOR development include the 
         US Veterans Health Administration, OSHERA, and the Health Services Platform Consortium..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sh.komet.gui.action;

import java.util.Optional;
import java.util.function.Consumer;
import javafx.event.ActionEvent;
import org.controlsfx.control.action.Action;
import sh.isaac.api.component.concept.ConceptSpecification;

/**
 *
 * @author kec
 */
public class ConceptAction extends Action {
   ConceptSpecification actionConcept;

   public ConceptAction(ConceptSpecification actionConcept) {
      super((String) null);
      setupProxy(actionConcept);
   }

   public ConceptAction(ConceptSpecification actionConcept, Consumer<ActionEvent> eventHandler) {
      super(null, eventHandler);
      setupProxy(actionConcept);
   }

   private void setupProxy(ConceptSpecification actionConcept) {
      this.actionConcept = actionConcept;
      Optional<String> optionalDescription = actionConcept.getPreferedConceptDescriptionText();
      if (optionalDescription.isPresent()) {
         this.setText(optionalDescription.get());
      } else {
         this.setText(actionConcept.getFullySpecifiedConceptDescriptionText());
      }
   }

   public ConceptSpecification getActionConcept() {
      return actionConcept;
   }
   
}