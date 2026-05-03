package GUI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import networking.Client;
import networking.packages.ProfileChangeRequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;

public class UserSettingsPage extends Scena {
    private static Stage stage;
    public static UserSettingsPage instance;

    private static ImageView imageView200x200;
    private static ImageView imageView50x50;
    private static ImageView imageView100x100;

    private static TextField gitLink;
    private static TextArea bio;
    private static TextField instagramLink;
    private static TextField displayName;

    static {
        try {
            instance = new UserSettingsPage();
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
        HBox shell = new HBox();
        shell.getStyleClass().add("border-pane");

        CustomMenuBar menuBar = new CustomMenuBar(
                "inactive-button-menu-bar",
                "active-button-menu-bar",
                "inactive-button-menu-bar",
                "inactive-button-menu-bar",
                "inactive-button-menu-bar"
        );

        VBox content = settingsContent();
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("timeline-scroll");
        HBox.setHgrow(scrollPane, Priority.ALWAYS);

        shell.getChildren().addAll(menuBar.getCustomMenuBar(), scrollPane);
        return shell;
    }

    private static VBox settingsContent() {
        VBox content = new VBox(16);
        content.getStyleClass().addAll("main-part", "demo-page");
        content.setPadding(new Insets(18));

        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Profile Settings");
        title.getStyleClass().add("feed-title");
        header.getChildren().add(title);

        FlowPane body = new FlowPane();
        body.setHgap(16);
        body.setVgap(16);
        body.setPrefWrapLength(980);
        body.getChildren().addAll(settingsFormCard(), profilePicturesCard());

        content.getChildren().addAll(header, body);
        return content;
    }

    private static VBox settingsFormCard() {
        VBox card = new VBox(12);
        card.getStyleClass().add("demo-card");
        card.setPadding(new Insets(18));
        card.setPrefWidth(560);

        Label subtitle = new Label("Update your public profile details.");
        subtitle.getStyleClass().add("demo-subtitle");

        displayName = inputTextField("Display name");
        displayName.setText(ProfilePage.username != null ? ProfilePage.username : "");
        gitLink = inputTextField("GitHub profile link");
        instagramLink = inputTextField("Instagram profile link");
        bio = updateProfileBio();

        Button saveButton = saveSettingsButton();
        HBox actionRow = new HBox(saveButton);
        actionRow.setAlignment(Pos.CENTER_RIGHT);

        card.getChildren().addAll(
                fieldGroup("Display name", displayName),
                fieldGroup("GitHub", gitLink),
                fieldGroup("Instagram", instagramLink),
                fieldGroup("Bio", bio),
                subtitle,
                actionRow
        );
        return card;
    }

    private static VBox profilePicturesCard() {
        VBox card = new VBox(10);
        card.getStyleClass().add("demo-card");
        card.setPadding(new Insets(18));
        card.setAlignment(Pos.TOP_CENTER);
        card.setPrefWidth(320);

        Label label = new Label("Profile Photos");
        label.getStyleClass().add("demo-card-title");

        imageView200x200 = profilePicturePlaceholder200x200();
        imageView100x100 = profilePicturePlaceholder100x100();
        imageView50x50 = profilePicturePlaceholder50x50();

        HBox previewRow = new HBox(10, imageView100x100, imageView50x50);
        previewRow.setAlignment(Pos.CENTER);

        Button importButton = importPhotoButton();
        importButton.setOnAction(actionEvent -> {
            Image imported = importPhoto();
            if (imported != null) {
                imageView200x200.setImage(imported);
                imageView100x100.setImage(imported);
                imageView50x50.setImage(imported);
            }
        });

        card.getChildren().addAll(label, imageView200x200, previewRow, importButton);
        return card;
    }

    private static VBox fieldGroup(String labelText, Region input) {
        VBox group = new VBox(6);
        Label label = new Label(labelText);
        label.getStyleClass().add("grid-input-label");
        input.setMaxWidth(Double.MAX_VALUE);
        VBox.setVgrow(input, Priority.NEVER);
        group.getChildren().addAll(label, input);
        return group;
    }

    private static TextField inputTextField(String placeholder) {
        TextField textField = new TextField();
        textField.setPromptText(placeholder);
        textField.setId("input-text-field");
        textField.setMinHeight(36);
        return textField;
    }

    private static TextArea updateProfileBio() {
        TextArea textArea = new TextArea();
        textArea.setPromptText("Write a short campus bio...");
        textArea.setPrefRowCount(4);
        textArea.setWrapText(true);
        return textArea;
    }

    private static Button saveSettingsButton() {
        Button button = new Button("Save changes");
        button.getStyleClass().add("save-settings-button");
        button.setOnAction(actionEvent -> {
            String gitString = gitLink.getText();
            String instaString = instagramLink.getText();
            String bioString = bio.getText();
            Client.trySend(new ProfileChangeRequest(gitString, instaString, bioString));
            ProfilePage.username = displayName.getText().isBlank() ? ProfilePage.username : displayName.getText().trim();
        });
        return button;
    }

    private static ImageView profilePicturePlaceholder200x200() {
        Image image = new Image(Paths.get("assets/icons/avatar-48x48.png").toUri().toString());
        ImageView view = new ImageView(image);
        view.setFitHeight(200);
        view.setFitWidth(200);
        return view;
    }

    private static ImageView profilePicturePlaceholder100x100() {
        Image image = new Image(Paths.get("assets/icons/female-avatar.png").toUri().toString());
        ImageView view = new ImageView(image);
        view.setFitHeight(100);
        view.setFitWidth(100);
        return view;
    }

    private static ImageView profilePicturePlaceholder50x50() {
        Image image = new Image(Paths.get("assets/icons/male-avatar.png").toUri().toString());
        ImageView view = new ImageView(image);
        view.setFitHeight(50);
        view.setFitWidth(50);
        return view;
    }

    private static Button importPhotoButton() {
        Button button = new Button("Import photo");
        button.getStyleClass().add("import-photo-button");
        return button;
    }

    private static Image importPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose profile image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png"));
        File file = fileChooser.showOpenDialog(stage);
        if (file == null) return null;
        return new Image(file.toURI().toString());
    }

    public static void setPrimaryStage(Stage stage) {
        UserSettingsPage.stage = stage;
    }

    public static Stage getPrimaryStage() {
        return stage;
    }
}
