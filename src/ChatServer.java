import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ChatServer {
    ArrayList<Client> clients = new ArrayList<>();
    ServerSocket server;

    public ChatServer() throws IOException {
        // создаем серверный сокет на порту 1234
        this.server = new ServerSocket(1234);
    }

    void sendAll(String message) {
        for (Client client : clients) {
            client.receive(message);
        }

    }

    public void run() {
        while (true) {
            try {
                System.out.println("Waiting...");
                // ждем клиента
                Socket socket = server.accept();
                System.out.println("Client connected!");
                // создаем клиента на своей стороне
                clients.add(new Client(socket));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new ChatServer().run();

    }
    class Client implements Runnable {
        Socket socket;
        Scanner in;
        PrintStream out;
        Client(Socket socket) {
            this.socket = socket;
            new Thread(this).start();
        }

        void receive(String message) {
            out.println(message);
        }
        public void run() {
            try {
                // получаем потоки ввода и вывода
                InputStream is = socket.getInputStream();
                OutputStream os = socket.getOutputStream();

                // создаем удобные средства ввода и вывода
                in = new Scanner(is);
                out = new PrintStream(os);

                // читаем из сети и пишем в сеть

                out.println("Welcome to Chat");
                String input = in.nextLine();
                while (!input.equals("bye")) {
                    sendAll(input);
                    input = in.nextLine();
                }
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}