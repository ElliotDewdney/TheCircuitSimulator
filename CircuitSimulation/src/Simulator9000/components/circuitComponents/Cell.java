package Simulator9000.components.circuitComponents;

import Simulator9000.CircuitGraph;
import Simulator9000.CurrentOnPin;
import Simulator9000.components.Component;

/**
 * This is a type of {@link Component} for the simulation of a bulb.
 * this is used by programs that want to interface with the circuit simulation library
 */
public class Cell implements Component {

    // for holding the values of the internal resistance and the emf
    private double internalResistance;
    private double emf;

    /**
     * for setting the internal resistance of the cell
     * @param internalResistance the internal resistance of the cell in ohms, e.g 5 Ohms ...
     */
    public void setInternalResistance(double internalResistance) {
        this.internalResistance = internalResistance;
    }

    /**
     * for setting the Electron Motive Force of the cell
     * @param emf the "Voltage" that the cell creates
     */
    public void setEmf(double emf) {
        this.emf = emf;
    }

    /**
     * this is the constructor for the cell.
     * @param internalResistance  the internal resistance of the cell in ohms, e.g 5 Ohms ...
     * @param emf the "Voltage" that the cell creates
     */
    public Cell(double internalResistance, double emf){
        this.internalResistance = internalResistance;
        this.emf = emf;
    }

    /**
     * this gives the current from the cell, it our using a linear rate of change plus constant emf
     * @return list of {@link CurrentOnPin} that are used to simulate the bulb
     */
    @Override
    public CurrentOnPin[] currentOnPin() {
        return new CurrentOnPin[]{
                (double[] Volts) -> (emf - (Volts[0] - Volts[1])) / internalResistance,
                (double[] Volts) -> ((Volts[0] - Volts[1]) - emf) / internalResistance,
        };
    }
}
