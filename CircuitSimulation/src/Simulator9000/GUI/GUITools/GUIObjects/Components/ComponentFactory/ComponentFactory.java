package GUI.GUITools.GUIObjects.Components.ComponentFactory;

import GUI.GUITools.GUIObjects.Components.Component;
import javafx.scene.layout.Pane;

/**
 * this is for the use in the factory programing paradigms
 */
public interface ComponentFactory {

    /**
     * Creates an new instance of a component and returns it
     * @return new Component
     */
    Component createComponent();
}
