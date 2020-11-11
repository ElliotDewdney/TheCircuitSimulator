package GUI.GUITools.GUIObjects.Components.ComponentFactory;

import GUI.GUITools.GUIObjects.Components.Bulb;
import GUI.GUITools.GUIObjects.Components.Component;

/**
 * a Factory of type {@link ComponentFactory} creates components
 */
public class BulbFactory implements ComponentFactory{

    /**
     * creates a new instance of Bulb to return
     * @return a new Bulb
     */
    @Override
    public Component createComponent() {
        return new Bulb();
    }
}
