package main;

import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Interval {
	// 行間の大きさ
	protected static final int LINE_HEIGHT = 8;

	// 表示中か
	private boolean isVisible = false;

	// ゲームオーバー画面
	private Image IntervalImage;

	public Interval() {
		// ゲームオーバーイメージをロード
		try {
			IntervalImage = ImageIO.read(getClass().getResource("image/black.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// ウインドウを描画
	public void draw(Graphics g) {
		if (isVisible == false)
			return;
		g.drawImage(IntervalImage, 0, 0, null);
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
