package view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import javax.sound.midi.Soundbank;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Handler;

public class selection_view {

    @FXML
    private TabPane tabpane;

    String idTabSelected = "tab1";

    private List<Image> image;

    private double largeur;

    @FXML
    public void initialize(){
        int cpt = 1;
        Decoupeur d = new Decoupeur();
        File f = new File(System.getProperty("user.dir")+"/ressources/Images");
        var truc = Arrays.asList(f.list());
        ScrollPane sp;
        for(var file : truc){
            File tileset = new File (System.getProperty("user.dir")+"/ressources/Images/"+file);
            Image taille = new Image(String.valueOf(tileset));
            double largeurImage = taille.getWidth()/32;
            double hauteurImage = taille.getHeight()/32;
            image = d.decoupe(tileset.getAbsolutePath(),(int)largeurImage,(int)hauteurImage);
            System.out.println(tileset);
            sp = new ScrollPane(new ImageView(new Image(f+"\\"+file)));

            sp.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    positionclic(mouseEvent);
                }
            }
            );
            Tab tab = new Tab(file,sp);
            tab.setId("tab" + cpt);
            tabpane.getTabs().add(tab);
            tabpane.getSelectionModel().selectedItemProperty().addListener(
                    new ChangeListener<Tab>() {
                        @Override
                        public void changed(ObservableValue<? extends Tab> ov, Tab t, Tab t1) {
                            idTabSelected = t1.getId();
                        }
                    }
            );
            cpt++;
        }
    }


    public void positionclic(MouseEvent mouseEvent) {
        System.out.println("Tab numéro : " + idTabSelected);
        System.out.println("Numéro de Tuile en X : " + (int)mouseEvent.getX()/32);
        System.out.println("Numéro de Tuile en Y : " + (int) mouseEvent.getY()/32);
        //System.out.println("Liste des tuiles : " + image);
        Decoupeur d=new Decoupeur();

        int numero= (int)mouseEvent.getX()/32+((int)largeur+(int)mouseEvent.getY()/32);
        System.out.println(image.get(numero));
    }


}
