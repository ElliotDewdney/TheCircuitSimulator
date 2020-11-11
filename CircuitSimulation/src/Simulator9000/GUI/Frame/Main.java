package GUI.Frame;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.InputStream;

/**
 * Main Class, contains main
 */
public class Main extends Application {

    /**
     * the Current VersionNo
     */
    public static final String VersionNo = "1.7.4";

    /**
     * opens that loading splash page, and the program after it has been loaded
     * @param primaryStage the stage that has been set up from the Java FX loader
     * @throws Exception due to mutipage loading
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        // this code initializes the fxml and loads the opening screen as a FXML page using the FXML loader
        InputStream tempURL = Main.class.getResourceAsStream("/GUI/GUITools/Icons/icon.png");
        Image logo = new Image(tempURL);

        primaryStage.getIcons().add(logo);
        primaryStage.setTitle("The Circuit Designer");

        Stage loadingStage = new Stage();
        loadingStage.getIcons().add(logo);
        loadingStage.setTitle("The Circuit Designer");

        loadingStage.initStyle(StageStyle.TRANSPARENT);
        loadingStage.initModality(Modality.APPLICATION_MODAL);

        // loads the loading page
        Parent loading = FXMLLoader.load(getClass().getResource("/GUI/GUITools/Loading/LoadingPage.fxml"));
        Scene loadingScene = new Scene(loading, 2800,600);
        loadingScene.setFill(Color.TRANSPARENT);
        loadingStage.setScene(loadingScene);
        loadingStage.show();


        Parent root = FXMLLoader.load(getClass().getResource("Frame.fxml"));
        primaryStage.setScene(new Scene(root, 1200, 720));


        Task<Void> fakeLoader = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(5000);
                return null;
            }
        };

        // switches the displayed panes
        fakeLoader.setOnSucceeded((WorkerStateEvent event) -> {
            loadingStage.close();
            primaryStage.show();
        });

        new Thread(fakeLoader).start();
    }



    public static void main(String[] args) {
        launch(args);
    }
}
