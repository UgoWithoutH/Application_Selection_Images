package modele;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.image.Image;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class CataloguePage {
    private Map<Integer, Image[][]> cataloguePage = new HashMap<>();
    private Image whiteSquare = new Image(String.valueOf(getClass().getResource("/Images/TransparentSquare32x32.png").toURI().toURL()));
    private int nbPages = 0;
    private Image[][] currentPage;
    private final int DEFAULT_COLUMN = 14;
    private final int DEFAULT_ROWS = 15;


    private IntegerProperty cptCurrentPage = new SimpleIntegerProperty();

    public CataloguePage() throws MalformedURLException, URISyntaxException {
    }

    public int getCptCurrentPage() {return cptCurrentPage.get();}
    public void setCptCurrentPage(int cptCurrentPage) {this.cptCurrentPage.set(cptCurrentPage);}
    public ReadOnlyIntegerProperty cptCurrentPageProperty(){return cptCurrentPage;}
    public Map<Integer, Image[][]> getCataloguePage() {return cataloguePage;}



    public int getNbPages() {
        return nbPages;
    }

    public Image[][] getCurrentPage() {
        return currentPage;
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

    public Image[][] getPage(int num){
        return cataloguePage.get(num);
    }

    public void addPage(){
        nbPages++;
        currentPage = initializeArrayTiles(DEFAULT_COLUMN,DEFAULT_ROWS);
        cataloguePage.put(nbPages,currentPage);
        setCptCurrentPage(nbPages);
    }

    public void changePage(int numPage){
        var page = cataloguePage.get(numPage);

        if(page != null){
            setCptCurrentPage(numPage);
            currentPage = page;
        }
    }

    public void addTilesInArray(Image image, double positionXDraw, double positionYDraw){
        int x = (int) positionXDraw/32;
        int y = (int) positionYDraw/32;
        currentPage[x][y] = image;
    }

    public void deleteTilesInArray(double positionXDraw, double positionYDraw){
        int x = (int) positionXDraw/32;
        int y = (int) positionYDraw/32;
        currentPage[x][y] = whiteSquare;
    }

}
