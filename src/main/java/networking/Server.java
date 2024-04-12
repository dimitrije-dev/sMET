package networking;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Server {
    public static void main(String[] args) {
        try {
            Server.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static final int PORT = 8080;
    private static final int MAX_CLIENTS = 10;

    private static boolean running = false;

    public static boolean isRunning() {
        return running;
    }

    private static boolean accepting = false;

    public static boolean isAccepting() {
        return accepting;
    }

    private static ServerSocket serverSocket = null;
    private static ExecutorService threadPool = null;
    private static List<ClientHandler> clients = new ArrayList<>();
    /**
     * Checks if a client with the given database ID is connected.
     *
     * @param  databaseId   the database ID to check
     * @return              true if a client with the given database ID is connected, false otherwise
     */
    public static boolean clientConnected(int databaseId) {
        for (ClientHandler client : clients) {
            if (client.getDatabaseId() == databaseId) {
                return true;
            }
        }
        return false;
    }
    public static ClientHandler getClient(int databaseId) {
        for (ClientHandler client : clients) {
            if (client.getDatabaseId() == databaseId) {
                return client;
            }
        }
        return null;
    }
    /**
     * Starts the server, connecting to the database and initializing the server socket.
     *
     * @throws IOException   if an I/O error occurs when opening the server socket
     */
    public static void start() throws IOException {
        if (isRunning()) {
            System.out.println("Can't run the server.Server is already running!");
            return;
        }

        System.out.println("Connecting to database...");
        DatabaseUtil.connect();
        if (!DatabaseUtil.isConnected()) {
            System.out.println("Can't start the server.Connection to database failed!");
            return;
        }
        System.out.println("Connected to database!");

        System.out.println("Starting server on port: " + PORT);
        serverSocket = new ServerSocket(PORT);
        running = true;
        System.out.println("Server started on port: " + PORT);

        startAccepting();
    }
    /**
     * Stops the server.
     *
     * @throws IOException
     */
    public static void stop() throws IOException {
        if (!isRunning()) {
            System.out.println("Can't stop the server.Server is already not running!");
            return;
        }
        if (isAccepting())
            stopAccepting();

        running = false;
        serverSocket.close();
        System.out.println("Server stopped!");
    }
    /**
     * Starts accepting new socket connections.
     *
     * @throws SocketException if an error occurs while accepting a new connection
     */
    private static void startAccepting() throws SocketException {
        if (!isRunning()) {
            System.out.println("Can't start accepting.Server is not running!");
            return;
        }

        System.out.println("Creating new thread pool...");
        threadPool = Executors.newCachedThreadPool();
        System.out.println("Thread pool created!");

        System.out.println("Starting accepting...");
        accepting = true;
        serverSocket.setSoTimeout(1000);
        while (isRunning() && isAccepting()) {
            try {
                final Socket socket = serverSocket.accept();
                onSocketAccepted(socket);
            } catch (SocketTimeoutException e) {
                if (!isRunning() || !isAccepting()) break;
            } catch (IOException e) {
                System.out.println("Failed to accept new connection!");
            }
        }
        stopAccepting();
        System.out.println("Server stopped accepting!");
    }
    /**
     * Stops the accepting process.
     * Shuts down the thread pool if the server is running and accepting new connections.
     * If the server is not running or is already not accepting, prints an error message.
     */
    private static void stopAccepting() {
        System.out.println("Stopping accepting process...");
        threadPool.shutdown();
        if (!isRunning()) {
            System.out.println("Can't stop accepting.Server is not running!");
            return;
        }
        if (!isAccepting()) {
            System.out.println("Can't stop accepting.Server is already not accepting!");
            return;
        }
        accepting = false;
    }
    /**
     * Handles a newly accepted socket connection.
     * Creates a new client handler, adds it to the list of clients, and starts a new thread to handle client requests.
     * If the maximum number of clients is reached, it stops accepting new connections.
     */
    private static void onSocketAccepted(final Socket socket) {
        System.out.println("New socket accepted!");
        final ClientHandler client = new ClientHandler(socket);
        clients.add(client);
        System.out.println("New client[" + socket.getInetAddress() + "] registered!");
        threadPool.execute(client);

        if (clients.size() > MAX_CLIENTS) stopAccepting();
    }

    protected static void removeClient(final ClientHandler client) {
        clients.remove(client);
    }

    public static void send(Object message, ClientHandler... clients) {
        for (ClientHandler client : clients) {
            client.send(message);
        }
    }

    public static void send(Object message) {
        for (ClientHandler client : clients) {
            client.send(message);
        }
    }
}