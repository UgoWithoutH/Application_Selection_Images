package view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import modele.CataloguePage;

public abstract class InitializerCanvas {

    public static void initializeImagesCanvas(Canvas cv, int nbColumn, int nbRows, CataloguePage cataloguePage, Image transparentSquare){
        int widthDraw = 0, heightDraw = 0;

        GraphicsContext gc = cv.getGraphicsContext2D();
        gc.clearRect(0,0,nbRows*64,nbColumn*64);

        for (int x = 0; x < nbColumn; x++){
            for(int y = 0; y < nbRows; y++){
                if(cataloguePage.getCurrentPage() == null) {
                    gc.drawImage(transparentSquare, widthDraw, heightDraw);
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

    public static void initializeLinesCanvas(Canvas cv, int nbColumn, int nbRows){
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
}
