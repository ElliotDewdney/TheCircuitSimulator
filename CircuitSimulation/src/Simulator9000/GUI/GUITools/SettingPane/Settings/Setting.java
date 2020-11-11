package GUI.GUITools.SettingPane.Settings;

import GUI.GUITools.SettingPane.SettingsController;

/**
 * used by the individual settings for all of the templates
 */
public interface Setting {
    /**
     * used to set the title of the setting template
     * @param title name of the setting
     */
    void setTitle(String title);

    /**
     * used to update the setting with the new data
     * @param data the new data to use as a pointer
     */
    void setSettingData(SettingsController.SettingsType.Setting data);

    /**
     * the extra data that is sometimes required
     * @param values used to add extra details if needed
     */
    void setValues(String[] values);

    /**
     * used to apply the new data to the setting and save the change
     */
    void applySetting();
}
