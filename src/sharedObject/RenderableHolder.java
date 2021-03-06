package sharedObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.AudioClip;
import javafx.scene.text.Font;

public class RenderableHolder {
	private static final RenderableHolder instance = new RenderableHolder();

	private List<IRenderable> entities;
	private Comparator<IRenderable> comparator;

	public static Image ship, eErrorCat, eBoss, eUFO, eBatEye, eFlyingEye, eCat, bullet, background, backgroundMM,
			backgroundW, healthpack, bossBullet, bossPower, bossLow, roundBulletR, errorCatBullet, BatBullet, beamSmallG, catBullet, sparkArr[], powerAttack, exploArr[], shieldmaxCat, shieldregenBall,
			attackCat, triplefireCat, powerattackCat, coin, gem;

	public static Font inGameFont, inGameFontSmall, titleFont, menuFont, tutorialFont;

	public static AudioClip fireBall, explosion, explosion2, powerAttackLaunch, laser, hit, hit2;

	public static AudioClip[] hits, explosions;

	public static MediaPlayer bgm, gameOverMusic, mainMenuMusic, gameWinnerMusic;

	public RenderableHolder() {
		entities = Collections.synchronizedList(new ArrayList<>());
		comparator = (IRenderable o1, IRenderable o2) -> {
			if (o1.getZ() > o2.getZ()) {
				return 1;
			}
			return -1;
		};
	}

	public static RenderableHolder getInstance() {
		return instance;
	}

	public static void loadResource() throws LoadUnableException {

		ship = imageLoader("player/Balloon.png");

		eErrorCat = imageLoader("enemy/ErrorCat220.gif");
		eBoss = imageLoader("enemy/Duck.gif");
		eUFO = imageLoader("enemy/P_UFO175.gif");
		eCat = imageLoader("enemy/Cat3.gif");
		eBatEye= imageLoader("enemy/BatEye.gif");
		eFlyingEye = imageLoader("enemy/halloween.gif");

		exploArr = new Image[12];
		for (int i = 0; i < 12; i++) {
			exploArr[i] = imageLoader("explosion/" + i + ".gif");
		}

		sparkArr = new Image[4];
		for (int i = 0; i < 4; i++) {
			sparkArr[i] = imageLoader("spark/" + i + ".png");
		}

		bullet = imageLoader("bullet/BalloonBullet.gif");
		powerAttack = imageLoader("bullet/fireball.gif");
		bossBullet = imageLoader("bullet/BossBullet1.gif");
		bossPower = imageLoader("bullet/bossPower.png");
		bossLow = imageLoader("bullet/bossLow.png");

		roundBulletR = imageLoader("bullet/PBullet.gif");
		errorCatBullet = imageLoader("bullet/ErrorBullet2.gif");
		BatBullet = imageLoader("bullet/BatBullet.png");
		beamSmallG = imageLoader("bullet/beamSmallG.png");
		catBullet = imageLoader("bullet/CatBullet3.png");

		background = imageLoader("background/SkyWide.jpg");
		backgroundMM = imageLoader("background/SkyWide.jpg");
		backgroundW = imageLoader("background/SkyWide.jpg");

		attackCat = imageLoader("items/UpBulletCat90.gif");
		triplefireCat = imageLoader("items/Triple2.gif");
		powerattackCat = imageLoader("items/MadCat90.gif");
		healthpack = imageLoader("items/Heart50.png");
		shieldmaxCat = imageLoader("items/StrongCat90.gif");
		shieldregenBall = imageLoader("items/Pokeball2.gif");

		bgm = mediaPlayerLoader("song/megalovania_intro.mp3");
		fireBall = audioClipLoader("song/Fire_Ball.mp3");
		laser = audioClipLoader("song/laser.wav");
		hit = audioClipLoader("song/hit.wav");
		hit2 = audioClipLoader("song/hit2.wav");
		gameWinnerMusic = mediaPlayerLoader("song/GameWinner.mp3");
		gameOverMusic = mediaPlayerLoader("song/GameLoser.mp3");
		mainMenuMusic = mediaPlayerLoader("song/NewHope.mp3");
		explosion = audioClipLoader("song/Explosion.wav");
		explosion2 = audioClipLoader("song/Explosion2.wav");
		powerAttackLaunch = audioClipLoader("song/PowerAttack.mp3");

		fireBall.setVolume(0.35);
		laser.setVolume(0.1);
		hit.setVolume(0.12);
		hit2.setVolume(0.08);
		explosion.setVolume(0.25);
		explosion2.setVolume(0.4);

		hits = new AudioClip[] { hit, hit2 };
		explosions = new AudioClip[] { explosion, explosion2 };
		
		inGameFont = fontLoader("font/Pusab.ttf", 30);
		inGameFontSmall = fontLoader("font/Pusab.ttf", 15);
		titleFont = fontLoader("font/Pusab.ttf", 70);
		menuFont = fontLoader("font/Pusab.ttf", 35);
		tutorialFont = fontLoader("font/Pusab.ttf", 18);

	}

	public void add(IRenderable entity) {
		entities.add(entity);
		Collections.sort(entities, comparator);
	}

	public void update() {
		for (int i = entities.size() - 1; i >= 0; i--) {
			if (entities.get(i).isDestroyed())
				entities.remove(i);
		}
	}

	public List<IRenderable> getEntities() {
		return entities;
	}

	public void clear() {
		entities.clear();
	}

	private static Image imageLoader(String path) throws LoadUnableException {
		try {
			return new Image(ClassLoader.getSystemResource(path).toString());
		} catch (Exception e) {
			throw new LoadUnableException(path);
		}
	}

	private static AudioClip audioClipLoader(String path) throws LoadUnableException {
		try {
			return new AudioClip(ClassLoader.getSystemResource(path).toString());
		} catch (Exception e) {
			throw new LoadUnableException(path);
		}
	}

	private static MediaPlayer mediaPlayerLoader(String path) throws LoadUnableException {
		try {
			return new MediaPlayer(new Media(ClassLoader.getSystemResource(path).toString()));
		} catch (Exception e) {
			throw new LoadUnableException(path);
		}
	}

	private static Font fontLoader(String path, double size) throws LoadUnableException {
		try {
			return Font.loadFont(ClassLoader.getSystemResource(path).toString(), size);
		} catch (Exception e) {
			throw new LoadUnableException(path);
		}
	}
}
