package GUI.GUITools.GUIObjects.Components.ComponentFactory;

import GUI.GUITools.GUIObjects.Components.Component;
import Utils.ComponentName;

import java.util.HashMap;

/**
 * used in the construction of new Components
 */
public class Constructinator {
    private static HashMap<ComponentName, ComponentFactory> Factories = new HashMap<>();

    /*
     * sets the values of the Component Factories in the map
     */
    static {
        Factories.put(ComponentName.BULB, new BulbFactory());
        Factories.put(ComponentName.RESISTOR, new ResistorFactory());
        Factories.put(ComponentName.CELL, new CellFactory());
        Factories.put(ComponentName.LED, new LEDFactory());
        Factories.put(ComponentName.SWITCH, new SwitchFactory());
        Factories.put(ComponentName.DIODE, new DiodeFactory());
        Factories.put(ComponentName.POTENTIOMETER, new PotentiometerFactory());
        Factories.put(ComponentName.VARIABLE_RESISTOR, new VariableResistorFactory());
    }

    /**
     * used to check if the Factory for the name give exists
     * @param name the name used to identify the component that needs to be constructed
     * @return if the component exists in the records
     */
    public static boolean Contains(ComponentName name){
        return Factories.containsKey(name);
    }

    /**
     * this returns the component of the type passed to the method
     * @param name the name used to identify the component that needs to be constructed
     * @return a new instance of the component that needs to be constructed
     */
    public static Component getComponent(ComponentName name){
        return Factories.get(name).createComponent();
    }

}

