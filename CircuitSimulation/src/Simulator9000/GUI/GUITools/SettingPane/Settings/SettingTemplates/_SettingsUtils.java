package GUI.GUITools.SettingPane.Settings.SettingTemplates;

import GUI.GUITools.SettingPane.Settings.GUIType;
import GUI.GUITools.SettingPane.Settings.Setting;
import GUI.GUITools.SettingPane.SettingsController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import java.io.IOException;
import java.util.ArrayList;

/**
 * handles the input and generation of the individual setting elements
 */
public class _SettingsUtils {

    private static ArrayList<Setting> GUISettingControllers =  new ArrayList<>();

    /**
     * used to make the change that the user had input
     */
    public static void applySettings(){
        GUISettingControllers.forEach(Setting::applySetting);
    }

    private Node SettingGUI;

    /**
     * getter for the setting GUI element
     * @return the setting's GUI
     */
    public Node getSettingGUI() {
        return SettingGUI;
    }

    /**
     * constructor for _SettingsUtils,
     * @param title the Title of the setting
     * @param type the setting type see {@link SettingsController.SettingsType}
     * @param dropDown any extra parameters passed onto setting
     * @param Data the data (pointer) that is is be available to the user
     */
    public _SettingsUtils(String title, GUIType type, String[] dropDown, SettingsController.SettingsType.Setting Data){
        System.out.println("Init Setting type : " + type + " \n\rTitle : " + title);
        FXMLLoader loader = new FXMLLoader(getClass().getResource(type.name() + ".fxml"));
        try {
            SettingGUI = loader.load();
            Setting Controller = loader.getController();
            Controller.setTitle(title);
            Controller.setSettingData(Data);
            Controller.setValues(dropDown);
            GUISettingControllers.add(Controller);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
