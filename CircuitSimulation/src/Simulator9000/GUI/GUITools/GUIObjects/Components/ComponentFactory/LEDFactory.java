package GUI.GUITools.GUIObjects.Components.ComponentFactory;

import GUI.GUITools.GUIObjects.Components.Component;
import GUI.GUITools.GUIObjects.Components.LED;

/**
 * a Factory of type {@link ComponentFactory} creates components
 */
public class LEDFactory implements ComponentFactory{

    /**
     * creates a new instance of LED to return
     * @return a new LED
     */
    @Override
    public Component createComponent() {
        return new LED();
    }
}
