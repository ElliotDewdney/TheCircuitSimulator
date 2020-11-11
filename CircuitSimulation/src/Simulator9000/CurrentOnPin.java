package Simulator9000;

/**
 * this interface is used to define the behavior of a individual pin of a component acts
 * an example of how it is sued can be seen in {@link Simulator9000.components.circuitComponents.Bulb}
 */
public interface CurrentOnPin {
    /**
     * implemented in anonymous class, using lambdas to overwrite
     * used to define calculation of vi characteristics
     * @param Volts an array of the voltages across the pins of the component
     * @return the current that will flow from the pin (positive-out)
     */
    double GetCurrent(double[] Volts);
}
