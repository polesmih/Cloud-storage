package controller;


import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import lombok.extern.slf4j.Slf4j;
import model.*;

import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;


@Slf4j
public class ControllerApp implements Initializable {

    public ListView<String> clientView;
    public ListView<String> serverView;
    public TextField clientPath;
    public TextField serverPath;
    public Label output;
    private Path currentDir;
    private ObjectEncoderOutputStream os;
    private ObjectDecoderInputStream is;
    private final ClientMessageHandler clientMessageHandler;

    public ControllerApp(ClientMessageHandler clientMessageHandler) {

        this.clientMessageHandler = clientMessageHandler;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            String userDir = System.getProperty("user.name"); //- установление пути в папку пользователя
            currentDir = Paths.get("/Users", userDir).toAbsolutePath();
            log.info("Current user: {}", System.getProperty("user.name"));
//            currentDir = Paths.get("./").toAbsolutePath();
//            Socket socket = new Socket("localhost", 8189);
//            os = new ObjectEncoderOutputStream(socket.getOutputStream());
//            is = new ObjectDecoderInputStream(socket.getInputStream());

            refreshClientView();
            addNavigationListeners();

            Thread readThread = new Thread(() -> {
                try {
                    while (true) {
                        AbstractCommand command = (AbstractCommand) clientMessageHandler.getIs().readObject();
                        switch (command.getType()) {
                            case SIMPLE_MESSAGE:
                                Message message = (Message) command;
                                statusUpdate(message.toString());
                                break;
                            case LIST_MESSAGE:
                                ListResponse response = (ListResponse) command;
//                                List<String> names = response.getNames();
                                List<String> names = new ArrayList<>();
                                if (!response.isRoot()) {
                                    names.add("..");
                                }
                                names.addAll(response.getNames());
                                refreshServerView(names);
                                break;
                            case PATH_RESPONSE:
                                PathUpResponse pathResponse = (PathUpResponse) command;
                                String path = pathResponse.getPath();
                                Platform.runLater(() -> serverPath.setText(path));
                                break;
                            case FILE_MESSAGE:
                                FileMessage msg = (FileMessage) command;
                                try (FileOutputStream fos = new FileOutputStream(currentDir.resolve(msg.getName()).toString())) {
                                    fos.write(msg.getData());
                                    refreshClientView();
                                    Platform.runLater(() -> {
                                        statusUpdate("File success downloaded!");
                                    });
//                                }
//                                break;
//                        }
//                    }
                                } catch (Exception e) {
                                    statusUpdate("File upload error...");
                                }
                                break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            readThread.setDaemon(true);
            readThread.start();
            statusUpdate("Connected");
        } catch (Exception e) {
            statusUpdate("Connected failed...");
        }


    }

    private void statusUpdate(String message) {
        Platform.runLater(() -> output.setText("Information: " + message));
    }

    private void refreshClientView() throws IOException {
        clientPath.setText(currentDir.toString());
        List<String> names = Files.list(currentDir)
                .map(p -> p.getFileName().toString())
                .collect(Collectors.toList());
        Platform.runLater(() -> {
            clientView.getItems().clear();
            clientView.getItems().add("..");
            clientView.getItems().addAll(names);
        });
    }

    private void refreshServerView(List<String> names) {
        Platform.runLater(() -> {
            serverView.getItems().clear();
            serverView.getItems().addAll(names);
        });
    }

    public void upload(ActionEvent actionEvent) throws IOException {
        String fileName = clientView.getSelectionModel().getSelectedItem();
        FileMessage message = new FileMessage(currentDir.resolve(fileName));
        os.writeObject(message);
        os.flush();
    }

    public void downLoad(ActionEvent actionEvent) throws IOException {
        String fileName = serverView.getSelectionModel().getSelectedItem();
        os.writeObject(new FileRequest(fileName));
        os.flush();
    }

    private void addNavigationListeners() {
        clientView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                String item = clientView.getSelectionModel().getSelectedItem();
                Path newPath = currentDir.resolve(item);
                if (Files.isDirectory(newPath)) {
                    currentDir = newPath;
                    try {
                        refreshClientView();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            }
        });

        serverView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                String item = serverView.getSelectionModel().getSelectedItem();
                try {
                    os.writeObject(new PathInRequest(item));
                    os.flush();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
    }

    public void clientPathUp(ActionEvent actionEvent) throws IOException {
        currentDir = currentDir.getParent();
        clientPath.setText(currentDir.toString());
        refreshClientView();
    }

    public void serverPathUp(ActionEvent actionEvent) throws IOException {
        os.writeObject(new PathUpRequest());
        os.flush();
    }

    public void openClientFile(ActionEvent actionEvent) {
        String item = clientView.getSelectionModel().getSelectedItem();
        Path path = currentDir.resolve(item);

        Desktop desktop = null;
        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
        }
        try {
            assert desktop != null;
            desktop.open(new File(String.valueOf(path)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //доработать метод: загрузка в файл загрузки на клиенте и только потом оттуда открывать)
    public void openServerFile(ActionEvent actionEvent) {
//        String item = serverView.getSelectionModel().getSelectedItem();
//        Path path = Paths.get("server_dir").resolve(item);
//
//        Desktop desktop = null;
//        if (Desktop.isDesktopSupported()) {
//            desktop = Desktop.getDesktop();
//        }
//        try {
//            assert desktop != null;
//            desktop.open(new File(String.valueOf(path)));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public void deleteFromClient(ActionEvent actionEvent) throws IOException {
        String fileName = clientView.getSelectionModel().getSelectedItem();
        boolean clientFileDeletingStatus;
        clientFileDeletingStatus = currentDir.resolve(fileName).toFile().delete();
        if (clientFileDeletingStatus)
        statusUpdate("File " + fileName + "successfully deleted!");
        else statusUpdate("Error deleting file from client");
        try {
//            Files.delete(Paths.get(String.valueOf(currentDir.resolve(fileName))));
            refreshClientView();

        } catch (IOException e) {
            e.printStackTrace();
        }
//        refreshClientView();
    }

    public void deleteFromServer(ActionEvent actionEvent) throws IOException {
        String fileName = serverView.getSelectionModel().getSelectedItem();
        try {
            Files.delete(Paths.get(String.valueOf(Paths.get("server_dir").resolve(fileName))));
            serverView.getItems().remove(fileName);
        } catch (IOException e) {
            statusUpdate("Error deleting file from server");
        }
    }


    public void renameOnClient(ActionEvent actionEvent) throws IOException {
        String fileName = clientView.getSelectionModel().getSelectedItem();
//        currentDir = currentDir.resolve(fileName);
        Path path = Paths.get(String.valueOf(currentDir.resolve(fileName)));

        TextInputDialog dialog = new TextInputDialog("Rename");
        dialog.setTitle("Rename");
//        dialog.setHeaderText("rename file");
        dialog.setContentText("enter new file name here");
        Optional<String> res = dialog.showAndWait();
        if (res.isPresent()) {
            try {
//                Path path = Paths.get("dir/" + fileName);
//                Path path = Paths.get(currentDir + fileName);
                Files.move(path, path.resolveSibling(res.get()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            refreshClientView();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void renameOnServer(ActionEvent actionEvent) {
        currentDir = currentDir.resolve("/");
        TextInputDialog dialog = new TextInputDialog("Rename");
        dialog.setTitle("Rename");
        dialog.setHeaderText("rename file");
        dialog.setContentText("enter new file name here");
        Optional<String> res = dialog.showAndWait();
        if (res.isPresent()) {
            try {
                Path path = Paths.get("server_dir/" + serverView.getSelectionModel().getSelectedItem());
                Files.move(path, path.resolveSibling(res.get()));
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        // обновить директорию...
    }
}

