package main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;


import data.*;


/**
 * キャラクター処理
 */
public class Chara implements Common {
	// キャラクターの移動スピード
	public int speed = 3;

	// 移動確率
	public static final double PROB_MOVE = 0.02;

	// キャラクターのイメージ
	private static BufferedImage charaImage;

	// キャラクター番号
	private int charaNo;

	// キャラクターの座標
	public int x, y; // 単位：マス
	private int px, py; // 単位：ピクセル

	// キャラクターの向いている方向（LEFT,RIGHT,UP,DOWNのどれか）
	private int direction;

	// キャラクターのアニメーションカウンタ
	public int count;

	// 移動中（スクロール中）か
	public boolean isMoving;
	// 移動中の場合の移動ピクセル数
	public int movingLength;

	// 移動方法
	private int moveType;

	// 話すメッセージ
	private String message;

	// キャラクターアニメーション用スレッド
	private Thread threadAnime;

	// マップへの参照
	private Map map;

	/**
	 * コンストラクタ
	 * @param x
	 * @param y
	 * @param charaNo
	 * @param direction
	 * @param moveType
	 * @param map
	 */
	public Chara(int x, int y, int charaNo, int direction, int moveType, Map map) {
		this.x = x;
		this.y = y;

		px = x * CS;
		py = y * CS;

		this.charaNo = charaNo;
		this.direction = direction;
		count = 0;
		this.moveType = moveType;
		this.map = map;

		// 初回の呼び出しのみイメージをロード
		if (charaImage == null) {
			loadImage();
		}

		// キャラクターアニメーション用スレッド開始
		threadAnime = new Thread(new AnimationThread());
		threadAnime.start();
	}

	/**
	 * スピード有のコンストラクタ
	 * @param x
	 * @param y
	 * @param charaNo
	 * @param direction
	 * @param moveType
	 * @param map
	 * @param speed
	 */
	public Chara(int x, int y, int charaNo, int direction, int moveType, Map map, int speed) {
		this.speed = speed;
		this.x = x;
		this.y = y;

		px = x * CS;
		py = y * CS;

		this.charaNo = charaNo;
		this.direction = direction;
		count = 0;
		this.moveType = moveType;
		this.map = map;

		// 初回の呼び出しのみイメージをロード
		if (charaImage == null) {
			loadImage();
		}

		// キャラクターアニメーション用スレッド開始
		threadAnime = new Thread(new AnimationThread());
		threadAnime.start();
	}
	public void draw(Graphics g, int offsetX, int offsetY) {
		int cx = (charaNo % 4) * (CS * 3);
		int cy = (charaNo / 4) * (CS * 4);
		// countとdirectionの値に応じて表示する画像を切り替える
		g.drawImage(charaImage, px + offsetX, py + offsetY, px + offsetX + CS,
				py + offsetY + CS, cx + count * CS, cy + direction * CS, cx
						+ CS + count * CS, cy + direction * CS + CS, null);
	}

	/**
	 * 加速処理
	 * @return int 加速後の速さ
	 */
	public int upSpeed(){
		this.speed++;
		return this.speed;
	}

	/**
	 * 移動処理。
	 *
	 * @return 1マス移動が完了したらtrueを返す。移動中はfalseを返す。
	 */
	public boolean move() {
		switch (direction) {
		case LEFT:
			if (moveLeft()) {
				// 移動が完了した
				return true;
			}
			break;
		case RIGHT:
			if (moveRight()) {
				// 移動が完了した
				return true;
			}
			break;
		case UP:
			if (moveUp()) {
				// 移動が完了した
				return true;
			}
			break;
		case DOWN:
			if (moveDown()) {
				// 移動が完了した
				return true;
			}
			break;
		}
		// 移動が完了していない
		return false;
	}

	/**
	 * 左へ移動する。
	 *
	 * @return 1マス移動が完了したらtrueを返す。移動中はfalseを返す。
	 */
	public boolean moveLeft() {
		// 1マス先の座標
		int nextX = x - 1;
		int nextY = y;
		direction = LEFT;
		if (nextX < 0) {
			nextX = 0;
		}
		// その場所に障害物がなければ移動を開始
		if (!map.isHit(nextX, nextY)) {
			// SPEEDピクセル分移動
			px -= this.speed;
			if (px < 0) {
				px = 0;
			}
			// 移動距離を加算
			movingLength += this.speed;
			// 移動が1マス分を超えていたら
			if (movingLength >= CS) {
				// 移動する
				x--;
				px = x * CS;
				// 移動が完了
				isMoving = false;
				return true;
			}
		} else {
			isMoving = false;
			// 元の位置に戻す
			px = x * CS;
			py = y * CS;
		}
		return false;
	}

	/**
	 * 右へ移動する。
	 *
	 * @return 1マス移動が完了したらtrueを返す。移動中はfalseを返す。
	 */
	public boolean moveRight() {
		// 1マス先の座標
		int nextX = x + 1;
		int nextY = y;
		direction = RIGHT;
		if (nextX > map.getCol() - 1) {
			nextX = map.getCol() - 1;
		}
		// その場所に障害物がなければ移動を開始
		if (!map.isHit(nextX, nextY)) {
			// SPEEDピクセル分移動
			px += this.speed;
			if (px > map.getWidth() - CS) {
				px = map.getWidth() - CS;
			}
			// 移動距離を加算
			movingLength += this.speed;
			// 移動が1マス分を超えていたら
			if (movingLength >= CS) {
				// 移動する
				x++;
				px = x * CS;
				// 移動が完了
				isMoving = false;
				return true;
			}
		} else {
			isMoving = false;
			px = x * CS;
			py = y * CS;
		}
		return false;
	}

	/**
	 * 上へ移動する。
	 *
	 * @return 1マス移動が完了したらtrueを返す。移動中はfalseを返す。
	 */
	public boolean moveUp() {
		// 1マス先の座標
		int nextX = x;
		int nextY = y - 1;
		direction = UP;
		if (nextY < 0) {
			nextY = 0;
		}
		// その場所に障害物がなければ移動を開始
		if (!map.isHit(nextX, nextY)) {
			// SPEEDピクセル分移動
			py -= this.speed;
			if (py < 0)
				py = 0;
			// 移動距離を加算
			movingLength += this.speed;
			// 移動が1マス分を超えていたら
			if (movingLength >= CS) {
				// 移動する
				y--;
				py = y * CS;
				// 移動が完了
				isMoving = false;
				return true;
			}
		} else {
			isMoving = false;
			px = x * CS;
			py = y * CS;
		}
		return false;
	}

	/**
	 * 下へ移動する。
	 *
	 * @return 1マス移動が完了したらtrueを返す。移動中はfalseを返す。
	 */
	public boolean moveDown() {
		// 1マス先の座標
		int nextX = x;
		int nextY = y + 1;
		direction = DOWN;
		if (nextY > map.getRow() - 1) {
			nextY = map.getRow() - 1;
		}
		// その場所に障害物がなければ移動を開始
		if (!map.isHit(nextX, nextY)) {
			// SPEEDピクセル分移動
			py += this.speed;
			if (py > map.getHeight() - CS) {// マップを超えて移動しない
				py = map.getHeight() - CS;
			}
			// 移動距離を加算
			movingLength += this.speed;
			// 移動が1マス分を超えていたら
			if (movingLength >= CS) {
				// 移動する
				y++;
				py = y * CS;
				// 移動が完了
				isMoving = false;
				return true;
			}
		} else {
			isMoving = false;
			px = x * CS;
			py = y * CS;
		}
		return false;
	}

	/**
	 * キャラクターが向いている方向のとなりにキャラクターがいるか調べる
	 *
	 * @return キャラクターがいたらそのCharaオブジェクトを返す
	 */
	public Chara talkWith() {
		int nextX = 0;
		int nextY = 0;
		// キャラクターが向いている方向の一歩となりの座標
		switch (direction) {
		case LEFT:
			nextX = x - 1;
			nextY = y;
			break;
		case RIGHT:
			nextX = x + 1;
			nextY = y;
			break;
		case UP:
			nextX = x;
			nextY = y - 1;
			break;
		case DOWN:
			nextX = x;
			nextY = y + 1;
			break;
		}
		// その方向にキャラクターがいるか調べる
		Chara chara;
		chara = map.charaCheck(nextX, nextY);
		// キャラクターがいれば話しかけたキャラクターを向ける
		if (chara != null) {
			switch (direction) {
			case LEFT:
				chara.setDirection(RIGHT);
				break;
			case RIGHT:
				chara.setDirection(LEFT);
				break;
			case UP:
				chara.setDirection(DOWN);
				break;
			case DOWN:
				chara.setDirection(UP);
				break;
			}
		}
		return chara;
	}

	/**
	 * あしもとに宝箱があるか調べる
	 *
	 * @return あしもとにあるTreasureEventオブジェクト
	 */
	public TreasureEvent search() {
		data.Event event = map.eventCheck(x, y);
		if (event instanceof TreasureEvent) {
			return (TreasureEvent) event;
		}
		return null;
	}

	private void loadImage() {
		// キャラクターのイメージをロード
		try {
			charaImage = ImageIO.read(getClass().getResource("image/yusya.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getPx() {
		return px;
	}

	public int getPy() {
		return py;
	}

	public void setDirection(int dir) {
		direction = dir;
	}

	public boolean isMoving() {
		return isMoving;
	}

	public void setMoving(boolean flag) {
		isMoving = flag;
		// 移動距離を初期化
		movingLength = 0;
	}

	/*
	 * キャラクターのメッセージを返す
	 *
	 * @return メッセージ
	 */
	public String getMessage() {
		return message;
	}

	/*
	 * キャラクターメッセージをセット(手動)
	 *
	 * @param メッセージ
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/*
	 * キャラクターの移動パターンを返す
	 *
	 * @return ムーブタイプ
	 */
	public int getMoveType() {
		return moveType;
	}

	// アニメーションクラス
	private class AnimationThread extends Thread {
		@Override
		public void run() {
			while (true) {
					// countを切り替える
					if (count == 0) {
						count = 1;
					} else if (count == 1) {
						count = 2;
					} else {
						count = 0;
					}
				// 300ミリ秒休止＝300ミリ秒おきにキャラクターの絵を切り替える
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public int getdirection() {
		return direction;
	}

}
