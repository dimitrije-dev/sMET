package networking;

import GUI.Feed;
import GUI.ProfilePage;
import javafx.application.Platform;
import javafx.util.Pair;
import networking.packages.ClientMessage;
import networking.packages.FeedUpdateRequest;
import networking.packages.ProfileInfoRequest;
import networking.packages.TestPacket;

import java.util.ArrayList;

public abstract class PackageHandler {
    /**
     * How the server will handle packet
     *
     * @param  message    description of parameter
     * @return            description of return value
     */
    protected static void handlePackageServer(Object message) {
        // How the server will handle packet
        if (message instanceof TestPacket packet) {
            packet.someValue = 5;
            Server.send(new TestPacket(packet.someValue));
        } else if (message instanceof ClientMessage clientMessage) {
            // TODO : Database.addNewMessage
            Server.send(new ClientMessage(clientMessage.message));
        }
    }
    /**
     * How the client will handle packet
     *
     * @param  message    description of parameter
     */
    protected static void handlePackageClient(Object message) {
        // How the client will handle packet
        if(message instanceof TestPacket packet){
            System.out.println("Exchanged packet with value: " + packet.someValue);
        }else if(message instanceof ClientMessage clientMessage){
            // TODO : Add message to queue
            // TODO : Update gui
        }else if(message instanceof ProfileInfoRequest info){
            ProfilePage.databaseId = info.databaseID;
            ProfilePage.username = info.username;
            ProfilePage.gitLink = info.gitLink;
            ProfilePage.instagramLink = info.instagramLink;
            ProfilePage.bio = info.bio;
            Platform.runLater(() ->{ProfilePage.usernameLabel.setText(info.username);
            ProfilePage.profileBio.setText(info.bio);});
        } else if(message instanceof FeedUpdateRequest feedUpdateRequest) {
            final ArrayList<FeedUpdateRequest.CPair> posts = feedUpdateRequest.posts;
            if(posts == null)
                throw new RuntimeException("Null posts!");
            Platform.runLater(() -> Feed.updatePostFeed(posts));
        }
    }
}
