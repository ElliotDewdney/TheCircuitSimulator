package GUI.GUITools.GUIObjects.Components;

import GUI.GUITools.GUIObjects.GUIComp;
import GUI.GUITools.GUIObjects.GUINode;
import GUI.GUITools.SettingPane.SettingsController;
import Utils.ComponentName;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Base class for the GUI parts of the simulation components
 * e.g used in {@link Bulb}
 */
public abstract class Component {

    /**
     * gets the name of the component
     * @return name of component
     */
    public abstract ComponentName name();

    /**
     * used to get the position of all of the pins of the component
     * @return a array of {@link GUI.GUITools.GUIObjects.GUINode.Placement} defining position
     */
    public abstract GUINode.Placement[] generateNodes();

    /**
     * gets/generates the simulation component in the sub-class
     * @return holds all the simulation maths
     */
    public abstract Simulator9000.components.Component getComponent();

    /**
     * used to format details of the simulation from the sub-class
     * @return formatted simulation data
     */
    abstract String getData();

    /**
     * this is run when the simulation is terminated
     * @param comp the gui element attached to this component
     */
    public abstract void resetSimulation(GUIComp comp);

    /**
     * this returns a GUI Element that contains simulation details about the component
     * @param comp the gui element attached to this component
     * @return GUI element containing details of the simulation
     */
    public Node getDisplayedData(GUIComp comp){

        // the title element
        Label Name = new Label(name().NiceName);
        Name.setFont(new Font(17));

        // simulation details
        Label data = new Label(" ( " + getData() + " ) ");
        Label Volts = new Label(" (Volts) => " + Math.round(Math.abs(comp.Voltages()[0]-comp.Voltages()[1])*1000)/1000f);
        Label Amps = new Label(" (Amps) => " + Math.round(Math.abs(comp.getNodes().get(0).SimNode.getCurrentThroughNode()*1000))/1000f);
        Border border = new Border(new BorderStroke(Color.RED, BorderStrokeStyle.DASHED, new CornerRadii(20), BorderWidths.DEFAULT));

        VBox shownData;
        if(SettingsController.getSettingsData().Reveal.getData()){
            shownData = new VBox(data,Volts,Amps);
        }else{
            // creates the Hidden Statement and hides the data
            Label title = new Label("Click for data ... ");
            title.setTextFill(Color.RED);
            title.setFont(new Font(15));
            shownData = new VBox(title);
            shownData.setPadding(new Insets(10,10,10,10));
            shownData.setBorder(border);
            shownData.setOnMouseClicked(event -> {
                shownData.setPadding( new Insets(0,0,0,0));
                shownData.setBorder(null);
                shownData.getChildren().remove(title);
                shownData.getChildren().addAll(data,Volts,Amps);
            });
        }

        // add GUI effects to the elements
        VBox box = new VBox(10,Name, shownData);
        box.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(6), BorderWidths.DEFAULT)));
        box.setPadding(new Insets(10, 10 ,10 ,10));
        box.setOnMouseEntered(event -> comp.getGUIObject().SecondaryPane.setBorder(border));
        box.setOnMouseExited(event -> comp.getGUIObject().SecondaryPane.setBorder(null));
        Node temp = Extras(comp);
        if(temp != null) box.getChildren().add(temp);
        box.setBackground(new Background(new BackgroundFill(Color.color(0.9,0.95,1),new CornerRadii(6), Insets.EMPTY)));
        return box;
    }

    /**
     * used to add optional extras in the subClasses
     * @param comp the gui element attached to this component
     * @return the extra GUI elements that are contained in the sub-class
     */
    public Node Extras(GUIComp comp){return null;}

}