package Simulator9000.components.circuitComponents;

import Simulator9000.CurrentOnPin;
import Simulator9000.components.Component;

/**
 * This is a type of {@link Component} for the simulation of a bulb.
 * this is used by programs that want to interface with the circuit simulation library
 */
public class Bulb implements Component {

    // hold the current value of the rating
    private double rating;

    /**
     * used to change the current rating
     * @param rating the voltage rating of the bulb, e.g 5v bulb or 12v bulb ...
     */
    public void setRating(double rating){
        this.rating = rating;
    }

    /**
     * this is the constructor for the bulb.
     * @param rating the voltage rating of the bulb, e.g 5v bulb or 12v bulb ...
     */
    public Bulb(double rating){
        this.rating = rating;
    }

    /**
     * this uses a non-linear VI characteristic to give current for the voltage
     * @return list of {@link CurrentOnPin} that are used to simulate the bulb
     */
    @Override
    public CurrentOnPin[] currentOnPin() {
        return new CurrentOnPin[]{
                (double[] Volts) -> (Volts[1] - Volts[0]) > 0 ?  Math.pow((Volts[1] - Volts[0])/2*rating,0.5f) : -Math.pow((Volts[0] - Volts[1])/2*rating,0.5f),
                (double[] Volts) -> (Volts[1] - Volts[0]) > 0 ? -Math.pow((Volts[1] - Volts[0])/2*rating,0.5f) :  Math.pow((Volts[0] - Volts[1])/2*rating,0.5f),
        };
    }
}
