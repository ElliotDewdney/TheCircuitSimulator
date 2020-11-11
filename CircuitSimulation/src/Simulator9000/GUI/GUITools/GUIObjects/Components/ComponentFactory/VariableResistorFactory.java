package GUI.GUITools.GUIObjects.Components.ComponentFactory;

import GUI.GUITools.GUIObjects.Components.Bulb;
import GUI.GUITools.GUIObjects.Components.Component;
import GUI.GUITools.GUIObjects.Components.VariableResistor;

/**
 * a Factory of type {@link ComponentFactory} creates components
 */
public class VariableResistorFactory implements ComponentFactory{

    /**
     * creates a new instance of VariableResistor to return
     * @return a new VariableResistor
     */
    @Override
    public Component createComponent() {
        return new VariableResistor();
    }
}
