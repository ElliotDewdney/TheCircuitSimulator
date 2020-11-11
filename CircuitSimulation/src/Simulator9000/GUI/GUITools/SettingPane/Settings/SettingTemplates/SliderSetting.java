package GUI.GUITools.SettingPane.Settings.SettingTemplates;

import GUI.GUITools.SettingPane.Settings.Setting;
import GUI.GUITools.SettingPane.SettingsController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;

/**
 * used to build the GUI element for each of the settings that the user creates
 */
public class SliderSetting implements Setting {
    @FXML
    private Label Title;
    @FXML
    private Label Text;
    @FXML
    private Slider slider;

    private SettingsController.SettingsType.IntegerSetting Data;

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
        Data = (SettingsController.SettingsType.IntegerSetting) data;
        Text.setText(Data.getData().toString());
    }

    private double increment;

    /**
     * used to add the additional data
     * @param values defines the additional data (not Used)
     */
    @Override
    public void setValues(String[] values) {
        double min = Integer.parseInt(values[0]);
        double max = Integer.parseInt(values[1]);
        increment = Integer.parseInt(values[2]);
        slider.setMax(max);
        slider.setMin(min);
        slider.setValue(Data.getData());
        slider.setOnMouseReleased(event -> Text.setText(String.valueOf((int)(((int)(slider.getValue()/ increment))* increment))));
    }

    /**
     * used to apply the setting
     */
    @Override
    public void applySetting() {
        Data.setData((int)(((int)(slider.getValue()/ increment))* increment));
    }
}
