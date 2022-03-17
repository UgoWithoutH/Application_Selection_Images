package modele;

import java.io.File;
import java.util.LinkedList;

/**
 * Classe de manager permet d'initialiser les tiles Sets et les catalogue
 * @author Tremblay Jeremy, Vignon Ugo, Viton Antoine, Wissocq Maxime, Coudour Adrien
 */
public class Manager {
    private CataloguePage cataloguePage;
    private LinkedList<File> listTilesets = new LinkedList<>();

    /**
     * Constructeur du manager
     * @param cataloguePage Création des catalogue de page
     */
    public Manager(CataloguePage cataloguePage){
        this.cataloguePage = cataloguePage;
        cataloguePage.addPage();
    }

    /**
     * Récupération des cataloguePage
     * @return catalogue des page actuel
     */
    public CataloguePage getCataloguePage() {return cataloguePage;}

    /**
     * Récupération de la liste des tilesSets
     * @return Liste des tilesSets actuel du manager
     */
    public LinkedList<File> getListTilesets() {return listTilesets;}
}
