package main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;

import data.*;


/*
 *マップ管理
 */
public class Map implements Common {

	// マップ
	private int[][] map;

	// あたり判定（イベント）マップ
	private int[][] hitmap;

	// マップの行数・列数（単位：マス）
	private int row;
	private int col;

	// マップ全体の大きさ（単位：ピクセル）
	private int width;
	private int height;

	private static BufferedImage chipImage;

	// このマップにいるキャラクターたちのリスト
	private ArrayList<Chara> charas = new ArrayList<Chara>();
	// このマップにいるモンスターのリスト
	private ArrayList<Monster> monsters = new ArrayList<Monster>();

	// このマップにあるイベントのリスト（配列）
	private ArrayList<data.Event> events = new ArrayList<data.Event>();

	// メインパネルへの参照
	public MainPanel panel;

	// マップファイル名
	private String mapFile;

	//マップデータオブジェクト
	private MapData csv = new MapData();
	//イベントデータオブジェクト
	private EventData ev = new EventData();

	// BGM名
	private String bgmName;

	//マップ番号
	private int mapNo;

	/**
	 * コンストラクタ
	 *
	 * @param mapFile マップファイル名
	 * @param eventFile イベントファイル名
	 * @param bgmNo BGM番号
	 * @param panel パネルへの参照
	 */
	public Map(int MapNo, String mapFile, String eventFile,
			String bgmName, MainPanel panel) {
		// 現在のマップファイル名、BGM名を読み取る
		this.mapNo = MapNo;
		this.mapFile = mapFile;
		this.bgmName = bgmName;

		// 初回の呼び出しのみイメージをロード
		if (chipImage == null) {
			loadImage();
		}
		// マップをロード
		load(csv.getCsv(this.mapNo));
		//load(mapFile);
		// イベントをロード
		loadEvent(ev.getCsv(this.mapNo));
		//loadEvent(eventFile);
		// フラグ（アイテム・イベント）リストを格納

	}

	public void draw(Graphics g, int offsetX, int offsetY) {
		if (this.mapNo == 0) {
			row = 32;
			col = 32;
		} else {
			row = 64;
			col = 64;
		}
		// オフセットを元に描画範囲を求める
		int firstTileX = pixelsToTiles(-offsetX);
		int lastTileX = firstTileX + pixelsToTiles(MainPanel.WIDTH) + 1;
		// 描画範囲がマップの大きさより大きくならないように調整
		lastTileX = Math.min(lastTileX, col);

		int firstTileY = pixelsToTiles(-offsetY);
		int lastTileY = firstTileY + pixelsToTiles(MainPanel.HEIGHT) + 1;
		// 描画範囲がマップの大きさより大きくならないように調整
		lastTileY = Math.min(lastTileY, row);

		for (int i = firstTileY; i < lastTileY; i++) {
			for (int j = firstTileX; j < lastTileX; j++) {
				// mapの値に応じて画像を描く
				int mapChipNo = map[i][j];
				// イメージ上の位置を求める
				// マップチップイメージは8x8を想定
				int cx = (mapChipNo % (chipImage.getWidth() / 32)) * CS;
				int cy = (mapChipNo / (chipImage.getHeight() / 32)) * CS;
				g.drawImage(chipImage, tilesToPixels(j) + offsetX,
						tilesToPixels(i) + offsetY, tilesToPixels(j) + offsetX
								+ CS, tilesToPixels(i) + offsetY + CS, cx, cy,
						cx + CS, cy + CS, panel);
				// (j, i) にあるイベントを描画
				for (int n = 0; n < events.size(); n++) {
					data.Event event = (data.Event) events.get(n);
					// イベントが(j, i)にあれば描画
					if (event.x == j && event.y == i) {
						mapChipNo = event.chipNo;
						// イメージ上の位置を求める
						// マップチップイメージは8x8を想定
						cx = (mapChipNo % (chipImage.getWidth() / 32)) * CS;
						cy = (mapChipNo / (chipImage.getHeight() / 32))
								* CS;
						g.drawImage(chipImage, tilesToPixels(j) + offsetX,
								tilesToPixels(i) + offsetY, tilesToPixels(j)
										+ offsetX + CS, tilesToPixels(i)
										+ offsetY + CS, cx, cy, cx + CS, cy
										+ CS, panel);
					}
				}
			}
		}
		// このマップにいるキャラクターを描画
		for (int n = 0; n < charas.size(); n++) {
			Chara chara = (Chara) charas.get(n);
			chara.draw(g, offsetX, offsetY);
		}
		// このマップにいるモンスターを描画
		for (int n = 0; n < monsters.size(); n++) {
			Monster oni = (Monster) monsters.get(n);
			oni.draw(g, offsetX, offsetY);
		}
	}

	/**
	 * (x,y)にぶつかるものがあるか調べる。
	 *
	 * @param x マップのx座標
	 *
	 * @param y マップのy座標
	 *
	 * @return (x,y)にぶつかるものがあったらtrueを返す。
	 */
	public boolean isHit(int x, int y) {
		// (x,y)に当たり判定があるか確認(129番チップ)
		if (hitmap[y][x] == 129){
			// pong.play();//壁にぶつかったらpong再生
			return true;
		}

		// 他のキャラクターがいたらぶつかる
		for (int i = 0; i < charas.size(); i++) {
			Chara chara = (Chara) charas.get(i);
			if (chara.getX() == x && chara.getY() == y) {
				return true;
			}
		}
		// ぶつかるイベントがあるか
		for (int i = 0; i < events.size(); i++) {
			data.Event event = (data.Event) events.get(i);
			if (event.x == x && event.y == y) {
				return event.isHit;
			}
		}
		// なければぶつからない
		return false;
	}
	/**
	 * (x,y)にぶつかるものがあるか調べる。
	 *
	 * @param x マップのx座標
	 *
	 * @param y マップのy座標
	 *
	 * @return (x,y)にぶつかるものがあったらtrueを返す。
	 */
	public boolean isHitMonster(int x, int y) {
		// (x,y)に当たり判定があるか確認(129番チップ)
		if (hitmap[y][x] == 129) {
			// pong.play();//壁にぶつかったらpong再生
			return true;
		}
		// ぶつかるイベントがあるか
		for (int i = 0; i < events.size(); i++) {
			data.Event event = (data.Event) events.get(i);
			if (event.x == x && event.y == y) {
				return event.isHit;
			}
		}
		// なければぶつからない
		return false;
	}
	/**
	 * (x,y)に壁があるか調べる。
	 *
	 * @param x マップのx座標
	 *
	 * @param y マップのy座標
	 *
	 * @return (x,y)にぶつかるものがあったらtrueを返す。
	 */
	public boolean isHitWall(int x, int y) {
		// (x,y)に当たり判定があるか確認(129番チップ)
		if (hitmap[y][x] == 129) {
			return true;
		}
		// イベントがあるか
		for (int i = 0; i < events.size(); i++) {
			data.Event event = (data.Event) events.get(i);
			if (event.x == x && event.y == y) {
				return true;
			}
		}
		// なければぶつからない
		return false;
	}

	/**
	 * 主人公の座標(x,y)にモンスターがいるか調べる(ゲームオーバーかどうか)
	 *
	 * @param 主人公のx座標
	 * @param 主人公のy座標
	 * @return boolean 主人公の現在の座標(x,y)にモンスターがいたらtrue
	 */
	public boolean isEnd(int x, int y) {
		// モンスターがいたらtrue
		for (int i = 0; i < monsters.size(); i++) {
			Monster monster = (Monster) monsters.get(i);
			if (monster.getX() == x && monster.getY() == y) {
				return true;
			}
		}
		return false;
	}

	/**
	 * キャラクターをこのマップに追加する
	 *
	 * @param chara キャラクター
	 */
	public void addChara(Chara chara) {
		charas.add(chara);
	}

	public void removeChara(Chara chara) {
		charas.remove(chara);
	}

	public void addMonster(Monster monster) {
		monsters.add(monster);
	}

	public void removeMosnter(Monster monster) {
		monsters.remove(monster);
	}

	/*
	 * (x,y)にキャラクターがいるか調べる
	 *
	 * @param x X座標
	 *
	 * @param y Y座標
	 *
	 * @return (x,y)にいるキャラクター、いなかったらnull
	 */
	public Chara charaCheck(int x, int y) {
		for (int i = 0; i < charas.size(); i++) {
			Chara chara = (Chara) charas.get(i);
			if (chara.getX() == x && chara.getY() == y) {
				return chara;
			}
		}
		return null;
	}

	/*
	 * (x,y)にモンスターがいるか調べる
	 *
	 * @param x X座標
	 *
	 * @param y Y座標
	 *
	 * @return (x,y)にいるキャラクター、いなかったらnull
	 */
	public Monster monsterCheck(int x, int y) {
		for (int i = 0; i < charas.size(); i++) {
			Monster monster = (Monster) monsters.get(i);
			if (monster.getX() == x && monster.getY() == y) {
				return monster;
			}
		}
		return null;
	}

	/*
	 * (x,y)にイベントがあるか調べる
	 *
	 * @param x X座標
	 *
	 * @param y Y座標
	 *
	 * @return (x,y)にいるイベント、いなかったらnull
	 */
	public data.Event eventCheck(int x, int y) {
		for (int i = 0; i < events.size(); i++) {
			data.Event event = (data.Event) events.get(i);
			if (event.x == x && event.y == y) {
				return event;
			}
		}
		return null;
	}

	/*
	 * 登録されているイベントを削除する
	 *
	 * @param event 削除したいイベント
	 */
	public void removeEvent(data.Event event) {
		events.remove(event);
	}

	/*
	 * ピクセル単位をマス単位に変更する
	 *
	 * @param pixels ピクセル単位
	 *
	 * @return マス単位
	 */
	public static int pixelsToTiles(double pixels) {
		return (int) Math.floor(pixels / CS);
	}

	/*
	 * マス単位をピクセル単位に変更する
	 *
	 * @param tiles マス単位
	 *
	 * @return ピクセル単位
	 */
	public static int tilesToPixels(int tiles) {
		return tiles * CS;
	}

	public int getRow() {
		return row;
	}

	public int getCol() {
		return col;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public ArrayList getCharas() {
		return charas;
	}

	public ArrayList getMonsters() {
		return monsters;
	}


	/**
	 * MapDataで生成したマップを読み込む
	 * @param array[] マップデータ
	 */
	private void load (String[] csv){
		int cnt = csv.length;
		int counterX = 0;
		int counterY = 0;
		int col = (cnt - 1) / 2;
		int row = (cnt - 1) / 2;
		StringTokenizer token;
		// マップサイズを設定
		width = col * CS;
		height = row * CS;
		map = new int[row][col];
		// 三行目以降の実際のマップデータを代入
		for (counterY = 0; counterY < row; counterY++) {
			// 行ループするたびに行を読み込み、カンマで区切る
			token = new StringTokenizer(csv[counterY], ",");
			for (counterX = 0; counterX < col; counterX++) {
				map[counterY][counterX] = Integer.parseInt(token.nextToken());
			}
		}
		hitmap = new int[row][col];
		// 当たり判定マップデータを代入
		for (counterY = 0; counterY < row; counterY++) {
			// 行ループするたびに行を読み込み、カンマで区切る
			token = new StringTokenizer(csv[counterY + row + 1], ",");
			for (counterX = 0; counterX < col; counterX++) {
				hitmap[counterY][counterX] = Integer.parseInt(token.nextToken());
			}
		}
	}

	/**
	 * ファイルからマップを読み込む
	 *
	 * @param filename 読み込むマップデータのファイル名
	 */
	private void load(String filename) {
		int counterX = 0;
		int counterY = 0;
		String garbage;
		try {
			// ファイルを読み込む
			FileReader fr = new FileReader(filename);
			BufferedReader br = new BufferedReader(fr);
			//BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(filename)));
			String line;
			StringTokenizer token;
			// 一行目を読み込む
			line = br.readLine();
			// 一行目をコンマで区切る
			token = new StringTokenizer(line, ",");
			// マップ幅、高さを代入
			col = Integer.parseInt(token.nextToken());
			row = Integer.parseInt(token.nextToken());
			counterX = 1;// 一行二列目まで代入完了したため
			// 一行目の不要部分の処理
			garbage = token.nextToken();
			garbage = token.nextToken();
			// 総レイヤー数を代入
			// SumLayer=Integer.parseInt(token.nextToken());
			// 一行目の不要部分の処理
			garbage = token.nextToken();
			garbage = token.nextToken();
			// マップサイズを設定
			width = col * CS;
			height = row * CS;

			map = new int[row][col];
			// 三行目以降の実際のマップデータを代入
			for (counterY = 0; counterY < row; counterY++) {
				// 行ループするたびに行を読み込み、カンマで区切る
				line = br.readLine();
				token = new StringTokenizer(line, ",");
				for (counterX = 0; counterX < col; counterX++) {
					map[counterY][counterX] = Integer.parseInt(token
							.nextToken());
				}
			}
			line = br.readLine();
			hitmap = new int[row][col];
			// 当たり判定マップデータを代入
			for (counterY = 0; counterY < row; counterY++) {
				// 行ループするたびに行を読み込み、カンマで区切る
				line = br.readLine();
				token = new StringTokenizer(line, ",");
				for (counterX = 0; counterX < col; counterX++) {
					hitmap[counterY][counterX] = Integer.parseInt(token.nextToken());
				}
			}
			// show();
			// 終了処理
			br.close();
		} catch (IOException ex) {
			// 例外発生時の処理
			ex.printStackTrace();
		}
	}

	/**
	 * EventDataで生成したCSVをロードする
	 *
	 * @param file
	 *            イベントファイル
	 */
	private void loadEvent(String csv[]) {
		int cnt = csv.length;
		for (int i = 0; i < cnt; i++) {
			// StringTokenizerを使って、「,」ごとにイベントの属性を読み込む

			StringTokenizer st = new StringTokenizer(csv[i], ",");
			// イベント情報を取得する
			// イベントタイプを取得してイベントごとに処理する
			String eventType = st.nextToken();
			if (eventType.equals("CHARA")) {// もしイベントがCHARAなら
				makeCharacter(st);
			} else if (eventType.equals("MONSTER")) {
				makeMonster(st);
			} else if (eventType.equals("MOVE")) {// 移動イベント
				makeMove(st);
			} else if (eventType.equals("TREASURE")) {// 宝箱イベント
				makeTreasure(st);
			} else if (eventType.equals("NULL")){
				continue;
			}
		}

	}

	/**
	 * イベントをロードする
	 *
	 * @param file イベントファイル
	 */
	private void loadEvent(String filename) {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(filename), "Shift_JIS"));
			//BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(filename)));
			String line;
			while ((line = br.readLine()) != null) {
				// 空行を読み飛ばす
				if (line.equals(""))
					continue;
				// //のコメント行は読み飛ばす
				if (line.startsWith("//"))
					continue;
				// もしそれ以外ならStringTokenizerを使って、「,」ごとにイベントの属性を読み込む
				StringTokenizer st = new StringTokenizer(line, ",");
				// イベント情報を取得する
				// イベントタイプを取得してイベントごとに処理する
				String eventType = st.nextToken();
				if (eventType.equals("CHARA")) {// もしイベントがCHARAなら
					makeCharacter(st);
				} else if (eventType.equals("MONSTER")){
					makeMonster(st);
				} else if (eventType.equals("MOVE")) {// 移動イベント
					makeMove(st);
				} else if (eventType.equals("TREASURE")) {// 宝箱イベント
					makeTreasure(st);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * マップチップのイメージをロード
	 */
	private void loadImage() {
		// マップチップのイメージをロード
		try {
			chipImage = ImageIO.read(getClass().getResource("image/mapchip.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * キャラクターイベントを作成
	 */

	private void makeCharacter(StringTokenizer st) {
		// イベントの座標
		int x = Integer.parseInt(st.nextToken());
		int y = Integer.parseInt(st.nextToken());
		// キャラクタ番号
		int charaNo = Integer.parseInt(st.nextToken());
		// 向き
		int dir = Integer.parseInt(st.nextToken());
		// 移動タイプ
		int moveType = Integer.parseInt(st.nextToken());
		// メッセージ
		String message = st.nextToken();

		// キャラクターを作成
		Chara c = new Chara(x, y, charaNo, dir, moveType, this);
		// メッセージを登録
		c.setMessage(message);

		// キャラクターベクトルに登録
		charas.add(c);
	}

	/*
	 * モンスターイベントを作成
	 */

	private void makeMonster(StringTokenizer st) {
		// イベントの座標
		int x = Integer.parseInt(st.nextToken());
		int y = Integer.parseInt(st.nextToken());
		// モンスター番号
		int monsterNo = Integer.parseInt(st.nextToken());
		// 向き
		int dir = Integer.parseInt(st.nextToken());
		// 移動タイプ
		int moveType = Integer.parseInt(st.nextToken());
		// スピード
		int speed = Integer.parseInt(st.nextToken());
		//モンスター番号からモンスターイメージを選択
		int monsterImageNo = 0;
		if(monsterNo>8){
			monsterImageNo = 1;
			monsterNo %=8;
		}
		// キャラクターを作成
		Monster m = new Monster(x, y, monsterImageNo, monsterNo, dir, moveType, this, speed);

		// キャラクターベクトルに登録
		monsters.add(m);
	}

	/*
	 * 宝箱イベントを作成
	 */
	private void makeTreasure(StringTokenizer st) {
		// 宝箱の座標
		int x = Integer.parseInt(st.nextToken());
		int y = Integer.parseInt(st.nextToken());

		// アイテム番号
		int itemNo = Integer.parseInt(st.nextToken());

		// マップチップ番号
		int mapChipNo = Integer.parseInt(st.nextToken());
		if(isHitWall(x,y)){
			makeTreasure(new StringTokenizer(util.createTreasure(mapNo,itemNo),","));
			return;
		}
		// 宝箱イベントを作成
		TreasureEvent t = new TreasureEvent(x, y, itemNo, mapChipNo);
		//Item i = new Item(itemNo, itemName, 0, 0);
		events.add(t);
	}

	/*
	 * 移動イベントを作成
	 */
	private void makeMove(StringTokenizer st) {
		// 移動イベントの座標
		int x = Integer.parseInt(st.nextToken());
		int y = Integer.parseInt(st.nextToken());
		// チップ番号
		int chipNo = Integer.parseInt(st.nextToken());
		// 移動先のマップ番号
		int destMapNo = Integer.parseInt(st.nextToken());
		// 移動先のX座標
		int destX = Integer.parseInt(st.nextToken());
		// 移動先のY座標
		int destY = Integer.parseInt(st.nextToken());
		// 移動イベントを作成
		MoveEvent m = new MoveEvent(x, y, chipNo, destMapNo, destX, destY);
		// 移動イベントを登録
		events.add(m);
	}

	/*
	 * マップをコンソールに表示。デバッグ用。
	 */
	public void show() {
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < col; j++) {
				System.out.print(map[i][j]);
			}
			System.out.println();
		}
	}

	/*
	 * このマップのBGM名を返す
	 *
	 * @return BGM名
	 */
	public String getBgmName() {
		return bgmName;
	}

	/*
	 * このマップのマップ名を返す
	 *
	 * @return マップ名
	 */
	public String getMapName() {
		return mapFile;
	}
}
