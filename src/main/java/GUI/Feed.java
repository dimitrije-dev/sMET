package GUI;

import Posts.TextPost;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import networking.Client;
import networking.DatabaseUtil;
import networking.packages.FeedUpdateRequest;
import networking.packages.PostRequest;
import networking.packages.ProfileInfoRequest;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;

public class Feed extends Scena {
    private static Stage stage;

    private static VBox postPart;
    private static VBox rightRail;

    public static Feed instance2;
    private static final ListView<String> listView = new ListView<>();
    private static final TextArea textArea = new TextArea();

    static {
        try {
            instance2 = new Feed();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Feed() throws FileNotFoundException {
        super(root());
        this.getStylesheets().add((Objects.requireNonNull(this.getClass().getResource("/feed-page.css"))).toExternalForm());
        if (Feed.instance2 == null) {
            Feed.instance2 = this;
        }
    }

    private static Parent root() throws FileNotFoundException {
        HBox shell = new HBox();
        shell.getStyleClass().add("border-pane");

        CustomMenuBar menuBar = new CustomMenuBar(
                "active-button-menu-bar",
                "inactive-button-menu-bar",
                "inactive-button-menu-bar",
                "inactive-button-menu-bar",
                "inactive-button-menu-bar"
        );

        VBox stream = buildStream();
        ScrollPane streamScroll = new ScrollPane(stream);
        streamScroll.getStyleClass().add("timeline-scroll");
        streamScroll.setFitToWidth(true);
        streamScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        streamScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        rightRail = buildRightRail();

        HBox content = new HBox(streamScroll, rightRail);
        content.setSpacing(18);
        content.setPadding(new Insets(18));
        HBox.setHgrow(streamScroll, Priority.ALWAYS);
        streamScroll.setMaxWidth(Double.MAX_VALUE);

        shell.getChildren().addAll(menuBar.getCustomMenuBar(), content);
        HBox.setHgrow(content, Priority.ALWAYS);

        shell.widthProperty().addListener((obs, oldVal, newVal) -> {
            boolean showRail = newVal.doubleValue() > 1250;
            rightRail.setVisible(showRail);
            rightRail.setManaged(showRail);
        });

        return shell;
    }

    private static VBox buildStream() throws FileNotFoundException {
        VBox stream = new VBox(16);
        stream.getStyleClass().add("main-part");
        stream.setFillWidth(true);
        stream.setPadding(new Insets(8, 0, 24, 0));
        stream.setMaxWidth(840);
        stream.setPrefWidth(840);

        stream.getChildren().addAll(
                feedHero(),
                searchBar(),
                listOfUsers(),
                postComposer(),
                defaultPost(),
                postPart()
        );
        return stream;
    }

    private static VBox postComposer() throws FileNotFoundException {
        VBox composer = new VBox(10);
        composer.getStyleClass().add("post-composer");

        textArea.setPromptText("Share update with your faculty...");
        textArea.setId("text-area-main");
        textArea.setPrefRowCount(3);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);

        HBox actions = new HBox();
        actions.setAlignment(Pos.CENTER_RIGHT);
        Button sendButton = GuiUtil.createButtonTextIcon("Share", "send-button", "assets/icons/send.png");
        sendButton.getStyleClass().add("send-button");
        GuiUtil.buttonScaleTransition(sendButton);
        sendButton.setOnAction(actionEvent -> post());
        actions.getChildren().add(sendButton);

        composer.getChildren().addAll(textArea, actions);
        return composer;
    }

    private static TextField searchBar() {
        TextField searchBar = new TextField();
        searchBar.setPromptText("Search students, clubs and tags...");
        searchBar.getStyleClass().add("campus-search");
        searchBar.setMaxWidth(Double.MAX_VALUE);
        searchBar.setOnKeyTyped(event -> updateListOfUsers(searchBar));
        return searchBar;
    }

    private static void updateListOfUsers(TextField searchBar) {
        String input = searchBar.getText();
        if (input == null || input.isBlank()) {
            listView.setPrefHeight(0);
            listView.getItems().clear();
            return;
        }

        ArrayList<String> users = DatabaseUtil.findUserFromSearchBar(input.trim());
        listView.getItems().clear();
        for (String user : users) {
            listView.getItems().add("@" + user);
        }
        listView.setPrefHeight(Math.max(40, users.size() * 30.0));
    }

    private static ListView<String> listOfUsers() {
        listView.setPrefHeight(0);
        listView.getStyleClass().add("list-view");
        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !listView.getSelectionModel().isEmpty()) {
                String selectedItem = listView.getSelectionModel().getSelectedItem();
                if (selectedItem == null) return;
                Client.trySend(new ProfileInfoRequest(selectedItem.substring(1)));
                stage.setScene(ProfilePage.instance);
                GuiUtil.relocate(ProfilePage.instance);
            }
        });
        return listView;
    }

    private static VBox buildRightRail() {
        VBox rail = new VBox(14);
        rail.getStyleClass().addAll("second-part", "right-rail");
        rail.setPadding(new Insets(6, 2, 2, 2));
        rail.setPrefWidth(280);
        rail.setMinWidth(260);

        rail.getChildren().addAll(
                profileBox("assets/icons/avatar-48x48.png", "@admin"),
                railCard("Trending on Campus", "#cs202-project", "#exam-week", "#ai-lab"),
                railCard("Upcoming", "Hackathon · May 14", "Career Night · May 17", "Math Clinic · May 20"),
                railCard("Who to Follow", "@frontend-club", "@met-buddy", "@career-center")
        );
        return rail;
    }

    private static VBox postPart() {
        postPart = new VBox(14);
        postPart.setPadding(new Insets(4, 0, 0, 0));
        postPart.setFillWidth(true);
        postPart.getStyleClass().add("post-part");
        return postPart;
    }

    public static void setPrimaryStage(Stage stage) {
        Feed.stage = stage;
    }

    public static Stage getPrimaryStage() {
        return stage;
    }

    public static void post() {
        String content = textArea.getText();
        if (content == null || content.isBlank()) {
            return;
        }
        Client.trySend(new PostRequest(content.trim()));
    }

    public static void post(String username, String content) {
        try {
            if (content == null || content.isBlank()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Empty post");
                alert.setHeaderText(null);
                alert.setContentText("You can't post an empty status.");
                alert.showAndWait();
                return;
            }

            TextPost textPost = new TextPost(content, username, username, getCurrentDateTime());
            textPost.setMaxWidth(Double.MAX_VALUE);
            postPart.getChildren().add(textPost);
            textArea.clear();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void updatePostFeed(ArrayList<FeedUpdateRequest.CPair> posts) {
        postPart.getChildren().clear();
        for (FeedUpdateRequest.CPair post : posts) {
            post(post.key, post.value);
        }
    }

    private static String getCurrentDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }

    private static TextPost defaultPost() throws FileNotFoundException {
        return new TextPost(
                "Welcome to the campus timeline. Share project updates, exam tips and event invites.",
                "sMET Team",
                "@admin",
                getCurrentDateTime()
        );
    }

    public static HBox profileBox(String profilePictureAvatarPath, String username) {
        HBox hBox = new HBox(10);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(10));
        hBox.getStyleClass().add("profile-box");

        try {
            hBox.getChildren().add(GuiUtil.createIcon(profilePictureAvatarPath));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        Label label = new Label(username);
        label.setId("label-white");
        label.setFont(Font.font("Arial", FontWeight.BOLD, 17));
        hBox.getChildren().add(label);

        GuiUtil.applyScaleTransition(hBox, 0.98, 0.98, 180, 220);
        hBox.setOnMouseClicked(mouseEvent -> {
            stage.setScene(ProfilePage.instance);
            GuiUtil.relocate(ProfilePage.instance);
        });
        return hBox;
    }

    private static VBox feedHero() {
        VBox hero = new VBox(4);
        hero.getStyleClass().add("feed-hero");

        Label title = new Label("Campus Timeline");
        title.getStyleClass().add("feed-title");

        Label subtitle = new Label("Your university in real time: notes, projects, events and opportunities.");
        subtitle.getStyleClass().add("feed-subtitle");
        subtitle.setWrapText(true);

        hero.getChildren().addAll(title, subtitle);
        return hero;
    }

    private static VBox railCard(String title, String... items) {
        VBox card = new VBox(8);
        card.getStyleClass().add("rail-card");

        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("rail-title");
        card.getChildren().add(titleLabel);

        for (String item : items) {
            Label itemLabel = new Label(item);
            itemLabel.getStyleClass().add("rail-item");
            itemLabel.setWrapText(true);
            card.getChildren().add(itemLabel);
        }
        return card;
    }
}
