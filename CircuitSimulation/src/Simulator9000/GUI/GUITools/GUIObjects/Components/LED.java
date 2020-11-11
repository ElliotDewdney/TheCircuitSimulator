package GUI.GUITools.GUIObjects.Components;

import GUI.Canvas.InspectionWindow.Editable;
import GUI.Canvas.InspectionWindow.ValueController;
import GUI.GUITools.GUIObjects.GUIComp;
import GUI.GUITools.GUIObjects.GUINode;
import Utils.ComponentName;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

/**
 * a {@link Component} of type LED
 * used in the GUI for to talk to the simulation
 */
public class LED extends Component{


    /**
     * defining the Property of forward voltage being Editable
     */
    @Editable( name = "Set Forward Voltage : " , TYPE = ValueController.Type.DOUBLE)
    public double forwardVoltage = 3.3;

    /**
     * @return the name of the component
     */
    @Override
    public ComponentName name(){return ComponentName.LED;}

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
    public void resetSimulation(GUIComp comp) {
        if(light == null) return;
        light.setVisible(false);
        light = null;
        comp.getGUIObject().Image.setEffect(null);
    }

    /**
     * used set the data to be shown by the simulation window
     * @return the data that needs to be displayed
     */
    @Override
    public String getData() {
        return "Forward voltage = " + forwardVoltage;
    }

    private Circle light;

    /**
     *  used to define the Extra details that are show when the simulation details are shown
     * @param comp the component that handles the GUI elements
     * @return a GUI elements that contains the extra details
     */
    @Override
    public Node Extras(GUIComp comp){
        if(light != null) light.setVisible(false);
        light = new Circle(0, Color.RED);
        light.setCenterX(32);
        light.setCenterY(32);
        light.setOpacity(0.1);
        comp.getGUIObject().Image.getChildren().add(light);
        Label temp = new Label();
        temp.setFont(new Font(15));
        if(comp.getCurrentOnPin(0) > 0.1) {
            light.setRadius(Math.abs(comp.getCurrentOnPin(0))*30);
            temp.setText("LED : ON");
        }else temp.setText("LED : OFF");
        return temp;
    }
}
