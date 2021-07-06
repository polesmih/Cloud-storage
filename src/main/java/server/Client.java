package server;

import java.io.*;
import java.net.Socket;

public class Client {

    public final static int PORT = 8189;
    public final static String SERVER = "localhost";
    public final static String PATH_TO_FILE = "C:/GB_Cloud-storage/Send/1.txt";

    public Client () throws IOException {


    Socket socket = null;
    FileInputStream fileInputStream = null;
    BufferedInputStream bufferedInputStream = null;
    OutputStream outputStream = null;

    try {
        socket = new Socket(SERVER, PORT);
        System.out.println("Connecting...");

        // отправка файла
        File file = new File(PATH_TO_FILE);
        byte[] bytes = new byte[(int) file.length()];

        fileInputStream = new FileInputStream(file);
        bufferedInputStream = new BufferedInputStream(fileInputStream);
        bufferedInputStream.read(bytes, 0, bytes.length);
        outputStream = socket.getOutputStream();

        System.out.println("Sending " + PATH_TO_FILE + "(" + bytes.length + "bytes)");
        outputStream.write(bytes, 0, bytes.length);
        outputStream.flush();

        System.out.println("Success!");

    } finally {
        if (bufferedInputStream != null)
            bufferedInputStream.close();
        if (outputStream != null)
            outputStream.close();
        if (socket != null)
            socket.close();
    }


    }
}
