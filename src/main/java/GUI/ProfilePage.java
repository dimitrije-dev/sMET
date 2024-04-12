package GUI;

import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import networking.Client;
import networking.packages.FollowRequest;
import org.w3c.dom.Text;

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



    private static final Rectangle2D bounds = screen.getVisualBounds();
    private static ImageView imageView1;

    public static int databaseId;
    public static String username;
    public static String gitLink;
    public static String instagramLink;
    public static String bio;

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
    /**
     * This function generates the root node for the JavaFX scene, including the menu bar and the main part.
     *
     * @return          the configured HBox representing the root node for the JavaFX scene
     * @throws IOException   if an I/O error occurs
     */
    private static Parent root() throws IOException {
        HBox hBox = new HBox();
        CustomMenuBar menuBar = new CustomMenuBar("inactive-button-menu-bar", "active-button-menu-bar", "inactive-button-menu-bar", "inactive-button-menu-bar", "inactive-button-menu-bar");
        AnchorPane anchorPane = menuBar.getCustomMenuBar();
        hBox.getChildren().addAll(anchorPane, mainPart());
        return hBox;
    }
    /**
     * Generates the main part of the layout by creating and positioning various nodes.
     *
     * @return         	the AnchorPane representing the main part of the layout
     */
    private static AnchorPane mainPart() throws IOException {
        AnchorPane mainPart = new AnchorPane();
        mainPart.getStyleClass().add("main-part");

        Node profilePicturePanel = profilePicturePanel();

        Node socialNetworkPanel = socialNetworkPanel();
        mainPart.getChildren().add(profilePicturePanel);
        mainPart.getChildren().add(socialNetworkPanel);

        mainPart.setMinWidth(bounds.getWidth() * 0.8125);
        mainPart.setMaxWidth(bounds.getWidth() * 0.8125);

        AnchorPane.setTopAnchor(profilePicturePanel, 10d);
        AnchorPane.setLeftAnchor(profilePicturePanel, 10d);

        Node settingsButton = settingsButton();

        mainPart.getChildren().add(settingsButton);

        AnchorPane.setTopAnchor(settingsButton, 10d);
        AnchorPane.setRightAnchor(settingsButton, 10d);

        AnchorPane.setTopAnchor(socialNetworkPanel, 400d);
        AnchorPane.setLeftAnchor(socialNetworkPanel, 10d);


        return mainPart;
    }
    public static Label usernameLabel;
    /**
     * A function that generates the profile picture panel.
     *
     * @return         the VBox containing the profile picture panel
     */
    private static VBox profilePicturePanel() throws FileNotFoundException ,IOException {
        VBox profilePicturePanel = new VBox();
        profilePicturePanel.getStyleClass().add("profile-picture-panel");


        Label label = new Label(username);
        label.setId("profile-picture-label");
        usernameLabel = label;
        HBox hBox = new HBox();

        Button button = followButton();


        button.setOnAction(e -> {
            Client.trySend(new FollowRequest(databaseId));
        });



        profilePicturePanel.setBackground(Background.fill(Paint.valueOf("#24273f")));


        hBox.getChildren().addAll(button);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER);
        ImageView imageView= new ImageView();

        Image image = new Image(new FileInputStream("/Users/dimimac/IntelliJ/JAVA II/PROJEKAT/projekat-cs202/assets/logos/isumbg.png"));
        imageView.setImage(image);
        imageView.setFitHeight(200);
        imageView.setFitWidth(200);

        profilePicturePanel.getChildren().addAll(imageView, label, hBox);

        profilePicturePanel.setSpacing(10);
        profilePicturePanel.setAlignment(Pos.CENTER);

        return profilePicturePanel;
    }
    /**
     * Generate a follow button.
     *
     * @return         The follow button created.
     */
    private  static Button followButton(){

        Button button = new Button();
        button.getStyleClass().add("follow-button");
        button.setText("Follow");
        return button;
    }
    /**
     * A method to create and configure a settings button.
     *
     * @throws FileNotFoundException    if the setting icon file is not found
     * @return                         the configured settings button
     */

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




    /**
     * Generates a VBox containing social network profiles with specified spacing and alignment.
     *
     * @return         	the VBox containing the social network profiles
     */
    private static VBox socialNetworkPanel() throws FileNotFoundException {
        VBox socialNetworkPanel = new VBox();
        socialNetworkPanel.setSpacing(10);
        socialNetworkPanel.setAlignment(Pos.CENTER_LEFT);
        socialNetworkPanel.getChildren().addAll(profileBio(ProfilePage.bio),githubButton(), instagramButton());
        socialNetworkPanel.getStyleClass().add("social-network-panel");
        return socialNetworkPanel;


    }

    public static TextArea profileBio;
    /**
     * A description of the entire Java function.
     *
     * @param  bio	description of parameter
     * @return     	description of return value
     */
    private static TextArea profileBio(String bio) {
        TextArea profileBio = new TextArea();
        ProfilePage.profileBio = profileBio;
        profileBio.setPrefHeight(100);
        profileBio.setPrefWidth(200);
        profileBio.setWrapText(true);
       // profileBio.setEditable(false);
        profileBio.getStyleClass().add("profile-bio");
        if (bio == null) {
            bio = "Enter your bio in the settings page";
        }
        profileBio.setText(bio);

        return profileBio;

    }
    /**
     * A description of the entire Java function.
     *
     * @param  paramName	description of parameter
     * @return         	description of return value
     */
    private static Button githubButton() throws FileNotFoundException {
        Button githubButton = GuiUtil.createButtonTextIcon( "Github", "github-button", "/Users/dimimac/INTELLIJ/JAVA II/PROJEKAT/projekat-cs202/assets/icons/github.png");
        githubButton.setOnMouseClicked(a -> {

            if (ProfilePage.gitLink == null) {

            } else {
                try {
                    Desktop.getDesktop().browse(new URI(ProfilePage.gitLink));
                } catch (IOException | URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }

        });
        return githubButton;
    }
    /**
     * This function creates an Instagram button and sets up a mouse click event to open the Instagram link in a browser if it is available.
     *
     * @return         	the Instagram button
     */
    private static Button instagramButton() throws FileNotFoundException {
        Button instagramButton = GuiUtil.createButtonTextIcon( "Instagram", "instagram-button", "/Users/dimimac/INTELLIJ/JAVA II/PROJEKAT/projekat-cs202/assets/icons/instagram.png");
        instagramButton.setOnMouseClicked(a -> {

            if (ProfilePage.instagramLink == null) {

            } else {
                try {
                    Desktop.getDesktop().browse(new URI(ProfilePage.instagramLink));
                } catch (IOException | URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return instagramButton;
    }
    /**
     * Generates a social network bar containing an icon, a name, and a link.
     *
     * @param socialNetworkIcon    the icon representing the social network
     * @param socialNetworkName    the name of the social network
     * @param socialNetworkLink    the link to the social network
     * @return                    the HBox containing the social network bar
     */
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

    /**
     * Set the primary stage for the application.
     *
     * @param  stage  the primary stage to be set
     */
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


