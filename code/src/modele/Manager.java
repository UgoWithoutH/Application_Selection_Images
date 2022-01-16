package modele;

import java.io.File;
import java.util.LinkedList;

public class Manager {
    private CataloguePage cataloguePage;
    private LinkedList<File> listTilesets = new LinkedList<>();

    public Manager(CataloguePage cataloguePage){
        this.cataloguePage = cataloguePage;
        cataloguePage.addPage();
    }


    public CataloguePage getCataloguePage() {return cataloguePage;}
    public void setCataloguePage(CataloguePage cataloguePage) {this.cataloguePage = cataloguePage;}

    public LinkedList<File> getListTilesets() {return listTilesets;}
}
