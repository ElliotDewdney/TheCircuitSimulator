package GUI.GUITools.GUIObjects;
import GUI.GUITools.GUIObjects.Components.Component;
import GUI.GUITools.GUIObjects.Components.ComponentFactory.Constructinator;
import GUI.GUITools.GUIObjects.Wires.WireManager;
import Simulator9000.CircuitGraph;
import Simulator9000.Interactable;
import Utils.ComponentName;
import Utils.Coordinate;
import javafx.scene.Node;
import javafx.scene.layout.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * used to hold the GUI element and interface with the simulation for components
 * for the gui part see {@link GUIObject}
 * for the simulation part see {@link Component}
 */
public class GUIComp extends CircuitGraph.SimComp {

    /**
     * states if the component can be moved
     */
    public boolean Fixed = false;

    /**
     * used to set the state of the simulation
     * @param Simulation flag, true if simulating
     */
    public void SetSimulation(boolean Simulation){
        Image.setSimulation(Simulation);
        this.Fixed = Simulation;
        nodes.forEach(node -> node.SetSimulation(Simulation));
        if(Simulation){
            CompObject = comp.getComponent();
            nodes.forEach(node -> node.SetSimulation(true));
        }else{
            comp.resetSimulation(this);
        }
    }

    /**
     * used to update the value of fixed
     * @param fixed holds the new value of fixed
     */
    public void setFixed(boolean fixed){
        Fixed = fixed;
    }

    /**
     * used to get the details of the simulation
     * @return GUI element to be displayed
     */
    public Node ShowData(){
        return comp.getDisplayedData(this);
    }

    private ComponentName name;

    private GUIObject Image;

    public Component comp;

    public Pane pane;

    public WireManager wireManager;

    private GUINode.Orientation rotation = GUINode.Orientation.ANGLE_0;

    private ArrayList<GUINode> nodes = new ArrayList<>();
    public ArrayList<GUINode> getNodes(){return nodes;}

    public GUIObject getGUIObject(){return Image;}

    /**
     * constructor used to build the GUI component
     * @param name the name of the component that is to be generated
     * @param X the X value for the top corner of the component
     * @param Y the Y value for the top corner of the component
     * @param pane the Canvas where the component is to be added
     * @param manager the active wire manager for this component
     */
    public GUIComp(ComponentName name, double X, double Y, Pane pane, WireManager manager){
        this.name = name;
        this.pane =  pane;
        this.wireManager = manager;

        // this sets the value of comp to the constructed component
        comp = Constructinator.getComponent(name);
        Image = GUIObject.getDraggableGUIObject(name, pane,this, new Coordinate(X,Y));

        // constructs all the nodes
        int pin = 0;
        for(GUINode.Placement node : comp.generateNodes()){
            nodes.add(new GUINode(node, this, pin++));
        }
        pane.getChildren().add(Image.Image);
    }

    /**
     * ran if the component has been dragged, or moved
     */
    public void moved(){
        nodes.forEach(GUINode::removeWire);
        System.out.println("move detected wires removed");
    }

    /**
     * used to set the rotation of the component, used internally
     */
    private void setRotationImage() {
        Image.SecondaryPane.setRotate(rotation.getRotationValue());
        nodes.forEach(node -> node.SetRotation(rotation));
        moved();
    }

    /**
     * used to update the rotation of the component, ran by {@link GUI.Canvas.InspectionWindow.InspectionController}
     */
    public void RotateLeftComp() {
        // gets the new rotation
        rotation = GUINode.Orientation.values()[(rotation.ordinal() - 1 + GUINode.Orientation.values().length) % GUINode.Orientation.values().length];
        System.out.println("Rotating component : " + name + "\n\rto an angle of : " + rotation);
        setRotationImage();
    }

    /**
     * used to update the rotation of the component, ran by {@link GUI.Canvas.InspectionWindow.InspectionController}
     */
    public void RotateRightComp() {
        // gets the new rotation
        rotation = GUINode.Orientation.values()[(rotation.ordinal() + 1) % GUINode.Orientation.values().length];
        System.out.println("Rotating component : " + name + "\n\rto an angle of : " + rotation);
        setRotationImage();
    }

    /**
     * used to safely remove the component from the circuit
     */
    public void remove(){
        wireManager.removeComp(this);
        nodes.forEach(node -> wireManager.nodes.remove(node));
    }

    /**
     * used to get the name of the component
     * @return gets the Name of the component
     */
    @Override
    public String toString(){
        return name.name();
    }

    /**
     * method used by the simulation, used to read the circuit graph into the simulation code
     * @return a list of all the nodes that are connected, index by pin
     */
    @Override
    public Collection<CircuitGraph.SimNode> getConnectedSimNodes() {
        ArrayList<CircuitGraph.SimNode> nodeArrayList = new ArrayList<>();
        nodes.forEach((GUINode node) -> nodeArrayList.add(node.SimNode));
        return nodeArrayList;
    }

    /**
     * used by the simulation code to make the component interactable if needed
     * @return retruns component, but will error if it is not Interactable
     */
    @Override
    public Interactable getIntractable() {
        return (Interactable) comp;
    }

    private Simulator9000.components.Component CompObject;

    /**
     * used by the simulation to get the Mathematical model for the simulation
     * @return The mathematical model for simulation
     */
    @Override
    public Simulator9000.components.Component getComponent() {
        if(CompObject == null) CompObject = comp.getComponent();
        return CompObject;
    }
}
