package Simulator9000.components.circuitComponents;

import Simulator9000.CurrentOnPin;
import Simulator9000.components.Component;

/**
 * This is a type of {@link Component} for the simulation of a Diode.
 * this is used by programs that want to interface with the circuit simulation library
 */
public class Diode implements Component {

    // this is for holding the forward voltage of the Diode
    private double forwardVoltage;

    /**
     * used to change the forward voltage of the diode
     * @param ForwardVoltage the forward voltage is the amount of voltage needed to get current to flow across a diode
     */
    public void setForwardVoltage(double ForwardVoltage) {
        this.forwardVoltage = ForwardVoltage;
    }

    /**
     * constructor of the Diode.
     * @param ForwardVoltage the amount of voltage needed to get current to flow across a diode
     */
    public Diode(double ForwardVoltage){
        this.forwardVoltage = ForwardVoltage;
    }

    /**
     * this uses a non linear + piecewise VI approximation for the current
     * @return list of {@link CurrentOnPin} that are used to simulate the Diode
     */
    @Override
    public CurrentOnPin[] currentOnPin() {
        CurrentOnPin Temp = (double[] Volts) -> {
            if(Volts[0] - Volts[1] < 0) return 0;
            if(Volts[0] - Volts[1] > forwardVoltage) return 100*(Volts[0] - Volts[1] - forwardVoltage ) + 1;
            return Math.pow(2,Math.pow(Volts[0] - Volts[1]/forwardVoltage,2)) - 1;
        };
        return new CurrentOnPin[]{
                (double[] Volts) -> -Temp.GetCurrent(Volts),
                Temp,
        };
    }
}
