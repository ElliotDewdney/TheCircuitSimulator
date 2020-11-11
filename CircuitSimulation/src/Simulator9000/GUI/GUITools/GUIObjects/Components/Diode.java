package GUI.GUITools.GUIObjects.Components;

import GUI.Canvas.InspectionWindow.Editable;
import GUI.Canvas.InspectionWindow.ValueController;
import GUI.GUITools.GUIObjects.GUIComp;
import GUI.GUITools.GUIObjects.GUINode;
import Utils.ComponentName;

/**
 * a {@link Component} of type Diode
 * used in the GUI for to talk to the simulation
 */
public class Diode extends Component{

    /**
     * defining the Property of forward voltage being Editable
     */
    @Editable( name = "Set Forward Voltage : " , TYPE = ValueController.Type.DOUBLE)
    public double forwardVoltage = 0.7;

    /**
     * @return the name of the component
     */
    @Override
    public ComponentName name(){return ComponentName.DIODE;}

    /**
     * defines the Node Placement of the component
     * @return a array of {@link GUI.GUITools.GUIObjects.GUINode.Placement} defining position
     */
    @Override
    public GUINode.Placement[] generateNodes() {
        GUINode.Placement[] nodes = {GUINode.Placement.MID_LEFT, GUINode.Placement.MID_RIGHT};
        return nodes;
    }

    /**
     * set the simulation properties
     * @return the component holding the simulation maths
     */
    @Override
    public Simulator9000.components.Component getComponent() {
        return new Simulator9000.components.circuitComponents.Diode(forwardVoltage);
    }

    /**
     * this is used when the simulation is stopped
     * @param comp passed the component that handles the GUI parts
     */
    @Override
    public void resetSimulation(GUIComp comp) { comp.getGUIObject().Image.setEffect(null); }

    /**
     * used set the data to be shown by the simulation window
     * @return the data that needs to be displayed
     */
    @Override
    public String getData() {
        return "Forward voltage = " + forwardVoltage;
    }
}
