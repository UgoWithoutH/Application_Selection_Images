package view;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import modele.MonFiltre;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

public class loading_view {

    LinkedList<MonFiltre> filtres = new LinkedList<>();

    @FXML
    public void initialize(){
        filtres.add(new MonFiltre(
                new String[]{"gif","tif", "jpeg", "jpg", "tiff"},
                "les fichiers image (*.gif, *.tif, *.jpeg)")
        );
    }

    @FXML
    public void click(MouseEvent mouseEvent) throws IOException {
        JFileChooser choix = new JFileChooser();
        for (MonFiltre m : filtres){
            choix.addChoosableFileFilter(m);
        }
        choix.setMultiSelectionEnabled(true);
        int retour = choix.showOpenDialog(null);
        if(retour == JFileChooser.APPROVE_OPTION) {
            // des fichiers ont été choisis ( sortie par OK)
            File[] fs = choix.getSelectedFiles();
            for (int i = 0; i < fs.length; ++i){
                System.out.println(fs[i].getName());
                System.out.println(fs[i].getAbsolutePath());
            }
        }
        System.out.println(mouseEvent.getY()/32);
        System.out.println(mouseEvent.getX()/32);
    }

    public void positionclic(MouseEvent mouseEvent) {
            System.out.println("Numéro de Tuile en X : " + (int)mouseEvent.getX()/32);
            System.out.println("Numéro de Tuile en Y : " + (int)mouseEvent.getY()/32);
    }
}
