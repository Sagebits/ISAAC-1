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
package sh.komet.gui.util;

import javax.inject.Singleton;
import org.jvnet.hk2.annotations.Service;
import sh.isaac.api.Get;
import sh.isaac.api.StaticIsaacCache;
import sh.komet.gui.contract.DialogService;
import sh.komet.gui.contract.RulesDrivenKometService;
import sh.komet.gui.contract.StatusMessageService;
import sh.komet.gui.provider.StatusMessageProvider;

/**
 *
 * @author kec
 */
@Service
@Singleton
public class FxGet implements StaticIsaacCache
{
   public static final String SHOW_BETA_PROPERTY = "SHOW_BETA_FEATURES";
   
   private static DialogService DIALOG_SERVICE = null;
   private static RulesDrivenKometService RULES_DRIVEN_KOMET_SERVICE = null;
   private static StatusMessageProvider STATUS_MESSAGE_PROVIDER = null;

   public static DialogService dialogs() {
      if (DIALOG_SERVICE == null) {
         DIALOG_SERVICE = Get.service(DialogService.class);
      }
      return DIALOG_SERVICE;
   }

   public static StatusMessageService statusMessageService() {
      if (STATUS_MESSAGE_PROVIDER == null) {
         STATUS_MESSAGE_PROVIDER = new StatusMessageProvider();
      }
      return STATUS_MESSAGE_PROVIDER;
   }

   public static RulesDrivenKometService rulesDrivenKometService() {
      if (RULES_DRIVEN_KOMET_SERVICE == null) {
         RULES_DRIVEN_KOMET_SERVICE = Get.service(RulesDrivenKometService.class);
      }
      return RULES_DRIVEN_KOMET_SERVICE;
   }

   public static boolean showBetaFeatures() {
      return Boolean.getBoolean(SHOW_BETA_PROPERTY);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void reset() {
      DIALOG_SERVICE = null;
      RULES_DRIVEN_KOMET_SERVICE = null;
      STATUS_MESSAGE_PROVIDER = null;
   }
}
