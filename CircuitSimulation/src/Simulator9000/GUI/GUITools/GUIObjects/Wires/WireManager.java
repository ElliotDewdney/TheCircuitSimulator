package GUI.GUITools.GUIObjects.Wires;

import GUI.Canvas.DragAndDropController;
import GUI.GUITools.GUIObjects.GUIComp;
import Simulator9000.CircuitGraph;
import Utils.Coordinate;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;

import java.util.ArrayList;

/**
 * handles the management of the wire contained in the circuit
 * this class contains all the circuit data, used to construct the circuit graph
 * handles the gesture for wire construction
 */
public class WireManager {
    /**
     * used to Report new nodes
     * @param node the node that was created
     */
    public void reportNode(WireNode node){
        nodes.add(node);
    }

    /**
     * list of all the nodes that are connected
     */
    public ArrayList<WireNode> nodes = new ArrayList<>();

    public Pane Screen;

    /**
     * if the program is actively wiring or not
     */
    public boolean wiring = false;

    private DragAndDropController Controller;

    private ArrayList<GUIWire> wires = new ArrayList<>();

    private GUIWire currentWire;

    /**
     * used to get all the wires that are part of the circuit
     * @return a List of all the simulatable wire in the circuit
     */
    public ArrayList<CircuitGraph.SimWire> getWires(){
        return new ArrayList<>(this.wires);
    }

    /**
     * add a wire to the manager
     * @param wire wire to be added
     */
    public void addWire(GUIWire wire){
        wires.add(wire);
    }

    /**
     * sets the current state of the simulation
     * @param Simulation flag for if simulating
     */
    public void SetSimulation(boolean Simulation){
        wires.forEach(wire -> wire.SetSimulation(Simulation));
    }

    /**
     * used to report nodes that were clicked
     * @param node the node that was clicked
     */
    public void nodePressed(WireNode node){
        if(currentWire == null)newWire(node);
        else finishWire(node);
    }

    /**
     * used to remove component from the wire mangers records
     * @param comp the component that needs to be removed
     */
    public void removeComp(GUIComp comp){
        Controller.RemoveComponent(comp);
    }

    /**
     * used to remove wire from the wire mangers records
     * @param wire the wire that needs to be removed
     */
    public void removeWire(GUIWire wire){
        wires.remove(wire);
    }

    /**
     * used to finish the active wire, ending user interaction
     * @param node the finishing node
     */
    public void finishWire(WireNode node){
        currentWire.addFinishingNode(node);
        Screen.setOnMouseClicked(null);
        Screen.setOnMouseMoved(null);
        currentWire = null;
        wiring = false;
    }

    /**
     * used to finish the active wire, ending user interaction
     */
    public void finishWire(){
        currentWire.finish();
        Screen.setOnMouseClicked(null);
        Screen.setOnMouseMoved(null);
        currentWire = null;
        wiring = false;
    }

    /**
     * used to create a new wire when a node is clicked on
     * @param startingNode the Origin of the wire
     */
    public void newWire(WireNode startingNode){
        wiring = true;
        Screen.getScene().setOnKeyPressed(event -> {
            if(currentWire == null) return;
            switch (event.getCode()){
                case ENTER: case SPACE:
                    finishWire();
                    break;
                case BACK_SPACE: case ESCAPE:
                    GUIWire temp = currentWire;
                    finishWire();
                    temp.deleteAllWires();
                    break;
            }
        });

        if(startingNode.isWired()) {
            currentWire = startingNode.getWire();
            currentWire.StartWiringFromNode(startingNode);
        }
        else {
            currentWire = new GUIWire(this, startingNode);
            wires.add(currentWire);
            startingNode.setWire(currentWire);
        }
        Screen.setOnMouseClicked(event -> {
            if(currentWire == null) return;
            if(event.getButton() == MouseButton.PRIMARY)  currentWire.createNewNode(new Coordinate(event.getX(), event.getY()).putToGrid());
            else finishWire();
        });
        Screen.setOnMouseMoved(event -> {
            if(currentWire == null) return;
            currentWire.updatePos(new Coordinate(event.getX(),event.getY()).putToGrid());
        });
    }

    /**
     * the constructor called by {@link DragAndDropController}
     * @param Screen the Canvas, and where to draw to
     * @param Controller the Active controller
     */
    public WireManager(Pane Screen, DragAndDropController Controller){
        this.Screen = Screen;
        this.Controller = Controller;
    }
}
