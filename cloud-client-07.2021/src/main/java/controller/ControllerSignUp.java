package controller;


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
import lombok.extern.slf4j.Slf4j;
import animations.Shake;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;


@Slf4j
public class ControllerSignUp {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button registration;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField loginField;

    @FXML
    private Button connect;

    @FXML
    void initialize() {

        connect.setOnAction(event -> {  // описываются события при нажатии на кнопку enter
            String loginText = loginField.getText().trim(); // считывание текста из поля логин (с удалением пробелов)
            String passwordText = passwordField.getText().trim();
            if (!loginText.equals("") && !passwordText.equals("")) {
                try {
                    loginUser(loginText, passwordText);
                } catch (SQLException | IOException e) {
                    e.printStackTrace();
                }
            }
        });

        registration.setOnAction(event -> { // описываются события при нажатии на кнопку registration
            openNewScene("regUser-cloud-storage.fxml");

        });

    }

    //авторизация пользователя
    private void loginUser(String loginText, String passwordText) throws SQLException, IOException {
        DataBaseHandler dbHandler = new DataBaseHandler();
        User user = new User();
        user.setLogin(loginText);
        user.setPassword(passwordText);
        ResultSet result = dbHandler.getUser(user);

        int counter = 0;

        while (result.next()) {
            counter++;
        }
        if (counter >= 1) {
            //открывается окно основного приложения при успешной авторизации
            openNewScene("cloud-storage.fxml");

        } else {
            Shake userLoginAnim = new Shake(loginField);
            Shake userPassAnim = new Shake(passwordField);
            userLoginAnim.playAnim();
            userPassAnim.playAnim();
        }

    }

    //метод открывания нужного окна приложения
    public void openNewScene(String window) {
        connect.getScene().getWindow().hide();
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
