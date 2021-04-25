package com.example.theplatform;

import android.graphics.Bitmap;

/**
 * Abstract class for the entities
 */
public abstract class GameEntity {
    protected int x,y;
    protected Bitmap image;

    protected int rowCount;
    protected int colCount;

    protected final int WIDTH;
    protected final int HEIGHT;
    protected final int width;
    protected final int height;


    public GameEntity(Bitmap image, int rowCount, int colCount, int x, int y){
        this.image = image;

        this.rowCount = rowCount;
        this.colCount = colCount;

        this.x = x;
        this.y = y;

        this.WIDTH = image.getWidth();
        this.HEIGHT = image.getHeight();

        this.width = this.WIDTH/colCount;
        this.height = this.HEIGHT/rowCount;

    }

    protected Bitmap createSubImageAt(int row, int col)  {
        // createBitmap(bitmap, x, y, width, height).
        Bitmap subImage = Bitmap.createBitmap(image, col* width, row* height ,width,height);
        return subImage;
    }

    public int getX()  {
        return this.x;
    }

    public int getY()  {
        return this.y;
    }


    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
