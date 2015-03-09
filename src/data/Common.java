package data;

public interface Common {
	// 方向を表す定数
	public static final int LEFT = 3;
	public static final int RIGHT = 1;
	public static final int UP = 0;
	public static final int DOWN = 2;

	// ゲームの画面状態を表す定数
	public static final int TITLE = 0;
	public static final int MAIN = 1;
	public static final int GAMEOVER = 2;
	public static final int INTERVAL = 3;
	public static final int GAMECLEAR = 4;

	// チップセットのサイズ（単位：ピクセル）
	public static final int CS = 32;
}
