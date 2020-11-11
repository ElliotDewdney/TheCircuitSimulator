package GUI.GUITools.GUIObjects.Components.ComponentFactory;

import GUI.GUITools.GUIObjects.Components.Component;
import GUI.GUITools.GUIObjects.Components.Diode;

/**
 * a Factory of type {@link ComponentFactory} creates components
 */
public class DiodeFactory implements ComponentFactory{

    /**
     * creates a new instance of Diode to return
     * @return a new Diode
     */
    @Override
    public Component createComponent() {
        return new Diode();
    }
}
