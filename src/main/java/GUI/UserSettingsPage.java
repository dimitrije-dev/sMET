package GUI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import networking.Client;
import networking.packages.ProfileChangeRequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;



public class UserSettingsPage extends Scena {
    private static Stage stage;
    public static UserSettingsPage instance;
    private static final Rectangle2D bounds = screen.getVisualBounds();

    private static ImageView imageView200x200;

    private static ImageView imageView50x50;

    private static ImageView imageView100x100;

    private static TextField gitLink;

    private static TextArea bio;

    private static TextField instagramLink;

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
        CustomMenuBar menuBar = new CustomMenuBar("inactive-button-menu-bar",
                "active-button-menu-bar", "inactive-button-menu-bar",
                "inactive-button-menu-bar", "inactive-button-menu-bar");
        AnchorPane anchorPane = menuBar.getCustomMenuBar();
        hBox.getChildren().addAll(anchorPane, mainPart());

        return hBox;
    }
    private static AnchorPane mainPart()  {
        AnchorPane mainPart = new AnchorPane();
        mainPart.getStyleClass().add("main-part");
        mainPart.setMinWidth(bounds.getWidth() * 0.8125);
        mainPart.setMaxWidth(bounds.getWidth() * 0.8125);

        double width = mainPart.getMinWidth();

        Label settingTitle = settingTitle();
        Node settingGrid = settingGrid();

        HBox profilePicturesPanel = profilePicturesPanel();

        Label profilePictureLabel = profilePictureLabel();

        mainPart.getChildren().addAll(settingTitle, settingGrid,profilePictureLabel, profilePicturesPanel);
        AnchorPane.setTopAnchor(settingTitle, 10d);
        AnchorPane.setLeftAnchor(settingTitle, width / 2 - 200d);

        AnchorPane.setTopAnchor(settingGrid, 200d);
        AnchorPane.setLeftAnchor(settingGrid, 50d);

        AnchorPane.setTopAnchor(profilePictureLabel, 440d);
        AnchorPane.setLeftAnchor(profilePictureLabel, 50d);

        AnchorPane.setTopAnchor(profilePicturesPanel, 500d);
        AnchorPane.setLeftAnchor(profilePicturesPanel, 50d);


        return mainPart;
    }
    private static Label settingTitle() {
        Label label = new Label("User Settings");
        label.getStyleClass().add("setting-title");

        return label;
    }

    /**
     * Generate and return a GridPane that contains various input fields and buttons for setting options.
     *
     * @return          the GridPane with setting options
     */
    private static Node settingGrid() {
        GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add("setting-grid-pane");
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        gitLink = inputTextField("Git link");
        instagramLink = inputTextField("Instagram link");
        bio = updateProfileBio();



        gridPane.add(gridInputLabel("Name and username"), 0, 0);
        gridPane.add(inputTextField("Name and username"), 1, 0);
        gridPane.add(gridInputLabel("Git link"), 0, 1);
        gridPane.add(gitLink, 1, 1);
        gridPane.add(gridInputLabel("Instagram link"), 0, 2);
        gridPane.add(instagramLink, 1, 2);
        gridPane.add(gridInputLabel("Update bio"), 0, 3);
        gridPane.add(bio, 1, 3);

        gridPane.add(saveSettingsButton(), 3, 4);

        return gridPane;
    }
    /**
     * Creates a label for grid input with the specified text.
     * @param text the text to be displayed on the label
     * @return a label for grid input
     */
    private static Node gridInputLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("grid-input-label");
        return label;
    }


    /**
     * Creates a text field input with the specified placeholder.
     *
     * @param placeholder the text to be displayed as the placeholder
     * @return a text field input with the specified placeholder
     */
    private static TextField inputTextField(String placeholder) {
        TextField textField = new TextField();
        textField.setPromptText(placeholder);
        textField.setId("input-text-field");
        textField.setBackground(new Background(new BackgroundFill(Paint.valueOf("#24273f"), CornerRadii.EMPTY, Insets.EMPTY)));
        textField.setMinWidth(400);
        textField.setMaxWidth(400);
        textField.setMinHeight(25);
        textField.setMaxHeight(25);
        return textField;

    }
    /**
     * Creates a TextArea for updating user bio with specific dimensions and prompt text.
     */
    private static TextArea updateProfileBio() {
        TextArea textArea = new TextArea();
        textArea.setPromptText("Update your bio");
        textArea.setMinWidth(400);
        textArea.setMaxWidth(400);
        textArea.setMinHeight(50);
        textArea.setMaxHeight(50);

        return textArea;
    }
    /**
     * Creates a button for saving settings. When clicked, it retrieves input from text fields and sends a profile change request.
     * @return save settings button
     */
    private static Node saveSettingsButton() {
        Button button = new Button();
        button.getStyleClass().add("save-settings-button");
        button.setText("Save");
        button.setOnAction(actionEvent -> {
            String gitString = gitLink.getText();
            String instaString = instagramLink.getText();
            String bioString = bio.getText();
            Client.trySend(new ProfileChangeRequest(gitString, instaString, bioString));
        });
        return button;
    }
    /**
     * Creates a label for the profile picture.
     *
     * @return the label for the profile picture
     */
    private static Label profilePictureLabel() {
        Label label = new Label("Profile picture:");
        label.getStyleClass().add("profile-picture-label");

        return label;
    }
    /**
     * Generates the profile picture placeholder with dimensions 200x200 and returns it as an ImageView object.
     * @return the created profile picture placeholder
     */
    private static ImageView profilePicturePlaceholder200x200() {
        Image image = new Image("https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png");
        imageView200x200 = new ImageView(image);
        imageView200x200.setFitHeight(200);
        imageView200x200.setFitWidth(200);

        return imageView200x200;
    }
    /**
     * Creates a placeholder profile picture with dimensions 100x100.
     * @return the ImageView object representing the placeholder picture
     */
    private static ImageView profilePicturePlaceholder100x100() {
        Image image = new Image("https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png");
        imageView100x100 = new ImageView(image);
        imageView100x100.setFitHeight(100);
        imageView100x100.setFitWidth(100);

        return imageView100x100;


    }
    /**
     * Generates a placeholder profile picture with dimensions 50x50.
     * @return the ImageView object representing the placeholder picture
     */
    private static ImageView profilePicturePlaceholder50x50() {
        Image image = new Image("https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_1280.png");
        imageView50x50 = new ImageView(image);
        imageView50x50.setFitHeight(50);
        imageView50x50.setFitWidth(50);

        return imageView50x50;

    }
    /**
     * Generates a profile pictures panel with placeholders of different sizes.
     *
     * @return         	the HBox profile pictures panel
     */
    private static HBox profilePicturesPanel() {
        HBox profilePicturesPanel = new HBox();
        profilePicturesPanel.getStyleClass().add("profile-pictures-panel");
        ImageView profilePicturePlaceholder200x200 = profilePicturePlaceholder200x200();
        ImageView profilePicturePlaceholder50x50 = profilePicturePlaceholder50x50();
        ImageView profilePicturePlaceholder100x100 = profilePicturePlaceholder100x100();

        profilePicturesPanel.getChildren().addAll(profilePicturePlaceholder200x200,profilePicturePlaceholder100x100,profilePicturePlaceholder50x50);
        profilePicturesPanel.setAlignment(Pos.BASELINE_CENTER);
        profilePicturesPanel.setSpacing(10);

        return profilePicturesPanel;

    }
    /**
     * Creates and returns a Button for importing a photo.
     *
     * @return the Button for importing a photo
     */
    private static Button importPhotoButton() {
        Button button = new Button();
        button.getStyleClass().add("import-photo-button");
        button.setText("Import photo");

        return button;
    }
    /**
     * Imports a photo by opening a file chooser dialog and returning the selected image.
     *
     * @return the imported Image, or null if no image was selected
     */
    private static Image importPhoto(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png"));
        File file = fileChooser.showOpenDialog(stage);
        Image image = null;
        if (file != null) {
            image = new Image(file.toURI().toString());
        }
        return image;
    }





}
