package main;

import java.awt.*;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 *タイトル画面処理
 */
public class Title {

	// 行間の大きさ
	protected static final int LINE_HEIGHT = 8;

	// 表示中か
	private boolean isVisible = false;

	// 選択されているコマンド番号
	private int selectedTitleMenuNo;

	// カーソルのアニメーションGIF
	private Image t_menuCursor;
	// タイトル画面
	private Image TitleImage;

	// コマンド番号
	public static final int START = 0;// はじめから
	public static final int ROAD = 1; // ロード
	public static final int OPTION = 2; // オプション

	public Title() {

		// はじめのカーソルはセーブに
		selectedTitleMenuNo = 0;

		// カーソルイメージをロード
		try {
			t_menuCursor = ImageIO.read(getClass().getResource(
					"image/menu_cursor.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		// タイトルイメージをロード
		try {
			TitleImage = ImageIO.read(getClass()
					.getResource("image/titleb.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// ウインドウを描画
	public void draw(Graphics g) {
		if (isVisible == false)
			return;

		int t_x = 220;
		int t_y = 293 + selectedTitleMenuNo * 52;

		g.drawImage(TitleImage, 0, 0, null);

		g.drawImage(t_menuCursor, t_x, t_y, null);
	}

	/**
	 * カーソルを上に移動
	 */
	public void t_upCursor() {
		// 上のときはカーソルを移動できない
		if (selectedTitleMenuNo == START)
			return;
		selectedTitleMenuNo--;
	}

	/**
	 * カーソルを下に移動
	 */
	public void t_downCursor() {
		// 下のときはカーソルを移動できない
		if (selectedTitleMenuNo == OPTION)
			return;
		selectedTitleMenuNo++;
	}

	/**
	 * 選択されているコマンド番号を返す
	 *
	 * @return コマンド番号
	 */
	public int getSelectedTitleMenuNo() {
		return selectedTitleMenuNo;
	}

	/**
	 * ウィンドウを表示 (オーバーライド)
	 */
	public void show() {
		isVisible = true;

		selectedTitleMenuNo = START;// カーソルはセーブに初期化
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
