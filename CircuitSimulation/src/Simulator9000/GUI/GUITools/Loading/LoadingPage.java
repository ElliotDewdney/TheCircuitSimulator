package GUI.GUITools.Loading;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;

/**
 * used for the loading screen
 */
public class LoadingPage {

    @FXML
    private StackPane stackPane;

    @FXML
    private WebView webView;

    /**
     * run by the FXML, loads the html frame
     */
    public void initialize(){
        webView.getEngine().load(getClass().getResource("/GUI/GUITools/Loading/loading.html").toString());

        // Make the page background transparent.
        stackPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0);");
        final com.sun.webkit.WebPage webPage = com.sun.javafx.webkit.Accessor.getPageFor(webView.getEngine());
        webPage.setBackgroundColor(0);

        this.stackPane.setPrefSize(14000, 1000);
    }

}