package main;

import java.awt.*;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 *ゲームオーバー画面処理
 */
public class GameOver {

	// 行間の大きさ
	protected static final int LINE_HEIGHT = 8;

	// 表示中か
	private boolean isVisible = false;

	// ゲームオーバー画面
	private Image GameOverImage;

	public GameOver() {
		// ゲームオーバーイメージをロード
		try {
			GameOverImage = ImageIO.read(getClass()
					.getResource("image/GameOver.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// ウインドウを描画
	public void draw(Graphics g) {
		if (isVisible == false)
			return;
		g.drawImage(GameOverImage, 0, 0, null);
	}

	/**
	 * ウィンドウを表示 (オーバーライド)
	 */
	public void show() {
		isVisible = true;
	}

	/**
	 * ウィンドウを隠す
	 */
	public void hide() {
		isVisible = false;
	}

	/**
	 * ウィンドウを表示中か
	 */
	public boolean isVisible() {
		return isVisible;
	}
}
