package sh.komet.gui.search.control;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.AnchorPane;
import org.controlsfx.control.PropertySheet;
import org.controlsfx.property.editor.Editors;
import org.controlsfx.property.editor.PropertyEditor;
import sh.isaac.MetaData;
import sh.isaac.api.Get;
import sh.isaac.api.component.concept.ConceptChronology;
import sh.isaac.api.component.concept.ConceptSpecification;
import sh.isaac.api.observable.coordinate.ObservableEditCoordinate;
import sh.isaac.api.observable.coordinate.ObservableLanguageCoordinate;
import sh.isaac.api.observable.coordinate.ObservableLogicCoordinate;
import sh.isaac.api.observable.coordinate.ObservableStampCoordinate;
import sh.komet.gui.control.PropertySheetItemConceptWrapper;
import sh.komet.gui.manifold.Manifold;

import java.util.ArrayList;
import java.util.Collection;
import sh.komet.gui.control.ConceptForControlWrapper;

/**
 *
 * @author aks8m
 */

public class LetPropertySheet {

    private PropertySheet propertySheet;
    private ObservableList<PropertySheet.Item> items;
    private Manifold manifold;
    private Manifold originalManifoldDeepClone;

    private static final String PATH = "Path";
    private static final String LANGUAGE = "Language";
    private static final String CLASSIFIER = "Classifier";
    private static final String DESCRIPTION_LOGIC = "Description Logic";
    private static final String DESCRIPTION_TYPE = "Description Type";
    private static final String DIALECT = "Dialect";

    public LetPropertySheet(Manifold manifold){
        this.originalManifoldDeepClone = manifold.deepClone();
        this.manifold = manifold;
        items = FXCollections.observableArrayList();

        buildPropertySheetItems();

        this.propertySheet = new PropertySheet(this.items);
        this.propertySheet.setMode(PropertySheet.Mode.NAME);
        this.propertySheet.setSearchBoxVisible(false);
        this.propertySheet.setModeSwitcherVisible(false);

        AnchorPane.setBottomAnchor(this.propertySheet, 0.0);
        AnchorPane.setTopAnchor(this.propertySheet, 0.0);
        AnchorPane.setLeftAnchor(this.propertySheet, 0.0);
        AnchorPane.setRightAnchor(this.propertySheet, 0.0);

        this.propertySheet.setPropertyEditorFactory(param -> {
            switch (param.getName()){
                case PATH:
                    return createCustomChoiceEditor(MetaData.PATH____ISAAC, param);
                case LANGUAGE:
                    return createCustomChoiceEditor(MetaData.LANGUAGE____ISAAC, param);
                case CLASSIFIER:
                    return createCustomChoiceEditor(MetaData.DESCRIPTION_LOGIC_CLASSIFIER____ISAAC, param);
                case DESCRIPTION_LOGIC:
                    return createCustomChoiceEditor(MetaData.DESCRIPTION_LOGIC_PROFILE____ISAAC, param);
                case DESCRIPTION_TYPE:
                    return createCustomChoiceEditor(MetaData.DESCRIPTION_TYPE____ISAAC, param);
                case DIALECT:
                    return createCustomChoiceEditor(MetaData.DIALECT_ASSEMBLAGE____ISAAC, param);
            }

            return Editors.createTextEditor(param);
        });
    }

    private PropertyEditor<?> createCustomChoiceEditor(ConceptSpecification conceptSpecification, PropertySheet.Item param){
        Collection<ConceptForControlWrapper> collection = new ArrayList<>();
        ConceptChronology concept = Get.concept(conceptSpecification.getConceptSequence());

        Get.taxonomyService().getAllRelationshipOriginSequences(concept.getNid()).forEach(i -> {
            ConceptForControlWrapper propertySheetItemConceptWrapper = 
                    new ConceptForControlWrapper(this.originalManifoldDeepClone, i);
            collection.add(propertySheetItemConceptWrapper);
            //System.out.println(Get.concept(i));
        });

        return Editors.createChoiceEditor(param, collection);
    }

    /**
     * Add to the items Observable list of PropertySheet Items
     */
    private void buildPropertySheetItems(){
        /**
         * Debug for correct oberservable implementation
         */
        this.manifold.getLanguageCoordinate().languageConceptSequenceProperty().addListener((observable, oldValue, newValue) ->
                System.out.println("Language Changed to: " + this.originalManifoldDeepClone.getPreferredDescriptionText(newValue.intValue()))
        );
        ///


        this.items.add(new PropertySheetItemConceptWrapper(this.originalManifoldDeepClone,
                this.manifold.getLanguageCoordinate().languageConceptSequenceProperty().get(),
                LANGUAGE,
                this.manifold.getLanguageCoordinate().languageConceptSequenceProperty()
        ));

        System.out.println(this.manifold);

        this.items.add(new PropertySheetItemConceptWrapper(this.originalManifoldDeepClone,
                this.manifold.getLogicCoordinate().classifierSequenceProperty().get(),
                CLASSIFIER,
                this.manifold.getLogicCoordinate().classifierSequenceProperty()
        ));
    }

    public PropertySheet getPropertySheet() {
        return propertySheet;
    }

    public Manifold getManifold() {
        return manifold;
    }

    public ObservableList<PropertySheet.Item> getItems() {
        return items;
    }
}
