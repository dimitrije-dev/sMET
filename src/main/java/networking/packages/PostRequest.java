package networking.packages;

import Posts.TextPost;

import java.io.Serializable;

public class PostRequest implements Serializable {
    private String text;

    public PostRequest(String text) {
        this.text = text;
    }
    /**
     * Retrieves the text content of the post.
     *
     * @return the text content of the post
     */
    public String getText() {
        return text;
    }
}
