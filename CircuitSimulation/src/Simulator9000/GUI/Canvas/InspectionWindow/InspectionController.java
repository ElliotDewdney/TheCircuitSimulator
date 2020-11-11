package GUI.Canvas.InspectionWindow;

import GUI.GUITools.GUIObjects.GUIComp;
import Utils.Coordinate;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * controls and shows the inspection window, for components
 */
public class InspectionController {

    @FXML
    private VBox Main;
    @FXML
    private VBox SettingVBox;
    @FXML
    private ToolBar Draggable;
    @FXML
    private Label CompName;

    private GUIComp comp;
    private Line connection;
    private Circle node;

    private Pane pane;

    /**
     * used to initialise the Inspection controller GUI
     */
    public void initialize(){
        Main.toFront();
        Main.setVisible(false);
        Main.setLayoutX(20);
        Main.setLayoutY(20);
    }

    private ArrayList<ValueController> controllers = new ArrayList<>();

    /**
     * this is called to pass in vital data to be displayed
     * @param comp the component to be linked with
     * @param pane the canvas on witch to draw it on to
     * @param connection the graphical connection to the component
     * @param node the node of the component (GUI only)
     */
    public void addComp(GUIComp comp, Pane pane, Line connection, Circle node){

        connection.setEndX(28);
        connection.setEndY(28);
        connection.setVisible(false);
        comp.setFixed(true);
        this.connection = connection;

        // this makes the Pane draggable so the window can be moved
        Coordinate coordinate = new Coordinate();
        Draggable.setOnMousePressed(event -> {
            coordinate.x = event.getX();
            coordinate.y = event.getY();
        });
        Draggable.setOnMouseDragged(event -> {

            double X = Main.getLayoutX() + event.getX() - coordinate.x;
            double Y = Main.getLayoutY() + event.getY() - coordinate.y;

            if(X < 10) X = 10;
            else if(X > pane.getWidth()-(10+Main.getWidth())) X = pane.getWidth()-(10+Main.getWidth());
            if(Y < 10) Y = 10;

            connection.setEndX(X+8);
            connection.setEndY(Y+8);
            Main.setLayoutX(X);
            Main.setLayoutY(Y);
        });

        // setting some vars
        this.node = node;
        this.comp = comp;
        this.pane = pane;
        //setting a readable name
        CompName.setText(comp.getGUIObject().getNiceName());

        // goes through all the fields of the component that is passed in,
        // gets all that are annotated with the EditableNum and creates ValueControllers for them
        for(Field field : comp.comp.getClass().getDeclaredFields()) if(field.isAnnotationPresent(Editable.class)){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Canvas/InspectionWindow/Value.fxml"));
            HBox root;
            try {
                root = loader.load();
                System.out.println("Creating Setting for " + field.getAnnotation(Editable.class).name() + ", Current Value is " + field.get(comp.comp));
                ValueController controller = ((ValueController)loader.getController()).setTitleAndData(field.getAnnotation(Editable.class).name(), comp.comp, field, field.getAnnotation(Editable.class).TYPE());
                controllers.add(controller);
                SettingVBox.getChildren().add(root);
            } catch (Exception ignored) {}
        }
    }

    /**
     * this updates and moves the position of the connector and the Main to the top right
     */
    public void update(){
        Main.setVisible(true);
        connection.setVisible(true);
        Main.setLayoutX(pane.getWidth() - (Main.getWidth() + 40));
        connection.setEndX(Main.getLayoutX() + 8);
        connection.setEndY(Main.getLayoutY() + 8);

    }

    /**
     * run by FXML on RotateLeft button
     */
    public void RotateLeft(){
        comp.RotateLeftComp();
    }

    /**
     * run by FXML on RotateRight button
     */
    public void RotateRight(){
        comp.RotateRightComp();
    }

    /**
     * Applies the settings and then closes the window if they all successfully apply
     */
    public void ApplyAndClose(){
        for( ValueController C : controllers) if (!C.Apply()) return;
        Close();
    }

    /**
     * used to safely close the inspection window
     */
    public void Close(){
        pane.getChildren().remove(Main);
        pane.getChildren().remove(node);
        pane.getChildren().remove(connection);
        comp.setFixed(false);
    }

}
