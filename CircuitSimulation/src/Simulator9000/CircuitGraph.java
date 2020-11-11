package Simulator9000;

import Simulator9000.components.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

/**
 * the container class holding all the circuit data
 */
public class CircuitGraph {

    // this is the data of the circuit for the simulation
    ArrayList< ? extends SimWire > wires;
    ArrayList< ? extends SimComp > components;

    /**
     * this defines the properties and methods of wires passed to the simulation
     */
    public static abstract class SimWire {

        /**
         * used to get all the {@link SimNode} that are connected
         * @return a list of all nodes that are connected
         */
        public abstract Collection<SimNode> getConnectedSimNodes();

        /**
         * contains the current absolute voltage of the wire
         */
        public double voltage = 0;

        /**
         * used to inform GUI's that implement the cause of the error
         * ran if that wire is cause of invalid circuit
         */
        public abstract void DisplayERROR();

        /**
         * used to get the difference between the current that is flowing into and out of the wire
         * @return positive or negative current total
         */
        public double getCurrentDeficit() {
            double CurrentDeficit = 0;
            for(SimNode node : getConnectedSimNodes()) CurrentDeficit += node.getCurrentThroughNode();
            return CurrentDeficit;
        }
    }

    /**
     * this defines the properties and methods of nodes passed to the simulation
     */
    public static abstract class SimNode {
        /**
         * getter for the pin number
         * @return returns a unique pin for the node, from the connected component
         */
        public abstract int getPinNumber();

        /**
         * used to get the {@link SimComp} that is connected to the node
         * @return the component that is connected
         */
        public abstract SimComp getConnectedComp();

        /**
         * used to inform GUI's that implement the cause of the error
         * ran if that wire is cause of invalid circuit
         */
        public abstract void DisplayERROR();

        /**
         * used to get the {@link SimWire} that is connected to the node
         * @return the wire that is connected
         */
        public abstract SimWire getConnectedWire();

        /**
         * getter for the voltage
         * @return the voltage of the wire that is connected
         */
        public double getVoltage() {
            return getConnectedWire().voltage;
        }

        /**
         * used by {@link SimWire} for getting its CurrentDeficit
         * @return the current that flows through the node
         */
        public double getCurrentThroughNode() {
            return getConnectedComp().getCurrentOnPin(getPinNumber());
        }
    }

    /**
     * defines the properties and methods of components passed to the simulation
     */
    public static abstract class SimComp {

        /**
         * used to get all the {@link SimNode} that are connected
         * @return a list of all nodes that are connected
         */
        public abstract Collection<SimNode> getConnectedSimNodes();

        /**
         * implemented if interactive components are used
         * @return will error if not interactable, else returns a {@link Interactable} component
         */
        public Interactable getIntractable () throws ClassCastException{
            throw new ClassCastException();
        }

        /**
         * get a array for the voltage levels on either side of the cell, used in {@link #getCurrentOnPin(int)}
         * @return all the voltages of the component, using their pins index for reference
         */
        public double[] Voltages(){
            Collection<SimNode> Nodes = getConnectedSimNodes();
            double[] Voltages = new double[Nodes.size()];
            for(SimNode node : Nodes) {
                Voltages[node.getPinNumber()] = node.getVoltage();
            }
            return Voltages;
        }

        /**
         * used to get the simulation characteristics of the components
         * @return using the {@link Component} to hold simulation maths
         */
        public abstract Component getComponent();

        /**
         * used to get the current that flows into/out of the component
         * @param pin the pin that the simulation of the component is targeting
         * @return the current that the pin has flowing from it
         */
        public double getCurrentOnPin(int pin){
            System.out.println("Getting Current On pin " + pin + " of component " + this + " - " + this.hashCode() + "\nWith voltages :");
            double[] volts = Voltages();
            // debugging
            for(double i : volts)System.out.println(" - " + i);

            double current = getComponent().currentOnPin()[pin].GetCurrent(volts);
            System.out.println("With result = " + current);
            return current;
        }
    }

    /**
     * this is the constructor and sets the circuit data
     * @param Wires holds a list of types that extend the {@link SimWire}, used for the simulation
     * @param Components holds a list of types that extend the {@link SimComp}, used for the simulation
     */
    public CircuitGraph(ArrayList< ? extends SimWire > Wires, ArrayList<? extends SimComp> Components) {
        this.wires = Wires;
        this.components = Components;
    }

    /**
     * used for checking if the circuit loaded into the circuit graph object is a valid graph
     * @return if invalid returns {@link CircuitERROR}
     */
    public CircuitERROR checkValid(){
        return new ValidityChecker().ErrorChecker();
    }

    /**
     * a class that is used to return data about the type of ERROR
     */
    public static class CircuitERROR{

        /**
         * detailed description of the error that has occurred
         */
        public String ERRORTYPE = "Unknown ERROR";

        /**
         * flag for if the circuit has came across an Error
         */
        public boolean ERRORED;

        /**
         * constructor for the Error
         * @param ERRORED flag for if the circuit has came across an Error
         * @param ERRORTYPE detailed description of the error that has occurred
         */
        public CircuitERROR(boolean ERRORED, String ERRORTYPE){this(ERRORED); this.ERRORTYPE = ERRORTYPE;}

        /**
         * constructor for the Error
         * @param ERRORED flag for if the circuit has came across an Error
         */
        CircuitERROR(boolean ERRORED){this.ERRORED = ERRORED;}
    }

    /**
     * this is a throw away class that helps to check if the circuit is valid
     * used by {@link #checkValid()}
     */
    private class ValidityChecker {

        /**
         * contains the Currently checked circuit components in list like data structures
         */
        HashSet<SimWire> CheckedWires = new HashSet<>();
        HashSet<SimComp> CheckedComps = new HashSet<>();

        // this runs the circuit checking algorithm
        CircuitERROR ErrorChecker(){
            try {
                if (wires.size() == 0) return new CircuitERROR(true, "ERROR- No wires in Circuit");
                CircuitERROR error = RecursivelyCheckCircuit(wires.get(0));
                if (!error.ERRORED) {
                    for (SimComp comp : components)
                        if (!CheckedComps.contains(comp)) {
                            comp.getConnectedSimNodes().forEach(SimNode::DisplayERROR);
                            return new CircuitERROR(true, "ERROR- Node not connected to circuit");
                        }
                    for (SimWire wire : wires)
                        if (!CheckedWires.contains(wire)) {
                            wire.DisplayERROR();
                            return new CircuitERROR(true, "ERROR- Wire not connected to circuit");
                        }
                }
                return error;
            }catch (Exception e){
                return new CircuitERROR(true);
            }
        }

        // this is used to get error due to incorrect meshing
        private CircuitERROR RecursivelyCheckCircuit(SimWire wire){
            // debugging
            System.out.println("Checking Wire Connected to : ");
            wire.getConnectedSimNodes().forEach(node -> System.out.println("    - " + node.toString()));

            CheckedWires.add(wire);
            if(wire.getConnectedSimNodes().isEmpty()){
                System.out.println("ERROR- Wire with no nodes");
                wire.DisplayERROR();
                return new CircuitERROR(true,"ERROR- Wire with no nodes");
            }
            for(SimNode node : wire.getConnectedSimNodes()){
                SimComp comp = node.getConnectedComp();
                CheckedComps.add(comp);
                for(SimNode tempNode : comp.getConnectedSimNodes()){
                    if(tempNode.getConnectedWire() == null){
                        System.out.println("ERROR- Node has no wire");
                        tempNode.DisplayERROR();
                        CircuitERROR error = new CircuitERROR(true,"ERROR- Node has no wire connected");
                        if(error.ERRORED) return error;
                    }else if(!CheckedWires.contains(tempNode.getConnectedWire()) && tempNode != node){
                        return RecursivelyCheckCircuit(tempNode.getConnectedWire());
                    }
                }
            }
            return new CircuitERROR(false);
        }

    }

}
