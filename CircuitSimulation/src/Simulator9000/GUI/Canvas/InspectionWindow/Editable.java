package GUI.Canvas.InspectionWindow;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * used for setting values (numbers) that can be edited when the inspector window is shown
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
public @interface Editable {

    /**
     * used for the title of the inspector window
     * @return the Nice name of the component
     */
    String name();

    /**
     * used to get the input type that is used for the inspector window
     * @return enum of type {@link GUI.Canvas.InspectionWindow.ValueController.Type} stating the data type
     */
    ValueController.Type TYPE();
}