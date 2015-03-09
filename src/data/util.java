package data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import main.*;
/**
 * 便利機能
 *
 */
public class util {
	private static Random rand = new Random();
	/**
	 * 乱数を返す
	 * @param int min
	 * @param int max
	 */
	public static int rnd (int min, int max){
		int rnd = max - min + 1;
		rnd = rand.nextInt(rnd) + min;
		return rnd;
	}

	/**
	 * クリア回数に応じたスピードランクアップ
	 * @param clear_times
	 */
	public static int boost (int clear_times){
		int up = 0;
		if(clear_times>=10&& clear_times<20){
			up = 1;
		} else if (clear_times>=20 && clear_times<40){
			up = 2;
		} else if (clear_times>40){
			up = 3;
		}
		return up;
	}

	public static String createTreasure(int mapNo,int treasureNo){
		int x;
		int y;
		int num;
		if(mapNo == 0){
			x = rnd(1,30);
			y = rnd(1,30);
		} else {
			x = rnd(1,62);
			y = rnd(1,62);
		}
		if (treasureNo == 0) {
			num = 20;
		} else {
			num = 21;
		}
		return x + "," + y + "," + treasureNo + "," + num;
	}

	/**
	 * メインクラス
	 * @param args
	 */
	public static void main (String[] args){
		String path1 = "bin\\main\\map\\forest.csv";
		//mapRead(path1,"map2);
		String path2 = "bin\\main\\event\\forest.dat";
		eventRead(path2,"event2");
	}

	/**
	 * csv直打ち用文字列作成
	 * @param path ファイルのパス
	 * @param param 変数名
	 */
	private static void mapRead(String path ,String param) {
		try {
			File file = new File(path);
			FileReader filereader = new FileReader(file);
			int ch = filereader.read();
			int cnt = 0;
			while (ch != -1) {
				if(ch == 13){
					System.out.println("\";");
					System.out.print(param+"["+cnt+"] = \"");
					cnt++;
				} else if (ch == 10){
					//改行だから飛ばす
				} else {
					System.out.print((char) ch);
				}
				ch = filereader.read();
			}
		} catch (FileNotFoundException e) {
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	/**
	 * csv直打ち用文字列イベント作成
	 * @param path ファイルのパス
	 * @param param 変数名
	 */
	private static void eventRead(String path ,String param) {
		try {
			File file = new File(path);
			FileReader filereader = new FileReader(file);
			int ch = filereader.read();
			int cnt = 0;
			while (ch != -1) {
				if(ch == 13){
					System.out.println("\";");
					System.out.print(param+"["+cnt+"] = \"");
					cnt++;
				} else if (ch == 10){
					//改行だから飛ばす
				} else {
					System.out.print((char) ch);
				}
				ch = filereader.read();
			}
		} catch (FileNotFoundException e) {
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		}
	}
}
