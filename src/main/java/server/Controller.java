package server;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {


    // For ServerIO

    public ListView<String> listView; // поле из fxml-файла графики программы
    public ListView<String> listViewServer;
    public Label output; // поле из fxml-файла
    private final String pathClient = "dir";
    private final String pathServ = "server_dir";
    private DataOutputStream os;
    private DataInputStream is;

    private byte[] buffer = new byte[256];


    public void send(ActionEvent actionEvent) throws IOException {

        String fileName = listView.getSelectionModel().getSelectedItem(); // отправка того, что выбрали
        os.writeUTF(fileName);
        long len = Files.size(Paths.get(pathClient, fileName));
        os.writeLong(len);

        try (FileInputStream fis = new FileInputStream(pathClient + "/" + fileName)){
            int read;
            while (true) {
                read = fis.read(buffer);
                if (read == -1) {
                    break;
                }
                os.write(buffer, 0, read);
            }

        }
        os.flush();

    }

    public void receive(ActionEvent actionEvent) throws IOException {
        String fileName = listViewServer.getSelectionModel().getSelectedItem();
        os.writeUTF(fileName);
        long len = Files.size(Paths.get(pathServ, fileName));
        os.writeLong(len);

        try (FileInputStream fis = new FileInputStream(pathServ + "/" + fileName)){
            int read;
            while (true) {
                read = fis.read(buffer);
                if (read == -1) {
                    break;
                }
                os.write(buffer, 0, read);
            }

        }
        os.flush();

    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            Socket socket = new Socket("localhost", 8190);
            is = new DataInputStream(socket.getInputStream());
            os = new DataOutputStream(socket.getOutputStream());
            File dir = new File(pathClient);
            listView.getItems().addAll(dir.list());// так передается в окно приложения файл

            File dir2 = new File(pathServ);
            listViewServer.getItems().addAll(dir2.list());// так передается в окно приложения файл


            Thread readThread = new Thread(() -> { // получение ответа от сервера об обработке файла
                try {
                    while (true) {
                        String status = is.readUTF();
                        Platform.runLater(() -> output.setText(status)); // какой статус сервера придет, такой на форме и отпечатается

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


    });
            readThread.setDaemon(true); // делаем поток сервисным
            readThread.start();// запуск потока


        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void refreshClientList() {
        File file = new File(pathClient);
        String[] files = file.list();
        listView.getItems().clear();
        if (files != null) {
            for (String name : files) {
                listView.getItems().add(name);
            }
        }
    }

    public void refreshClientList(ActionEvent actionEvent) {

        refreshClientList();
    }


    private void refreshServerList() {
        File file = new File(pathServ);
        String[] files = file.list();
        listViewServer.getItems().clear();
        if (files != null) {
            for (String name : files) {
                listViewServer.getItems().add(name);
            }
        }
    }

    public void refreshServerList(ActionEvent actionEvent) {

        refreshServerList();
    }


    public void deleteFromClient(ActionEvent actionEvent) {

    }


    public void deleteFromServer(ActionEvent actionEvent) {

    }
}
