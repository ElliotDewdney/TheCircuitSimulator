package Simulator9000.components.circuitComponents;

import Simulator9000.CurrentOnPin;
import Simulator9000.components.Component;

public class Switch implements Component {

    //for holding the values of open and resistance
    private boolean Open;
    private double resistance;

    /**
     * sets the state of the switch
     * @param open the state/position of the switch
     */
    public void setOpen(boolean open){
        Open = open;
    }

    /**
     *constructor for the Switch component
     * @param open this starting state of the switch
     * @param resistance the resistance of a closed switch
     */
    public Switch(boolean open, double resistance){
        Open = open;
        this.resistance = resistance;
    }

    //
    /**
     * either use of linear VI or return 0 depending on the state of open
     * @return list of {@link CurrentOnPin} that are used to simulate the Switch
     */
    @Override
    public CurrentOnPin[] currentOnPin() {
        return new CurrentOnPin[]{
                (double[] Volts) -> Open? (Volts[1] - Volts[0] ) / resistance : 0,
                (double[] Volts) -> Open? (Volts[0] - Volts[1] ) / resistance : 0,
        };
    }
}
