package view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import modele.Manager;

import java.io.IOException;

public class Preview_view {

    @FXML
    private Pane mainNode;
    private Manager manager;
    private Scene scene = Navigator.getScene();
    private static final int WINDOW_WIDTH = 950;
    private int nbColumnPreviewCanvas;
    private int nbRowsPreviewCanvas;
    private int nbColumnCanvas;

    @FXML
    public void initialize(){
    }

    public void setManager(Manager manager) {
        this.manager = manager;
    }

    public void initializePreviewView(int nbColumnCanvas, int nbRowsCanvas){
        this.nbColumnCanvas = nbColumnCanvas;
        nbColumnPreviewCanvas = nbColumnCanvas * manager.getCataloguePage().getNbPages();
        nbRowsPreviewCanvas = nbRowsCanvas;
        Canvas cv = new Canvas(nbColumnPreviewCanvas*32,nbRowsPreviewCanvas*32);
        HBox mainNodePreview = new HBox();
        VBox vb = new VBox();
        Button btnBack = new Button("Back");
        btnBack.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/FXML/Selection_view.fxml"));
                    Parent pane = fxmlLoader.load();
                    Selection_view controller = fxmlLoader.getController();
                    controller.setManager(manager);
                    controller.initializeSelectionView();
                    scene.setRoot(pane);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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

    private void initializePreviewCanvas(Canvas cv, int nbColumnCanvas, int nbRowsCancas){
        initializeImagesPreviewCanvas(cv,nbColumnCanvas,nbRowsCancas);
        InitializerCanvas.initializeLinesCanvas(cv,nbColumnCanvas,nbRowsCancas);
    }

    private void initializeImagesPreviewCanvas(Canvas cv, int nbColumn, int nbRows){
        int widthDraw = 0, heightDraw = 0;
        int nbTab = nbColumn/nbColumnCanvas;
        GraphicsContext gc = cv.getGraphicsContext2D();

        for(int i = 1; i <= nbTab; i++) {
            var page = manager.getCataloguePage().getPage(i);
            for (int x = 0; x < nbColumnCanvas; x++) {
                for (int y = 0; y < nbRowsPreviewCanvas; y++) {
                    heightDraw = heightDraw;
                    gc.drawImage(page[x][y], widthDraw, heightDraw);
                    heightDraw += 32;
                }
                widthDraw += 32;
                heightDraw = 0;
            }
        }
    }




}
