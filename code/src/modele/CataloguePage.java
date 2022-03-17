package modele;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.image.Image;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Catalogue Page,
 * Récupération des données de la page, modification possible,
 * ajout de page de changement de page ainsi que ajout ou suppression de tuile
 * @author Tremblay Jeremy, Vignon Ugo, Viton Antoine, Wissocq Maxime, Coudour Adrien
 */
public class CataloguePage {
    private Map<Integer, Image[][]> cataloguePage = new HashMap<>();
    private Image whiteSquare = new Image(String.valueOf(getClass().getResource("/Images/TransparentSquare32x32.png").toURI().toURL()));
    private int nbPages = 0;
    private Image[][] currentPage;
    private final int DEFAULT_COLUMN = 14;
    private final int DEFAULT_ROWS = 15;

    /**
     * Constructeur du catalogue Page ne fait rien
     * @throws MalformedURLException Url mal formée
     * @throws URISyntaxException Problème de suntax de l'exception
     */
    public CataloguePage() throws MalformedURLException, URISyntaxException {
    }

    private IntegerProperty cptCurrentPage = new SimpleIntegerProperty();
    public int getCptCurrentPage() {return cptCurrentPage.get();}
    public void setCptCurrentPage(int cptCurrentPage) {this.cptCurrentPage.set(cptCurrentPage);}
    public ReadOnlyIntegerProperty cptCurrentPageProperty(){return cptCurrentPage;}


    /**
     * Récupération du nombre de page actuel ouvert par l'utilisateur
     * @return nombre de page de l'utilisateur
     */
    public int getNbPages() {
        return nbPages;
    }

    /**
     * Récupéréation de la page actuel
     * @return une image en 2 dimension correspondant à l'image actuelle
     */
    public Image[][] getCurrentPage() {
        return currentPage;
    }

    /**
     * Initialisation des tuiles
     * Parcours le nombre de ligne et de colonne et affichage un carré transparent
     * @param nbColumn nombre de colonne de la page
     * @param nbRows nombre de ligne de la page
     * @return image en 2 dimension correspondant à l'image
     */
    public Image[][] initializeArrayTiles(int nbColumn, int nbRows){
        var tab = new Image[nbColumn][nbRows];

        for(int x = 0; x < nbColumn; x++){
            for(int y = 0; y < nbRows; y++){
                tab[x][y] = whiteSquare;
            }
        }
        return tab;
    }

    /**
     * Récupération de l'image actuel
     * @param num numéro de la page que l'on souhaite récupérer
     * @return Image en 2 dimension de l'image que l'on souhaite récupérer
     */
    public Image[][] getPage(int num){
        return cataloguePage.get(num);
    }

    /**
     * Ajout d'une page dans le catalogue de page
     */
    public void addPage(){
        nbPages++;
        currentPage = initializeArrayTiles(DEFAULT_COLUMN,DEFAULT_ROWS);
        cataloguePage.put(nbPages,currentPage);
        setCptCurrentPage(nbPages);
    }

    /**
     * Changement de la page affiché à l'utilisateur
     * @param numPage nouvelle page que l'on souhaite afficher
     */
    public void changePage(int numPage){
        var page = cataloguePage.get(numPage);

        if(page != null){
            setCptCurrentPage(numPage);
            currentPage = page;
        }
    }

    /**
     * Ajout d'une tuiles dans la liste
     * @param image Image que l'on souhaite affichée
     * @param positionXDraw position X de l'image que l'on souhaite changer pour afficher la nouvelle image
     * @param positionYDraw position Y de l'image que l'on souhaite changer pour afficher la nouvelle image
     */
    public void addTilesInArray(Image image, double positionXDraw, double positionYDraw){
        int x = (int) positionXDraw/32;
        int y = (int) positionYDraw/32;
        currentPage[x][y] = image;
    }

    /**
     * Modification de l'image par une image transparente
     * @param positionXDraw Position en X dans la liste de la tuile
     * @param positionYDraw Position en Y dans la liste de la tuile
     */
    public void deleteTilesInArray(double positionXDraw, double positionYDraw){
        int x = (int) positionXDraw/32;
        int y = (int) positionYDraw/32;
        currentPage[x][y] = whiteSquare;
    }

}
