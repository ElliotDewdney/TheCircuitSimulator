package Simulator9000.components.circuitComponents;

import Simulator9000.CurrentOnPin;
import Simulator9000.components.Component;

/**
 * This is a type of {@link Component} for the simulation of a Potentiometer.
 * this is used by programs that want to interface with the circuit simulation library
 */
public class Potentiometer implements Component {

    // holds the value of the divider and total resistance
    private double division;
    private double totalResistance;

    /**
     * sets the division percentage
     * @param percentage this is position of the voltage divider (/100)
     */
    public void setPercentage(double percentage) {
        this.division = percentage / 100;
    }

    /**
     * constructor for the Potentiometer class
     * @param percentage position of the voltage divider (/100)
     * @param resistance the total resistance of the Potentiometer
     */
    public Potentiometer(double percentage, double resistance) {
        division = percentage / 100;
        this.totalResistance = resistance;
    }

    /**
     * this is approximates the potentiometer using voltage divider equations
     * @return list of {@link CurrentOnPin} that are used to simulate the Potentiometer
     */
    @Override
    public CurrentOnPin[] currentOnPin() {
        return new CurrentOnPin[]{
                (double[] Volts) -> (Volts[1] - Volts[0]) / (totalResistance * division),
                (double[] Volts) -> (Volts[0] - Volts[1]) / (totalResistance * division) + (Volts[2] - Volts[1]) / (totalResistance * (1 - division)),
                (double[] Volts) -> (Volts[1] - Volts[2]) / (totalResistance * (1 - division)),
        };
    }
}
