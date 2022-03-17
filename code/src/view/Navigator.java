package view;

import javafx.scene.Scene;

/**
 * Navigateur entre les scènes
 * @author Tremblay Jeremy, Vignon Ugo, Viton Antoine, Wissocq Maxime, Coudour Adrien
 */
public class Navigator {
    private static Scene scene;

    /**
     * modification de la scène courante
     * @param scene Nouvelle scène courante du projet
     */
    public static void setScene(Scene scene) {
        Navigator.scene = scene;
    }

    /**
     * Récupération de la scène actuelle du projet
     * @return Scène actuelle
     */
    public static Scene getScene() {
        return scene;
    }
}
