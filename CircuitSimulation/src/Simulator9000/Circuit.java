package Simulator9000;

/**
 * the main simulation class of the library, used in simulation circuits
 */
public class Circuit {

    // this holds the data for the current circuit
    private CircuitGraph graph;

    // constants that define the properties of the simulation
    private double precision = 0.0001d;
    private int maxIterations = 4096;

    /**
     * for Constructing the Circuit and setting the value of the graph {@link CircuitGraph} to the circuit
     * @param graph holds the components and wires of the circuit to be simulated
     */
    public Circuit(CircuitGraph graph){
        this.graph = graph;
    }

    /**
     * see {@link #Circuit(CircuitGraph)}
     * @param graph passed to main constructor
     * @param maxIterations sets the max number of approximations made for the simulation
     * @param precision this is the max deviation that a simulation will prematurely endWith
     */
    public Circuit(CircuitGraph graph, int maxIterations, double precision){
        this(graph);
        this.precision = precision;
        this.maxIterations = maxIterations;
    }

    /**
     * this interface is extended anomalously by the class running the simulation
     * used to define how the errors during circuit simulation are dealt with
     */
    public interface ERROR{
        /**
         * this method is run when the simulation has a error
         * @param error container class for the details of the error using {@link CircuitGraph.CircuitERROR} (cause of the error)
         */
        void ReportError(CircuitGraph.CircuitERROR error);
    }

    /**
     * this is used for controlling the simulation,
     * e.g waking or terminating the simulation thread
     */
    public static class SimulationControl{
        // flag telling the simulation if to terminate
        private boolean terminate;

        /**
         * used to wait for a event, this being either a wake up or terminate
         * @return flag telling the program if to terminate
         * @throws InterruptedException if the wait timeouts throws a InterruptedException
         */
        synchronized boolean ThreadWaiter() throws InterruptedException {
            this.wait();
            return terminate;
        }

        /**
         * running this method will end the simulation thread an exit simulation mode
         */
        synchronized public void TerminateSimulation(){
            terminate = true;
            this.notify();
        }

        /**
         * used to rerun the simulation
         */
        synchronized public void WakeSimulation(){
            this.notify();
        }

    }

    //

    /**
     * will start a new thread for the simulation and getting the SimulationControl objects
     * @param simulationEnd this defines the action, using {@link Runnable}, that will happen after a simulation
     * @param error this uses {@link ERROR} to define how errors are reported
     * @return using {@link SimulationControl}
     */
    public SimulationControl getNewSimulationThread(Runnable simulationEnd, ERROR error){
        SimulationControl simControl = new SimulationControl();

        // this gets all the interactable parts of the circuit and hands them a controller
        graph.components.forEach((CircuitGraph.SimComp comp) -> {
            try{ comp.getIntractable().MakeInteractable(simControl); }
            catch (Exception e){ System.out.println("Comp : " + comp + " not Intractable"); }
        });

        // this starts a new thread for the simulation to run in and reports to the controller
        Thread simulation = new Thread(() -> {
            while (true){
                CircuitGraph.CircuitERROR sim = StartSimulating();
                if (sim.ERRORED){
                    error.ReportError(sim);
                    return;
                } else {
                    simulationEnd.run();
                    boolean Stop = false;
                    try {
                        Stop = simControl.ThreadWaiter();
                    } catch (InterruptedException e) {
                        System.out.println("Wait failed");
                    }
                    if (Stop) return;
                }
            }
        });
        simulation.start();
        return simControl;
    }

    /**
     * this starts simulating the circuit so the current and voltage levels are valid
     * @return a container class any errors that have occurred, using {@link CircuitGraph.CircuitERROR}
     */
    public CircuitGraph.CircuitERROR StartSimulating(){
        // checking the circuit is valid
        graph.wires.forEach(wire -> wire.voltage = 0);
        CircuitGraph.CircuitERROR error = graph.checkValid();
        if(error.ERRORED) return error;

        int Counter = 0;
        int depth = 1;
        try {
            while (IterateOnce(graph, 1d / depth++) > precision && Counter++ < maxIterations) System.out.println("Ran Sim code once");

            // for debugging
            System.out.println("Counter reached : " + Counter);
            graph.components.forEach((CircuitGraph.SimComp comp) -> System.out.println("Voltage across : " + comp + " - " + Math.round(Math.abs(comp.Voltages()[0] - comp.Voltages()[1]) * 1000) / 1000f));

        }catch (Exception e){
            error = new CircuitGraph.CircuitERROR(true, "ERROR during simulation");
        }
        return error;
    }

    /**
     * runs one iteration of the approximation formula for simulation of the circuit
     * @param graph the circuit data, using {@link CircuitGraph}
     * @param depth the Change in the voltage that the program uses to simulate
     * @return RMS(root mean square) of the changes made to the voltages across the circuit
     */
    private double IterateOnce(CircuitGraph graph, double depth){
        double change = 0;
        for(CircuitGraph.SimWire wire : graph.wires){
            // use of newton's approximation formula
            double CurrentDeficit0 = wire.getCurrentDeficit();
            wire.voltage += depth;
            double CurrentDeficit1 = wire.getCurrentDeficit();
            // for debugging
            System.out.println("Currents  = " + CurrentDeficit0 + ", " + CurrentDeficit1);
            double dV = CurrentDeficit1*(depth/(CurrentDeficit1-CurrentDeficit0));
            // for debugging
            System.out.println("dV = " + dV);
            change += Math.abs(dV-depth);
            wire.voltage -= dV;
        }
        return change;
    }
}
