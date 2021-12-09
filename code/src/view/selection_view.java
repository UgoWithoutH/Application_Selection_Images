package view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Handler;

public class selection_view {

    @FXML
    private TabPane tabpane;

    @FXML
    public void initialize(){
        File f = new File(System.getProperty("user.dir")+"/ressources/Images");
        var truc = Arrays.asList(f.list());
        ScrollPane sp;
        for(var file : truc){
            sp= new ScrollPane(new ImageView(new Image(f+"\\"+file)));

            sp.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    positionclic(mouseEvent);
                }
            }
            );
            Tab tab = new Tab(file,sp);
            tabpane.getTabs().add(tab);
        }
    }


    public void positionclic(MouseEvent mouseEvent) {
        System.out.println("Numéro de Tuile en X : " + (int)mouseEvent.getX()/32);
        System.out.println("Numéro de Tuile en Y : " + (int)mouseEvent.getY()/32);
    }
}
