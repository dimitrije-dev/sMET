package GUI;

import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public class ProfilePage extends Scena {
    private static Stage stage;
    public static ProfilePage instance;

    private static final ImageView imageView = new ImageView();


    private static final Rectangle2D bounds = screen.getVisualBounds();
    private static ImageView imageView1;

    static {
        try {
            instance = new ProfilePage();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ProfilePage() throws IOException {
        super(root());
        this.getStylesheets().add((Objects.requireNonNull(this.getClass().getResource("/profile-page.css"))).toExternalForm());
        if (ProfilePage.instance == null) {
            ProfilePage.instance = this;
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

        Node profilePicturePanel = profilePicturePanel();

        mainPart.getChildren().add(profilePicturePanel);

        mainPart.setMinWidth(bounds.getWidth() * 0.8125);
        mainPart.setMaxWidth(bounds.getWidth() * 0.8125);

        AnchorPane.setTopAnchor(profilePicturePanel, 10d);
        AnchorPane.setLeftAnchor(profilePicturePanel, 10d);

        Node settingsButton = settingsButton();

        mainPart.getChildren().add(settingsButton);

        AnchorPane.setTopAnchor(settingsButton, 10d);
        AnchorPane.setRightAnchor(settingsButton, 10d);


        return mainPart;
    }

    private static VBox profilePicturePanel() throws FileNotFoundException ,IOException {
        VBox profilePicturePanel = new VBox();
        profilePicturePanel.getStyleClass().add("profile-picture-panel");


        Label label = new Label("Admin Team");

        HBox hBox = new HBox();
        Button button = new Button("Add picture");

        button.setOnAction(e -> {
            File file = fileChooser();
            Image image = new Image(file.toURI().toString());
            imageView.setImage(image);
        });



        profilePicturePanel.setBackground(Background.fill(Paint.valueOf("#FFFFFF")));


        hBox.getChildren().addAll(button);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER);
        imageView.setFitHeight(200);
        imageView.setFitWidth(200);
        profilePicturePanel.getChildren().addAll(imageView, label, hBox);

        profilePicturePanel.setSpacing(10);
        profilePicturePanel.setAlignment(Pos.CENTER);

        return profilePicturePanel;
    }

    private static Node settingsButton() throws FileNotFoundException {

        Button settingsButton = new Button();
        settingsButton.getStyleClass().add("settings-button");
        settingsButton.setMaxHeight(50);
        settingsButton.setMaxWidth(50);
        settingsButton.setGraphic(GuiUtil.createButtonIcon("/Users/dimimac/INTELLIJ/JAVA II/PROJEKAT/projekat-cs202/assets/icons/settingIcon.png"));
        settingsButton.setOnMouseClicked(e -> {
            stage.setScene(UserSettingsPage.instance);
            GuiUtil.relocate(UserSettingsPage.instance);
        });
        GuiUtil.applyScaleTransition(settingsButton, 0.95, 0.95, 200, 300);
        return settingsButton;
    }


    private static File fileChooser() {

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(stage);

        return file;

    };


    private static VBox socialNetworkPanel() {
        VBox socialNetworkPanel = new VBox();
        socialNetworkPanel.getStyleClass().add("social-network-panel");
        return socialNetworkPanel;


    }

    private static HBox socialNetworkBar(ImageView socialNetworkIcon, String socialNetworkName, String socialNetworkLink) {
        HBox socialNetworkBar = new HBox();
        socialNetworkBar.getStyleClass().add("social-network-bar");
        Label soccialNetworkName = new Label(socialNetworkName);
        socialNetworkBar.getChildren().addAll(socialNetworkIcon, soccialNetworkName);
        socialNetworkBar.setSpacing(10);
        socialNetworkBar.setAlignment(Pos.CENTER_LEFT);
        socialNetworkBar.setOnMouseClicked(event -> {

            if (socialNetworkLink == null) {

            }
            try {
                Desktop.getDesktop().browse(new URI(socialNetworkLink));
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });

        return socialNetworkBar;
    }

    private static TilePane socialNetworkNameInput(String socialNetworkName) {
        TilePane socialNetworkNameInput = new TilePane();
        socialNetworkNameInput.getStyleClass().add("social-network-name-input");
        TextInputDialog td = new TextInputDialog("Enter URL here");
        td.setHeaderText("Enter URL to your + " + socialNetworkName);
        td.showAndWait();
        return socialNetworkNameInput;

    }


    public static void setPrimaryStage(Stage stage) {
        ProfilePage.stage = stage;
    }

    /**
     * Returns the primary stage of the application.
     *
     * @return the primary stage of the application
     */
    public static Stage getPrimaryStage() {
        return stage;
    }
}


