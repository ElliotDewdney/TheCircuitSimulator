package Simulator9000;

/**
 * this is used when a UI element wants to be interactive,
 * using this they will be able to notify the simulation thread,
  */
public interface Interactable {
    /**
     * using this the component can restart simulation, e.g when a switch is toggled
     * @param Controller this {@link Simulator9000.Circuit.SimulationControl} can be used to restart the simulation, or terminate it
     */
    void MakeInteractable(Circuit.SimulationControl Controller);
}