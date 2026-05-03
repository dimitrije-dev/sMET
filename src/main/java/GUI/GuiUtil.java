package GUI;

import javafx.animation.ScaleTransition;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class GuiUtil {
    private static Stage stage;
    static Screen screen = Screen.getPrimary();
    private static final Rectangle2D bounds = screen.getVisualBounds();
    private static final String DEFAULT_ICON_PATH = "assets/icons/person.png";

    /**
     * Generates a logo node by loading an image file and setting its dimensions.
     *
     * @return The logo node as an ImageView object.
     * @throws FileNotFoundException If the image file is not found.
     */
    public static Node logo() throws FileNotFoundException {
        Image image = loadImage("assets/logos/smet.png");
        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(100);
        imageView.setFitWidth(200);
        return imageView;
    }

    /**
     * Creates an ImageView object from an image file.
     *
     * @param path the path to the image file
     * @return the created ImageView object
     * @throws FileNotFoundException if the image file is not found
     */
    public static ImageView createIcon(String path) throws FileNotFoundException {
        Image image = loadImage(path);
        return new ImageView(image);
    }
    /**
     * resize image to 50*50
     *
     * @param  path	path of the image file
     * @return         	an ImageView with the resized image
     */
    public static ImageView createButtonIcon(String path) throws FileNotFoundException {
        //resize image to 50*50

        Image image = loadImage(path, 50, 50);

        ImageView imageView = new ImageView(image);

        return imageView;
    }

    /**
     * Create a button with the given text and ID.
     *
     * @param buttonText the text to display on the button
     * @param buttonId   the unique ID of the button
     * @return the created button
     */
    public static Button createButton(String buttonText, String buttonId) {
        Button button = new Button();
        button.setId(buttonId);
        Label label = new Label(buttonText);
        label.setId(buttonId + "-bt");
        HBox buttonBox = createButtonBox(label);
        button.setGraphic(buttonBox);
        return button;
    }

    /**
     * Creates a button with text and an icon.
     *
     * @param buttonText the text to be displayed on the button
     * @param id         the id of the button
     * @param iconPath   the path to the icon image file
     * @return the created button
     * @throws FileNotFoundException if the icon image file is not found
     */

    public static Button createButtonTextIcon(String buttonText, String id, String iconPath) throws FileNotFoundException {
        Button button = new Button();
        button.setId(id);
        Label label = new Label(buttonText);
        label.setId(id + "-bt");
        HBox buttonBox = createButtonBox(label, createIcon(iconPath));
        button.setGraphic(buttonBox);
        return button;
    }

    /**
     * Creates a horizontal box with the given nodes as children.
     *
     * @param children the nodes to be added to the box
     * @return the created horizontal box
     */
    private static HBox createButtonBox(Node... children) {
        HBox buttonBox = new HBox(children);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(0);
        return buttonBox;
    }

    /**
     * A description of the entire Java function.
     *
     * @param  buttonText  description of parameter
     * @param  id         description of parameter
     * @param  iconPath   description of parameter
     * @return           description of return value
     */
    private static Button iconOnlyButton(String buttonText, String id, String iconPath) throws FileNotFoundException {
        Button button = new Button();
        button.setId(id);
        Label label = new Label(buttonText);
        label.setId(id + "-bt");
        HBox buttonBox = createButtonBox(createIcon(iconPath));
        button.setGraphic(buttonBox);
        return button;
    }

    /**
     * Generates a button scale transition effect for the given button.
     *
     * @param button the button to apply the scale transition effect to
     */

    public static void buttonScaleTransition(Button button) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(300), button);
        scaleTransition.setToX(0.95);
        scaleTransition.setToY(0.95);

        ScaleTransition reverseTransition = new ScaleTransition(Duration.millis(200), button);
        reverseTransition.setToX(1.0);
        reverseTransition.setToY(1.0);


        button.setOnMouseEntered(mouseEvent -> scaleTransition.playFromStart());

        button.setOnMouseExited(mouseEvent -> reverseTransition.playFromStart());
    }

    /**
     * Generates a scale transition effect for a button when the mouse enters and exits.
     *
     * @param button the button to apply the scale transition effect to
     */
    public static void buttonScaleTransitionMenu(Button button) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(300), button);
        scaleTransition.setToX(1.08);
        scaleTransition.setToY(1.08);


        ScaleTransition reverseTransition = new ScaleTransition(Duration.millis(200), button);
        reverseTransition.setToX(1.0);
        reverseTransition.setToY(1.0);

        button.setOnMouseEntered(mouseEvent -> scaleTransition.playFromStart());

        button.setOnMouseExited(mouseEvent -> reverseTransition.playFromStart());
    }

    /**
     * Applies a scale transition to the specified node.
     *
     * @param node          the node to apply the scale transition to
     * @param scaleValueX   the X scale value
     * @param scaleValueY   the Y scale value
     * @param durationEnter the duration of the scale transition on mouse enter
     * @param durationExit  the duration of the scale transition on mouse exit
     */
    public static void applyScaleTransition(Node node, double scaleValueX, double scaleValueY, int durationEnter, int durationExit) {
        ScaleTransition scaleTransitionEnter = new ScaleTransition(Duration.millis(durationEnter), node);
        scaleTransitionEnter.setToX(scaleValueX);
        scaleTransitionEnter.setToY(scaleValueY);

        ScaleTransition scaleTransitionExit = new ScaleTransition(Duration.millis(durationExit), node);
        scaleTransitionExit.setToX(1.0);
        scaleTransitionExit.setToY(1.0);

        node.setOnMouseEntered((MouseEvent mouseEvent) -> scaleTransitionEnter.playFromStart());
        node.setOnMouseExited((MouseEvent mouseEvent) -> scaleTransitionExit.playFromStart());
    }
    /**
     * A description of the entire Java function.
     *
     * @param  scene	description of parameter
     * @return         	description of return value
     */
    public static void relocate(Scene scene) {
        if (stage != null) {
            stage.setX((bounds.getWidth() - scene.getWidth()) / 2);
            stage.setY((bounds.getHeight() - scene.getHeight()) / 2);
        }
    }
    /**
     * Set the primary stage for the application.
     *
     * @param  stage	The primary stage for the application
     */
    public static void setPrimaryStage(Stage stage) {
        GuiUtil.stage = stage;
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
     * @param  buttonText    description of parameter
     * @param  id            description of parameter
     * @param  icon          description of parameter
     * @return               description of return value
     */
    public static Button createButtonMenu(String buttonText, String id, String icon) throws FileNotFoundException {
        Button button = new Button();
        button.setId(id);
        Image image = loadImage(icon);


        ImageView imageView = new ImageView(image);
        imageView.setFitHeight(30);
        imageView.setFitWidth(30);
        Label label = new Label(buttonText);
        label.setId(id + "-bt");
        HBox buttonBox = createButtonBox(imageView, label);
        buttonBox.setSpacing(10);
        button.setGraphic(buttonBox);
        return button;
    }

    private static Image loadImage(String path) throws FileNotFoundException {
        return loadImage(path, 0, 0);
    }

    private static Image loadImage(String path, double width, double height) throws FileNotFoundException {
        Path resolvedPath = resolveAssetPath(path);
        String uri = resolvedPath.toUri().toString();
        if (width > 0 && height > 0) {
            return new Image(uri, width, height, true, true);
        }
        return new Image(uri);
    }

    private static Path resolveAssetPath(String rawPath) throws FileNotFoundException {
        String normalizedPath = normalizePath(rawPath);
        Path candidate = Paths.get(normalizedPath);
        if (!candidate.isAbsolute()) {
            candidate = Paths.get("").toAbsolutePath().resolve(candidate).normalize();
        }
        if (Files.exists(candidate)) {
            return candidate;
        }

        Path fallback = Paths.get("").toAbsolutePath().resolve(DEFAULT_ICON_PATH).normalize();
        if (Files.exists(fallback)) {
            return fallback;
        }

        throw new FileNotFoundException("Unable to load image: " + rawPath);
    }

    private static String normalizePath(String rawPath) {
        if (rawPath == null || rawPath.isBlank()) {
            return DEFAULT_ICON_PATH;
        }

        String normalized = rawPath.replace("\\", "/");
        int assetsIndex = normalized.indexOf("/assets/");
        if (assetsIndex >= 0) {
            return normalized.substring(assetsIndex + 1);
        }
        return normalized;
    }



}
