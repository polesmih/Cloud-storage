package animations;

import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class Shake {
    private TranslateTransition tt;

    public Shake(Node node) {
        tt = new TranslateTransition(Duration.millis(100), node); //продолжительност тряски при неверной авторизации
        tt.setFromX(0f);
        tt.setByX(20f); // диапазон тряски окна приложения
        tt.setCycleCount(3);// количество тряски
        tt.setAutoReverse(true);// для возврата действий тряки (вправо/влево)
    }

    public void playAnim(){
        tt.playFromStart();
    }
}
