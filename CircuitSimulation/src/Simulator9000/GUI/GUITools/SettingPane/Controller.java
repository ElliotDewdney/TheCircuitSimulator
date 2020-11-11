package GUI.GUITools.SettingPane;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Controller for the setting window,
 * used to display and interact with the settings
 */
public class Controller {

    @FXML
    public VBox SettingsPane;
    @FXML
    public ListView<SettingMenuItem> ListSettings;

    /**
     * throw away class defined to make the setting side bar
     */
    private static class SettingMenuItem{
        SettingCategories type;
        String name;

        public SettingMenuItem(SettingCategories type, String name){
            this.type = type;
            this.name = name;
        }
        @Override
        public String toString(){return name;}
    }

    /**
     * used as for generating the side bar for the settings
     */
    private static SettingMenuItem[] names = {
            new SettingMenuItem(SettingCategories.CIRCUIT_EDITOR , "Circuit Editor"),
            new SettingMenuItem(SettingCategories.FILES , "Flies and Saving"),
            new SettingMenuItem(SettingCategories.SIMULATION_MODE , "Simulation Mode"),
            new SettingMenuItem(SettingCategories.THEMES , "Themes and Colour"),
    };

    /**
     * runs when a user changes the {@link SettingCategories} to load another SettingCategories
     */
    @FXML
    public void OnEdit() {
        SettingsPane.getChildren().removeAll(SettingsPane.getChildren());
        SettingsController.getInstance().getGUIbyType(ListSettings.getFocusModel().getFocusedItem().type).forEach(tempNode -> SettingsPane.getChildren().add(tempNode));
    }

    /**
     * run when user selects ApplyAndClose
     * runs Apply();
     * then Close();
     */
    public void ApplyAndClose(){
        Apply();
        Close();
    }

    /**
     * tells the {@link SettingsController} to apply all the settings
     */
    @FXML
    public void Apply(){
        SettingsController.getInstance().ApplySettings();
    }

    /**
     * used to close the window and will not apply any changes
     */
    @FXML
    public void Close(){
        Stage stage = (Stage) SettingsPane.getScene().getWindow();
        stage.close();
    }

    /**
     * reloads the default settings, losing all changes made
     */
    @FXML
    public void ResetToDefalts(){
        SettingsController.getInstance().ResetDefalt();
        OnEdit();
    }

    /**
     * used to initialize the window
     */
    public void initialize(){
        ObservableList<SettingMenuItem> items = FXCollections.observableArrayList(names);
        ListSettings.setItems(items);
    }

}
