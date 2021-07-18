package server.server;

import server.Handler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerIO {


    public final static int PORT = 8190;

   // public static void main(String[] args) throws IOException {
    public ServerIO () throws IOException {
        ServerSocket server = new ServerSocket(PORT);
        System.out.println("Server started...");
        while (true) {
            Socket socket = server.accept(); // block
            System.out.println("Client accepted...");
            try {
                new Thread(new Handler(socket)).start();
            } catch (Exception e) {
                System.err.println("Connection error");
            }
        }
    }
}
