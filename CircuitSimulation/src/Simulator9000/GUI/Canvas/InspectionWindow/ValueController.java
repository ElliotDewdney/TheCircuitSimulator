package GUI.Canvas.InspectionWindow;

import GUI.GUITools.GUIObjects.Components.Component;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.lang.reflect.Field;

/**
 * used to control individual inputs of the inspector window
 */
public class ValueController {

    @FXML
    private Label Title;
    @FXML
    private HBox Box;

    private Type type;
    private CheckBox BooleanInput = new CheckBox();
    private TextField DoubleInput = new TextField();
    private Component currentComponent;
    private Field activeField;

    /**
     * this tries to apply the changes made to the pointer supplied in the field
     * it returns true if the Apply method was successful
     * @return flag for if it was successful
     */
    public boolean Apply(){
        switch (type) {
            case BOOLEAN:
                try {
                    activeField.set(currentComponent, BooleanInput.isSelected());
                    return true;
                } catch (Exception e) {
                    System.out.println("Not Working");
                    DataInvalidAlertBox(String.valueOf(BooleanInput.isSelected()));
                    return false;
                }
            case DOUBLE:
                try {
                    if(Double.parseDouble(DoubleInput.getText())<0) throw new Exception();
                    activeField.set(currentComponent, Double.parseDouble(DoubleInput.getText()));
                    return true;
                } catch (Exception e) {
                    System.out.println("Not Working");
                    DataInvalidAlertBox(DoubleInput.getText());
                    return false;
                }
        }
        return false;
    }

    /**
     * this is a Error box if the data entered is incorrect
     * used as a general error box for this part of the GUI
     * @param data this is the details of the error that the program is to display
     */
    public void DataInvalidAlertBox(String data){
        Alert alert = new Alert(Alert.AlertType.ERROR, "Program Error");
        alert.setHeaderText("Unable to set the Property to the entered value for :");
        alert.setContentText(" " + currentComponent.name().NiceName + " ( " + activeField.getName() +  " ) ==> " + data);
        alert.setTitle("Value Incorrect");
        alert.showAndWait();
    }

    /**
     * defines the current types of value that are available
     * BOOLEAN - will display a Check box
     * DOUBLE - will show a number input box
     */
    public enum Type{
        BOOLEAN, DOUBLE;
    }

    /**
     * the title and data are passed to this where the Data is Retrieved and displayed
     * @param name the name of the component to show as the title
     * @param comp the component to inspect
     * @param field the field of the component to change
     * @param type the Type on input that is required
     * @return retuning so multiple calls can follow
     */
    public ValueController setTitleAndData(String name, Component comp, Field field, Type type){
        Title.setText(name);
        this.type = type;
        // uses the type to get generate the GUI
        switch (type) {
            case DOUBLE:
                Box.getChildren().add(DoubleInput);
                try {
                    DoubleInput.setText(String.valueOf(field.get(comp)));
                } catch (IllegalAccessException e) {}
                break;
            case BOOLEAN:
                Box.getChildren().add(BooleanInput);
                try {
                    BooleanInput.setSelected((boolean)field.get(comp));
                } catch (IllegalAccessException e) {}
                break;
        }
        this.currentComponent = comp;
        this.activeField = field;
        return this;
    }
}
