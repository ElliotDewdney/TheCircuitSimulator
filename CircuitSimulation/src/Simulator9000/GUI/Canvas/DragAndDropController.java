package GUI.Canvas;

import GUI.Canvas.InspectionWindow.InspectionController;
import GUI.Frame.Controller;
import GUI.GUITools.GUIObjects.Components.ComponentFactory.Constructinator;
import GUI.GUITools.GUIObjects.GUIComp;
import GUI.GUITools.GUIObjects.Wires.WireManager;
import GUI.GUITools.GUIObjects.Wires.WireNode;
import GUI.GUITools.SettingPane.SettingsController;
import Simulator9000.Circuit;
import Simulator9000.CircuitGraph;
import Utils.ComponentName;
import Utils.Coordinate;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Controller for the Circuit tabs of the main GUI
 */
public class DragAndDropController {
    @FXML
    public Tab TheTab;
    @FXML
    public Pane circuitCanvas;
    @FXML
    private SplitPane Splitter;

    /**
     * uses a public Static instance manager to pull pointers similar to a singleton
     */
    public static HashMap<Pane,DragAndDropController> Instances =  new HashMap<>();
    public static Controller controller;
    public WireManager wireManager;
    private ArrayList<GUIComp> components = new ArrayList<>();

    /**
     * used to remove any component safely
     * @param comp the component to be removed
     */
    public void RemoveComponent(GUIComp comp){
        components.remove(comp);
    }

    private Background colour;

    /**
     * run by JavaFX used to initialize the Canvas
     */
    public void initialize(){
        // sets a dependency on the Theme setting for the background colour
        SettingsController.getSettingsData().Theme.addDependencies(data -> {
            switch ((String)data){
                case "Dark": colour = new Background(new BackgroundFill(Color.web("#333333"), CornerRadii.EMPTY, Insets.EMPTY));
                    break;
                case "Blueprint":
                    colour = new Background(new BackgroundImage(new Image(DragAndDropController.class.getResourceAsStream("/GUI/GUITools/Icons/BlueprintTile.png"),32,32,false,false),
                        BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT,
                        BackgroundSize.DEFAULT));
                    break;
                default: colour = new Background(new BackgroundFill(Color.web("#F0F0F0"), CornerRadii.EMPTY, Insets.EMPTY));
                    break;
            }
            circuitCanvas.setBackground(colour);
        });

        // add a dependency on the Setting Background Colour
        SettingsController.getSettingsData().backgroundColour.addDependencies(data -> {
            if(SettingsController.getSettingsData().Theme.getData().equals("Custom")) colour = new Background(new BackgroundFill((Color)data,CornerRadii.EMPTY,Insets.EMPTY));
            circuitCanvas.setBackground(colour);
        });

        Instances.put(circuitCanvas, this);
        wireManager = new WireManager(circuitCanvas, this);

        // this set the pane to accept drag events
        circuitCanvas.setOnDragOver(event -> { if(!simulating)event.acceptTransferModes(TransferMode.COPY_OR_MOVE); });
        circuitCanvas.setOnDragDropped(event -> {
            if(simulating)return;
            // get Content of the drag event
            Dragboard Current = event.getDragboard();
            String name = Current
                    .getString()
                    .toUpperCase()
                    .trim();

            System.out.println("Tyring to add Comp : " + name);
            if(Constructinator.Contains(ComponentName.valueOf(name))) {
                GUIComp temp = new GUIComp(ComponentName.valueOf(name), event.getX(), event.getY(), circuitCanvas, wireManager);
                components.add(temp);
            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR, "Sorry this feature is Unavailable");
                alert.setHeaderText("This may be coming in the next update");
                alert.setTitle("Component Loading Error");
                alert.show();
            }
        });

    }

    private Circuit.SimulationControl simulationControl;
    private VBox SimulationTools;
    private ScrollPane Scroller =  new ScrollPane();
    private boolean simulating;

    /**
     * run when the pane is instructed to simulate
     * @param simulating flag, true for Simulation
     */
    public void SimulatePressed(boolean simulating){
        this.simulating = simulating;
        wireManager.SetSimulation(simulating);
        if(inspectionController != null)inspectionController.ApplyAndClose();
        components.forEach(comp -> comp.SetSimulation(simulating));
        Splitter.setDividerPositions(1);
        if(simulating){
            Splitter.getItems().add(Scroller);
            Circuit circuit = new Circuit(new CircuitGraph(wireManager.getWires(), components), SettingsController.getSettingsData().maxInterations.getData(), Math.pow(10, -SettingsController.getSettingsData().depth.getData()));

            // defining what to do is the simulation encounters a error
            Circuit.ERROR Error = (CircuitGraph.CircuitERROR error) -> Platform.runLater(() -> {
                SimulationTools = null;
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERROR in simulating circuit");
                alert.setHeaderText("ERROR");
                alert.setContentText(error.ERRORTYPE);
                alert.show();
                controller.ActionSimulate();
            });

            // this defines what to do on a successful simulation run
            Runnable toDo = () -> Platform.runLater(() -> {
                circuitCanvas.setBackground(new Background(new BackgroundFill(Color.WHITE,CornerRadii.EMPTY,Insets.EMPTY)));
                Label temp = new Label("Potential Differences and Currents :");
                temp.setFont(new Font(20));
                SimulationTools = new VBox(5, temp);
                SimulationTools.setPadding(new Insets(20,20,20,20));
                Scroller.setContent(SimulationTools);
                Scroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                components.forEach(node -> SimulationTools.getChildren().add(node.ShowData()));
                SimulationTools.widthProperty().addListener( x -> Platform.runLater(() -> Splitter.setDividerPositions(1 - ((SimulationTools.getWidth()+10)/ Splitter.getWidth()))));
            });

            // get the simulation controller add starts the simulation thread
            simulationControl = circuit.getNewSimulationThread(toDo, Error);
        }else {
            simulationControl.TerminateSimulation();
            Splitter.getItems().remove(Scroller);
            circuitCanvas.setBackground(colour);
        }
    }


    private Node InspectorWindow;
    private Line connection;
    private InspectionController inspectionController;
    private Circle node;

    /**
     *  this is to show the inspector window and is run when a draggableGUIObject is double clicked on
     * @param comp the Component that is clicked on
     */
    public void ShowInspectorWindow(GUIComp comp){
        circuitCanvas.getChildren().remove(InspectorWindow);
        circuitCanvas.getChildren().remove(connection);
        circuitCanvas.getChildren().remove(node);
        try {
            // loads the inspector window
            FXMLLoader loader =  new FXMLLoader(getClass().getResource("InspectionWindow/InspectorWindow.fxml"));
            InspectorWindow = loader.load();
            connection = new Line(comp.getGUIObject().Image.getLayoutX()+32,comp.getGUIObject().Image.getLayoutY()+32, 0,0);
            connection.setStroke(Color.ORANGE);
            connection.setStrokeWidth(4);
            node = new Circle(comp.getGUIObject().Image.getLayoutX()+32,comp.getGUIObject().Image.getLayoutY()+32,5,Color.ORANGE);
            inspectionController = loader.getController();
            inspectionController.addComp(comp, circuitCanvas, connection, node);
        } catch (IOException e) {
            e.printStackTrace();
        }
        circuitCanvas.getChildren().add(connection);
        circuitCanvas.getChildren().add(node);
        circuitCanvas.getChildren().add(InspectorWindow);
        Platform.runLater(() -> inspectionController.update());
    }

    /**
     * this returns the current circuit as a image
     * @return a image object that contains the circuit
     */
    public WritableImage ExportAsImage(){
        if(simulating) return null;
        if(wireManager.nodes.isEmpty()) return null;

        Coordinate min = wireManager.nodes.get(0).getPos();
        Coordinate max = wireManager.nodes.get(0).getPos();

        // gets the corners of the image
        for(WireNode node : wireManager.nodes){
            System.out.println(node.getPos());
            if(min.x > node.getPos().x) min.x = node.getPos().x;
            if(min.y > node.getPos().y) min.y = node.getPos().y;
            if(max.x < node.getPos().x) max.x = node.getPos().x;
            if(max.y < node.getPos().y) max.y = node.getPos().y;
        }
        try {
            // removes decorations of the GUI elements
            wireManager.SetSimulation(true);
            components.forEach(comp -> comp.SetSimulation(true));
            circuitCanvas.setBackground(new Background(new BackgroundFill(Color.WHITE,CornerRadii.EMPTY,Insets.EMPTY)));
            SnapshotParameters params = new SnapshotParameters();
            params.setViewport(new Rectangle2D(min.x - 40 ,min.y - 40,max.x-min.x + 80,max.y-min.y + 80));
            WritableImage image = circuitCanvas.snapshot(params, null);
            wireManager.SetSimulation(false);
            components.forEach(comp -> comp.SetSimulation(false));
            circuitCanvas.setBackground(colour);
            return image;
        } catch (Exception ignored) {}
        return null;
    }
}
