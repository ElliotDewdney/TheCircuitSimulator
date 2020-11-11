package GUI.GUITools.SettingPane;

import GUI.GUITools.SettingPane.Settings.GUIType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * used to define the list of settings that can be used,
 * all settings are defined in the {@link SettingsData} class
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
public @interface SettingConstruction {

    /**
     * @return the title that of the setting
     */
    String title();

    /**
     * @return the category that of the setting
     */
    SettingCategories category();

    /**
     * @return the type that of the setting
     */
    GUIType type();

    /**
     * @return the additionalInfo that of the setting (settingType specific)
     */
    String[] values() default {};
}
