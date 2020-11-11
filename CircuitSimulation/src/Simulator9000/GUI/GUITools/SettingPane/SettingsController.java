package GUI.GUITools.SettingPane;

import GUI.GUITools.SettingPane.Settings.SettingTemplates._SettingsUtils;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * a singleton class that is used to interface with any settings data,
 * also controller for the setting GUI
 *
 */
public class SettingsController {

    private static SettingsController ourInstance = new SettingsController();

    private SettingsData settingsData;

    /**
     * used to get the current setting data class to add the dependency
     * @return the settings data
     */
    public static SettingsData getSettingsData(){
        return getInstance().settingsData;
    }

    /**
     * used in the Singleton programing Paradigm
     * @return a Singleton for Setting Controller
     */
    public static SettingsController getInstance() {
        return ourInstance;
    }

    private HashMap<SettingCategories, ArrayList<Node>> SettingsByCat =  new HashMap<>();

    /**
     * used to get the GUI elements that are defined in a certain category
     * @param categories this a type of {@link SettingCategories} for current category
     * @return a list of the GUI elements that need to be displayed
     */
    public ArrayList<Node> getGUIbyType(SettingCategories categories){
        return (SettingsByCat.get(categories) == null) ? new ArrayList<>() : SettingsByCat.get(categories);
    }

    /**
     * Constructor run internally as the singleton
     */
    private SettingsController() {
        // read the current settings from file
        settingsData = Deserializer();

        // constructs GUI elements using the templates
        for(Field field : SettingsData.class.getDeclaredFields()) for(SettingConstruction settingConstruction : field.getAnnotationsByType(SettingConstruction.class)) try {
            _SettingsUtils temp =  new _SettingsUtils(settingConstruction.title(),settingConstruction.type(),settingConstruction.values(), (SettingsType.Setting) field.get(settingsData));
            SettingsByCat.computeIfAbsent(settingConstruction.category(), k -> new ArrayList<>());
            SettingsByCat.get(settingConstruction.category()).add(temp.getSettingGUI());
        } catch (IllegalAccessException ignored) { }

    }

    private final String fileLocation = "src/GUI/GUITools/SettingPane/Data";
    private final String delimiter = " <== ";

    /**
     * used to read in the current settings from the file
     * @return the instance of SettingData that has been streamed to the file
     */
    private SettingsData Deserializer(){
        SettingsData settingsData = new SettingsData();
        HashMap<String,String> Data = new HashMap<>();
        try {
            BufferedReader reader =  new BufferedReader(new FileReader(fileLocation));
            String data;
            // reads the file into the hash map using key value pairs, separating using the delimiter
            while((data = reader.readLine()) != null) {
                String[] temp = data.split(delimiter);
                Data.put(temp[0],temp[1]);
            }
            // this reflectively assigned the values that were read into the fields of the Settings data instance
            for(Field field : SettingsData.class.getDeclaredFields())
                if(Data.containsKey(field.getName()))
                    ((SettingsType.Setting)field.get(settingsData)).Deserialization(Data.get(field.getName()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return settingsData;
    }

    /**
     * takes in an instance of SettingData and write it to the settings file
     * @param settingsData the instance to be writen
     */
    private void Serializer(SettingsData settingsData){
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileLocation));
            // for each setting writes to the setting file
            for(Field field : SettingsData.class.getDeclaredFields())
                writer.write(field.getName() + delimiter + ((SettingsType.Setting)field.get(settingsData)).Serialization()+ System.lineSeparator());
            writer.flush();
            writer.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * will apply all the changes to the settings
     */
    public void ApplySettings(){
        _SettingsUtils.applySettings();
        Serializer(settingsData);
    }

    /**
     * used to reset all the setting to their default settings
     */
    public void ResetDefalt(){
        settingsData =  new SettingsData();
        Serializer(settingsData);
        ourInstance = new SettingsController();
    }

    /**
     * holds all the settings classed + interfaces for the Setting Data
     */
    public static class SettingsType{

        /**
         * used to define the way that dependency are added to seeings
         */
        public interface Dependency{

            /**
             * this is ran when the setting is changed
             * @param data the new data from the setting change
             */
            void action(Object data);
        }

        /**
         * defines the way that settings are Serialised/Deserialised to a string
         */
        public interface SettingSerialisation{

            /**
             * used for Serialization
             * @return the string that holds the data of the setting
             */
            String Serialization();

            /**
             * used for Deserialization
             * @param data the string that holds the data of the setting
             */
            void Deserialization(String data);
        }

        /**
         * base class for all the setting type of the program
         */
        public interface Setting extends SettingSerialisation{

            /**
             * used to get the data from the setting using Super-Class Object
             * @return the data held by the setting
             */
            Object getData();

            /**
             * used to set the data to the setting using Super-Class Object
             * @param data the new data tobe held by the setting
             */
            void setData(Object data);

            /**
             * used to add Dependencies to the setting
             * @param dependency see {@link Dependency}
             */
            void addDependencies(Dependency dependency);
        }

        /**
         * a Setting object of type {@link Setting}, that is used for settings that store strings
         */
        public static class StringSetting implements Setting{
            private String data;
            private ArrayList<Dependency> dependencies = new ArrayList<>();

            /**
             * the constructor of Setting, used to define the default setting
             * @param defaltSetting the value of the default setting
             */
            public StringSetting(String defaltSetting){this.data = defaltSetting;}

            /**
             * used to get a pointer to the data,
             * @return the setting data
             */
            @Override
            public String getData(){return data;}

            /**
             * used to set the new value of the data
             * @param data the new setting data
             */
            @Override
            public void setData(Object data){
                this.data = (String) data;
                dependencies.forEach(dependency -> dependency.action(this.data));
            }

            /**
             * used to see the dependencies of the setting
             * @param dependency see {@link Dependency}
             */
            @Override
            public void addDependencies(Dependency dependency) {
                dependency.action(data);
                dependencies.add(dependency);
            }

            /**
             * used for Serialization
             * @return the string that holds the data of the setting
             */
            @Override
            public String Serialization() {
                return data;
            }

            /**
             * used for Deserialization
             * @param data the string that holds the data of the setting
             */
            @Override
            public void Deserialization(String data) {
                this.data = data;
            }
        }

        /**
         * a Setting object of type {@link Setting}, that is used for settings that store integers
         */
        public static class IntegerSetting implements Setting{
            private Integer data;
            private ArrayList<Dependency> dependencies = new ArrayList<>();

            /**
             * the constructor of Setting, used to define the default setting
             * @param defaltSetting the value of the default setting
             */
            public IntegerSetting(Integer defaltSetting){this.data = defaltSetting;}

            /**
             * used to get a pointer to the data,
             * @return the setting data
             */
            @Override
            public Integer getData(){return data;}

            /**
             * used to set the new value of the data
             * @param data the new setting data
             */
            @Override
            public void setData(Object data){
                this.data = (Integer) data;
                dependencies.forEach(dependency -> dependency.action(this.data));
            }

            /**
             * used to see the dependencies of the setting
             * @param dependency see {@link Dependency}
             */
            @Override
            public void addDependencies(Dependency dependency) {
                dependency.action(data);
                dependencies.add(dependency);
            }

            /**
             * used for Serialization
             * @return the string that holds the data of the setting
             */
            @Override
            public String Serialization() {
                return String.valueOf(data);
            }

            /**
             * used for Deserialization
             * @param data the string that holds the data of the setting
             */
            @Override
            public void Deserialization(String data) {
                this.data = Integer.parseInt(data);
            }
        }

        /**
         * a Setting object of type {@link Setting}, that is used for settings that store colours
         */
        public static class ColorSetting implements Setting{
            private Color data;
            private ArrayList<Dependency> dependencies = new ArrayList<>();

            /**
             * the constructor of Setting, used to define the default setting
             * @param defaltSetting the value of the default setting
             */
            public ColorSetting(Color defaltSetting){this.data = defaltSetting;}

            /**
             * used to get a pointer to the data,
             * @return the setting data
             */
            @Override
            public Color getData(){return data;}

            /**
             * used to set the new value of the data
             * @param data the new setting data
             */
            @Override
            public void setData(Object data){
                this.data = (Color) data;
                dependencies.forEach(dependency -> dependency.action(this.data));
            }

            /**
             * used to see the dependencies of the setting
             * @param dependency see {@link Dependency}
             */
            @Override
            public void addDependencies(Dependency dependency) {
                dependency.action(data);
                dependencies.add(dependency);
            }

            /**
             * used for Serialization
             * @return the string that holds the data of the setting
             */
            @Override
            public String Serialization() {
                return data.toString();
            }

            /**
             * used for Deserialization
             * @param data the string that holds the data of the setting
             */
            @Override
            public void Deserialization(String data) {
                this.data = Color.valueOf(data);
            }
        }

        /**
         * a Setting object of type {@link Setting}, that is used for settings that store Doubles
         */
        public static class DoubleSetting implements Setting{
            private Double data;
            private ArrayList<Dependency> dependencies = new ArrayList<>();

            /**
             * the constructor of Setting, used to define the default setting
             * @param defaltSetting the value of the default setting
             */
            public DoubleSetting(Double defaltSetting){this.data = defaltSetting;}

            /**
             * used to get a pointer to the data,
             * @return the setting data
             */
            @Override
            public Double getData(){return data;}

            /**
             * used to set the new value of the data
             * @param data the new setting data
             */
            @Override
            public void setData(Object data){
                this.data = (Double) data;
                dependencies.forEach(dependency -> dependency.action(this.data));
            }

            /**
             * used to see the dependencies of the setting
             * @param dependency see {@link Dependency}
             */
            @Override
            public void addDependencies(Dependency dependency) {
                dependency.action(data);
                dependencies.add(dependency);
            }

            /**
             * used for Serialization
             * @return the string that holds the data of the setting
             */
            @Override
            public String Serialization() {
                return String.valueOf(data);
            }

            /**
             * used for Deserialization
             * @param data the string that holds the data of the setting
             */
            @Override
            public void Deserialization(String data) {
                this.data = Double.valueOf(data);
            }
        }

        /**
         * a Setting object of type {@link Setting}, that is used for settings that store Booleans
         */
        public static class BooleanSetting implements Setting{
            private Boolean data;
            private ArrayList<Dependency> dependencies = new ArrayList<>();

            /**
             * the constructor of Setting, used to define the default setting
             * @param defaltSetting the value of the default setting
             */
            public BooleanSetting(Boolean defaltSetting){this.data = defaltSetting;}

            /**
             * used to get a pointer to the data,
             * @return the setting data
             */
            @Override
            public Boolean getData(){return data;}

            /**
             * used to set the new value of the data
             * @param data the new setting data
             */
            @Override
            public void setData(Object data){
                this.data = (Boolean) data;
                dependencies.forEach(dependency -> dependency.action(this.data));
            }

            /**
             * used to see the dependencies of the setting
             * @param dependency see {@link Dependency}
             */
            @Override
            public void addDependencies(Dependency dependency) {
                dependency.action(data);
                dependencies.add(dependency);
            }

            /**
             * used for Serialization
             * @return the string that holds the data of the setting
             */
            @Override
            public String Serialization() {
                return String.valueOf(data);
            }

            /**
             * used for Deserialization
             * @param data the string that holds the data of the setting
             */
            @Override
            public void Deserialization(String data) {
                this.data = Boolean.parseBoolean(data);
            }
        }
    }
}

