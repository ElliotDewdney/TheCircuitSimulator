package GUI.GUITools.GUIObjects.Components;

import GUI.Canvas.InspectionWindow.Editable;
import GUI.Canvas.InspectionWindow.ValueController;
import GUI.GUITools.GUIObjects.GUIComp;
import GUI.GUITools.GUIObjects.GUINode;
import Simulator9000.Circuit;
import Simulator9000.Interactable;
import Utils.ComponentName;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

/**
 * a {@link Component} of type Switch
 * used in the GUI for to talk to the simulation
 */
public class Switch extends Component implements Interactable {

    /**
     * defining the Property of SwitchFlicked being Editable
     */
    @Editable( name = "Default Value :", TYPE = ValueController.Type.BOOLEAN)
    public boolean SwitchFlicked = false;

    /**
     * defining the Property of resistance being Editable
     */
    @Editable( name = "Change internal resistance : ", TYPE = ValueController.Type.DOUBLE)
    public double resistance = 0.001;

    @Override
    public ComponentName name(){return ComponentName.SWITCH;}

    /**
     * defines the Node Placement of the component
     * @return a array of {@link GUI.GUITools.GUIObjects.GUINode.Placement} defining position
     */
    @Override
    public GUINode.Placement[] generateNodes() {
        GUINode.Placement[] nodes = {GUINode.Placement.MID_LEFT, GUINode.Placement.MID_RIGHT};
        return nodes;
    }

    private Simulator9000.components.circuitComponents.Switch aSwitch;

    /**
     * set the simulation properties
     * @return the component holding the simulation maths
     */
    @Override
    public Simulator9000.components.Component getComponent() {
        aSwitch = new Simulator9000.components.circuitComponents.Switch(SwitchFlicked, resistance);
        return aSwitch;
    }

    /**
     * used set the data to be shown by the simulation window
     * @return the data that needs to be displayed
     */
    @Override
    public String getData() {
        return "internal resistance = " + resistance + (SwitchFlicked? ", ON" : ", OFF");
    }

    /**
     * this is used when the simulation is stopped
     * @param comp passed the component that handles the GUI parts
     */
    @Override
    public void resetSimulation(GUIComp comp) {
        comp.getGUIObject().Image.setEffect(null);
        comp.getGUIObject().SetAddition(null);
    }

    /**
     *  used to define the Extra details that are show when the simulation details are shown
     * @param comp the component that handles the GUI elements
     * @return a GUI elements that contains the extra details
     */
    @Override
    public Node Extras(GUIComp comp){
        comp.getGUIObject().SetAddition(SwitchFlicked? "CLOSED" : null);
        VBox box = new VBox();
        CheckBox Switch = new CheckBox("Toggle Switch");
        Switch.setSelected(SwitchFlicked);
        Switch.setOnAction(event -> {
            SwitchFlicked = Switch.isSelected();
            aSwitch.setOpen(SwitchFlicked);
            controller.WakeSimulation();
        });
        box.getChildren().add(Switch);
        box.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID, new CornerRadii(4), BorderWidths.DEFAULT)));
        box.setPadding(new Insets(20, 0 ,20 ,10));
        box.setBackground(new Background(new BackgroundFill(Color.color(1,1,0.8),new CornerRadii(4),Insets.EMPTY)));
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
