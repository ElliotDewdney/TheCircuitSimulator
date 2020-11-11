package GUI.GUITools.SettingPane.Settings.SettingTemplates;

import GUI.GUITools.SettingPane.Settings.Setting;
import GUI.GUITools.SettingPane.SettingsController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;

/**
 * used to build the GUI element for each of the settings that the user creates
 * (not Valid yet) this is a work in progress please ignore !!
 */
public class SpinnerSetting implements Setting {
    @FXML
    private Label Title;
    @FXML
    private Spinner<Double> spinner;

    private Double Data;

    @FXML
    public void OnChange(){ }

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
    }

    /**
     * used to add the additional data
     * @param values defines the additional data (not Used)
     */
    @Override
    public void setValues(String[] values) {
        spinner =  new Spinner<>(Double.valueOf(values[0]),Double.valueOf(values[2]), Data, Double.valueOf(values[3]));
    }

    /**
     * used to apply the setting
     */
    @Override
    public void applySetting() { }
}
