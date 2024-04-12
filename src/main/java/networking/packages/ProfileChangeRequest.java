package networking.packages;

import java.io.Serializable;

public class ProfileChangeRequest implements Serializable {
    public String git, insta, bio;

    public ProfileChangeRequest(String git, String insta, String bio) {
        this.git = git;
        this.insta = insta;
        this.bio = bio;
    }
}
