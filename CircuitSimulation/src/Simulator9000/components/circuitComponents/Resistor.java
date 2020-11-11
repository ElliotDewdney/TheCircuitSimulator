package Simulator9000.components.circuitComponents;

import Simulator9000.CurrentOnPin;
import Simulator9000.components.Component;

/**
 * This is a type of {@link Component} for the simulation of a Resistor.
 * this is used by programs that want to interface with the circuit simulation library
 */
public class Resistor implements Component {

    // this is used to hold the resistance
    private double Resistance;

    /**
     * sets the resistance of the resistor
     * @param resistance the resistance measured in ohms
     */
    public void setResistance(double resistance){
        Resistance = resistance;
    }

    /**
     * constructor for the Resistor component
     * @param resistance the resistance measure in ohms
     */
    public Resistor(double resistance){
        Resistance = resistance;
    }

    /**
     * this uses a linear VI characteristic to give current for the voltage
     * @return list of {@link CurrentOnPin} that are used to simulate the bulb
     */
    @Override
    public CurrentOnPin[] currentOnPin() {
        return new CurrentOnPin[]{
                (double[] Volts) -> (Volts[1] - Volts[0] ) / Resistance,
                (double[] Volts) -> (Volts[0] - Volts[1] ) / Resistance,
        };
    }
}
