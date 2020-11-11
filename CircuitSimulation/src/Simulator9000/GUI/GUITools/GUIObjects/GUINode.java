package GUI.GUITools.GUIObjects;

import GUI.GUITools.GUIObjects.Wires.GUIWire;
import GUI.GUITools.GUIObjects.Wires.WireNode;
import Simulator9000.CircuitGraph;
import Utils.Coordinate;
import javafx.application.Platform;
import javafx.scene.effect.Shadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.HashMap;

/**
 * used to hold the details of the Node part of the circuit
 * interfaces with the simulation
 */
public class GUINode extends WireNode {

    /**
     * used to hold simulation independent code for the GUINode
     */
    public class Node extends CircuitGraph.SimNode{

        int pinNumber;

        /**
         * gets the ordinal value for the node
         * @return the numerical pin value
         */
        @Override
        public int getPinNumber(){
            return pinNumber;
        }

        /**
         * ran when a Error happens in the simulation, used to display the cause to the user
         */
        public void DisplayERROR(){
            new Thread(() -> {
                try {
                    for(int i = 0; i < 3; i++) {
                        Platform.runLater(() -> {
                            node.setRadius(8);
                            node.setEffect(new Shadow(9, Color.ORANGE));
                        });
                        Thread.sleep(500);
                        Platform.runLater(() -> {
                            node.setRadius(6);
                            node.setEffect(null);
                        });
                        Thread.sleep(500);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        /**
         * used by the simulation to get the component that is connected to the node
         * @return the component that is connected to the node
         */
        @Override
        public CircuitGraph.SimComp getConnectedComp() {
            return comp;
        }

        /**
         * this used to show debug and errors from the simulation
         * @return a debug string, giving the position of the node
         */
        @Override
        public String toString(){ return "Node connected to " + comp.toString() + " ( ID = " + comp.hashCode() +  " ) on pin " + SimNode.pinNumber;
        }

        /**
         * used to get the connected wire, ran by the simulation
         * @return gives the wire that is connected
         */
        @Override
        public CircuitGraph.SimWire getConnectedWire() {
            return connectedWire;
        }
    }

    /**
     * the Simulation interface for the node
     */
    public Node SimNode = new Node();

    /**
     * used to get the radius of the node
     * @return will return fixed value of 6
     */
    public int getRadius(){return 6;}

    /**
     * used to define that the relative position of the node in the component
     */
    public enum Placement{
        MID_LEFT,MID_RIGHT,MID_TOP,MID_BOTTOM,TOP_RIGHT,TOP_LEFT,BOTTOM_RIGHT,BOTTOM_LEFT
    }

    /**
     * this defines the rotation of the component, and then for a Matix-Transformation of the nodes
     */
    public enum Orientation {
        ANGLE_0(0),ANGLE_90(90),ANGLE_180(180),ANGLE_270(270);

        /**
         * used to construct rotations Transformations
         * @param rotationValue the value of the transformation
         */
        Orientation(int rotationValue){
            this.rotationValue = rotationValue;
        }

        int rotationValue;

        /**
         * returns the absolute angle of the rotation, (in degrees)
         * @return angel in degrees
         */
        public int getRotationValue() {
            return rotationValue;
        }
    }

    /**
     * contains the coordinate mapping of the defined positions
     */
    private static HashMap<Placement, Coordinate> conversion = new HashMap<>();

    static{
        conversion.put(Placement.MID_LEFT, new Coordinate(0,0.5));
        conversion.put(Placement.MID_RIGHT, new Coordinate(1,0.5));
        conversion.put(Placement.MID_TOP, new Coordinate(0.5,0));
        conversion.put(Placement.MID_BOTTOM, new Coordinate(0.5,1));
        conversion.put(Placement.TOP_RIGHT, new Coordinate(1,0.25));
        conversion.put(Placement.TOP_LEFT, new Coordinate(0,0.25));
        conversion.put(Placement.BOTTOM_RIGHT, new Coordinate(1,0.75));
        conversion.put(Placement.BOTTOM_LEFT, new Coordinate(0,0.75));
    }

    private Circle node;

    private Coordinate pos;

    private Coordinate currentPos;

    private Coordinate dimensions;

    private GUIComp comp;

    private Orientation rotation;

    /**
     * used to set the rotation of the component and therefore apply the required transformation
     * @param orientation this is the new absolute angle of the rotation
     */
    public void SetRotation(Orientation orientation){
        rotation = orientation;
        currentPos = getPos(orientation);
        node.setCenterX(currentPos.x * dimensions.x);
        node.setCenterY(currentPos.y * dimensions.y);
    }

    /**
     * used to get the position of the node inside of the component
     * @param orientation the current orientation
     * @return the position of the node
     */
    private Coordinate getPos(Orientation orientation){
        switch (orientation) {
            case ANGLE_0 : return new Coordinate(pos.x, pos.y);
            case ANGLE_90 : return new Coordinate((1 - pos.y),pos.x);
            case ANGLE_180 : return new Coordinate((1 - pos.x),(1 - pos.y));
            case ANGLE_270 : return new Coordinate((1 - pos.y),(1 - pos.x));
            default: return  pos;
        } 
    }

    /**
     * used to graphical element of the node
     * @return a circle displayed as the node
     */
    public Circle getNode(){return node;}

    /**
     * used to convert relative positions of the node into absolute positions in Canvas space
     * @param pos the position to be converted
     * @return the absolute position of the node
     */
    private Coordinate convertinator(Coordinate pos){
        pos.x += comp.getGUIObject().Image.getLayoutX();
        pos.y += comp.getGUIObject().Image.getLayoutY();
        return pos;
    }

    /**
     * this is used to initialise the node, ran internally by the constructors
     * @param pos the position inside the comp of the node
     * @param comp the component that the node belongs to
     * @param pin the ordinal value of the pin
     */
    private void NodeSetUp(Coordinate pos, GUIComp comp, int pin){
        fixed = true;
        this.comp = comp;
        this.pos = pos;
        currentPos = pos;
        node = new Circle(6,Color.valueOf("ff575a"));
        comp.getGUIObject().Image.getChildren().add(node);
        dimensions = new Coordinate(comp.getGUIObject().RawImage.getImage().getWidth(),comp.getGUIObject().RawImage.getImage().getWidth());
        SetRotation(Orientation.ANGLE_0);
        Start(node, comp.wireManager);
        SimNode.pinNumber = pin;
    }

    /**
     * this is the preferred direction for a connected wire
     * @return the direction of the wire
     */
    @Override
    public GUIWire.direction wiringDirection() {
        return (currentPos.y == 0 || currentPos.y == 1)? GUIWire.direction.Vertical: GUIWire.direction.Horizontal;
    }

    /**
     * will be thrown away as the node is fixed, from wiring
     * @param X the x value to update the node with
     */
    @Override
    public void updateX(double X){}

    /**
     * will be thrown away as the node is fixed, from wiring
     * @param Y the y value to update the node with
     */
    @Override
    public void updateY(double Y){}

    /**
     * used to get the position of the node from the perspective of the Canvas
     * @return the absolute position of the node
     */
    @Override
    public Coordinate getPos(){
        return convertinator(new Coordinate(node.getCenterX(), node.getCenterY()));
    }

    /**
     * used to set the state of the simulation
     * @param Simulation flag, true if simulating
     */
    @Override
    public void SetSimulation(boolean Simulation) {
        this.Simulation = Simulation;
        node.setVisible(!Simulation);
    }

    /**
     * used to when the node needs to be safely deleted
     */
    @Override
    public void delete(){
        removeWire();
    }

    /**
     * used to construct the node using a internal coordinate
     * @param pos the position inside the comp of the node
     * @param comp the component that the node belongs to
     * @param pin the ordinal value of the pin
     */
    public GUINode(Coordinate pos, GUIComp comp, int pin){ NodeSetUp(pos, comp, pin); }

    /**
     * used with the {@link Placement} to define position
     * @param pos the position inside the comp of the node
     * @param comp the component that the node belongs to
     * @param pin the ordinal value of the pin
     */
    public GUINode(Placement pos, GUIComp comp, int pin){ this(conversion.get(pos), comp, pin); }
}
