package Logic;

import Posts.TextPost;

import java.awt.*;
import java.io.FileInputStream;
import java.util.ArrayList;

public class User {
    private String username;
    private String password;
    private String image;
    private String bio = "";
    private String instagramLink = "";
    private String gitLink = "";
    private ArrayList<TextPost> posts = new ArrayList<>();

    private ArrayList<User> following = new ArrayList<>();
    private ArrayList<User> followers = new ArrayList<>();


    public User() {
    }

    public User(String username, String password, String image ,String bio, String instagramLink, String gitLink) {
        this.username = username;
        this.password = password;
        this.bio = bio;
        this.instagramLink = instagramLink;
        this.gitLink = gitLink;
        setImage(image);

    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        if (image != null) {
            this.image = image;
        } else {
            this.image = "/Users/dimimac/INTELLIJ/JAVA II/PROJEKAT/projekat-cs202/assets/photos/profilePlaceholder.png";
        }
    }


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{"
                + "username='" + username + "'"
                + ", password='" + password + "'"
                + '}';
    }

}
