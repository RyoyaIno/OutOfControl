package main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import data.*;


/**
 * キャラクター処理
 */
public class Monster implements Common {
	// キャラクターの移動スピード
	public int speed;

	// 移動確率
	public static final double PROB_MOVE = 0.30;

	// キャラクターのイメージ
	private static BufferedImage monsterImage;
	private static BufferedImage monsterImage2;

	//モンスターチップ番号
	private int monsterImageNo;
	// キャラクター番号
	private int monsterNo;

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

	// キャラクターアニメーション用スレッド
	private Thread threadAnime;

	// マップへの参照
	private Map map;

	public Monster(int x, int y, int monsterImageNo, int monsterNo, int direction, int moveType, Map map, int speed) {
		this.speed = speed  + util.boost(Main.clear_times);
		this.x = x;
		this.y = y;

		px = x * CS;
		py = y * CS;

		this.monsterImageNo = monsterImageNo;
		this.monsterNo = monsterNo;
		this.direction = direction;
		count = 0;
		this.moveType = moveType;
		this.map = map;

		// 初回の呼び出しのみイメージをロード
		if (monsterImage == null || monsterImage2 == null) {
			loadImage();
		}

		// キャラクターアニメーション用スレッド開始
		threadAnime = new Thread(new AnimationThread());
		threadAnime.start();
	}

	public void draw(Graphics g, int offsetX, int offsetY) {
		BufferedImage image;
		if(monsterImageNo == 0){
			image = monsterImage;
		} else {
			image = monsterImage2;
		}
		int cx = (monsterNo % 4) * (CS * 3);
		int cy = (monsterNo / 4) * (CS * 4);
		// countとdirectionの値に応じて表示する画像を切り替える
		g.drawImage(image, px + offsetX, py + offsetY, px + offsetX + CS,
				py + offsetY + CS, cx + count * CS, cy + direction * CS, cx
						+ CS + count * CS, cy + direction * CS + CS, null);
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
		if (!map.isHitMonster(nextX, nextY)) {
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
		if (!map.isHitMonster(nextX, nextY)) {
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
		if (!map.isHitMonster(nextX, nextY)) {
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
		if (!map.isHitMonster(nextX, nextY)) {
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

	private void loadImage() {
		// キャラクターのイメージをロード
		try {
			monsterImage = ImageIO.read(getClass().getResource("image/monster.png"));
			monsterImage2 = ImageIO.read(getClass().getResource("image/dragon.png"));
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

	/**
	 * キャラクターの移動パターンを返す
	 *
	 * @return ムーブタイプ
	 */
	public int getMoveType() {
		return moveType;
	}

	/**
	 * モンスターのムーブタイプを変更
	 * @
	 */
	public void chgMoveType(int x){
		moveType = x;
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
