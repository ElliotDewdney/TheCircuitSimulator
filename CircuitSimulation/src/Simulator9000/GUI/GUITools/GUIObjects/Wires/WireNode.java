package GUI.GUITools.GUIObjects.Wires;

import Utils.Coordinate;
import javafx.event.Event;
import javafx.scene.shape.Circle;

import java.util.ArrayList;

/**
 * base class used for the nodes (junctions) that make up the circuit
 * e.g {@link GUI.GUITools.GUIObjects.GUINode}
 */
public abstract class WireNode {

    /**
     * describes if the node is being moved
     */
    public boolean fixed = false;

    protected boolean Simulation;

    protected GUIWire connectedWire;

    ArrayList<WireSegment> ConnectedSegments = new ArrayList<>();

    /**
     * the graphical element of the node
     */
    public Circle node;

    /**
     * used to Initialise the Super after Sub class construction
     * @param node the Graphical element of the node
     * @param manager the active wire manager
     */
    public void Start(Circle node, WireManager manager){
        manager.reportNode(this);
        this.node = node;
        node.setRadius(getRadius());

        //animation for mouse over
        node.setOnMouseEntered(event -> node.setRadius(getRadius()*2));
        node.setOnMouseExited(event -> node.setRadius(getRadius()));

        // making the node able to be wired
        node.setOnMouseClicked(event -> {
            if(Simulation) return;
            switch (event.getButton()) {
                case PRIMARY:
                    manager.nodePressed(this);
                    break;
                case SECONDARY:
                    if(!manager.wiring)delete();
                    break;
            }
            event.consume();
        });

        // so the node will not get dragged
        node.setOnMouseDragged(Event::consume);
        node.setOnMouseReleased(Event::consume);
    }

    /**
     * @return true if a wire is connected
     */
    public boolean isWired(){return connectedWire != null;}

    /**
     * setter for the connectedWire
     * @param connectedWire Connects this wire to the node (only for the node)
     */
    public void setWire(GUIWire connectedWire){this.connectedWire = connectedWire;}

    /**
     * getter for connectedWire
     * @return the wire that is connected to the node
     */
    public GUIWire getWire(){return connectedWire;}

    /**
     * used to remove all the connected wire from the node
     * @return if the action was successful
     */
    public boolean removeWire(){
        if(connectedWire == null) return true;
        GUIWire connectedWireTemp = connectedWire;
        connectedWire = null;
        return connectedWireTemp.deleteNodeFromWire(this);
    }

    /**
     * detects the preferred direction of wire from the node
     * @return the {@link GUIWire.direction} preferred
     */
    public abstract GUIWire.direction wiringDirection();

    /**
     * getter for the radius of the subclass
     * @return positive final int of radius of the circle
     */
    public abstract int getRadius();

    /**
     * used to change the X value of the node
     * @param X the x value to update the node with
     */
    public void updateX(double X) {if(!fixed) node.setCenterX(X);}

    /**
     * used to change the Y value of the node
     * @param Y the y value to update the node with
     */
    public void updateY(double Y) {if(!fixed) node.setCenterY(Y);}

    /**
     * used to get the center position of the node in the canvas
     * @return the center potion relative to the canvas
     */
    public abstract Coordinate getPos();

    /**
     * used to set the state of the simulation
     * @param Simulation flag, true if simulating
     */
    public abstract void SetSimulation(boolean Simulation);

    /**
     * used to when the node needs to be safely deleted
     */
    public abstract void delete();

}
