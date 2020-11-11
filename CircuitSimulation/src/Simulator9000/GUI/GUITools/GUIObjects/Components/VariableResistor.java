package GUI.GUITools.GUIObjects.Components;

import GUI.Canvas.InspectionWindow.Editable;
import GUI.Canvas.InspectionWindow.ValueController;
import GUI.GUITools.GUIObjects.GUIComp;
import GUI.GUITools.GUIObjects.GUINode;
import Simulator9000.Circuit;
import Simulator9000.Interactable;
import Simulator9000.components.circuitComponents.Resistor;
import Utils.ComponentName;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 * a {@link Component} of type VariableResistor
 * used in the GUI for to talk to the simulation
 */
public class VariableResistor extends Component implements Interactable {

    /**
     * defining the Property of Percentage being Editable
     */
    @Editable(name = "Set initial value (%) : ", TYPE = ValueController.Type.DOUBLE)
    public double percentage = 50;
    private double currentPercentage;

    /**
     * defining the Property of resistance being Editable
     */
    @Editable( name = "Set total resistance : ", TYPE = ValueController.Type.DOUBLE)
    public double resistance = 30;

    /**
     * @return the name of the component
     */
    @Override
    public ComponentName name(){return ComponentName.POTENTIOMETER;}

    /**
     * defines the Node Placement of the component
     * @return a array of {@link GUI.GUITools.GUIObjects.GUINode.Placement} defining position
     */
    @Override
    public GUINode.Placement[] generateNodes() {
        GUINode.Placement[] nodes = {GUINode.Placement.MID_LEFT, GUINode.Placement.MID_RIGHT};
        return nodes;
    }

    public Resistor resistor;

    /**
     * set the simulation properties
     * @return the component holding the simulation maths
     */
    @Override
    public Simulator9000.components.Component getComponent() {
        resistor = new Resistor(resistance*percentage/100);
        currentPercentage = percentage;
        return resistor;
    }

    // set the data to be shown by the simulation window
    @Override
    public String getData() {
        return "Percentage = " + Math.round(currentPercentage*10)/10f + ", Total Resistance = " + Math.round(resistance*currentPercentage/10)/10f;
    }

    /**
     * this is used when the simulation is stopped
     * @param comp passed the component that handles the GUI parts
     */
    @Override
    public void resetSimulation(GUIComp comp) {
        comp.getGUIObject().Image.setEffect(null);
    }

    /**
     *  used to define the Extra details that are show when the simulation details are shown
     * @param comp the component that handles the GUI elements
     * @return a GUI elements that contains the extra details
     */
    @Override
    public Node Extras(GUIComp comp){
        Pane box = new Pane();
        box.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, new CornerRadii(4), BorderWidths.DEFAULT)));
        Slider slider = new Slider();
        slider.setMin(0.1);
        slider.setMax(99.9);
        slider.setValue(currentPercentage);
        slider.setOnMouseReleased(event -> {
            currentPercentage = slider.getValue();
            resistor.setResistance(resistance*currentPercentage/100);
            controller.WakeSimulation();
        });
        slider.setPadding(new Insets(20, 10 ,20 ,10));
        box.getChildren().add(slider);
        box.setBackground(new Background(new BackgroundFill(Color.color(1,0.8,0.7),new CornerRadii(4),Insets.EMPTY)));
        return box;
    }

    private Circuit.SimulationControl controller;

    /**
     * from the simulation library making the component intractable
     * @param Controller this {@link Simulator9000.Circuit.SimulationControl} can be used to restart the simulation, or terminate it
     */
    @Override
    public void MakeInteractable(Circuit.SimulationControl Controller) {
        System.out.println("Made Component Interactable");
        this.controller = Controller;
    }
}