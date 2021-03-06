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
package sh.komet.gui.control.measure;

import java.util.Optional;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import org.controlsfx.control.PropertySheet;
import sh.isaac.api.ConceptProxy;
import sh.isaac.model.statement.MeasureImpl;
import sh.komet.gui.manifold.Manifold;

/**
 *
 * @author kec
 */
public class PropertySheetMeasureWrapper implements PropertySheet.Item {
    private final SimpleObjectProperty<MeasureImpl> measureProperty;
    private final String name;

    public PropertySheetMeasureWrapper(Manifold manifold, SimpleObjectProperty<MeasureImpl> measureProperty) {
        this.measureProperty = measureProperty;
        this.name = manifold.getPreferredDescriptionText(new ConceptProxy(measureProperty.getName()));
    }

    @Override
    public Class<?> getType() {
        return null;
    }

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public MeasureImpl getValue() {
        return measureProperty.get();
    }

    @Override
    public void setValue(Object value) {
        if (measureProperty.get() != value) {
            measureProperty.set((MeasureImpl) value);
        }
     }

    @Override
    public Optional<ObservableValue<? extends Object>> getObservableValue() {
        return Optional.of(measureProperty);
    }    
}
