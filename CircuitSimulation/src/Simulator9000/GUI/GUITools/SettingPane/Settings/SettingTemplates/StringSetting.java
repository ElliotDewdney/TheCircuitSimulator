package GUI.GUITools.SettingPane.Settings.SettingTemplates;

import GUI.GUITools.SettingPane.Settings.Setting;
import GUI.GUITools.SettingPane.SettingsController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * used to build the GUI element for each of the settings that the user creates
 */
public class StringSetting implements Setting {
    @FXML
    private Label Title;
    @FXML
    private TextField textField;

    private SettingsController.SettingsType.StringSetting Data;

    /**
     * sets the title of the setting for the user to see
     * @param title the title of the setting
     */
    @Override
    public void setTitle(String title) {
        Title.setText(title);
    }

    /**
     * the data for the setting to handle
     * @param data the Setting (pointer)
     */
    @Override
    public void setSettingData(SettingsController.SettingsType.Setting data) {
        Data = (SettingsController.SettingsType.StringSetting) data;
        textField.setPromptText(Data.getData());
    }

    /**
     * used to add the additional data
     * @param values defines the additional data (not Used)
     */
    @Override
    public void setValues(String[] values) {}

    /**
     * used to apply the setting
     */
    @Override
    public void applySetting() {
        Data.setData((textField.getText()).equals("") ? Data.getData() : textField.getText());
    }
}
