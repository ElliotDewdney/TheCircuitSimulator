package GUI.Frame;
import GUI.Canvas.DragAndDropController;
import GUI.GUITools.GUIObjects.GUIObject;
import GUI.GUITools.SettingPane.SettingsController;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

/**
 * for controlling the main outer frame of the GUI
 */
public class Controller {
    /**
     * Map for all the circuits and tabs
     */
    private HashMap<Tab, DragAndDropController> Controllers = new HashMap<>();

    // these are defined by the FXML
    @FXML
    private TabPane TheBigPane;
    @FXML
    private VBox BasicComp;
    @FXML
    private VBox AdvancedComp;
    @FXML
    private VBox OtherComp;
    @FXML
    private MenuItem Simulate;
    @FXML
    private AnchorPane Back1;
    @FXML
    private AnchorPane Back2;
    @FXML
    private AnchorPane Back3;
    @FXML
    private WebView HelpDialoge;

    private String Colour;

    /**
     * this is run when the FXML is loaded
     */
    public void initialize() {
        HelpDialoge.getEngine().load(this.getClass().getResource("/GUI/Frame/HelpDialog.html").toString());

        // adding a dependency for the theme, resulting in the change of the background colour
        SettingsController.getSettingsData().Theme.addDependencies(data -> {
            switch ((String)data){
                case "Dark": Colour = "333333";
                    break;
                case "Blueprint": Colour = "cff6ff";
                    break;
                default: Colour = "d7d7d7";
                    break;
            }
            Back1.setStyle("-fx-background-color: #"+Colour);
            Back2.setStyle("-fx-background-color: #"+Colour);
            Back3.setStyle("-fx-background-color: #"+Colour);
        });

        // this adds the icons to the side panel
        VBox[] Boxes = {BasicComp, AdvancedComp, OtherComp};
        for(int i = 0; i < Boxes.length; i++) {
            int Counter = i;
            GUIObject.getIconByType(i).forEach((temp) -> {
                Pane Icon = temp.Image;
                Icon.setOnDragDetected(event -> {
                    Dragboard db = Icon.startDragAndDrop(TransferMode.COPY_OR_MOVE);
                    ClipboardContent content = new ClipboardContent();
                    content.putImage(temp.RawImage.getImage());
                    content.putString(temp.name.toString());
                    db.setContent(content);
                });
                Boxes[Counter].getChildren().add(Icon);
            });
        }
        DragAndDropController.controller = this;
    }

    /**
     * ran by the FXML loader
     * this is to show a help window
     */
    @FXML
    public void HelpHelp(){
        WebView temp = new WebView();
        temp.getEngine().load(this.getClass().getResource("/GUI/Frame/HelpDialog.html").toString());
        Parent root = temp;
        Scene tempS = new Scene(root,800,600);
        Stage window = new Stage();
        window.setTitle("Help Page");
        window.setScene(tempS);
        window.show();
    }

    /**
     * ran by the FXML loader
     * this will close the current tab in the tab pane
     */
    @FXML
    public void ActionDelete(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Are you sure about that");
        alert.setHeaderText("Close");
        alert.setContentText("are you sure you want to close without saving?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
            TheBigPane.getTabs().remove(TheBigPane.getSelectionModel().getSelectedItem());
        }
    }

    /**
     * this shows the About window
     * this will close the current tab in the tab pane
     */
    @FXML
    public void HelpAbout(){

        VBox temp = new VBox(10);
        temp.setPadding(new Insets(20,20,20,20));
        Parent root = temp;
        temp.getChildren().add(new Label("Version number : " + Main.VersionNo));
        temp.getChildren().add(new Label("Made by Elliot \"Dwendy\" Dewdney for his computing A-Level"));
        temp.getChildren().add(new Label("This is open source and not for profit"));
        temp.getChildren().add(new Label("The source code can be found here : https://github.com/ElliotDewdney/TheCircuitSimulator"));
        Scene tempS = new Scene(root);
        Stage window = new Stage();
        window.setTitle("About Page");
        window.setScene(tempS);
        window.show();
    }

    private boolean Simulating = false;
    private DragAndDropController CurrentController;

    /**
     * this shows the About window
     * this starts or stops a simulation
     */
    @FXML
    public void ActionSimulate(){
        DragAndDropController temp;
        if(Simulating) temp = CurrentController;
        else if(Controllers.containsKey(TheBigPane.getSelectionModel().getSelectedItem()))temp = Controllers.get(TheBigPane.getSelectionModel().getSelectedItem());
        else return;
        Simulating = !Simulating;
        CurrentController = temp;
        temp.SimulatePressed(Simulating);
        if(Simulating)Simulate.setText("Stop Simulation");
        else Simulate.setText("Simulate");
    }

    /**
     * this shows the About window
     * creates a tab from DragAndDrop.FXML and adds it to the tab pane
     */
    @FXML
    public void FileNew() {
        Tab root = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GUI/Canvas/DragAndDrop.fxml"));
            root = loader.load();
            DragAndDropController controller = loader.getController();
            Controllers.put(root, controller);
        } catch (IOException e) {
            e.printStackTrace();
        }
        TheBigPane.getTabs().add(root);

    }

    /**
     * this shows the About window
     * tells the Controller to save a Snapshot to the file provided by the user
     */
    @FXML
    public void FileExport(){
        if(!Controllers.containsKey(TheBigPane.getSelectionModel().getSelectedItem())){
            ExportError();
            return;
        }
        WritableImage tempImage  =  Controllers.get(TheBigPane.getSelectionModel().getSelectedItem()).ExportAsImage();
        if(tempImage == null) {
            ExportError();
            return;
        }

        RenderedImage image = SwingFXUtils.fromFXImage(tempImage, null);
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("png files (*.png)", "*.png"));
        //Prompt user to select a file
        File file = fileChooser.showSaveDialog(null);
        if(file == null) return;
        try {
            ImageIO.write(image, "png", file);
        } catch (IOException ignored) { }
    }

    /**
     * this shows the About window
     * this method sets puts the values of the Exported image into the clipboard
     */
    @FXML
    public void CopyToClipboard(){
        if(!Controllers.containsKey(TheBigPane.getSelectionModel().getSelectedItem())){
            ExportError();
            return;
        }
        Image image = Controllers.get(TheBigPane.getSelectionModel().getSelectedItem()).ExportAsImage();
        if(image == null) {
            ExportError();
            return;
        }
        ClipboardContent content = new ClipboardContent();
        content.putImage(image);
        Clipboard.getSystemClipboard().setContent(content);
    }

    // provide a method for creating a error message for Exporting
    private void ExportError(){
        Alert alert = new Alert(Alert.AlertType.ERROR, "Program unable to Export to image");
        alert.setHeaderText("Sorry I can't do an Export right now");
        alert.setTitle("Export Error");
        alert.showAndWait();
    }

    // this shows the setting pane
    @FXML
    public void ActionSettings(){
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/GUI/GUITools/SettingPane/SettingsPage.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene temp = new Scene(root, 600,400);
        Stage window = new Stage();
        window.setScene(temp);
        window.setTitle("Settings Page");
        window.show();
    }
}
