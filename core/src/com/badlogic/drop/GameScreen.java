package com.badlogic.drop;

import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

public class GameScreen implements Screen {
	final Drop game;

	Texture eggImage;
	Texture sovaImage;
	Sound op;
	Music bird;
	OrthographicCamera camera;
	Rectangle bucket;
	Array<Rectangle> raindrops;
	long lastEggTime;
	int eggGathered;
	TextureRegion backgroundTexture;

	public GameScreen(final Drop gam) {
		this.game = gam;

		// загрузка изображений для капли и ведра, 64x64 пикселей каждый
		eggImage = new Texture(Gdx.files.internal("egg.png"));
		sovaImage = new Texture(Gdx.files.internal("sova.png"));
		backgroundTexture = new TextureRegion(new Texture( "background.png"), 0, 0, 1252, 704 );

		// загрузка звукового эффекта падающей капли и фоновой "музыки" дождя
		op = Gdx.audio.newSound(Gdx.files.internal("op.wav"));
		bird = Gdx.audio.newMusic(Gdx.files.internal("bird.mp3"));
		bird.setLooping(true);

		// создает камеру
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);

		// создается Rectangle для представления ведра
		bucket = new Rectangle();
		// центрируем ведро по горизонтали
		bucket.x = 800 / 2 - 64 / 2;
		// размещаем на 20 пикселей выше нижней границы экрана.
		bucket.y = 20;

		bucket.width = 64;
		bucket.height = 64;

		// создает массив капель и возрождает первую
		raindrops = new Array<Rectangle>();
		spawnEggdrop();

	}

	private void spawnEggdrop() {
		Rectangle raindrop = new Rectangle();
		raindrop.x = MathUtils.random(0, 800 - 64);
		raindrop.y = 480;
		raindrop.width = 64;
		raindrop.height = 64;
		raindrops.add(raindrop);
		lastEggTime = TimeUtils.nanoTime();
	}

	@Override
	public void render(float delta) {
		// очищаем экран темно-синим цветом.
		// Аргументы для glClearColor красный, зеленый
		// синий и альфа компонент в диапазоне [0,1]
		// цвета используемого для очистки экрана.

		// сообщает камере, что нужно обновить матрицы.
		camera.update();

		// сообщаем SpriteBatch о системе координат
		// визуализации указанных для камеры.
		game.batch.setProjectionMatrix(camera.combined);

		// начитаем новую серию, рисуем ведро и
		// все капли
		game.batch.begin();
		game.batch.draw(backgroundTexture, 0, 0);
		game.font.draw(game.batch, "egg: " + eggGathered, 0, 480);
		game.batch.draw(sovaImage, bucket.x, bucket.y);
		for (Rectangle raindrop : raindrops) {
			game.batch.draw(eggImage, raindrop.x, raindrop.y);
		}
		game.batch.end();

		// обработка пользовательского ввода
		if (Gdx.input.isTouched()) {
			Vector3 touchPos = new Vector3();
			touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			camera.unproject(touchPos);
			bucket.x = touchPos.x - 64 / 2;
		}
		if (Gdx.input.isKeyPressed(Keys.LEFT))
			bucket.x -= 200 * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Keys.RIGHT))
			bucket.x += 200 * Gdx.graphics.getDeltaTime();

		// убедитесь, что ведро остается в пределах экрана
		if (bucket.x < 0)
			bucket.x = 0;
		if (bucket.x > 800 - 64)
			bucket.x = 800 - 64;

		// проверка, нужно ли создавать новую каплю
		if (TimeUtils.nanoTime() - lastEggTime > 1000000000)
			spawnEggdrop();

		// движение капли, удаляем все капли выходящие за границы экрана
		// или те, что попали в ведро. Воспроизведение звукового эффекта
		// при попадании.
		Iterator<Rectangle> iter = raindrops.iterator();
		while (iter.hasNext()) {
			Rectangle raindrop = iter.next();
			raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
			if (raindrop.y + 64 < 0)
				iter.remove();
			if (raindrop.overlaps(bucket)) {
				eggGathered++;
				op.play();
				iter.remove();
			}
		}
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		// воспроизведение фоновой музыки
		// когда отображается экрана
		bird.play();
	}

	@Override
	public void hide() {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		eggImage.dispose();
		sovaImage.dispose();
		op.dispose();
		bird.dispose();
	}

}