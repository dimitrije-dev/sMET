package networking.packages;

import java.io.Serializable;

public class FollowRequest implements Serializable {

    public int databaseID;
    public FollowRequest(int databaseID) {
        this.databaseID = databaseID;
    }
}
