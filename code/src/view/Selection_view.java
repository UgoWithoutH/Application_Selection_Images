package view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import modele.Decoupeur;

import java.io.File;
import java.util.*;

public class Selection_view {

    @FXML
    private TabPane tabpane;

    @FXML
    private FlowPane affichageDecoupe;

    private static final String NOMMAGE_TAB = "tab";
    private static final int WINDOW_HEIGHT = 700;
    String idTabSelected = NOMMAGE_TAB+"1";
    private LinkedList<Image> currentImages = new LinkedList<>();
    private final LinkedList<Image> selectedimages = new LinkedList<>();
    private Map<String,LinkedList<Image>> map = new HashMap<>();
    private Canvas cv;
    private int cptPixelY = 0;
    private int cptPixelX = 0;
    private boolean drawable = true;


    @FXML
    public void initialize() {
        int cpt = 1;
        double widthCanvas = 200;
        double heightCanvas = 500;

        cv = new Canvas(widthCanvas, heightCanvas);
        Button btnExport = new Button("Export tileset");
        Button btnSuppr = new Button("UNDO");
        btnExport.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("export");
            }
        });

        btnSuppr.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(!selectedimages.isEmpty()) {
                    if(cptPixelY == 0){
                        if(cptPixelX != 0){
                            cptPixelX -= 32;
                            cptPixelY = ((int) (cv.getHeight()/32))*32;
                        }
                    }
                    if(!(cptPixelY == 0 && cptPixelX == 0)){
                        selectedimages.removeLast();
                        cv.getGraphicsContext2D().clearRect(cptPixelX, cptPixelY -32,32,32);
                        cptPixelY -=32;
                    }
                    if(cptPixelY == 0 && cptPixelX == 0){
                        drawImagesUndo(cv,selectedimages);
                    }
                }
            }
        });
        //affichageDecoupe.setStyle("-fx-border-width: 2; -fx-border-color: black; -fx-border-style: solid;");
        affichageDecoupe.getChildren().add(cv);
        affichageDecoupe.getChildren().add(btnExport);
        affichageDecoupe.getChildren().add(btnSuppr);
        affichageDecoupe.setMaxWidth(widthCanvas);
        affichageDecoupe.setMaxHeight(WINDOW_HEIGHT);
        Decoupeur d = new Decoupeur();
        File f = new File(System.getProperty("user.dir") + "/ressources/Images");
        var ArrayFile = Arrays.asList(f.list());
        ScrollPane sp;
        for (var file : ArrayFile) {
            File tileset = new File(System.getProperty("user.dir") + "/ressources/Images/" + file);
            Image tilesetImage = new Image(String.valueOf(tileset));
            double largeurImage = tilesetImage.getWidth() / 32;
            double hauteurImage = tilesetImage.getHeight() / 32;
            var decoupe = d.decoupe(tileset.getAbsolutePath(), (int) largeurImage, (int) hauteurImage);

            //ScrollPane
            sp = new ScrollPane(); //new ImageView(new Image(f + "\\" + file))

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
                            currentImages = map.get(t1.getId());
                        }
                    }
            );
            cpt++;
        }
        currentImages = map.get("tab1");
    }

    public void drawImagesUndo(Canvas cv, LinkedList<Image> myList){
        int maxTilesY = (int) cv.getHeight()/32;
        int maxTilesX = (int) cv.getWidth()/32;
        int maxTiles = (maxTilesY*maxTilesX);
        int i = 0;
        int numberElement = myList.size();
        int z;
        int numberElementIteration;
        LinkedList<Image> imagesDrawable = new LinkedList<>();

        if(numberElement >= maxTiles){
            z = numberElement - maxTiles;
            numberElementIteration = maxTiles;
            while(i != maxTiles){
                imagesDrawable.add(myList.get(z));
                i++;
                z++;
            }
        }
        else {
            z = 0;
            numberElementIteration = numberElement;
            while (i != numberElement) {
                imagesDrawable.add(myList.get(z));
                i++;
                z++;
            }
        }
        for(i = 0; i<numberElementIteration;i++){
            if((int) cv.getHeight()/32 == cptPixelY/32) {
                cptPixelY = 0;
                cptPixelX += 32;
            }
            cv.getGraphicsContext2D().drawImage(imagesDrawable.get(i),cptPixelX,cptPixelY);
            cptPixelY+= 32;
            System.out.println(i);
        }
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
            ImageView myImageView = new ImageView(image);
            myImageView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if((int) cv.getHeight()/32 == cptPixelY/32) {
                        if (((int) cv.getWidth() / 32) == ((cptPixelX+32)/32)) {
                            cv.getGraphicsContext2D().clearRect(0, 0, cv.getWidth(), cv.getHeight());
                            cptPixelY = 0;
                            cptPixelX = 0;
                        } else {
                            cptPixelX += 32;
                            cptPixelY = 0;
                        }

                    }
                    cv.getGraphicsContext2D().drawImage(myImageView.getImage(),cptPixelX, cptPixelY);
                    selectedimages.add(myImageView.getImage());
                    cptPixelY += 32;
                }
            });
            gp.add(myImageView,x,y);
            currentDimension += dimensionTile;
            x++;
        }
        gp.setGridLinesVisible(true);
        return gp;
    }
}
