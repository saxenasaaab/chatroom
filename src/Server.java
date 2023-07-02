import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    // List to keep track of clients
    private ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();
    private int port;

    public Server(int port) {
        this.port = port;
    }

    public void start() {
        try {
            // Create a ServerSocket to listen on the specified port
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);

            while (true) {
                // Wait for a client to connect
                Socket socket = serverSocket.accept();

                // Create a new ClientHandler thread to handle the client
                ClientHandler clientHandler = new ClientHandler(socket, this);

                // Add the client handler to the list of clients
                clients.add(clientHandler);

                // Start the client handler thread
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadcast(String message, ClientHandler excludeClient) {
        // Send a message to all clients except the one that sent the message
        for (ClientHandler client : clients) {
            if (client != excludeClient) {
                client.sendMessage(message);
            }
        }
    }

    public void removeClient(ClientHandler clientHandler) {
        // Remove a client from the list of clients
        clients.remove(clientHandler);
    }

    public static void main(String[] args) {
        Server server = new Server(args[0]);
        server.start();
    }
}

class ClientHandler extends Thread {
    private Socket socket;
    private Server server;
    private BufferedReader reader;
    private PrintWriter writer;

    public ClientHandler(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    public void run() {
        try {
            // Create input and output streams for the socket
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            reader = new BufferedReader(new InputStreamReader(inputStream));
            writer = new PrintWriter(outputStream, true);

            // Send a welcome message to the client
            writer.println("Welcome to the chat room!");

            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                // When a message is received from the client, broadcast it to all clients
                server.broadcast(inputLine, this);
            }

            // If the client disconnects, remove it from the list of clients
            server.removeClient(this);

            // Close the input and output streams and the socket
            reader.close();
            writer.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        // Send a message to the client
        writer.println(message);
    }
}
