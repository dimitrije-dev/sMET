package networking;

import networking.packages.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private final Socket socket;
    private int databaseId = -1;

    public int getDatabaseId() {
        return databaseId;
    }

    public ClientHandler(Socket socket) {
        this.socket = socket;
        if (this.socket != null && !this.socket.isClosed()) {
            try {
                objectInputStream = new ObjectInputStream(this.socket.getInputStream());
                objectOutputStream = new ObjectOutputStream(this.socket.getOutputStream());

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else
            throw new RuntimeException("Socket is null or closed");
    }
    /**
     * The run method continuously receives and handles messages until an exception is caught.
     * Exceptions are caught and handled by closing streams and the socket, and then rethrowing a RuntimeException.
     */
    @Override
    public void run() {
        try {
            while (true) {
                Object message = receive();
                if (!this.handleMessage(message))
                    break;
            }

        } catch (IOException | ClassNotFoundException e) {
            try {
                objectOutputStream.close();
                objectInputStream.close();
                this.socket.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    /**
     * Sends a message using the objectOutputStream.
     *
     * @param message the message to be sent
     */
    public void send(Object message) {
        try {
            objectOutputStream.writeObject(message);
            objectOutputStream.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Receive an object from the input stream.
     *
     * @return the object received from the input stream
     * @throws IOException            if an I/O error occurs
     * @throws ClassNotFoundException   if the class of a serialized object cannot be found
     */
    public Object receive() throws IOException, ClassNotFoundException {
        return objectInputStream.readObject();
    }
    /**
     * Disconnects the client from the server by closing all streams and the socket.
     */
    protected void disconnect() {
        try {
            this.objectInputStream.close();
            this.objectOutputStream.close();
            this.socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean loggedIn = false;
    /**
     * Handles different types of messages received by the server.
     * If the message is a DisconnectRequest, it disconnects the client and removes it from the server.
     * If the message is a LogoutRequest, it sets the loggedIn status to false.
     * If the message is a ConnectRequest, it validates the user, sets the databaseId, and sends the request.
     * If the user is not logged in, it returns true.
     * If the message is a PostRequest, it adds a post, sends the feed to the client, and updates followers' feeds.
     * If the message is a FollowRequest, it follows another user.
     * If the message is a ProfileInfoRequest, it retrieves and sends the profile information.
     * If the message is a ProfileChangeRequest, it updates the profile information.
     * Finally, it handles the package using PackageHandler.
     * @param message The message received by the server.
     * @return true if the message is successfully handled, false otherwise.
     */
    private boolean handleMessage(Object message) {
        if (message instanceof DisconnectRequest) {
            this.disconnect();
            Server.removeClient(this);
            return false;
        } else if (message instanceof LogoutRequest) {
            loggedIn = false;
        } else if (message instanceof ConnectRequest request) {
            boolean valid = DatabaseUtil.validateUser(request);
            request.setValidRequest(valid);
            this.databaseId = DatabaseUtil.getUserId(request);
            send(request);
            loggedIn = valid;
        }
        if (!loggedIn) {
            return true;
        }

        if (message instanceof PostRequest postRequest) {
            if(postRequest.getText().isEmpty()){
                Server.send(DatabaseUtil.getFeed(this.databaseId), this);
                return true;
            }
            DatabaseUtil.addPost(this, postRequest.getText());
            Server.send(DatabaseUtil.getFeed(this.databaseId), this);
            List<Integer> followersIds = DatabaseUtil.getFollowersIds(this.databaseId);
            for (int id : followersIds) {
                if (Server.clientConnected(id)) {
                    final FeedUpdateRequest feedUpdateRequest1 = DatabaseUtil.getFeed(id);
                    Server.send(feedUpdateRequest1, Server.getClient(id));
                }
            }
        } else if (message instanceof FollowRequest followRequest) {
            if (!DatabaseUtil.isFollowing(this.databaseId, followRequest.databaseID))
                DatabaseUtil.follow(this.databaseId, followRequest.databaseID);
        }else if(message instanceof ProfileInfoRequest request){
            final ProfileInfoRequest profileInfoRequest = DatabaseUtil.getProfileInfo(request.username);
            Server.send(profileInfoRequest, this);
        }else if(message instanceof  ProfileChangeRequest request) {
            DatabaseUtil.setProfileInfo(request, this.databaseId);
        }
        PackageHandler.handlePackageServer(message);
        return true;
    }
}
