package view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import modele.Decoupeur;

import java.io.File;
import java.util.*;

public class Selection_view {

    @FXML
    private TabPane tabpane;

    @FXML
    private FlowPane affichageDecoupe;

    private static final String NOMMAGE_TAB = "tab";

    String idTabSelected = NOMMAGE_TAB+"1";

    private List<Image> mesImagesCourrantes = new LinkedList<>();

    private Map<String,List<Image>> map = new HashMap<>();

    private double largeur;

    private Canvas cv;

    private int cptpx = 0;

    @FXML
    public void initialize() {
        int cpt = 1;

        cv = new Canvas(100, 500);
        affichageDecoupe.getChildren().add(cv);
        Decoupeur d = new Decoupeur();
        File f = new File(System.getProperty("user.dir") + "/ressources/Images");
        var truc = Arrays.asList(f.list());
        ScrollPane sp;
        for (var file : truc) {
            File tileset = new File(System.getProperty("user.dir") + "/ressources/Images/" + file);
            Image taille = new Image(String.valueOf(tileset));
            double largeurImage = taille.getWidth() / 32;
            double hauteurImage = taille.getHeight() / 32;
            var decoupe = d.decoupe(tileset.getAbsolutePath(), (int) largeurImage, (int) hauteurImage);
            System.out.println(decoupe);
            sp = new ScrollPane(new ImageView(new Image(f + "\\" + file)));

            sp.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                     @Override
                                     public void handle(MouseEvent mouseEvent) {
                                         positionclic(mouseEvent);
                                     }
                                 }
            );
            Tab tab = new Tab(file, sp);
            tab.setId(NOMMAGE_TAB + cpt);
            map.put(tab.getId(), decoupe);
            tabpane.getTabs().add(tab);
            tabpane.getSelectionModel().selectedItemProperty().addListener(
                    new ChangeListener<Tab>() {
                        @Override
                        public void changed(ObservableValue<? extends Tab> ov, Tab t, Tab t1) {
                            idTabSelected = t1.getId();
                            mesImagesCourrantes = map.get(t1.getId());
                        }
                    }
            );
            cpt++;
        }
        mesImagesCourrantes = map.get("tab1");
    }


    public void positionclic(MouseEvent mouseEvent) {
        System.out.println(mesImagesCourrantes);
        System.out.println("Tab numéro : " + idTabSelected);
        System.out.println("Numéro de Tuile en X : " + (int)mouseEvent.getX()/32);
        System.out.println("Numéro de Tuile en Y : " + (int) mouseEvent.getY()/32);
        //System.out.println("Liste des tuiles : " + image);
        Decoupeur d=new Decoupeur();
        int numero= (int)mouseEvent.getX()/32+((int)(int)mouseEvent.getY()/32)*32;
        System.out.println("num :" + numero);
        System.out.println(mesImagesCourrantes.get(numero));
        cptpx += 32;
        cv.getGraphicsContext2D().drawImage(mesImagesCourrantes.get(numero),0,cptpx);
    }


}
