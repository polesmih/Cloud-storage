import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppClient extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent parent = FXMLLoader.load(getClass().getResource("signUp-cloud-storage.fxml"));
        primaryStage.setScene(new Scene(parent));
        primaryStage.setTitle("Cloud-Storage");
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
