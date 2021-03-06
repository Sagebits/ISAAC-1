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
package sh.komet.gui.control.repetition;

import java.util.ArrayList;
import java.util.List;
import org.controlsfx.control.PropertySheet;
import sh.isaac.model.statement.RepetitionImpl;
import sh.komet.gui.control.property.PropertyEditorFactory;
import sh.komet.gui.control.measure.PropertySheetMeasureWrapper;
import sh.komet.gui.manifold.Manifold;

/**
 *
 * @author kec
 */
public class RepetitionPropertySheet {
    
    protected final Manifold manifold;
    
    
    private final PropertySheet propertySheet = new PropertySheet();
    {
        this.propertySheet.setMode(PropertySheet.Mode.NAME);
        this.propertySheet.setSearchBoxVisible(false);
        this.propertySheet.setModeSwitcherVisible(false);
        
    }

    public RepetitionPropertySheet(Manifold manifold) {
        this.manifold = manifold;
        this.propertySheet.setPropertyEditorFactory(new PropertyEditorFactory(this.manifold));
    }
    
    public PropertySheet getPropertySheet() {
        return propertySheet;
    }
    
    public void setRepition(RepetitionImpl circumstance) {
        
        propertySheet.getItems().clear();
        if (circumstance != null) {
            propertySheet.getItems().addAll(getProperties(circumstance));
        }
    }

    private List<PropertySheet.Item> getProperties(RepetitionImpl circumstance) {
       ArrayList<PropertySheet.Item> itemList = new ArrayList<>();
       
       itemList.add(new PropertySheetMeasureWrapper(manifold, circumstance.periodStartProperty()));
       itemList.add(new PropertySheetMeasureWrapper(manifold, circumstance.periodDurationProperty()));
       itemList.add(new PropertySheetMeasureWrapper(manifold, circumstance.eventFrequencyProperty()));
       itemList.add(new PropertySheetMeasureWrapper(manifold, circumstance.eventDurationProperty()));
       
       return itemList;
    }
}
