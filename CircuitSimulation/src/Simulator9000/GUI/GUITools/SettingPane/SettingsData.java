package GUI.GUITools.SettingPane;

import GUI.GUITools.SettingPane.Settings.GUIType;
import javafx.scene.paint.Color;

/**
 * A Container Class that holding all the settings.
 *
 * to create a new setting:
 * put @SettingConstruction( and fill in the details see {@link SettingConstruction}
 *
 * then put  public SettingsController.SettingsType. then the setting type see {@link SettingsController}
 *
 */
public class SettingsData{

    @SettingConstruction(title = "Theme", category = SettingCategories.THEMES, type = GUIType.DROPDOWN, values = {"Dark", "Light", "Blueprint", "Custom"})
    public SettingsController.SettingsType.StringSetting Theme = new SettingsController.SettingsType.StringSetting("Blueprint");

    @SettingConstruction(title = "Background Colour", category = SettingCategories.THEMES, type = GUIType.COLOUR)
    public SettingsController.SettingsType.ColorSetting backgroundColour =  new SettingsController.SettingsType.ColorSetting(Color.valueOf("#6ce6cb"));

    @SettingConstruction(title = "WireColour Colour", category = SettingCategories.THEMES, type = GUIType.COLOUR)
    public SettingsController.SettingsType.ColorSetting WireColour =  new SettingsController.SettingsType.ColorSetting(Color.valueOf("#b02020"));

    @SettingConstruction(title = "Standard", category = SettingCategories.CIRCUIT_EDITOR, type = GUIType.DROPDOWN, values = {"IEEE","IEC"})
    public SettingsController.SettingsType.StringSetting Standard =  new SettingsController.SettingsType.StringSetting("IEEE");

    @SettingConstruction(title = "UI Scale", category = SettingCategories.CIRCUIT_EDITOR, type = GUIType.SLIDER, values = {"0", "20", "1"})
    public SettingsController.SettingsType.IntegerSetting EditorUIScale = new SettingsController.SettingsType.IntegerSetting(10);

    @SettingConstruction(title = "Wire Thickness", category = SettingCategories.THEMES, type = GUIType.SLIDER, values = {"0", "10", "1"})
    public SettingsController.SettingsType.IntegerSetting WireThiccness = new SettingsController.SettingsType.IntegerSetting(3);

    @SettingConstruction(title = "UI Scale", category = SettingCategories.SIMULATION_MODE, type = GUIType.SLIDER, values = {"0", "20", "1"})
    public SettingsController.SettingsType.IntegerSetting SimulationUIScale = new SettingsController.SettingsType.IntegerSetting(10);

    @SettingConstruction(title = "Default File Path", category =  SettingCategories.FILES, type = GUIType.STRING)
    public SettingsController.SettingsType.StringSetting FilePath = new SettingsController.SettingsType.StringSetting("C://Documents/");

    @SettingConstruction(title = "Imperial/metric", category = SettingCategories.SIMULATION_MODE, type = GUIType.DROPDOWN, values = {"Metric","Imperial"})
    public SettingsController.SettingsType.StringSetting Mesurment = new SettingsController.SettingsType.StringSetting("Metric");

    @SettingConstruction(title = "Auto-save", category = SettingCategories.FILES, type = GUIType.CHECKBOX)
    public SettingsController.SettingsType.BooleanSetting Autosave = new SettingsController.SettingsType.BooleanSetting(true);

    @SettingConstruction(title = "Simulation results Revealed", category = SettingCategories.SIMULATION_MODE, type = GUIType.CHECKBOX)
    public SettingsController.SettingsType.BooleanSetting Reveal = new SettingsController.SettingsType.BooleanSetting(true);

    @SettingConstruction(title = "Auto-save repeat time (mins)", category = SettingCategories.FILES, type = GUIType.SLIDER, values = {"1", "10", "1"})
    public SettingsController.SettingsType.IntegerSetting AutoSaveTime = new SettingsController.SettingsType.IntegerSetting(5);

    @SettingConstruction(title = "grid size", category = SettingCategories.CIRCUIT_EDITOR, type = GUIType.SLIDER, values = {"0", "10", "1"})
    public SettingsController.SettingsType.IntegerSetting gridSize = new SettingsController.SettingsType.IntegerSetting(4);

    @SettingConstruction(title = "maximum allowed error (1x10^-n)", category = SettingCategories.SIMULATION_MODE, type = GUIType.SLIDER, values = {"1", "10" , "1"})
    public SettingsController.SettingsType.IntegerSetting depth = new SettingsController.SettingsType.IntegerSetting(4);

    @SettingConstruction(title = "maximum iterations per simulation (smaller = faster)", category = SettingCategories.SIMULATION_MODE, type = GUIType.SLIDER, values = {"256", "8192", "1"})
    public SettingsController.SettingsType.IntegerSetting maxInterations = new SettingsController.SettingsType.IntegerSetting(2048);
}
