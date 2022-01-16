package view;

import javafx.scene.Scene;

public class Navigator {
    private static Scene scene;

    public static void setScene(Scene scene) {
        Navigator.scene = scene;
    }

    public static Scene getScene() {
        return scene;
    }
}
