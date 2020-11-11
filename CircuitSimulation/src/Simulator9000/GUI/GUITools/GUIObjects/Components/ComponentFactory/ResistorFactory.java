package GUI.GUITools.GUIObjects.Components.ComponentFactory;

import GUI.GUITools.GUIObjects.Components.Component;
import GUI.GUITools.GUIObjects.Components.Resistor;

/**
 * a Factory of type {@link ComponentFactory} creates components
 */
public class ResistorFactory implements ComponentFactory {

    /**
     * creates a new instance of Resistor to return
     * @return a new Resistor
     */
    @Override
    public Component createComponent() {
         return new Resistor();
    }
}
