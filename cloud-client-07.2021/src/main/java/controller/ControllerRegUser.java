package controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import database.DataBaseHandler;
import database.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ControllerRegUser {

        @FXML
        private ResourceBundle resources;

        @FXML
        private URL location;

        @FXML
        private TextField nameField;

        @FXML
        private Button registration;

        @FXML
        private PasswordField passwordField;

        @FXML
        private TextField loginField;

        @FXML
        void initialize() { // код действий при нажатии на кнопку регистрации
            registration.setOnAction(event -> {
                registrationUser();
            });
        }

        private void registrationUser() {
            DataBaseHandler dbHandler = new DataBaseHandler();

            String name = nameField.getText();
            String login = loginField.getText();
            String password = passwordField.getText();

            User user = new User(name, login, password);

            dbHandler.registrationUser(login, password, name);
            openNewScene("/signUp-cloud-storage.fxml");

        }

    public void openNewScene(String window) {
        registration.getScene().getWindow().hide();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(window));
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Parent root = loader.getRoot();// путь к файлу, что необходимо загрузить
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.show();//подождать отображения нового файла
    }

    }


