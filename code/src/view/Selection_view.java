package view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import modele.Decoupeur;

import java.io.File;
import java.util.*;

public class Selection_view {

    @FXML
    private TabPane tabpane;

    @FXML
    private FlowPane affichageDecoupe;

    private static final String NOMMAGE_TAB = "tab";
    private static final String NOMMAGE_SCROLLPANE = "s";

    String idTabSelected = NOMMAGE_TAB+"1";

    private List<Image> mesImagesCourrantes = new LinkedList<>();

    private Map<String,List<Image>> map = new HashMap<>();

    private double largeur;

    private Canvas cv;

    private int cptpx = 0;

    @FXML
    public void initialize() {
        int cpt = 1;
        double widthCanvas = 200;
        double heightCanvas = 500;

        cv = new Canvas(widthCanvas, heightCanvas);
        affichageDecoupe.getChildren().add(cv);
        affichageDecoupe.setMaxSize(widthCanvas,heightCanvas);
        affichageDecoupe.setMinSize(widthCanvas,heightCanvas);
        Decoupeur d = new Decoupeur();
        File f = new File(System.getProperty("user.dir") + "/ressources/Images");
        var truc = Arrays.asList(f.list());
        ScrollPane sp;
        for (var file : truc) {
            File tileset = new File(System.getProperty("user.dir") + "/ressources/Images/" + file);
            Image tilesetImage = new Image(String.valueOf(tileset));
            double largeurImage = tilesetImage.getWidth() / 32;
            double hauteurImage = tilesetImage.getHeight() / 32;
            var decoupe = d.decoupe(tileset.getAbsolutePath(), (int) largeurImage, (int) hauteurImage);
            //GridPane
            //ScrollPane
            sp = new ScrollPane(); //new ImageView(new Image(f + "\\" + file))
            sp.setId(NOMMAGE_SCROLLPANE+cptpx);
            sp.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                     @Override
                                     public void handle(MouseEvent mouseEvent) {
                                         positionclic(mouseEvent);
                                     }
                                 }
            );

            GridPane gp = initializeGridPane(decoupe,tilesetImage);
            sp.setContent(gp);
            //Tab
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

    public GridPane initializeGridPane(List<Image> myListDecoupe, Image tileset){
        GridPane gp = new GridPane();
        int x = 0, y = 0;
        double dimensionTile = myListDecoupe.get(0).getWidth();
        double dimensionMax = 704;
        double currentDimension = 0;

        for(var image : myListDecoupe){
            if(currentDimension == dimensionMax){
                y++;
                x = 0;
                currentDimension = 0;
            }
            gp.add(new ImageView(image),x,y);
            currentDimension += dimensionTile;
            x++;
        }
        gp.setGridLinesVisible(true);
        return gp;
    }

    private void addGridEvent(GridPane gp) {
        gp.getChildren().forEach(item -> {
            item.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (event.getClickCount() == 2) {
                        System.out.println("doubleClick");
                    }
                    if (event.isPrimaryButtonDown()) {
                        System.out.println("PrimaryKey event");
                    }

                }
            });

        });
    }

    public void positionclic(MouseEvent mouseEvent) {
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
