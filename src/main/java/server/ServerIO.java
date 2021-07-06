package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerIO {


    public final static int PORT = 8189;
    public final static String PATH_TO_FILE = "C:/GB_Cloud-storage/Res/2.txt";
    public final static int FILE_SIZE = 6000000;

    public static void main(String[] args) {

        ServerSocket serverSocket = null;
        Socket socket = null;
        int bytesRead;
        int current = 0;
        FileOutputStream fileOutputStream = null;
        BufferedOutputStream bufferedOutputStream = null;

        try {
            serverSocket = new ServerSocket(PORT);

            while (true) {
                System.out.println("Waiting connection...");
                try {
                    // ожидание подключения
                    socket = serverSocket.accept();
                    System.out.println("Accepted connection: " + socket);

                    // получение файла
                    byte[] bytes = new byte[FILE_SIZE];
                    InputStream inputStream = socket.getInputStream();
                    fileOutputStream = new FileOutputStream(PATH_TO_FILE);
                    bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                    bytesRead = inputStream.read(bytes, 0, bytes.length);
                    current = bytesRead;

                    do {
                        bytesRead = inputStream.read(bytes, current, (bytes.length - current));
                        if (bytesRead >= 0)
                            current += bytesRead;
                    } while (bytesRead >= -1);

                    bufferedOutputStream.write(bytes, 0, current);
                    bufferedOutputStream.flush();

                    System.out.println("File " + PATH_TO_FILE + " downloaded (" + current + "bytes read)");

                } finally {
                    if (bufferedOutputStream != null)
                        bufferedOutputStream.close();
                    if (fileOutputStream != null)
                        fileOutputStream.close();
                    if (socket != null)
                        socket.close();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}


