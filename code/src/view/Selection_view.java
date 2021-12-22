package view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import modele.CataloguePage;
import modele.Decoupeur;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.nio.IntBuffer;
import java.util.*;

public class Selection_view {

    @FXML
    private Pane mainNode;

    private static final int WINDOW_HEIGHT = 700;
    private static final int WINDOW_WIDTH = 950;
    private static final String NOMMAGE_TAB = "tab";
    private int cptTabs = 1;
    String idTabSelected = NOMMAGE_TAB+"1";
    private LinkedList<Image> currentImages = new LinkedList<>();
    private ImageView imageViewSelected;
    private final Map<String,LinkedList<Image>> map = new HashMap<>();
    private final CataloguePage cataloguePage = new CataloguePage();
    private Image[][] tabPreview;
    private boolean draw = true;
    //canvas
    private Canvas cv;
    private int nbColumnCanvas;
    private int nbRowsCancas;
    private Image whiteSquare;
    private SplitPane mainNodeSelection;
    private LinkedList<File> listTilesets = new LinkedList<>();

    public Selection_view() throws MalformedURLException, URISyntaxException {
    }

    @FXML
    public void initialize() throws URISyntaxException, MalformedURLException {
        whiteSquare = new Image(String.valueOf(getClass().getResource("/Images/WhiteSquare32x32.png").toURI().toURL()));
        Button btn = new Button("CHARGER TILESETS");
        btn.setPrefSize(200,50);
        HBox hb = new HBox(btn);
        hb.setPrefSize(935,565);
        hb.setAlignment(Pos.CENTER);
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                List<File> listFile;
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Selection tilesets");
                fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG", "*.png"),
                        new FileChooser.ExtensionFilter("JPG","*.jpeg","*.jpeg","*.jpe","*.jfif"));
                listFile = fileChooser.showOpenMultipleDialog(mainNode.getScene().getWindow());
                if(listFile == null) return;
                for(File f : listFile){
                    listTilesets.add(f);
                }
                initializeSelectionView();
            }
        });
        mainNode.getChildren().add(hb);
    }

    public void initializeSelectionView() {
        double widthDelimitation = WINDOW_WIDTH/2;
        double widthCanvas = widthDelimitation;
        double heightCanvas = 500;
        nbColumnCanvas = (int) widthCanvas/32;
        nbRowsCancas = (int) heightCanvas/32;
        cataloguePage.addPage(nbColumnCanvas,nbRowsCancas);
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
                cataloguePage.addPage(nbColumnCanvas,nbRowsCancas);
                initializeCanvas(cv,nbColumnCanvas,nbRowsCancas);
            }
        });
        Button nextPage = new Button("Next");
        nextPage.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                cataloguePage.changePage(cataloguePage.getCptCurrentPage()+1);
                initializeCanvas(cv,nbColumnCanvas,nbRowsCancas);
            }
        });
        Button previousPage = new Button("Previous");
        previousPage.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                cataloguePage.changePage(cataloguePage.getCptCurrentPage()-1);
                initializeCanvas(cv,nbColumnCanvas,nbRowsCancas);
            }
        });
        Button btnExport = new Button("Export Tileset");
        Button btnSuppr = new Button("Erase");
        Button btnPreview = new Button("Preview");

        btnExport.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                exportImage();
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
                initializePreviewView();
            }
        });
        Label labelPage = new Label();
        labelPage.textProperty().bind(cataloguePage.cptCurrentPageProperty().asString());
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
        FlowPane affichageDecoupe = new FlowPane();
        affichageDecoupe.setAlignment(Pos.CENTER);
        affichageDecoupe.getChildren().addAll(vb,btnExport,btnSuppr,btnPreview);
        affichageDecoupe.setMaxSize(WINDOW_WIDTH/2,WINDOW_HEIGHT);
        TabPane tabpane = new TabPane();
        initializeTabs(tabpane);
        mainNodeSelection = new SplitPane(affichageDecoupe,tabpane);
        mainNodeSelection.setDividerPositions(0.21237458193979933);
        mainNodeSelection.setOrientation(Orientation.HORIZONTAL);
        mainNodeSelection.setPrefSize(WINDOW_WIDTH,WINDOW_HEIGHT/2);
        mainNode.getChildren().clear();
        mainNode.getChildren().add(mainNodeSelection);
    }

public void exportImage(){
    Image[][] tab = initializeArrayExport();
    WritableImage writableImage = new WritableImage(tab.length * 32, tab[0].length * 32);
    PixelWriter px = writableImage.getPixelWriter();

    for (int x = 0; x < tab.length; x++) {
        for (int y = 0; y < tab[0].length; y++) {
            int[] pixels = new int[32 * 32];
            PixelReader prt = tab[x][y].getPixelReader();
            PixelFormat.Type type = prt.getPixelFormat().getType();
            WritablePixelFormat<IntBuffer> format = null;
            if (type == PixelFormat.Type.INT_ARGB_PRE) {
                format = PixelFormat.getIntArgbPreInstance();
            } else {
                format = PixelFormat.getIntArgbInstance();
            }
            prt.getPixels(0, 0, 32, 32, format, pixels, 0, 32);
            px.setPixels(x * 32, y * 32, 32, 32, format, pixels, 0, 32);
        }
    }
    FileChooser f = new FileChooser();
    f.setTitle("Save tileset");
    f.setInitialFileName("tileset");
    f.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG", "*.png"),
                                   new FileChooser.ExtensionFilter("JPG","*.jpeg","*.jpeg","*.jpe","*.jfif"));
    File file = f.showSaveDialog(mainNode.getScene().getWindow());
    if(file == null) return;
    f.setInitialDirectory(file.getParentFile());
    try {
        ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "PNG", file);
    } catch (IOException e) {
        e.printStackTrace();
    }
}

//PreviewView
    public void initializePreviewView(){
        mainNode.getChildren().clear();
        int nbColumnPreviewCanvas = nbColumnCanvas * cataloguePage.getNbPages();
        int nbRowsPreviewCanvas = nbRowsCancas;
        Canvas cv = new Canvas(nbColumnPreviewCanvas*32,nbRowsPreviewCanvas*32);
        HBox mainNodePreview = new HBox();
        VBox vb = new VBox();
        Button btnBack = new Button("Back");
        btnBack.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                mainNode.getChildren().clear();
                mainNode.getChildren().add(mainNodeSelection);
            }
        });
        initializePreviewCanvas(cv,nbColumnPreviewCanvas,nbRowsPreviewCanvas);
        ScrollPane sp = new ScrollPane(cv);
        sp.setMaxSize(WINDOW_WIDTH-50,500);
        HBox hb = new HBox(sp);
        hb.setAlignment(Pos.CENTER);
        vb.getChildren().addAll(btnBack,hb);
        hb.setPrefWidth(WINDOW_WIDTH-20);
        mainNode.getChildren().add(vb);
    }

    public Image[][] initializeArrayExport(){
        var tab = new Image[cataloguePage.getNbPages()*nbColumnCanvas][nbRowsCancas];
        int realX = 0;

        for(int i = 1; i <= cataloguePage.getNbPages();i++){
            var page = cataloguePage.getPage(i);

            for(int x = 0; x < nbColumnCanvas; x++){
                for(int y = 0; y < nbRowsCancas; y++){
                    tab[realX][y] = page[x][y];
                }
                realX++;
            }
        }
        return tab;
    }

    public void initializePreviewCanvas(Canvas cv, int nbColumnCanvas, int nbRowsCancas){
        initializeImagesPreviewCanvas(cv,nbColumnCanvas,nbRowsCancas);
        initializeLinesCanvas(cv,nbColumnCanvas,nbRowsCancas);
    }

    public void initializeImagesPreviewCanvas(Canvas cv, int nbColumn, int nbRows){
        int widthDraw = 0, heightDraw = 0;
        int nbTab = nbColumn/nbColumnCanvas;
        GraphicsContext gc = cv.getGraphicsContext2D();

        for(int i = 1; i <= nbTab; i++) {
            var page = cataloguePage.getPage(i);
            for (int x = 0; x < nbColumnCanvas; x++) {
                for (int y = 0; y < nbRowsCancas; y++) {
                    heightDraw = heightDraw;
                    gc.drawImage(page[x][y], widthDraw, heightDraw);
                    heightDraw += 32;
                }
                widthDraw += 32;
                heightDraw = 0;
            }
        }
    }

// Tabs
    public void initializeTabs(TabPane tabpane){
        Decoupeur d = new Decoupeur();
        ScrollPane sp;
        int cpt = 1;

        for (var tileset : listTilesets) {
            Image tilesetImage = null;
            try {
                tilesetImage = new Image(tileset.toURI().toURL().toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            double largeurImage = tilesetImage.getWidth() / 32;
            double hauteurImage = tilesetImage.getHeight() / 32;
            LinkedList<Image> decoupe = null;
            try {
                decoupe = d.decoupe(tileset.toURI().toURL().toString(), 32, 32);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            //ScrollPane
            sp = new ScrollPane();
            GridPane gp = initializeGridPane(decoupe,tilesetImage);
            sp.setContent(gp);
            //Tab
            Tab tab = new Tab("tileset n°"+cpt, sp);
            tab.setId(NOMMAGE_TAB + cptTabs);
            map.put(tab.getId(), decoupe);
            tabpane.getTabs().add(tab);
            tabpane.getSelectionModel().selectedItemProperty().addListener(
                    new ChangeListener<Tab>() {
                        @Override
                        public void changed(ObservableValue<? extends Tab> ov, Tab t, Tab t1) {
                            if(t1 == null) return;
                            idTabSelected = t1.getId();
                            currentImages = map.get(t1.getId());
                        }
                    }
            );
            cptTabs++;
            cpt++;
        }
        currentImages = map.get("tab1");
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
            cataloguePage.addTilesInArray(imageViewSelected.getImage(),positionXDraw,positionYDraw);
            imageViewSelected = null;
        }
        else{
            cv.getGraphicsContext2D().clearRect(positionXDraw,positionYDraw,32,32);
            cv.getGraphicsContext2D().drawImage(whiteSquare,positionXDraw,positionYDraw);
            cataloguePage.deleteTilesInArray(positionXDraw,positionYDraw);
        }
        initializeLinesCanvas(cv,nbColumnCanvas,nbRowsCancas);
    }

    public void initializeCanvas(Canvas cv, int nbColumn, int nbRows){
        initializeImagesCanvas(cv, nbColumn, nbRows);
        initializeLinesCanvas(cv, nbColumn, nbRows);
    }
    public void initializeImagesCanvas(Canvas cv, int nbColumn, int nbRows){
        int widthDraw = 0, heightDraw = 0;

        GraphicsContext gc = cv.getGraphicsContext2D();

        for (int x = 0; x < nbColumn; x++){
            for(int y = 0; y < nbRows; y++){
                if(cataloguePage.getCurrentPage() == null) {
                    gc.drawImage(whiteSquare, widthDraw, heightDraw);
                }
                else{
                    gc.drawImage(cataloguePage.getCurrentPage()[x][y], widthDraw, heightDraw);
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
    //GridPane
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
