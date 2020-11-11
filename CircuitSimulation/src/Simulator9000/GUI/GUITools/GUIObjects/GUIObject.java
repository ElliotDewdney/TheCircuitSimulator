package GUI.GUITools.GUIObjects;
import GUI.Canvas.DragAndDropController;
import GUI.GUITools.SettingPane.SettingsController;
import Utils.ComponentName;
import Utils.Coordinate;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * used as a container/utility class for the getting and containing of images
 */
public class GUIObject {

    /**
     * used to define that categories that the component fall into
     */
    private static final ComponentName[][] types = {
            /*Basic*/       {ComponentName.BULB,ComponentName.CELL,ComponentName.RESISTOR,ComponentName.LED,ComponentName.VARIABLE_RESISTOR},
            /*Advanced*/    {ComponentName.DIODE,ComponentName.CAPACITOR,ComponentName.MOTOR},
            /*Other*/       {ComponentName.POTENTIOMETER,ComponentName.SWITCH}
    };

    public Pane Image;
    public ImageView RawImage;
    public Pane SecondaryPane;
    public ComponentName name;
    private imageGetter getter;

    /**
     * used to get the nice name e.g ComponentName.BULB == Bulb
     * @return the nice name of the component
     */
    public String getNiceName(){
        return name.NiceName;
    }

    /**
     * this is used to construct the image to be viewed in the GUI
     * @param name the name of the component
     */
    public GUIObject(ComponentName name){
        this.name = name;
        getter = imageGetter.getImage(name);
        RawImage = getter.Image;
        SecondaryPane = new Pane(RawImage);
        Image = new Pane(SecondaryPane);
    }

    /**
     * used to change the image e.g ../SWITCH == ../SWITCH-CLOSED
     * @param Addition the additional information
     */
    public void SetAddition(String Addition){
        getter.AddToImage(name,Addition);
    }

    /**
     * used to set the state of the simulation
     * @param simulation flag, true if simulating
     */
    public void setSimulation(boolean simulation){
        if(simulation) getter.updateTheme("Light");
        else getter.updateTheme(SettingsController.getSettingsData().Theme.getData());
    }

    /**
     * used to position components onto the grid defined by the settings
     * @param num the position to be rounded
     * @return the
     */
    public static double ConvertToGrid(double num){
        int gridSize = (int) Math.pow(2,SettingsController.getSettingsData().gridSize.getData().doubleValue());
        return ((int)((num + 0.5*gridSize) / gridSize)) * gridSize;
    }

    /**
     * creates a GUIObjects that is able to be dragged and moved with gestures with the mouse
     * @param name the name of the component for it to be generated
     * @param pane the Canvas where it is drawn
     * @param comp the component to be affiliated with
     * @param pos the drag drop position of the interaction
     * @return a draggable GUIObject
     */
    public static GUIObject getDraggableGUIObject(ComponentName name, Pane pane, GUIComp comp, Coordinate pos){
        GUIObject Image = new GUIObject(name);
        Coordinate coordinate = new Coordinate();
        AtomicBoolean flag = new AtomicBoolean(true);
        // convert to the grid
        Image.Image.setLayoutX(ConvertToGrid(pos.x - Image.RawImage.getImage().getWidth()/2));
        Image.Image.setLayoutY(ConvertToGrid(pos.y - Image.RawImage.getImage().getHeight()/2));

        // make react to drag gestures
        Image.Image.setOnMousePressed(event -> {
            if(comp.Fixed || comp.wireManager.wiring)return;
            if(event.getClickCount() == 2) {
                if(event.getButton().equals(MouseButton.PRIMARY))DragAndDropController.Instances.get(pane).ShowInspectorWindow(comp);
                else {
                    comp.moved();
                    pane.getChildren().remove(Image.Image);
                    comp.remove();
                }
            }
            coordinate.x = event.getX();
            coordinate.y = event.getY();
        });

        // move when dragged
        Image.Image.setOnMouseDragged(event -> {
            if(comp.Fixed || comp.wireManager.wiring)return;
            if (flag.get()) {
                flag.set(false);
                comp.moved();
            }
            if (Image.Image.getLayoutX() < 0 || Image.Image.getLayoutY() < 0) {
                Image.Image.getChildren().get(0).setOpacity(0.5);
            } else {
                Image.Image.getChildren().get(0).setOpacity(1);
            }
            Image.Image.setLayoutX(ConvertToGrid(Image.Image.getLayoutX() + event.getX() - coordinate.x));
            Image.Image.setLayoutY(ConvertToGrid(Image.Image.getLayoutY() + event.getY() - coordinate.y));
        });

        // used to finnish the drag gesture
        Image.Image.setOnMouseReleased(event -> {
            if(comp.Fixed)return;
            if (Image.Image.getLayoutX() < 0 || Image.Image.getLayoutY() < 0) {
                comp.moved();
                pane.getChildren().remove(Image.Image);
                comp.remove();
            }
            flag.set(true);
        });
        return Image;
    }

    /**
     * used to get the components by the category
     * @param type the category type
     * @return list of component names
     */
    public static ArrayList<GUIObject> getIconByType(int type){
        ComponentName[] Comps = types[type];
        ArrayList<GUIObject> Icons = new ArrayList<>();
        for(ComponentName temp : Comps){
            Icons.add(new GUIObject(temp));
        }
        return Icons;
    }

    /**
     * internal class for constructing the images dynamically
     */
    private static class imageGetter{
        private static ColorAdjust Dark = new ColorAdjust(0,0,1,1);
        private static ColorAdjust Blueprint = new ColorAdjust(0,0,-1,1);

        public ImageView Image;

        /**
         * constructs the image, as a container class
         * @param Image the image to contain
         */
        private imageGetter(ImageView Image){
            this.Image = Image;
            SettingsController.getSettingsData().Theme.addDependencies(data -> updateTheme((String) data));
        }

        /**
         * changes the theme of the images
         * @param data the theme to change to
         */
        public void updateTheme(String data){
            switch (data){
                case "Dark" :
                    Image.setEffect(Dark);
                    break;
                case "Blueprint" :
                    Image.setEffect(Blueprint);
                    break;
                default:
                    Image.setEffect(null);
            }
        }

        /**
         * used to add additions to the image see {@link #SetAddition(String)}
         * @param name the name of the component (base)
         * @param Addition the required addition to the file path
         */
        public void AddToImage(ComponentName name, String Addition) {
            if(Addition == null) Image.setImage(images.get(name));
            else{
                System.out.println("Loading Image : " + name);
                InputStream tempURL = imageGetter.class.getResourceAsStream(
                        "/GUI/GUITools/Icons/" + SettingsController.getSettingsData().Standard.getData() + "/" + name.toString() + "-" + Addition + ".png");
                Image.setImage(new Image(tempURL));
            }
        }

        /**
         * map of the images, and names, initialised first
         */
        private static HashMap<ComponentName , Image> images;

        /**
         * map of the active images, and names
         */
        private static HashMap<ComponentName, ArrayList<ImageView>> imageViews =  new HashMap<>();

        /**
         * used to get define the image that that i got by this class
         * @param name the name of the component to be got
         * @return the image that was requested to be got
         */
        public static imageGetter getImage(ComponentName name){
            if(images == null) SettingsController.getSettingsData().Standard.addDependencies(data -> {
                initImages((String)data);
                imageViews.forEach((componentName, imageList) -> imageList.forEach(imageView -> imageView.setImage(images.get(componentName))));
            });
            ImageView temp = new ImageView(images.get(name));
            imageViews.put(name, (imageViews.get(name) == null) ? new ArrayList<>() : imageViews.get(name));
            imageViews.get(name).add(temp);
            return new imageGetter(temp);
        }

        /**
         * internal method run when the standard is changed, loads all the images
         * @param currentStandard the current standard e.g IEEE or IEC
         */
        private static void initImages(String currentStandard){
            HashMap<ComponentName, String> Components =  new HashMap<>();
            for(ComponentName name : ComponentName.values())Components.put(name,"/GUI/GUITools/Icons/" + currentStandard + "/" + name.toString()+ ".png");
            images = new HashMap<>();
            Components.forEach((ComponentName name , String path) -> {
                try {
                    System.out.println("Loading Image : " + name);
                    InputStream tempURL = imageGetter.class.getResourceAsStream(path);
                    try{images.put(name,new Image(tempURL));}
                    catch (Exception ignored) {}
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}


