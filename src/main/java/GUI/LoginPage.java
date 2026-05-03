package GUI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import networking.Client;
import networking.DatabaseUtil;
import networking.packages.PostRequest;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

public class LoginPage extends Scena {

    private static Stage stage;
    public static LoginPage instance;

    private static final TextField usernameField = new TextField();
    private static final PasswordField passwordField = new PasswordField();

    static {
        try {
            instance = new LoginPage();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public LoginPage() throws FileNotFoundException {
        super(root());
        this.getStylesheets().add((Objects.requireNonNull(this.getClass().getResource("/login-page.css"))).toExternalForm());
        if (LoginPage.instance == null) {
            LoginPage.instance = this;
        }
    }

    private static Parent root() throws FileNotFoundException {
        HBox root = new HBox();
        root.getStyleClass().add("login-root");

        StackPane brandPane = new StackPane();
        brandPane.getStyleClass().add("login-brand-pane");
        brandPane.setMinWidth(420);

        ImageView campusVisual = GuiUtil.createIcon("assets/logos/isumbg.png");
        campusVisual.setPreserveRatio(true);
        campusVisual.setFitWidth(520);
        campusVisual.setOpacity(0.35);

        VBox brandContent = new VBox(12);
        brandContent.setAlignment(Pos.CENTER_LEFT);
        brandContent.setPadding(new Insets(38));

        ImageView logo = (ImageView) GuiUtil.logo();
        logo.setPreserveRatio(true);
        logo.setFitWidth(240);
        logo.setFitHeight(110);

        Label title = new Label("Campus Social Network");
        title.getStyleClass().add("login-brand-title");

        Label subtitle = new Label("Connect with students, share updates, and follow events around your faculty.");
        subtitle.getStyleClass().add("login-brand-subtitle");
        subtitle.setWrapText(true);
        subtitle.setMaxWidth(420);

        brandContent.getChildren().addAll(logo, title, subtitle);
        brandPane.getChildren().addAll(campusVisual, brandContent);
        StackPane.setAlignment(brandContent, Pos.CENTER_LEFT);

        VBox formPane = new VBox();
        formPane.getStyleClass().add("login-form-pane");
        formPane.setAlignment(Pos.CENTER);
        formPane.setPadding(new Insets(20));
        formPane.setMinWidth(360);

        VBox card = new VBox(14);
        card.getStyleClass().add("login-card");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(28));
        card.setMaxWidth(410);

        Label cardTitle = new Label("Welcome Back");
        cardTitle.getStyleClass().add("login-card-title");

        Label cardSubtitle = new Label("Sign in with your campus account.");
        cardSubtitle.getStyleClass().add("login-card-subtitle");

        usernameField.setPromptText("Username");
        usernameField.getStyleClass().add("login-input");

        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("login-input");

        Button loginBtn = buildLoginButton();
        card.getChildren().addAll(cardTitle, cardSubtitle, usernameField, passwordField, loginBtn);
        formPane.getChildren().add(card);

        root.getChildren().addAll(brandPane, formPane);
        HBox.setHgrow(brandPane, Priority.ALWAYS);
        HBox.setHgrow(formPane, Priority.ALWAYS);

        root.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            boolean showBrand = newWidth.doubleValue() > 980;
            brandPane.setManaged(showBrand);
            brandPane.setVisible(showBrand);
        });

        return root;
    }

    private static Button buildLoginButton() throws FileNotFoundException {
        Button loginBtn = GuiUtil.createButtonTextIcon("Sign In", "loginBtn", "assets/icons/person.png");
        GuiUtil.buttonScaleTransition(loginBtn);

        loginBtn.setOnAction(actionEvent -> {
            final String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
            final String password = passwordField.getText() == null ? "" : passwordField.getText();

            if (username.isEmpty() || password.isEmpty()) {
                showInfo("Missing fields", "Please enter both username and password.");
                return;
            }

            try {
                DatabaseUtil.connect();
                DatabaseUtil.addUser(username, password);
                Client.connect(username, password);
            } catch (IOException e) {
                showInfo("Connection issue", "Unable to reach server. Please make sure server is running.");
                return;
            }

            if (!Client.isLoggedIn()) {
                showInfo("Login failed", "Username or password is incorrect.");
                return;
            }

            Client.trySend(new PostRequest(""));
            passwordField.clear();
            stage.setScene(Feed.instance2);
            GuiUtil.relocate(Feed.instance2);
        });

        return loginBtn;
    }

    private static void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void setPrimaryStage(Stage stage) {
        LoginPage.stage = stage;
    }
}
