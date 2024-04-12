package GUI;

import Posts.TextPost;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.effect.Glow;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Pair;
import networking.Client;
import networking.DatabaseUtil;
import networking.packages.FeedUpdateRequest;
import networking.packages.PostRequest;
import networking.packages.ProfileInfoRequest;

import java.awt.*;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;


public class Feed extends Scena {
    private static Stage stage;
    private static final Rectangle2D bounds = screen.getVisualBounds();

    private static  VBox postPart;

    public static Feed instance2;
    private static ListView<String> listView = new ListView<>();
    private static final VBox mainPart = new VBox();


    private static ArrayList<String> usernamesOfSearch = null;

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
    /**
     * A description of the entire Java function.
     *
     * @return         	description of return value
     */
    private static Parent root() throws FileNotFoundException {
        HBox hBox = new HBox();
        hBox.getStyleClass().add("border-pane");
        CustomMenuBar menuBar = new CustomMenuBar("active-button-menu-bar", "inactive-button-menu-bar", "inactive-button-menu-bar", "inactive-button-menu-bar", "inactive-button-menu-bar");
        AnchorPane anchorPane = menuBar.getCustomMenuBar();
        hBox.getChildren().addAll(anchorPane, mainPart(), secondPart());


        return hBox;
    }

    /**
     * This function creates and configures the main part of the UI, including setting dimensions, spacing, alignment, style class, and content.
     *
     * @return         	the configured ScrollPane representing the main part of the UI
     */
    private static ScrollPane mainPart() throws FileNotFoundException {

        mainPart.setMinWidth(bounds.getWidth() * 0.625);
        mainPart.setMaxWidth(bounds.getWidth() * 0.625);
        mainPart.setSpacing(20);
        mainPart.setAlignment(Pos.TOP_CENTER);
        ScrollPane scrollPane = new ScrollPane(mainPart);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        mainPart.getStyleClass().add("main-part");
        mainPart.setMinHeight(1000);

        scrollPane.setMinWidth(bounds.getWidth() * 0.625);
        scrollPane.setMaxWidth(bounds.getWidth() * 0.625);

        AnchorPane anchorPane = postMakerArea();
        anchorPane.setMaxWidth(600);
        anchorPane.setMinWidth(600);

        anchorPane.setMinHeight(130);
        anchorPane.setMaxHeight(130);

        mainPart.getChildren().addAll(searchBar(), listOfUsers(), anchorPane, defaultPost(),postPart());


        return scrollPane;
    }
    /**
     * A description of the entire Java function.
     *
     * @return         description of return value
     */
    private static TextField searchBar() {
        TextField searchBar = new TextField();
        searchBar.setMaxWidth(600);
        searchBar.setMinWidth(600);
        searchBar.setMinHeight(50);
        searchBar.setMaxHeight(50);
        searchBar.setPromptText("Search sMet");
        searchBar.setOnKeyTyped(event -> {
            updateListOfUsers(searchBar);
        });
        return searchBar;
    }
    /**
     * Updates the list of users based on the input from the search bar.
     *
     * @param  searchBar    the TextField representing the search bar input
     */
    private static void updateListOfUsers(TextField searchBar) {
        System.out.println(searchBar.getText());
        if (searchBar.getText().isEmpty()) {
            listView.setMinHeight(0);
            listView.getItems().clear();
        } else {
            usernamesOfSearch = DatabaseUtil.findUserFromSearchBar(searchBar.getText());
            System.out.println(usernamesOfSearch);
            for (int j = 0; j < usernamesOfSearch.size(); j++) {
                String user = usernamesOfSearch.get(j);
                if (listView.getItems().contains("@" + user))
                    continue;
                listView.getItems().add("@" + user);

            }
            listView.setMinHeight(usernamesOfSearch.size() * 20);
        }



    }
    /**
     * A description of the entire Java function.
     *
     * @param  paramName	description of parameter
     * @return         	description of return value
     */
    private static Node listOfUsers() {

        listView = new ListView<>();
        listView.setMaxWidth(600);
        listView.setMinWidth(600);
        listView.setMaxHeight(0);

        listView.setBackground(new Background( new BackgroundFill( Paint.valueOf("#66000000"), CornerRadii.EMPTY, Insets.EMPTY ) ) );
//        listView.setOpacity(0.1);
        listView.getStyleClass().add("list-view");
        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !listView.getSelectionModel().isEmpty()) {
                String selectedItem = listView.getSelectionModel().getSelectedItem();
                Client.trySend(new ProfileInfoRequest(selectedItem.substring(1)));
                stage.setScene(ProfilePage.instance);
                GuiUtil.relocate(ProfilePage.instance);
            }
        });

        return listView;
    }

    /**
     * A description of the entire Java function.
     *
     * @param  paramName	description of parameter
     * @return         	description of return value
     */
    private static VBox secondPart() {
        VBox secondPart = new VBox();
        secondPart.getStyleClass().add("second-part");
        secondPart.setMinWidth(bounds.getWidth() * 0.1875);
        secondPart.setMaxWidth(bounds.getWidth() * 0.1875);

        secondPart.getChildren().add(profileBox("/Users/dimimac/INTELLIJ/JAVA II/PROJEKAT/projekat-cs202/assets/icons/avatar-48x48.png", "Admin Team"));


        return secondPart;
    }

    /**
     * Generates a post maker area with a send button and text area,
     * and adds styling to the anchor pane.
     *
     * @return         	the anchor pane for the post maker area
     */
    private static AnchorPane postMakerArea() throws FileNotFoundException {
        AnchorPane anchorPane = new AnchorPane();

        Node sendButton = sendButton();
        Node textArea = textAreaMain();

        anchorPane.getChildren().addAll(textArea, sendButton);

        AnchorPane.setTopAnchor(textArea, 0d);
        AnchorPane.setLeftAnchor(textArea, 0d);

        AnchorPane.setTopAnchor(sendButton, 80d);
        AnchorPane.setRightAnchor(sendButton, 0d);


        anchorPane.getStyleClass().add("post-maker-area");
        return anchorPane;
    }
    /**
     * Sends a button and handles the action event for posting.
     *
     * @return         	the send button
     */
    private static Node sendButton() throws FileNotFoundException {
        Button sendButton = GuiUtil.createButtonTextIcon("POST    ", "send-button", "/Users/dimimac/INTELLIJ/JAVA II/PROJEKAT/projekat-cs202/assets/icons/send.png");
        sendButton.getStyleClass().add("send-button");
        GuiUtil.buttonScaleTransition(sendButton);

        sendButton.setOnAction(actionEvent -> {
            post();
        });
        return sendButton;
    }
    /**
     * A description of the entire Java function.
     *
     * @param  paramName	description of parameter
     * @return         	description of return value
     */
    private static Node textAreaMain() {

        textArea.setId("text-area-main");
        textArea.setPromptText("Write here...");

        textArea.setMinWidth(600);
        textArea.setMaxWidth(600);
        textArea.setMaxHeight(75);

        return textArea;
    }
    /**
     * A private static function that creates and configures a VBox for the post part.
     *
     * @return         	the configured VBox for the post part
     */
    private static VBox postPart() {

        postPart = new VBox();

        postPart.setSpacing(20);
        postPart.setPadding(new Insets(10, 10, 10, 10));
        postPart.setMinWidth(bounds.getWidth() * 0.625);
        postPart.setMaxWidth(bounds.getWidth() * 0.625);

        postPart.setAlignment(Pos.TOP_CENTER);

        postPart.setMinHeight(1000);
        postPart.setBackground(new Background( new BackgroundFill( Paint.valueOf("#66000000"), CornerRadii.EMPTY, Insets.EMPTY ) ) );



        postPart.getStyleClass().add("post-part");

        return postPart;
    }


    /**
     * Sets the primary stage for the application.
     *
     * @param stage the primary stage to be set
     */
    public static void setPrimaryStage(Stage stage) {
        Feed.stage = stage;
    }

    /**
     * Returns the primary stage of the application.
     *
     * @return the primary stage of the application
     */
    public static Stage getPrimaryStage() {
        return stage;
    }
    /**
     * A description of the entire Java function.
     *
     * @param  paramName	description of parameter
     * @return         	description of return value
     */
    public static void post() {
        String content = textArea.getText();
        if (!content.isEmpty()) {
            Client.trySend(new PostRequest(content));
        }

    }
    /**
     * A description of the entire Java function.
     *
     * @param  username	description of parameter
     * @param  content	description of parameter
     */
    public static void post(String username,String content){
        try {
            TextPost textPost = new TextPost(content, username, username, getCurrentDateTime());
            if (!content.isEmpty()) {
                postPart.getChildren().add(textPost);
                textArea.clear();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("You can't post an empty post.");
                alert.showAndWait();
            }


        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Update the post feed with the given list of posts.
     *
     * @param  posts  the list of posts to be updated
     */
    public static void updatePostFeed(ArrayList<FeedUpdateRequest.CPair> posts){
        System.out.println("Updating posts...");
        postPart.getChildren().clear();

        for (FeedUpdateRequest.CPair post : posts) {
            post(post.key, post.value);
        }
    }
    /**
     * Retrieves the current date and time in the specified format.
     *
     * @return         	the current date and time formatted as "yyyy-MM-dd HH:mm:ss"
     */
    private static String getCurrentDateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return now.format(formatter);
    }
    /**
     * Generates a default TextPost with the message "Hello World!", posted by the Admin Team
     * and tagged with "@smet" at the current date and time.
     *
     * @return         the default TextPost created
     */
    private static TextPost defaultPost() throws FileNotFoundException {
        return new TextPost("Hello World!", "Admin Team", "@smet", getCurrentDateTime());
    }
    /**
     * Generate a profile box containing a profile picture and username label.
     *
     * @param  profilePictureAvatarPath    the path to the profile picture or avatar
     * @param  username                    the username to be displayed
     * @return                             the HBox containing the profile box
     */
    public static HBox profileBox(String profilePictureAvatarPath, String username) {
        HBox hBox = new HBox();

        try {
            hBox.getChildren().add(GuiUtil.createIcon(profilePictureAvatarPath));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        hBox.getStyleClass().add("profile-box");
        Label label = new Label(username);
        label.setId("label-white");
        label.setFont(Font.font("Areal", FontWeight.BOLD, 20));
        hBox.getChildren().add(label);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(10, 10, 10, 10));

        GuiUtil.applyScaleTransition(hBox, 0.95, 0.95, 200, 300);
        hBox.setOnMouseClicked(mouseEvent -> {
            stage.setScene(ProfilePage.instance);
            GuiUtil.relocate(ProfilePage.instance);
        });

        return hBox;
    }
}

