package GUI.GUITools.GUIObjects.Components;

import GUI.Canvas.InspectionWindow.Editable;
import GUI.Canvas.InspectionWindow.ValueController;
import GUI.GUITools.GUIObjects.GUIComp;
import GUI.GUITools.GUIObjects.GUINode;
import Utils.ComponentName;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.Shadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

/**
 * a {@link Component} of type bulb
 * used in the GUI for to talk to the simulation
 */
public class Bulb extends Component{

    /**
     * defining the Property of rating being Editable
     */
    @Editable(name = "Set Bulb Rating (V) : ", TYPE = ValueController.Type.DOUBLE)
    public double rating = 1;

    /**
     * @return the name of the component
     */
    @Override
    public ComponentName name(){return ComponentName.BULB;}

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
        return new Simulator9000.components.circuitComponents.Bulb(rating);
    }

    /**
     * used set the data to be shown by the simulation window
     * @return the data that needs to be displayed
     */
    @Override
    public String getData() {
        return "Rating = " + rating;
    }

    /**
     * this is used when the simulation is stopped
     * @param comp passed the component that handles the GUI parts
     */
    @Override
    public void resetSimulation(GUIComp comp) {
        if(light != null)light.setVisible(false);
        light = null;
    }

    private Circle light;

    /**
     *  used to define the Extra details that are show when the simulation details are shown
     * @param comp the component that handles the GUI elements
     * @return a GUI elements that contains the extra details
     */
    @Override
    public Node Extras(GUIComp comp){
        double PD = Math.abs(comp.Voltages()[0] - comp.Voltages()[1]);
        if(light != null) light.setVisible(false);
        light = new Circle(0, Color.YELLOW);
        light.setCenterX(32);
        light.setCenterY(32);
        light.setOpacity(0.3);
        light.setEffect(new Shadow(1, Color.YELLOW));
        comp.getGUIObject().Image.getChildren().add(light);
        Label temp = new Label();
        temp.setFont(new Font(15));
        if(PD > rating) {
            light.setRadius(Math.abs(comp.getCurrentOnPin(0))*50);
            temp.setText("Bulb : ON");
        }else temp.setText("Bulb : OFF");
        return temp;
    }
}
