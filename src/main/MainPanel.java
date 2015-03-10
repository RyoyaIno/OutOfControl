package main;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;


import data.*;


/**
 * メインのクラス
 */
class MainPanel extends JPanel implements KeyListener, Runnable, Common {
	// パネルサイズ
	public static final int WIDTH = 640;
	public static final int HEIGHT = 480;
	// １フレームの時間（50fpsなので1フレーム20ms）
	private static final int PERIOD = 20;
	// デバッグモード(trueだと座標などが表示される)
	private static final boolean DEBUG_MODE = false;
	// 現在のゲームモード(初手タイトル画面)
	private int gameState = TITLE;
	// マップ
	private Map[] maps;
	// 現在のマップナンバー
	private int mapNo;
	// Charaクラスからエリ(主人公)宣言
	private Chara syujinkou;

	// アクションキー
	private ActionKey leftKey;
	private ActionKey rightKey;
	private ActionKey upKey;
	private ActionKey downKey;
	private ActionKey spaceKey;
	private ActionKey shiftKey;
	// メニュー画面用アクションキー
	private ActionKey menuLeftKey;
	private ActionKey menuRightKey;
	private ActionKey menuUpKey;
	private ActionKey menuDownKey;

	// ゲームループ
	private Thread gameLoop;

	// 乱数生成器
	private Random rand = new Random();

	// メッセージウインドウ
	private MessageWindow messageWindow;
	// タイトル
	private Title title;
	// インターバル
	private Interval interval;
	//ゲームオーバー
	private GameOver gameOver;
	//ゲームクリア
	private GameClear gameClear;
	//主人公のスピード
	private static int speed;

	// メッセージウインドウを表示する領域
	private static Rectangle MSSG_RECT = new Rectangle(20, 400, WIDTH - 40, 80);

	// サウンドエンジン
	private MidiEngine midiEngine = new MidiEngine();
	private WaveEngine waveEngine = new WaveEngine();

	// BGM名
	private static final String[] bgmNames = { "OutOfControl","Start","スパイラル","ErogenousRock","GameOver"};
	private static final String[] bgmFiles = { "bgm/OutOfControl.mid", "bgm/Start.mid","bgm/スパイラル.mid","bgm/ErogenousRock.mid","bgm/gameover.mid" };

	// サウンド名
	private static final String[] soundNames = { "OutOfControl", "treasure", "door", "step","pi", "phoon","kick1","kick2","dragon","恐怖","item","scream-man","scream-woman","break","trumpet"};
	private static final String[] soundFiles = { "sound/OutOfControl.wav", "sound/treasure.wav","sound/door.wav", "sound/step.wav", "sound/beep.wav","sound/phoon.wav","sound/kick-middle1.wav","sound/kick-high1.wav" ,"sound/dragon.wav", "sound/hora-.wav","sound/item.wav","sound/scream-man.wav","sound/scream-woman.wav","sound/glass-break2.wav","sound/trumpet1.wav"};

	// ダブルバッファリング用
	private Graphics dbg;
	private Image dbImage = null;

	//プレイ時間の取得用
	private static long time;

	public MainPanel() {

		// パネルの推奨サイズを設定
		setPreferredSize(new Dimension(WIDTH, HEIGHT));

		// パネルがキー操作を受け付けるようにする
		setFocusable(true);
		addKeyListener(this);

		// アクションキー生成
		leftKey = new ActionKey();
		rightKey = new ActionKey();
		upKey = new ActionKey();
		downKey = new ActionKey();
		spaceKey = new ActionKey(ActionKey.DETECT_INITIAL_PRESS_ONLY);// 押し続けても意味なくする
		shiftKey = new ActionKey(ActionKey.DETECT_INITIAL_PRESS_ONLY);
		// メニュー画面版
		menuLeftKey = new ActionKey(ActionKey.DETECT_INITIAL_PRESS_ONLY);
		menuRightKey = new ActionKey(ActionKey.DETECT_INITIAL_PRESS_ONLY);
		menuUpKey = new ActionKey(ActionKey.DETECT_INITIAL_PRESS_ONLY);
		menuDownKey = new ActionKey(ActionKey.DETECT_INITIAL_PRESS_ONLY);

		// メッセージウインドウの追加
		messageWindow = new MessageWindow(MSSG_RECT);
		// タイトルの追加
		title = new Title();
		// ゲームオーバーの追加
		gameOver = new GameOver();
		// インターバルの追加
		interval = new Interval();
		// ゲームクリアの追加
		gameClear = new GameClear();

		// サウンドをロード
		loadSound();
		title.show();

		// ゲームループ開始
		gameLoop = new Thread(this);
		gameLoop.start();
	}

	public void run() {// ゲームループ
		long beforeTime, timeDiff, sleepTime;
		beforeTime = System.currentTimeMillis();

		while (true) {

			// ゲーム状態を更新
			gameUpdate();
			// レンダリング
			gameRender();
			// 画面に描画
			paintScreen();

			timeDiff = System.currentTimeMillis() - beforeTime;
			sleepTime = PERIOD - timeDiff; // このフレームの残り時間

			// 最低でも5msは休止を入れる
			if (sleepTime <= 0) {
				sleepTime = 5;
			}
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			beforeTime = System.currentTimeMillis();
		}
	}

	private void gameUpdate() {
		switch (gameState) {
		case TITLE:
			// タイトル画面のキー入力をチェック
			titleProcessInput();
			break;
		case MAIN:
			checkInput();
			if (!messageWindow.isVisible()) {// メッセージウインドウとが表示されていないなら
				// 主人公移動処理
				syujinkouMove();
				// キャラ移動処理
				charaMove();
				// モンスターの移動処理
				moveMonster();
			}
			break;
		case GAMEOVER:
			gameOverInput();
			break;
		case INTERVAL:
			//noInput
			break;
		case GAMECLEAR:
			gameOverInput();
			break;
		}
	}

	/*
	 * バッファにレンダリング
	 */
	private void gameRender() {

		// 初回呼び出し時にダブルバッファリング用オブジェクトを作成
		if (dbImage == null) {
			// バッファーイメージ
			dbImage = createImage(WIDTH, HEIGHT);
			if (dbImage == null) {
				return;
			} else {
				// バッファーイメージの描画オブジェクト
				dbg = dbImage.getGraphics();
			}
		}
		switch (gameState) {
		case TITLE:
			titleRender();
			break;
		case MAIN:
			mainRender();
			break;
		case GAMEOVER:
			gameOverRender();
			break;
		case INTERVAL:
			intervalRender();
			break;
		case GAMECLEAR:
			gameClearRender();
			break;
		}
	}

	private void titleRender() {
		// BGMを再生
		waveEngine.play("OutOfControl");
		// タイトルを描画
		title.draw(dbg);
	}

	private void gameOverRender(){
		// BGMを再生
		midiEngine.play("GameOver");
		// タイトルを描画
		gameOver.draw(dbg);
	}

	static boolean intervalFlg = false;
	private void intervalRender(){
		// タイトルを描画
		interval.draw(dbg);
		// BGMを再生
		waveEngine.play("恐怖");
		//停止
		if(intervalFlg){
			try {
				Thread.sleep(300);
				waveEngine.play("scream-woman");
				gameState = MAIN;
				interval.hide();
				intervalFlg = true;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		intervalFlg = true;
	}

	private void gameClearRender(){
		//BGMを再生
		waveEngine.play("OutOfControl");
		//ゲームクリア画面描画
		gameClear.draw(dbg);
	}

	// バッファをクリア
	private void mainRender() {

		dbg.setColor(Color.WHITE);
		dbg.fillRect(0, 0, WIDTH, HEIGHT);
		// X方向のオフセットを計算
		int offsetX = MainPanel.WIDTH / 2 - syujinkou.getPx();
		// マップの端ではスクロールしないようにする
		offsetX = Math.min(offsetX, 0);
		offsetX = Math.max(offsetX, MainPanel.WIDTH - maps[mapNo].getWidth());
		// Y方向のオフセットを計算
		int offsetY = MainPanel.HEIGHT / 2 - syujinkou.getPy();
		// マップの端ではスクロールしないようにする
		offsetY = Math.min(offsetY, 0);
		offsetY = Math.max(offsetY, MainPanel.HEIGHT - maps[mapNo].getHeight());
		// マップを描く
		// キャラクターはマップが描いてくれる
		maps[mapNo].draw(dbg, offsetX, offsetY);
		// メッセージウインドウを描画
		messageWindow.draw(dbg);

		// デバッグ情報の表示
		if (DEBUG_MODE) {
			Font font = new Font("SansSerif", Font.BOLD, 16);
			dbg.setFont(font);
			dbg.setColor(Color.YELLOW);
			dbg.drawString(
					maps[mapNo].getMapName() + " (" + maps[mapNo].getCol()
							+ "," + maps[mapNo].getRow() + ")", 4, 16);
			dbg.drawString("(" + syujinkou.getX() + "," + syujinkou.getY()
					+ ") ", 4, 32);
			dbg.drawString("(" + syujinkou.getPx() + "," + syujinkou.getPy()
					+ ")", 4, 48);
			dbg.drawString(maps[mapNo].getBgmName(), 4, 64);
		}
	}

	/*
	 * バッファを画面に描画
	 */
	private void paintScreen() {
		Graphics g = getGraphics();
		// バッファーイメージを画面に描画
		if ((g != null) && (dbImage != null)) {
			g.drawImage(dbImage, 0, 0, null);
		}
		Toolkit.getDefaultToolkit().sync();
		if (g != null) {
			g.dispose();
		}
	}

	/*
	 * キー入力をチェック
	 */
	private void checkInput() {
		boolean ready = false;
		if (ready/*readyWindow.isVisible()*/) {// 開始準備ウインドウ
			//readyWindowCheckInput();
		} else if (messageWindow.isVisible()) {// メッセージウインドウ表示中
			messageWindowCheckInput();
		} else {
			mainWindowCheckInput();// メイン画面(メッセージウインドウ出てないとき)
		}
	}

	/*
	 * タイトル画面のキー入力を処理する
	 */
	private void titleProcessInput() {
		if (false) {// 準備ウインドウ表示中
			//準備ウインドウのチェックインプットを呼び出す
		} else {// 準備ウインドウ表示中以外
			if (menuUpKey.isPressed()) {
				//title.t_upCursor();
			}
			if (menuDownKey.isPressed()) {
				//title.t_downCursor();
			}
			if (spaceKey.isPressed()) {
				switch (title.getSelectedTitleMenuNo()) {
				case Title.START: // はじめから
					System.out.println("Game Start");
					waveEngine.play("phoon");// 決定音
					// 初手スタートマップ
					mapNo = 0;
					MakeMap();
					// マップにキャラクターを登録
					// 主人公生成
					speed = 3 + util.boost(Main.clear_times);
					syujinkou = new Chara(3, 2, 0, DOWN, 0,maps[mapNo],speed);
					// 始めのイベント
					messageWindow.setMessage("憂鬱な朝。悲鳴で目が覚めた。");
					messageWindow.show();
					// マップにキャラクターを登録
					// キャラクターはマップに属す
					maps[mapNo].addChara(syujinkou);
					title.hide();// タイトルを閉じる
					waveEngine.stop("OutOfControl");//titleBGMストップ
					// マップに割り当てられたBGMを再生
					midiEngine.play(maps[mapNo].getBgmName());
					gameState = INTERVAL; // メイン画面へ
					interval.show();
					//プレイ時間計測スタート
					time = System.currentTimeMillis();
					//プレイ回数加算
					Main.play_times++;
					break;
				case Title.ROAD: // ロード
					break;
				case Title.OPTION:
					break;
				}
			}
		}
	}


	/*
	 * ゲームオーバー画面のキー入力を処理する
	 */
	private void gameOverInput() {
		if (!messageWindow.isVisible()) {
			if (spaceKey.isPressed()) {
				flag = true;// イベントを元に戻す
				// BGMを停止
				midiEngine.stop();
				if (gameState == GAMEOVER){
					gameOver.hide();
				} else {
					gameClear.hide();
				}
				System.out.println("TITLE");
				gameState = TITLE; // タイトル画面へ
				title.show();
			}
		} else {
			messageWindowCheckInput();
		}
	}

	/*
	 * メインウインドウでのキー入力をチェックする
	 */
	private void mainWindowCheckInput() {
		if (shiftKey.isPressed()) { // シフトキーが押されたら
			//一時停止の処理を入れる予定
		}
		if (leftKey.isPressed()) {
			if (!syujinkou.isMoving()) { // 移動中でなければ
				syujinkou.setDirection(LEFT); // 方向をセットして（以下それぞれの方向）
				syujinkou.setMoving(true); // 移動開始
			}
		}
		if (rightKey.isPressed()) { // 右
			if (!syujinkou.isMoving()) {
				syujinkou.setDirection(RIGHT);
				syujinkou.setMoving(true);
			}
		}
		if (upKey.isPressed()) { // 上
			if (!syujinkou.isMoving()) {
				syujinkou.setDirection(UP);
				syujinkou.setMoving(true);
			}
		}
		if (downKey.isPressed()) { // 下
			if (!syujinkou.isMoving()) {
				syujinkou.setDirection(DOWN);
				syujinkou.setMoving(true);
			}
		}
		if (spaceKey.isPressed()) { // スペース
			// 移動中は表示できない
			if (syujinkou.isMoving())
				return;
			// 話す
			if (!messageWindow.isVisible()) { // メッセージウィンドウを表示
				Chara chara = syujinkou.talkWith();
				if (chara != null) {// キャラがいれば（nullでなければ）
					// メッセージをセット
					messageWindow.setMessage(chara.getMessage());
					// メッセージウインドウを表示
					messageWindow.show();
				}
			}
		}
	}

	/**
	 * メッセージウィンドウが出ているときのキー入力をチェックする
	 */
	private void messageWindowCheckInput() {
		if (spaceKey.isPressed()/*||downKey.isPressed()||upKey.isPressed()||leftKey.isPressed()||rightKey.isPressed()*/) {
			waveEngine.play("pi");
			if (messageWindow.nextMessage()) {// 次のメッセージへ
				messageWindow.hide(); // 終了したら隠す
			}
		}
	}

	/**
	 * 主人公移動処理
	 */
	static boolean flag = true;
	static boolean barrierFlg = false;//バリアが有効かどうか
	static boolean preEnd = false;//バリア解除されても良いかどうか
	private void syujinkouMove() {
		//ゲームオーバーかどうかチェック
		if(maps[mapNo].isEnd(syujinkou.x, syujinkou.y) && !barrierFlg){
			time = (System.currentTimeMillis()-time)/1000;
			System.out.println("Game Over! time >> " + time + "s");
			midiEngine.stop();
			waveEngine.play("kick2");
			waveEngine.play("scream-man");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			gameState = GAMEOVER;
			gameOver.show();
			Main.save();
		} else if (maps[mapNo].isEnd(syujinkou.x, syujinkou.y) && barrierFlg){
			//バリアが有効な場合の処理
			waveEngine.play("kick1");
		} else if (!maps[mapNo].isEnd(syujinkou.x, syujinkou.y) && preEnd && barrierFlg){
			System.out.println("Barrier is broken.");
			waveEngine.play("break");
			messageWindow.setMessage("バリアが壊れてしまった！");
			messageWindow.show();
			barrierFlg = false;
		}
		preEnd = maps[mapNo].isEnd(syujinkou.x, syujinkou.y);
		// 移動（スクロール）中なら移動する
		if (syujinkou.isMoving()) {
			if (syujinkou.move()) { // 移動（スクロール）
				// 移動が完了した後の処理はここに書く
				// 移動イベント
				// イベントがあるかチェック
				data.Event event = maps[mapNo].eventCheck(syujinkou.getX(),syujinkou.getY());
				if (event instanceof MoveEvent) {// 移動イベントなら
					if(mapNo == 2 && syujinkou.x == 60 && syujinkou.y == 39){
						//ゲームクリア処理
						System.out.println("Game Clear!");
						gameState = GAMECLEAR;
						midiEngine.stop();
						waveEngine.play("trumpet");
						gameClear.show();
						//クリア回数加算
						Main.clear_times++;
						//クリアタイム計測
						time = (System.currentTimeMillis()-time)/1000;
						Main.time = (int)time;
						System.out.println("Clear Time >> " + Main.time + "s");
						if(Main.best_time>time || Main.best_time == 0){
							Main.best_time = (int)time;
						}
						Main.save();
						return;
					}
					MoveEvent m = (MoveEvent) event;
					// 移動サウンド
					waveEngine.play("step");
					// 移動元マップから主人公消去
					maps[mapNo].removeChara(syujinkou);
					// 現在のマップ番号を移動先のマップ番号に変える
					mapNo = m.destMapNo;
					// 移動先マップでの座標を取得して主人公を作り直す
					syujinkou = new Chara(m.destX, m.destY, 0, DOWN, 0,maps[mapNo],speed);
					// 移動先マップに主人公を登録
					maps[mapNo].addChara(syujinkou);
					// 移動先マップのBGMを鳴らす
					midiEngine.play(maps[mapNo].getBgmName());
				} else if (event instanceof TreasureEvent){
					TreasureEvent treasure = syujinkou.search();
					// 宝箱があれば
					if (treasure != null) {
						// 宝箱サウンド
						waveEngine.play("item");
						if(treasure.getItemNo()==0){
							//スピードアップ
							speed = syujinkou.upSpeed();
							System.out.println("Speed UP! Speed is "+ speed);
							// メッセージをセットする
							messageWindow.setMessage("スピードアップ！");
						}else if(treasure.getItemNo() == 1){
							System.out.println("Set barrier!");
							//バリアをセット
							barrierFlg = true;
							// メッセージセット
							messageWindow.setMessage("バリア発動！");
						}
						// メッセージウインドウを表示
						messageWindow.show();
						// 宝箱は削除
						maps[mapNo].removeEvent(treasure);
					}
				} else if (mapNo == 2 && syujinkou.x >= 3 && syujinkou.x<=4 && syujinkou.y == 34 && flag){
					flag = false;
					System.out.println("Dragon event happend.");
					waveEngine.play("dragon");
					messageWindow.setMessage("ドラゴンの鳴き声が聞こえる...。");
					messageWindow.show();
					midiEngine.play("ErogenousRock");
				}
			}
		}
	}

	/*
	 * 主人公以外のキャラクターの移動処理
	 */

	private void charaMove() {
		// マップにいるキャラクターを取得
		ArrayList<?> charas = maps[mapNo].getCharas();
		for (int i = 0; i < charas.size(); i++) {
			Chara chara = (Chara) charas.get(i);
			// キャラクターの移動タイプを調べる
			if (chara.getMoveType() == 1) {// 移動タイプなら
				if (chara.isMoving()) { // 移動中なら
					chara.move(); // 移動する
				} else if (rand.nextDouble() < Chara.PROB_MOVE) {
					// 移動してない場合はChara.PROB_MOVEの確率で再移動する
					// 方向はランダム
					chara.setDirection(rand.nextInt(4));
					chara.setMoving(true);
				}
			}
		}
	}

	/*
	 * キーが押されたらキーの状態を「押された」に変える
	 *
	 * @param e キーイベント
	 */
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_J) {
			leftKey.press();
		}
		if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_L) {
			rightKey.press();
		}
		if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_I) {
			if (title.isVisible()) {
				menuUpKey.press();
				waveEngine.play("pi");
			} else {
				upKey.press();
			}
		}
		if (keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_K) {
			if (title.isVisible()) {
				menuDownKey.press();
				waveEngine.play("pi");
			} else {
				downKey.press();
			}
		}
		if (keyCode == KeyEvent.VK_SPACE) {
			spaceKey.press();
		}
		if (keyCode == KeyEvent.VK_SHIFT) {
			shiftKey.press();
		}
	}

	/*
	 * キーが離されたらキーの状態を「離された」に変える
	 *
	 * @param e キーイベント
	 */
	public void keyReleased(KeyEvent e) {
		int keyCode = e.getKeyCode();
		if (keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_J) {
			leftKey.release();
			menuLeftKey.release();
		}
		if (keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_L) {
			rightKey.release();
			menuRightKey.release();
		}
		if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_I) {
			upKey.release();
			menuUpKey.release();
		}
		if (keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_K) {
			downKey.release();
			menuDownKey.release();
		}
		if (keyCode == KeyEvent.VK_SPACE) {
			spaceKey.release();
		}
		if (keyCode == KeyEvent.VK_SHIFT) {
			shiftKey.release();
		}
	}

	public void keyTyped(KeyEvent e) {
	}

	/**
	 * サウンドをロードする
	 */
	private void loadSound() {
		// BGMをロード
		for (int i = 0; i < bgmNames.length; i++) {
			midiEngine.load(bgmNames[i], bgmFiles[i]);
		}

		// サウンドをロード
		for (int i = 0; i < soundNames.length; i++) {
			waveEngine.load(soundNames[i], soundFiles[i]);
		}
	}

	/**
	 * モンスターの動きを制御
	 */
	static int counter = 1;//週回カウント用
	private void moveMonster() {
		// マップにいるモンスターを取得
		ArrayList<?> monsters = maps[mapNo].getMonsters();
		int sx = syujinkou.x;//主人公のx座標
		int sy = syujinkou.y;//主人公のy座標
		int mx;//モンスターのx座標
		int my;//モンスターのy座標
		boolean bool;//乱数用
		for (int i = 0; i < monsters.size(); i++) {
			Monster monster = (Monster) monsters.get(i);
			bool = rand.nextBoolean();
			if (monster.getMoveType() == 1) {
				// 移動タイプなら
				if (monster.isMoving()) { // 移動中なら
					monster.move(); // 移動する
				} else if (rand.nextDouble() < Chara.PROB_MOVE) {
					// 移動してない場合はChara.PROB_MOVEの確率で再移動する
					// 方向はランダム
					monster.setDirection(rand.nextInt(4));
					monster.setMoving(true);
				}
			} else if (monster.getMoveType() == 2) {//追尾タイプ
				mx = monster.x;
				my = monster.y;
				if(Math.sqrt(Math.pow(Math.abs(mx-sx),2)+Math.pow(Math.abs(my-sy), 2)) > 10 ){
					//15ピクセル以内じゃなければ追尾中止でランダム移動
					if (monster.isMoving()) { // 移動中なら
						monster.move(); // 移動する
					} else if (rand.nextDouble() < Chara.PROB_MOVE) {
						// 移動してない場合はChara.PROB_MOVEの確率で再移動する
						// 方向はランダム
						monster.setDirection(rand.nextInt(4));
						monster.setMoving(true);
					}
					continue;
				}
				if (monster.isMoving()) { // 移動中なら
					monster.move(); // 移動する
				} else if (mx == sx) {
					if (my > sy) {// 上にいるとき
						monster.setDirection(UP);
						monster.setMoving(true);
					} else {// 下にいるとき
						monster.setDirection(DOWN);
						monster.setMoving(true);
					}
				} else if (my == sy) {
					if (mx > sx) {// 右にいるとき
						monster.setDirection(LEFT);
						monster.setMoving(true);
					} else {// 左にいるとき
						monster.setDirection(RIGHT);
						monster.setMoving(true);
					}
				} else if (mx < sx && my < sy) {// 左下にいるとき
					monster.setDirection((bool) ? DOWN : RIGHT);
					monster.setMoving(true);
				} else if (mx < sx && my > sy) {// 左上にいるとき
					monster.setDirection((bool) ? UP : RIGHT);
					monster.setMoving(true);
				} else if (mx > sx && my < sy) {// 右下にいるとき
					monster.setDirection((bool) ? DOWN : LEFT);
					monster.setMoving(true);
				} else if (mx > sx && my > sy) {// 右上にいるとき
					monster.setDirection((bool) ? UP : LEFT);
					monster.setMoving(true);
				} else {
					monster.setMoving(false);
				}
			} else if (monster.getMoveType() == 3){//縦移動タイプ
				if (monster.isMoving()) { // 移動中なら
					monster.move(); // 移動する
				} else {
					// 方向はランダム
					monster.setDirection((bool) ? UP : DOWN);
					monster.setMoving(true);
				}
			} else if (monster.getMoveType() == 4){//横移動タイプ
				if (monster.isMoving()) { // 移動中なら
					monster.move(); // 移動する
				} else {
					// 方向はランダム
					monster.setDirection((bool) ? RIGHT : LEFT);
					monster.setMoving(true);
				}
			} else if (monster.getMoveType() == 5){//ぐるぐる回っちゃうタイプ
				if (monster.isMoving()) { // 移動中なら
					monster.move(); // 移動する
				}else {
					int dir;
					if(counter<=4){
						dir = LEFT;
					} else if (counter<=8){
						dir = DOWN;
					} else if (counter<=12){
						dir = RIGHT;
					} else if (counter<=15){
						dir = UP;
					} else {
						counter=0;
						dir = UP;
					}
					counter++;
					monster.setDirection(dir);
					monster.setMoving(true);
				}

			}
		}
	}

	private void MakeMap() {
		// マップ生成(マップで鳴らすBGM番号も渡す)
		maps = new Map[3];
		// 城
		maps[0] = new Map(0, "bin/main/map/castle.csv", "event/catsle.dat","OutOfControl", this);
		// フィールド
		maps[1] = new Map(1, "bin/main/map/field.csv", "event/field.dat","Start", this);
		// フィールド
		maps[2] = new Map(2, "bin/main/map/forest.csv", "event/forest.dat","スパイラル", this);
	}
}
