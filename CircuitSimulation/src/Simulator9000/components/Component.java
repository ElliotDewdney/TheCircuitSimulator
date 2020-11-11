package Simulator9000.components;
import Simulator9000.CurrentOnPin;

/**
 * defines the properties of a
 */
public interface Component {
    /**
     * describes how a component is simulated giving the V-I characteristics of that component
     * this is done by extending the CurrentOnPin Interface in a array, e.g {@link Simulator9000.components.circuitComponents.Bulb}
     * @return an array containing the voltage characteristics, using {@link CurrentOnPin}
     */
    CurrentOnPin[] currentOnPin();
}
