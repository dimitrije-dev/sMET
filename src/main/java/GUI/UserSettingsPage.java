package GUI;

import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

public class UserSettingsPage extends Scena {
    private static Stage stage;
    public static UserSettingsPage instance;
    private static final Rectangle2D bounds = screen.getVisualBounds();
    static {
        try {
            instance = new UserSettingsPage();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public UserSettingsPage() throws IOException {
        super(root());
        this.getStylesheets().add((Objects.requireNonNull(this.getClass().getResource("/profile-page.css"))).toExternalForm());
        if (UserSettingsPage.instance == null) {
            UserSettingsPage.instance = this;
        }
    }
    private static Parent root() throws IOException {
        HBox hBox = new HBox();
        CustomMenuBar menuBar = new CustomMenuBar("inactive-button-menu-bar", "active-button-menu-bar", "inactive-button-menu-bar", "inactive-button-menu-bar", "inactive-button-menu-bar");
        AnchorPane anchorPane = menuBar.getCustomMenuBar();
        hBox.getChildren().addAll(anchorPane, mainPart());
        return hBox;
    }
    private static AnchorPane mainPart() throws IOException {
        AnchorPane mainPart = new AnchorPane();
        mainPart.getStyleClass().add("main-part");
        mainPart.setMinWidth(bounds.getWidth() * 0.8125);
        mainPart.setMaxWidth(bounds.getWidth() * 0.8125);
        return mainPart;
    }




}
