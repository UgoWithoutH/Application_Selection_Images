package view;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import modele.Decoupeur;

import java.io.File;
import java.util.*;

public class Selection_view {

    @FXML
    private TabPane tabpane;

    @FXML
    private FlowPane affichageDecoupe;

    private static final int WINDOW_HEIGHT = 700;
    private static final int WINDOW_WIDTH = 950;
    private static final String NOMMAGE_TAB = "tab";
    private int cptTabs = 1;
    String idTabSelected = NOMMAGE_TAB+"1";
    private LinkedList<Image> currentImages = new LinkedList<>();
    private ImageView imageViewSelected;
    private final Map<String,LinkedList<Image>> map = new HashMap<>();
    private Map<Integer,Image[][]> cataloguePage = new HashMap<>();
    private Image[][] currentPage;
    private int cptPages = 0;
    private boolean draw = true;
    private Image whiteSquare = new Image(System.getProperty("user.dir")+"/code/ressources/Images/WhiteSquare32x32.png");
    //canvas
    private Canvas cv;
    private int nbColumnCanvas;
    private int nbRowsCancas;
    //binding current page
    private IntegerProperty cptCurrentPage = new SimpleIntegerProperty();
    public int getCptCurrentPage() {return cptCurrentPage.get();}
    public void setCptCurrentPage(int cptCurrentPage) {this.cptCurrentPage.set(cptCurrentPage);}
    public ReadOnlyIntegerProperty cptCurrentPageProperty(){return cptCurrentPage;}

    @FXML
    public void initialize() {
        double widthDelimitation = WINDOW_WIDTH/2;
        double widthCanvas = widthDelimitation;
        double heightCanvas = 500;
        nbColumnCanvas = (int) widthCanvas/32;
        nbRowsCancas = (int) heightCanvas/32;

        addCataloguePage(nbColumnCanvas,nbRowsCancas);
        cv = new Canvas(widthCanvas, heightCanvas);
        cv.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                drawClickCanvas(event);
            }
        });
        initializeCanvas(cv,nbColumnCanvas,nbRowsCancas);
        VBox vb = new VBox();
        vb.setPrefSize(widthDelimitation,heightCanvas);
        Button newPage = new Button("Add Page");
        newPage.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                cv.getGraphicsContext2D().clearRect(0,0,widthCanvas,heightCanvas);
                addCataloguePage(nbColumnCanvas,nbRowsCancas);
                initializeCanvas(cv,nbColumnCanvas,nbRowsCancas);
            }
        });
        Button nextPage = new Button("Next");
        nextPage.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                changeCataloguePage(getCptCurrentPage()+1);
                affichageTab(currentPage,nbColumnCanvas,nbRowsCancas);
                initializeCanvas(cv,nbColumnCanvas,nbRowsCancas);
                affichageTab(currentPage,nbColumnCanvas,nbRowsCancas);
            }
        });
        Button previousPage = new Button("Previous");
        previousPage.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                changeCataloguePage(getCptCurrentPage()-1);
                initializeCanvas(cv,nbColumnCanvas,nbRowsCancas);
            }
        });
        Button btnExport = new Button("Export Tileset");
        Button btnSuppr = new Button("Erase");
        Button btnPreview = new Button("Preview");
        btnExport.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println("export");
            }
        });

        btnSuppr.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                draw = false;
                imageViewSelected = null;
            }
        });

        btnPreview.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

            }
        });
        Label labelPage = new Label();
        labelPage.textProperty().bind(cptCurrentPageProperty().asString());
        HBox hbTop = new HBox();
        hbTop.setAlignment(Pos.CENTER);
        HBox hb1 = new HBox();
        hb1.getChildren().addAll(previousPage,newPage,nextPage);
        HBox hb2 = new HBox();
        HBox.setMargin(hb2,new Insets(0,0,0,20));
        hb2.setAlignment(Pos.CENTER_RIGHT);
        hb2.getChildren().addAll(new Label("Page : "),labelPage);
        hbTop.getChildren().addAll(hb1,hb2);
        vb.getChildren().addAll(hbTop,cv);
        //affichageDecoupe.setStyle("-fx-border-width: 2; -fx-border-color: black; -fx-border-style: solid;");
        affichageDecoupe.getChildren().add(vb);
        affichageDecoupe.getChildren().add(btnExport);
        affichageDecoupe.getChildren().add(btnSuppr);
        affichageDecoupe.setMaxSize(WINDOW_WIDTH/2,WINDOW_HEIGHT);
        initializeTabs();
    }

public void affichageTab(Image[][] tab, int nbColumn, int nbRows){
        for(int i = 0; i < nbColumn;i++){
            for(int z = 0; z < nbRows; z++){
                System.out.print(tab[i][z]);
            }
            System.out.println();
        }
}



//Initialize Tabs
    public void initializeTabs(){
        Decoupeur d = new Decoupeur();
        File f = new File(System.getProperty("user.dir")+"/code/ressources/Images");
        var ArrayFile = Arrays.asList(f.list());
        ScrollPane sp;
        for (var file : ArrayFile) {
            File tileset = new File(System.getProperty("user.dir") + "/code/ressources/Images/" + file);
            Image tilesetImage = new Image(String.valueOf(tileset));
            double largeurImage = tilesetImage.getWidth() / 32;
            double hauteurImage = tilesetImage.getHeight() / 32;
            var decoupe = d.decoupe(tileset.getAbsolutePath(), (int) largeurImage, (int) hauteurImage);
            //ScrollPane
            sp = new ScrollPane();
            GridPane gp = initializeGridPane(decoupe,tilesetImage);
            sp.setContent(gp);
            //Tab
            Tab tab = new Tab(file, sp);
            tab.setId(NOMMAGE_TAB + cptTabs);
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
            cptTabs++;
        }
        currentImages = map.get("tab1");
    }

//Catalogue
    public void addCataloguePage(int nbColumn, int nbRows){
        cptPages++;
        currentPage = initializeArrayTiles(nbColumn,nbRows);
        cataloguePage.put(cptPages,currentPage);
        setCptCurrentPage(cptPages);
    }

    public void changeCataloguePage(int numPage){
        var page = cataloguePage.get(numPage);

        if(page != null){
            setCptCurrentPage(numPage);
            currentPage = page;
        }
    }

    public Image[][] initializeArrayTiles(int nbColumn, int nbRows){
        var tab = new Image[nbColumn][nbRows];

        for(int x = 0; x < nbColumn; x++){
            for(int y = 0; y < nbRows; y++){
                tab[x][y] = whiteSquare;
            }
        }
        return tab;
    }

    public void addTilesInArray(Image[][] tab, Image image, double positionXDraw, double positionYDraw){
        int x = (int) positionXDraw/32;
        int y = (int) positionYDraw/32;
        tab[x][y] = image;
    }

    public void deleteTilesInArray(Image[][] tab, double positionXDraw, double positionYDraw){
        int x = (int) positionXDraw/32;
        int y = (int) positionYDraw/32;
        tab[x][y] = whiteSquare;
    }


    //Canvas
    public void drawClickCanvas(MouseEvent event){
        double decalageX = 2.66;
        double decalageY = 20;

        double positionXClick = event.getSceneX()-decalageX;
        double positionYClick = event.getSceneY()-decalageY;
        int positionXDraw = ((int) positionXClick/32)*32;
        int positionYDraw = ((int) positionYClick/32)*32;

        if(draw) {
            if (imageViewSelected == null) return;
            cv.getGraphicsContext2D().clearRect(positionXDraw,positionYDraw,32,32);
            cv.getGraphicsContext2D().drawImage(imageViewSelected.getImage(), positionXDraw, positionYDraw);
            addTilesInArray(currentPage,imageViewSelected.getImage(),positionXDraw,positionYDraw);
            imageViewSelected = null;
        }
        else{
            cv.getGraphicsContext2D().clearRect(positionXDraw,positionYDraw,32,32);
            cv.getGraphicsContext2D().drawImage(whiteSquare,positionXDraw,positionYDraw);
            deleteTilesInArray(currentPage,positionXDraw,positionYDraw);
        }
        initializeLinesCanvas(cv,nbColumnCanvas,nbRowsCancas);
    }

    //initialize
    public void initializeCanvas(Canvas cv, int nbColumn, int nbRows){
        initializeImagesCanvas(cv, nbColumn, nbRows);
        initializeLinesCanvas(cv, nbColumn, nbRows);
    }
    public void initializeImagesCanvas(Canvas cv, int nbColumn, int nbRows){
        int widthDraw = 0, heightDraw = 0;

        GraphicsContext gc = cv.getGraphicsContext2D();

        for (int x = 0; x < nbColumn; x++){
            for(int y = 0; y < nbRows; y++){
                if(currentPage == null) {
                    gc.drawImage(whiteSquare, widthDraw, heightDraw);
                }
                else{
                    gc.drawImage(currentPage[x][y], widthDraw, heightDraw);
                }
                heightDraw += 32;
            }
            widthDraw += 32;
            heightDraw = 0;
        }
    }

    public void initializeLinesCanvas(Canvas cv, int nbColumn, int nbRows){
        int widthMax = ((int) cv.getWidth()/32)*32;
        int heightMax = ((int) cv.getHeight()/32)*32;
        double drawX = 0,drawY = 0;

        GraphicsContext gc = cv.getGraphicsContext2D();
        for(int i = 0; i <= nbColumn; i++){
            gc.beginPath();
            gc.moveTo(drawX,0);
            gc.lineTo(drawX,heightMax);
            gc.stroke();
            drawX += 32;
        }

        for(int i = 0; i <= nbRows; i++) {
            gc.beginPath();
            gc.moveTo(0,drawY);
            gc.lineTo(widthMax,drawY);
            gc.stroke();
            drawY += 32;
        }
    }

    public GridPane initializeGridPane(List<Image> myListDecoupe, Image tileset){
        GridPane gp = new GridPane();
        int x = 0, y = 0;
        double dimensionTile = myListDecoupe.get(0).getWidth();
        double dimensionMax = tileset.getWidth();
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
                    imageViewSelected = myImageView;
                    draw = true;
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
