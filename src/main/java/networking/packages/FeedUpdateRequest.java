package networking.packages;

import Posts.TextPost;
import javafx.util.Pair;

import java.io.Serializable;
import java.util.ArrayList;

public class FeedUpdateRequest implements Serializable {
    public FeedUpdateRequest() {
        this.posts = new ArrayList<>();
    }
    public FeedUpdateRequest(ArrayList<CPair> posts) {
        this.posts = posts;
    }

    public static class CPair implements Serializable {
        public String key;
        public String value;
        public CPair(){

        }

        public CPair(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
    public ArrayList<CPair> posts;
}