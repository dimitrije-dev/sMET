package GUI;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.util.Objects;

public class EventsPage extends Scena {
    private static Stage stage;
    public static EventsPage instance;

    static {
        try {
            instance = new EventsPage();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public EventsPage() throws FileNotFoundException {
        super(root());
        this.getStylesheets().add((Objects.requireNonNull(this.getClass().getResource("/feed-page.css"))).toExternalForm());
        if (EventsPage.instance == null) {
            EventsPage.instance = this;
        }
    }

    private static Parent root() throws FileNotFoundException {
        HBox hBox = new HBox();
        hBox.getStyleClass().add("border-pane");
        CustomMenuBar menuBar = new CustomMenuBar("inactive-button-menu-bar",
                "inactive-button-menu-bar", "inactive-button-menu-bar",
                "active-button-menu-bar", "inactive-button-menu-bar");
        AnchorPane anchorPane = menuBar.getCustomMenuBar();

        VBox contentPane = contentPane();
        ScrollPane scrollPane = new ScrollPane(contentPane);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("timeline-scroll");

        hBox.getChildren().addAll(anchorPane, scrollPane);
        HBox.setHgrow(scrollPane, Priority.ALWAYS);
        return hBox;
    }

    private static VBox contentPane() {
        VBox content = new VBox();
        content.getStyleClass().addAll("main-part", "demo-page");
        content.setFillWidth(true);
        content.setPadding(new Insets(36));
        content.setSpacing(20);

        Label title = new Label("Events Demo");
        title.getStyleClass().add("demo-title");

        Label subtitle = new Label("Plan your semester with curated events and workshops.");
        subtitle.getStyleClass().add("demo-subtitle");

        FlowPane cardsRow = new FlowPane();
        cardsRow.setPrefWrapLength(980);
        cardsRow.setHgap(16);
        cardsRow.setVgap(16);

        cardsRow.getChildren().addAll(
                eventCard("AI Career Night", "May 9, 18:00", "Industry speakers and networking with recruiters."),
                eventCard("Hackathon Sprint", "May 14, 10:00", "Build a prototype in teams and pitch to mentors."),
                eventCard("Exam Strategy Lab", "May 20, 16:00", "Study planning session with top-performing students.")
        );

        content.getChildren().addAll(title, subtitle, cardsRow);
        return content;
    }

    private static VBox eventCard(String titleText, String timeText, String bodyText) {
        VBox card = new VBox();
        card.getStyleClass().add("demo-card");
        card.setSpacing(10);
        card.setPadding(new Insets(16));
        card.setAlignment(Pos.TOP_LEFT);
        card.setMinWidth(280);
        card.setMaxWidth(280);

        Label title = new Label(titleText);
        title.getStyleClass().add("demo-card-title");

        Label time = new Label(timeText);
        time.getStyleClass().add("demo-card-meta");

        Label body = new Label(bodyText);
        body.getStyleClass().add("demo-card-body");
        body.setWrapText(true);

        Button cta = new Button("Join Event");
        cta.getStyleClass().add("demo-primary-button");
        GuiUtil.buttonScaleTransition(cta);

        card.getChildren().addAll(title, time, body, cta);
        return card;
    }

    public static void setPrimaryStage(Stage stage) {
        EventsPage.stage = stage;
    }

    public static Stage getPrimaryStage() {
        return stage;
    }
}
