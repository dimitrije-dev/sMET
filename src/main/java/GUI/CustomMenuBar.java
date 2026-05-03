package GUI;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import networking.Client;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class CustomMenuBar {
    private static Stage stage;
    private final AnchorPane anchorPane;


    public CustomMenuBar(String feedButtonStyle, String profileButtonStyle, String messagesButtonStyle, String eventButtonStyle, String iMetButtonStyle) throws FileNotFoundException {
        anchorPane = new AnchorPane();

        String feedString = "  Feed";
        String profileString = " Profile";
        String eventString = " Events";
        String iMetString = " iMet";
        String logOutString = " Log out";
        String messangerString = " Messages";

        String feedIconPath = "assets/icons/person.png";
        String profileIconPath = "assets/icons/avatar-48x48.png";
        String eventIconPath = "assets/icons/female-avatar.png";
        String metIconPath = "assets/logos/imet-logo-bela.png";
        String logoutIconPath = "assets/icons/male-avatar.png";
        String messagesIconPath = "assets/icons/send30x.png";


        Node logo = GuiUtil.logo();
        Node feedBtn = createButton(feedString, feedButtonStyle, feedIconPath, () -> switchScene(Feed.instance2));

        Node messangerBtn = createButton(messangerString, messagesButtonStyle, messagesIconPath, () -> switchScene(MessangerPage.instance));

        Node profileBtn = createButton(profileString, profileButtonStyle, profileIconPath, () -> {
            ProfilePage.username = Client.getUsername();
            if (ProfilePage.usernameLabel != null) {
                ProfilePage.usernameLabel.setText(ProfilePage.username);
            }
            switchScene(ProfilePage.instance);
        });

        Node eventBtn = createButton(eventString, eventButtonStyle, eventIconPath, () -> switchScene(EventsPage.instance));

        Node iMetButton = createButton(iMetString, iMetButtonStyle, metIconPath, this::openURL);

        Node logoutBtn = createButton(logOutString, "inactive-button-menu-bar", logoutIconPath, () -> {
            Client.logout();
            switchScene(LoginPage.instance);
        });
        VBox menuRoot = new VBox(14);
        menuRoot.setPadding(new Insets(20, 14, 20, 14));
        menuRoot.setAlignment(Pos.TOP_CENTER);

        VBox navButtons = new VBox(8);
        navButtons.setFillWidth(true);
        navButtons.getChildren().addAll(feedBtn, profileBtn, messangerBtn, eventBtn, iMetButton);

        for (Node node : navButtons.getChildren()) {
            if (node instanceof Button button) {
                button.setMaxWidth(Double.MAX_VALUE);
            }
        }
        if (logoutBtn instanceof Button button) {
            button.setMaxWidth(Double.MAX_VALUE);
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        menuRoot.getChildren().addAll(logo, navButtons, spacer, logoutBtn);
        anchorPane.getChildren().add(menuRoot);
        AnchorPane.setTopAnchor(menuRoot, 0d);
        AnchorPane.setRightAnchor(menuRoot, 0d);
        AnchorPane.setBottomAnchor(menuRoot, 0d);
        AnchorPane.setLeftAnchor(menuRoot, 0d);

        anchorPane.setPrefWidth(250);
        anchorPane.setMinWidth(220);
        anchorPane.getStyleClass().add("menu-bar");
    }

    /**
     * Set the primary stage for the application.
     *
     * @param stage the primary stage to be set
     */
    public static void setPrimaryStage(Stage stage) {
        CustomMenuBar.stage = stage;
    }

    /**
     * Retrieves the custom menu bar as an AnchorPane.
     *
     * @return the AnchorPane representing the custom menu bar
     */
    public AnchorPane getCustomMenuBar() {
        return anchorPane;
    }

    /**
     * Switches the scene of the stage and relocates the GUI elements accordingly.
     *
     * @param scene the new scene to switch to
     */
    private void switchScene(Scene scene) {
        stage.setScene(scene);
        GuiUtil.relocate(scene);
    }

    /**
     * Opens the specified URL in the default browser.
     */
    private void openURL() {
        try {
            Desktop.getDesktop().browse(new URI("https://imet.metropolitan.ac.rs/student/#/home"));
        } catch (IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create a button with the given text, id, and icon path, and set the action to be performed when the button is clicked.
     *
     * @param buttonText the text to display on the button
     * @param id         the unique identifier of the button
     * @param iconPath   the file path to the icon to display on the button
     * @param action     the action to be executed when the button is clicked
     */
    private Node createButton(String buttonText, String id, String iconPath, Runnable action) throws FileNotFoundException {
        Button button = GuiUtil.createButtonMenu(buttonText, id, iconPath);
        button.setId(id);
        button.setOnAction(actionEvent -> {
            action.run();
        });

        GuiUtil.buttonScaleTransitionMenu(button);
        return button;
    }


}
