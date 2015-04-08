/*
 * Copyright 2015 kec.
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
package gov.vha.isaac.ochre.model.coordinate;

import gov.vha.isaac.ochre.api.coordinate.LanguageCoordinate;
import java.util.Arrays;

/**
 *
 * @author kec
 */
public class LanguageCoordinateImpl implements LanguageCoordinate {
    
    int languageConceptSequence;
    int[] dialectAssemblagePreferenceList;
    int[] descriptionTypePreferenceList;

    public LanguageCoordinateImpl(int languageConceptSequence, int[] dialectAssemblagePreferenceList, int[] descriptionTypePreferenceList) {
        this.languageConceptSequence = languageConceptSequence;
        this.dialectAssemblagePreferenceList = dialectAssemblagePreferenceList;
        this.descriptionTypePreferenceList = descriptionTypePreferenceList;
    }
    
    

    @Override
    public int getLanugageConceptSequence() {
        return languageConceptSequence;
    }

    @Override
    public int[] getDialectAssemblagePreferenceList() {
        return dialectAssemblagePreferenceList;
    }

    @Override
    public int[] getDescriptionTypePreferenceList() {
        return descriptionTypePreferenceList;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + this.languageConceptSequence;
        hash = 79 * hash + Arrays.hashCode(this.dialectAssemblagePreferenceList);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LanguageCoordinateImpl other = (LanguageCoordinateImpl) obj;
        if (this.languageConceptSequence != other.languageConceptSequence) {
            return false;
        }
        if (!Arrays.equals(this.dialectAssemblagePreferenceList, other.dialectAssemblagePreferenceList)) {
            return false;
        }
        if (!Arrays.equals(this.descriptionTypePreferenceList, other.descriptionTypePreferenceList)) {
            return false;
        }
        return true;
    }
    
}