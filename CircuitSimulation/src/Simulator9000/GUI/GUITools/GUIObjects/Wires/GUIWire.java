package GUI.GUITools.GUIObjects.Wires;

import GUI.GUITools.GUIObjects.GUINode;
import GUI.GUITools.SettingPane.SettingsController;
import Simulator9000.CircuitGraph;
import Utils.Coordinate;
import javafx.application.Platform;
import javafx.scene.effect.Shadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * used as the wire GUI element providing a link to the Simulation elements
 * this create and handles GUI Inputs + interfacing with the SimWire class
 */
public class GUIWire extends CircuitGraph.SimWire {

    private ArrayList<WireSegment> wireSegments =  new ArrayList<>();
    private ArrayList<WireNode> Nodes = new ArrayList<>();
    private WireSegment[] currentWires = new WireSegment[2];
    private WireManager manager;

    /**
     * this returns all the Terminal nodes that are part of this wire
     * @return A list that contains all the {@link Simulator9000.CircuitGraph.SimWire} that are connected
     */
    @Override
    public Collection<CircuitGraph.SimNode> getConnectedSimNodes() {
        ArrayList<CircuitGraph.SimNode> nodeArrayList = new ArrayList<>();
        Nodes.forEach(node -> {
            if(node instanceof GUINode) nodeArrayList.add(((GUINode)node).SimNode);
        });
        return nodeArrayList;
    }

    /**
     * used to set the state of the Simulation so that the component makes the graphical changes required
     * @param Simulation flag, true if simulating the circuit
     */
    public void SetSimulation(boolean Simulation){
        wireSegments.forEach(wire -> wire.setSimulation(Simulation));
        Nodes.forEach(node -> node.SetSimulation(Simulation));
    }

    /**
     * used to indicate to the user that there is a problem with this wire
     * ran by the {@link CircuitGraph} when a Error with it is detected
     */
    public void DisplayERROR(){
        wireSegments.forEach(WireSegment::Error);
    }

    /**
     * states the direction of the wire :
     * Vertical - wire Can only change the value of the Y
     * Horizontal - wire can only change the value of the X
     */
    public enum direction{
        Vertical,Horizontal
    }

    /**
     * this constructor is for creating a single connected GUI wire that is active
     * @param manager the manager that is active for this wire
     * @param node the node that the wire is going to origin from
     */
    public GUIWire(WireManager manager, WireNode node){
        this.manager = manager;
        Nodes.add(node);
        StartWiringFromNode(node);
    }

    /**
     * this constructs a wire that contains all the node that are passed to it and is inactive
     * @param manager the manager that is active for this wire
     * @param Nodes a list of nodes that the wire contains to start with
     */
    public GUIWire(WireManager manager, Collection<WireNode> Nodes){
        manager.addWire(this);
        this.manager = manager;
        for(WireNode node : Nodes){
            this.Nodes.add(node);
            node.setWire(this);
            node.ConnectedSegments.forEach(wireSegment -> {if(!wireSegments.contains(wireSegment))wireSegments.add(wireSegment);});
        }
    }

    /**
     * this makes this wire a active wire,
     * @param node the node to start the wiring from
     */
    public void StartWiringFromNode(WireNode node){
        currentWires[0] = new WireSegment(manager.Screen, node, node.wiringDirection());
    }

    /**
     * Calling this method will clear all the gui elements that are part of this wire
     */
    public void deleteAllWires(){
        ArrayList<WireNode> NodesTemp = (ArrayList<WireNode>) Nodes.clone();
        NodesTemp.forEach(WireNode::delete);
    }

    /**
     * this is run when one node from a wire need to be deleted
     * @param node this is the node to be deleted
     * @return if the delete was successful
     */
    public boolean deleteNodeFromWire(WireNode node){
        for(WireNode tempNode : Nodes) if(!tempNode.fixed)return false;
        node.setWire(null);
        Nodes.remove(node);

        // removes the directly connected to the node
        wireSegments.forEach(wireSegment -> {
            if(wireSegment.origin == node || wireSegment.end == node) wireSegment.delete();
        });

        // gets the new set of wire meshes
        ArrayList<ArrayList<WireNode>> temp = wireConnectionChecker();
        System.out.println("Number of new wires = " + temp.size());

        // creates a new wire for each mesh
        temp.forEach(newNodes -> {
            if(newNodes.size() > 1) {
                new GUIWire(manager, newNodes);
            }else{
                System.out.println("part Deleted");
                newNodes.get(0).setWire(null);
                newNodes.get(0).delete();
            }
        });

        // deletes itself
        manager.removeWire(this);
        deleteAllWires();
        return true;
    }

    /**
     * this gets the current state of the wire mesh as a list of new meshes
     * used to split the current wire graph object into multiple
     * @return a list of the node as a collection
     */
    private ArrayList<ArrayList<WireNode>> wireConnectionChecker(){
        ArrayList<ArrayList<WireNode>> nodeSubGraphs =  new ArrayList<>();

        while(!Nodes.isEmpty()) {
            ArrayList<WireNode> newNodes = new Circuit().NodesConnectedTo(Nodes.get(0));
            Nodes.removeAll(newNodes);
            nodeSubGraphs.add(newNodes);
        }
        return nodeSubGraphs;
    }

    /**
     * controller class for the recursive graph exploring
     */
    private static class Circuit{

        // list like data structure for the Nodes that have been visited
        private HashSet<WireNode> VisitedNodes = new HashSet<>();

        /**
         * this returns a list of all the nodes that are connected, via wires, to the original node
         * @param node the original node to start the recursion with
         * @return a list of all the connected nodes
         */
        public ArrayList<WireNode> NodesConnectedTo(WireNode node){
            ArrayList<WireNode> newNode = new ArrayList<>();
            newNode.add(node);
            VisitedNodes.add(node);
            node.ConnectedSegments.forEach( Wire -> {
                WireNode tempNode = Wire.origin == node ? Wire.end : Wire.origin;
                if(!VisitedNodes.contains(tempNode))newNode.addAll(NodesConnectedTo(tempNode));
            });
            return newNode;
        }

    }

    /**
     * used to stop the active wiring of the components,
     * will reset the wire to passive
     */
    public void finish(){
        updatePos(currentWires[0].getEndPos());
        currentWires[0].delete();
        Nodes.forEach(node->node.fixed = true);
        if(currentWires[1] != null)wireSegments.add(currentWires[1]);
    }

    /**
     * used to set the current mouse position, used to update the Graphical position
     * ran by {@link WireManager} when in active wire mode
     * @param pos the mouses position on the canvas
     */
    public void updatePos(Coordinate pos){
        if(currentWires[1] != null) currentWires[1].updatePos(pos);
        currentWires[0].updatePos(pos);
    }

    /**
     * this is used to create a new node at the mouses coordinates
     * this is ran in {@link WireManager} when it detects a click
     * @param pos this will be the final position of the mouse
     */
    public void createNewNode(Coordinate pos){
        pos = currentWires[0].getEndPos();
        updatePos(pos);
        if(currentWires[1] != null)wireSegments.add(currentWires[1]);
        currentWires[1] = currentWires[0];
        currentWires[1].origin.fixed = true;
        WireNode node = new WiringNode(manager, pos);
        node.setWire(this);
        currentWires[0].setEnd(node);
        node.setWire(this);
        Nodes.add(node);
        currentWires[0] = new WireSegment(manager.Screen, node, (currentWires[1].direction == direction.Horizontal)? direction.Vertical : direction.Horizontal);
    }

    /**
     * this will finnish the wire, if the user clicks on a node to finnish
     * @param node this is the node used to finnish the wire
     */
    public void addFinishingNode(WireNode node){
        updatePos(node.getPos());
        Nodes.add(node);
        currentWires[0].origin.fixed = true;
        currentWires[0].setEnd(node);
        if(currentWires[1] != null) wireSegments.add(currentWires[1]);
        wireSegments.add(currentWires[0]);
        if(node.isWired() && !(node.getWire() == this))node.getWire().mergeWith(this);
        else node.setWire(this);
    }

    private void mergeWith(GUIWire wire){
        wire.Nodes.forEach(node -> node.setWire(this));
        Nodes.addAll(wire.Nodes);
        wireSegments.addAll(wire.wireSegments);
        manager.removeWire(wire);
    }


}

/**
 * a container class holding details of the parts of the wire
 * used internally by {@link GUIWire}  only
 */
class WireSegment{
    private Line wire = new Line();
    WireNode origin;
    WireNode end;
    GUIWire.direction direction;
    boolean simulation;

    /**
     * used to set the segments end
     * @param node node to become the new End
     */
    public void setEnd(WireNode node) {
        node.ConnectedSegments.add(this);
        this.end = node;
    }
    /**
     * used to set the segments Origin
     * @param node node to become the new Origin
     */
    public void setOrigin(WireNode node) {
        node.ConnectedSegments.add(this);
        this.origin = node;
    }

    /**
     * used to notify the user is the wire placement if invalid,
     * animates 3 orange flashes
     */
    public void Error(){
        new Thread(() -> {
            try {
                for(int i = 0; i < 3; i++) {
                    Platform.runLater(() -> wire.setEffect(new Shadow(4, Color.ORANGE)));
                    Thread.sleep(500);
                    Platform.runLater(() -> wire.setEffect(null));
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * this sets the state of the simulation
     * @param Simulation flag for if Simulating
     */
    public void setSimulation(boolean Simulation){
        simulation = Simulation;
        if(Simulation){
            wire.setStroke(Color.DARKGREY);
        }else{
            wire.setStroke(SettingsController.getSettingsData().WireColour.getData());
        }
    }

    /**
     * the constructor for the wire segment, used to build wires
     * @param massivePane the canvas where the lines are drawn
     * @param origin the node where the wire should start at
     * @param direction the direction, see {@link GUIWire.direction}
     */
    public WireSegment(Pane massivePane, WireNode origin, GUIWire.direction direction){
        setOrigin(origin);
        System.out.println(direction);
        this.direction = direction;

        // Using the custom Settings menu interfaces
        SettingsController.SettingsType.ColorSetting Colour = SettingsController.getSettingsData().WireColour;
        SettingsController.SettingsType.IntegerSetting thickness = SettingsController.getSettingsData().WireThiccness;
        Colour.addDependencies(data -> { if(!simulation) wire.setStroke((Color) data); });
        thickness.addDependencies(data -> wire.setStrokeWidth((int)data));
        massivePane.getChildren().add(wire);

        //graphical changes
        wire.toBack();
        wire.setEndX(origin.getPos().x);
        wire.setEndY(origin.getPos().y);
        wire.setStartX(origin.getPos().x);
        wire.setStartY(origin.getPos().y);
    }

    /**
     * used to set new coordinates of the wire, using only valid positions
     * @param pos the new coordinates of the wire
     */
    public void updatePos(Coordinate pos){
        if(end == null) {
            if (direction == GUIWire.direction.Vertical) {

                // changes the Y value of the end only
                origin.updateX(pos.x);
                wire.setStartY(origin.getPos().y);
                wire.setStartX(origin.getPos().x);
                wire.setEndY(pos.y);
                wire.setEndX(origin.getPos().x);
            } else {

                // changes the X value of the end only
                origin.updateY(pos.y);
                wire.setStartY(origin.getPos().y);
                wire.setStartX(origin.getPos().x);
                wire.setEndX(pos.x);
                wire.setEndY(origin.getPos().y);
            }
        }else{

            // changes no value
            wire.setEndX(end.getPos().x);
            wire.setEndY(end.getPos().y);
            wire.setStartY(origin.getPos().y);
            wire.setStartX(origin.getPos().x);
        }

    }

    /**
     * gets the end position of the wire
     * @return a Coordinate containing the end position of the wire
     */
    public Coordinate getEndPos(){
        return new Coordinate(wire.getEndX(),wire.getEndY());
    }

    /**
     * used to safely delete the wire
     */
    public void delete(){
        wire.setVisible(false);
        if(end != null) end.ConnectedSegments.remove(this);
        origin.ConnectedSegments.remove(this);
    }

}

/**
 * this is a instance of {@link WireNode} for internal use by the {@link GUIWire}
 * movable node that are junctions between the wires
 */
class WiringNode extends WireNode{
    public Circle node = new Circle(getRadius(), Color.DARKGRAY);

    /**
     * creates a new Wire node
     * @param manager using this manager
     * @param pos and this center position
     */
    public WiringNode(WireManager manager, Coordinate pos){
        node.setCenterX(pos.x);
        node.setCenterY(pos.y);
        manager.Screen.getChildren().add(node);
        Start(node, manager);
    }

    /**
     * getter for preferred direction of wires connected
     * @return gets the preferred direction of connected wires
     */
    @Override
    public GUIWire.direction wiringDirection() {
        return GUIWire.direction.Vertical;
    }

    /**
     * used by the super to get the radius for animation
     * @return the default radius of the circle
     */
    @Override
    public int getRadius() {return 5;}

    /**
     * used to get the current position of the node
     * @return the nodes Coordinate
     */
    @Override
    public Coordinate getPos() {
        return new Coordinate(node.getCenterX(), node.getCenterY());
    }

    /**
     * this sets the current state of the simulation
     * @param Simulation flag for state of simulation
     */
    @Override
    public void SetSimulation(boolean Simulation) {
        this.Simulation = Simulation;
        node.setVisible(!Simulation);
    }

    /**
     * used to safely delete the node
     */
    @Override
    public void delete() {
        if(removeWire()) node.setVisible(false);
    }
}