package logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;

import drawing.GameScreen;
import game.GameMain;
import sharedObject.RenderableHolder;
import window.SceneManager;

public class GameLogic {

	private Queue<Bullet> pendingBullet;

	private List<Unit> gameObjectContainer;
	private static final int FPS = 60;
	public static final long LOOP_TIME = 1000000000 / FPS;

	private int gameOverCountdown = 24;
	private int gameWinCountdown = 24;

	private double maxEnemyCap;
	public static double currentEnemyWeight;
	public static boolean isBossAlive;
	public static boolean isSemiAlive;
	public static boolean killedBoss;
	public static boolean killedErrorCat;
	private int stageLevel;

	private long nextItemsSpawnTime;
	public static long relaxTime;

	private GameScreen canvas;
	private boolean isGameRunning;

	private Player player;
	private EErrorCat eErrorCat;
	private EBoss eBoss;

	public GameLogic(GameScreen canvas) {
		this.gameObjectContainer = new ArrayList<Unit>();
		this.maxEnemyCap = 6; // default enemy capacity
		GameLogic.currentEnemyWeight = 0;
		stageLevel = 1;
		GameLogic.isBossAlive = false;
		GameLogic.isSemiAlive = false;
		killedBoss = false;
		killedErrorCat = false;

		RenderableHolder.getInstance().add(new Background());
		RenderableHolder.getInstance().add(new Distance());
		player = new Player(this);
		addNewObject(player);

		spawnEnemy();

		this.canvas = canvas;
		nextItemsSpawnTime = System.nanoTime() + ThreadLocalRandom.current().nextLong(8000000000l, 10000000000l);
		pendingBullet = new ConcurrentLinkedQueue<>();

	}

	protected void addNewObject(Unit unit) {
		if (unit instanceof Enemy) {
			GameLogic.currentEnemyWeight += ((Enemy) unit).getWeight();
		}
		gameObjectContainer.add(unit);
		RenderableHolder.getInstance().add(unit);
	}

	protected void winGame() {
		this.isGameRunning = false;
		this.gameObjectContainer.clear();
		this.pendingBullet.clear();
	}

	public void startGame() {
		this.isGameRunning = true;
		new Thread(this::gameLoop, "Game Loop Thread").start();
	}

	public void stopGame() {
		this.isGameRunning = false;
		this.gameObjectContainer.clear();
		this.pendingBullet.clear();

	}

	private void gameLoop() {
		long lastLoopStartTime = System.nanoTime();
		GameLogic.relaxTime = System.nanoTime() + 6000000000l;
		GameLogic.currentEnemyWeight += 10.8;
		while (isGameRunning) {
			long elapsedTime = System.nanoTime() - lastLoopStartTime;
			if (elapsedTime >= LOOP_TIME) {
				lastLoopStartTime += LOOP_TIME;
				updateGame();
			}

			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void updateGame() {
		// TODO fill code

		if (killedErrorCat) {
			GameLogic.relaxTime = System.nanoTime() + 12000000000l;
			GameLogic.currentEnemyWeight += 21.6;

			nextItemsSpawnTime = System.nanoTime() + 11000000000l;

			addNewObject(
					new IShieldMaxCat((SceneManager.SCENE_WIDTH - RenderableHolder.shieldmaxCat.getWidth()) / 2 - 100));
			addNewObject(new IAttackCat((SceneManager.SCENE_WIDTH - RenderableHolder.attackCat.getWidth()) / 2));
			addNewObject(new IShieldRegenBall(
					(SceneManager.SCENE_WIDTH - RenderableHolder.shieldregenBall.getWidth()) / 2 + 100));

			killedErrorCat = false;
		}

		if (relaxTime >= System.nanoTime()) {
			GameLogic.currentEnemyWeight -= 0.03;
		}
		spawnEnemy();
		spawnItems();

		while (!pendingBullet.isEmpty()) {
			gameObjectContainer.add(pendingBullet.poll());

		}

		for (Unit i : gameObjectContainer) {
			i.update();
		}
		for (Unit i : gameObjectContainer) {
			for (Unit j : gameObjectContainer) {
				if (i != j && ((Unit) i).collideWith((Unit) j)) {
					((Unit) i).onCollision((Unit) j);
				}
			}
		}
		int i = 0;
		while (i < gameObjectContainer.size()) {
			if (gameObjectContainer.get(i).isDestroyed()) {
				gameObjectContainer.remove(i);
			} else {
				i++;
			}
		}
		if (player.isDestroyed()) {
			gameOverCountdown--;
		}
		if (killedBoss) {
			gameWinCountdown--;
		}
		if (gameWinCountdown == 0) {
			GameMain.winGame();
		}
		if (gameOverCountdown == 0) {
			GameMain.loseGame();
		}

		double mod = Distance.distance / 500;
		Distance.hiddenDistance += 0.5 + mod / 4;
		Distance.distance = (int) Distance.hiddenDistance;

	}

	public void addPendingBullet(Bullet a) {
		pendingBullet.add(a);
		canvas.addPendingBullet(a);
	}

	private void spawnEnemy() {
		Random r = new Random();
		this.maxEnemyCap = 6 + stageLevel * 0.85;

		if (Distance.distance >= 5000 && !isSemiAlive) {
			eErrorCat = new EErrorCat(this);
			addNewObject(eErrorCat);
			GameLogic.currentEnemyWeight += eErrorCat.getWeight();
		}
		if (Distance.distance >= 50000 && !isBossAlive) {
			eBoss = new EBoss(this);
			addNewObject(eBoss);
			GameLogic.currentEnemyWeight += eBoss.getWeight();
		}

		if (Distance.distance >= 800 * stageLevel * stageLevel) {
			stageLevel++;
		}

		if (GameLogic.currentEnemyWeight < this.maxEnemyCap) {
			int chance = r.nextInt(100) - 10000 / (Distance.distance + 1);
			if (chance < 60) {
				ECat eCat = new ECat(this, ThreadLocalRandom.current()
						.nextDouble(SceneManager.SCENE_WIDTH - RenderableHolder.eCat.getWidth()));
				addNewObject(eCat);
			} else if (chance < 75) {
				EBatEye eBatEye = new EBatEye(this, ThreadLocalRandom.current()
						.nextDouble(SceneManager.SCENE_WIDTH - RenderableHolder.eBatEye.getWidth()));
				addNewObject(eBatEye);
			} else if (chance < 90) {
				EUFO eUFO = new EUFO(this, ThreadLocalRandom.current()
						.nextDouble(SceneManager.SCENE_WIDTH - RenderableHolder.eUFO.getWidth()));
				addNewObject(eUFO);
			} else {
				EFlyingEye eFlyingEye = new EFlyingEye(this, ThreadLocalRandom.current()
						.nextDouble(SceneManager.SCENE_WIDTH - RenderableHolder.eFlyingEye.getWidth()));
				addNewObject(eFlyingEye);
			}

		}

	}

	private void spawnItems() {
		long now = System.nanoTime();
		if (this.nextItemsSpawnTime <= now) {
			this.nextItemsSpawnTime = now + ThreadLocalRandom.current().nextLong(8000000000l, 11000000000l);

			double rand = ThreadLocalRandom.current().nextDouble(100);
			if (rand <= 10) {
				addNewObject(new IAttackCat(ThreadLocalRandom.current()
						.nextDouble(SceneManager.SCENE_WIDTH - RenderableHolder.attackCat.getWidth())));
			} else if (rand <= 30) {
				addNewObject(new ITripleFireCat(ThreadLocalRandom.current()
						.nextDouble(SceneManager.SCENE_WIDTH - RenderableHolder.triplefireCat.getWidth())));
			} else if (rand <= 50) {
				addNewObject(new IPowerAttackCat(ThreadLocalRandom.current()
						.nextDouble(SceneManager.SCENE_WIDTH - RenderableHolder.powerattackCat.getWidth())));
			} else if (rand <= 60) {
				addNewObject(new IShieldMaxCat(ThreadLocalRandom.current()
						.nextDouble(SceneManager.SCENE_WIDTH - RenderableHolder.shieldmaxCat.getWidth())));
			} else if (rand <= 70) {
				addNewObject(new IShieldRegenBall(ThreadLocalRandom.current()
						.nextDouble(SceneManager.SCENE_WIDTH - RenderableHolder.shieldregenBall.getWidth())));
			} else {
				addNewObject(new IHPBox(ThreadLocalRandom.current()
						.nextDouble(SceneManager.SCENE_WIDTH - RenderableHolder.healthpack.getWidth())));
			}
		}

	}
}
