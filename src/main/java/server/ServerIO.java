package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerIO {
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(8189);
        System.out.println("Server started...");
        while (true) {
            Socket socket = server.accept();
            System.out.println("Client accepted...");
            try {
                new Thread(new )
            }
        }
    }
}
