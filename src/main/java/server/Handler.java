package server;

import java.io.*;
import java.net.Socket;

public class Handler implements Runnable {

    private String serverDir = "server_dir";
    private String clientDir = "dir";

    private final byte[] buffer;
    private final Socket socket;
    private final DataInputStream is;
    private final DataOutputStream os;

    public Handler(Socket socket) throws IOException {
        this.socket = socket;
        is = new DataInputStream(socket.getInputStream());
        os = new DataOutputStream(socket.getOutputStream());
        buffer = new byte[1024];
    }

    @Override
    public void run() {
        try {
            while (true) {
                String fileName = is.readUTF(); // ожидание имени файла
                long size = is.readLong();// ожидание размера файла
                System.out.println("Size: " + size);


                // отправка файла на сервер
                try (FileOutputStream fos = new FileOutputStream(serverDir + "/" + fileName)) { // в try с ресурсами close() вызывается автоматически, поэтому поток закрыать не нужно
                    for (int i = 0; i < (size + 1023) / 1024; i++) { // при такой формуле всегда будет минимум 1 итеррация
                        int read = is.read(buffer);// чтение файла
                        fos.write(buffer, 0, read); // запись от 0 до сколько прочитали
                    }
                }
                os.writeUTF("File " + "\""+ fileName + "\"" + " successfully received!"); // сообщение о статусе операции
                os.flush();


                // отправка файла клиенту
                try (FileOutputStream fos = new FileOutputStream(clientDir + "/" + fileName)){
                    for (int i = 0; i < (size + 1023) / 1024; i++) {
                        int read = is.read(buffer);
                        fos.write(buffer, 0, read);
                    }
                }
                os.writeUTF("File " + "\""+ fileName + "\"" + " successfully received!");
                os.flush();

            }

        } catch (Exception e) {
            System.err.println("Exception while read");
        }
    }

    public Socket getSocket() {
        return socket;
    }
}
