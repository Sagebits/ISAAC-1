/*
 * Copyright 2018 Organizations participating in ISAAC, ISAAC's KOMET, and SOLOR development include the
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
package sh.isaac.komet.preferences;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import sh.isaac.api.preferences.IsaacPreferences;
import sh.isaac.komet.preferencesfx.model.Category;

/**
 *
 * @author kec
 */
public class WindowPreferences extends AbstractPreferences {
    StringProperty windowNameProperty = new SimpleStringProperty(this, "Window name", "String");

    public WindowPreferences(IsaacPreferences preferencesNode, String preferencesName) {
        super(preferencesNode, preferencesName);
    }

    @Override
    public Property<?>[] getProperties() {
        return new Property<?>[] { windowNameProperty };
    }

    @Override
    public Category[] getCategories() {
        return new Category[] {};
    }
    
}
