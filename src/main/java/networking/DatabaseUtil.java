package networking;

import networking.packages.ConnectRequest;
import networking.packages.FeedUpdateRequest;
import networking.packages.ProfileChangeRequest;
import networking.packages.ProfileInfoRequest;
import org.mindrot.jbcrypt.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class DatabaseUtil {
    private static boolean connected = false;
    private final static String URL = System.getenv().getOrDefault("SMET_DB_URL", "jdbc:mysql://localhost:3306/iMetDatabase");
    private final static String USERNAME = System.getenv().getOrDefault("SMET_DB_USERNAME", "root");
    private final static String PASSWORD = System.getenv().getOrDefault("SMET_DB_PASSWORD", "");
    private static Connection connection = null;
    /**
     * Connects to the database if not already connected.
     */
    public static void connect() {
        if (connected) {
            System.out.println("Already connected");
            return;
        }
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
            return;
        }
        try {
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return;
        }

        connected = true;

    }
    /**
     * Disconnects from the database and sets the 'connected' flag to false.
     *
     * @throws SQLException if an error occurs during disconnection
     */
    public static void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
        connected = false;
    }
    /**
     * Check if the system is connected.
     */
    public static boolean isConnected() {
        return connected;
    }
    /**
     * Test method to execute a sample query and print the results.
     */
    public static void test() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * from tabelaTeste;");
            while (resultSet.next()) {
                System.out.println(resultSet.getInt(1) + " " + resultSet.getString(2));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Adds a new user to the database with the given username and password.
     *
     * @param  username   the username of the user to be added
     * @param  password   the password of the user to be added
     */
    public static void addUser(String username, String password) {
        if (!connected || connection == null) return;

        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        if (userExists(username)) return;
        String query = "INSERT INTO `user` (username, password) VALUES ('" + username + "', '" + hashedPassword + "')";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Checks if the user with the given username exists in the USER table.
     *
     * @param  username   the username of the user to check
     * @return           true if the user exists, false otherwise
     */
    public static boolean userExists(String username) {
        if (!connected || connection == null) return false;
        String query = "SELECT * from `user` WHERE username = '" + username + "'";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Validates the user by checking if the provided username and password match the records in the database.
     *
     * @param request the ConnectRequest object containing the username
     * @return true if the username and password match, false otherwise
     */
    public static boolean validateUser(ConnectRequest request) {
        String query = "SELECT * from `user` WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, request.getUsername());
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) return false;
            String hashedPassword = resultSet.getString("password");
            return BCrypt.checkpw(request.getPassword(), hashedPassword);
        } catch (SQLException e) {
            throw new RuntimeException(e);

        }
    }
    /**
     * Retrieves the user ID based on the provided ConnectRequest.
     *
     * @param  request  the ConnectRequest containing the username
     * @return          the user ID, or -1 if not found
     */
    public static int getUserId(ConnectRequest request) {
        String query = "SELECT * from `user` WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, request.getUsername());
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) return -1;
            return resultSet.getInt("id");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Finds users from the search bar based on the given username.
     *
     * @param username The username to search for.
     * @return The list of users matching the search criteria.
     */
    public static ArrayList<String> findUserFromSearchBar(String username) {
        if (!connected || connection == null) return new ArrayList<>();
        String query = "SELECT * from `user` WHERE username LIKE '" + username + "%'";
        ArrayList<String> users = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                users.add(resultSet.getString("username"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
    /**
     * Adds a new post to the database with the provided content for the specified user.
     *
     * @param clientHandler   the ClientHandler object associated with the user
     * @param content         the content of the post to be added
     */
    public static void addPost(ClientHandler clientHandler,String content) {
        String query = "INSERT INTO `post` (post_content, user_id) VALUES ('" + content + "', '" + clientHandler.getDatabaseId() + "')";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Retrieves the follower IDs for a specific user by their ID.
     *
     * @param  userId   the ID of the user for whom the follower IDs are retrieved
     * @return          a List of Integer containing the follower IDs
     */
    public static List<Integer> getFollowersIds(int userId) {
        String query = "SELECT * from followers WHERE follower_id = " + userId;
        List<Integer> followersIds = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                followersIds.add(resultSet.getInt("user_id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return followersIds;
    }
    /**
     * Retrieves the profile information for a specific user by their ID.
     *
     * @param  userId   the ID of the user for whom the profile information is retrieved
     * @return          a ProfileInfoRequest object containing the user's profile information
     */
    public static ProfileInfoRequest getProfileInfo(int userId) {
        String query = "SELECT * from `user` WHERE id = " + userId;
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) return null;
            return new ProfileInfoRequest(resultSet.getInt("id"), resultSet.getString("username"), resultSet.getString("bio"), resultSet.getString("gitLink"), resultSet.getString("instaLink"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * A description of the entire Java function.
     *
     * @param  username	description of parameter
     * @return         	description of return value
     */
    public static ProfileInfoRequest getProfileInfo(String username) {
        String query = "SELECT * from `user` WHERE username = '" + username + "'";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) return null;
            return new ProfileInfoRequest(resultSet.getInt("id"), resultSet.getString("username"), resultSet.getString("bio"), resultSet.getString("gitLink"), resultSet.getString("instaLink"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * A description of the entire Java function.
     *
     * @param  profileInfoRequest  description of parameter
     * @param  id                  description of parameter
     * @return                     description of return value
     */
    public static void setProfileInfo(ProfileChangeRequest profileInfoRequest,int id) {
        String query = "UPDATE `user` SET instaLink = '" + profileInfoRequest.insta + "', gitLink = '" + profileInfoRequest.git + "', bio = '" + profileInfoRequest.bio + "' WHERE id = '" + id + "'";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * A description of the entire Java function.
     *
     * @param  userId      description of parameter
     * @param  followerId  description of parameter
     * @return             description of return value
     */
    public static boolean isFollowing(int userId, int followerId) {
        String query = "SELECT * from followers WHERE user_id = " + userId + " AND follower_id = " + followerId;
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Make the user with userId follow the user with followerId
     */
    public static void follow(int userId, int followerId) {
        System.out.println("FOLLOWING: " + userId + " " + followerId);
        String query = "INSERT INTO followers (follower_id, user_id) VALUES (" + followerId + ", " + userId + ")";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Retrieves the feed for a specific user.
     *
     * @param  userId   the ID of the user for whom the feed is retrieved
     * @return          a FeedUpdateRequest object containing the user's feed
     */
    public static FeedUpdateRequest getFeed(int userId) {
        System.out.println("GETTING FEED: " + userId);
        String query = """
                SELECT u.username, p.post_content
                FROM `post` p
                JOIN `user` u ON u.id = p.user_id
                LEFT JOIN followers f ON f.follower_id = u.id
                WHERE u.id = ? OR f.user_id = ?
                ORDER BY p.post_time DESC;
                """;
        ArrayList<FeedUpdateRequest.CPair> posts = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.setInt(2, userId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                System.out.println("Adding new post to feed");
                posts.add(new FeedUpdateRequest.CPair(resultSet.getString("username"), resultSet.getString("post_content")));
            }
            return new FeedUpdateRequest(posts);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
