package com.badlogic.drop;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;


public class Drop extends Game {

    SpriteBatch batch;
    BitmapFont font;

    public void create() {
        batch = new SpriteBatch();
        // libGDX по умолчанию использует Arial шрифт.
        font = new BitmapFont();
        this.setScreen(new MainScreen(this));
    }

    public void render() {
        super.render(); // важно!
    }

    public void dispose() {
        batch.dispose();
        font.dispose();
    }

}
