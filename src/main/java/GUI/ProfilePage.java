package GUI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import networking.Client;
import networking.packages.FollowRequest;

import java.awt.Desktop;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public class ProfilePage extends Scena {
    private static Stage stage;
    public static ProfilePage instance;

    public static int databaseId;
    public static String username;
    public static String gitLink;
    public static String instagramLink;
    public static String bio;

    public static Label usernameLabel;
    public static TextArea profileBio;

    static {
        try {
            instance = new ProfilePage();
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
        HBox shell = new HBox();
        shell.getStyleClass().add("border-pane");

        CustomMenuBar menuBar = new CustomMenuBar(
                "inactive-button-menu-bar",
                "active-button-menu-bar",
                "inactive-button-menu-bar",
                "inactive-button-menu-bar",
                "inactive-button-menu-bar"
        );

        VBox content = profileContent();
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("timeline-scroll");
        HBox.setHgrow(scrollPane, Priority.ALWAYS);

        shell.getChildren().addAll(menuBar.getCustomMenuBar(), scrollPane);
        return shell;
    }

    private static VBox profileContent() throws IOException {
        VBox content = new VBox(16);
        content.getStyleClass().addAll("main-part", "demo-page");
        content.setPadding(new Insets(18));

        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        usernameLabel = new Label(resolveUsername());
        usernameLabel.getStyleClass().add("feed-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button settingsButton = settingsButton();
        header.getChildren().addAll(usernameLabel, spacer, settingsButton);

        FlowPane cards = new FlowPane();
        cards.setHgap(16);
        cards.setVgap(16);
        cards.setPrefWrapLength(900);
        cards.getChildren().addAll(profileCard(), socialCard());

        content.getChildren().addAll(header, cards);
        return content;
    }

    private static VBox profileCard() throws IOException {
        VBox card = new VBox(12);
        card.getStyleClass().add("demo-card");
        card.setPadding(new Insets(18));
        card.setPrefWidth(360);

        ImageView avatar = new ImageView(new Image(new FileInputStream("assets/logos/isumbg.png")));
        avatar.setFitWidth(160);
        avatar.setFitHeight(160);
        avatar.setPreserveRatio(true);

        Label handle = new Label("@" + resolveUsername());
        handle.getStyleClass().add("demo-subtitle");

        Button follow = followButton();
        card.getChildren().addAll(avatar, handle, follow);
        return card;
    }

    private static VBox socialCard() throws FileNotFoundException {
        VBox card = new VBox(12);
        card.getStyleClass().add("demo-card");
        card.setPadding(new Insets(18));
        card.setPrefWidth(520);

        Label about = new Label("About");
        about.getStyleClass().add("demo-card-title");

        profileBio = new TextArea();
        profileBio.getStyleClass().add("profile-bio");
        profileBio.setWrapText(true);
        profileBio.setEditable(false);
        profileBio.setPrefRowCount(5);
        profileBio.setText((bio == null || bio.isBlank()) ? "No bio yet. Update it in Settings." : bio);

        HBox links = new HBox(10);
        Button githubButton = socialButton("GitHub", "assets/icons/person.png", gitLink);
        Button instagramButton = socialButton("Instagram", "assets/icons/person.png", instagramLink);
        links.getChildren().addAll(githubButton, instagramButton);

        card.getChildren().addAll(about, profileBio, links);
        return card;
    }

    private static Button followButton() {
        Button button = new Button("Follow");
        button.getStyleClass().add("follow-button");
        button.setOnAction(e -> Client.trySend(new FollowRequest(databaseId)));
        return button;
    }

    private static Button socialButton(String title, String iconPath, String url) throws FileNotFoundException {
        Button button = GuiUtil.createButtonTextIcon(title, title.toLowerCase() + "-button", iconPath);
        if (url == null || url.isBlank()) {
            button.setDisable(true);
            button.setTooltip(new Tooltip("Link not set"));
            return button;
        }
        button.setOnAction(a -> {
            try {
                Desktop.getDesktop().browse(new URI(url));
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
        return button;
    }

    private static Button settingsButton() throws FileNotFoundException {
        Button settingsButton = new Button();
        settingsButton.getStyleClass().add("settings-button");
        settingsButton.setGraphic(GuiUtil.createButtonIcon("assets/icons/person.png"));
        settingsButton.setOnAction(e -> {
            stage.setScene(UserSettingsPage.instance);
            GuiUtil.relocate(UserSettingsPage.instance);
        });
        GuiUtil.applyScaleTransition(settingsButton, 0.95, 0.95, 200, 300);
        return settingsButton;
    }

    private static String resolveUsername() {
        if (username == null || username.isBlank()) {
            return Client.getUsername() != null ? Client.getUsername() : "student";
        }
        return username;
    }

    public static void setPrimaryStage(Stage stage) {
        ProfilePage.stage = stage;
    }

    public static Stage getPrimaryStage() {
        return stage;
    }
}
