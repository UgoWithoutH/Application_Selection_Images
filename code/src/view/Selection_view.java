package view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import modele.CataloguePage;
import modele.Decoupeur;
import modele.Manager;

import javax.imageio.ImageIO;
import java.io.File;
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
    private boolean draw = true;
    private Manager manager;
    private Scene scene = Navigator.getScene();
    //canvas
    private Canvas cv;
    private int nbColumnCanvas;
    private int nbRowsCancas;
    private Image transparentSquare;
    private SplitPane mainNodeSelection;

    @FXML
    private void initialize(){
        try {
            transparentSquare = new Image(String.valueOf(getClass().getResource("/Images/TransparentSquare32x32.png").toURI().toURL()));
        } catch (MalformedURLException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void setManager(Manager manager){
        this.manager = manager;
    }

    public void initializeSelectionView() {
        double widthDelimitation = WINDOW_WIDTH/2;
        double widthCanvas = widthDelimitation;
        double heightCanvas = 500;
        nbColumnCanvas = (int) widthCanvas/32;
        nbRowsCancas = (int) heightCanvas/32;
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
                manager.getCataloguePage().addPage();
                initializeCanvas(cv,nbColumnCanvas,nbRowsCancas);
            }
        });
        Button nextPage = new Button("Next");
        nextPage.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                manager.getCataloguePage().changePage(manager.getCataloguePage().getCptCurrentPage()+1);
                initializeCanvas(cv,nbColumnCanvas,nbRowsCancas);
            }
        });
        Button previousPage = new Button("Previous");
        previousPage.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                manager.getCataloguePage().changePage(manager.getCataloguePage().getCptCurrentPage()-1);
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
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXML/Preview_view.fxml"));
                    Pane pane = fxmlLoader.load();
                    Preview_view preview_view = fxmlLoader.getController();
                    preview_view.setManager(manager);
                    preview_view.initializePreviewView(nbColumnCanvas,nbRowsCancas);
                    scene.setRoot(pane);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        Label labelPage = new Label();
        labelPage.textProperty().bind(manager.getCataloguePage().cptCurrentPageProperty().asString());
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

private void exportImage(){
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

    private Image[][] initializeArrayExport(){
        CataloguePage cataloguePage = manager.getCataloguePage();

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

// Tabs
    private void initializeTabs(TabPane tabpane){
        Decoupeur d = new Decoupeur();
        ScrollPane sp;
        int cpt = 1;

        for (var tileset : manager.getListTilesets()) {
            Image tilesetImage = new Image(tileset.toString());
            double largeurImage = tilesetImage.getWidth() / 32;
            double hauteurImage = tilesetImage.getHeight() / 32;
            var decoupe = d.decoupe(tileset.toString(), 32, 32);
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
    private void drawClickCanvas(MouseEvent event){
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
            manager.getCataloguePage().addTilesInArray(imageViewSelected.getImage(),positionXDraw,positionYDraw);
            imageViewSelected = null;
        }
        else{
            cv.getGraphicsContext2D().clearRect(positionXDraw,positionYDraw,32,32);
            cv.getGraphicsContext2D().drawImage(transparentSquare,positionXDraw,positionYDraw);
            manager.getCataloguePage().deleteTilesInArray(positionXDraw,positionYDraw);
        }
        InitializerCanvas.initializeLinesCanvas(cv,nbColumnCanvas,nbRowsCancas);
    }

    private void initializeCanvas(Canvas cv, int nbColumn, int nbRows){
        InitializerCanvas.initializeImagesCanvas(cv, nbColumn, nbRows,manager.getCataloguePage(),transparentSquare);
        InitializerCanvas.initializeLinesCanvas(cv, nbColumn, nbRows);
    }

    //GridPane
    private GridPane initializeGridPane(List<Image> myListDecoupe, Image tileset){
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
