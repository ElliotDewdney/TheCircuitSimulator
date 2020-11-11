package GUI.GUITools.GUIObjects.Components.ComponentFactory;

import GUI.GUITools.GUIObjects.Components.Component;
import GUI.GUITools.GUIObjects.Components.Switch;

/**
 * a Factory of type {@link ComponentFactory} creates components
 */
public class SwitchFactory implements ComponentFactory{

    /**
     * creates a new instance of Switch to return
     * @return a new Switch
     */
    @Override
    public Component createComponent() {
        return new Switch();
    }
}
