package view;

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

public class selection_view {

    @FXML
    private TabPane tabpane;

    @FXML
    public void initialize(){
        File f = new File(System.getProperty("user.dir")+"/ressources/Images");
        var truc = Arrays.asList(f.list());
        for(var file : truc){
            Tab tab = new Tab(file,
                    new ScrollPane(
                         new ImageView(new Image(f+"\\"+file))
                    ));
            tabpane.getTabs().add(tab);
        }
    }
}
