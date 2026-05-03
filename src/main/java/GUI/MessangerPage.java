package GUI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.util.Objects;

public class MessangerPage extends Scena {
    private static Stage stage;
    public static MessangerPage instance;

    static {
        try {
            instance = new MessangerPage();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public MessangerPage() throws FileNotFoundException {
        super(root());
        this.getStylesheets().add((Objects.requireNonNull(this.getClass().getResource("/messanger-page.css"))).toExternalForm());
        if (MessangerPage.instance == null) {
            MessangerPage.instance = this;
        }

    }
    /**
     * A description of the entire Java function.
     *
     * @return         description of return value
     */
    private static Parent root() throws FileNotFoundException {
        HBox hBox = new HBox();
        hBox.getStyleClass().add("border-pane");
        CustomMenuBar menuBar = new CustomMenuBar("inactive-button-menu-bar",
                "inactive-button-menu-bar", "active-button-menu-bar",
                "inactive-button-menu-bar","inactive-button-menu-bar");
        AnchorPane anchorPane = menuBar.getCustomMenuBar();
        Node main = messengerMain();
        hBox.getChildren().addAll(anchorPane, main);
        HBox.setHgrow(main, Priority.ALWAYS);
        return hBox;
    }

    private static Node messengerMain() {
        BorderPane main = new BorderPane();
        main.getStyleClass().addAll("main-part", "demo-page");
        main.setPadding(new Insets(28));

        VBox leftSidebar = new VBox();
        leftSidebar.getStyleClass().add("demo-card");
        leftSidebar.setPadding(new Insets(16));
        leftSidebar.setSpacing(10);
        leftSidebar.setMinWidth(240);
        leftSidebar.setPrefWidth(290);

        Label leftTitle = new Label("Conversations");
        leftTitle.getStyleClass().add("demo-card-title");
        leftSidebar.getChildren().add(leftTitle);

        leftSidebar.getChildren().addAll(
                convoButton("Project Team", "Last message: Let's deploy tonight"),
                convoButton("Frontend Squad", "Last message: Updated styles are live"),
                convoButton("Mentor Group", "Last message: Great progress this week")
        );

        VBox chatView = new VBox();
        chatView.setSpacing(12);
        chatView.setPadding(new Insets(18));
        chatView.getStyleClass().add("demo-card");

        Label chatTitle = new Label("Project Team");
        chatTitle.getStyleClass().add("demo-card-title");

        VBox chatMessages = new VBox();
        chatMessages.setSpacing(10);
        chatMessages.getChildren().addAll(
                bubble("Ana", "Can we ship the new profile page tonight?", false),
                bubble("You", "Yes. I wrapped up the demo screens too.", true),
                bubble("Marko", "Perfect, let's test login flow once more.", false)
        );

        ScrollPane scrollPane = new ScrollPane(chatMessages);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("chat-scroll");
        scrollPane.setMinHeight(360);

        TextArea compose = new TextArea();
        compose.setPromptText("Write a message...");
        compose.getStyleClass().add("chat-compose");
        compose.setMinHeight(90);

        Button send = new Button("Send");
        send.getStyleClass().add("demo-primary-button");
        GuiUtil.buttonScaleTransition(send);

        chatView.getChildren().addAll(chatTitle, scrollPane, compose, send);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        chatView.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(chatView, Priority.ALWAYS);

        HBox center = new HBox(leftSidebar, chatView);
        center.setSpacing(18);
        center.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            boolean compact = newWidth.doubleValue() < 940;
            leftSidebar.setManaged(!compact);
            leftSidebar.setVisible(!compact);
        });
        main.setCenter(center);

        return main;
    }

    private static Button convoButton(String title, String subtitle) {
        Button button = new Button(title + "\n" + subtitle);
        button.setWrapText(true);
        button.getStyleClass().add("demo-list-button");
        button.setAlignment(Pos.CENTER_LEFT);
        button.setMaxWidth(Double.MAX_VALUE);
        return button;
    }

    private static HBox bubble(String sender, String message, boolean mine) {
        HBox row = new HBox();
        row.setAlignment(mine ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        Label label = new Label(sender + ": " + message);
        label.getStyleClass().add(mine ? "chat-bubble-mine" : "chat-bubble");
        label.setWrapText(true);
        row.getChildren().add(label);
        return row;
    }

    /**
     * Set the primary stage for the MessangerPage.
     *
     * @param  stage  the primary stage to be set
     */
    public static void setPrimaryStage(Stage stage) {
        MessangerPage.stage = stage;
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
