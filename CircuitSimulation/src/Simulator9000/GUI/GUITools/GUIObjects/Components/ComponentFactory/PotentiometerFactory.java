package GUI.GUITools.GUIObjects.Components.ComponentFactory;

import GUI.GUITools.GUIObjects.Components.Component;
import GUI.GUITools.GUIObjects.Components.Potentiometer;

/**
 * a Factory of type {@link ComponentFactory} creates components
 */
public class PotentiometerFactory implements ComponentFactory{

    /**
     * creates a new instance of Potentiometer to return
     * @return a new Potentiometer
     */
    @Override
    public Component createComponent() {
        return new Potentiometer();
    }
}
