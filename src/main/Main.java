
package main;

import java.awt.Container;

import javax.swing.JApplet;

import netscape.javascript.JSObject;

public class Main extends JApplet{

	//セーブデータの格納
	public static int saveFlag;
	public static String id;
	public static int best_time;
	public static int play_times;
	public static int clear_times;
	public static int time = 0;

	// javascript連携
	private static JSObject win;

	/**
	 * constructer
	 */
	public void init() {
		//javascript連携オブジェクト生成
		win = (JSObject) JSObject.getWindow(this);
		//htmlのparamから値の取得
		this.load();
		// パネルを作成
		MainPanel panel = new MainPanel();
		Container contentPane = getContentPane();
		contentPane.add(panel);
		System.out.println("Applet is start.");
	}

	/**
	 * セーブデータのロード
	 */
	public void load(){
		saveFlag = Integer.parseInt(getParameter("saveFlag"));
		id = getParameter("id");
		best_time = Integer.parseInt(getParameter("best_time"));
		play_times = Integer.parseInt(getParameter("play_times"));
		clear_times = Integer.parseInt(getParameter("clear_times"));
	}

	/**
	 * セーブ
	 */

	public static void save(){
		if (saveFlag == 1) {
			String bt = Integer.toString(best_time);
			String pt = Integer.toString(play_times);
			String ct = Integer.toString(clear_times);
			String tt = Integer.toString(time);
			String[] data = new String[25];
			data[0] = id + "," + bt + "," + pt + "," + ct + "," + tt + ",";
			System.out.println(data[0]);
			win.call("save", data);
		} else {

		}
	}
}