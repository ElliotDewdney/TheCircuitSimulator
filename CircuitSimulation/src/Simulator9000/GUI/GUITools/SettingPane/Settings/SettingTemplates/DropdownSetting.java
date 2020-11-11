package GUI.GUITools.SettingPane.Settings.SettingTemplates;

import GUI.GUITools.SettingPane.Settings.Setting;
import GUI.GUITools.SettingPane.SettingsController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

/**
 * used to build the GUI element for each of the settings that the user creates
 */
public class DropdownSetting implements Setting {
    @FXML
    private Label Title;
    @FXML
    private ComboBox<String> comboBox;

    private SettingsController.SettingsType.StringSetting Data;

    /**
     * sets the title of the setting for the user to see
     * @param title the title of the setting
     */
    @Override
    public void setTitle(String title) {Title.setText(title);}

    /**
     * the data for the setting to handle
     * @param data the Setting (pointer)
     */
    @Override
    public void setSettingData(SettingsController.SettingsType.Setting data) {
        Data = (SettingsController.SettingsType.StringSetting) data;
        comboBox.setPromptText("Selected : " + Data.getData());
    }

    /**
     * used to add the additional data
     * @param values defines the dropdown menu
     */
    @Override
    public void setValues(String[] values) {
        comboBox.setItems(FXCollections.observableArrayList(values));
    }

    /**
     * used to apply the setting
     */
    @Override
    public void applySetting() {
        Data.setData((comboBox.getSelectionModel().getSelectedItem() != null) ? comboBox.getSelectionModel().getSelectedItem() : Data.getData());
    }
}
