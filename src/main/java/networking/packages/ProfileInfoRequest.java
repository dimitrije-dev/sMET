package networking.packages;

import java.io.Serializable;

public class ProfileInfoRequest implements Serializable {
    public int databaseID;
    public String username;
    public String gitLink, instagramLink, bio;

    public ProfileInfoRequest() {
    }
    public ProfileInfoRequest(String username){
        this.username = username;
    }

    public ProfileInfoRequest(int databaseID, String username, String bio, String gitLink, String instagramLink) {
        this.databaseID = databaseID;
        this.username = username;
        this.bio = bio;
        this.gitLink = gitLink;
        this.instagramLink = instagramLink;
    }
}
