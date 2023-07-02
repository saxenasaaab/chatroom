
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String username;

    public Client(String hostname, int port, String username) {
        try {
            socket = new Socket(hostname, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            this.username = username;
        } catch (IOException e) {
            System.out.println("Error creating client: " + e.getMessage());
        }
    }

    public void start() {
        try {
            // Send the username to the server
            out.println(username+" has joined the chat now.");

            // Start a new thread to read messages from the server
            Thread readerThread = new Thread(new ClientReader());
            readerThread.start();

            // Read messages from the console and send them to the server
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String input = console.readLine();
                if (input != null) {
                    out.println(username+": "+input);
                }
            }
        } catch (IOException e) {
            System.out.println("Error starting client: " + e.getMessage());
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                //TODO
            }
        }
    }
    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 12345;
        String username = args[0];

        Client client = new Client(hostname, port, username);
        client.start();
    }

    private class ClientReader implements Runnable {
        @Override
        public void run() {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                System.out.println("Error reading from server: " + e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    // Ignore
                }
            }
        }
    }
}

