package GUI.GUITools.GUIObjects.Components.ComponentFactory;

import GUI.GUITools.GUIObjects.Components.Cell;
import GUI.GUITools.GUIObjects.Components.Component;

/**
 * a Factory of type {@link ComponentFactory} creates components
 */
public class CellFactory implements ComponentFactory{

    /**
     * creates a new instance of Cell to return
     * @return a new Cell
     */
    @Override
    public Component createComponent() {
        return new Cell();
    }
}
